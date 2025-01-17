CREATE OR REPLACE PACKAGE client_pkg IS

    TYPE client_obj IS RECORD (
        pesel        CHAR(11),
        first_name   VARCHAR2(50),
        last_name    VARCHAR2(50),
        middle_name  VARCHAR2(50),
        phone_number VARCHAR2(20),
        date_of_birth DATE
    );

    TYPE client_tbl IS TABLE OF client_obj;

    PROCEDURE CREATE_CLIENT (
        p_pesel IN CHAR,
        p_first_name IN VARCHAR2,
        p_last_name IN VARCHAR2,
        p_middle_name IN VARCHAR2,
        p_phone_number IN VARCHAR2,
        p_date_of_birth IN DATE,
        p_result OUT VARCHAR2
    );

    FUNCTION READ_ALL_CLIENTS RETURN client_tbl PIPELINED;

    FUNCTION READ_CLIENT_BY_PESEL (p_pesel IN CHAR) RETURN client_tbl PIPELINED;

    FUNCTION READ_CLIENTS_BY_ACCOUNT (p_account_number IN CHAR) RETURN client_tbl PIPELINED;

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

    FUNCTION READ_ALL_CLIENTS RETURN client_tbl PIPELINED IS
    BEGIN
        FOR rec IN (
            SELECT PESEL,
                   first_name,
                   last_name,
                   middle_name,
                   phone_number,
                   date_of_birth
            FROM CLIENT
        ) LOOP
            PIPE ROW(client_obj(
                rec.PESEL,
                rec.first_name,
                rec.last_name,
                rec.middle_name,
                rec.phone_number,
                rec.date_of_birth
            ));
        END LOOP;
        RETURN;
    END READ_ALL_CLIENTS;

    FUNCTION READ_CLIENT_BY_PESEL (
        p_pesel IN CHAR
    ) RETURN client_tbl PIPELINED IS
    BEGIN
        FOR rec IN (
            SELECT PESEL,
                   first_name,
                   last_name,
                   middle_name,
                   phone_number,
                   date_of_birth
            FROM CLIENT
            WHERE PESEL = p_pesel
        ) LOOP
            PIPE ROW(client_obj(
                rec.PESEL,
                rec.first_name,
                rec.last_name,
                rec.middle_name,
                rec.phone_number,
                rec.date_of_birth
            ));
        END LOOP;
        RETURN;
    END READ_CLIENT_BY_PESEL;

    FUNCTION READ_CLIENTS_BY_ACCOUNT (
        p_account_number IN CHAR
    ) RETURN client_tbl PIPELINED IS
    BEGIN
        FOR rec IN (
            SELECT c.PESEL,
                   c.first_name,
                   c.last_name,
                   c.middle_name,
                   c.phone_number,
                   c.date_of_birth
            FROM CLIENT c
            INNER JOIN CLIENT_ACCOUNT ca ON c.PESEL = ca.pesel
            WHERE ca.account_number = p_account_number
        ) LOOP
            PIPE ROW(client_obj(
                rec.PESEL,
                rec.first_name,
                rec.last_name,
                rec.middle_name,
                rec.phone_number,
                rec.date_of_birth
            ));
        END LOOP;
        RETURN;
    END READ_CLIENTS_BY_ACCOUNT;

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

