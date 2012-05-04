-- #############################################################################################
--
-- %Purpose: Show Memory Sort Hit % (Memory and Disc)
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
ttitle left 'Show Memory Sort Hit %' -
skip 2

select (sum(decode(name, 'sorts (memory)', value, 0)) /
       (sum(decode(name, 'sorts (memory)', value, 0)) +
        sum(decode(name, 'sorts (disk)', value, 0)))) * 100 "Memory Sort Hit %"
  from v$sysstat;
