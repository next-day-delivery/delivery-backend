-- liquibase formatted sql

-- changeset Seungwon-Choi:06-create-p-category-table
CREATE TABLE p_category
(
    category_id   UUID PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,

    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(100) NOT NULL,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by    VARCHAR(100) NOT NULL,
    deleted_at    TIMESTAMP,
    deleted_by    VARCHAR(100),

    CONSTRAINT uk_category_name
        UNIQUE (category_name)
);

CREATE INDEX idx_category_name
    ON p_category (category_name);

-- rollback DROP TABLE p_category;
