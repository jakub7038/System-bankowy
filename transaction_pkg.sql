
CREATE OR REPLACE PACKAGE transaction_pkg IS

    TYPE transaction_obj IS RECORD (
        ID NUMBER,
        account_number CHAR(26),
        account_number_receiver CHAR(26),
        date_of_transaction TIMESTAMP,
        amount NUMBER,
        type_of_transaction VARCHAR2(50),
        description_of_transaction VARCHAR2(255)
    );

    TYPE transaction_tbl IS TABLE OF transaction_obj;

    PROCEDURE CREATE_TRANSACTION (
        p_account_number IN CHAR,
        p_account_number_receiver IN CHAR,
        p_amount IN NUMBER,
        p_type_of_transaction IN VARCHAR2,
        p_description_of_transaction IN VARCHAR2,
        p_result OUT VARCHAR2
    );

    FUNCTION READ_ALL_TRANSACTIONS_FUNC RETURN transaction_tbl PIPELINED;

    FUNCTION READ_TRANSACTIONS_BY_ACCOUNT_FUNC (
        p_account_number IN CHAR
    ) RETURN transaction_tbl PIPELINED;

END transaction_pkg;
/


CREATE OR REPLACE PACKAGE BODY transaction_pkg IS

    PROCEDURE CREATE_TRANSACTION (
    p_account_number IN CHAR,
    p_account_number_receiver IN CHAR,
    p_amount IN NUMBER,
    p_type_of_transaction IN VARCHAR2,
    p_description_of_transaction IN VARCHAR2,
    p_result OUT VARCHAR2
) IS
    v_sender_balance NUMBER;
    v_receiver_exists NUMBER;
BEGIN
    IF LENGTH(p_account_number) != 26 OR REGEXP_LIKE(p_account_number, '[^0-9]') THEN
        p_result := 'Error: Sender account number must be exactly 26 numeric characters.';
        RETURN;
    END IF;

    IF LENGTH(p_account_number_receiver) != 26 OR REGEXP_LIKE(p_account_number_receiver, '[^0-9]') THEN
        p_result := 'Error: Receiver account number must be exactly 26 numeric characters.';
        RETURN;
    END IF;

    IF p_account_number = p_account_number_receiver THEN
        p_result := 'Error: Sender and receiver account numbers cannot be the same.';
        RETURN;
    END IF;

    IF p_amount <= 0 THEN
        p_result := 'Error: Transaction amount must be greater than 0.';
        RETURN;
    END IF;

    IF p_type_of_transaction IS NULL OR TRIM(p_type_of_transaction) = '' THEN
        p_result := 'Error: Transaction type cannot be null or empty.';
        RETURN;
    END IF;

    BEGIN
        SELECT balance
        INTO v_sender_balance
        FROM ACCOUNT
        WHERE account_number = p_account_number;

        IF v_sender_balance <= 0 THEN
            p_result := 'Error: Sender balance must be a positive number.';
            RETURN;
        END IF;

        IF v_sender_balance < p_amount THEN
            p_result := 'Error: Insufficient funds.';
            RETURN;
        END IF;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_result := 'Error: Sender account does not exist.';
            RETURN;
    END;

    BEGIN
        SELECT COUNT(*)
        INTO v_receiver_exists
        FROM ACCOUNT
        WHERE account_number = p_account_number_receiver;

        IF v_receiver_exists > 0 THEN
            UPDATE ACCOUNT
            SET balance = balance + p_amount
            WHERE account_number = p_account_number_receiver;
        END IF;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_result := 'Error: Receiver account does not exist.';
            RETURN;
    END;

    UPDATE ACCOUNT
    SET balance = balance - p_amount
    WHERE account_number = p_account_number;

    INSERT INTO "TRANSACTION" (
        account_number,
        account_number_receiver,
        date_of_transaction,
        amount,
        type_of_transaction,
        description_of_transaction
    ) VALUES (
        p_account_number,
        p_account_number_receiver,
        CURRENT_TIMESTAMP,
        p_amount,
        p_type_of_transaction,
        p_description_of_transaction
    );

    COMMIT;
    p_result := 'Transaction completed successfully.';

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_result := 'Error: One or both account numbers do not exist.';
    WHEN OTHERS THEN
        ROLLBACK;
        IF SQLCODE = -1400 THEN
            p_result := 'Error: A required field is missing or NULL.';
        ELSE
            p_result := 'Error creating transaction: ' || SQLERRM;
        END IF;
END CREATE_TRANSACTION;

    FUNCTION READ_ALL_TRANSACTIONS_FUNC
    RETURN transaction_tbl PIPELINED
    IS
        l_transaction transaction_tbl := transaction_tbl();
    BEGIN
        FOR rec IN (
            SELECT ID, 
                   account_number, 
                   account_number_receiver, 
                   date_of_transaction, 
                   amount, 
                   type_of_transaction, 
                   description_of_transaction
            FROM "TRANSACTION"
        ) LOOP
            l_transaction.EXTEND; 
            l_transaction(l_transaction.COUNT) := transaction_obj(
                rec.ID,
                rec.account_number,
                rec.account_number_receiver,
                rec.date_of_transaction,
                rec.amount,
                rec.type_of_transaction,
                rec.description_of_transaction
            );
        END LOOP;

        FOR i IN 1..l_transaction.COUNT LOOP
            PIPE ROW(l_transaction(i));
        END LOOP;
        RETURN;
    END READ_ALL_TRANSACTIONS_FUNC;

    FUNCTION READ_TRANSACTIONS_BY_ACCOUNT_FUNC (
        p_account_number IN CHAR
    ) RETURN transaction_tbl PIPELINED
    IS
        l_transaction transaction_tbl := transaction_tbl();
    BEGIN
        FOR rec IN (
            SELECT ID, 
                   account_number, 
                   account_number_receiver, 
                   date_of_transaction, 
                   amount, 
                   type_of_transaction, 
                   description_of_transaction
            FROM "TRANSACTION"
            WHERE account_number = p_account_number 
               OR account_number_receiver = p_account_number
        ) LOOP
            l_transaction.EXTEND; 
            l_transaction(l_transaction.COUNT) := transaction_obj(
                rec.ID,
                rec.account_number,
                rec.account_number_receiver,
                rec.date_of_transaction,
                rec.amount,
                rec.type_of_transaction,
                rec.description_of_transaction
            );
        END LOOP;

        FOR i IN 1..l_transaction.COUNT LOOP
            PIPE ROW(l_transaction(i));
        END LOOP;
        RETURN;
    END READ_TRANSACTIONS_BY_ACCOUNT_FUNC;

END transaction_pkg;
/
