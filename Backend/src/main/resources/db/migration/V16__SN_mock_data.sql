-- 1. Insert Sellers with explicit IDs
INSERT INTO seller (id, name, email) VALUES
(1, 'Arpit Vora', 'arpit@sellernexus.com'),
(2, 'Riya Shah', 'riya@sellernexus.com'),
(3, 'Karan Patel', 'karan@sellernexus.com'),
(4, 'Sneha Mehta', 'sneha@sellernexus.com'),
(5, 'John Doe', 'john@sellernexus.com');

-- Reset the sequence to continue from the last inserted ID
SELECT setval('seller_id_seq', 5, true);


 -- 2. Insert Platform Credentials
 INSERT INTO platform_credential
 (seller_id, platform, api_key, api_secret, access_token, refresh_token, expires_in, expiry_time, external_merchant_id)
 VALUES
 (1, 'SourcePlatform', 'SRC_KEY_1', 'SRC_SECRET_1', 'SRC_TOKEN_1', NULL, NULL, NULL, NULL),
 (1, 'DestinationPlatform', 'DST_KEY_1', 'DST_SECRET_1', 'DST_TOKEN_1', NULL, NULL, NULL, NULL),

 (2, 'SourcePlatform', 'SRC_KEY_2', 'SRC_SECRET_2', 'SRC_TOKEN_2', NULL, NULL, NULL, NULL),
 (2, 'DestinationPlatform', 'DST_KEY_2', 'DST_SECRET_2', 'DST_TOKEN_2', NULL, NULL, NULL, NULL),

 (3, 'SourcePlatform', 'SRC_KEY_3', 'SRC_SECRET_3', 'SRC_TOKEN_3', NULL, NULL, NULL, NULL),

 (4, 'SourcePlatform', 'SRC_KEY_4', 'SRC_SECRET_4', 'SRC_TOKEN_4', NULL, NULL, NULL, NULL),
 (4, 'DestinationPlatform', 'DST_KEY_4', 'DST_SECRET_4', 'DST_TOKEN_4', NULL, NULL, NULL, NULL),

 (5, 'SourcePlatform', 'SRC_KEY_5', 'SRC_SECRET_5', 'SRC_TOKEN_5', NULL, NULL, NULL, NULL);



-- 3. Insert Products
INSERT INTO sn_product (seller_id, product_name, sku, price, quantity) VALUES
(1, 'Wireless Mouse', 'WM-1001', 19.99, 150),
(1, 'Gaming Keyboard', 'GK-2001', 49.99, 80),
(2, 'Bluetooth Speaker', 'BS-3001', 29.99, 120),
(2, 'Phone Stand', 'PS-4001', 9.99, 200),
(3, 'LED Table Lamp', 'TL-5001', 24.99, 60),
(4, 'USB-C Cable', 'UC-6001', 7.99, 500),
(4, 'Travel Backpack', 'TB-7001', 39.99, 45),
(5, 'Yoga Mat', 'YM-8001', 22.99, 100);

-- 4. Insert Transfer Logs
INSERT INTO transfer_log (seller_id, product_id, platform_name, status, message) VALUES
(1, 1, 'SourcePlatform', 'SUCCESS', 'Product synced successfully'),
(1, 2, 'DestinationPlatform', 'FAILED', 'API authentication error'),
(2, 3, 'SourcePlatform', 'SUCCESS', 'Price updated'),
(2, 4, 'DestinationPlatform', 'SUCCESS', 'Stock updated'),
(3, 5, 'DestinationPlatform', 'FAILED', 'SKU not found on platform'),
(4, 6, 'SourcePlatform', 'SUCCESS', 'Product added to inventory'),
(4, 7, 'DestinationPlatform', 'FAILED', 'Rate limit exceeded'),
(5, 8, 'DestinationPlatform', 'SUCCESS', 'New product created');
