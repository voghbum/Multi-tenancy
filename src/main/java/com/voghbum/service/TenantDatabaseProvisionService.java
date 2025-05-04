package com.voghbum.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.util.concurrent.CompletableFuture;

@Configuration
@EnableAsync
class AsyncConfig {}

@Service
public class TenantDatabaseProvisionService {
    private final Logger logger = LoggerFactory.getLogger(TenantDatabaseProvisionService.class);

    @Async
    public CompletableFuture<Boolean> provisionDatabaseForTenant(String dbName, int port, String user, String password) {
        logger.info("creating new postgresql container with docker compose...");
        try {
            String containerName = dbName;
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "compose", "-p", containerName, "up", "-d"
            );
            pb.directory(new java.io.File(".")); // make sure it runs in project root
            pb.environment().put("CONTAINER_NAME", containerName);
            pb.environment().put("POSTGRES_DB", dbName);
            pb.environment().put("POSTGRES_USER", user);
            pb.environment().put("POSTGRES_PASSWORD", password);
            pb.environment().put("PORT", String.valueOf(port));
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.error("tenant db container cannot be created!");
                return CompletableFuture.completedFuture(false);
            }
            boolean healthy = waitForContainerHealthy(containerName, 200); // 200 seconds timeout
            return CompletableFuture.completedFuture(healthy);
        } catch (Exception e) {
            logger.error("tenant db container cannot be created!", e);
            return CompletableFuture.completedFuture(false);
        }
    }

    private boolean waitForContainerHealthy(String containerName, int timeoutSeconds) throws InterruptedException {
        logger.info("waiting for container healthy...");
        int waited = 0;
        while (waited < timeoutSeconds) {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                    "docker", "inspect", "-f", "{{.State.Health.Status}}", containerName
                );
                Process process = pb.start();
                BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
                String status = reader.readLine();
                process.waitFor();
                if (status != null && status.replaceAll("\"", "").trim().equals("healthy")) {
                    return true;
                }
            } catch (Exception ignored) {}
            Thread.sleep(1000);
            waited++;
        }
        logger.info("container cannot healthy");
        return false;
    }
} 