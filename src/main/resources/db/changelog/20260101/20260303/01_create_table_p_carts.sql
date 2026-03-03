-- liquibase formatted sql

-- changeset Junwon-Kim:01-create-p-cart-table
CREATE TABLE p_cart
(
    cart_id      UUID PRIMARY KEY,
    user_id      BIGINT      NOT NULL,
    store_id     UUID        NOT NULL,
    cart_status  VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT ck_cart_status
        CHECK (cart_status IN ('ACTIVE', 'INACTIVE', 'COMPLETED'))
);

-- rollback DROP TABLE p_cart;

-- changeset Junwon-Kim:02-create-p-cart-indexes
CREATE INDEX idx_cart_user_status
    ON p_cart (user_id, cart_status);

CREATE INDEX idx_cart_store_id
    ON p_cart (store_id);

-- rollback DROP INDEX idx_cart_user_status;
-- rollback DROP INDEX idx_cart_store_id;
