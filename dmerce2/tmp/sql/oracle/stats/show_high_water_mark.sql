-- #############################################################################################
--
-- %Purpose: Show Highwater Mark of a table (Choose Table and Schema Owner)
--
-- Calculate Highwatermark as follows or use Package DBMS_SPACE.UNUSED_SPACE
--
-- select blocks
--   from dba_segments
--  where owner = 'PPB'
--    and segment_name = 'ACM';
--
-- select empty_blocks
--   from dba_tables
--  where owner = 'PPB'
--    and table_name = 'ACM';
--
-- Highwatermark := blocks - empty_blocks -1;
--
-- Beispiel: Ausgangslage der Tabelle Test
-- ---------------------------------------
-- Total Blocks      = 440
-- Total Bytes       = 1802240
-- Unused Blocks     = 15
-- Unused Bytes      = 61440
-- Highwater Mark    = (440 - 15 - 1) = 424
--
-- ALTER TABLE test DEALLOCATE UNUSED;  /* Verändert Unused Blocks und Bytes */
--
-- Total Blocks      = 425
-- Total Bytes       = 1740800
-- Unused Blocks     = 0
-- Unused Bytes      = 0
-- Highwater Mark    = (425 - 0 - 1) = 424
--
-- DELETE FROM test;  /* Verändert HWM nicht, SELECT COUNT(*) geht lange ! */
--
-- Total Blocks      = 425
-- Total Bytes       = 1740800
-- Unused Blocks     = 0
-- Unused Bytes      = 0
-- Highwater Mark    = (425 - 0 - 1) = 424
--
-- TRUNCATE TABLE test; /* Reset der HWM, SELECT COUNT(*) geht schnell ! */
--
-- Total Blocks      = 20
-- Total Bytes       = 81920
-- Unused Blocks     = 19
-- Unused Bytes      = 77824
-- Highwater Mark    = (20 - 19 - 1) = 0
--
-- #############################################################################################
--
set serveroutput on size 200000
set echo off
set feedback off
set verify off
set showmode off
set linesize 500
--
ACCEPT l_user CHAR PROMPT  'Schemaowner: '
ACCEPT l_table CHAR PROMPT 'Tablename: '
--
declare
        OP1 number;
        OP2 number;
        OP3 number;
        OP4 number;
        OP5 number;
        OP6 number;
        OP7 number;
        HWM number;
begin
   dbms_space.unused_space(UPPER('&&l_user'),UPPER('&&l_table'),'TABLE',OP1,OP2,OP3,OP4,OP5,OP6,OP7);
   HWM := OP1 - OP3 - 1;
   dbms_output.put_line('--------------------------');
   dbms_output.put_line('Total Blocks      = '||OP1);
   dbms_output.put_line('Total Bytes       = '||OP2);
   dbms_output.put_line('Unused Blocks     = '||OP3);
   dbms_output.put_line('Unused Bytes      = '||OP4);
   dbms_output.put_line('Highwater Mark    = ('||OP1||' - '||OP3||' - 1) = '||HWM);
end;
/
