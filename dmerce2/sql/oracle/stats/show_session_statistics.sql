-- #############################################################################################
--
-- %Purpose: Show Session Statistic (Users Logged-On, Users Waiting, Users Waiting-for-Locks)
--
-- #############################################################################################
--
-- Das Diagramm «No. of Users Logged On» zeigt die Anzahl concurrent
-- Users Sessions, unabhänig davon ob sie nun aktiv sind oder nicht.
--
-- Das Diagramm «No. of Users Running» zeigt die Users Sessions,
-- welche eine Transaktion ausführen.
--
-- Das Diagramm «No. of Users Waiting» zeigt die User Sessions, die
-- auf einen Event (for whatever reason) warten müssen, um eine Aktion durchzuführen.
--
-- Das Diagramm «No. of Users Waiting for Lock» zeigt die User Sessions,
-- die auf die Freigabe eines Locks warten müssen.
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
ttitle off;
--
select sessions_current "Users logged on"
  from v$license;
--
select count(*) "Users running"
  from v$session_wait
 where wait_time!=0;
--
ttitle left 'Users waiting' -
skip 2

select substr(w.sid,1,5) "Sid",
          substr(s.username,1,15) "User",
          substr(event,1,40) "Event",
          seconds_in_wait "Wait [s]"
from v$session_wait w, v$session s
where s.sid = w.sid
   and state = 'WAITING'
   and event not like 'SQL*Net%'
   and event != 'client message'
   and event not like '%mon timer'
   and event != 'rdbms ipc message'
   and event != 'Null Event';
--
select count(*) "Users waiting for locks"
  from v$session
 where lockwait is not null;
