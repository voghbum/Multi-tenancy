package com.voghbum.app.tenantresolver;

import com.voghbum.service.JwtService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class TenantResolverByJWT extends TenantResolver{
    private final JwtService jwtService;

    public TenantResolverByJWT(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String resolve0(ServletRequest request) {
        String authHeader = ((HttpServletRequest) request).getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtService.extractTenantId(token);
        }
        return null;
    }

    @Override
    public boolean check(ServletRequest request) {
        if (request instanceof HttpServletRequest hr) {
            return hr.getHeader("Authorization") != null;
        }
        return false;
    }
}
