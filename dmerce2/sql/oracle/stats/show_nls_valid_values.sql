-- #############################################################################################
--
-- %Purpose: NLS: Show valid NLS parameters (TERRITORY, CHARACTERSET) from v$nls_valid_values
--
-- #############################################################################################
--
spool show_nls_valid_values.lst
ttitle left 'Show valid NLS parameters (TERRITORY, CHARACTERSET)' skip 2
set feed off
set pagesize 30000
set linesize 200

COLUMN parameter format A15
COLUMN value format A15

SELECT parameter,value
  FROM v$nls_valid_values
 WHERE parameter = 'CHARACTERSET'
ORDER BY value;

spool off;
