package com.voghbum.db.conf;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class TenantDataSourceService {
    private final Map<String, DataSource> dataSourceMap = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(TenantDataSourceService.class);

    @PostConstruct
    public void loadTenantsFromApi() {
        //TODO: bu klasörü resource altına koyduk. Örnekte src ile aynı hierarşideydi. Klasörü bulamayabilir.
        File[] files = Paths.get("tenant_db_configuration").toFile().listFiles();
        //LOG.info("Found tenant db properties: {}", files);

        for (File propertyFile : files) {
            Properties tenantProperties = new Properties();
            try {
                tenantProperties.load(new FileInputStream(propertyFile));
                String tenantId = tenantProperties.getProperty("name");

                String className = tenantProperties.getProperty("datasource.driver-class-name");
                String username = tenantProperties.getProperty("datasource.username");
                String password = tenantProperties.getProperty("datasource.password");
                String url = tenantProperties.getProperty("datasource.url");
                dataSourceMap.put(tenantId, createDataSource(url, username, password, className));
            } catch (IOException exp) {
                LOG.error("Failed to build dataSource from tenant db conf file {}", propertyFile, exp);
                throw new RuntimeException("Problem in tenant datasource:" + exp);
            }
        }
    }

    private DataSource createDataSource(String jdbcUrl, String username, String password, String className) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(className);
        return dataSource;
    }

    public Map<String, DataSource> getDataSourceMap() {
        return dataSourceMap;
    }
}