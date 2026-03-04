-- liquibase formatted sql

-- changeset Seungwon-Choi:16-create-p-review-table
CREATE TABLE p_review
(
    review_id     UUID PRIMARY KEY,
    user_id       BIGINT       NOT NULL,
    order_id      UUID         NOT NULL,
    store_id      UUID         NOT NULL,
    content       VARCHAR(255),
    rating        INTEGER      NOT NULL,
    review_status VARCHAR(30)  NOT NULL,

    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(100) NOT NULL,

    CONSTRAINT fk_review_user
        FOREIGN KEY (user_id)
            REFERENCES p_user (user_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_review_order
        FOREIGN KEY (order_id)
            REFERENCES p_order (order_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_review_store
        FOREIGN KEY (store_id)
            REFERENCES p_store (store_id)
            ON DELETE CASCADE,

    -- 주문당 리뷰 1개 보장 (@OneToOne unique=true)
    CONSTRAINT uk_review_order
        UNIQUE (order_id),

    -- 별점 범위 제한 (1~5)
    CONSTRAINT ck_review_rating_range
        CHECK (rating BETWEEN 1 AND 5),

    -- Enum 무결성 보장
    CONSTRAINT ck_review_status
        CHECK (review_status IN ('VISIBLE', 'HIDDEN'))
);

-- rollback DROP TABLE p_review;
