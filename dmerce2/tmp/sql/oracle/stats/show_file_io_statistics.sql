-- #############################################################################################
--
-- %Purpose: Show File-I/O Rate, System-I/O Rate and Throughput on all DB-Files
--
-- #############################################################################################
--
--              File I/O Rate
--
-- Das File I/O Rate Diagramm zeigt die Anzahl physical reads
-- und writes pro Sekunde der Oracle Datenbankfiles der gesamten Instance.
--
--              System I/O Rate
--
-- Das System I/O Rate Diagramm zeigt die Anzahl logischen und physischen
-- Reads sowie die Anzahl Blockänderungen pro Sekunde.
--
--              Throughput
--
-- Das Diagramm zeigt die Anzahl User Calls und Transaktionen pro
-- Sekunde der gesamten Instance.
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
ttitle left 'File I/O Rate' -
skip 2

select sum(phyrds), sum(phywrts)
 from v$filestat;

ttitle left 'System I/O Rate' -
skip 2

select (sum(decode(name,'db block gets', value,0)) +
        sum(decode(name,'consistent gets', value,0))) "BLOCK GETS+CONSISTENT GETS",
        sum(decode(name,'db block changes', value,0)) "DB BLOCK CHANGES",
        sum(decode(name,'physical reads', value,0)) "PHYSICAL READS"
  from v$sysstat;

ttitle left 'Throughput' -
skip 2

select sum(decode(name,'user commits', value,0)) "USER COMMITS",
       sum(decode(name,'user calls', value,0)) "USER CALLS"
  from v$sysstat;
