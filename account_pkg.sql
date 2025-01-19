CREATE OR REPLACE PACKAGE account_pkg IS

    TYPE account_obj IS RECORD (
        account_number       CHAR(26),
        type_of_account      VARCHAR2(50),
        balance              NUMBER,
        date_of_creation     DATE,
        status               VARCHAR2(50),
        login                VARCHAR2(50)
    );

    TYPE account_tbl IS TABLE OF account_obj;

    PROCEDURE create_account (
        p_type_of_account   IN VARCHAR2,
        p_balance           IN NUMBER,
        p_status            IN VARCHAR2,
        p_login             IN VARCHAR2,
        p_password          IN VARCHAR2,
        p_pesel             IN CHAR,
        p_result            OUT VARCHAR2
    );

    PROCEDURE create_client_account(
        p_pesel            IN CHAR,
        p_account_number   IN CHAR
    );

    FUNCTION read_all_accounts_func
    RETURN account_tbl PIPELINED;

    FUNCTION read_account_by_number_func(
        p_account_number   IN CHAR
    ) RETURN account_tbl PIPELINED;

    FUNCTION read_accounts_by_client(
        p_pesel IN CHAR
    ) RETURN account_tbl PIPELINED;

    PROCEDURE update_password (
        p_account_number   IN CHAR,
        p_old_password     IN VARCHAR2,
        p_new_password     IN VARCHAR2,
        p_result           OUT VARCHAR2
    );

    PROCEDURE update_login (
        p_account_number   IN CHAR,
        p_new_login        IN VARCHAR2,
        p_result           OUT VARCHAR2
    );

    PROCEDURE DELETE_ACCOUNT (
        p_account_number IN CHAR,
        p_result OUT VARCHAR2
    );

    PROCEDURE login (
        p_username         IN VARCHAR2,
        p_password         IN VARCHAR2,
        p_login_status     OUT VARCHAR2
    );

    PROCEDURE admin_update_password (
        p_account_number   IN CHAR,
        p_new_password     IN VARCHAR2,
        p_result           OUT VARCHAR2
    );

    PROCEDURE admin_update_login (
        p_account_number IN CHAR,
        p_new_login      IN VARCHAR2,
        p_result         OUT VARCHAR2
    );

END account_pkg;
/

create or replace PACKAGE BODY account_pkg IS

    PROCEDURE create_account (
        p_type_of_account   IN VARCHAR2,
        p_balance           IN NUMBER,
        p_status            IN VARCHAR2,
        p_login             IN VARCHAR2,
        p_password          IN VARCHAR2,
        p_pesel             IN CHAR,
        p_result            OUT VARCHAR2
    ) IS
        v_account_number   CHAR(26);
        hashed_password    VARCHAR2(64);
        salt               RAW(32);
    BEGIN
        v_account_number := LPAD(TO_CHAR(DBMS_RANDOM.VALUE(1, 999999999999999999)), 14, '0') ||
                            TO_CHAR(SYSDATE, 'YYYYMMDDHH24MI');

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
            v_account_number,
            p_type_of_account,
            p_balance,
            SYSDATE,
            p_status,
            p_login,
            hashed_password,
            salt
        );

        create_client_account(p_pesel => p_pesel, p_account_number => v_account_number);

        COMMIT;
        p_result := 'Account created successfully.';
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_result := 'Error creating account: ' || SQLERRM;
    END create_account;

    PROCEDURE create_client_account(
        p_pesel            IN CHAR,
        p_account_number   IN CHAR
    ) IS
    BEGIN
        INSERT INTO CLIENT_ACCOUNT (PESEL, account_number)
        VALUES (p_pesel, p_account_number);

        COMMIT;
    END create_client_account;

    FUNCTION read_all_accounts_func
    RETURN account_tbl PIPELINED
    IS
    BEGIN
        FOR rec IN (
            SELECT account_number, 
                   type_of_account, 
                   balance, 
                   date_of_creation, 
                   status, 
                   login
            FROM ACCOUNT
        ) LOOP
            PIPE ROW(account_obj(
                rec.account_number, 
                rec.type_of_account, 
                rec.balance, 
                rec.date_of_creation, 
                rec.status, 
                rec.login
            ));
        END LOOP;
        RETURN;
    END read_all_accounts_func;

    FUNCTION read_account_by_number_func(
        p_account_number   IN CHAR
    ) RETURN account_tbl PIPELINED
    IS
    BEGIN
        FOR rec IN (
            SELECT account_number, 
                   type_of_account, 
                   balance, 
                   date_of_creation, 
                   status, 
                   login
            FROM ACCOUNT
            WHERE account_number = p_account_number
        ) LOOP
            PIPE ROW(account_obj(
                rec.account_number, 
                rec.type_of_account, 
                rec.balance, 
                rec.date_of_creation, 
                rec.status, 
                rec.login
            ));
        END LOOP;
        RETURN;
    END read_account_by_number_func;

    FUNCTION read_accounts_by_client(
        p_pesel IN CHAR
    ) RETURN account_tbl PIPELINED
    IS
    BEGIN
        FOR rec IN (
            SELECT a.account_number, 
                   a.type_of_account, 
                   a.balance, 
                   a.date_of_creation, 
                   a.status, 
                   a.login
            FROM ACCOUNT a
            JOIN CLIENT_ACCOUNT ca ON a.account_number = ca.account_number
            WHERE ca.pesel = p_pesel
        ) LOOP
            PIPE ROW(account_obj(
                rec.account_number, 
                rec.type_of_account, 
                rec.balance, 
                rec.date_of_creation, 
                rec.status, 
                rec.login
            ));
        END LOOP;
        RETURN;
    END read_accounts_by_client;

    PROCEDURE update_password (
        p_account_number   IN CHAR,
        p_old_password     IN VARCHAR2,
        p_new_password     IN VARCHAR2,
        p_result           OUT VARCHAR2
    ) IS
        hashed_old_password VARCHAR2(64);
        hashed_new_password VARCHAR2(64);
        stored_password     VARCHAR2(64);
        salt                RAW(32);
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
        SET password = hashed_new_password
        WHERE account_number = p_account_number;

        COMMIT;
        p_result := 'Password updated successfully.';
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_result := 'Error updating password: ' || SQLERRM;
    END update_password;

