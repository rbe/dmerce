-- #############################################################################################
--
-- %Purpose: Show 'Who uses what objects' in the Database
--
-- #############################################################################################
--
-- Das Diagramm «Table Access» zeigt alle Datenbankobjekte, welche zur
-- Zeit von welcher Session benutzt werden.
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
ttitle left 'Who uses what objects' -
skip 2

select sid "Sid", substr(owner,1,15) "Owner",
  substr(object,1,20) "Object"
  from v$access
where owner != 'SYS'
order by owner;
