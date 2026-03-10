-- liquibase formatted sql

-- changeset Sehui:20_fix_order_status_constraint
ALTER TABLE p_order
    DROP CONSTRAINT ck_order_status;

ALTER TABLE p_order
    ADD CONSTRAINT ck_order_status
        CHECK (order_status IN (
                                'ORDER_REQUESTED',
                                'ORDER_REJECTED',
                                'ORDER_CANCELED',
                                'ORDER_ACCEPTED',
                                'ORDER_COOKED',
                                'ORDER_COMPLETED'
            ));

-- rollback ALTER TABLE p_order DROP CONSTRAINT ck_order_status;
-- rollback ALTER TABLE p_order ADD CONSTRAINT ck_order_status CHECK (order_status IN ('PENDING','PAID','PREPARING','DELIVERING','COMPLETED','CANCELLED'));
