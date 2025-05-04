package com.voghbum.controller;

import com.voghbum.db.conf.MultitenantConfiguration;
import com.voghbum.db.master.entity.Tenant;
import com.voghbum.db.master.repository.TenantRepository;
import com.voghbum.service.TenantDatabaseProvisionService;
import com.voghbum.dto.TenantCreateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/tenant")
public class TenantController {
    Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private final TenantRepository tenantRepository;
    private final MultitenantConfiguration multitenantConfiguration;
    private final TenantDatabaseProvisionService tenantDatabaseProvisionService;

    public TenantController(TenantRepository tenantRepository, MultitenantConfiguration multitenantConfiguration,
                            TenantDatabaseProvisionService tenantDatabaseProvisionService) {
        this.tenantRepository = tenantRepository;
        this.multitenantConfiguration = multitenantConfiguration;
        this.tenantDatabaseProvisionService = tenantDatabaseProvisionService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Tenant>> getAllTenants() {
        return ResponseEntity.ok(tenantRepository.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTenant(@RequestBody TenantCreateRequest request) throws ExecutionException, InterruptedException {
        logger.info("new tenant coming: {}", request.getTenantId());
        var status = tenantDatabaseProvisionService.provisionDatabaseForTenant(
            request.getDbName(), request.getPort(), request.getDbUser(), request.getDbPassword()
        );

        Tenant tenant = new Tenant();
        tenant.setTenantId(request.getTenantId());
        tenant.setDriverClassName("org.postgresql.Driver");
        tenant.setUrl("jdbc:postgresql://localhost:" + request.getPort() + "/" + request.getDbName());
        tenant.setUsername(request.getDbUser());
        tenant.setPassword(request.getDbPassword());

        status.thenAccept((stat) -> {
            if(stat) {
                Tenant savedTenant = tenantRepository.save(tenant);
                multitenantConfiguration.registerNewTenant(savedTenant);
            }
        });

        return ResponseEntity.accepted().body("Tenant provisioning started. Check status later.");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeTenant(@RequestParam String tenantId) {
        multitenantConfiguration.removeTenant(tenantId);
        tenantRepository.findByTenantId(tenantId)
                .ifPresent(tenantRepository::delete);
                
        return ResponseEntity.ok().build();
    }
} 