-- liquibase formatted sql

-- changeset Seungwon-Choi:09-create-p-cart-table
CREATE TABLE p_cart
(
    cart_id  UUID PRIMARY KEY,
    user_id  BIGINT      NOT NULL,
    store_id UUID        NOT NULL,
    status   VARCHAR(30) NOT NULL,

    CONSTRAINT fk_cart_user
        FOREIGN KEY (user_id)
            REFERENCES p_user (user_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_cart_store
        FOREIGN KEY (store_id)
            REFERENCES p_store (store_id)
            ON DELETE CASCADE,

    CONSTRAINT ck_cart_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'COMPLETED'))
);

-- rollback DROP TABLE p_cart;
