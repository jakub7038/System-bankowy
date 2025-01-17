CREATE OR REPLACE PACKAGE client_account_pkg IS

    PROCEDURE ADD_CLIENT_TO_ACCOUNT (
        p_pesel IN CHAR,
        p_account_number IN CHAR,
        p_result OUT VARCHAR2
    );

    PROCEDURE DELETE_CLIENT_FROM_ACCOUNT (
        p_pesel IN CHAR,
        p_account_number IN CHAR,
        p_result OUT VARCHAR2
    );

END client_account_pkg;
/

CREATE OR REPLACE PACKAGE BODY client_account_pkg IS

    PROCEDURE ADD_CLIENT_TO_ACCOUNT (
        p_pesel IN CHAR,
        p_account_number IN CHAR,
        p_result OUT VARCHAR2
    ) IS
        v_exists NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_exists
        FROM CLIENT_ACCOUNT
        WHERE pesel = p_pesel AND account_number = p_account_number;

        IF v_exists > 0 THEN
            p_result := 'Konta są juz polaczone.';
            RETURN;
        END IF;

        INSERT INTO CLIENT_ACCOUNT (pesel, account_number)
        VALUES (p_pesel, p_account_number);

        COMMIT;
        p_result := 'dodano.';
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_result := 'problem: ' || SQLERRM;
    END ADD_CLIENT_TO_ACCOUNT;

    PROCEDURE DELETE_CLIENT_FROM_ACCOUNT (
        p_pesel IN CHAR,
        p_account_number IN CHAR,
        p_result OUT VARCHAR2
    ) IS
        v_account_count NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_account_count
        FROM CLIENT_ACCOUNT
        WHERE account_number = p_account_number;

        IF v_account_count < 2 THEN
            p_result := 'Nie mozesz usunac bo tylko 1 zostalo.';
            RETURN;
        END IF;

        DELETE FROM CLIENT_ACCOUNT
        WHERE pesel = p_pesel AND account_number = p_account_number;

        IF SQL%ROWCOUNT > 0 THEN
            COMMIT;
            p_result := 'usunieto.';
        ELSE
            p_result := 'problem.';
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_result := 'problem: ' || SQLERRM;
    END DELETE_CLIENT_FROM_ACCOUNT;

END client_account_pkg;
/