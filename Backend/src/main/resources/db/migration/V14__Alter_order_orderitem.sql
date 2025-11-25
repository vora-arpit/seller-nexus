
ALTER TABLE orders ADD COLUMN orderitem_id BIGINT;

ALTER TABLE orders ADD CONSTRAINT fk_order_orderitem FOREIGN KEY (orderitem_id) REFERENCES orderitem(id);
