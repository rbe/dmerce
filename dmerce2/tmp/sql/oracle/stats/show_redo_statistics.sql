-- #############################################################################################
--
-- %Purpose: Show Redo Allocation Hits in % (Redolog Tuning)
--
-- Redo Allocation Tuning
--
-- Das Diagramm «Redo Allocation Hit %» zeigt das Buffer Tuning der Redolog
-- File Aktivitäten. Die Misses dürfen nicht grösser als 1 % sein.
--
-- Das Diagramm «Redo Statistics» zeigt die Anzahl Redo Entries, Space
-- Requests und Synch. Writes pro Sekunde für die Datenbank Instance.
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
ttitle left 'Redo Alloc Hit%' -
skip 2

select ((gets+immediate_gets) /
        (gets+immediate_gets+misses+immediate_misses)) *100 "Redo Alloc Hit%"
  from v$latch
 where name = 'redo allocation';

ttitle left 'Redo Statistics' -
skip 2

select sum(decode(name,'redo entries', value,0)) "Redo Entries",
       sum(decode(name,'redo log space requests', value,0)) "Space Requests",
       sum(decode(name,'redo synch writes', value,0)) "Synch Writes"
  from v$sysstat;
