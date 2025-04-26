package com.voghbum.db.conf;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource defaultDataSource() {
        HikariDataSource defaultDataSource = new HikariDataSource();
        defaultDataSource.setJdbcUrl("jdbc:postgresql://localhost:5435/tenant-1-db");
        defaultDataSource.setUsername("tenant-1-user");
        defaultDataSource.setPassword("tenant-1-password");
        defaultDataSource.setDriverClassName("org.postgresql.Driver");
        return defaultDataSource;
    }

    @Bean
    @Primary
    public DataSource dataSource(TenantDataSourceService tenantDataSourceService, DataSource defaultDataSource) {
        MultiTenantDataSource multiTenantDataSource = new MultiTenantDataSource();
        multiTenantDataSource.setTargetDataSources(new HashMap<>(tenantDataSourceService.getDataSourceMap()));
        multiTenantDataSource.setDefaultTargetDataSource(defaultDataSource); // To Ensure fallback
        return multiTenantDataSource;
    }
}
