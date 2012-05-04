rem Script Description: This script examines various V$ dynamic views. The script
rem                     makes suggestions on modifications that can be made to your
rem                     system,  if specific conditions exist.  The report should be
rem                     run after the system has been up for at least 10 hours and
rem                     should be run over a period of time to get a real feel for 
rem                     what the real condition of the database is.  A one-time
rem                     sample-run on an inactive system will not give an accurate
rem                     picture of what is really occurring within the database.
rem
rem Output file:        tuningstats.lis
rem
rem Prepared By:        Oracle Resource Stop
rem
rem Usage Information:  SQLPLUS SYS/pswd
rem                     @tuningstats.sql
rem

spool tuningstats.lis
--
set pause off
set newpage 1
set linesize 120
set pagesize 46
set show off
set echo off
--
prompt ========================================================================
prompt                         SYSTEM STATISTICS FOR ORACLE
prompt ========================================================================
prompt 
prompt
set heading off
ttitle off

prompt LIBRARY CACHE STATISTICS:
prompt

SELECT
  'PINS    # of times an item in the library cache was executed - '||
  sum(pins),
  'RELOADS # of library cache misses on execution steps         - '||
  sum(reloads),
  '                                        RELOADS / PINS * 100 = '||
  round((sum(reloads) / sum(pins) * 100),2)||'%'
FROM
  v$librarycache;

prompt
prompt
prompt |o| Increase memory until RELOADS is near 0, but watch out for Paging/
prompt |o| Swapping.  To increase library cache, increase SHARED_POOL_SIZE.
prompt |o|
prompt |o| Library Cache Misses indicate that the shared pool is not big
prompt |o| enough to hold the shared SQL area for all concurrently open
prompt |o| cursors.  If you have no Library Cache misses (PINS=0), you may
prompt |o| get a small increase in performance by setting CURSOR_SPACE_FOR_TIME
prompt |o| = TRUE which prevents ORACLE from deallocating a shared SQL area
prompt |o| while an application cursor associated with it is open.
prompt |o|
prompt |o| (For Multi-threaded server, add 1k to SHARED_POOL_SIZE per user).
prompt    
prompt ========================================================================
prompt
prompt

column xn1 format a50
column xn2 format a50
column xn3 format a50
column xv1 new_value xxv1 noprint
column xv2 new_value xxv2 noprint
column xv3 new_value xxv3 noprint
column d1  format a50
column d2  format a50

prompt HIT RATIO:
prompt
prompt Values Hit Ratio is calculated against:
prompt
--
SELECT lpad(name,20,' ')||' = '||value xn1, value xv1
FROM   v$sysstat
WHERE  statistic# = 37;
prompt
--
SELECT lpad(name,20,' ')||' = '||value xn2, value xv2
FROM   v$sysstat
WHERE  statistic# = 38;
prompt
--
SELECT lpad(name,20,' ')||' = '||value xn3, value xv3
FROM   v$sysstat
WHERE  statistic# = 39;
prompt
--
SELECT 'logical reads = db block gets + consistent gets',
       lpad('Logical Reads = ',24,' ')||to_char(&xxv1+&xxv2) d1
from   dual;
prompt
--
SELECT 'Hit Ratio=(logical rds-physical rds)/logical rds',
       lpad('Hit Ratio = ',24,' ')||
       round( (((&xxv2+&xxv1) - &xxv3) / (&xxv2+&xxv1)) * 100,2)||'%' d2
FROM   dual;
--
prompt
prompt
prompt |o|
prompt |o| If the Hit Ratio is less than 60% - 70%, increase the initialization
prompt |o| parameter DB_BLOCK_BUFFERS.  
prompt |o|
prompt
prompt ========================================================================
prompt
prompt
set heading on
column name   format a30
column gets   format 999,999,999
column waits  format 999,999,999
prompt
prompt ROLLBACK CONTENTION STATISTICS:
prompt 
--
SELECT name, waits, gets
FROM   v$rollstat, v$rollname
WHERE  v$rollstat.usn = v$rollname.usn;
prompt
--
SELECT 'The average of waits/gets is '||
       round((sum(waits) / sum(gets)) * 100,2)||'%'
FROM   v$rollstat;
--
prompt
prompt
prompt |o| GETS  = # of gets on the rollback segment header
prompt |o| WAITS = # of waits for the rollback segment header
prompt |o|
prompt |o| If the ratio of waits to gets is more than 1% or 2%, consider
prompt |o| creating more rollback segments.
prompt |o|
prompt |o| Another way to gauge rollback contention is:
prompt |o|
prompt
prompt

col xn1 format 9999999
col xv1 format new_value xxv1 noprint

set heading on

SELECT class, count
FROM   v$waitstat
WHERE  class in ('system undo header', 'system undo block',
                 'undo header',        'undo block');
prompt
set heading off
--
SELECT 'Total requests = '||sum(count) xn1, sum(count) xv1
FROM   v$waitstat;
prompt
--
SELECT 'Contention for system undo header = '||
       (round(count/(&xxv1+0.00000000001),4)) * 100||'%'
FROM   v$waitstat
WHERE  class = 'system undo header';
prompt
--
SELECT 'Contention for system undo block = '||
       (round(count/(&xxv1+0.00000000001),4)) * 100||'%'
