# System-bankowy

Database set up tutorial:



CREATE USER bank IDENTIFIED BY bank;
GRANT ALL PRIVILEGES TO bank;

if the dbms_crypto not working make sure to do this(it requires sysdba to grant this)
GRANT EXECUTE ON DBMS_CRYPTO TO bank;

SET SERVEROUTPUT ON;

IMPORT ALL OF THE PACKAGES !!!!


this needs to be executed after for the db to work:

CREATE SEQUENCE transaction_id_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE OR REPLACE TRIGGER trg_transaction_id
BEFORE INSERT ON "TRANSACTION"
FOR EACH ROW
BEGIN
    :NEW.ID := transaction_id_seq.NEXTVAL;
END;
/

this is required for the admin to work in app:

CREATE OR REPLACE TRIGGER trg_transaction_id
BEFORE INSERT ON "TRANSACTION"
FOR EACH ROW
BEGIN
    :NEW.ID := transaction_id_seq.NEXTVAL;
END;
/

DECLARE
    v_result VARCHAR2(4000);
BEGIN
    client_pkg.CREATE_CLIENT(
        p_pesel       => '00000000000',
        p_first_name  => 'admin',
        p_last_name   => 'admin',
        p_middle_name => NULL,
        p_phone_number => '000-000-0000',
        p_date_of_birth => TO_DATE('2000-01-01', 'YYYY-MM-DD'),
        p_result      => v_result
    );

    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/

DECLARE
    v_result VARCHAR2(4000);
BEGIN
    account_pkg.create_account(
        p_type_of_account => 'Checking',
        p_balance          => 0,
        p_status           => 'Active',
        p_login            => 'admin',
        p_password         => 'admin',
        p_pesel            => '00000000000',
        p_result           => v_result
    );

    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/


Java dependencies:
https://gluonhq.com/products/javafx/
https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html