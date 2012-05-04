-- #############################################################################################
--
-- %Purpose: Show Rollback Segment Report Usage (Nowait Hit %, Waits, Shrinks)
--
-- #############################################################################################
--
-- Rollback Nowait Hit % zeigt die Hits und Misses für die Online Rollback
-- Segmente. Ist dieser Wert zu gross, so müssen mehr Rollbacksegmente
-- erstellt werden.
--
--              Rollback Segment Waits
--
-- Rollback Segment Waits können einfach aus v$waitstat gelesen werden.

-- Waits auf «undo header» werden häufig verringert, indem man weitere
-- Rollback Segmente erstellt.
-- Waits auf «undo block» werden verringert, indem man Rollback Segmente
-- mit mehr Extents erstellt (10 - 20 Extents).
--
--              Rollback Segments Shrinks
--
-- Rollbacksegmente sollten nicht dauernd wachsen und wieder kleiner werden,
-- um den OPTIMAL Parameter einzuhalten. Dies kann mit dem folgenden Query
-- kontrolliert werden. EXTENTS und SHRINKS sollten keine auftreten, sonst
-- muss der Parameter OPTIMAL angepasst werden.
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
ttitle left 'Rollback Nowait Hit %' -
skip 2

select ((sum(gets)-sum(waits)) / sum(gets)) * 100 "Rollback Nowait Hit %"
  from v$rollstat;

ttitle left 'Rollback Segment Waits' -
skip 2

SELECT * from v$waitstat;

ttitle left 'Rollback Segments Shrinks' -
skip 2

SELECT substr(name,1,5) "Name", extents,
       gets, waits, extends, shrinks
  FROM v$rollstat stat, v$rollname name
 WHERE stat.usn = name.usn
   AND status = 'ONLINE';
