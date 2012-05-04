-- #############################################################################################
--
-- %Purpose: Show Low-level Locks (Latches) on internal shared Memory Structures
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
ttitle left 'Latch Statistics' -
skip 2

select substr(ln.name,1,30) "Name",l.gets,l.misses,l.sleeps,
       l.immediate_gets "ImmGets",l.immediate_misses "ImmMiss"
  from v$latch l, v$latchname ln, v$latchholder lh
 where l.latch#=ln.latch#
   and l.addr=lh.laddr(+)
 order by l.level#, l.latch#;
