-- liquibase formatted sql

-- changeset sehui:20260311-01-update-status-constraints
ALTER TABLE p_payment DROP CONSTRAINT ck_payment_status;
ALTER TABLE p_payment ADD CONSTRAINT ck_payment_status
    CHECK (payment_status IN ('PENDING', 'COMPLETED', 'CANCELED', 'FAILED','CANCELFAILED'));

ALTER TABLE p_delivery DROP CONSTRAINT ck_delivery_status;
ALTER TABLE p_delivery ADD CONSTRAINT ck_delivery_status
    CHECK (delivery_status IN ('DELIVERY_PENDING', 'DELIVERY_ING', 'DELIVERY_COMPLETED', 'DELIVERY_CANCELED'));

-- rollback ALTER TABLE p_payment DROP CONSTRAINT ck_payment_status;
-- rollback ALTER TABLE p_delivery DROP CONSTRAINT ck_delivery_status;