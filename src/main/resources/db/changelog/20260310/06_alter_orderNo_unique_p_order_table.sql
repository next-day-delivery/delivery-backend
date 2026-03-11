--liquibase formatted sql
-- changeset Sehui:16-add-notnull-to-order-no
-- 1. p_order 테이블의 order_no 컬럼에 NOT NULL 제약 조건 추가

-- 기존 데이터 중 null이 있다면 먼저 채워줘야 에러가 안 납니다 (필요 시 주석 해제)
-- UPDATE p_order SET order_no = 'TEMP_' || CAST(order_id AS VARCHAR) WHERE order_no IS NULL;

ALTER TABLE p_order
    ALTER COLUMN order_no SET NOT NULL;

-- 2. p_checkout 테이블에 결제 대기 중인 장바구니에 대한 부분 유니크 인덱스 추가
CREATE UNIQUE INDEX uq_p_checkout_cart_pending
    ON p_checkout (cart_id)
    WHERE status = 'PAYMENT_PENDING';

-- rollback DROP INDEX uq_p_checkout_cart_pending;
-- rollback ALTER TABLE p_order ALTER COLUMN order_no DROP NOT NULL;