-- liquibase formatted sql

-- changeset Seungwon-Choi:10-create-p-cart-item-table
CREATE TABLE p_cart_item
(
    cart_item_id UUID PRIMARY KEY,
    cart_id      UUID   NOT NULL,
    product_id   UUID   NOT NULL,
    quantity     BIGINT NOT NULL,

    CONSTRAINT fk_cart_item_cart
        FOREIGN KEY (cart_id)
            REFERENCES p_cart (cart_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_cart_item_product
        FOREIGN KEY (product_id)
            REFERENCES p_product (product_id)
            ON DELETE RESTRICT,

    CONSTRAINT ck_cart_item_quantity_positive
        CHECK (quantity > 0)
);

-- rollback DROP TABLE p_cart_item;
