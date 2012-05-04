-- #############################################################################################
--
-- %Purpose: Tuning Redologs und Checkpoints (Contention, Waits, Number/Duration of Checkpoints)
--
-- #############################################################################################

1). Redolog Buffer Contention
-----------------------------
SELECT SUBSTR(name,1,20) "Name",gets,misses,immediate_gets,immediate_misses
  FROM v$latch
 WHERE name in ('redo allocation', 'redo copy');

Name                       GETS     MISSES IMMEDIATE_GETS IMMEDIATE_MISSES
-------------------- ---------- ---------- -------------- ----------------
redo allocation     277'446'780  2'534'627              0                0
redo copy                33'818     27'694    357'613'861          150'511

MISSES/GETS (must be < 1%)

Redo allocation: (2'534'627 / 277'446'780) * 100 = 0.91 %
Redo Copy:       (27'694 / 33'818) * 100 = 81.8 %

IMMEDIATE_MISSES/(IMMEDIATE_GETS+IMMEDIATE_MISSES) (must be < 1%)

Redo Copy: 150'511/(150'511+357'613'861) = 0.04 %

2). Waits on Redo Log Buffer
----------------------------
SELECT name,value
 FROM v$sysstat
 WHERE name = 'redo log space requests';

The value of 'redo log space requests' reflects the number
of times a user process waits for space in the redo log buffer.
Optimal is if the value is near 0 (Oracle Manual says this ...)

NAME                                                                  VALUE
---------------------------------------------------------------- ----------
redo log space requests                                               22641

4). Number of Checkpoints per hour
----------------------------------
set feed off;
set pagesize 10000;
set wrap off;
set linesize 200;
set heading on;
set tab on;
set scan on;
set verify off;
--
spool show_logswitches.lst

ttitle left 'Redolog File Status from V$LOG' skip 2

select group#, sequence#,
       Members, archived, status, first_time
  from v$log;

ttitle left 'Number of Logswitches per Hour' skip 2

select to_char(first_time,'YYYY.MM.DD') day,
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'00',1,0)),'99') "00",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'01',1,0)),'99') "01",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'02',1,0)),'99') "02",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'03',1,0)),'99') "03",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'04',1,0)),'99') "04",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'05',1,0)),'99') "05",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'06',1,0)),'99') "06",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'07',1,0)),'99') "07",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'08',1,0)),'99') "08",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'09',1,0)),'99') "09",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'10',1,0)),'99') "10",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'11',1,0)),'99') "11",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'12',1,0)),'99') "12",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'13',1,0)),'99') "13",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'14',1,0)),'99') "14",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'15',1,0)),'99') "15",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'16',1,0)),'99') "16",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'17',1,0)),'99') "17",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'18',1,0)),'99') "18",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'19',1,0)),'99') "19",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'20',1,0)),'99') "20",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'21',1,0)),'99') "21",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'22',1,0)),'99') "22",
       to_char(sum(decode(substr(to_char(first_time,'DDMMYYYY:HH24:MI'),10,2),'23',1,0)),'99') "23"
  from v$log_history
 group by to_char(first_time,'YYYY.MM.DD') 
/
spool off;


DAY   00  01  02  03  04  05  06  07  08  09  10  11  12  13  14  15  16  17  18  19  20  21  22  23
----- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
07/07   0   0   0   0   0   0   0   0   0   0   0   0   0   0   1   2   0   0   0   0   0   0   0   0
07/08   0   0   0   0   0   0   0   0   0   0   0   5   0   4   1   0   1   0   0   0   0   0   0   0
07/12   0   0   0   0   0   0   0   0   0   0   1   1   0   1   1   0   0   0   0   0   0   0   0   0
07/13   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   1   0
07/14   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   1   1   1   1   0   0
07/15   1   0   0   0   0   0   0   0   0   0   0   0   2   1   0   0   1   2   2   0   0   0   0   0
07/16   0   0  10  10  15  11   5   0   0   0   0   0   2   5   5   4   5   7   6   6   7   4   4   4
07/17   2   2   1   3   4   6   9  10  11  11  12  12  11  11  12  11  11  12  12   9   9  10  12   9
07/18  12   9  10  10   8   8   9  10   9   8   9  10  10  11  10  11  10  10  11  10  11   9  10  10
07/19   9   3   1   1   0   0   4   6   7   7   4   5  11  10   5   4   5   7   6   8   7   5   5   3
07/20   1   1   8  10   7   5   4   5   4   5   7   7   9   7   9   9   7   9  10  11  12  11  12   9
07/21   9  10  10  10  12  10   7   8   9   8   9  10  11  11  11   8  10  10  12   7   6   7   7   7
07/22   8   7   9  10   8   6   7   8   8   8   9   9   9  10   9   9   9   9   9   9  10   7   6   7
07/23   5   5   7   7   7   2   3   3   4   5   6   5   5   4   3   3   4   4   6   6   5   9   8   5
07/24   4   4   5   4   7   6   5   8   8  11  11  11     

log_checkpoint_interval = 900'000'000  (OK, must be greather than Redolog-File)
log_checkpoint_timeout  = 1200 (Set it to 0, so time-based checkpoints are disabled)

5). Time needed to write a checkpoint
-------------------------------------
Beginning database checkpoint by background
Mon Aug  2 16:37:36 1999
Thread 1 advanced to log sequence 2860
  Current log# 4 seq# 2860 mem# 0: /data/ota/db1/OTASICAP/redo/redoOTASICAP04.log
Mon Aug  2 16:43:31 1999
Completed database checkpoint by background

==> 6 Minutes

Mon Aug  2 16:45:15 1999
Beginning database checkpoint by background
Mon Aug  2 16:45:15 1999
Thread 1 advanced to log sequence 2861
  Current log# 5 seq# 2861 mem# 0: /data/ota/db1/OTASICAP/redo/redoOTASICAP05.log
Mon Aug  2 16:50:29 1999
Completed database checkpoint by background

==> 5.5 Minutes

Mon Aug  2 16:51:50 1999
Beginning database checkpoint by background
Mon Aug  2 16:51:51 1999
Thread 1 advanced to log sequence 2862
  Current log# 6 seq# 2862 mem# 0: /data/ota/db1/OTASICAP/redo/redoOTASICAP06.log
Mon Aug  2 16:56:44 1999
Completed database checkpoint by background

==> 5.5 Minutes