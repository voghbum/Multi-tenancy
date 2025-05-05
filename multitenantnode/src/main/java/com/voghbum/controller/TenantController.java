package com.voghbum.controller;

import com.voghbum.db.conf.MultitenantConfiguration;
import com.voghbum.db.master.entity.Tenant;
import com.voghbum.db.master.repository.TenantRepository;
import com.voghbum.service.TenantDatabaseProvisionService;
import com.voghbum.dto.TenantCreateRequest;
import com.voghbum.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/tenant")
public class TenantController {
    Logger logger = LoggerFactory.getLogger(TenantController.class);
    private final TenantRepository tenantRepository;
    private final MultitenantConfiguration multitenantConfiguration;
    private final TenantService tenantService;

    public TenantController(TenantRepository tenantRepository, MultitenantConfiguration multitenantConfiguration,
                            TenantService tenantService) {
        this.tenantRepository = tenantRepository;
        this.multitenantConfiguration = multitenantConfiguration;
        this.tenantService = tenantService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Tenant>> getAllTenants() {
        return ResponseEntity.ok(tenantRepository.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTenant(@RequestBody TenantCreateRequest request) {
        tenantService.createNewTenantStack(request);
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