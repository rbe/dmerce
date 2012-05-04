-- #############################################################################################
--
-- %Purpose: Guide for Tuning with UTLBSTAT und UTLESTAT
--
-- #############################################################################################

***********************************
1). Select Library cache statistics
***********************************

The library cache is where Oracle stores object definitions, SQL
statements etc. Each namespace (or library) contains different
types of object. The figures here give a quick summary of the
usage of each namespace. The most useful indicator here is the
RELOADS column.

Nearly no reloads, shared pool is OK.

****************************
2). CPU used by this session
****************************

By comparing the relative time spent waiting on each wait event
and the CPU used by this session, we can see where the Oracle
instance is spending most of its time.

Statistic                   Total        Per Transact Per Logon    Per Second
--------------------------- ------------ ------------ ------------ ------------
CPU used by this session          542660         1.18      2647.12        95.22
parse time cpu                       244            0         1.19          .04

***********************************
3). Locating CPU-Heavy SQL (Top 15)
***********************************

The statement below finds SQL statements which access database buffers
a lot. Such statements are typically heavy on CPU usage because they are
probably looking at a lot of rows of data:

SELECT disk_reads, buffer_gets, executions,
       buffer_gets/executions "Gets/Exec",
       disk_reads/executions "Reads/Exec",
       round((buffer_gets - disk_reads) / buffer_gets, 2) * 100 "HitRatio",
       sql_text
 FROM v$sqlarea
 WHERE buffer_gets > 10000000
   AND executions > 1000000
ORDER BY buffer_gets DESC;

Disk-Reads Buffer-Gets Executions  Gets/Exec Reads/Exec   HitRatio
---------- ----------- ---------- ---------- ---------- ----------
     14607    40882602    3211863 12.7286257 .004547828        100
SQL-Text
---------------------------------------------------------------------------
UPDATE SSCREQUEST SET MSGSTATE=:b1,RESPONSE=:b2,SERVICESTATE=:b3,RETURNCODE
=:b4,INIPARA=:b5,PROCESSTIME=SYSDATE WHERE REQUESTID=:b6

Disk-Reads Buffer-Gets Executions  Gets/Exec Reads/Exec   HitRatio
---------- ----------- ---------- ---------- ---------- ----------
    188249    34923996    1528920 22.8422651 .123125474         99
SQL-Text
---------------------------------------------------------------------------
UPDATE SSCORDER SET STATE=:b1,RETURNCODE=:b2,PRIORITY=:b3,PROCESSTIME=SYSDA
TE,DEFERRED=TO_DATE(:b4,'YYYYMMDDHH24MISS')WHERE REQUESTID=:b5

**********************************************************
4). Locating CPU-Heavy Sessions (CPU used by this session)
**********************************************************

SELECT substr(a.sid,1,5) "Sid",
       substr(a.process,1,7) "Process",
       substr(a.username,1,20) "User",
       v.value "CPU used by this session"
  FROM v$statname s, v$sesstat v, v$session a
 WHERE s.name = 'CPU used by this session'
   and v.statistic#=s.statistic#
   AND v.sid = a.sid
   and v.value > 1000
ORDER BY v.value DESC;

Sid   Process User                 CPU used by this session
----- ------- -------------------- ------------------------
62    12602   SSCDSP                                2791153
82    10949   SSCSMH                                1591242
103   12603   SSCDSP                                 191758
110   16629   DISPATCHER                              50677
83    9913    SMH_LINK                                48296
69    12430   SSCFSM                                  44123
59    12265   SSCFSM                                  43812
60    10997   SSCFSM                                  43722
51    16628   DISPATCHER                              35865
71    11017   SSCSMH                                  33881
53    12138   SSCFSM                                  27938

**********************
5). Common Wait Events
**********************

System wide wait events for NON-background processes (PMON,
SMON, etc).  Times are in hundreths of seconds.  Each one of
these is a context switch which costs CPU time.  By looking at
the Total Time you can often determine what is the bottleneck
that processes are waiting for.  This shows the total time spent
waiting for a specific event and the average time per wait on
that event.

