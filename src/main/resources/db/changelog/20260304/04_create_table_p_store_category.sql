-- liquibase formatted sql

-- changeset Seungwon-Choi:07-create-p-store-category-table
CREATE TABLE p_store_category
(
    store_category_id UUID PRIMARY KEY,
    store_id          UUID NOT NULL,
    category_id       UUID NOT NULL,

    CONSTRAINT fk_store_category_store
        FOREIGN KEY (store_id)
            REFERENCES p_store (store_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_store_category_category
        FOREIGN KEY (category_id)
            REFERENCES p_category (category_id)
            ON DELETE RESTRICT,

    CONSTRAINT uk_store_category
        UNIQUE (store_id, category_id)
);

-- rollback DROP TABLE p_store_category;
