-- #############################################################################################
--
-- %Purpose: Show DB-Events which causing Sessions to wait
--
-- #############################################################################################
--
-- Die V$SESSION_WAIT View enthält sämtliche Events, welche die User-
-- und System Sessions in den Wartezustand versetzen. Diese View kann
-- verwendet werden, um rasch einen Performance Engpass herauszufinden.
--
-- Eine Waiting Time von 0 zeigt an, dass die Session
-- gerade auf einen Event wartet. Grosse Wait Times weisen auf ein
-- Performance Problem hin (siehe Oracle Tuning Guide Seite A-4).
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
ttitle left 'Session Events' -
skip 2

select w.Sid "Sid", nvl(substr(s.username,1,15),'Background') "User",
  substr(w.event,1,25) "Event", w.wait_time "Wait Time"
  from v$session_wait w, v$session s
where w.sid = s.sid
order by 2,4;
