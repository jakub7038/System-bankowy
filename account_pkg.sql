CREATE OR REPLACE PACKAGE account_pkg IS
    PROCEDURE CREATE_ACCOUNT (
        p_account_number IN CHAR,
        p_type_of_account IN VARCHAR2,
        p_balance IN NUMBER,
        p_date_of_creation IN DATE,
        p_status IN VARCHAR2,
        p_login IN VARCHAR2,
        p_password IN VARCHAR2,
        p_pesel IN CHAR,
        p_result OUT VARCHAR2
    );

    PROCEDURE CREATE_CLIENT_ACCOUNT(
        p_pesel IN CHAR,
        p_account_number IN CHAR
    );

    PROCEDURE READ_ALL_ACCOUNTS (
        p_accounts OUT SYS_REFCURSOR
    );

    PROCEDURE READ_ACCOUNT_BY_NUMBER (
        p_account_number IN VARCHAR2,
        p_account OUT SYS_REFCURSOR
    );

    PROCEDURE UPDATE_PASSWORD (
        p_account_number IN CHAR,
        p_old_password IN VARCHAR2,
        p_new_password IN VARCHAR2,
        p_result OUT VARCHAR2
    );

    PROCEDURE UPDATE_LOGIN (
        p_account_number IN CHAR,
        p_new_login IN VARCHAR2,
        p_result OUT VARCHAR2
    );

    PROCEDURE LOGIN (
        p_username IN VARCHAR2,
        p_password IN VARCHAR2,
        p_login_status OUT VARCHAR2
    );
END account_pkg;
/

CREATE OR REPLACE PACKAGE BODY account_pkg IS

    PROCEDURE CREATE_ACCOUNT (
        p_account_number IN CHAR,
        p_type_of_account IN VARCHAR2,
        p_balance IN NUMBER,
        p_date_of_creation IN DATE,
        p_status IN VARCHAR2,
        p_login IN VARCHAR2,
        p_password IN VARCHAR2,
        p_pesel IN CHAR,
        p_result OUT VARCHAR2
    ) IS
        hashed_password VARCHAR2(64);
        salt RAW(32);
    BEGIN
        salt := DBMS_CRYPTO.RANDOMBYTES(16);
        hashed_password := RAWTOHEX(
            DBMS_CRYPTO.HASH(
                UTL_RAW.CAST_TO_RAW(p_password) || salt,
                DBMS_CRYPTO.HASH_SH256
            )
        );

        INSERT INTO ACCOUNT (
            account_number,
            type_of_account,
            balance,
            date_of_creation,
            status,
            login,
            password,
            salt
        ) VALUES (
            p_account_number,
            p_type_of_account,
            p_balance,
            p_date_of_creation,
            p_status,
            p_login,
            hashed_password,
            salt
        );

        CREATE_CLIENT_ACCOUNT(p_pesel => p_pesel, p_account_number => p_account_number);

        COMMIT;
        p_result := 'Account created successfully.';
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_result := 'Error creating account: ' || SQLERRM;
    END CREATE_ACCOUNT;

    PROCEDURE CREATE_CLIENT_ACCOUNT(
        p_pesel IN CHAR,
        p_account_number IN CHAR
    ) IS
    BEGIN
        INSERT INTO CLIENT_ACCOUNT (PESEL, account_number)
        VALUES (p_pesel, p_account_number);

        COMMIT;
    END CREATE_CLIENT_ACCOUNT;

    PROCEDURE READ_ALL_ACCOUNTS (
        p_accounts OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_accounts FOR
            SELECT account_number, 
                   type_of_account, 
                   balance, 
                   date_of_creation, 
                   status, 
                   login
            FROM ACCOUNT;
    END READ_ALL_ACCOUNTS;

    PROCEDURE READ_ACCOUNT_BY_NUMBER (
        p_account_number IN VARCHAR2,
        p_account OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_account FOR
            SELECT account_number,
                   type_of_account,
                   balance, 
                   date_of_creation, 
                   status, 
                   login
            FROM ACCOUNT
            WHERE account_number = p_account_number;
    END READ_ACCOUNT_BY_NUMBER;

    PROCEDURE UPDATE_PASSWORD (
        p_account_number IN CHAR,
        p_old_password IN VARCHAR2,
        p_new_password IN VARCHAR2,
        p_result OUT VARCHAR2
    ) IS
        hashed_old_password VARCHAR2(64);
        hashed_new_password VARCHAR2(64);
        stored_password VARCHAR2(64);
        salt RAW(32);
    BEGIN
        SELECT salt, password
        INTO salt, stored_password
        FROM ACCOUNT
        WHERE account_number = p_account_number;

        hashed_old_password := RAWTOHEX(
            DBMS_CRYPTO.HASH(
                UTL_RAW.CAST_TO_RAW(p_old_password) || salt,
                DBMS_CRYPTO.HASH_SH256
            )
        );

        IF hashed_old_password != stored_password THEN
            p_result := 'Incorrect old password.';
            RETURN;
        END IF;

        hashed_new_password := RAWTOHEX(
            DBMS_CRYPTO.HASH(
                UTL_RAW.CAST_TO_RAW(p_new_password) || salt,
                DBMS_CRYPTO.HASH_SH256
            )
        );

        UPDATE ACCOUNT
        SET 
            password = hashed_new_password
        WHERE account_number = p_account_number;

        COMMIT;
        p_result := 'Password updated successfully.';
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_result := 'Error updating password: ' || SQLERRM;
    END UPDATE_PASSWORD;

    PROCEDURE UPDATE_LOGIN (
        p_account_number IN CHAR,
        p_new_login IN VARCHAR2,
        p_result OUT VARCHAR2
    ) IS
        v_account_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_account_count
        FROM ACCOUNT
        WHERE login = p_new_login;

        IF v_account_count > 0 THEN
            p_result := 'Error: New login is already taken.';
        ELSE
            UPDATE ACCOUNT
            SET login = p_new_login
            WHERE account_number = p_account_number;
            
            IF SQL%ROWCOUNT > 0 THEN
                COMMIT;
                p_result := 'Login updated successfully.';
            ELSE
                p_result := 'Error: Account not found.';
            END IF;
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            p_result := 'Error updating login: ' || SQLERRM;
    END UPDATE_LOGIN;

    PROCEDURE LOGIN (
        p_username IN VARCHAR2,
        p_password IN VARCHAR2,
        p_login_status OUT VARCHAR2
    ) IS
        stored_hashed_password VARCHAR2(64);
        stored_salt RAW(32);
        input_hashed_password VARCHAR2(64);
    BEGIN
        BEGIN
            SELECT password, salt
            INTO stored_hashed_password, stored_salt
            FROM ACCOUNT
            WHERE login = p_username;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                p_login_status := 'Invalid username or password';
                RETURN;
        END;

        input_hashed_password := RAWTOHEX(
            DBMS_CRYPTO.HASH(
                UTL_RAW.CAST_TO_RAW(p_password) || stored_salt,
                DBMS_CRYPTO.HASH_SH256
            )
        );

        IF input_hashed_password != stored_hashed_password THEN
            p_login_status := 'Invalid username or password';
        ELSE
            p_login_status := 'Login successful';
        END IF;
    END LOGIN;

END account_pkg;
/