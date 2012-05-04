-- #############################################################################################
--
-- %Purpose: Show DB-Files with Heavy I/O (where are Hotspots of Reads und Writes) ?
--
--           READ_PCT: Prozent an gesamten Reads, Summe von READ_PCT = 100%
--           WRITE_PCT: Prozent an gesamten Writes, Summe von WRITE_PCT = 100%
--
--           IF there is a large number of writes to the temporary tablespace
--           you can increase SORT_AREA_SIZE
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
drop table tot_read_writes;
create table tot_read_writes
 as select sum(phyrds) phys_reads, sum(phywrts) phys_wrts
      from v$filestat;

spool show_io_activity.lst
ttitle left 'Disk I/O s by Datafile' -
skip 2

column name format a40 heading "Filename"
column phyrds format 999,999,999 heading "Reads|Number"
column phywrts format 999,999,999 heading "Writes|Number"
column read_pct format 999.99  heading "Reads|in [%]"
column write_pct format 999.99 heading "Writes|in [%]"
select name, phyrds, phyrds * 100 / trw.phys_reads read_pct,
             phywrts,  phywrts * 100 / trw.phys_wrts write_pct
  from  tot_read_writes trw, v$datafile df, v$filestat fs
 where df.file# = fs.file#
 order by phyrds desc;
spool off;
set feed on echo off termout on pages 24 verify on
ttitle off
