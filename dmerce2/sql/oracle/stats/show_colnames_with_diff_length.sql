-- #############################################################################################
--
-- %Purpose: Show columns that have the same name but different characteristics
--
--           This script lists columns that have the same name but
--           different characteristics. They may cause problems
--           when tables are joined on the columns or unexpected
--           results are returned.
--
-- #############################################################################################
--
accept user_namen char prompt 'Schemaowner or Wildcard : '
set feed off echo off termout on pages 5000 lines 500 verify off array 1
--
spool list_colnames_with_diff_length.lst
ttitle left 'Columns with Inconsistent Data Lengths' -
skip 2

SELECT SUBSTR(owner,1,10) "Owner",
       column_name "ColName", table_name||' '||data_type||'('||
       DECODE(data_type, 'NUMBER', data_precision, data_length)||')' "Characteristics"
  FROM all_tab_columns
 WHERE (column_name, owner)
    IN (SELECT column_name, owner
         FROM all_tab_columns
         GROUP BY column_name, owner
         HAVING MIN(DECODE(data_type, 'NUMBER', data_precision, data_length))
         < max(decode(data_type, 'NUMBER', data_precision, data_length)))
   AND owner LIKE UPPER('&user_namen');
spool off;
set feed on echo off termout on pages 24 verify on;
