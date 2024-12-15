CREATE OR REPLACE PACKAGE transaction_pkg IS

    PROCEDURE CREATE_TRANSACTION (
    p_id IN NUMBER,
    p_account_number IN CHAR,
    p_account_number_receiver IN CHAR,
    p_date_of_transaction IN TIMESTAMP,
    p_amount IN NUMBER,
    p_type_of_transaction IN VARCHAR2,
    p_description_of_transaction IN VARCHAR2
    ); 
    PROCEDURE READ_ALL_TRANSACTIONS;
    PROCEDURE READ_TRANSACTIONS_BY_ACCOUNT(
    p_account_number IN CHAR
    );

END transaction_pkg;


CREATE OR REPLACE PACKAGE BODY  transaction_pkg IS

PROCEDURE CREATE_TRANSACTION (
    p_id IN NUMBER,
    p_account_number IN CHAR,
    p_account_number_receiver IN CHAR,
    p_date_of_transaction IN TIMESTAMP,
    p_amount IN NUMBER,
    p_type_of_transaction IN VARCHAR2,
    p_description_of_transaction IN VARCHAR2
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
END CREATE_TRANSACTION;

PROCEDURE READ_ALL_TRANSACTIONS IS
    CURSOR transaction_cursor IS
        SELECT ID, 
               account_number, 
               account_number_RECEIVER, 
               date_of_transaction, 
               amount, 
               type_of_transaction, 
               description_of_transaction
        FROM TRANSACTION;
    transaction_record transaction_cursor%ROWTYPE;
BEGIN
    OPEN transaction_cursor;

    FETCH transaction_cursor INTO transaction_record;
    IF transaction_cursor%NOTFOUND THEN
        DBMS_OUTPUT.PUT_LINE('Nie znaleziono transakcji.');
        CLOSE transaction_cursor;
        RETURN;
    END IF;

    LOOP
        DBMS_OUTPUT.PUT_LINE(
            transaction_record.ID || ' ' ||
            transaction_record.account_number || ' ' ||
            transaction_record.account_number_RECEIVER || ' ' ||
            TO_CHAR(transaction_record.date_of_transaction, 'YYYY-MM-DD HH24:MI:SS') || ' ' ||
            transaction_record.amount || ' ' ||
            transaction_record.type_of_transaction || ' ' ||
            transaction_record.description_of_transaction
        );
        FETCH transaction_cursor INTO transaction_record;
        EXIT WHEN transaction_cursor%NOTFOUND;
    END LOOP;

    CLOSE transaction_cursor;
END READ_ALL_TRANSACTIONS;

PROCEDURE READ_TRANSACTIONS_BY_ACCOUNT(
    p_account_number IN CHAR
) IS
    CURSOR transaction_cursor IS
        SELECT ID, 
               account_number, 
               account_number_RECEIVER, 
               date_of_transaction, 
               amount, 
               type_of_transaction, 
               description_of_transaction
        FROM TRANSACTION
        WHERE account_number = p_account_number OR account_number_RECEIVER = p_account_number;
    transaction_record transaction_cursor%ROWTYPE;
BEGIN
    OPEN transaction_cursor;

    FETCH transaction_cursor INTO transaction_record;
    IF transaction_cursor%NOTFOUND THEN
        DBMS_OUTPUT.PUT_LINE('Nie znaleziono transakcji dla konta: ' || p_account_number);
        CLOSE transaction_cursor;
        RETURN;
    END IF;

    LOOP
        DBMS_OUTPUT.PUT_LINE(
            transaction_record.ID || ' ' ||
            transaction_record.account_number || ' ' ||
            transaction_record.account_number_RECEIVER || ' ' ||
            TO_CHAR(transaction_record.date_of_transaction, 'YYYY-MM-DD HH24:MI:SS') || ' ' ||
            transaction_record.amount || ' ' ||
            transaction_record.type_of_transaction || ' ' ||
            transaction_record.description_of_transaction
        );
        FETCH transaction_cursor INTO transaction_record;
        EXIT WHEN transaction_cursor%NOTFOUND;
    END LOOP;

    CLOSE transaction_cursor;
END READ_TRANSACTIONS_BY_ACCOUNT;


END transaction_pkg;    