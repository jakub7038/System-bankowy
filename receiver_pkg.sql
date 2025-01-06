CREATE OR REPLACE PACKAGE receiver_pkg IS

    PROCEDURE CREATE_RECEIVER(
        p_account_number_receiver IN CHAR,
        p_account_number_tied IN CHAR,
        p_description IN VARCHAR2,
        p_first_name IN VARCHAR2,
        p_last_name IN VARCHAR2,
        p_result OUT VARCHAR2
    );

    PROCEDURE READ_ALL_RECEIVERS(
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE READ_RECEIVER_BY_TIED_ACCOUNT(
        p_account_number_tied IN CHAR,
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE DELETE_RECEIVER(
        p_account_number_receiver IN RECEIVER.account_number_RECEIVER%TYPE,
        p_account_number_tied     IN RECEIVER.account_number_TIED%TYPE,
        p_result OUT VARCHAR2
    );

END receiver_pkg;
/

CREATE OR REPLACE PACKAGE BODY receiver_pkg IS

    PROCEDURE CREATE_RECEIVER(
        p_account_number_receiver IN CHAR,
        p_account_number_tied IN CHAR,
        p_description IN VARCHAR2,
        p_first_name IN VARCHAR2,
        p_last_name IN VARCHAR2,
        p_result OUT VARCHAR2
    ) IS
    BEGIN
        INSERT INTO RECEIVER (
            account_number_RECEIVER,
            account_number_TIED, 
            description,
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

        p_result := 'Receiver created successfully.';
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_result := 'Error creating receiver: ' || SQLERRM;
    END CREATE_RECEIVER;

    PROCEDURE READ_ALL_RECEIVERS(
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
        SELECT account_number_RECEIVER, 
               account_number_TIED, 
               description, 
               first_name, 
               last_name 
        FROM RECEIVER;
    END READ_ALL_RECEIVERS;

    PROCEDURE READ_RECEIVER_BY_TIED_ACCOUNT(
        p_account_number_tied IN CHAR,
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
        SELECT account_number_RECEIVER, 
               account_number_TIED, 
               description, 
               first_name, 
               last_name
        FROM RECEIVER
        WHERE account_number_TIED = p_account_number_tied;
    END READ_RECEIVER_BY_TIED_ACCOUNT;

    PROCEDURE DELETE_RECEIVER(
        p_account_number_receiver IN RECEIVER.account_number_RECEIVER%TYPE,
        p_account_number_tied     IN RECEIVER.account_number_TIED%TYPE,
        p_result OUT VARCHAR2
    ) IS
    BEGIN
        DELETE FROM RECEIVER
        WHERE account_number_RECEIVER = p_account_number_receiver
        AND account_number_TIED = p_account_number_tied;

        IF SQL%ROWCOUNT > 0 THEN
            p_result := 'Receiver deleted successfully.';
        ELSE
            p_result := 'No receiver found to delete.';
        END IF;

        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_result := 'Error deleting receiver: ' || SQLERRM;
    END DELETE_RECEIVER;

END receiver_pkg;
/
