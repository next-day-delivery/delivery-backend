-- liquibase formatted sql

-- changeset Seungwon-Choi:20-user-address-partial-unique-index runInTransaction:false
CREATE UNIQUE INDEX CONCURRENTLY uk_user_address_active
    ON p_user_address (user_id)
    WHERE deleted_at IS NULL;

-- rollback DROP INDEX CONCURRENTLY IF EXISTS uk_user_address_active;
