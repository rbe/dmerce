-- #############################################################################################
--
-- %Purpose: Show Object Privileges for Schema Owner which can be choosen
--
-- Use:      Needs Oracle DBA Access
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

spool show_grants.lst
ttitle off

ACCEPT user_namen CHAR PROMPT 'Schema Owner: '

column owner format a10 heading 'Object|Owner'
column grantor format a22 heading 'User who|performed the Grant'
column grantee format a24 heading 'User/Role to whom|Access was granted'
column privilege format a12 heading 'Object|Privilege'
column table_name noprint new_value tab

ttitle left 'Object Grants on: 'tab skip 2

break on table_name skip page -
on owner skip 2 -
on grantor skip -
on grantee -
on privileges

select  substr(table_name,1,20) table_name,
  substr(owner,1,16) owner,
  substr(grantor,1,24) grantor,
  substr(grantee,1,24) grantee,
  substr(privilege,1,12) privilege
from dba_tab_privs
where upper(owner) LIKE UPPER('&user_namen')
order by 1,2,3,4,5;
spool off;
