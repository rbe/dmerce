CONNECT SYS/$DMERCE_ORACLE_SYSDBAPWD as SYSDBA
SET ECHO OFF
SPOOL $DMERCE_HOME/logs/orclcrdbcatalog$oracle_ver.log
@$ORACLE_HOME/rdbms/admin/catalog.sql
@$ORACLE_HOME/rdbms/admin/catproc.sql

CONNECT system/manager
@$ORACLE_HOME/sqlplus/admin/pupbld.sql
SPOOL OFF
EXIT;
