-- liquibase formatted sql

-- changeset Seungwon-Choi:13-create-p-order-line-table
CREATE TABLE p_order_line
(
    order_line_id UUID PRIMARY KEY,
    order_id      UUID         NOT NULL,
    product_id    UUID         NOT NULL,
    quantity      BIGINT       NOT NULL,
    price         BIGINT       NOT NULL,

    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(100) NOT NULL,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by    VARCHAR(100) NOT NULL,
    deleted_at    TIMESTAMP,
    deleted_by    VARCHAR(100),

    CONSTRAINT fk_order_line_order
        FOREIGN KEY (order_id)
            REFERENCES p_order (order_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_order_line_product
        FOREIGN KEY (product_id)
            REFERENCES p_product (product_id)
            ON DELETE RESTRICT,

    CONSTRAINT ck_order_line_quantity_positive
        CHECK (quantity > 0),

    CONSTRAINT ck_order_line_price_positive
        CHECK (price >= 0)
);

-- rollback DROP TABLE p_order_line;
