package com.voghbum.db.conf;

import com.voghbum.db.dbrouting.MultitenantDataSource;
import com.voghbum.db.master.entity.Tenant;
import com.voghbum.db.master.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.voghbum.db.repository",
        entityManagerFactoryRef = "tenantEntityManagerFactory",
        transactionManagerRef = "tenantTransactionManager"
)
public class MultitenantConfiguration {
    private final Logger logger = LoggerFactory.getLogger(MultitenantDataSource.class);
    private final TenantRepository tenantRepository;
    private final MultitenantDataSource multitenantDataSource;

    @Autowired
    public MultitenantConfiguration(TenantRepository tenantRepository, MultitenantDataSource multitenantDataSource) {
        this.tenantRepository = tenantRepository;
        this.multitenantDataSource = multitenantDataSource;
    }

    @Bean
    public DataSource dataSource() {
        // Initialize tenant data sources from master database
        initializeTenantDataSources();

        // Set default data source to empty H2
        DriverManagerDataSource h2DataSource = new DriverManagerDataSource();
        h2DataSource.setDriverClassName("org.h2.Driver");
        h2DataSource.setUrl("jdbc:h2:mem:empty;DB_CLOSE_DELAY=-1");
        h2DataSource.setUsername("sa");
        h2DataSource.setPassword("");

        multitenantDataSource.setDefaultDataSource(h2DataSource);
        multitenantDataSource.afterPropertiesSet();

        return multitenantDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.voghbum.db.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        em.setJpaProperties(properties);
        
        return em;
    }

    @Bean
    public PlatformTransactionManager tenantTransactionManager() {
        return new JpaTransactionManager(tenantEntityManagerFactory().getObject());
    }

    @Transactional
    public void initializeTenantDataSources() {
        List<Tenant> tenants = tenantRepository.findAll();
        for (Tenant tenant : tenants) {
            if(!checkConnection(tenant)) {
                continue;
            }
            migrateTenantDatabase(tenant);
            if (!multitenantDataSource.containsDataSource(tenant.getTenantId())) {
                multitenantDataSource.addDataSource(tenant.getTenantId(), prepareDataSource(tenant));
            }
        }
    }

    private boolean checkConnection(Tenant tenant) {
        DriverManagerDataSource dataSource = prepareDataSource(tenant);
        try (Connection connection = dataSource.getConnection()) {
            return !connection.isClosed();
        } catch (SQLException ignored) {
            return false;
        }
    }

    @Transactional
    public void registerNewTenant(Tenant tenant) {
        tenantRepository.save(tenant);
        multitenantDataSource.addDataSource(tenant.getTenantId(), prepareDataSource(tenant));
        migrateTenantDatabase(tenant);
    }

    @Transactional
    public void removeTenant(String tenantId) {
        tenantRepository.findByTenantId(tenantId).ifPresent(tenant -> {
            tenantRepository.delete(tenant);
            multitenantDataSource.removeDataSource(tenantId);
        });
    }

    private void migrateTenantDatabase(Tenant tenant) {
        logger.info("Migrating db with flyway: {}", tenant);
        Flyway flyway = Flyway.configure()
            .dataSource(tenant.getUrl(), tenant.getUsername(), tenant.getPassword())
            .locations("classpath:db/migration/tenantdb")
            .load();
        flyway.migrate();
    }

    private DriverManagerDataSource prepareDataSource(Tenant tenant) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(tenant.getDriverClassName());
        dataSource.setUrl(tenant.getUrl());
        dataSource.setUsername(tenant.getUsername());
        dataSource.setPassword(tenant.getPassword());
        return dataSource;
    }
}