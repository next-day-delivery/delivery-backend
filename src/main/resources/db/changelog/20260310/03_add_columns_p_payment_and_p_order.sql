-- changeset Sehui:15-update-p-payment-and-p-order
-- 1. p_payment 테이블 컬럼 추가 및 속성 변경

ALTER TABLE p_payment
    ADD COLUMN user_id BIGINT; -- 유저 PK 타입에 맞게 조정
ALTER TABLE p_payment
    ADD COLUMN amount BIGINT;
ALTER TABLE p_payment
    ADD COLUMN payment_key VARCHAR(255);

-- 2. 기존 NOT NULL 제약조건 변경 (주문 생성 전 결제 생성을 위해 order_id를 선택사항으로)
ALTER TABLE p_payment
    ALTER COLUMN order_id DROP NOT NULL;

-- 3. 유니크 및 외래키 제약조건 추가

ALTER TABLE p_payment
    ADD CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES p_user (user_id);

-- 4. p_order 테이블 비즈니스 키 추가
ALTER TABLE p_order
    ADD COLUMN order_no VARCHAR(255);
ALTER TABLE p_order
    ADD CONSTRAINT uk_order_order_no UNIQUE (order_no);

-- rollback ALTER TABLE p_order DROP CONSTRAINT uk_order_order_no;
-- rollback ALTER TABLE p_order DROP COLUMN order_no;
-- rollback ALTER TABLE p_payment DROP CONSTRAINT fk_payment_user;
-- rollback ALTER TABLE p_payment DROP CONSTRAINT uk_payment_order_no;
-- rollback ALTER TABLE p_payment DROP COLUMN payment_key;
-- rollback ALTER TABLE p_payment DROP COLUMN amount;
-- rollback ALTER TABLE p_payment DROP COLUMN user_id;
-- rollback ALTER TABLE p_payment DROP COLUMN order_no;
-- rollback ALTER TABLE p_payment ALTER COLUMN order_id SET NOT NULL;