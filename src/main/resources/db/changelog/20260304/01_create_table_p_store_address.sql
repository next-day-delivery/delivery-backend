-- liquibase formatted sql

-- changeset Seungwon-Choi:04-create-p-store-address-table
CREATE TABLE p_store_address
(
    store_address_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sido             VARCHAR(32) NOT NULL,
    sigungu          VARCHAR(32) NOT NULL,
    dong             VARCHAR(32) NOT NULL
);

-- rollback DROP TABLE p_store_address;
