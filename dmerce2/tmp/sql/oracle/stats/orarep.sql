Top 10 CPU Time:
----------------

col program form a30 heading "Program"
col CPUMins form 99990 heading "CPU Mins"

spool topcpu.lst

select rownum as rank, a.*
from (
    SELECT v.sid, program, v.value / (100 * 60) CPUMins
    FROM v$statname s , v$sesstat v, v$session sess
   WHERE s.name = 'CPU used by this session'
     and sess.sid = v.sid
     and v.statistic#=s.statistic#
     and v.value>0
   ORDER BY v.value DESC) a
where rownum < 11
   ;

spool off


Sort Usage
----------
col username form a10 heading "DB User"
col osuser form a10 heading "OS User"
col name form a20 heading "Type Of Sort"
col tablespace form a10

SELECT s.username
, s.sid
, s.serial#
, u.tablespace
, u.contents
, u.segtype
, u.extents
, u.blocks
FROM v$session s, v$sort_usage u
WHERE s.saddr = u.session_addr ;

SELECT  s.sid
        ,s.username
        ,s.osuser
        ,vsn.name
        ,vss.value
FROM    v$session s,
        v$sesstat vss,
        v$statname vsn
WHERE   vss.statistic#=vsn.statistic#
AND     s.sid = vss.sid
AND     vsn.name like '%sort%'
AND     s.username is not null
ORDER BY s.username
/



Datafile stats
--------------
set pages 200
col name form a56

select d.name,s.PHYRDS,s.PHYWRTS
from v$datafile d, v$filestat s
where d.file#=s.file#
order by 1
/




Latch contention
----------------
col name form A25
col gets form 999,999,999
col misses form 999.99 Heading "Miss %"
col spins form 999.99 Heading "Spin %"
col igets form 999,999,999
col imisses form 999.99 "IMiss %"

spool monlatch.lst

select name
,gets
,misses*100/decode(gets,0,1,gets) misses
,spin_gets*100/decode(misses,0,1,misses) spins
, immediate_gets igets
,immediate_misses*100/decode(immediate_gets,0,1,immediate_gets) imisses
from v$latch
order by gets + immediate_gets
/

col name form A18 trunc
col gets form 999,999,990
col misses form 90.9 Heading "Miss %"
col cspins form A6 heading 'spin|sl06'
col csleep1 form A5 heading 'sl01|sl07'
col csleep2 form A5 heading 'sl02|sl08'
col csleep3 form A5 heading 'sl03|sl09'
col csleep4 form A5 heading 'sl04|sl10'
col csleep5 form A5 heading 'sl05|sl11'
col Interval form A12

set recsep off

select a.name
,a.gets gets
,a.misses*100/decode(a.gets,0,1,a.gets) misses
,to_char(a.spin_gets*100/decode(a.misses,0,1,a.misses),'990.9')||
to_char(a.sleep6*100/decode(a.misses,0,1
,a.misses),'90.9') cspins
,to_char(a.sleep1*100/decode(a.misses,0,1 ,a.misses),'90.9')||
to_char(a.sleep7*100/decode(a.misses,0,1
,a.misses),'90.9') csleep1
,to_char(a.sleep2*100/decode(a.misses,0,1 ,a.misses),'90.9')||
to_char(a.sleep8*100/decode(a.misses,0,1
,a.misses),'90.9') csleep2
,to_char(a.sleep3*100/decode(a.misses,0,1 ,a.misses),'90.9')||
to_char(a.sleep9*100/decode(a.misses,0,1
,a.misses),'90.9') csleep3
,to_char(a.sleep4*100/decode(a.misses,0,1 ,a.misses),'90.9')||
to_char(a.sleep10*100/decode(a.misses,0,1
,a.misses),'90.9') csleep4
,to_char(a.sleep5*100/decode(a.misses,0,1 ,a.misses),'90.9')||
to_char(a.sleep11*100/decode(a.misses,0,1
,a.misses),'90.9') csleep5
from v$latch a
where a.misses <> 0
order by 2 desc
/

select child#
   , ROUND(sleeps/gets*100,2)
   , ROUND( ((1 - sleeps/gets) * 100),2) ratio
fromv$latch_children
wherename = 'cache buffers lru chain'
/         

spool off




Database Hit Ratio:
------------------
CLEAR
SET HEAD ON
SET VERIFY OFF

