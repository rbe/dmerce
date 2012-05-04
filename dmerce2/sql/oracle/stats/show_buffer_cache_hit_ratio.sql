-- #############################################################################################
--
-- %Purpose: Show Buffer Cache Hit Ratio in % for active Instance since last Startup
--
-- #############################################################################################
--
-- Der Buffer Cache enthält Kopien der gelesenen Daten Blocks aus
-- den Datenbankfiles. Alle Sessions teilen sich den Buffer Cache, der Inhalt
-- des Buffer Caches wird gemäss einem LRU Algorithmus mittels DBWR auf die
-- DB geschrieben. Muss ein Block im Datenbankfile gelesen werden, so handelt
-- es sich dabei um einen Cache Miss, wird der Block bereits im Memory gefunden
-- so spricht man von einem Cache Hit.
-- Die Tabelle V$SYSSTAT zeigt die kumulierten Werte seit die Instance
-- gestartet wurde. Die Physical Reads seit dem Instance Startup verschlechtert
-- die Hit-Ratio, nach einer bestimmten Zeit pendelt sich der Wert ein.
--
-- Es sollte ein HitRatio mit folgenden Werten angestrebt werden:
--
-- Datenbank mit vielen Online-Transaction intensiven Benutzern: 98%
-- Batch intensive Applikation: 89%
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
ttitle left 'Show Buffer Cache Hit Ratio in %' -
skip 2
--
select (1 - (sum(decode(a.name,'physical reads',value,0)) /
            (sum(decode(a.name,'consistent gets',value,0)) +
            sum(decode(a.name,'db block gets',value,0))))) * 100 "Hit Ratio in %"
 from v$sysstat a;
