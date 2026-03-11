--liquibase formatted sql
-- changeset Sehui:16-create-p-checkout-table
CREATE TABLE p_checkout
(
    checkout_id    UUID                        NOT NULL,
    order_no       VARCHAR(50)                 NOT NULL,
    cart_id        UUID                        NOT NULL,
    user_id        BIGINT                      NOT NULL,
    store_id       UUID                        NOT NULL,
    amount         BIGINT                      NOT NULL,
    cart_hash      VARCHAR(128)                NOT NULL,
    order_snapshot JSONB                       NOT NULL,
    status         VARCHAR(30)                 NOT NULL,
    expires_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    pg_payment_key VARCHAR(100),
    payment_method VARCHAR(30),
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     VARCHAR(100)                NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_by     VARCHAR(100),
    CONSTRAINT pk_p_checkout PRIMARY KEY (checkout_id)
);

-- 주문 번호 유니크 제약조건 추가
ALTER TABLE p_checkout
    ADD CONSTRAINT uc_p_checkout_order_no UNIQUE (order_no);

-- rollback DROP TABLE p_checkout;