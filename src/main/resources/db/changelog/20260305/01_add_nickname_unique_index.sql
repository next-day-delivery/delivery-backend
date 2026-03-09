-- liquibase formatted sql

-- changeset Seungwon-Choi:17-add-nickname-unique-index
CREATE UNIQUE INDEX uk_users_nickname_active ON p_user (nickname) WHERE deleted_at IS NULL;

-- rollback DROP INDEX uk_users_nickname_active;
