-- #############################################################################################
--
-- %Purpose: Show SQL-Statements in Memory with I/O-intensiv SQL-Statements (V$SQLAREA)
--
-- #############################################################################################
--
-- Output from V$SQLAREA:
--
-- EXECUTIONS: The number of executions that took place on this object
--             since iw was brought into the library cache.
--
-- READS_PER_RUN: Number od Disk-Bytes reads per execution, If this is high, then
--                the statement is I/O bound.
--
-- I/O-intensive SQL-Statements in the memory (V$SQLAREA)
--
--                                    Total   Read-Per-Run   Disk-Reads  Buffer-Gets       Hit
-- SQL-Statement                       Runs    [Number of]  [Number of]  [Number of] Ratio [%]
-- ------------------------------- -------- -------------- ------------ ------------ ---------
-- DECLARE job BINARY_INTEGER := :        1      204,670.0      204,670       47,982       ###
-- DECLARE job BINARY_INTEGER :=          1       77,858.0       77,858      181,282        57
-- select msisdn, function, modif         1       12,087.0       12,087       25,602        53
-- select msisdn, function, modif         1       12,031.0       12,031       25,599        53
-- select msisdn, function, modifi        1       11,825.0       11,825       25,598        54
-- select "A".rowid, 'PPB', 'FRAG         1       11,538.0       11,538       11,542         0
-- select msisdn.ms_id ,to_char(msi     270        3,259.1      879,953    3,939,464        78
-- select msisdn.ms_id  from msis       270        3,258.0      879,656    3,939,723        78
--
-- The last two statements are quit heavy, they runs 270 times, each time they needed 3000
-- disk reads, total used 870000 disk reads
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
column sql_text format a40 heading 'SQL-Statement'
column executions format 999,999 heading 'Total|Runs'
column reads_per_run format 999,999,999.9 heading 'Read-Per-Run|[Number of]'
column disk_reads format 999,999,999 heading 'Disk-Reads|[Number of]'
column buffer_gets format 999,999,999 heading 'Buffer-Gets|[Number of]'
column hit_ratio format 99 heading 'Hit|Ratio [%]'

ttitle left 'I/O-intensive SQL-Statements in the memory (V$SQLAREA)' -
skip 2

SELECT sql_text, executions,
       round(disk_reads / executions, 2) reads_per_run,
       disk_reads, buffer_gets,
       round((buffer_gets - disk_reads) / buffer_gets, 2)*100 hit_ratio
FROM   v$sqlarea
WHERE  executions  > 0
AND    buffer_gets > 0
AND    (buffer_gets - disk_reads) / buffer_gets < 0.80
ORDER BY 3 desc;
