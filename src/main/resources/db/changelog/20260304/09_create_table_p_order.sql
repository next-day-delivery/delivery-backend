-- liquibase formatted sql

-- changeset Seungwon-Choi:12-create-p-order-table
CREATE TABLE p_order
(
    order_id     UUID PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    order_status VARCHAR(30)  NOT NULL,
    address      VARCHAR(255),

    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   VARCHAR(100) NOT NULL,

    CONSTRAINT fk_order_user
        FOREIGN KEY (user_id)
            REFERENCES p_user (user_id)
            ON DELETE RESTRICT,

    CONSTRAINT ck_order_status
        CHECK (order_status IN (
                                'PENDING',
                                'PAID',
                                'PREPARING',
                                'DELIVERING',
                                'COMPLETED',
                                'CANCELLED'
            ))
);

-- rollback DROP TABLE p_order;
