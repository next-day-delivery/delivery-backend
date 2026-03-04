-- liquibase formatted sql

-- changeset Seungwon-Choi:11-create-p-ai-response-table
CREATE TABLE p_ai_response
(
    res_id       UUID PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    product_name VARCHAR(100),
    prompt       VARCHAR(255),
    content      VARCHAR(255),

    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   VARCHAR(100) NOT NULL,

    CONSTRAINT fk_ai_response_user
        FOREIGN KEY (user_id)
            REFERENCES p_user (user_id)
            ON DELETE CASCADE
);

-- rollback DROP TABLE p_ai_response;
