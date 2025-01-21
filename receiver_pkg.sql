CREATE OR REPLACE PACKAGE receiver_pkg IS

    TYPE receiver_obj IS RECORD (
        account_number_receiver CHAR(26),
        account_number_tied     CHAR(26),
        description             VARCHAR2(255),
        first_name              VARCHAR2(50),
        last_name               VARCHAR2(50)
    );

    TYPE receiver_tbl IS TABLE OF receiver_obj;

    PROCEDURE CREATE_RECEIVER(
        p_account_number_receiver IN CHAR,
        p_account_number_tied IN CHAR,
        p_description IN VARCHAR2,
        p_first_name IN VARCHAR2,
        p_last_name IN VARCHAR2,
        p_result OUT VARCHAR2
    );

    FUNCTION READ_ALL_RECEIVERS_FUNC
    RETURN receiver_tbl PIPELINED;

    FUNCTION READ_RECEIVER_BY_TIED_ACCOUNT_FUNC(
        p_account_number_tied IN CHAR
    ) RETURN receiver_tbl PIPELINED;

    PROCEDURE DELETE_RECEIVER(
        p_account_number_receiver IN CHAR,
        p_account_number_tied     IN CHAR,
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
    IF LENGTH(p_account_number_receiver) < 26 THEN
        p_result := 'Error: Account number receiver too short.';
        RETURN;
    END IF;

    IF LENGTH(p_account_number_tied) < 26 THEN
        p_result := 'Error: Account number tied too short.';
        RETURN;
    END IF;

    IF REGEXP_LIKE(p_account_number_receiver, '[^0-9]') THEN
        p_result := 'Error: only digits.';
        RETURN;
    END IF;

    IF REGEXP_LIKE(p_account_number_tied, '[^0-9]') THEN
        p_result := 'Error: only digits.';
        RETURN;
    END IF;

    IF p_first_name IS NULL OR TRIM(p_first_name) = '' THEN
        p_result := 'Error: First name cannot be empty.';
        RETURN;
    END IF;

    IF p_last_name IS NULL OR TRIM(p_last_name) = '' THEN
        p_result := 'Error: Last name cannot be empty.';
        RETURN;
    END IF;

    IF p_description IS NULL OR TRIM(p_description) = '' THEN
        p_result := 'Error: Description cannot be empty.';
        RETURN;
    END IF;

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
            IF SQLCODE = -1400 THEN
                p_result := 'Error: One or more required fields are missing.';
            ELSE
                p_result := 'Error: ' || SQLERRM;
            END IF;
    END;
END CREATE_RECEIVER;



    FUNCTION READ_ALL_RECEIVERS_FUNC
    RETURN receiver_tbl PIPELINED
    IS
    BEGIN
        FOR rec IN (
            SELECT account_number_RECEIVER, 
                   account_number_TIED, 
                   description, 
                   first_name, 
                   last_name 
            FROM RECEIVER
        ) LOOP
            PIPE ROW(receiver_obj(
                rec.account_number_RECEIVER,
                rec.account_number_TIED,
                rec.description,
                rec.first_name,
                rec.last_name
            ));
        END LOOP;
        RETURN;
    END READ_ALL_RECEIVERS_FUNC;

    FUNCTION READ_RECEIVER_BY_TIED_ACCOUNT_FUNC(
        p_account_number_tied IN CHAR
    ) RETURN receiver_tbl PIPELINED
    IS
    BEGIN
        FOR rec IN (
            SELECT account_number_RECEIVER, 
                   account_number_TIED, 
                   description, 
                   first_name, 
                   last_name
            FROM RECEIVER
            WHERE account_number_TIED = p_account_number_tied
        ) LOOP
            PIPE ROW(receiver_obj(
                rec.account_number_RECEIVER,
                rec.account_number_TIED,
                rec.description,
                rec.first_name,
                rec.last_name
            ));
        END LOOP;
        RETURN;
    END READ_RECEIVER_BY_TIED_ACCOUNT_FUNC;

    PROCEDURE DELETE_RECEIVER(
        p_account_number_receiver IN CHAR,
        p_account_number_tied     IN CHAR,
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