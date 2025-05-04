package com.voghbum.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TenantDatabaseProvisionService {
    private final Logger logger = LoggerFactory.getLogger(TenantDatabaseProvisionService.class);
    public boolean provisionDatabaseForTenant(String dbName, int port, String user, String password) {
        try {
            String containerName = dbName;
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "run", "-d",
                "--name", containerName,
                "-e", "POSTGRES_DB=" + dbName,
                "-e", "POSTGRES_USER=" + user,
                "-e", "POSTGRES_PASSWORD=" + password,
                "-p", port + ":5432",
                "postgres:15"
            );
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.error("tenant db container db cannot created!");
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("tenant db container db cannot created!", e);
            return false;
        }
    }
} 