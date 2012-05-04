-- #############################################################################################
--
-- %Purpose: Show waiting Sessions blocked through other Sessions
--
-- #############################################################################################
--
-- Die View ROW_LOCK_WAITS zeigt die wartenden Sessions. Dieses Statement
-- ist als View implementiert, es darf keine Rows zurückbringen, da sonst
-- ein User warten muss.
--
-- create or replace view row_lock_waits
-- (username, sid, object_owner,
--  object_name, sql_text, file_nr, block_nr, record_nr)
-- as
-- select s.username, s.sid,
--  o.owner,
--  o.object_name,
--  a.sql_text,
--  s.row_wait_file#,
--  s.row_wait_block#,
--  s.row_wait_row#
-- from   v$session s, v$sqlarea a, dba_objects o
-- where  o.object_id = s.row_wait_obj#
-- and    s.sql_address = a.address
-- and    s.row_wait_obj# > 0;
--
-- #############################################################################################
--
set feed off;
set pagesize 10000;
set wrap off;
set linesize 200;
set heading on;
set tab on;
set scan on;
set verify off;
--
ttitle left 'Waiting Sessions' -
skip 2

select * from row_lock_waits;
