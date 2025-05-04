package com.voghbum.filter;

import com.voghbum.app.TenantContext;
import com.voghbum.app.TenantFinder;
import com.voghbum.controller.EmployeeController;
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
    private final Logger logger = LoggerFactory.getLogger(TenantFilter.class);

    public TenantFilter(TenantFinder tenantFinder) {
        this.tenantFinder = tenantFinder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String tenantID = tenantFinder.find(request);
            if(tenantID != null) {
                TenantContext.setCurrentTenant(tenantID);
                MDC.put("tenantID", tenantID);
                logger.info("MDC filter setted!");
                filterChain.doFilter(request, response);
            } else {
                throw new RuntimeException("TenantID cannot resolve using request");
            }
        } finally {
            TenantContext.setCurrentTenant(null);
        }
    }
}