Ignore any 'idle' wait events. Common IDLE wait events include:

client message                 - Process waiting for data from the
SQL*Net message from client      client application
SQL*Net more data from client
rdbms ipc message              - Usually background process waiting for work
pipe get                       - DBMS_PIPE read waiting for data
Null event                     - Miscellaneous
pmon timer                     - PMON waiting for work
smon timer                     - SMON waiting for work
parallel query dequeue         - Waiting for input (Discussed later)

Event Name                       Count         Total Time    Avg Time
-------------------------------- ------------- ------------- -------------
log file sync                           527031       2076364          3.94
db file sequential read                 379096        948531           2.5
write complete waits                      1549         90879         58.67
db file scattered read                  508925         55312           .11
buffer busy waits                         6070         44408          7.32
log file switch completion                 276         13875         50.27
latch free                                2828          5517          1.95
enqueue                                    284          2498           8.8
log buffer space                           102           753          7.38
buffer deadlock                             24            48             2
control file sequential read               134            16           .12

-----------------------------------------
System wide waits for "buffer busy waits"
-----------------------------------------
This wait happens when a session wants to access a database
block in the buffer cache but it cannot as the buffer is "busy".
The two main cases where this can occur are:

- Another session is reading the block into the buffer
- Another session holds the buffer in an incompatible mode to our request

If the TIME spent waiting for buffers is significant then we need to
determine which segment/s is/are suffering from contention.
The "Buffer busy wait statistics" section of the Bstat/estat shows
which block type/s are seeing the most contention. This information
is derived from the V$WAITSTAT which can be queried in isolation.
This shows the class of block with the most waits at the BOTTOM of the list.

SELECT time, count, class
  FROM V$WAITSTAT
ORDER BY time,count;

      TIME      COUNT CLASS
---------- ---------- ------------------
         0          0 sort block
         0          0 save undo block
         0          0 save undo header
         0          0 free list
         0          0 system undo block
         0          0 system undo header
      5812        442 segment header
    506921      35983 undo block
    561185      30039 undo header
    922683     203757 data block

Additional information can be obtained from the internal
view X$KCBFWAIT thus:

SELECT count, file#, substr(name,1,50)
  FROM x$kcbfwait, v$datafile
 WHERE indx + 1 = file#
 ORDER BY count;

     COUNT      FILE# SUBSTR(NAME,1,50)
---------- ---------- --------------------------------------------------
         0          3 /data/ota/db1/OTASICAP/data/temp01.dbf
         0          4 /data/ota/db1/OTASICAP/data/users.dbf
         0         16 /data/ota/db2/OTASICAP/data/idx_sccms_02.dbf
         0         17 /data/ota/db1/OTASICAP/data/tbs_sccorder_03.dbf
         0          5 /data/ota/db1/OTASICAP/data/tools.dbf
         1          1 /data/ota/db1/OTASICAP/data/system01.dbf
         9         13 /data/ota/db1/OTASICAP/data/tbs_sccms_01.dbf
        14         14 /data/ota/db1/OTASICAP/data/tbs_sccms_02.dbf
        36         15 /data/ota/db2/OTASICAP/data/idx_sccms_01.dbf
        52         12 /data/ota/db2/OTASICAP/data/idx_sccorder_03.dbf
      1181         11 /data/ota/db2/OTASICAP/data/idx_sccorder_02.dbf
      5337          7 /data/ota/db2/OTASICAP/data/idx_01.dbf
      6535          8 /data/ota/db1/OTASICAP/data/tbs_sccorder_01.dbf
      7838         10 /data/ota/db2/OTASICAP/data/idx_sccorder_01.dbf
     11479          9 /data/ota/db1/OTASICAP/data/tbs_sccorder_02.dbf
     19028          2 /data/ota/db1/OTASICAP/data/rbs01.dbf
     47124         18 /data/ota/db2/OTASICAP/data/rbs02.dbf
    171997          6 /data/ota/db1/OTASICAP/data/data01.dbf

