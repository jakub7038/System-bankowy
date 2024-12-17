CREATE OR REPLACE PACKAGE ini_data IS
    PROCEDURE CREATE_EXAMPLE_ENTITIES;
    PROCEDURE READ_ALL_DATA;
END ini_data;

CREATE OR REPLACE PACKAGE BODY ini_data IS

    PROCEDURE CREATE_EXAMPLE_ENTITIES AS
    BEGIN
        client_pkg.CREATE_CLIENT(
            p_pesel => '12345678920',
            p_first_name => 'John',
            p_last_name => 'Pork',
            p_middle_name => 'Bob',
            p_phone_number => '123456789',
            p_date_of_birth => TO_DATE('1990-01-01', 'YYYY-MM-DD')
        );

        client_pkg.CREATE_CLIENT(
            p_pesel => '12345678921',
            p_first_name => 'Jan',
            p_last_name => 'Kowalski',
            p_middle_name => 'Michał',
            p_phone_number => '123456789',
            p_date_of_birth => TO_DATE('1985-03-10', 'YYYY-MM-DD')
        );

        client_pkg.CREATE_CLIENT(
            p_pesel => '98765432101',
            p_first_name => 'Anna',
            p_last_name => 'Nowak',
            p_middle_name => 'Zofia',
            p_phone_number => '987654321',
            p_date_of_birth => TO_DATE('1992-07-25', 'YYYY-MM-DD')
        );

        -- Create accounts
        account_pkg.CREATE_ACCOUNT(
            p_account_number => '1234567890123461',
            p_type_of_account => 'Savings',
            p_balance => 1000000,
            p_date_of_creation => SYSDATE,
            p_status => 'Active',
            p_login => 'user12346',
            p_password => 'password123',
            p_pesel => '12345678920'
        );

        account_pkg.CREATE_ACCOUNT(
            p_account_number => '1234567890123470',
            p_type_of_account => 'Checking',
            p_balance => 200000,
            p_date_of_creation => SYSDATE,
            p_status => 'Active',
            p_login => 'jan_kowalski_125',
            p_password => 'password456',
            p_pesel => '12345678920'
        );

        account_pkg.CREATE_ACCOUNT(
            p_account_number => '2345678901234568',
            p_type_of_account => 'Current',
            p_balance => 500000,
            p_date_of_creation => SYSDATE,
            p_status => 'Active',
            p_login => 'anna_nowak_125',
            p_password => 'anna_password',
            p_pesel => '98765432101'
        );

        account_pkg.CREATE_ACCOUNT(
            p_account_number => '2345678901234578',
            p_type_of_account => 'Savings',
            p_balance => 1500000,
            p_date_of_creation => SYSDATE,
            p_status => 'Active',
            p_login => 'anna_nowak_126',
            p_password => 'anna_password123',
            p_pesel => '98765432101'
        );

        card_pkg.CREATE_CARD(
            p_card_number => '9876543210987691',
            p_date_of_expiration => TO_DATE('12/2025', 'MM/YYYY'),
            p_daily_limit => 500000,
            p_single_payment_limit => 100000,
            p_CVV => '123',
            p_is_active => 1,
            p_account_number => '1234567890123461',
            p_PIN => '1234'
        );

        card_pkg.CREATE_CARD(
            p_card_number => '9876543210987701',
            p_date_of_expiration => TO_DATE('11/2025', 'MM/YYYY'),
            p_daily_limit => 600000,
            p_single_payment_limit => 120000,
            p_CVV => '456',
            p_is_active => 1,
            p_account_number => '1234567890123461',
            p_PIN => '5678'
        );

        card_pkg.CREATE_CARD(
            p_card_number => '1234567890123457',
            p_date_of_expiration => TO_DATE('01/2026', 'MM/YYYY'),
            p_daily_limit => 400000,
            p_single_payment_limit => 80000,
            p_CVV => '789',
            p_is_active => 1,
            p_account_number => '2345678901234568',
            p_PIN => '1122'
        );

        card_pkg.CREATE_CARD(
            p_card_number => '1234567890123461',
            p_date_of_expiration => TO_DATE('05/2026', 'MM/YYYY'),
            p_daily_limit => 450000,
            p_single_payment_limit => 90000,
            p_CVV => '321',
            p_is_active => 1,
            p_account_number => '2345678901234568',
            p_PIN => '3344'
        );

        receiver_pkg.CREATE_RECEIVER(
            p_account_number_receiver => '3456789012345679',
            p_account_number_tied => '1234567890123461',
            p_description => 'Rodzina',
            p_first_name => 'Paweł',
            p_last_name => 'Nowak'
        );

        receiver_pkg.CREATE_RECEIVER(
            p_account_number_receiver => '4567890123456790',
            p_account_number_tied => '1234567890123461',
            p_description => 'Przyjaciel',
            p_first_name => 'Katarzyna',
            p_last_name => 'Zielinska'
        );

        receiver_pkg.CREATE_RECEIVER(
            p_account_number_receiver => '5678901234567891',
            p_account_number_tied => '2345678901234568',
            p_description => 'Firma',
            p_first_name => 'Marek',
            p_last_name => 'Wiśniewski'
        );

        receiver_pkg.CREATE_RECEIVER(
            p_account_number_receiver => '6789012345678902',
            p_account_number_tied => '2345678901234568',
            p_description => 'Zlecenie',
            p_first_name => 'Ewa',
            p_last_name => 'Kaczmarek'
        );

        COMMIT;

    END CREATE_EXAMPLE_ENTITIES;

    PROCEDURE READ_ALL_DATA AS
    BEGIN
        receiver_pkg.read_all_receivers;
        client_pkg.read_all_clients;
        card_pkg.read_all_cards;
        account_pkg.READ_ALL_ACCOUNTS;
    END READ_ALL_DATA;

-- SET SERVEROUTPUT ON;
-- EXEC ini_data.CREATE_EXAMPLE_ENTITIES;
-- EXEC ini_data.READ_ALL_DATA;

-- copy this and execute


-- CREATE USER bank IDENTIFIED BY bank;
-- GRANT ALL PRIVILEGES TO bank;
-- GRANT EXECUTE ON DBMS_CRYPTO TO bank;

-- if the dbms_crypto not working make sure to do this
END ini_data;