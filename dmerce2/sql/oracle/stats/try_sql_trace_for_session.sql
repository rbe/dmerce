-- #############################################################################################
--
-- %Purpose: Try to set SQL_TRACE ON for another Session / Program
--
-- Use:      SYS-User
--
-- #############################################################################################
--
CREATE OR REPLACE
PROCEDURE  try_sql_trace_for_session  (ProgName IN VARCHAR2) IS
--
-- Try to enable SQL_TRACE for ProgName
--
-- Example
--
-- SQL> set serveroutput on;
-- SQL> execute sys.TRY_SQL_TRACE_FOR_SESSION('SqlNav');
-- SID: 11 Serial#: 58
-- Tracing enabled ... bye, bye
--
-- PL/SQL procedure successfully completed.
--
  nCount  NUMBER := 0;

  CURSOR curs_get_sid IS
  SELECT sid,serial#
    FROM v$session
   WHERE program LIKE '%'||ProgName||'%';

BEGIN
    WHILE nCount = 0
    LOOP
      FOR rec IN curs_get_sid LOOP
        dbms_output.put_line('SID: '||rec.sid||' Serial#: '||rec.serial#);
        dbms_system.set_sql_trace_in_session(rec.sid,rec.serial#,TRUE);
        nCount := 1;
      END LOOP;
      dbms_lock.sleep(10);
    END LOOP;
    dbms_output.put_line('Tracing enabled ... bye, bye');
END;
/
