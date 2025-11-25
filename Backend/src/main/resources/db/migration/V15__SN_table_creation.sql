-- ========================
-- SELLER TABLE
-- ========================
CREATE TABLE seller (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- PLATFORM CREDENTIAL TABLE
-- ========================
CREATE TABLE platform_credential (
    id SERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    platform VARCHAR(100) NOT NULL,
    api_key VARCHAR(255),
    api_secret VARCHAR(255),
    access_token VARCHAR(500),
    refresh_token VARCHAR(500),
    expires_in INTEGER,
    expiry_time BIGINT,
    external_merchant_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES seller(id) ON DELETE CASCADE
);


-- ========================
-- PRODUCT TABLE
-- ========================
CREATE TABLE sn_product (
    id SERIAL PRIMARY KEY,
    seller_id INT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    sku VARCHAR(100) NOT NULL UNIQUE,
    price DECIMAL(10,2),
    quantity INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES seller(id) ON DELETE CASCADE
);

-- ========================
-- TRANSFER LOG TABLE
-- ========================
CREATE TABLE transfer_log (
    id SERIAL PRIMARY KEY,
    seller_id INT NOT NULL,
    product_id INT,
    platform_name VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    message TEXT,
    synced_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES seller(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES sn_product(id) ON DELETE SET NULL
);
