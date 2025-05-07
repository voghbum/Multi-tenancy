package com.voghbum.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.util.concurrent.CompletableFuture;
import java.net.ServerSocket;

@Configuration
@EnableAsync
class AsyncConfig {}

@Service
public class TenantSubServiceProvider {
    private final Logger logger = LoggerFactory.getLogger(TenantSubServiceProvider.class);

    @Async
    public CompletableFuture<Integer> provisionDatabaseForTenant(String dbName, String user, String password) {
        logger.info("creating new postgresql container with docker compose...");
        try {
            int port = findRandomAvailablePort();
            String containerName = dbName;
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "compose", "-p", containerName, "up", "-d"
            );
            pb.directory(new java.io.File("multitenantnode/"));
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
                throw new RuntimeException("tenant db container cannot be created!");
            }
            boolean healthy = waitForContainerHealthy(containerName, 200); // 200 seconds timeout
            if (!healthy) {
                throw new RuntimeException("tenant db container is not healthy!");
            }
            return CompletableFuture.completedFuture(port);
        } catch (Exception e) {
            logger.error("tenant db container cannot be created!", e);
            throw new RuntimeException("tenant db container cannot be created!", e);
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

    /**
     * Finds a random available port on the system.
     */
    public int findRandomAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (Exception e) {
            logger.error("Could not find an available port", e);
            throw new RuntimeException("No available port found");
        }
    }

    /**
     * Runs the singlenode-app docker container on the given port.
     */
    @Async
    public CompletableFuture<Integer> runSingleNodeAppContainer(String tenantId) {
        int port = findRandomAvailablePort();
        String containerName = "singlenode-" + tenantId;
        logger.info("Running singlenode-app container for tenant {} on port {}", tenantId, port);
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "run", "-d", "--name", containerName, "-e", "PORT=" + port, "-p", port + ":9090", "singlenode-app"
            );
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.error("singlenode-app container could not be started!");
                throw new RuntimeException("singlenode-app container could not be started!");
            }
            boolean healthy = waitForContainerHealthy(containerName, 120); // 120 saniye timeout
            if (!healthy) {
                throw new RuntimeException("singlenode-app container is not healthy!");
            }
            return CompletableFuture.completedFuture(port);
        } catch (Exception e) {
            logger.error("singlenode-app container could not be started!", e);
            throw new RuntimeException("singlenode-app container could not be started!", e);
        }
    }
} 