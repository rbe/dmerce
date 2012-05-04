SET ECHO OFF

CONNECT SYS/$DMERCE_ORACLE_SYSDBAPWD AS SYSDBA
SPOOL $DMERCE_HOME/log/crdb$DMERCE_ORACLE_VER/$DMERCE_ORACLE_INST/crdbcatalog.log
@$ORACLE_HOME/rdbms/admin/catalog.sql;
@$ORACLE_HOME/rdbms/admin/catexp7.sql;
@$ORACLE_HOME/rdbms/admin/catblock.sql;
@$ORACLE_HOME/rdbms/admin/catproc.sql;
@$ORACLE_HOME/rdbms/admin/catoctk.sql;
@$ORACLE_HOME/rdbms/admin/catobtk.sql;
@$ORACLE_HOME/rdbms/admin/caths.sql;
@$ORACLE_HOME/rdbms/admin/owminst.plb;
SPOOL OFF

SPOOL $DMERCE_HOME/log/crdb$DMERCE_ORACLE_VER/$DMERCE_ORACLE_INST/crdbcatalog-pupbld.log
CONNECT SYSTEM/$DMERCE_ORACLE_SYSTEMPWD
@$ORACLE_HOME/sqlplus/admin/pupbld.sql;
SPOOL OFF

CONNECT SYSTEM/$DMERCE_ORACLE_SYSTEMPWD
SPOOL $DMERCE_HOME/log/crdb$DMERCE_ORACLE_VER/$DMERCE_ORACLE_INST/crdbcatalog-sqlPlusHelp.log
@$ORACLE_HOME/sqlplus/admin/help/hlpbld.sql helpus.sql;
SPOOL OFF

EXIT;
