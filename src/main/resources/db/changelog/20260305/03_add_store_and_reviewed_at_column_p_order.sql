-- liquibase formatted sql

-- changeset Sehui:13-add-store-id-and-reviewed-at-to-p-order
-- 1. store_id 컬럼 추가
ALTER TABLE p_order
    ADD COLUMN store_id UUID;

-- 2. reviewed_at 컬럼 추가
ALTER TABLE p_order
    ADD COLUMN reviewed_at TIMESTAMP;

-- 3. 외래키 제약조건 추가 (p_store 테이블 참조)
ALTER TABLE p_order
    ADD CONSTRAINT fk_order_store
        FOREIGN KEY (store_id)
            REFERENCES p_store (store_id)
            ON DELETE RESTRICT;

-- 4. store_id NOT NULL 설정
ALTER TABLE p_order
    ALTER COLUMN store_id SET NOT NULL;

-- rollback ALTER TABLE p_order DROP CONSTRAINT fk_order_store;
-- rollback ALTER TABLE p_order DROP COLUMN store_id;
-- rollback ALTER TABLE p_order DROP COLUMN reviewed_at;