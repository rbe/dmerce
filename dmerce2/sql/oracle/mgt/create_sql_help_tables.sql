-- #############################################################################################
--
-- %Purpose: Install SQL*PLUS and PL/SQL Help Tables in Data Dictionary for: SQL>help command
--
-- Use:      Privileges for SYSTEM User
--
-- #############################################################################################
--
#!/bin/sh

$ORACLE_HOME/bin/svrmgrl << EOF
  connect system/manager
  @$ORACLE_HOME/sqlplus/admin/help/helptbl.sql;
  exit;
EOF

$ORACLE_HOME/bin/svrmgrl << EOF
  connect system/manager
  @$ORACLE_HOME/sqlplus/admin/help/helpindx.sql;
  exit;
EOF

$ORACLE_HOME/bin/sqlldr userid=system/manager control=$ORACLE_HOME/sqlplus/admin/help/plushelp.ctl
$ORACLE_HOME/bin/sqlldr userid=system/manager control=$ORACLE_HOME/sqlplus/admin/help/plshelp.ctl
$ORACLE_HOME/bin/sqlldr userid=system/manager control=$ORACLE_HOME/sqlplus/admin/help/sqlhelp.ctl
