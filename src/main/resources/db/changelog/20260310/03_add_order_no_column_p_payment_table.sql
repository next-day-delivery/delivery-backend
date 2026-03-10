--liquibase formatted sql

--changeset sehui:20260310-1
--comment: order_id NULL 허용 및 order_no 컬럼 추가
ALTER TABLE p_payment
    ALTER COLUMN order_id DROP NOT NULL;

--changeset sehui:20260310-2
--comment: order_no 컬럼 추가 및 UNIQUE 제약 조건 설정
ALTER TABLE p_payment
    ADD COLUMN order_no VARCHAR(50);
ALTER TABLE p_payment
    ADD CONSTRAINT uc_p_payment_order_no UNIQUE (order_no);