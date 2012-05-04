-- #############################################################################################
--
-- %Purpose: Generate 'CREATE TABLE' Script for an existing Table in the database
--
-- Use:      SYSTEM, SYS or user having SELECT ANY TABLE  system privilege
--
-- #############################################################################################
--
set serveroutput on size 200000
set echo off
set feedback off
set verify off
set showmode off
--
ACCEPT l_user CHAR PROMPT  'Username: '
ACCEPT l_table CHAR PROMPT 'Tablename: '
--
DECLARE
 CURSOR TabCur IS
 SELECT table_name,owner,tablespace_name,
        initial_extent,next_extent,
        pct_used,pct_free,pct_increase,degree
   FROM sys.dba_tables
  WHERE owner=upper('&&l_user')
    AND table_name=UPPER('&&l_table');
--
 CURSOR ColCur(TableName varchar2) IS
 SELECT column_name col1,
        DECODE (data_type,
                'LONG',       'LONG   ',
                'LONG RAW',   'LONG RAW  ',
                'RAW',        'RAW  ',
                'DATE',       'DATE   ',
                'CHAR',       'CHAR' || '(' || data_length || ') ',
                'VARCHAR2',   'VARCHAR2' || '(' || data_length || ') ',
                'NUMBER',     'NUMBER' ||
                DECODE (NVL(data_precision,0),0, ' ',' (' || data_precision ||
                DECODE (NVL(data_scale, 0),0, ') ',',' || DATA_SCALE || ') '))) ||
        DECODE (NULLABLE,'N', 'NOT NULL','  ') col2
   FROM sys.dba_tab_columns
  WHERE table_name=TableName
    AND owner=UPPER('&&l_user')
 ORDER BY column_id;
--
 ColCount    NUMBER(5);
 MaxCol      NUMBER(5);
 FillSpace   NUMBER(5);
 ColLen      NUMBER(5);
--
BEGIN
 MaxCol:=0;
 --
 FOR TabRec in TabCur LOOP
    SELECT MAX(column_id) INTO MaxCol FROM sys.dba_tab_columns
     WHERE table_name=TabRec.table_name
       AND owner=TabRec.owner;
    --
    dbms_output.put_line('CREATE TABLE '||TabRec.table_name);
    dbms_output.put_line('( ');
    --
    ColCount:=0;
    FOR ColRec in ColCur(TabRec.table_name) LOOP
      ColLen:=length(ColRec.col1);
      FillSpace:=40 - ColLen;
      dbms_output.put(ColRec.col1);
      --
      FOR i in 1..FillSpace LOOP
         dbms_output.put(' ');
      END LOOP;
      --
      dbms_output.put(ColRec.col2);
      ColCount:=ColCount+1;
      --
      IF (ColCount < MaxCol) THEN
         dbms_output.put_line(',');
      ELSE
         dbms_output.put_line(')');
      END IF;
    END LOOP;
    --
    dbms_output.put_line('TABLESPACE '||TabRec.tablespace_name);
    dbms_output.put_line('PCTFREE '||TabRec.pct_free);
    dbms_output.put_line('PCTUSED '||TabRec.pct_used);
    dbms_output.put_line('STORAGE ( ');
    dbms_output.put_line('  INITIAL     '||TabRec.initial_extent);
    dbms_output.put_line('  NEXT        '||TabRec.next_extent);
    dbms_output.put_line('  PCTINCREASE '||TabRec.pct_increase);
    dbms_output.put_line(' )');
    dbms_output.put_line('PARALLEL '||TabRec.degree);
    dbms_output.put_line('/');
 END LOOP;
END;
/
