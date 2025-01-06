CREATE OR REPLACE PACKAGE client_pkg IS
    PROCEDURE CREATE_CLIENT (
        p_pesel IN CHAR,
        p_first_name IN VARCHAR2,
        p_last_name IN VARCHAR2,
        p_middle_name IN VARCHAR2,
        p_phone_number IN VARCHAR2,
        p_date_of_birth IN DATE,
        p_result OUT VARCHAR2
    );

    PROCEDURE READ_ALL_CLIENTS (
        p_clients OUT SYS_REFCURSOR
    );

    PROCEDURE READ_CLIENT_BY_PESEL (
        p_pesel IN CHAR,
        p_client OUT SYS_REFCURSOR
    );

    PROCEDURE UPDATE_FIRST_NAME (
        p_pesel IN CHAR,
        p_value IN VARCHAR2,
        p_result OUT VARCHAR2
    );

    PROCEDURE UPDATE_LAST_NAME (
        p_pesel IN CHAR,
        p_value IN VARCHAR2,
        p_result OUT VARCHAR2
    );

    PROCEDURE UPDATE_MIDDLE_NAME (
        p_pesel IN CHAR,
        p_value IN VARCHAR2,
        p_result OUT VARCHAR2
    );

    PROCEDURE UPDATE_PHONE_NUMBER (
        p_pesel IN CHAR,
        p_value IN VARCHAR2,
        p_result OUT VARCHAR2
    );

END client_pkg;
/

CREATE OR REPLACE PACKAGE BODY client_pkg IS
    PROCEDURE CREATE_CLIENT (
        p_pesel IN CHAR,
        p_first_name IN VARCHAR2,
        p_last_name IN VARCHAR2,
        p_middle_name IN VARCHAR2,
        p_phone_number IN VARCHAR2,
        p_date_of_birth IN DATE,
        p_result OUT VARCHAR2
    ) IS
    BEGIN
        INSERT INTO CLIENT (
            PESEL, first_name, last_name, middle_name, phone_number, date_of_birth
        ) VALUES (
            p_pesel, p_first_name, p_last_name, p_middle_name, p_phone_number, p_date_of_birth
        );
        COMMIT;
        p_result := 'Client created successfully.';
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_result := 'Error creating client: ' || SQLERRM;
    END CREATE_CLIENT;

    PROCEDURE READ_ALL_CLIENTS (
        p_clients OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_clients FOR
            SELECT PESEL, 
                   first_name, 
                   last_name, 
                   middle_name, 
                   phone_number, 
                   TO_CHAR(date_of_birth, 'YYYY-MM-DD') AS date_of_birth
            FROM CLIENT;
    END READ_ALL_CLIENTS;

    PROCEDURE READ_CLIENT_BY_PESEL (
        p_pesel IN CHAR,
        p_client OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_client FOR
            SELECT PESEL, 
                   first_name, 
                   last_name, 
                   middle_name, 
                   phone_number, 
                   TO_CHAR(date_of_birth, 'YYYY-MM-DD') AS date_of_birth
            FROM CLIENT
            WHERE PESEL = p_pesel;
    END READ_CLIENT_BY_PESEL;

    PROCEDURE UPDATE_FIRST_NAME (
        p_pesel IN CHAR,
        p_value IN VARCHAR2,
        p_result OUT VARCHAR2
    ) IS
    BEGIN
        UPDATE CLIENT
        SET first_name = p_value
        WHERE PESEL = p_pesel;

        IF SQL%ROWCOUNT > 0 THEN
            COMMIT;
            p_result := 'First name updated successfully.';
        ELSE
            p_result := 'Error: Client not found.';
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_result := 'Error updating first name: ' || SQLERRM;
    END UPDATE_FIRST_NAME;

    PROCEDURE UPDATE_LAST_NAME (
        p_pesel IN CHAR,
        p_value IN VARCHAR2,
        p_result OUT VARCHAR2
    ) IS
    BEGIN
        UPDATE CLIENT
        SET last_name = p_value
        WHERE PESEL = p_pesel;

        IF SQL%ROWCOUNT > 0 THEN
            COMMIT;
            p_result := 'Last name updated successfully.';
        ELSE
            p_result := 'Error: Client not found.';
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_result := 'Error updating last name: ' || SQLERRM;
    END UPDATE_LAST_NAME;

    PROCEDURE UPDATE_MIDDLE_NAME (
        p_pesel IN CHAR,
        p_value IN VARCHAR2,
        p_result OUT VARCHAR2
    ) IS
    BEGIN
        UPDATE CLIENT
        SET middle_name = p_value
        WHERE PESEL = p_pesel;

        IF SQL%ROWCOUNT > 0 THEN
            COMMIT;
            p_result := 'Middle name updated successfully.';
        ELSE
            p_result := 'Error: Client not found.';
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_result := 'Error updating middle name: ' || SQLERRM;
    END UPDATE_MIDDLE_NAME;

    PROCEDURE UPDATE_PHONE_NUMBER (
        p_pesel IN CHAR,
        p_value IN VARCHAR2,
        p_result OUT VARCHAR2
    ) IS
    BEGIN
        UPDATE CLIENT
        SET phone_number = p_value
        WHERE PESEL = p_pesel;

        IF SQL%ROWCOUNT > 0 THEN
            COMMIT;
            p_result := 'Phone number updated successfully.';
        ELSE
            p_result := 'Error: Client not found.';
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_result := 'Error updating phone number: ' || SQLERRM;
    END UPDATE_PHONE_NUMBER;

END client_pkg;
/

