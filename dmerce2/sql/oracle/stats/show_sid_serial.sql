-- #############################################################################################
--
-- %Purpose: Shows the SID etc of sessions connected to the database
--
--           Requires access to the dynamic performance tables.
--
-- #############################################################################################
--
set linesize 2000 trimspool on
select
   to_char(SID,'999') SID,
   to_char(Serial#,'999999') Srl#,
   substr(USERNAME,1,10) Username,
   decode(STATUS,'ACTIVE','Active',null) Status,
   substr(to_char(LOGON_TIME,'Dy HH24:MI'),1,10) Logon,
   substr(OSUSER,1,8) OSUser,
   substr(MACHINE,1,12) Machine,
   substr(PROGRAM,1,10) Program,
   substr(Sql_text,1,60) Sql_text
from
   v$session s,
   v$sqltext t
where
   s.type <> 'BACKGROUND' and
   s.sql_address = t.address (+) and
   t.piece (+) = 0
order by
   Status,
   SID
/
