-- #############################################################################################
--
-- %Purpose: Show complete System Statistic, e.g. Full Table Scans, Redolog Infos from V$SYSSTAT
--
-- #############################################################################################
--
-- System Statistics
--
-- Das Diagramm «System Stats» zeigt alle Parameter der wichtigen System
-- Statistiktabelle v$sysstat.
--
-- Auswertung der System Statistik
--
-- Aus den Systemstatistiken können wichtige Informationen gewonnen werden.
-- Man beachte dass es sich bei diesen Angaben immer um kumulierte Werte seit
-- dem letzten Startup handelt.
--
-- Full Table Scans
--
-- table scan blocks gotten       61'900'307
-- table scan rows gotten       194'6840'695
-- table scans (long tables)          13'267
-- table scans (short tables)        307'195
--
-- Index Scans
--
-- table fetch by rowid           15'653'655
--
-- Redo Waits
--
-- redo log space requests              1018
-- redo log space wait time            21263
--
-- Bei grösseren Waits sind die RDO-Files zu vergrössern und er Parameter
-- LOG_BUFFER muss erhöht werden. Die Zeit ist in 1/100 Sekunden angegeben
-- (21263 = 212 Sekunden = 3,5 Minuten in etwa 5 Wochen).
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
ttitle left 'System Statistics' -
skip 2

select s.name, s.value
  from v$sysstat s
order by s.name, s.value;
