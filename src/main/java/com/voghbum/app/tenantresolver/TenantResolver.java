package com.voghbum.app.tenantresolver;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;

public abstract class TenantResolver {
    private TenantResolver next;
    abstract protected String resolve0(ServletRequest request);
    abstract protected boolean check(ServletRequest request);

    public String resolve(ServletRequest request) {
        if(!check(request) ) {
            if(next != null) {
                return next.resolve(request);
            }
            return null;
        }
        return resolve0(request);
    }

    public void setNext(TenantResolver tenantResolver) {
        this.next = tenantResolver;
    }
}
