-- liquibase formatted sql

-- changeset Sehui:19-add-reviewed-at-to-p-order

-- 2. reviewed_at 컬럼 추가
ALTER TABLE p_order
    ADD COLUMN reviewed_at TIMESTAMP;


-- rollback ALTER TABLE p_order DROP COLUMN reviewed_at;