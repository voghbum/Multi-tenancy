package com.voghbum.filter;

import com.voghbum.app.TenantContext;
import com.voghbum.app.TenantFinder;
import com.voghbum.controller.EmployeeController;
import com.voghbum.db.master.repository.TenantRepository;
import com.voghbum.service.JwtService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {
    private final TenantFinder tenantFinder;
    private final TenantRepository tenantRepository;

    public TenantFilter(TenantFinder tenantFinder, TenantRepository tenantRepository) {
        this.tenantFinder = tenantFinder;
        this.tenantRepository = tenantRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String tenantID = tenantFinder.find(request);
            if(tenantID == null) {
                throw new RuntimeException("TenantID cannot resolve using request");
            } else {
                if(!tenantID.equals("Super Admin")) {
                    tenantRepository.findByTenantId(tenantID).orElseThrow(() -> new RuntimeException("Given tenant-ID unknown! tenant-Id: " + tenantID));
                    setTenantId(tenantID);
                }
                else {
                    setTenantId(tenantID);
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.setCurrentTenant(null);
        }
    }

    private void setTenantId(String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
        MDC.put("tenantID", tenantId);
    }
}