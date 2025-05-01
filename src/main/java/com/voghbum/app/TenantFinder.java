package com.voghbum.app;

import com.voghbum.app.tenantresolver.TenantResolver;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TenantFinder {
    private final TenantResolver tenantResolver;

    public TenantFinder(List<TenantResolver> tenantResolvers) {
        this.tenantResolver = tenantResolvers.get(0);
        TenantResolver chain = this.tenantResolver;
        for (int i = 1; i < tenantResolvers.size(); i++) {
            chain.setNext(tenantResolvers.get(i));
            chain = tenantResolvers.get(i);
        }
    }

    public String find(ServletRequest request) {
        return tenantResolver.resolve(request);
    }
}
