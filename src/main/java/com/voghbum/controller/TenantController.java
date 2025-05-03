package com.voghbum.controller;

import com.voghbum.db.conf.MultitenantConfiguration;
import com.voghbum.db.master.entity.Tenant;
import com.voghbum.db.master.repository.TenantRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant")
public class TenantController {

    private final TenantRepository tenantRepository;
    private final MultitenantConfiguration multitenantConfiguration;

    public TenantController(TenantRepository tenantRepository, MultitenantConfiguration multitenantConfiguration) {
        this.tenantRepository = tenantRepository;
        this.multitenantConfiguration = multitenantConfiguration;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Tenant>> getAllTenants() {
        return ResponseEntity.ok(tenantRepository.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<Tenant> createTenant(@RequestBody Tenant tenant) {
        Tenant savedTenant = tenantRepository.save(tenant);
        multitenantConfiguration.registerNewTenant(savedTenant);
        return ResponseEntity.ok(savedTenant);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeTenant(@RequestParam String tenantId) {
        multitenantConfiguration.removeTenant(tenantId);
        tenantRepository.findByTenantId(tenantId)
                .ifPresent(tenantRepository::delete);
                
        return ResponseEntity.ok().build();
    }
} 