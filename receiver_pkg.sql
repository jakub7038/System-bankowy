CREATE OR REPLACE PACKAGE receiver_pkg IS

    PROCEDURE CREATE_RECEIVER(
        p_account_number_receiver IN CHAR,
        p_account_number_tied IN CHAR,
        p_description IN VARCHAR2,
        p_first_name IN VARCHAR2,
        p_last_name IN VARCHAR2
    );
    PROCEDURE READ_ALL_RECEIVERS;
    PROCEDURE READ_RECEIVER_BY_TIED_ACCOUNT(
    p_account_number_tied IN CHAR
    ); 

    PROCEDURE DELETE_RECEIVER(
    p_account_number_receiver IN RECEIVER.account_number_RECEIVER%TYPE,
    p_account_number_tied     IN RECEIVER.account_number_TIED%TYPE
    );

END receiver_pkg;


CREATE OR REPLACE PACKAGE BODY receiver_pkg IS

    PROCEDURE CREATE_RECEIVER(
    p_account_number_receiver IN CHAR,
    p_account_number_tied IN CHAR,
    p_description IN VARCHAR2,
    p_first_name IN VARCHAR2,
    p_last_name IN VARCHAR2
    ) IS
    BEGIN
        INSERT INTO RECEIVER (
            account_number_RECEIVER,
            account_number_TIED, description,
            first_name,
            last_name
        ) VALUES (
            p_account_number_receiver,
            p_account_number_tied,
            p_description,
            p_first_name,
            p_last_name
        );
        COMMIT;
    END CREATE_RECEIVER;
    
    PROCEDURE READ_ALL_RECEIVERS IS
    CURSOR receiver_cursor IS
        SELECT account_number_RECEIVER, 
               account_number_TIED, 
               description, 
               first_name, 
               last_name 
        FROM RECEIVER;
    receiver_record receiver_cursor%ROWTYPE;
    BEGIN
        OPEN receiver_cursor;
    
        FETCH receiver_cursor INTO receiver_record;
        IF receiver_cursor%NOTFOUND THEN
            DBMS_OUTPUT.PUT_LINE('Nie znaleziono odbiorc�w.');
            CLOSE receiver_cursor;
            RETURN;
        END IF;
        
        LOOP
            DBMS_OUTPUT.PUT_LINE(
                receiver_record.account_number_RECEIVER || ' ' ||
                receiver_record.account_number_TIED || ' ' ||
                receiver_record.description || ' ' ||
                receiver_record.first_name || ' ' ||
                receiver_record.last_name
            );
            FETCH receiver_cursor INTO receiver_record;
            EXIT WHEN receiver_cursor%NOTFOUND;
        END LOOP;
    
        CLOSE receiver_cursor;
    END READ_ALL_RECEIVERS;
    
    
    PROCEDURE READ_RECEIVER_BY_TIED_ACCOUNT(
    p_account_number_tied IN CHAR
    ) IS
        CURSOR receiver_cursor IS
            SELECT account_number_RECEIVER, 
                   account_number_TIED, 
                   description, 
                   first_name, 
                   last_name
            FROM RECEIVER
            WHERE account_number_TIED = p_account_number_tied;
        receiver_record receiver_cursor%ROWTYPE;
    BEGIN
        OPEN receiver_cursor;
        
        FETCH receiver_cursor INTO receiver_record;
        IF receiver_cursor%NOTFOUND THEN
            DBMS_OUTPUT.PUT_LINE('Nie znaleziono odbiorc�w powi�zanych z tym kontem.');
            CLOSE receiver_cursor;
            RETURN;
        END IF;
        LOOP
            DBMS_OUTPUT.PUT_LINE(
                receiver_record.account_number_RECEIVER || ' ' ||
                receiver_record.account_number_TIED || ' ' ||
                receiver_record.description || ' ' ||
                receiver_record.first_name || ' ' ||
                receiver_record.last_name
            );
            FETCH receiver_cursor INTO receiver_record;
            EXIT WHEN receiver_cursor%NOTFOUND;
        END LOOP;
    
        CLOSE receiver_cursor;
    END READ_RECEIVER_BY_TIED_ACCOUNT;

    PROCEDURE DELETE_RECEIVER(
        p_account_number_receiver IN RECEIVER.account_number_RECEIVER%TYPE,
        p_account_number_tied     IN RECEIVER.account_number_TIED%TYPE
    ) IS
    BEGIN
        DELETE FROM RECEIVER
        WHERE account_number_RECEIVER = p_account_number_receiver
        AND account_number_TIED = p_account_number_tied;

        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END DELETE_RECEIVER;

END receiver_pkg;