col username form a10
col HitRatio format 999.99 heading 'Hit Ratio'
col CGets format 9999999999999 heading 'Consistent Gets'
col DBGets format 9999999999999 heading 'DB Block Gets'
col PhyGets format 9999999999999 heading 'Physical Reads'

select Username,
 v$sess_io.sid,
       consistent_gets,
       block_gets,
       physical_reads,
       100*(consistent_gets+block_gets-physical_reads)/
(consistent_gets+block_gets) HitRatio
from v$session, v$sess_io
where v$session.sid = v$sess_io.sid
and (consistent_gets+block_gets) > 0
and Username is NOT NULL
/

select 'Hit Ratio' Database,
       cg.value CGets,
       db.value DBGets,
       pr.value PhyGets,
       100*(cg.value+db.value-pr.value)/(cg.value+db.value) HitRatio
from v$sysstat db, v$sysstat cg, v$sysstat pr
where db.name = 'db block gets'
and cg.name = 'consistent gets'
and pr.name = 'physical reads'
/



Quick Database Performance Overview
-----------------------------------
set ver off
set echo off

spool snap.lst

select trunc(
            (1- (sum(decode(name,'physical reads',value,0))/
                 (sum(decode(name,'db block gets',value,0))
               + (sum(decode(name,'consistent gets',value,0))
            )))
            )*100
            ) "Buffer Hit Ratio"
from v$sysstat;

select a.value+b.value "Logical reads"
       ,c.value         "Physical Reads"
       ,d.value         "Physical Writes",
       round (100*((a.value+b.value)-c.value)
                         / (a.value+b.value)) "Buffer Hit Ratio" ,
       round(c.value*100/(a.value+b.value)) "% Missed"
from v$sysstat a,v$sysstat b,v$sysstat c, v$sysstat d
where a.name = 'consistent gets'
and   b.name = 'db block gets'
and   c.name = 'physical reads'
and   d.name = 'physical writes'
;

prompt
prompt Data Dictionary Hit Ratio
prompt

select sum(gets) "Data Dict. Gets",
       sum(getmisses) "Data Dict. Cache Misses",
       round((1-(sum(getmisses)/sum(gets)))*100) "DATA DICT CACHE HIT RATIO",
       round(sum(getmisses)*100/sum(gets)) "% MISSED"
from v$rowcache;

prompt
prompt Library Cache Miss Ratio
prompt

select sum(pins) "executions",
       sum(reloads) "Cache Misses",
       round((1-(sum(reloads)/sum(pins)))*100) "LIBRARY CACHE HIT RATIO",
       round(sum(reloads)*100/sum(pins)) "% Missed"       
from v$librarycache;

select namespace,
       trunc(gethitratio*100) "Hit Ratio",
       trunc(pinhitratio*100) "Pin Hit Ratio",
       reloads "Reloads"
from v$librarycache;

prompt
prompt Redo Log Buffer
prompt

select substr(name,1,30),value
from v$sysstat where name ='redo log space requests';

select pool,name,bytes from v$sgastat where name ='free memory';

select sum(executions) "Tot SQL since startup",
       sum(users_executing) "SQL executing now"
from v$sqlarea;

col c0 newline
set pages 0

select
  'Instance No : ' ||INSTANCE_NUMBER c0
, 'Instance Name: '|| INSTANCE_NAME       c0
, 'Host Name: '|| HOST_NAME          c0
, 'Version: '|| VERSION           c0
, 'Startup Time: '|| STARTUP_TIME     c0
, 'Status: ' || STATUS          c0
, 'Parallel: ' ||PARALLEL       c0
, 'Thread: ' || THREAD#       c0
, 'Archiver: ' || ARCHIVER          c0
, 'Logins : ' || LOGINS            c0
, 'Shutdown Pending : ' || SHUTDOWN_PENDING    c0
, 'Status : ' || DATABASE_STATUS    c0
, 'Instance Role : ' || INSTANCE_ROLE          c0
from v$instance;

set pages 200

