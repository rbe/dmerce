SET echo OFF
SET termout ON
SET feedback OFF
SET linesize 132
SET serveroutput ON SIZE 100000

SPOOL crtabperms.lst

CONNECT dmerce_sys/dmerce_sys;

EXEC dmerce.p_copyright;

ACCEPT u PROMPT 'Schema (user): '

DELETE FROM tabperms WHERE table_schema = UPPER('&u');
DELETE FROM colperms WHERE table_schema = UPPER('&u');
COMMIT;

INSERT INTO tabperms
       SELECT table_schema, table_name, privilege, grantee
         FROM all_tab_privs
        WHERE table_schema = UPPER('&u');

INSERT INTO colperms
       SELECT table_schema, table_name, column_name, privilege, grantee
         FROM all_col_privs
        WHERE table_schema = UPPER('&u');
COMMIT;

DELETE FROM tabdefs WHERE owner = UPPER('&u');
DELETE FROM coldefs WHERE owner = UPPER('&u');
COMMIT;

INSERT INTO tabdefs
       SELECT owner, table_name
         FROM all_tables
        WHERE owner = UPPER('&u');

INSERT INTO coldefs
       SELECT owner, table_name, column_name
         FROM all_tab_columns
        WHERE owner = UPPER('&u');
COMMIT;

DELETE FROM colpermcounts WHERE table_schema = UPPER('&u');
--EXEC dmerce_sys.p_all_colpermcounts(UPPER('&u'), 'R_MASY_001');
--EXEC dmerce_sys.p_all_colpermcounts(UPPER('&u'), 'R_MASY_002');

COMMIT;

SPOOL OFF

--EXIT;
