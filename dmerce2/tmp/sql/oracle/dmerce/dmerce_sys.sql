SPOOL dmerce_sys.lst

CONNECT sys/change_on_install AS SYSDBA;

@$DMERCE_HOME/sql/oracle/dmerce/dmerce_sys_drop.sql
@$DMERCE_HOME/sql/oracle/dmerce/dmerce_sys_user.sql
@$DMERCE_HOME/sql/oracle/dmerce/dmerce_sys_role.sql
GRANT r_dmerce_sys TO dmerce_sys;

CONNECT dmerce_sys/dmerce_sys;

@$DMERCE_HOME/sql/oracle/dmerce/dmerce_sys_tables.sql
@$DMERCE_HOME/sql/oracle/dmerce/dmerce_sys_constraints.sql
@$DMERCE_HOME/sql/oracle/dmerce/dmerce_sys_index.sql
@$DMERCE_HOME/sql/oracle/dmerce/dmerce_sys_alter_tables.sql
@$DMERCE_HOME/sql/oracle/dmerce/dmerce_sys_views.sql
@$DMERCE_HOME/sql/oracle/dmerce/dmerce.pkg
@$DMERCE_HOME/sql/oracle/dmerce/dmerce_sys.pkg
@$DMERCE_HOME/sql/oracle/dmerce/dmerce_web_user.sql
@$DMERCE_HOME/sql/oracle/dmerce/dmerce_web_privs_dmerce_sys.sql

COMMIT;

SPOOL OFF
