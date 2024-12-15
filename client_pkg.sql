CREATE OR REPLACE PACKAGE client_pkg IS
    -- Deklaracja procedur
    PROCEDURE CREATE_CLIENT (
    p_pesel IN CHAR,
    p_first_name IN VARCHAR2,
    p_last_name IN VARCHAR2,
    p_middle_name IN VARCHAR2,
    p_phone_number IN VARCHAR2,
    p_date_of_birth IN DATE
    );
    PROCEDURE READ_ALL_CLIENTS;
    PROCEDURE READ_CLIENT_BY_PESEL(p_pesel IN CHAR);
    PROCEDURE UPDATE_FIRST_NAME (
    p_value IN CHAR,
    p_pesel IN VARCHAR2
    );
    PROCEDURE UPDATE_LAST_NAME (
    p_pesel IN CHAR,
    p_value IN VARCHAR2
    );
    PROCEDURE UPDATE_MIDDLE_NAME (
    p_pesel IN CHAR,
    p_value IN VARCHAR2
    );
    PROCEDURE UPDATE_PHONE_NUMBER (
    p_pesel IN CHAR,
    p_value IN VARCHAR2
    );
    
END client_pkg;


CREATE OR REPLACE PACKAGE BODY client_pkg IS
    -- Implementacja procedury
    PROCEDURE CREATE_CLIENT (
    p_pesel IN CHAR,
    p_first_name IN VARCHAR2,
    p_last_name IN VARCHAR2,
    p_middle_name IN VARCHAR2,
    p_phone_number IN VARCHAR2,
    p_date_of_birth IN DATE
    ) IS
    BEGIN
    INSERT INTO CLIENT (
        PESEL, first_name, last_name, middle_name, phone_number, date_of_birth
    ) VALUES (
        p_pesel, p_first_name, p_last_name, p_middle_name, p_phone_number, p_date_of_birth
    );
    COMMIT;
    END CREATE_CLIENT;
    
    PROCEDURE READ_ALL_CLIENTS IS
    CURSOR client_cursor IS
        SELECT * FROM CLIENT;
    client_record client_cursor%ROWTYPE;
    BEGIN
    OPEN client_cursor;

    FETCH client_cursor INTO client_record;
    IF client_cursor%NOTFOUND THEN
        DBMS_OUTPUT.PUT_LINE('Nie znaleziono klientów.');
        CLOSE client_cursor;
        RETURN;
    END IF;

    LOOP
        DBMS_OUTPUT.PUT_LINE(
            client_record.PESEL || ' ' ||
            client_record.first_name || ' ' ||
            client_record.last_name || ' ' ||
            client_record.middle_name || ' ' ||
            client_record.phone_number || ' ' ||
            TO_CHAR(client_record.date_of_birth, 'YYYY-MM-DD')
        );
        FETCH client_cursor INTO client_record;
        EXIT WHEN client_cursor%NOTFOUND;
    END LOOP;

    CLOSE client_cursor;
    END READ_ALL_CLIENTS;
    
    PROCEDURE READ_CLIENT_BY_PESEL(p_pesel IN CHAR) IS
    CURSOR client_cursor IS
        SELECT PESEL, 
               first_name, 
               last_name, 
               middle_name, 
               phone_number, 
               date_of_birth
        FROM CLIENT
        WHERE PESEL = p_pesel;
    client_record client_cursor%ROWTYPE;
    BEGIN
    OPEN client_cursor;
    
    FETCH client_cursor INTO client_record;
    
    IF client_cursor%FOUND THEN
        DBMS_OUTPUT.PUT_LINE(
            client_record.PESEL || ' ' ||
            client_record.first_name || ' ' ||
            client_record.last_name || ' ' ||
            client_record.middle_name || ' ' ||
            client_record.phone_number || ' ' ||
            TO_CHAR(client_record.date_of_birth, 'YYYY-MM-DD')
        );
    ELSE
        DBMS_OUTPUT.PUT_LINE('Nie istnieje klient z peselem: ' || p_pesel);
    END IF;

    CLOSE client_cursor;
    END READ_CLIENT_BY_PESEL;
    
    PROCEDURE UPDATE_FIRST_NAME (
        p_value IN CHAR,
        p_pesel IN VARCHAR2
    ) IS
    BEGIN
        UPDATE CLIENT
        SET first_name = p_value
        WHERE PESEL = p_pesel;
        COMMIT;
    END UPDATE_FIRST_NAME;
    
    PROCEDURE UPDATE_LAST_NAME (
    p_pesel IN CHAR,
    p_value IN VARCHAR2
) IS
BEGIN
    UPDATE CLIENT
    SET last_name = p_value
    WHERE PESEL = p_pesel;
    COMMIT;
END UPDATE_LAST_NAME;

PROCEDURE UPDATE_MIDDLE_NAME (
    p_pesel IN CHAR,
    p_value IN VARCHAR2
) IS
BEGIN
    UPDATE CLIENT
    SET middle_name = p_value
    WHERE PESEL = p_pesel;
    COMMIT;
END UPDATE_MIDDLE_NAME;

PROCEDURE UPDATE_PHONE_NUMBER (
    p_pesel IN CHAR,
    p_value IN VARCHAR2
) IS
BEGIN
    UPDATE CLIENT
    SET phone_number = p_value
    WHERE PESEL = p_pesel;
    COMMIT;
END UPDATE_PHONE_NUMBER ;
 
END client_pkg;
