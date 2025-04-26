package com.voghbum.internal.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voghbum.internal.TenantInterceptor;
import com.voghbum.internal.Tenants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ServiceConfiguration {

    @Bean
    WebMvcConfigurer webMvcConfigurer(ObjectMapper objectMapper, Tenants tenants) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new TenantInterceptor(objectMapper, tenants));
            }
        };
    }
}