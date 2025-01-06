CREATE OR REPLACE PACKAGE card_pkg IS
    PROCEDURE CREATE_CARD (
        p_card_number IN CHAR,
        p_date_of_expiration IN DATE,
        p_daily_limit IN NUMBER,
        p_single_payment_limit IN NUMBER,
        p_CVV IN CHAR,
        p_is_active IN NUMBER,
        p_account_number IN CHAR,
        p_PIN IN CHAR,
        p_result OUT VARCHAR2
    );

    PROCEDURE READ_ALL_CARDS (
        p_result OUT SYS_REFCURSOR
    );

    PROCEDURE READ_CARD_BY_NUMBER (
        p_card_number IN CHAR,
        p_result OUT SYS_REFCURSOR
    );

    PROCEDURE READ_CARD_BY_ACCOUNT_NUMBER (
        p_account_number IN CHAR,
        p_result OUT SYS_REFCURSOR
    );

    PROCEDURE UPDATE_CARD_LIMITS (
        p_card_number IN CHAR,
        p_daily_limit IN NUMBER,
        p_single_payment_limit IN NUMBER,
        p_result OUT VARCHAR2
    );

    PROCEDURE UPDATE_CARD_STATUS (
        p_card_number IN CHAR,
        p_is_active IN NUMBER,
        p_result OUT VARCHAR2
    );

    PROCEDURE UPDATE_CARD_PIN (
        p_card_number IN CHAR,
        p_PIN IN CHAR,
        p_result OUT VARCHAR2
    );

END card_pkg;
/
CREATE OR REPLACE PACKAGE BODY card_pkg IS

    PROCEDURE CREATE_CARD (
        p_card_number IN CHAR,
        p_date_of_expiration IN DATE,
        p_daily_limit IN NUMBER,
        p_single_payment_limit IN NUMBER,
        p_CVV IN CHAR,
        p_is_active IN NUMBER,
        p_account_number IN CHAR,
        p_PIN IN CHAR,
        p_result OUT VARCHAR2
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
        ) VALUES (
            p_card_number, 
            p_date_of_expiration, 
            p_daily_limit, 
            p_single_payment_limit, 
            p_CVV, 
            p_is_active, 
            p_account_number,
            p_PIN
        );

        p_result := 'Card created successfully.';
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            p_result := 'Error creating card: ' || SQLERRM;
    END CREATE_CARD;

    PROCEDURE READ_ALL_CARDS (
        p_result OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_result FOR
            SELECT card_number, 
                   date_of_expiration, 
                   daily_limit, 
                   single_payment_limit, 
                   CVV, 
                   is_active, 
                   account_number,
                   PIN
            FROM CARD;
    END READ_ALL_CARDS;

    PROCEDURE READ_CARD_BY_NUMBER (
        p_card_number IN CHAR,
        p_result OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_result FOR
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
    END READ_CARD_BY_NUMBER;

    PROCEDURE READ_CARD_BY_ACCOUNT_NUMBER (
        p_account_number IN CHAR,
        p_result OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_result FOR
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
    END READ_CARD_BY_ACCOUNT_NUMBER;

    PROCEDURE UPDATE_CARD_LIMITS (
        p_card_number IN CHAR,
        p_daily_limit IN NUMBER,
        p_single_payment_limit IN NUMBER,
        p_result OUT VARCHAR2
    ) IS
    BEGIN
        UPDATE CARD
        SET 
            daily_limit = p_daily_limit,
            single_payment_limit = p_single_payment_limit
        WHERE card_number = p_card_number;

        p_result := 'Card limits updated successfully.';
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            p_result := 'Error updating card limits: ' || SQLERRM;
    END UPDATE_CARD_LIMITS;

    PROCEDURE UPDATE_CARD_STATUS (
        p_card_number IN CHAR,
        p_is_active IN NUMBER,
        p_result OUT VARCHAR2
    ) IS
    BEGIN
        UPDATE CARD
        SET is_active = p_is_active
        WHERE card_number = p_card_number;

        p_result := 'Card status updated successfully.';
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            p_result := 'Error updating card status: ' || SQLERRM;
    END UPDATE_CARD_STATUS;

    PROCEDURE UPDATE_CARD_PIN (
        p_card_number IN CHAR,
        p_PIN IN CHAR,
        p_result OUT VARCHAR2
    ) IS
    BEGIN
        UPDATE CARD
        SET PIN = p_PIN
        WHERE card_number = p_card_number;

        p_result := 'Card PIN updated successfully.';
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            p_result := 'Error updating card PIN: ' || SQLERRM;
    END UPDATE_CARD_PIN;

END card_pkg;
/
