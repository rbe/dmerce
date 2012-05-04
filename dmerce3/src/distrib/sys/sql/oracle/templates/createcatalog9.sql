connect sys/change_on_install as sysdba
set echo on
spool C:\oracle\admin\dmerce\createdbcatalog.log
@?\rdbms\admin\catalog.sql;
@?\rdbms\admin\catproc.sql;
connect system/manager
@?\sqlplus\admin\pupbld.sql;
connect system/manager
set echo on
spool C:\oracle\admin\dmerce\sqlplushelp.log
@?\sqlplus\admin\help\hlpbld.sql helpus.sql;
exit;
