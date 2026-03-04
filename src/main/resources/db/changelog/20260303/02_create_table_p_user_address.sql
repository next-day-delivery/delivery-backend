-- liquibase formatted sql

-- changeset Seungwon-Choi:03-create-p-user-address-table
CREATE TABLE p_user_address
(
    user_address_id UUID PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    address         VARCHAR(255) NOT NULL,

    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(100) NOT NULL,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(100) NOT NULL,
    deleted_at      TIMESTAMP,
    deleted_by      VARCHAR(100),

    CONSTRAINT fk_user_address_user
        FOREIGN KEY (user_id)
            REFERENCES p_user (user_id)
            ON DELETE RESTRICT
);

-- rollback DROP TABLE p_user_address;
