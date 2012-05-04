-- #############################################################################################
--
-- %Purpose: Show Status for all Objects (VALID, INVALID) of a Schema Owner
--
--           Show object of a user or wildcard incl. status
--           (choice valid or invalid (default both)
--
-- #############################################################################################
--
col user_name noprint new_value user_name
col date_time noprint new_value date_time
col owner format a13 trunc
col object_name format a30
col object_type format a8 trunc heading OBJ-TYPE
col status format a7
col last_ddl_time format a17
prompt
prompt User name, wildcard or <RETURN> for all users:
prompt Object name, wildcard or <RETURN> for all objects:
prompt V = valid, I = invalid, <RETURN> = valid and invalid:
prompt
accept user_name char prompt "User name: "
accept object_name char prompt "Object name: "
accept status char prompt "Status: "
set echo off termout off pause off
select upper(nvl('&&user_name','%')) user_name,
       to_char(sysdate,'dd.mm.yyyy hh24:mi') date_time
from dual;
set termout on;
set feed on;
set pagesize 10000;
set wrap off;
set linesize 200;
set tab on;
set verify off
set timing off
ttitle left 'Objects of user 'user_name' at 'date_time -
       right  sql.pno skip 2
spool show_object_status.lst
select  owner, object_name,
        decode(object_type,'PACKAGE','PCK-SPEZ',
                           'PACKAGE BODY','PCK-BODY',
                           'DATABASE LINK','DB-LINK',
                                           object_type) object_type,
        status, to_char(last_ddl_time,'dd.mm.yy hh24:mi:ss') last_ddl_time
from  sys.dba_objects
where   owner like nvl(upper('&user_name'),'%')
and     object_name like nvl(upper('&object_name'),'%')
and     status like decode(upper(substr('&status',1,1)),
                                'V', 'VALID',
                                'I','INVALID',
                                              '%')
order by owner, object_name, object_type
/
spool off
prompt
prompt show_object_status.lst has been spooled
prompt
