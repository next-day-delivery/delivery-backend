-- liquibase formatted sql

-- changeset Seungwon-Choi:01-create-p-user-table
CREATE TABLE p_user
(
    user_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username   VARCHAR(100) NOT NULL,
    nickname   VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    is_public  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) NOT NULL,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),

    -- Domain Constraints
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_role CHECK (role IN ('CUSTOMER', 'OWNER', 'MANAGER', 'MASTER'))
);

-- rollback DROP TABLE p_user;


-- changeset Seungwon-Choi:02-create-p-user-indexes
CREATE INDEX idx_users_email_active ON p_user (email) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_username_active ON p_user (username) WHERE deleted_at IS NULL;

-- rollback DROP INDEX idx_users_email_active;
-- rollback DROP INDEX idx_users_username_active;
