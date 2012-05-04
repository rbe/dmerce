-- #############################################################################################
--
-- %Purpose: Show Redo Waits ('redo log space wait time', 'redo log space requests')
--
-- #############################################################################################
--
col name  format        a30 justify l heading 'Redo Log Buffer'
col value format 99,999,990 justify c heading 'Waits'
SELECT name,
       value
  FROM v$sysstat
 WHERE name IN ('redo log space wait time','redo log space requests')
/
