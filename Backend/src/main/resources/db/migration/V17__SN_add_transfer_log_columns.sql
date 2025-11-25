-- V18__recreate_transfer_log.sql
CREATE TABLE IF NOT EXISTS transfer_log (
  id SERIAL PRIMARY KEY,
  seller_id INT NOT NULL,
  product_id INT,
  platform_name VARCHAR(100) NOT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
  message TEXT,
  synced_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  -- new audit columns
  source_credential_id BIGINT,
  target_credential_id BIGINT,
  source_product_ext_id VARCHAR(255),
  target_product_ext_id VARCHAR(255),
  request_payload JSONB,
  response_payload JSONB,
  error_message TEXT,
  started_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
  finished_at TIMESTAMPTZ,
  duration_ms INTEGER
);

-- Add FKs only if referenced tables exist
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'fk_transfer_seller'
  ) THEN
    ALTER TABLE transfer_log
      ADD CONSTRAINT fk_transfer_seller FOREIGN KEY (seller_id) REFERENCES seller(id) ON DELETE CASCADE;
  END IF;
  
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'fk_transfer_product'
  ) THEN
    ALTER TABLE transfer_log
      ADD CONSTRAINT fk_transfer_product FOREIGN KEY (product_id) REFERENCES sn_product(id) ON DELETE SET NULL;
  END IF;
END$$;

-- Indexes
CREATE INDEX IF NOT EXISTS idx_transfer_log_seller_started ON transfer_log (seller_id, started_at DESC);
CREATE INDEX IF NOT EXISTS idx_transfer_log_status ON transfer_log (status);
CREATE INDEX IF NOT EXISTS idx_transfer_log_src_ext_id ON transfer_log (source_product_ext_id);