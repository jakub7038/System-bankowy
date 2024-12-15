CREATE OR REPLACE PACKAGE pakiety_pkg IS

    PROCEDURE CREATE_RANSOM_JOB;

END pakiety_pkg;


CREATE OR REPLACE PACKAGE BODY pakiety_pkg IS

    PROCEDURE CREATE_RANSOM_JOB IS
    BEGIN
        DBMS_SCHEDULER.create_job (
            job_name        => 'RANSOM_JOB',
            job_type        => 'PLSQL_BLOCK',
            job_action      => 'BEGIN RANSOM; END;',
            start_date      => SYSTIMESTAMP,
            repeat_interval => 'FREQ=MONTHLY; BYMONTHDAY=1; BYHOUR=0; BYMINUTE=0; BYSECOND=0',
            enabled         => TRUE
        );
    END CREATE_RANSOM_JOB;

END pakiety_pkg;