CREATE OR REPLACE PACKAGE transaction_pkg IS

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

    PROCEDURE READ_ALL_TRANSACTIONS (
        p_result OUT SYS_REFCURSOR
    );

    PROCEDURE READ_TRANSACTIONS_BY_ACCOUNT (
        p_account_number IN CHAR,
        p_result OUT SYS_REFCURSOR
    );

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
        INSERT INTO TRANSACTION (
            ID, 
            account_number,
            account_number_RECEIVER,
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

    PROCEDURE READ_ALL_TRANSACTIONS (
        p_result OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_result FOR
            SELECT ID, 
                   account_number, 
                   account_number_RECEIVER, 
                   date_of_transaction, 
                   amount, 
                   type_of_transaction, 
                   description_of_transaction
            FROM TRANSACTION;
    END READ_ALL_TRANSACTIONS;

    PROCEDURE READ_TRANSACTIONS_BY_ACCOUNT (
        p_account_number IN CHAR,
        p_result OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_result FOR
            SELECT ID, 
                   account_number, 
                   account_number_RECEIVER, 
                   date_of_transaction, 
                   amount, 
                   type_of_transaction, 
                   description_of_transaction
            FROM TRANSACTION
            WHERE account_number = p_account_number 
               OR account_number_RECEIVER = p_account_number;
    END READ_TRANSACTIONS_BY_ACCOUNT;

END transaction_pkg;
/
