-- #############################################################################################
--
-- %Purpose: Show SQL-Statements in Memory for the connected Sessions (Shared Cursors)
--
-- #############################################################################################
--
-- Das Diagramm «SQL Area» zeigt die shared Cusor Informationen
-- im Library Cache.
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
column username format a12 heading 'User'
ttitle left 'SQL of connected sessions' -
skip 2

select distinct nvl(username,type) username,sid,sql_text
  from v$session, v$sqlarea
 where sql_hash_value = hash_value
   and sql_text is not null
 order by username;