PROCEDURE admin_update_password (
    p_account_number   IN CHAR,
    p_new_password     IN VARCHAR2,
    p_result           OUT VARCHAR2
) IS
    hashed_new_password VARCHAR2(64);
    stored_salt         RAW(32);
BEGIN
    SELECT salt
    INTO stored_salt
    FROM ACCOUNT
    WHERE account_number = p_account_number;

    hashed_new_password := RAWTOHEX(
        DBMS_CRYPTO.HASH(
            UTL_RAW.CAST_TO_RAW(p_new_password) || stored_salt,
            DBMS_CRYPTO.HASH_SH256
        )
    );

    UPDATE ACCOUNT
    SET password = hashed_new_password
    WHERE account_number = p_account_number;

    IF SQL%ROWCOUNT > 0 THEN
        COMMIT;
        p_result := 'Password updated successfully by admin.';
    ELSE
        p_result := 'Error: Account not found.';
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_result := 'Error updating password: ' || SQLERRM;
END admin_update_password;

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

PROCEDURE admin_update_login (
    p_account_number IN CHAR,
    p_new_login      IN VARCHAR2,
    p_result         OUT VARCHAR2
) IS
BEGIN
    UPDATE ACCOUNT
    SET login = p_new_login
    WHERE account_number = p_account_number;

    IF SQL%ROWCOUNT > 0 THEN
        COMMIT;
        p_result := 'Login updated successfully by admin.';
    ELSE
        p_result := 'Error: Account not found.';
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        p_result := 'Error updating login: ' || SQLERRM;
END admin_update_login;

    PROCEDURE DELETE_ACCOUNT (
    p_account_number IN CHAR,
    p_result OUT VARCHAR2
) IS
BEGIN
    DELETE FROM ACCOUNT WHERE account_number = p_account_number;

    IF SQL%ROWCOUNT > 0 THEN
        COMMIT;
        p_result := 'Account and associated cards deleted successfully.';
    ELSE
        p_result := 'Account not found.';
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_result := 'Error: ' || SQLERRM;
END DELETE_ACCOUNT;

    PROCEDURE LOGIN (
    p_username IN VARCHAR2,
    p_password IN VARCHAR2,
    p_login_status OUT VARCHAR2
) IS
    stored_hashed_password VARCHAR2(64);
    stored_salt RAW(32);
    input_hashed_password VARCHAR2(64);
    account_number NUMBER;
BEGIN
    BEGIN
        SELECT password, salt, account_number
        INTO stored_hashed_password, stored_salt, account_number
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
        p_login_status := 'Login successful, Account Number: ' || account_number;
    END IF;
END LOGIN;

END account_pkg;
/