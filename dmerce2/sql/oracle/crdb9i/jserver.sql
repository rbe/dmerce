CONNECT SYS/$DMERCE_ORACLE_SYSDBAPWD AS SYSDBA
SET ECHO OFF
SPOOL $DMERCE_HOME/log/crdb$DMERCE_ORACLE_VER/$DMERCE_ORACLE_INST/jserver.log
@$ORACLE_HOME/javavm/install/initjvm.sql;
@$ORACLE_HOME/xdk/admin/initxml.sql;
@$ORACLE_HOME/xdk/admin/xmlja.sql;
@$ORACLE_HOME/javavm/install/init_jis.sql $ORACLE_HOME;
@$ORACLE_HOME/javavm/install/jisaephc.sql $ORACLE_HOME;
@$ORACLE_HOME/javavm/install/jisja.sql $ORACLE_HOME;
@$ORACLE_HOME/javavm/install/jisdr.sql 2481 2482;
@$ORACLE_HOME/jsp/install/initjsp.sql;
@$ORACLE_HOME/jsp/install/jspja.sql;
@$ORACLE_HOME/rdbms/admin/initjms.sql;
@$ORACLE_HOME/rdbms/admin/initrapi.sql;
@$ORACLE_HOME/rdbms/admin/initsoxx.sql;
@$ORACLE_HOME/rdbms/admin/initapcx.sql;
@$ORACLE_HOME/rdbms/admin/initcdc.sql;
@$ORACLE_HOME/rdbms/admin/initqsma.sql;
@$ORACLE_HOME/rdbms/admin/initsjty.sql;
@$ORACLE_HOME/rdbms/admin/initaqhp.sql;
SPOOL OFF
EXIT;
