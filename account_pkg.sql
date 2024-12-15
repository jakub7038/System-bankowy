CREATE OR REPLACE PACKAGE account_pkg IS
    PROCEDURE CREATE_ACCOUNT (
    p_account_number IN CHAR,
    p_type_of_account IN VARCHAR2,
    p_balance IN NUMBER,
    p_date_of_creation IN DATE,
    p_status IN VARCHAR2,
    p_login IN VARCHAR2,
    p_password IN VARCHAR2,
    p_pesel IN CHAR
    );
    PROCEDURE CREATE_CLIENT_ACCOUNT(
        p_pesel IN CHAR,
        p_account_number IN CHAR
    );
    PROCEDURE READ_ALL_ACCOUNTS;
    PROCEDURE READ_ACCOUNT_BY_NUMBER (
    p_account_number IN VARCHAR2
    );
    PROCEDURE UPDATE_PASSWORD (
    p_account_number IN CHAR,
    p_old_password IN VARCHAR2,
    p_new_password IN VARCHAR2
    );
    PROCEDURE UPDATE_LOGIN (
    p_account_number IN CHAR,
    p_new_login IN VARCHAR2
    ); 
    PROCEDURE LOGIN (
    p_username IN VARCHAR2,
    p_password IN VARCHAR2
    );

END account_pkg;



CREATE OR REPLACE PACKAGE BODY account_pkg IS

    PROCEDURE CREATE_ACCOUNT (
    p_account_number IN CHAR,
    p_type_of_account IN VARCHAR2,
    p_balance IN NUMBER,
    p_date_of_creation IN DATE,
    p_status IN VARCHAR2,
    p_login IN VARCHAR2,
    p_password IN VARCHAR2,
    p_pesel IN CHAR
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

PROCEDURE READ_ALL_ACCOUNTS IS
    CURSOR account_cursor IS
        SELECT account_number, 
               type_of_account, 
               balance, 
               date_of_creation, 
               status, 
               login
        FROM ACCOUNT;
    account_record account_cursor%ROWTYPE;
BEGIN
    OPEN account_cursor;

    FETCH account_cursor INTO account_record;
    IF account_cursor%NOTFOUND THEN
        DBMS_OUTPUT.PUT_LINE('Nie znaleziono kont.');
        CLOSE account_cursor;
        RETURN;
    END IF;

    LOOP
        DBMS_OUTPUT.PUT_LINE(
            account_record.account_number || ' ' ||
            account_record.type_of_account || ' ' ||
            account_record.balance || ' ' ||
            account_record.date_of_creation || ' ' ||
            account_record.status || ' ' ||
            account_record.login
        );
        FETCH account_cursor INTO account_record;
        EXIT WHEN account_cursor%NOTFOUND;
    END LOOP;

    CLOSE account_cursor;
END READ_ALL_ACCOUNTS;

PROCEDURE READ_ACCOUNT_BY_NUMBER (
    p_account_number IN VARCHAR2
) IS
    CURSOR account_cursor IS
        SELECT account_number,
               type_of_account,
               balance, 
               date_of_creation, 
               status, 
               login
        FROM ACCOUNT
        WHERE account_number = p_account_number;
    account_record account_cursor%ROWTYPE;
BEGIN
    OPEN account_cursor;
    FETCH account_cursor INTO account_record;

    IF account_cursor%FOUND THEN
        DBMS_OUTPUT.PUT_LINE(
            account_record.account_number || ' ' ||
            account_record.type_of_account || ' ' ||
            account_record.balance || ' ' ||
            account_record.date_of_creation || ' ' ||
            account_record.status || ' ' ||
            account_record.login
        );
    ELSE
        DBMS_OUTPUT.PUT_LINE('Nie znaleziono konta o numerze: ' || p_account_number);
    END IF;

    CLOSE account_cursor;
END READ_ACCOUNT_BY_NUMBER;

PROCEDURE UPDATE_PASSWORD (
    p_account_number IN CHAR,
    p_old_password IN VARCHAR2,
    p_new_password IN VARCHAR2
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
        RAISE_APPLICATION_ERROR(-20001, 'Nieprawid�owe stare has�o.');
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
END UPDATE_PASSWORD;


PROCEDURE UPDATE_LOGIN (
    p_account_number IN CHAR,
    p_new_login IN VARCHAR2
) IS
    v_account_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_account_count
    FROM ACCOUNT
    WHERE login = p_new_login;

    IF v_account_count > 0 THEN
        DBMS_OUTPUT.PUT_LINE('B��d: Nowy login jest ju� zaj�ty przez inne konto.');
    ELSE
        UPDATE ACCOUNT
        SET login = p_new_login
        WHERE account_number = p_account_number;
        
        IF SQL%ROWCOUNT > 0 THEN
            COMMIT;
            DBMS_OUTPUT.PUT_LINE('Login zosta� pomy�lnie zaktualizowany.');
        ELSE
            DBMS_OUTPUT.PUT_LINE('B��d: Nie znaleziono konta o podanym numerze.');
        END IF;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Wyst�pi� b��d podczas aktualizacji loginu: ' || SQLERRM);
        ROLLBACK;
END UPDATE_LOGIN;


PROCEDURE LOGIN (
    p_username IN VARCHAR2,
    p_password IN VARCHAR2
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
            DBMS_OUTPUT.PUT_LINE('Niepoprawne has�o lub login');
            RETURN;
    END;

    input_hashed_password := RAWTOHEX(
        DBMS_CRYPTO.HASH(
            UTL_RAW.CAST_TO_RAW(p_password) || stored_salt,
            DBMS_CRYPTO.HASH_SH256
        )
    );

    IF input_hashed_password != stored_hashed_password THEN
        DBMS_OUTPUT.PUT_LINE('Niepoprawne has�o lub login');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Zalogowano');
    END IF;
END LOGIN;

    
END account_pkg;