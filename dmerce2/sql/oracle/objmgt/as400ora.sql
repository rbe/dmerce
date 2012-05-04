 SET ECHO OFF
PROMPT
PROMPT ####################################################
PROMPT ## Automatic create file to create table from     ##
PROMPT ##   the AS400 and create file for the Insert     ##
PROMPT ##                                                ##
PROMPT ##          by DPC Don A. Beeler USNR-Ret         ##
PROMPT ##                 Febuary 2002                   ##
PROMPT ####################################################
PROMPT
PROMPT This will create a temporary table called xxxx_temp_apps1.
PROMPT The temporary table xxxx_temp_apps1 will be droped at the end.
PROMPT Example AS400 library and file name: ordtalib.otirp@as400 
ACCEPT as400_name CHAR PROMPT 'Enter the AS400 library.file@AS400 name :
'
ACCEPT table_name CHAR PROMPT 'Enter the Oracle table name :'
SET PAGESIZE 0
SET LINESIZE 150
SET VERIFY OFF
SET TRIMSPOOL ON
SET FEEDBACK OFF
SET TERMOUT OFF

COLUMN dummy NOPRINT FORMAT A1
COLUMN dummy2 NOPRINT FORMAT A1
COLUMN command FORMAT A70
COLUMN file_name new_value spool_file_name

CREATE TABLE xxxx_temp_apps1
  AS SELECT * FROM &as400_name
    WHERE 1 = 2;

SELECT 'create_'||LOWER('&table_name')||'.sql' file_name FROM dual;
SPOOL &spool_file_name

SELECT 1 dummy, 'CREATE TABLE &table_name ('||CHR(10) command, 0 dummy2
    FROM dual
UNION
SELECT 2 dummy, '  '||column_name||'
'||DECODE(data_type,'CHAR','VARCHAR2',data_type)||
  ' '||DECODE(data_type,'DATE',NULL,
'CHAR','('||TO_CHAR(data_length)||')', 
    'VARCHAR2','('||TO_CHAR(data_length)||')',
    'NUMBER',DECODE(NVL(data_precision,0),0,' ', 
    DECODE(NVL(data_scale,0),0,'('||TO_CHAR(data_precision)||')',
    '('||TO_CHAR(data_precision)||','||TO_CHAR(data_scale)||')' )))||
    ' '||DECODE(nullable,'Y',NULL,'NOT NULL')||
    ','||CHR(10) command, column_id dummy2
      FROM all_tab_columns
        WHERE table_name = 'XXXX_TEMP_APPS1'
          AND column_id <> (SELECT MAX(column_id) 
            FROM all_tab_columns
              WHERE table_name = 'XXXX_TEMP_APPS1')
UNION
SELECT 2 dummy, '  '||column_name||'
'||DECODE(data_type,'CHAR','VARCHAR2',data_type)||
  ' '||DECODE(data_type,'DATE',NULL,
'CHAR','('||TO_CHAR(data_length)||')', 
    'VARCHAR2','('||TO_CHAR(data_length)||')',
    'NUMBER',DECODE(NVL(data_precision,0),0,' ', 
    DECODE(NVL(data_scale,0),0,'('||TO_CHAR(data_precision)||')',
    '('||TO_CHAR(data_precision)||','||TO_CHAR(data_scale)||')' )))||
    ' '||DECODE(nullable,'Y',NULL,'NOT NULL')||
    ' '||CHR(10) command, column_id dummy2
      FROM all_tab_columns
        WHERE table_name = 'XXXX_TEMP_APPS1'
          AND column_id = (SELECT MAX(column_id) 
            FROM all_tab_columns
              WHERE table_name = 'XXXX_TEMP_APPS1')
UNION
SELECT 3 dummy, ')'||CHR(10) command, 0 dummy2
  FROM dual
UNION
SELECT 4 dummy, '/'||CHR(10) command, 0 dummy2
  FROM dual
ORDER BY 1, 3;

SPOOL OFF

SELECT 'insert_'||LOWER('&table_name')||'.sql' file_name FROM dual;
SPOOL &spool_file_name

SELECT 1 dummy, 'INSERT INTO &table_name ('||CHR(10) command, 0 dummy2
    FROM dual
UNION
SELECT 2 dummy, '  '||column_name||', '||CHR(10) command, column_id
dummy2
      FROM all_tab_columns
        WHERE table_name = 'XXXX_TEMP_APPS1'
          AND column_id <> (SELECT MAX(column_id) 
            FROM all_tab_columns
              WHERE table_name = 'XXXX_TEMP_APPS1')
UNION
SELECT 3 dummy, '  '||column_name||') '||CHR(10) command, column_id
dummy2
      FROM all_tab_columns
        WHERE table_name = 'XXXX_TEMP_APPS1'
          AND column_id = (SELECT MAX(column_id) 
            FROM all_tab_columns
              WHERE table_name = 'XXXX_TEMP_APPS1')
UNION
SELECT 4 dummy, 'SELECT ' command, 0 dummy2
    FROM dual
UNION
SELECT 5 dummy, '
'||DECODE(data_type,'CHAR','RTRIM(LTRIM('||column_name||')), ',
  column_name||', ')||CHR(10) command, column_id dummy2
      FROM all_tab_columns
        WHERE table_name = 'XXXX_TEMP_APPS1'
          AND column_id <> (SELECT MAX(column_id) 
            FROM all_tab_columns
              WHERE table_name = 'XXXX_TEMP_APPS1')
UNION
SELECT 6 dummy, '
'||DECODE(data_type,'CHAR','RTRIM(LTRIM('||column_name||'))',
  column_name||' ')||CHR(10) command, column_id dummy2
      FROM all_tab_columns
        WHERE table_name = 'XXXX_TEMP_APPS1'
          AND column_id = (SELECT MAX(column_id) 
            FROM all_tab_columns
              WHERE table_name = 'XXXX_TEMP_APPS1')
UNION
SELECT 7 dummy, ' FROM &as400_name'||CHR(10) command, 0 dummy2
    FROM dual
UNION
SELECT 8 dummy, '/'||CHR(10) command, 0 dummy2
    FROM dual
ORDER BY 1, 3;

SPOOL OFF

DROP TABLE xxxx_temp_apps1;
SET PAGESIZE 24
SET FEEDBACK ON
SET TRIMSPOOL OFF
SET VERIFY ON
SET LINESIZE 80
SET TERMOUT ON
SET ECHO ON
PROMPT
PROMPT Create and insert files was created.
