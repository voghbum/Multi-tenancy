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
    private final TenantRepository tenantRepository;
    private final MultitenantConfiguration multitenantConfiguration;

    public TenantService(TenantDatabaseProvisionService tenantDatabaseProvisionService, TenantRepository tenantRepository, MultitenantConfiguration multitenantConfiguration) {
        this.tenantDatabaseProvisionService = tenantDatabaseProvisionService;
        this.tenantRepository = tenantRepository;
        this.multitenantConfiguration = multitenantConfiguration;
    }

    public void createNewTenantStack(TenantCreateRequest request) {
        logger.info("Creating new tenant stack for tenant: {}", request.getTenantId());
        tenantDatabaseProvisionService.provisionDatabaseForTenant(request.getDbName(),
                request.getPort(), request.getDbUser(), request.getDbPassword())
                .thenCompose(state -> {
                    if(!state) {
                        logger.error("Tenant creation failed in db creating stage!");
                        throw new RuntimeException("Tenant creation failed in db creating stage!");
                    }
                    return tenantDatabaseProvisionService.runSingleNodeAppContainer();
                }).thenApply((state) -> {
                    if(!state) {
                        logger.error("Tenant creation failed in singleNode creating stage!");
                        throw new RuntimeException("Tenant creation failed in db creating stage!");
                    }
                    Tenant tenant = new Tenant();
                    tenant.setTenantId(request.getTenantId());
                    tenant.setDriverClassName("org.postgresql.Driver");
                    tenant.setUrl("jdbc:postgresql://localhost:" + request.getPort() + "/" + request.getDbName());
                    tenant.setUsername(request.getDbUser());
                    tenant.setPassword(request.getDbPassword());
                    return tenant;
                })
                .thenAccept(multitenantConfiguration::registerNewTenant);
    }
}
