-- #############################################################################################
--
-- %Purpose: Show DBMS_JOBS for all Oracle Users
--
-- Use:      Needs Oracle DBA Access
--
-- #############################################################################################
--
ttitle off;
SELECT SUBSTR(job,1,4) "Job",
     SUBSTR(log_user,1,5) "User",
       SUBSTR(schema_user,1,5) "Schema",
       SUBSTR(TO_CHAR(last_date,'DD.MM.YYYY HH24:MI'),1,16) "Last Date",
       SUBSTR(TO_CHAR(next_date,'DD.MM.YYYY HH24:MI'),1,16) "Next Date",
       SUBSTR(broken,1,2) "B",
       SUBSTR(failures,1,6) "Failed",
       SUBSTR(what,1,20) "Command"
  FROM dba_jobs;