prompt
prompt if miss_ratio or immediate_miss_ratio > 1 then latch
prompt contention exists, (decrease LOG_SMALL_ENTRY_MAX_SIZE

select substr(l.name,1,30) name,
       (misses/(gets+.001))*100 mis_ratio,
       (immediate_misses/(immediate_gets+.001))*100 miss_ratio
from v$latch l
where
  (misses/(gets+.001))*100 > .2
or
  (immediate_misses/(immediate_gets+.001))*100 > .2
order by l.name;      


prompt If these are < 1% of Total Number of Requests for Data
prompt then extra rollback segments may be needed.
select class,count
from v$waitstat
where class in ('free list','system undo header',
                'system undo block','undo header',
                'undo block')
group by class,count;

prompt Total Number of Requests for Data
select sum(value) from v$sysstat
where name in ('db block gets','consistent gets');

spool off



Free Memory in SGA
------------------
col value form 999,999,999,999 heading "Shared Pool Size"
col bytes form 999,999,999,999 heading "Free Bytes"
col percentfree form 999 heading "Percent Free"

spool freesga.lst

select  pool
        , to_number(v$parameter.value) value, v$sgastat.bytes,
(v$sgastat.bytes/v$parameter.value)*100 percentfree
from v$sgastat, v$parameter
wherev$sgastat.name = 'free memory'
andv$parameter.name = 'shared_pool_size';

spool off




Tables Recently Queried by a Full Table Scan
--------------------------------------------
set serverout on size 1000000
set verify off

col object_name form a30

PROMPT Column flag in x$bh table is set to value 0x80000, when
PROMPT block was read by a sequential scan.

spool bufferts.lst

SELECT o.object_name,o.object_type,o.owner
FROM dba_objects o,x$bh x
WHERE x.obj=o.object_id
AND o.object_type='TABLE'
AND standard.bitand(x.flag,524288)>0
AND o.owner<>'SYS';

spool off



DB Block Buffer Usage
---------------------
set serverout on size 1000000
set verify off

spool buffer.lst

select decode(state, 0, 'Free',
1, decode(lrba_seq,0,'Available','Being Modified'),
2, 'Not Modified',
3, 'Being Read',
'Other') "BLOCK STATUS"
,count(*) cnt
from sys.x$bh
group by decode(state, 0, 'Free',
1, decode(lrba_seq,0,'Available','Being Modified'),
2, 'Not Modified',
3, 'Being Read',
'Other')
/

set verify on
spool off




Cost Of Current Open Cursors
----------------------------
spool opencur.lst
set pagesize 66 linesize 132
set echo on

column executions      heading "Execs"      format 99999999
column rows_processed  heading "Rows Procd" format a20
column loads           heading "Loads"      format 999999.99
column buffer_gets     heading "Buffer Gets"
column sql_text        heading "SQL Text"   format a60 wrap
column avg_cost        heading "Avg Cost"   format 99999999
break on report

compute sum      of rows_processed     on report
compute sum      of executions         on report
compute sum  avg of loads              on report
compute avg      of avg_cost           on report

select rownum as rank, a.*
from (
   select buffer_gets, lpad(rows_processed ||
       decode(users_opening + users_executing, 0, ' ','*'),20) "rows_processed",
       executions, loads,
       (decode(rows_processed,0,1,1)) *
            buffer_gets/ decode(rows_processed,0,1,
                                  rows_processed) avg_cost,
      sqla.sql_text
from  v$sqlarea sqla, v$open_cursor oc
where sqla.hash_value = sqla.hash_value
and oc.address = sqla.address
and oc.hash_value = sqla.hash_value
order by 5 desc
) a
where rownum < 11
/                                                           

spool off





Session Hit Ratio
-----------------
col sid form 999 heading "Sid"
col username form a10 heading "Username"
col HitRatio form 999 heading Hit%
col consistent_gets form 999999999 heading "Cons Gets"
col physical_reads form 999999999 heading "Phys Reads"
col consistent_changes form  999999999 heading "Cons Changes"

spool sesshits.lst

select a.sid, a.username
              ,ROUND(
                 (b.consistent_gets+b.block_gets-b.physical_reads)
                       /(b.consistent_gets+b.block_gets) * 100,2) HitRatio
              , b.block_gets, b.consistent_gets,
b.physical_reads, b.block_changes,
b.consistent_changes
from v$session a, v$sess_io b
wherea.sid = b.sid
and (b.consistent_gets+b.block_gets) > 0
and Username is NOT NULL
order by a.sid, a.username
/
spool off




Session Details
---------------
spool sessdets.lst
set pages 200

col name form a30
col username form a12
col sid form 999
col owner form a12
col object form a20
col type form a10

prompt
prompt Objects Accessed per Session
prompt

select a.sid, a.username, b.*
from v$session a, v$access b
where a.sid = b.sid
order by a.sid
/
prompt
prompt Memory per Session
prompt

select a.sid, a.username, c.name, b.value
from v$session a, v$sesstat b, v$statname c
where a.sid = b.sid
and b.statistic# = c.statistic#
and c.name like 'session%memory'
order by a.sid
/
spool off




SQL Text Of Session Locks
-------------------------
set pagesize 66 linesize 132
set echo on

spool sqltext.lst

column sql_text        heading "SQL Text"   format a60 wrap
column object          heading "Object"   format a25 wrap
column username        heading "User"   format a10 wrap

select s.username username, 
       a.sid sid, 
       a.owner||'.'||a.object object, 
       s.lockwait, 
       t.sql_text sql_text
from   v$sqltext t, 
       v$session s, 
       v$access a
where  t.address=s.sql_address 
and    t.hash_value=s.sql_hash_value 
and    s.sid = a.sid 
and    a.owner != 'SYS'
and    upper(substr(a.object,1,2)) != 'V$'
order by t.piece
/



DB Info
-------
CLEAR
SET HEAD ON
SET VERIFY OFF
SET FEED OFF

alter session set nls_date_format='DD-MON-RR' ;

col instance_name form a8 Heading "Instance"
col name form a8 Heading "DB Name"
col host_name form a12 Heading "Hostname"

select dbid
,name
,instance_name
,host_name
,created
,log_mode
,open_mode
from v$database, v$instance
/

set feed on





Database Layout
---------------
set pages 200

column tablespace_name format a15
column file_name format a56
col Mb format 9,999 heading "Mb"

spool dblayout.lst

select file_name,tablespace_name,bytes/1024/1024 Mb
from dba_data_files
union all
select file_name,tablespace_name,bytes/1024/1024 Mb
from dba_temp_files
union all
select  member file_name,'log group '||a.group# tablespace_name
             ,b.bytes/1024/1024 Mb
from v$logfile a,v$log b
where a.group#=b.group#
union all
select name file_name,'control file' tablespace_name,0 Mb
from v$controlfile
order by 1,2;

spool off





Compare 2 databases (schema)
----------------------------
set echo off verify off

col owner format a10
col object_type format a20
col object_name format a30

set pages 100
set lines 120

accept dblink prompt 'Enter the name of the remote database : '
accept passw  prompt 'Enter system password : ' hide
accept own    prompt 'Enter the owner name you want to check objects of  for all: '

spool chk2dbs.lst

drop database link &dblink;

create database link &dblink
connect to system identified by &passw using '&dblink';

set heading off

select 'Objects in '||name||' which are not in &dblink'   from v$database ;

set heading on

select a.owner, a.object_type, a.object_name   from dba_objects a
 where a.owner not in ('SYS','SYSTEM') and a.owner like UPPER('&own%')
   and not exists (select b.object_name     from dba_objects@&dblink b
   where b.owner = a.owner     and b.object_type = a.object_type
     and b.object_name = a.object_name)
 order by a.owner, a.object_type, a.object_name
/

set heading off

select 'Objects in &dblink which are not in '||name  from v$database ;

set heading on

select a.owner, a.object_type, a.object_name
  from dba_objects@&dblink a
 where a.owner not in ('SYS','SYSTEM') and a.owner like UPPER('&own%')
   and not exists (select b.object_name     from dba_objects b
   where b.owner = a.owner     and b.object_type = a.object_type
     and b.object_name = a.object_name)
 order by a.owner, a.object_type, a.object_name
/

column owner format a8
column null_text format a8
column column_name format a30
column data_type form a10 heading "Datatype"
column data_length form 999 Heading "LEN"
column data_precision form 9999 Heading "PREC"
column data_scale form 99999 HEading "SCALE"
column column_id form 999 heading "ID"set heading off

select 'Differences in Columns in &dblink which are not in '||name
  from v$database ;

set heading on
select a.owner,a.column_name, a.data_type, a.data_length, a.data_precision,
          a.data_scale, decode(a.nullable,'N','NOT NULL',NULL) null_text
          ,a.column_id, a.default_length from dba_tab_columns@&dblink a
 where a.owner not in ('SYS','SYSTEM') and a.owner like UPPER('&own%')
minus
select b.owner,b.column_name, b.data_type, b.data_length, b.data_precision,
          b.data_scale, decode(b.nullable,'N','NOT NULL',NULL) null_text
          ,b.column_id, b.default_length from dba_tab_columns b
 where b.owner not in ('SYS','SYSTEM') and b.owner like UPPER('&own%')
/

set heading off
select 'Differences in Columns in '||name||' which are not in &dblink'
  from v$database ;

set heading on

select a.owner,a.column_name, a.data_type, a.data_length, a.data_precision,
          a.data_scale, decode(a.nullable,'N','NOT NULL',NULL) null_text
          ,a.column_id, a.default_length from dba_tab_columns a
 where a.owner not in ('SYS','SYSTEM') and a.owner like UPPER('&own%') minus
select b.owner, b.column_name, b.data_type, b.data_length, b.data_precision,
          b.data_scale, decode(b.nullable,'N','NOT NULL',NULL) null_text
          ,b.column_id, b.default_length from dba_tab_columns@&dblink b
 where b.owner not in ('SYS','SYSTEM') and b.owner like UPPER('&own%')
/

set heading off
select 'Differences in Constraints in &dblink which are not in '||name
  from v$database ;

set heading on

select a.owner,a.constraint_name,a.table_name
from  dba_constraints@&dblink a
 where a.owner not in ('SYS','SYSTEM') and a.owner like UPPER('&own%')
and   a.constraint_type in ('P','R') minus
select b.owner,b.constraint_name,b.table_name from  dba_constraints b
 where b.owner not in ('SYS','SYSTEM') and b.owner like UPPER('&own%')
and   b.constraint_type in ('P','R')
/

set heading off

select 'Differences in Constraints in '||name||' which are not in &dblink'
from v$database ;

set heading on

select a.owner,a.constraint_name,a.table_name
from  dba_constraints a
where a.owner not in ('SYS','SYSTEM') and a.owner like UPPER('&own%')
and   a.constraint_type in ('P','R') minus
select b.owner,b.constraint_name,b.table_name from  dba_constraints@&dblink b
where b.owner not in ('SYS','SYSTEM') and b.owner like UPPER('&own%')
and   b.constraint_type in ('P','R')
/

set heading off

select 'Differences in CHECK Constraints in &dblink which are not in '||name
  from v$database ;

set heading on

select a.owner,a.constraint_name,a.table_name
from  dba_constraints@&dblink a
where a.owner not in ('SYS','SYSTEM') and a.owner like UPPER('&own%')
and   a.constraint_type in ('C') and constraint_name not like 'SYS%'minus
select b.owner,b.constraint_name,b.table_name from  dba_constraints b
where b.owner not in ('SYS','SYSTEM') and b.owner like UPPER('&own%')
and   b.constraint_type in ('C') and constraint_name not like 'SYS%'
/

set heading off

select 'Differences in CHECK Constraints in '||name||' which are not in &dblink'
from v$database ;

set heading on

select a.owner,a.constraint_name,a.table_name
from  dba_constraints a
where a.owner not in ('SYS','SYSTEM') and a.owner like UPPER('&own%')
and   a.constraint_type in ('C') and constraint_name not like 'SYS%'minus
select b.owner,b.constraint_name,b.table_namefrom  dba_constraints@&dblink b
where b.owner not in ('SYS','SYSTEM') and b.owner like UPPER('&own%')
and   b.constraint_type in ('C') and constraint_name not like 'SYS%'
/

set heading off

select 'Differences in PROCEDURES in &dblink which are not in '||name
  from v$database ;

set heading on

select a.owner,a.name,a.type
from  dba_source@&dblink a
where a.owner not in ('SYS','SYSTEM') and a.owner = UPPER('&own%') minus
select b.owner,b.name,b.type from  dba_source b
where b.owner not in ('SYS','SYSTEM') and b.owner = UPPER('&own%')
/
set heading off
select 'Differences in PROCEDURES in '||name||' which are not in &dblink'
from v$database ;

set heading on

select a.owner,a.name,a.type from  dba_source a
where a.owner not in ('SYS','SYSTEM') and a.owner = UPPER('&own%') minus
select b.owner,b.name,b.type from  dba_source@&dblink b
where b.owner not in ('SYS','SYSTEM') and b.owner = UPPER('&own%')
/
REM drop database link &dblink;

spool off





Size SGA on current database
----------------------------
rem This is a sga utilisation script

set serverout on

spool sga.lst 

DECLARE

l_uplift CONSTANT NUMBER := 0.3;  /* i.e. 30% above calculated */

l_numusers NUMBER DEFAULT 50; /* Change this to a predicted number if not an  or set to 0 for actuals */
l_avg_uga NUMBER;
l_max_uga NUMBER;
l_sum_sql_shmem NUMBER;
l_sum_obj_shmem NUMBER;
l_total_avg NUMBER;
l_total_max NUMBER;
BEGIN
dbms_output.enable(20000);

IF ( l_numusers = 0) THEN
   SELECT sessions_highwater
   INTO l_numusers
   FROM v$license;
   dbms_output.put_line('Maximum concurrent users on this database = '
||TO_CHAR(l_numusers));
ELSE
   dbms_output.put_line('Approximating SGA for = '
||TO_CHAR(l_numusers)||' concurrent users');
END IF;
dbms_output.new_line;

-- MTS user memory on a per user basis
SELECT
    avg(value)*l_numusers
   ,max(value)*l_numusers
INTO l_avg_uga, l_max_uga
FROM v$sesstat s, v$statname n
WHERE s.statistic# = n.statistic#
AND n.name = 'session uga memory max';

dbms_output.put_line('AVG MTS Mem on '||to_char(l_numusers)
                            ||' Users: '||to_char(l_avg_uga) );
dbms_output.put_line('MAX MTS Mem on '||to_char(l_numusers)
                            ||' Users: '||to_char(l_max_uga) );

SELECT
    sum(sharable_mem) INTO l_sum_sql_shmem
FROM v$sqlarea;

dbms_output.put_line('Shared SQL Memory : '||to_char(l_sum_sql_shmem) );

SELECT
    sum(sharable_mem) INTO l_sum_obj_shmem
FROM v$db_object_cache;

dbms_output.put_line('Object Memory : '||to_char(l_sum_obj_shmem) );

l_total_avg := l_avg_uga + l_sum_sql_shmem + l_sum_obj_shmem;
l_total_max := l_max_uga + l_sum_sql_shmem + l_sum_obj_shmem;

dbms_output.put_line('Current Shared_pool size between :'
|| TO_CHAR(ROUND(l_total_avg  + (l_total_avg * l_uplift), 0) )
||' and '
|| TO_CHAR(ROUND(l_total_max + (l_total_max * l_uplift), 0) )
||' bytes');

dbms_output.put_line('Current Shared_pool size between :'
|| TO_CHAR(ROUND(
(l_total_avg + (l_total_avg * l_uplift)) /(1024*1024), 0) )
||' and '
|| TO_CHAR(ROUND(
(l_total_max + (l_total_max * l_uplift )) /(1024*1024) ,0) )
||' M bytes');
end;
/

spool off




Monitor database deadlocks
--------------------------
col username form A15
col sid form 9990
col type form A4
col lmode form 990
col request form 990
col id1 form 9999990
col id2 form 9999990

break on id1 skip 1 dup
spool check_lock.lis

SELECT sn.username, m.sid, m.type,
   DECODE(m.lmode, 0, 'None'
                 , 1, 'Null'
                 , 2, 'Row Share'
                 , 3, 'Row Excl.'
                 , 4, 'Share'
                 , 5, 'S/Row Excl.'
                 , 6, 'Exclusive'
                 , lmode, ltrim(to_char(lmode,'990'))) lmode,
   DECODE(m.request, 0, 'None'
                 , 1, 'Null'
                 , 2, 'Row Share'
                 , 3, 'Row Excl.'
                 , 4, 'Share'
                 , 5, 'S/Row Excl.'
                 , 6, 'Exclusive'
                 , request, ltrim(to_char(request,'990'))) request,
         m.id1,m.id2
FROM v$session sn, V$lock m
WHERE (sn.sid = m.sid AND m.request != 0)
   OR (sn.sid = m.sid
      AND m.request = 0 AND lmode != 4
      AND (id1, id2 ) IN (SELECT s.id1, s.id2
                          FROM v$lock s
                          WHERE request != 0
                                 AND s.id1 = m.id1
                                 AND s.id2 = m.id2 )
      )
ORDER BY id1,id2, m.request;
spool off
clear breaks





Monitor All Database Locks
--------------------------
set pages 20
col username form A10
col sid form 9990
col type form A4
col lmode form 990
col request form 990
col objname form A15 Heading "Object Name"
rem Display the object id's if the object_name is not unique
rem col id1 form 999999900  
rem col id2 form 999999900

spool check_lock.lis

SELECT sn.username, m.sid, m.type,
   DECODE(m.lmode, 0, 'None'
                 , 1, 'Null'
                 , 2, 'Row Share'
                 , 3, 'Row Excl.'
                 , 4, 'Share'
                 , 5, 'S/Row Excl.'
                 , 6, 'Exclusive'
                 , lmode, ltrim(to_char(lmode,'990'))) lmode,
   DECODE(m.request, 0, 'None'
                 , 1, 'Null'
                 , 2, 'Row Share'
                 , 3, 'Row Excl.'
                 , 4, 'Share'
                 , 5, 'S/Row Excl.'
                 , 6, 'Exclusive'
                 , request, ltrim(to_char(request,'990'))) request,
         obj1.object_name objname, obj2.object_name objname
FROM v$session sn, V$lock m, dba_objects obj1, dba_objects obj2
WHERE sn.sid = m.sid
AND m.id1 = obj1.object_id (+)
AND m.id2 = obj2.object_id (+)
     AND lmode != 4
ORDER BY id1,id2, m.request
/

spool off
clear breaks




Monitor Rollback Segments
-------------------------
set pages 200
col user0 form a15
col comm0 form a15
col name0 form a10
col extents0 form 999 Heading "Extents"
col shrinks0 form 999 Heading "Shrinks"
col waits form 9999 heading "Wraps"

select
       rn.name    name0,
       s.username user0,
       r.rssize ,
       r.waits,
       r.extents  extents0,
       r.shrinks shrinks0,
 r.optsize,
       decode (s.command,1,'CREATE TABLE',
                         2,'INSERT',
                         3,'SELECT',
                         6,'UPDATE',
                         7,'DELETE',
                         9,'CREATE INDEX',
                        10,'DROP INDEX',
                        12,'DROP INDEX',
                        26,'LOCK TABLE',
                        44,'COMMIT',
                        45,'ROLLBACK',
                        46,'SAVEPOINT',
                        48,'SET TRANSACTION',
                        NULL, NULL,
                        'look it up '||to_char(s.command)) comm0
from v$session s, v$transaction t, v$rollstat r, v$rollname rn
where s.taddr (+) = t.addr
and t.xidusn (+) = r.usn
and rn.usn = r.usn
order by rn.name
/
SELECT   rn.name name0
 , p.pid
         ,p.spid
         , NVL (p.username, 'NO TRANSACTION') user0
         , p.terminal
  FROM v$lock l, v$process p, v$rollname rn
  WHERE    l.sid = p.pid(+)
  AND      TRUNC (l.id1(+)/65536) = rn.usn
  AND      l.type(+) = 'TX'
  AND      l.lmode(+) = 6
  ORDER BY rn.name;

PROMPT
PROMPT  Time since last WRAP
PROMPT
select n.name
       , round(
           24*((sysdate-startup_time) - trunc(sysdate-startup_time)) / 
                          (s.writes/s.rssize),1) "Hours"
from v$instance ,v$rollname n,v$rollstat s
where n.usn = s.usn
and s.status = 'ONLINE'
/
spool off





Put Database in Archive Log Mode
--------------------------------
#!/bin/ksh
DATABASE=$1
USERPASS="/ as sysdba"

# Check that we have a database
if [ -z "${DATABASE}" ]
then
   echo
   echo "No Database Specified !"
   echo
   echo "Usage : set_archivelog.ksh "
   echo
   echo "where ORACLE_SID (Mandatory) - SID of database to start"
   echo
   echo " e.g.  $ set_archivelog.ksh PROD"
   echo
   exit 1
fi

ORACLE_SID=$DATABASE
export ORACLE_SID
export ORAENV_ASK=NO
. oraenv      

if [ ! -d "${ORACLE_HOME}" ]
then
  echo
  echo "$ORACLE_HOME does not exist !"
  echo
  exit 1
fi

#
# SHUT THE DATABASE DOWN
#
sqlplus "${USERPASS}" << EOF
shutdown immediate
exit
EOF

sqlplus "${USERPASS}" << EOF
startup mount exclusive;
alter database archivelog;
alter database open;
archive log start
archive log list
exit
EOF

exit 0





