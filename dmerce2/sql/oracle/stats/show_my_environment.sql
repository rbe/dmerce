-- #############################################################################################
--
-- %Purpose: Show information about your current database account (who am I)
--
-- #############################################################################################
--
set termout off
set head off
set termout on

select 'User: '|| user || ' on database ' || global_name,
       '(Terminal='||USERENV('TERMINAL')||
       ', Session-Id='||USERENV('SESSIONID')||')'
from   global_name;

set head on feed on
