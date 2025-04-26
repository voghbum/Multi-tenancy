package com.voghbum;

import com.voghbum.internal.configuration.TenantsContextInitializer;
import com.voghbum.internal.configuration.TenantsDatabaseInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication()
public class MultiTenantSpringQuickStart {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MultiTenantSpringQuickStart.class)
                .initializers(new TenantsContextInitializer())
                .listeners(new TenantsDatabaseInitializer())
                .run(args);
    }
}
