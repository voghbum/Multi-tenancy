package com.voghbum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MultiTenantSpringQuickStart {
    public static void main(String[] args) {
        SpringApplication.run(MultiTenantSpringQuickStart.class, args);
    }
}