This shows the file/s with the most waits (at the BOTTOM of the list)
so by combining the two sets of information we know what block
type/s in which file/s are causing waits.
The segments in each file can be seen using a query like:

SELECT distinct owner, segment_name, segment_type
  FROM dba_extents
 WHERE file_id = &FILE#;

For File-Nr 6:

SELECT distinct owner "Owner",
       substr(segment_name,1,30) "Segment-Name",
       substr(segment_type,1,20) "Segment-Type"
  FROM dba_extents
 WHERE file_id = 6;

Owner                          Segment-Name                   Segment-Type
------------------------------ ------------------------------ -----------------
DISPATCHER                     ARCHIVIO_SSC_SMS_CCBS          TABLE
DISPATCHER                     IDX_MSISDN_RELOADFIX           INDEX
DISPATCHER                     RELOADFIX                      TABLE
DISPATCHER                     SSCMS_MSCOUNT_5                TABLE
DISPATCHER                     SSC_CHANGES                    TABLE
DISPATCHER                     SSC_SMS_CCBS                   TABLE
DISPATCHER                     SSC_SMS_CCBS_OLD               TABLE
ORASTAT                        SEGMENT_INFO                   TABLE
SYSADM                         BKP_99_STATE_SSCORDER          TABLE
SYSADM                         SSCAPPS                        TABLE
SYSADM                         SSCCDR                         TABLE
SYSADM                         SSCCONF                        TABLE
SYSADM                         SSCREQUEST                     TABLE
SYSADM                         SSCRESPONSE                    TABLE
SYSADM                         SSCSMBUF                       TABLE

If there are a large number of segments of the type listed then
monitoring V$SESSION_WAIT may help isolate which object is causing
the waits. Repeatedly run the following statement and collect the
output. After a period of time sort the results to see which
file & blocks are showing contention:

SELECT p1 "File", p2 "Block", p3 "Reason"
 FROM v$session_wait
 WHERE event='buffer busy waits';

      File      Block     Reason
---------- ---------- ----------
        18      79362       1012
        18      67842       1016

      File      Block     Reason
---------- ---------- ----------
         6      10526       1016

-----------------------------------------------
System wide waits for "db file sequential read"
-----------------------------------------------
This wait happens when a session is waiting for an IO to complete.
Typically this indicates single block reads, although reads from
a disk sort area may use this wait event when reading several
contiguous blocks. Remember IO is a normal activity so you are
really interested in unnecessary or slow IO activity.

==> Tune SQL Statements

-----------------------------------------------------------------
System wide waits for "db file scattered read" (FULL TABLE SCANS)
-----------------------------------------------------------------
This wait happens when a session is waiting for a multiblock
IO to complete. This typically occurs during FULL TABLE SCANS
or index fast full scans. Oracle reads up to
DB_FILE_MULTIBLOCK_READ_COUNT consecutive blocks at a time
and scatters them into buffers in the buffer cache.
How this is done depends on the value of USE_READV.
If the TIME spent waiting for multiblock reads is significant
then we need to determine which segment/s we are performing
the reads against. See the "Tablespace IO" and "File IO"
sections of the ESTAT report to get information on which
tablespaces / files are servicing multiblock reads (BLKS_READ/READS>1).

It is probably best at this stage to find which sessions
are performing scans and trace them to see if the scans
are expected or not. This statement can be used to see
which sessions may be worth tracing:

SELECT substr(a.sid,1,5) "Sid",
       substr(a.process,1,7) "Process",
       substr(a.username,1,20) "User",
       total_waits, time_waited
  FROM v$session_event v, v$session a
 WHERE v.event='db file scattered read'
   and v.total_waits > 0
   and a.sid = v.sid
 ORDER BY v.total_waits DESC;

