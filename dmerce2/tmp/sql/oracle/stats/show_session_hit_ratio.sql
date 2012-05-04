-- #############################################################################################
--
-- %Purpose: Show Hit-Ratios, Consistent-Gets, DB-Block-Gets, Physical-Reads for the Sessions
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
spool show_session_hit_ratio.lst

ttitle 'User Hit Ratios' -
skip 2

column nl newline;
column "Hit Ratio" format 999.99
column  "User Session" format a15;
set pagesize 999

select  se.username||'('|| se.sid||')' "User Session",
       sum(decode(name, 'consistent gets',value, 0))  "Consis Gets",
        sum(decode(name, 'db block gets',value, 0))  "DB Blk Gets",
        sum(decode(name, 'physical reads',value, 0))  "Phys Reads",
       (sum(decode(name, 'consistent gets',value, 0))  +
        sum(decode(name, 'db block gets',value, 0))  -
        sum(decode(name, 'physical reads',value, 0)))
           /
       (sum(decode(name, 'consistent gets',value, 0))  +
        sum(decode(name, 'db block gets',value, 0))  )  * 100 "Hit Ratio"
  from  v$sesstat ss, v$statname sn, v$session se
where   ss.sid    = se.sid
  and   sn.statistic# = ss.statistic#
  and   value != 0
  and   sn.name in ('db block gets', 'consistent gets', 'physical reads')
group by se.username, se.sid
having
       (sum(decode(name, 'consistent gets',value, 0))  +
        sum(decode(name, 'db block gets',value, 0))  -
        sum(decode(name, 'physical reads',value, 0)))
           /
       (sum(decode(name, 'consistent gets',value, 0))  +
        sum(decode(name, 'db block gets',value, 0))  )  * 100
       < 100;
spool off;
set feed on echo off termout on pages 24 verify on
ttitle off
