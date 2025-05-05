package com.voghbum.service;

import com.voghbum.db.conf.MultitenantConfiguration;
import com.voghbum.db.master.entity.Tenant;
import com.voghbum.db.master.repository.TenantRepository;
import com.voghbum.dto.TenantCreateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TenantService {
    private final Logger logger = LoggerFactory.getLogger(TenantService.class);
    private final TenantDatabaseProvisionService tenantDatabaseProvisionService;
    private final MultitenantConfiguration multitenantConfiguration;

    public TenantService(TenantDatabaseProvisionService tenantDatabaseProvisionService, MultitenantConfiguration multitenantConfiguration) {
        this.tenantDatabaseProvisionService = tenantDatabaseProvisionService;
        this.multitenantConfiguration = multitenantConfiguration;
    }

    public void createNewTenantStack(TenantCreateRequest request) {
        logger.info("Creating new tenant stack for tenant: {}", request.getTenantId());

        tenantDatabaseProvisionService.provisionDatabaseForTenant(request.getDbName(),
                request.getDbUser(), request.getDbPassword())
                .thenCompose(dbPort -> tenantDatabaseProvisionService.runSingleNodeAppContainer(request.getTenantId())
                        .thenApply(singleNodePort -> new int[]{dbPort, singleNodePort}))
                .thenApply(ports -> {
                    int dbPort = ports[0];
                    int singleNodePort = ports[1];
                    Tenant tenant = new Tenant();
                    tenant.setTenantId(request.getTenantId());
                    tenant.setDriverClassName("org.postgresql.Driver");
                    tenant.setUrl("jdbc:postgresql://localhost:" + dbPort + "/" + request.getDbName());
                    tenant.setUsername(request.getDbUser());
                    tenant.setPassword(request.getDbPassword());
                    tenant.setSingleNodeEndpoint("http://localhost:" + singleNodePort + "/api/performance/rating");
                    return tenant;
                })
                .thenAccept(multitenantConfiguration::registerNewTenant)
                .exceptionally(ex -> {
                    logger.error("Tenant provisioning failed", ex);
                    return null;
                });
    }
}
