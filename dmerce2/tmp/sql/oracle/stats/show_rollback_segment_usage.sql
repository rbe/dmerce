-- #############################################################################################
--
-- %Purpose: Show which Users are accessing which Rollback Segments.
--
--           It is sometimes useful to know which users are accessing the rollback segments.
--           This is important when a user is continally filling the rollback segments
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
spool show_rollback_segment_usage.lst

ttitle 'Current Rollback Segment Usage' -
skip 2

column "Rollback Segment Name" format a18;
column  "Oracle User Session" format a40;

select r.name "Rollback Segment Name",
       p.spid "Process ID",
       s.username||'('||l.sid||')' "Oracle User Session",
       sq.sql_text
  from v$sqlarea sq, v$lock l, v$process p, v$session s, v$rollname r
 where l.sid = p.pid(+)
   and s.sid = l.sid
   and trunc(l.id1(+) / 65536) = r.usn
   and l.type(+) = 'TX'
   and l.lmode(+) = 6
   and s.sql_address = sq.address
   and s.sql_hash_value = sq.hash_value
 order by r.name
/
spool off;
set feed on echo off termout on pages 24 verify on
ttitle off
