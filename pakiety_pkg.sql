REATE OR REPLACE PACKAGE pakiety_pkg IS

    PROCEDURE CREATE_RANSOM_JOB;

 PROCEDURE CREATE_RANSOM_TRANSACTION;

END pakiety_pkg;
/

CREATE OR REPLACE PACKAGE BODY pakiety_pkg IS

    PROCEDURE CREATE_RANSOM_JOB IS
        v_result VARCHAR2(200);
    BEGIN
        DBMS_SCHEDULER.create_job (
            job_name        => 'RANSOM_JOB',
            job_type        => 'PLSQL_BLOCK',
            job_action      => 'BEGIN pakiety_pkg.CREATE_RANSOM_TRANSACTION; END;',
            start_date      => SYSTIMESTAMP,
            repeat_interval => 'FREQ=MONTHLY; BYMONTHDAY=1; BYHOUR=0; BYMINUTE=0; BYSECOND=1',
            enabled         => TRUE
        );
    END CREATE_RANSOM_JOB;

    PROCEDURE CREATE_RANSOM_TRANSACTION IS
        v_account_number CHAR(12);
        v_receiver_account_number CHAR(26) := '00000000000000000000000000';
        v_amount NUMBER := 500;
        v_type_of_transaction VARCHAR2(50) := 'Ransom';
        v_description VARCHAR2(200) := 'Ransom payment';
        v_result VARCHAR2(200);
        CURSOR account_cursor IS
            SELECT account_number FROM ACCOUNT;
    BEGIN
        FOR rec IN account_cursor LOOP
            transaction_pkg.CREATE_TRANSACTION(
                p_account_number => rec.account_number,
                p_account_number_receiver => v_receiver_account_number,
                p_amount => v_amount,
                p_type_of_transaction => v_type_of_transaction,
                p_description_of_transaction => v_description,
                p_result => v_result
            );
        END LOOP;
    END CREATE_RANSOM_TRANSACTION;

END pakiety_pkg;
/

BEGIN
   pakiety_pkg.CREATE_RANSOM_TRANSACTION;
END;
/

BEGIN
    DBMS_SCHEDULER.enable('RANSOM_JOB');
END;
/


BEGIN
    DBMS_SCHEDULER.disable('RANSOM_JOB');
END;
/

SELECT job_name, enabled, last_start_date, next_run_date
FROM user_scheduler_jobs
WHERE job_name = 'RANSOM_JOB';
