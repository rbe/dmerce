-- #############################################################################################
--
-- %Purpose: Show Statistics of connected Sessions (PID, Connection-Type, Username, Logon-Time)
--
-- #############################################################################################
--
col osuser format a10 trunc heading "OSUSER AS"
col orauser format a10 trunc
col machine format a10 trunc
col sprogram format a15 trunc
col process format a20 trunc
col server format a3 trunc
col sess_id format 9999
col proc_id format a7
--
SELECT  s.osuser osuser,
        s.username orauser,
        s.machine machine,
        s.program sprogram,
        p.program process,
        s.sid sess_id,
        p.spid proc_id,
        s.logon_time,
        s.server server
FROM    v$session s,
        v$process p
WHERE   s.paddr = p.addr
AND     type != 'BACKGROUND'
AND     p.username is not null
ORDER BY 6
/
col osuser clear
col machine clear
col orauser clear
ttitle off