Sid   Process User                 TOTAL_WAITS TIME_WAITED
----- ------- -------------------- ----------- -----------
109   12533   SMH_LINK                  136172        6401
111   10391   DISPATCHER                 30045        3390
120   31320   VP_LINK                     5792         789
75    4214116 VP_LINK                     5760         371
101   4290951 SYSADM                      1582         462
85    11165   SYSADM                      1566         522
6     9832                                 570         840
82    10949   SSCSMH                       179          88
108   52360   VP_LINK                       85          65
115   8781    SYS                           14           2

USE_READV can have a dramatic effect on the performance of table scans.
On many platforms USE_READV=FALSE performs better than TRUE but
this should be tested.
DB_FILE_MULTIBLOCK_READ_COUNT should generally be made as large
as possible. The value is usually capped by Oracle and so it
cannot be set too high. The 'capped' value differs between platforms
and versions and usually depends on the settings of DB_BLOCK_SIZE
and USE_READV.

Current Settings

use_readv                           boolean FALSE
db_file_multiblock_read_count       integer 32

----------------------------------------------
System wide waits for "enqueue" (local 'lock')
----------------------------------------------
A wait for an enqueue is a wait for a local 'lock'. The count and
average wait times for this wait-event can be misleading as "enqueue"
waits re-arm every few seconds. To qualify how many waits have really
occurred you need the enqueue waits statistic from the statistics
section of the estat report.

To determine which enqueues are causing the most waits system-wide
look at View X$KSQST

SELECT  ksqsttyp "Lock",
        ksqstget "Gets",
        ksqstwat "Waits"
  FROM X$KSQST where KSQSTWAT>0;

Lo       Gets      Waits
-- ---------- ----------
CF       3720          2
CI       1936          2
CU        379          1
DX   23582708          7
ST       4591         20
TX   19710183      16189

TX  Transaction Lock

Generally due to application or table setup issues

TM  DML enqueue

Generally due to application issues, particularly if
foreign key constraints have not been indexed.

ST Space management enqueue

Usually caused by too much space management occurring
(Eg: small extent sizes, lots of sorting etc..)

----------------------------------
System wide waits for "latch free"
----------------------------------
Latches are like short duration locks that protect critical
bits of code. As a latch wait is typically quite short it is
possible to see a large number of latch waits which only
account for a small percentage of time.
If the TIME spent waiting for latches is significant then we
need to determine which latches are suffering from contention.
The Bstat/estat section on latches shows latch activity in
the period sampled. This section of the estat report is based
on View V$LATCH gives a summary of latch activity since instance
startup and can give an indication of which latches are responsible
for the most time spent waiting for "latch free" thus:

SELECT latch#, substr(name,1,30) gets, misses, sleeps
  FROM v$latch
 WHERE sleeps>0
 ORDER BY sleeps DESC;

    LATCH# GETS                               MISSES     SLEEPS
---------- ------------------------------ ---------- ----------
        11 cache buffers chains              1619686     103490
        18 redo allocation                   1934934       8607
        37 global tx hash mapping              33542       4493
        39 library cache                      325559       2555
        19 redo copy                           18711       2326
         8 enqueues                           593898       1781
         2 session allocation                 337507       1506
        15 cache buffers lru chain             23440       1411
         9 enqueue hash chains                 98115       1069
        29 undo global data                   264600        791
        16 system commit number               210693        752
        27 transaction allocation              87931        461
         7 messages                           113626        315
        25 dml lock allocation                 29710        206
        26 list of block allocation            29313        176
         4 session idle bit                    34286        155
        13 multiblock read objects              5452         35
        32 row cache objects                     326          9
        35 global tx free list                  1956          7
         6 modify parameter values                 2          3
        38 shared pool                            31          3
         0 latch wait list                       219          2
        50 parallel query stats                    2          1

cache buffers chains latches

Individual block contention can show up as contention for one of these
latches.  Each cache buffers chains latch covers a list of buffers in
the buffer cache.

---------------------------------------------
System wide waits for "log file space/switch"
---------------------------------------------
There are several wait events which may indicate problems with
the redo buffer and redo log throughput:

log buffer space
log file switch (checkpoint incomplete)
log file switch (archiving needed)
log file switch/archive
log file switch (clearing log file)
log file switch completion
switch logfile command

