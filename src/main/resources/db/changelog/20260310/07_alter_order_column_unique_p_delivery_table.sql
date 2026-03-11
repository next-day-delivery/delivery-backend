--liquibase formatted sql
-- changeset Sehui:17-add-unique-to-delivery-order-id
ALTER TABLE p_delivery
    ADD CONSTRAINT uk_delivery_order_id UNIQUE (order_id);

-- rollback ALTER TABLE p_delivery DROP CONSTRAINT uk_delivery_order_id;