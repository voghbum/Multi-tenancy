CREATE TABLE IF NOT EXISTS public.tenant_database (
    id BIGSERIAL PRIMARY KEY,
    tenantId VARCHAR(255) NOT NULL UNIQUE,
    driverClassName VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    singleNodeEndpoint VARCHAR(255) NOT NULL
);