-- liquibase formatted sql

-- changeset Seungwon-Choi:14-create-p-payment-table
CREATE TABLE p_payment
(
    payment_id     UUID PRIMARY KEY,
    order_id       UUID         NOT NULL,
    payment_method VARCHAR(30)  NOT NULL,
    payment_status VARCHAR(30)  NOT NULL,

    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     VARCHAR(100) NOT NULL,

    CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id)
            REFERENCES p_order (order_id)
            ON DELETE CASCADE,

    -- 주문당 결제 1개 보장 (@OneToOne)
    CONSTRAINT uk_payment_order
        UNIQUE (order_id),

    -- 결제 방법 Enum 무결성
    CONSTRAINT ck_payment_method
        CHECK (payment_method IN (
                                  'CARD',
                                  'KAKAO_PAY',
                                  'NAVER_PAY',
                                  'TOSS_PAY'
            )),

    -- 결제 상태 Enum 무결성
    CONSTRAINT ck_payment_status
        CHECK (payment_status IN (
                                  'READY',
                                  'COMPLETE',
                                  'FAILED',
                                  'CANCELLED'
            ))
);

-- rollback DROP TABLE p_payment;
