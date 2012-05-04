-- #############################################################################################
--
-- %Purpose: Show Table Grants for all Schema Owners
--
-- Use:      Each Oracle User
--
-- #############################################################################################
--
spool show_column_grants.lst
ttitle left 'Show grants on table columns' skip 2
set feed off
set pagesize 10000

column owner noprint new_value own
column table_name format A20 heading 'Object|Name' trunc
column column_name format A20 heading 'Column|Name' trunc
column privilege format A9 heading 'Privilege' trunc
column grantee format A17 heading 'User/Role to whom|Access is granted' trunc
column grantor format A10 heading 'User who|made Grant' trunc

ttitle center 'Object Owner: 'own skip 2

break on owner skip page on grantee on grantor skip 1

select  owner,
  grantee,
  grantor,
  table_name,
  column_name,
  privilege
from dba_col_privs
order by owner, grantee, grantor, table_name
/
spool off;
