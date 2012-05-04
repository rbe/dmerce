-- #############################################################################################
--
-- %Purpose: Show all Privileges for a connected User through Roles and direct
--
--           Show all Privileges for a connected user over Roles and
--           direct. Show all System- and Object Privileges as well.
--
-- #############################################################################################
--
set feed off
set pagesize 30000
set linesize 200
clear breaks columns
set pause off
spool show_privileges_for_user.lis
--
ttitle left "Currently active Role(s) for User: " sql.user -
skip 2
column username format A22 heading 'User' trunc
column role format A40 heading 'Active|Role' trunc
column default_role format A7 heading 'Default|Role' trunc
column admin_option format A7 heading 'Admin|Option' trunc
--
break on username

select  username,role,default_role,admin_option
  from    user_role_privs, session_roles
  where   granted_role = role
  order   by role
/
--
ttitle left "Currently inactive Role(s) for User: " sql.user -
skip 2
column username format A22 heading 'User' trunc
column granted_role format A40 heading 'Granted|Role' trunc
column default_role format A7 heading 'Default|Role' trunc
column admin_option format A7 heading 'Admin|Option' trunc

select  username,granted_role,default_role,admin_option
from  user_role_privs
where not exists (select 'x'
      from   session_roles
      where  role = granted_role)
union
select 'All Role(s) are active','','',''
from  dual
where 0 = (select count('x')
     from   user_role_privs
     where  not exists (select 'x'
             from  session_roles
             where role = granted_role))
order by 1,2
/
--
ttitle left "Sub-Role(s) for User: " sql.user -
skip 2
column granted_role format A39 heading 'These Role(s) are granted to ...' trunc
column role format A39 heading '... these Role(s)'

select  granted_role,role
from  role_role_privs
union
select  'No Sub-Role(s) found',''
from  dual
where 0 = (select count('x')
     from  role_role_privs)
/
--
ttitle "System Privileges through Roles and direct for User: " sql.user -
skip 2
column role format A40 heading 'Role' trunc
column privilege format A30 heading 'System|Privilege' trunc
column admin_option format A7 heading 'Admin|Option' trunc
--
break on role skip 1

select  role,privilege,admin_option
from  role_sys_privs
union
select  'directly' role,privilege,admin_option
from  user_sys_privs
order by 1,2
/
--
ttitle "Object Privileges through Roles and direct for User: " sql.user -
skip 2
column role format a20 heading 'Role' trunc
column owner format a17 heading 'Object|Owner' trunc
column table_name format a20 heading 'Object|Name' trunc
column privilege format a12 heading 'Privilege' trunc
column grantable format a6 heading 'Admin|Option' trunc
break on role on owner on table_name

select  role, owner, table_name, privilege, grantable
from  role_tab_privs
where role in (select role
     from   session_roles)
and column_name is null
union
select  'directly' role, owner, table_name, privilege, grantable
from  user_tab_privs_recd
order by 1,2,3,4
/
--
ttitle "Column Privileges for User: " sql.user -
skip 2
column role format a16 heading 'Role' trunc
column owner format a10 heading 'Object|Owner' trunc
column table_name format a20 heading 'Object|Name' trunc
column column_name format a20 heading 'Column|Name' trunc
column privilege format a10 heading 'Privilege' trunc

select  role, owner, table_name, column_name, privilege, grantable
from  role_tab_privs
where role in (select role
     from   session_roles)
and column_name is not null
union
select  'directly' role, owner, table_name, column_name,
  privilege, grantable
from  user_col_privs_recd
order by 1,2,3,4
/
spool off
clear breaks columns
ttitle off
prompt
prompt Listing created in "showpriv.lis"
prompt