Increase the LOG_BUFFER size until there is no incremental
benefit (sizes > 1Mb are unlikely to add any benefit)

For all other waits:

- Ensure redo logs are on fast disks (NOT RAID5)
- Ensure redo logs are large enough to give a sensible gap
  between log switches. A 'rule-of-thumb' is to have one log
  switch every 15 to 30 minutes.
- Ensure the ARCHiver process is running and keeping up.

-------------------------------------
System wide waits for "log file sync"
-------------------------------------
This wait happens when a commit (or rollback) is issued and
the session has to wait for the redo entry to be flushed to
disk to guarantee that instance failure will not roll back the transaction.

- Where possible reduce the commit frequency.
  Eg: Commit at batch intervals and not per row.
- Speed up redo writing (Eg: Do NOT use RAID 5, use fast disks etc..)

--------------------------------------------
System wide waits for "write complete waits"
--------------------------------------------
This wait happens when a requested buffer is being written to disk
we cannot use the buffer while it is being written.
If the TIME spent waiting for buffers to be written is
significant then note the "Average Write Queue Length"
if this too is large then the the cache aging rate is
too high for the speed that DBWR is writing buffers to disk.

- Decrease the cache aging rate
- Increase the throughput of DBWR

**********************
5). Dirty Queue Length
**********************

The average write queue length gives an indication of whether the cache
is being cleared of dirty blocks fast enough or not. Unless the system
is very high throughput the write queue should never be very large.
As a general guideline if the average write queue is in double figures
it is certainly worth finding where the activity is coming from.
If the figure is in single digits there may still be un-necessary
activity in the cache.

The 2 main ways to improve the write queue length are:

Eliminate any unnecessary write IO from going via the cache.
Improve DBWR throughput. Eg: Use asynchronous writes,
multiple database writer processes, larger block write batch.

Anything above 100 indicates that the DBWR is
having real problems keeping up !

SELECT SUM(DECODE(name,'summed dirty queue length',value)) /
       SUM(DECODE(name,'write requests',value)) "Average Write Queue Length"
  FROM v$sysstat
 WHERE name IN ( 'summed dirty queue length','write requests')
   AND value > 0;

Average Write Queue Length (Should be 0)
----------------------------------------
0.35044

*****************************
6). Tablespace IO and File IO
*****************************

TABLE_SPACE      Tablespace name
FILE_NAME        File name in this tablespace
READS            Number of read calls to the OS
BLKS_READ        Number of blocks read.
                   The above two differ only when multi-block
                   reads are being performed. Eg: On full table
                   scans we read up to DB_FILE_MULTIBLOCK_READ_COUNT
                   blocks per read.
READ_TIME        Total time for the reads in 1/100ths of a second
WRITES           Number of OS write calls
BLKS_WRT         Number of blocks written
WRITE_TIME       Write time in 1/100ths of a second. NB: this figure
                 may be incorrect.

TABLE_SPACE       READS   BLKS_READ  READ_TIME  WRITES     BLKS_WRT   WRITE_TIME MEGABYTES
----------------- ------- ---------- ---------- ---------- ---------- ---------- ----------
DATA                24487      88794       4713      17011      17011     531654        524
IDX                  6844       6844      26326      38219      38219    1454912        524
RBS                     2          2          2     102702     102702    5333108       1598
SYSTEM                101        152         71         97         97       3542        105
TBS_SCCORDER       684066    6344009      86376      46697      46697    2081706       3222
TBS_SCCORDER_IDX   125173     125174     847903     228494     228494   12198710       3222
TBS_SSCMS           28801      28801      41554      39439      39439    1235851       2148
TBS_SSCMS_IDX       20732      20732       5126        231        231       6541       2148
TEMP                   51        725         27         89        678        242       1049
TOOLS                   0          0          0          0          0          0        105
USERS                   0          0          0         15         15        410        105

Read-Time per Block in [MS] = ((READ_TIME/100) / BLKS_READ) * 1000

