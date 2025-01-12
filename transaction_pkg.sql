
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
        p_id IN NUMBER,
        p_account_number IN CHAR,
        p_account_number_receiver IN CHAR,
        p_date_of_transaction IN TIMESTAMP,
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
        p_id IN NUMBER,
        p_account_number IN CHAR,
        p_account_number_receiver IN CHAR,
        p_date_of_transaction IN TIMESTAMP,
        p_amount IN NUMBER,
        p_type_of_transaction IN VARCHAR2,
        p_description_of_transaction IN VARCHAR2,
        p_result OUT VARCHAR2
    ) IS
    BEGIN
        INSERT INTO "TRANSACTION" (
            ID, 
            account_number,
            account_number_receiver,
            date_of_transaction,
            amount,
            type_of_transaction,
            description_of_transaction
        ) VALUES (
            p_id,
            p_account_number,
            p_account_number_receiver,
            p_date_of_transaction,
            p_amount,
            p_type_of_transaction,
            p_description_of_transaction
        );

        COMMIT;
        p_result := 'Transaction created successfully.';
    EXCEPTION
        WHEN OTHERS THEN
            p_result := 'Error creating transaction: ' || SQLERRM;
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



