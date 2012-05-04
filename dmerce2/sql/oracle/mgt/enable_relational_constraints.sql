-- #############################################################################################
--
-- %Purpose: Enable all relational constraints on tables owned by the user that executes this script
--
-- #############################################################################################
--
-- Requires Oracle 8.1
--
------------------------------------------------------------------------------
PROMPT
PROMPT Generating script to enable disabled relational constraints...

set pagesize 0
set feedback off
set termout off
set linesize 100
set trimspool on
set wrap on

spool enable_relational_constraints.lst.sql

PROMPT PROMPT
PROMPT PROMPT Enabling relational constraints...

SELECT 'PROMPT ... enabling constraint '||constraint_name||' on table '||table_name
,      'alter table '||table_name||' enable constraint '||constraint_name||';'
FROM   user_constraints
WHERE  constraint_type = 'R'
AND    status = 'DISABLED'
/

spool off
set feedback on
set termout on
spool enable_relational_constraints.log
@enable_relational_constraints.lst.sql
spool off
