CREATE TABLE IF NOT EXISTS public.tenant_database (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL UNIQUE,
    driver_class_name VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
); 