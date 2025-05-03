package com.voghbum.db.dbrouting;

import com.voghbum.app.TenantContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;

@Component
public class MultitenantDataSource extends AbstractRoutingDataSource {
    private final Map<Object, Object> targetDataSources = new HashMap<>();

    public MultitenantDataSource() {
        setTargetDataSources(targetDataSources);
    }

    @Override
    protected String determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant();
    }

    public void addDataSource(String tenantId, DataSource dataSource) {
        targetDataSources.put(tenantId, dataSource);
        setTargetDataSources(targetDataSources);
    }

    public void removeDataSource(String tenantId) {
        targetDataSources.remove(tenantId);
        setTargetDataSources(targetDataSources);
    }

    public boolean containsDataSource(String tenantId) {
        return targetDataSources.containsKey(tenantId);
    }

    public void setDefaultDataSource(DataSource dataSource) {
        setDefaultTargetDataSource(dataSource);
    }

    public void finalizeDataSources() {
        afterPropertiesSet();
    }
}