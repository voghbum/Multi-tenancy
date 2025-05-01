package com.voghbum.filter;

import com.voghbum.app.TenantContext;
import com.voghbum.app.TenantFinder;
import com.voghbum.service.JwtService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
class TenantFilter implements Filter {
    private final TenantFinder tenantFinder;

    public TenantFilter(TenantFinder tenantFinder) {
        this.tenantFinder = tenantFinder;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            String tenantID = tenantFinder.find(request);
            if(tenantID != null) {
                TenantContext.setCurrentTenant(tenantID);
                chain.doFilter(request, response);
            } else {
                throw new RuntimeException("TenantID cannot resolve using request");
            }
        } finally {
            TenantContext.setCurrentTenant(null);
        }
    }
}