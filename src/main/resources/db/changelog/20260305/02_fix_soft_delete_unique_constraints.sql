-- liquibase formatted sql

-- changeset Seungwon-Choi:18-fix-soft-delete-unique-constraints
ALTER TABLE p_user
    DROP CONSTRAINT IF EXISTS uk_users_username;
ALTER TABLE p_user
    DROP CONSTRAINT IF EXISTS uk_users_email;

DROP INDEX IF EXISTS idx_users_email_active;
DROP INDEX IF EXISTS idx_users_username_active;

CREATE UNIQUE INDEX uk_users_username_active ON p_user (username) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uk_users_email_active ON p_user (email) WHERE deleted_at IS NULL;

-- rollback UPDATE p_user
-- rollback SET username = username || '_deleted_' || CAST(user_id AS VARCHAR)
-- rollback WHERE deleted_at IS NOT NULL
-- rollback   AND username IN (
-- rollback       SELECT username FROM p_user GROUP BY username HAVING COUNT(*) > 1
-- rollback   );

-- rollback UPDATE p_user
-- rollback SET email = email || '_deleted_' || CAST(user_id AS VARCHAR)
-- rollback WHERE deleted_at IS NOT NULL
-- rollback   AND email IN (
-- rollback       SELECT email FROM p_user GROUP BY email HAVING COUNT(*) > 1
-- rollback   );

-- rollback DROP INDEX IF EXISTS uk_users_username_active;
-- rollback DROP INDEX IF EXISTS uk_users_email_active;

-- rollback ALTER TABLE p_user ADD CONSTRAINT uk_users_email UNIQUE (email);
-- rollback ALTER TABLE p_user ADD CONSTRAINT uk_users_username UNIQUE (username);

-- rollback CREATE INDEX idx_users_email_active ON p_user (email) WHERE deleted_at IS NULL;
-- rollback CREATE INDEX idx_users_username_active ON p_user (username) WHERE deleted_at IS NULL;
