rem -----------------------------------------------------------------------
rem Filename:   usedsp.sql
rem Purpose:    Used space in Meg by segment type
rem Author:     Frank Naude (frank@ibi.co.za)
rem -----------------------------------------------------------------------

set pagesize 50000
set line 80

col "Total Used Meg" format 999,999,990
col "Data part" format 999,999,990
col "Index part" format 999,999,990
col "LOB part" format 999,999,990
col "RBS part" format 999,999,990

tti 'Used space in Meg by segment type'

select sum(bytes)/1024/1024 "Total Used",
       sum( decode( substr(segment_type,1,5), 'TABLE',     bytes/1024/1024, 0))
                "Data part",
       sum( decode( substr(segment_type,1,5), 'INDEX',     bytes/1024/1024, 0))
                "Index part",
       sum( decode( substr(segment_type,1,3), 'LOB',       bytes/1024/1024, 0))
                "LOB part",
       sum( decode(segment_type,              'ROLLBACK',  bytes/1024/1024, 0))
                "RBS part",
        sum( decode(segment_type,             'TEMPORARY', bytes/1024/1024, 0))
                "TEMP part"
from   sys.dba_segments
/
tti off

tti "Total database size"

select sum(bytes)/1024/1024 "Total DB size in Meg"
  from sys.v_$datafile
/
tti off
