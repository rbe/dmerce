-- #############################################################################################
--
-- %Purpose: Show block chaining (chained rows) with ANALYZE TABLE LIST CHAINED ROWS
--
-- Use:      Needs Oracle DBA Access
--
-- #############################################################################################
--
set echo off termout off
DROP TABLE lst_chained_rows$tmp;
set termout on
CREATE TABLE lst_chained_rows$tmp
(
owner_name    VARCHAR2(30),
table_name    VARCHAR2(30),
cluster_name  VARCHAR2(30),
head_rowid    ROWID,
timestamp     DATE
);
--
accept user_namen char     prompt 'Username or %: '
accept tabellen_namen char prompt 'Tablename or %: '
set feed off echo off termout off pages 0 verify off array 1
--
SPOOL list_chained_rows.sql
SELECT  'ANALYZE TABLE '||OWNER||'.'||TABLE_NAME,
        'LIST CHAINED ROWS INTO lst_chained_rows$tmp;'
FROM  sys.dba_tables
WHERE owner LIKE UPPER('&user_namen')
AND table_name LIKE UPPER('&tabellen_namen')
ORDER BY owner, table_name;
--
spool off
set feed on echo on termout on pages 66 verify on
@list_chained_rows.sql
--
set echo off
column table_name format a30
column owner_name format a16 trunc
--
spool list_chained_rows.lst
--
SELECT  RPAD(owner_name,16,'.') owner_name,
  RPAD(c.table_name,30,'.') table_name,
  num_rows,
  COUNT(*) ch_rows, pct_free
FROM  sys.dba_tables t, lst_chained_rows$tmp c
WHERE t.owner = c.owner_name
AND t.table_name = c.table_name
GROUP BY owner_name, c.table_name, pct_free, num_rows
UNION
SELECT  'No block chaining',NULL,0,0,0
FROM  dual
WHERE   0 = ( SELECT  COUNT(*)
        FROM  lst_chained_rows$tmp
        WHERE rownum = 1 )
ORDER BY 1,2;
spool off
set feed on echo off termout on pages 24 verify on
ttitle off
DROP TABLE lst_chained_rows$tmp;
