-- #############################################################################################
--
-- %Purpose: Show Free List Hit Ratio in % to verify Database Block Contention
--
-- #############################################################################################
--
-- Das Diagramm �Free List Hit %� zeigt Informationen zur Datenblock
-- Contention. F�r jedes Segment unterh�lt Oracle ein oder mehrere Freelists.
-- Freelists enthalten allozierte Datenblocks f�r diesen Segment- Extent mit
-- freiem Platz f�r INSERTS. Bei vielen concurrent INSERTS sind unter
-- Umst�nden mehrere Freelists zu erstellen.
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
ttitle left 'Free List Hit%' -
skip 2

select (sum(value)-(sum(count)/2))/sum(value)*100 "Gets"
  from v$waitstat w, v$sysstat s
 where w.class='free list'
   and s.name in ('db block gets', 'consistent gets');

select (sum(count) / (sum(value))) * 100 "Misses"
from v$waitstat w, v$sysstat s
where w.class='free list'
and s.name in ('db block gets', 'consistent gets');
