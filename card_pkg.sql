CREATE OR REPLACE PACKAGE card_pkg IS
    PROCEDURE CREATE_CARD (
        p_card_number IN CHAR,
        p_date_of_expiration IN DATE,
        p_daily_limit IN NUMBER,
        p_single_payment_limit IN NUMBER,
        p_CVV IN CHAR,
        p_is_active IN NUMBER,
        p_account_number IN CHAR,
        p_PIN IN CHAR
    );
    PROCEDURE READ_ALL_CARDS;
    PROCEDURE READ_CARD_BY_NUMBER (
    p_card_number IN CHAR
    );
    PROCEDURE READ_CARD_BY_ACCOUNT_NUMBER (
    p_account_number IN CHAR
    ); 
    PROCEDURE UPDATE_CARD_LIMITS (
    p_card_number IN CHAR,
    p_daily_limit IN NUMBER,
    p_single_payment_limit IN NUMBER
    ); 
    PROCEDURE UPDATE_CARD_STATUS (
    p_card_number IN CHAR,
    p_is_active IN NUMBER
    );
    PROCEDURE UPDATE_CARD_PIN (
    p_card_number IN CHAR,
    p_PIN IN CHAR
    ); 

END card_pkg;

CREATE OR REPLACE PACKAGE BODY card_pkg IS
    PROCEDURE CREATE_CARD (
        p_card_number IN CHAR,
        p_date_of_expiration IN DATE,
        p_daily_limit IN NUMBER,
        p_single_payment_limit IN NUMBER,
        p_CVV IN CHAR,
        p_is_active IN NUMBER,
        p_account_number IN CHAR,
        p_PIN IN CHAR
    ) IS
    BEGIN
        INSERT INTO CARD (
            card_number, 
            date_of_expiration, 
            daily_limit, 
            single_payment_limit, 
            CVV, 
            is_active, 
            account_number,
            PIN
        ) 
        VALUES (
            p_card_number, 
            p_date_of_expiration, 
            p_daily_limit, 
            p_single_payment_limit, 
            p_CVV, 
            p_is_active, 
            p_account_number,
            p_PIN
        );
        COMMIT;
    END CREATE_CARD;
    
    PROCEDURE READ_ALL_CARDS IS
    CURSOR card_cursor IS
        SELECT card_number, 
               date_of_expiration, 
               daily_limit, 
               single_payment_limit, 
               CVV, 
               is_active, 
               account_number,
               PIN
        FROM CARD;
    card_record card_cursor%ROWTYPE;
    BEGIN
        OPEN card_cursor;
    
        FETCH card_cursor INTO card_record;
        IF card_cursor%NOTFOUND THEN
            DBMS_OUTPUT.PUT_LINE('Nie znaleziono kart.');
            CLOSE card_cursor;
            RETURN;
        END IF;
    
        LOOP
            DBMS_OUTPUT.PUT_LINE(
                card_record.card_number || ' ' ||
                TO_CHAR(card_record.date_of_expiration, 'YYYY-MM-DD') || ' ' ||
                card_record.daily_limit || ' ' ||
                card_record.single_payment_limit || ' ' ||
                card_record.CVV || ' ' ||
                card_record.is_active || ' ' ||
                card_record.account_number || ' ' ||
                card_record.PIN
            );
            FETCH card_cursor INTO card_record;
            EXIT WHEN card_cursor%NOTFOUND;
        END LOOP;
    
        CLOSE card_cursor;
    END READ_ALL_CARDS;
    
    PROCEDURE READ_CARD_BY_NUMBER (
    p_card_number IN CHAR
    ) IS
    CURSOR card_cursor IS
        SELECT card_number, 
               date_of_expiration, 
               daily_limit, 
               single_payment_limit, 
               CVV, 
               is_active, 
               account_number,
               PIN
        FROM CARD
        WHERE card_number = p_card_number;
    card_record card_cursor%ROWTYPE;
    BEGIN
        OPEN card_cursor;
        FETCH card_cursor INTO card_record;
    
        IF card_cursor%FOUND THEN
            DBMS_OUTPUT.PUT_LINE(
                card_record.card_number || ' ' ||
                TO_CHAR(card_record.date_of_expiration, 'YYYY-MM-DD') || ' ' ||
                card_record.daily_limit || ' ' ||
                card_record.single_payment_limit || ' ' ||
                card_record.CVV || ' ' ||
                card_record.is_active || ' ' ||
                card_record.account_number || ' ' ||
                card_record.PIN
            );
        ELSE
            DBMS_OUTPUT.PUT_LINE('Nie znaleziono karty o numerze: ' || p_card_number);
        END IF;
    
        CLOSE card_cursor;
    END READ_CARD_BY_NUMBER;
    
    
    PROCEDURE READ_CARD_BY_ACCOUNT_NUMBER (
    p_account_number IN CHAR
    ) IS
        CURSOR card_cursor IS
            SELECT card_number, 
                   date_of_expiration, 
                   daily_limit, 
                   single_payment_limit, 
                   CVV, 
                   is_active, 
                   account_number,
                   PIN
            FROM CARD
            WHERE account_number = p_account_number;
        card_record card_cursor%ROWTYPE;
    BEGIN
        OPEN card_cursor;
        
        FETCH card_cursor INTO card_record;
        
        IF card_cursor%FOUND THEN
            WHILE card_cursor%FOUND LOOP
                DBMS_OUTPUT.PUT_LINE(
                    card_record.card_number || ' ' ||
                    TO_CHAR(card_record.date_of_expiration, 'YYYY-MM-DD') || ' ' ||
                    card_record.daily_limit || ' ' ||
                    card_record.single_payment_limit || ' ' ||
                    card_record.CVV || ' ' ||
                    card_record.is_active || ' ' ||
                    card_record.account_number || ' ' ||
                    card_record.PIN
                );
                
                FETCH card_cursor INTO card_record;
            END LOOP;
        ELSE
            DBMS_OUTPUT.PUT_LINE('Nie znaleziono karty powi�zanej z tym kontem: ' || p_account_number);
        END IF;
    
        CLOSE card_cursor;
    END READ_CARD_BY_ACCOUNT_NUMBER;
    
    
    PROCEDURE UPDATE_CARD_LIMITS (
    p_card_number IN CHAR,
    p_daily_limit IN NUMBER,
    p_single_payment_limit IN NUMBER
    ) IS
    BEGIN
        UPDATE CARD
        SET 
            daily_limit = p_daily_limit,
            single_payment_limit = p_single_payment_limit
        WHERE card_number = p_card_number;
    
        COMMIT;
    END UPDATE_CARD_LIMITS;
    
    PROCEDURE UPDATE_CARD_STATUS (
    p_card_number IN CHAR,
    p_is_active IN NUMBER
    ) IS
    BEGIN
        UPDATE CARD
        SET is_active = p_is_active
        WHERE card_number = p_card_number;
    
        COMMIT;
    END UPDATE_CARD_STATUS;
    
     PROCEDURE UPDATE_CARD_PIN (
    p_card_number IN CHAR,
    p_PIN IN CHAR
    ) IS
    BEGIN
        UPDATE CARD
        SET PIN = p_PIN
        WHERE card_number = p_card_number;
    
        COMMIT;
    END UPDATE_CARD_PIN;

END card_pkg;