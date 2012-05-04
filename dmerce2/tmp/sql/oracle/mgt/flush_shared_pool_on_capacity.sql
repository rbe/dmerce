-- #############################################################################################
--
-- %Purpose: Flush Shared Pool when it reaches 60-70% of it's capacity
--
-- Use:      SYS-User
--
-- #############################################################################################
--
-- On a recent project we had a problem where performance would start
-- acceptable at the beginning of the day and by mid-day would be
-- totally unacceptable. Investigation showed that the third party
-- application that ran on top of the Oracle database was generating
-- ad hoc SQL without using bind variables. This generation of ad hoc
-- SQL and non-use of bind variables was resulting in proliferation of
-- non-reusable code fragments in the shared pool, one user had over
-- 90 shared pool segments assigned for queries that differed only by
-- the selection parameter (for example "where last_name='SMITH'"
-- instead of "where last_name='JONES'"). This proliferation of multiple
-- nearly identical SQL statements meant that for each query issued
-- the time to scan the shared pool for identical statements was increasing
-- for each non-reusable statement generated.
--
-- A flush of the shared pool was the only solution to solve this performance
-- problem, resulting that all other query returned again in less than a second.
--
-- It was determined that an automatic procedure was needed to monitor
-- the shared pool and flush it when it reached 60-70% of capacity.

--
-- The following procedue was created:
--
CREATE OR REPLACE VIEW sys.sql_summary AS SELECT
   username,
   sharable_mem,
   persistent_mem,
   runtime_mem
FROM sys.v_$sqlarea a, dba_users b
WHERE a.parsing_user_id = b.user_id;

CREATE OR REPLACE PROCEDURE flush_it AS

  CURSOR get_share IS
  SELECT SUM(sharable_mem)
    FROM sys.sql_summary;

  CURSOR get_var IS
  SELECT value
    FROM v$sga
   WHERE name like 'Var%';

  CURSOR get_time is
  SELECT SYSDATE
    FROM dual;

  todays_date   DATE;
  mem_ratio     NUMBER;
  share_mem     NUMBER;
  variable_mem  NUMBER;
  cur           INTEGER;
  sql_com       VARCHAR2(60);
  row_proc      NUMBER;

BEGIN

  OPEN get_share;
  OPEN get_var;

  FETCH get_share INTO share_mem;
  DBMS_OUTPUT.PUT_LINE('share_mem: '||to_char(share_mem));

  FETCH get_var INTO variable_mem;
  DBMS_OUTPUT.PUT_LINE('variable_mem: '||to_char(variable_mem));

  mem_ratio:=share_mem/variable_mem;
  DBMS_OUTPUT.PUT_LINE('mem_ratio: '||to_char(mem_ratio));

  IF (mem_ratio>0.3) THEN
    DBMS_OUTPUT.PUT_LINE ('Flushing Shared Pool ...');
    cur:=DBMS_SQL.open_cursor;
    sql_com:='ALTER SYSTEM FLUSH SHARED_POOL';
    DBMS_SQL.PARSE(cur,sql_com,dbms_sql.v7);
    row_proc:=DBMS_SQL.EXECUTE(cur);
    DBMS_SQL.CLOSE_CURSOR(cur);
  END IF;
END;
/

-- This procedure was then loaded into the job queue and scheduled to run
-- every hour using the following commands:

DECLARE
  job NUMBER;
BEGIN
  DBMS_JOB.SUBMIT(job,'flush_it;',sysdate,'sysdate+1/24');
END;
/
