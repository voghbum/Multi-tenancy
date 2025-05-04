package com.voghbum.dto;

public class TenantCreateRequest {
    private String tenantId;
    private String dbName;
    private int port;
    private String dbUser;
    private String dbPassword;

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getDbName() { return dbName; }
    public void setDbName(String dbName) { this.dbName = dbName; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getDbUser() { return dbUser; }
    public void setDbUser(String dbUser) { this.dbUser = dbUser; }
    public String getDbPassword() { return dbPassword; }
    public void setDbPassword(String dbPassword) { this.dbPassword = dbPassword; }
} 