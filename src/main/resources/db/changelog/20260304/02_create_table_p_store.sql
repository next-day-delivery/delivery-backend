-- liquibase formatted sql

-- changeset Seungwon-Choi:05-create-p-store-table
CREATE TABLE p_store
(
    store_id         UUID PRIMARY KEY,
    user_id          BIGINT        NOT NULL,
    store_address_id BIGINT        NOT NULL,
    name             VARCHAR(100)  NOT NULL,
    rating_avg       DECIMAL(2, 1) NOT NULL DEFAULT 0.0,
    review_count     INTEGER       NOT NULL DEFAULT 0,
    detail_address   VARCHAR(255),

    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(100)  NOT NULL,
    updated_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(100)  NOT NULL,
    deleted_at       TIMESTAMP,
    deleted_by       VARCHAR(100),

    CONSTRAINT fk_store_user
        FOREIGN KEY (user_id)
            REFERENCES p_user (user_id)
            ON DELETE RESTRICT,

    CONSTRAINT fk_store_address
        FOREIGN KEY (store_address_id)
            REFERENCES p_store_address (store_address_id)
            ON DELETE RESTRICT,

    CONSTRAINT ck_store_rating_range
        CHECK (rating_avg BETWEEN 0 AND 5),

    CONSTRAINT ck_store_review_count_non_negative
        CHECK (review_count >= 0)
);

-- rollback DROP TABLE p_store;
