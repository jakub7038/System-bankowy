CREATE TABLE CLIENT (
    PESEL CHAR(11) PRIMARY KEY,
    first_name VARCHAR(24) NOT NULL,
    last_name VARCHAR(24) NOT NULL,
    middle_name VARCHAR(24),
    phone_number VARCHAR(16) NOT NULL,
    date_of_birth DATE NOT NULL
);

CREATE TABLE ACCOUNT (
    account_number CHAR(26) PRIMARY KEY,
    type_of_account VARCHAR(20) NOT NULL,
    balance NUMBER DEFAULT 0 NOT NULL,
    date_of_creation DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    salt CHAR(32),
    password CHAR(64) NOT NULL
);

CREATE TABLE CLIENT_ACCOUNT (
    pesel CHAR(11),
    account_number CHAR(26),
    PRIMARY KEY (pesel, account_number),
    FOREIGN KEY (pesel) REFERENCES CLIENT (PESEL) ON DELETE CASCADE,
    FOREIGN KEY (account_number) REFERENCES ACCOUNT (account_number) ON DELETE CASCADE
);

CREATE TABLE CARD (
    card_number CHAR(16) PRIMARY KEY,
    date_of_expiration DATE NOT NULL,
    daily_limit NUMBER(15) NOT NULL CHECK (daily_limit >= 0),
    single_payment_limit NUMBER(15) NOT NULL CHECK (single_payment_limit >= 0),
    CVV CHAR(3) NOT NULL,
    is_active NUMBER(1) DEFAULT 0,
    account_number CHAR(26) NOT NULL,
    PIN CHAR(4) NOT NULL,
    FOREIGN KEY (account_number) REFERENCES ACCOUNT(account_number) ON DELETE CASCADE
);

CREATE TABLE TRANSACTION (
    ID NUMBER PRIMARY KEY,
    account_number CHAR(26) NOT NULL,
    account_number_RECEIVER CHAR(26) NOT NULL,
    date_of_transaction TIMESTAMP NOT NULL,
    amount NUMBER DEFAULT 0 NOT NULL,
    type_of_transaction VARCHAR(50) NOT NULL,
    description_of_transaction VARCHAR(255),
    FOREIGN KEY (account_number) REFERENCES ACCOUNT(account_number)
);

CREATE TABLE RECEIVER (
    account_number_RECEIVER CHAR(26) NOT NULL,
    account_number_TIED CHAR(26) NOT NULL,
    description VARCHAR(255),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    CONSTRAINT pk_receiver PRIMARY KEY (account_number_RECEIVER, account_number_TIED),
    CONSTRAINT fk_receiver_account FOREIGN KEY (account_number_TIED) REFERENCES ACCOUNT(account_number) ON DELETE CASCADE
);


CREATE SEQUENCE transaction_id_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;
/

CREATE OR REPLACE TRIGGER trg_transaction_id
BEFORE INSERT ON "TRANSACTION"
FOR EACH ROW
BEGIN
    :NEW.ID := transaction_id_seq.NEXTVAL;
END;
/