-- liquibase formatted sql

-- changeset Sehui:13-add-store-id-and-reviewed-at-to-p-order

-- 2. reviewed_at 컬럼 추가
ALTER TABLE p_order
    ADD COLUMN reviewed_at TIMESTAMP;


-- rollback ALTER TABLE p_order DROP CONSTRAINT fk_order_store;
-- rollback ALTER TABLE p_order DROP COLUMN store_id;
-- rollback ALTER TABLE p_order DROP COLUMN reviewed_at;