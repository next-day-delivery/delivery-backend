-- liquibase formatted sql

-- changeset Sehui:19-add-reviewed-at-to-p-order
ALTER TABLE p_order
    ADD COLUMN reviewed_at TIMESTAMP;

-- rollback ALTER TABLE p_order DROP COLUMN reviewed_at;
