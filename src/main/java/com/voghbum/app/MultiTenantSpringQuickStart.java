package com.voghbum.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication()
public class MultiTenantSpringQuickStart {
    public static void main(String[] args) {
        SpringApplication.run(MultiTenantSpringQuickStart.class, args);
    }
}
