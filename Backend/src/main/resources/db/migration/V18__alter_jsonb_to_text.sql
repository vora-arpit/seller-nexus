-- Change JSONB columns to TEXT for better Hibernate compatibility
ALTER TABLE transfer_log
  ALTER COLUMN request_payload TYPE TEXT,
  ALTER COLUMN response_payload TYPE TEXT;
