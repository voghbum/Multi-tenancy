package com.voghbum.db.dbrouting;

import com.voghbum.app.TenantContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.HashMap;
import java.util.Map;

public class MultitenantDataSource extends AbstractRoutingDataSource {
    private final Map<Object, Object> targetDataSources = new HashMap<>();

    @Override
    protected String determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant() != null ? TenantContext.getCurrentTenant() : "default";
    }

    public void addDataSource(String tenantId, String driverClassName, String url, String username, String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        
        targetDataSources.put(tenantId, dataSource);
        setTargetDataSources(targetDataSources);
        afterPropertiesSet();
    }

    public void removeDataSource(String tenantId) {
        targetDataSources.remove(tenantId);
        setTargetDataSources(targetDataSources);
        afterPropertiesSet();
    }

    public boolean containsDataSource(String tenantId) {
        return targetDataSources.containsKey(tenantId);
    }
}