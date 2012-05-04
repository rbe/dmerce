-- #############################################################################################
--
-- %Purpose: Show Number of Logswitches per Hour and Day as a Histogram
--
-- Use:      Needs Oracle DBA Access
--
-- #############################################################################################
--

### Version for Oracle 7

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

ttitle left 'Redolog File Status aus V$LOG' skip 2

select group#, sequence#,
       Members, archived, status, first_time
  from v$log;

ttitle left 'Anzahl Logswitches pro Stunde' skip 2

select substr(time,1,5) day,
       to_char(sum(decode(substr(time,10,2),'00',1,0)),'99') "00",
       to_char(sum(decode(substr(time,10,2),'01',1,0)),'99') "01",
       to_char(sum(decode(substr(time,10,2),'02',1,0)),'99') "02",
       to_char(sum(decode(substr(time,10,2),'03',1,0)),'99') "03",
       to_char(sum(decode(substr(time,10,2),'04',1,0)),'99') "04",
       to_char(sum(decode(substr(time,10,2),'05',1,0)),'99') "05",
       to_char(sum(decode(substr(time,10,2),'06',1,0)),'99') "06",
       to_char(sum(decode(substr(time,10,2),'07',1,0)),'99') "07",
       to_char(sum(decode(substr(time,10,2),'08',1,0)),'99') "08",
       to_char(sum(decode(substr(time,10,2),'09',1,0)),'99') "09",
       to_char(sum(decode(substr(time,10,2),'10',1,0)),'99') "10",
       to_char(sum(decode(substr(time,10,2),'11',1,0)),'99') "11",
       to_char(sum(decode(substr(time,10,2),'12',1,0)),'99') "12",
       to_char(sum(decode(substr(time,10,2),'13',1,0)),'99') "13",
       to_char(sum(decode(substr(time,10,2),'14',1,0)),'99') "14",
       to_char(sum(decode(substr(time,10,2),'15',1,0)),'99') "15",
       to_char(sum(decode(substr(time,10,2),'16',1,0)),'99') "16",
       to_char(sum(decode(substr(time,10,2),'17',1,0)),'99') "17",
       to_char(sum(decode(substr(time,10,2),'18',1,0)),'99') "18",
       to_char(sum(decode(substr(time,10,2),'19',1,0)),'99') "19",
       to_char(sum(decode(substr(time,10,2),'20',1,0)),'99') "20",
       to_char(sum(decode(substr(time,10,2),'21',1,0)),'99') "21",
       to_char(sum(decode(substr(time,10,2),'22',1,0)),'99') "22",
       to_char(sum(decode(substr(time,10,2),'23',1,0)),'99') "23"
  from v$log_history
 group by substr(time,1,5)
/
spool off;

### Version for Oracle 8.1.x

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

ttitle left 'Number of Logswitches per hour' skip 2

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

### Version for Oracle 8.1.x with archived redologs

alter session set nls_date_format = 'DD.MM.YYYY:HH24:MI';

spool show_logswitches.lst

ttitle left 'Redolog File Status from V$LOG' skip 2

select group#, sequence#,
       Members, archived, status, first_time
  from v$log;

ttitle left 'Number of Logswitches per Hour' skip 2

select substr(completion_time,1,5) day,
       to_char(sum(decode(substr(completion_time,12,2),'00',1,0)),'99') "00",
       to_char(sum(decode(substr(completion_time,12,2),'01',1,0)),'99') "01",
       to_char(sum(decode(substr(completion_time,12,2),'02',1,0)),'99') "02",
       to_char(sum(decode(substr(completion_time,12,2),'03',1,0)),'99') "03",
       to_char(sum(decode(substr(completion_time,12,2),'04',1,0)),'99') "04",
       to_char(sum(decode(substr(completion_time,12,2),'05',1,0)),'99') "05",
       to_char(sum(decode(substr(completion_time,12,2),'06',1,0)),'99') "06",
       to_char(sum(decode(substr(completion_time,12,2),'07',1,0)),'99') "07",
       to_char(sum(decode(substr(completion_time,12,2),'08',1,0)),'99') "08",
       to_char(sum(decode(substr(completion_time,12,2),'09',1,0)),'99') "09",
       to_char(sum(decode(substr(completion_time,12,2),'10',1,0)),'99') "10",
       to_char(sum(decode(substr(completion_time,12,2),'11',1,0)),'99') "11",
       to_char(sum(decode(substr(completion_time,12,2),'12',1,0)),'99') "12",
       to_char(sum(decode(substr(completion_time,12,2),'13',1,0)),'99') "13",
       to_char(sum(decode(substr(completion_time,12,2),'14',1,0)),'99') "14",
       to_char(sum(decode(substr(completion_time,12,2),'15',1,0)),'99') "15",
       to_char(sum(decode(substr(completion_time,12,2),'16',1,0)),'99') "16",
       to_char(sum(decode(substr(completion_time,12,2),'17',1,0)),'99') "17",
       to_char(sum(decode(substr(completion_time,12,2),'18',1,0)),'99') "18",
       to_char(sum(decode(substr(completion_time,12,2),'19',1,0)),'99') "19",
       to_char(sum(decode(substr(completion_time,12,2),'20',1,0)),'99') "20",
       to_char(sum(decode(substr(completion_time,12,2),'21',1,0)),'99') "21",
       to_char(sum(decode(substr(completion_time,12,2),'22',1,0)),'99') "22",
       to_char(sum(decode(substr(completion_time,12,2),'23',1,0)),'99') "23"
  from V$ARCHIVED_LOG
 group by substr(completion_time,1,5)
/
spool off;

