-- liquibase formatted sql

-- changeset Seungwon-Choi:15-create-p-delivery-table
CREATE TABLE p_delivery
(
    delivery_id     UUID PRIMARY KEY,
    order_id        UUID         NOT NULL,
    delivery_status VARCHAR(30)  NOT NULL,

    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(100) NOT NULL,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(100) NOT NULL,
    deleted_at      TIMESTAMP,
    deleted_by      VARCHAR(100),

    CONSTRAINT fk_delivery_order
        FOREIGN KEY (order_id)
            REFERENCES p_order (order_id)
            ON DELETE CASCADE,

    -- 주문당 배달 1개 보장 (@OneToOne)
    CONSTRAINT uk_delivery_order
        UNIQUE (order_id),

    -- Enum 무결성 보장
    CONSTRAINT ck_delivery_status
        CHECK (delivery_status IN (
                                   'PENDING',
                                   'ING',
                                   'COMPLETED'
            ))
);

-- rollback DROP TABLE p_delivery;
