-- liquibase formatted sql

-- changeset Sehui:19_add_reviewed_at_to_p_order

-- 2. reviewed_at 컬럼 추가
ALTER TABLE p_order
    ADD COLUMN reviewed_at TIMESTAMP;


-- rollback ALTER TABLE p_order DROP COLUMN reviewed_at;
