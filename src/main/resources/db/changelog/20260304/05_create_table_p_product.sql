-- liquibase formatted sql

-- changeset Seungwon-Choi:08-create-p-product-table
CREATE TABLE p_product
(
    product_id     UUID PRIMARY KEY,
    store_id       UUID         NOT NULL,
    product_name   VARCHAR(100) NOT NULL,
    product_detail VARCHAR(255),
    price          INTEGER      NOT NULL,
    is_hidden      BOOLEAN      NOT NULL DEFAULT FALSE,

    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     VARCHAR(100) NOT NULL,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by     VARCHAR(100) NOT NULL,
    deleted_at     TIMESTAMP,
    deleted_by     VARCHAR(100),

    CONSTRAINT fk_product_store
        FOREIGN KEY (store_id)
            REFERENCES p_store (store_id)
            ON DELETE CASCADE,

    CONSTRAINT ck_product_price_positive
        CHECK (price >= 0)
);

-- rollback DROP TABLE p_product;