FROM   v$waitstat
WHERE  class = 'system undo block';
prompt
--
SELECT 'Contention for undo header       = '||
       (round(count/(&xxv1+0.00000000001),4)) * 100||'%'
FROM   v$waitstat
WHERE  class = 'undo header';
prompt
--
SELECT 'Contention for undo block        = '||
       (round(count/(&xxv1+0.00000000001),4)) * 100||'%'
FROM   v$waitstat
WHERE  class = 'undo block';
prompt
--
prompt
prompt |o| If the percentage for an area is more than 1% or 2%, consider
prompt |o| creating more rollback segments.   NOTE:  This value is usually
prompt |o| very small and has been rounded to 4 places.
prompt |o|
prompt
prompt ========================================================================
prompt 
prompt

prompt REDO CONTENTION STATISTICS:
prompt

SELECT name||' = '||value
FROM   v$sysstat
WHERE  name = 'redo log space requests';

prompt 
prompt
prompt |o| 
prompt |o| This value should be near zero (0).  If this value increments
prompt |o| consistently, processes have had to wait for space in the redo
prompt |o| buffer.  If this condition exests over time, increase the size of 
prompt |o| LOG_BUFFER in the init.ora file in increments of 5% until the value
prompt |o| nears 0.
prompt |o|
prompt |o| NOTE:  Increasing the LOG_BUFFER value will increase the total SGA
prompt |o|
prompt
prompt ========================================================================
prompt
prompt

ttitle off

prompt SORT AREA SIZE:
prompt

col value format 999,999,999

SELECT 'INIT.ORA sort_area_size:  '||value
FROM   v$parameter
WHERE  name like 'sort_area_size';
prompt
--
SELECT a.name, value
FROM   v$statname a, v$sysstat
WHERE  a.statistic# = v$sysstat.statistic# and
       a.name in ('sorts (disk)', 'sorts (memory)', 'sorts (rows)');

prompt
prompt
prompt |o|   
prompt |o| To make best use of sort memory, the initial extent of your Users
prompt |o| sort-work Tablespace should be sufficient to hold at least one sort
prompt |o| run from memory to reduce dynamic space allocation.  If you are
prompt |o| getting a high ratio of disk sorts as opposed to memory sorts,
prompt |o| setting SORT_AREA_RETAINED_SIZE = 0 in init.ora will force the sort
prompt |o| area to be released immediately after a sort finishes.
prompt |o|
prompt 
prompt ========================================================================
prompt
prompt
set heading on

prompt TABLESPACE SIZING:
prompt

col tablespace_name format a10             heading 'Tablespace|Name' 
col sbytes          format 99,999,999,999  heading 'Total Bytes'
col fbytes          format 99,999,999,999  heading 'Free Bytes '
col kount           format            999  heading 'Ext'

ttitle left 'Tablespace Sizing Information' skip 2
compute sum of fbytes on tablespace_name
compute sum of sbytes on tablespace_name
compute sum of sbytes on report
compute sum of fbytes on report
break on report

SELECT a.tablespace_name,
       a.bytes            sbytes,
       sum(b.bytes)       fbytes,
       count(*)           kount
FROM   dba_data_files a, dba_free_space b
WHERE  a.file_id = b.file_id
GROUP BY a.tablespace_name, a.bytes
ORDER BY a.tablespace_name;

prompt
prompt
prompt |o| 
prompt |o| This looks at Tablespace Sizing - Total bytes and free bytes
prompt |o| 
prompt |o| A large number of Free Chunks indicates that the tablespace may
prompt |o| need to be defragmented and compressed.
prompt |o|
prompt
prompt ========================================================================
prompt 
prompt

prompt FRAGMENTED DATABASE OBJECTS:
prompt

col owner         noprint               new_value owner_var
col segment_name  format a30            heading 'Object Name'
col segment_type  format a09            heading 'Table/Indx'
col sum(bytes)    format 99,999,999,999 heading 'Bytes Used'
col count(*)      format 999            heading 'No.'
break on owner skip page 2

ttitle left 'Table Fragmentation Report' skip 2 -
       left 'Creator:  ' owner_var skip 2

SELECT a.owner,
       segment_name,
       segment_type,
       sum(bytes),
       max_extents,
       count(*)
FROM   dba_extents a, dba_tables b
WHERE  segment_name = b.table_name
HAVING count(*) > 3
GROUP BY a.owner, segment_name, segment_type, max_extents
ORDER BY a.owner, segment_name, segment_type, max_extents;
--
prompt
prompt

ttitle left 'Index Fragmentation Report' skip 2 -
       left 'Creator:  ' owner_var skip 2

SELECT a.owner,
       segment_name,
       segment_type,
       sum(bytes),
       max_extents,
       count(*)
FROM   dba_extents a, dba_indexes b
WHERE  segment_name = index_name
HAVING count(*) > 3
GROUP BY a.owner, segment_name, segment_type, max_extents
ORDER BY a.owner, segment_name, segment_type, max_extents;

prompt
prompt
prompt |o|
prompt |o| If the number of extents is approching MAXEXTENTS, it is time
prompt |o| to defragment the table.
prompt |o|
prompt
prompt ========================================================================
prompt
prompt
