rem -----------------------------------------------------------------------
rem Filename:   cr8like.sql
rem Purpose:    Script to create a new user (with privs) like an existing 
rem             database user.
rem Date:       02-November-1998
rem Author:     Frank Naude (frank@ibi.co.za) and Rina Nieuwenhuis
rem -----------------------------------------------------------------------

set pages 0 feed off veri off lines 500

accept oldname prompt "Enter user to model new user to: "
accept newname prompt "Enter new user name: " 
accept psw     prompt "Enter new user's password: " 

-- Create user...
select 'create user &&newname identified by &&psw'||
       ' default tablespace '||default_tablespace||
       ' temporary tablespace '||temporary_tablespace||' profile '||
       profile||';'
from   sys.dba_users 
where  username = upper('&&oldname');

-- Grant Roles...
select 'grant '||granted_role||' to &&newname'||
       decode(ADMIN_OPTION, 'YES', ' WITH ADMIN OPTION')||';'
from   sys.dba_role_privs
where  grantee = upper('&&oldname');  

-- Grant System Privs...
select 'grant '||privilege||' to &&newname'||
       decode(ADMIN_OPTION, 'YES', ' WITH ADMIN OPTION')||';'
from   sys.dba_sys_privs
where  grantee = upper('&&oldname');  

-- Grant Table Privs...
select 'grant '||privilege||' on '||owner||'.'||table_name||' to &&newname;'
from   sys.dba_tab_privs
where  grantee = upper('&&oldname');  

-- Grant Column Privs...
select 'grant '||privilege||' on '||owner||'.'||table_name||
       '('||column_name||') to &&newname;'
from   sys.dba_col_privs
where  grantee = upper('&&oldname');  

-- Set Default Role...
select 'alter user &&newname default role '||defrole||';'
from    sys.dba_users
where   username = upper('&&oldname');
