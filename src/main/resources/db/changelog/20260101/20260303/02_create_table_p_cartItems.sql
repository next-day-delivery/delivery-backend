-- liquibase formatted sql

-- changeset Junwon-Kim:01-create-p-cart-item-table
CREATE TABLE p_cart_item
(
    cart_id    UUID   NOT NULL,
    product_id UUID   NOT NULL,
    quantity   BIGINT NOT NULL DEFAULT 1,

    CONSTRAINT pk_cart_item PRIMARY KEY (cart_id, product_id),
    CONSTRAINT ck_cart_item_quantity CHECK (quantity > 0)
);

-- rollback DROP TABLE p_cart_item;


-- changeset Junwon-Kim:02-create-p-cart-item-indexes
CREATE INDEX idx_cart_item_product_id ON p_cart_item (product_id);

-- rollback DROP INDEX idx_cart_item_product_id;
