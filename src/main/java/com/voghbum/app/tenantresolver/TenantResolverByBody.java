package com.voghbum.app.tenantresolver;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class TenantResolverByBody extends TenantResolver {
    @Override
    public String resolve0(ServletRequest request) {
        return ((HttpServletRequest) request).getHeader("X-TenantID");
    }

    @Override
    public boolean check(ServletRequest request) {
        if(request instanceof HttpServletRequest hr) {
            return hr.getHeader("X-TenantID") != null;
        }
        return false;
    }
}