TBS_SCCORDER:     ((86'376/100)/6'344'009)*1000 = 0.13 ms (very fast)
TBS_SCCORDER_IDX: ((847'903/100)/125'174)*1000 = 67.73 ms (very slow)
TBS_SSCMS:        ((41'554/100)/28'801)*1000 =   14.43 ms (OK)
TBS_SSCMS_IDX:    ((5'126/100)/)20'732*1000 =     2.47 ms (very fast)

TABLE_SPACE       FILE_NAME                                        READS      BLKS_READ  READ_TIME  WRITES     BLKS_WRT   WRITE_TIME MEGABYTES
----------------- ------------------------------------------------ ---------- ---------- ---------- ---------- ---------- ---------- ----------
DATA              /data/ota/db1/OTASICAP/data/data01.dbf                24487      88794       4713      17011      17011     531654        524
IDX               /data/ota/db2/OTASICAP/data/idx_01.dbf                 6844       6844      26326      38219      38219    1454912        524
RBS               /data/ota/db1/OTASICAP/data/rbs01.dbf                     1          1          0      23922      23922     854404        524
RBS               /data/ota/db2/OTASICAP/data/rbs02.dbf                     1          1          2      78780      78780    4478704       1074
SYSTEM            /data/ota/db1/OTASICAP/data/system01.dbf                101        152         71         97         97       3542        105
TBS_SCCORDER      /data/ota/db1/OTASICAP/data/tbs_sccorder_01.dbf      242174    2841155      34210      24178      24178     806926       1074
TBS_SCCORDER      /data/ota/db1/OTASICAP/data/tbs_sccorder_02.dbf      282835    2075092      41336      22519      22519    1274780       1074
TBS_SCCORDER      /data/ota/db1/OTASICAP/data/tbs_sccorder_03.dbf      159057    1427762      10830          0          0          0       1074
TBS_SCCORDER_IDX  /data/ota/db2/OTASICAP/data/idx_sccorder_01.dbf       23354      23354      49920      52053      52053    1963087       1074
TBS_SCCORDER_IDX  /data/ota/db2/OTASICAP/data/idx_sccorder_02.dbf       84367      84368     743566     124286     124286    8090752       1074
TBS_SCCORDER_IDX  /data/ota/db2/OTASICAP/data/idx_sccorder_03.dbf       17452      17452      54417      52155      52155    2144871       1074
TBS_SSCMS         /data/ota/db1/OTASICAP/data/tbs_sccms_01.dbf          12889      12889      19041      17960      17960     562416       1074
TBS_SSCMS         /data/ota/db1/OTASICAP/data/tbs_sccms_02.dbf          15912      15912      22513      21479      21479     673435       1074
TBS_SSCMS_IDX     /data/ota/db2/OTASICAP/data/idx_sccms_01.dbf          20428      20428       4738        184        184       4997       1074
TBS_SSCMS_IDX     /data/ota/db2/OTASICAP/data/idx_sccms_02.dbf            304        304        388         47         47       1544       1074
TEMP              /data/ota/db1/OTASICAP/data/temp01.dbf                   51        725         27         89        678        242       1049
TOOLS             /data/ota/db1/OTASICAP/data/tools.dbf                     0          0          0          0          0          0        105
USERS             /data/ota/db1/OTASICAP/data/users.dbf                     0          0          0         15         15        410        105

**********************
7). Fragmented Objects
**********************

set feed off
set pagesize 5000
break on owner skip 1 on tablespace_name on segment_type
column datum new_value datum noprint
column owner format A10 heading 'Owner'
column tablespace_name format A20 heading 'Tablespace'
column segment_type format A10 heading 'Segment-|Type'
column segment_name format A30 heading 'Segment-|Name'
column extent_id format 999 heading 'Number of|Extents'
column bytes format 999,999,999,999 heading 'Size|[Bytes]'
--
-- Looking for frgmented objects
--
select  to_char(sysdate, 'MM/DD/YY') datum,
    owner,
    tablespace_name,
    segment_type,
    segment_name,
    count(extent_id) extent_id,
    sum(bytes) bytes
  from sys.dba_extents
 where substr(owner,1,10) not in ('SYS')
group by
  owner,
  tablespace_name,
  segment_type,
  segment_name
having count(extent_id) > 3
order by 1,2,3,4;

Owner      Tablespace           Type       Name                             Extents          [Bytes]
---------- -------------------- ---------- ------------------------------ --------- ----------------
DISPATCHER IDX                  INDEX      PK_SSC_SMS_CCBS                        4       20,971,520
           TBS_SCCORDER         TABLE      SSCORDER                               4      419,430,400
           TBS_SCCORDER_IDX     INDEX      IDX_SSCORDER_HISTORY_MSISDN            4      419,430,400

SYSADM     TBS_SSCMS            TABLE      SSCMS                                  4    2,076,172,288

*************************************************
8). Sessions with bad Buffer Cache Hit Ratio in %
*************************************************

set feed off;
set pagesize 10000;
set wrap off;
set linesize 200;
set heading on;
set tab on;
set scan on;
set verify off;
--
ttitle left 'Show Sessions with bad Buffer Cache Hit Ratio in %' -
skip 2
--
select substr(a.username,1,12) "User",
       a.sid "sid",
       b.consistent_gets "ConsGets",
       b.block_gets "BlockGets",
       b.physical_reads "PhysReads",
       100 * round((b.consistent_gets + b.block_gets - b.physical_reads) /
             (b.consistent_gets + b.block_gets),3) HitRatio
  from v$session a, v$sess_io b
 where a.sid = b.sid
   and (b.consistent_gets + b.block_gets) > 0
   and a.username is not null
order by HitRatio asc;

Show Sessions with bad Buffer Cache Hit Ratio in %

User                sid   ConsGets  BlockGets  PhysReads   HITRATIO
------------ ---------- ---------- ---------- ---------- ----------
SYSADM              101      45719          6      38640       15.5
DISPATCHER          111     466434         16     390966       16.2
VP_LINK              95      94058          6      65802         30
VP_LINK              75      94150          4      43350         54
DISPATCHER           84    1288381         28     470437       63.5
VP_LINK              40     436526          2     116192       73.4
VP_LINK              90     437587          2     112440       74.3
SMH_LINK            109    1920356    1345626     799163       75.5
SSCSMSCI             68          5          0          1         80
DISPATCHER           94         52          8          9         85
SYS                 115      58443       6811       6845       89.5
VP_LINK              88         19          2          2       90.5
VP_LINK              72     374313     998401      78614       94.3
VP_LINK              89     373818     997023      78529       94.3
VP_LINK              87     373435     995770      78406       94.3
VP_LINK              97     373546     996162      78587       94.3
VP_LINK              93     374399     998969      78478       94.3
VP_LINK              80       7048      18880       1444       94.4
VP_LINK              70       1813       4768        364       94.5
VP_LINK              92       1851       4952        369       94.6
VP_LINK              86       1870       4994        362       94.7
MIG_OTA              91       1113          8         46       95.9
VP_LINK             104         21         14          1       97.1
VP_LINK              98         56          2          1       98.3
VP_LINK              96        239        578         13       98.4
DISPATCHER          110    1092370    3875255      59535       98.8
SSCSMH              105      59195      35151       1035       98.9
TMH_LINK             77        842       2086         30         99
VP_LINK             114     387838     853135      11724       99.1
SSCSMH               37     984588     543820      11500       99.2
SSCSMH               54    2318348    1304849      20178       99.4
SSCFSM               52    2938795    2408492      25907       99.5
SSCFSM               53    2933194    2403007      25963       99.5
SSCSMH               71    2835536    1592916      23697       99.5
SSCFSM               69    2938692    2407723      26245       99.5
SSCFSM               60    2934409    2404253      25990       99.5
SSCFSM               59    2936309    2406120      25942       99.5
SSCDSP              103   18518943   10226773     106777       99.6
DISPATCHER           51    1200555    2587257      12744       99.7
