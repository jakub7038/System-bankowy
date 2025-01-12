CREATE OR REPLACE PACKAGE card_pkg IS

    TYPE card_type IS RECORD (
        card_number               VARCHAR2(16),
        date_of_expiration        DATE,
        daily_limit               NUMBER,
        single_payment_limit      NUMBER,
        CVV                       VARCHAR2(3),
        is_active                 NUMBER,
        account_number            VARCHAR2(26),
        PIN                       VARCHAR2(4)
    );

    TYPE card_table IS TABLE OF card_type;

    PROCEDURE CREATE_CARD (
        p_card_number IN VARCHAR2,
        p_date_of_expiration IN DATE,
        p_daily_limit IN NUMBER,
        p_single_payment_limit IN NUMBER,
        p_CVV IN VARCHAR2,
        p_is_active IN NUMBER,
        p_account_number IN VARCHAR2,
        p_PIN IN VARCHAR2,
        p_result OUT VARCHAR2
    );

    FUNCTION READ_ALL_CARDS_FUNC RETURN card_table PIPELINED;

    FUNCTION READ_CARD_BY_NUMBER (p_card_number IN VARCHAR2) RETURN card_table PIPELINED;

    FUNCTION READ_CARD_BY_ACCOUNT_NUMBER (p_account_number IN VARCHAR2) RETURN card_table PIPELINED;

    PROCEDURE UPDATE_CARD_LIMITS (
        p_card_number IN VARCHAR2,
        p_daily_limit IN NUMBER,
        p_single_payment_limit IN NUMBER,
        p_result OUT VARCHAR2
    );

    PROCEDURE UPDATE_CARD_STATUS (
        p_card_number IN VARCHAR2,
        p_is_active IN NUMBER,
        p_result OUT VARCHAR2
    );

    PROCEDURE UPDATE_CARD_PIN (
        p_card_number IN VARCHAR2,
        p_PIN IN VARCHAR2,
        p_result OUT VARCHAR2
    );

END card_pkg;
/

CREATE OR REPLACE PACKAGE BODY card_pkg IS

    PROCEDURE CREATE_CARD (
        p_card_number IN VARCHAR2,
        p_date_of_expiration IN DATE,
        p_daily_limit IN NUMBER,
        p_single_payment_limit IN NUMBER,
        p_CVV IN VARCHAR2,
        p_is_active IN NUMBER,
        p_account_number IN VARCHAR2,
        p_PIN IN VARCHAR2,
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

    FUNCTION READ_ALL_CARDS_FUNC RETURN card_table PIPELINED IS
    BEGIN
        FOR r IN (SELECT card_number, date_of_expiration, daily_limit, 
                          single_payment_limit, CVV, is_active, 
                          account_number, PIN
                   FROM CARD) 
        LOOP
            PIPE ROW (card_type(r.card_number, r.date_of_expiration, r.daily_limit,
                                r.single_payment_limit, r.CVV, r.is_active,
                                r.account_number, r.PIN));
        END LOOP;
        RETURN;
    END READ_ALL_CARDS_FUNC;

    FUNCTION READ_CARD_BY_NUMBER (p_card_number IN VARCHAR2) RETURN card_table PIPELINED IS
    BEGIN
        FOR r IN (SELECT card_number, date_of_expiration, daily_limit, 
                          single_payment_limit, CVV, is_active, 
                          account_number, PIN
                   FROM CARD
                   WHERE card_number = p_card_number) 
        LOOP
            PIPE ROW (card_type(r.card_number, r.date_of_expiration, r.daily_limit,
                                r.single_payment_limit, r.CVV, r.is_active,
                                r.account_number, r.PIN));
        END LOOP;
        RETURN;
    END READ_CARD_BY_NUMBER;

    FUNCTION READ_CARD_BY_ACCOUNT_NUMBER (p_account_number IN VARCHAR2) RETURN card_table PIPELINED IS
BEGIN
    FOR r IN (SELECT card_number, date_of_expiration, daily_limit, 
                      single_payment_limit, CVV, is_active, 
                      account_number, PIN
                FROM CARD
                WHERE account_number = TO_NUMBER(p_account_number))
    LOOP
        PIPE ROW (card_type(r.card_number, r.date_of_expiration, r.daily_limit,
                            r.single_payment_limit, r.CVV, r.is_active,
                            r.account_number, r.PIN));
    END LOOP;
    RETURN;
END READ_CARD_BY_ACCOUNT_NUMBER;

    PROCEDURE UPDATE_CARD_LIMITS (
        p_card_number IN VARCHAR2,
        p_daily_limit IN NUMBER,
        p_single_payment_limit IN NUMBER,
        p_result OUT VARCHAR2
    ) IS
    BEGIN
        UPDATE CARD
        SET daily_limit = p_daily_limit,
            single_payment_limit = p_single_payment_limit
        WHERE card_number = p_card_number;

        p_result := 'Card limits updated successfully.';
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            p_result := 'Error updating card limits: ' || SQLERRM;
    END UPDATE_CARD_LIMITS;

    PROCEDURE UPDATE_CARD_STATUS (
        p_card_number IN VARCHAR2,
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
        p_card_number IN VARCHAR2,
        p_PIN IN VARCHAR2,
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



