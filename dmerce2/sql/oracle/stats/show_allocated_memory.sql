-- #############################################################################################
--
-- %Purpose: Show Memory allocated in [Bytes] for the whole Instance
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
ttitle left 'Memory Allocated in [bytes] for the whole instance' -
skip 2

select /*+ use_nl(n,s) */ sum(value) "Memory Allocated in bytes"
  from v$statname n, v$sesstat s
 where s.statistic# = n.statistic#
   and name = 'session uga memory';
