-- #############################################################################################
--
-- %Purpose: NLS: Show current NLS database settings from sys.props$
--
-- #############################################################################################
--
spool show_nls_current_settings.lst
ttitle left 'Show current NLS database settings' skip 2
set feed off
set pagesize 30000
set linesize 200
column name format a25
column VALUE$ format a35

SELECT name,value$ FROM sys.props$;

spool off;
