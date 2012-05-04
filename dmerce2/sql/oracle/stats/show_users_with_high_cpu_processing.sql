-- #############################################################################################
--
-- %Purpose: Show Users with High CPU Processing since Instance Startup
--
-- #############################################################################################
--
set feed off;
set pagesize 10000;
set wrap off;
set linesize 80;
set heading on;
set tab on;
set scan on;
set verify off;
--
spool show_users_with_high_cpu_processing.lst

ttitle 'Show Users with High CPU Processing' -
skip 2

column user_process format a10 heading "UserProcess(SID)"
column value format 999,999,999.99

select ss.username||'('||se.sid||')' user_process, value
  from v$session ss, v$sesstat se, v$statname sn
 where  se.statistic# = sn.statistic#
   and  name  like '%CPU used by this session%'
   and  se.sid = ss.sid
   and  ss.username is not null
 order  by substr(name,1,25), value desc
/
spool off;
set feed on echo off termout on pages 24 verify on
ttitle off


