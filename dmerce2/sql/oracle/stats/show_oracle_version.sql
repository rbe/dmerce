-- #############################################################################################
--
-- %Purpose: Show installed Database Version and Options with Port specific infos
--
-- Use:      Needs Oracle DBA Access
--
-- #############################################################################################
--
set feed off;
set pagesize 10000;
set wrap off;
set linesize 200;
set heading on;
set tab on;
set scan on;
set verify off;
set termout on;
set serveroutput on;

ttitle left 'Oracle Version:' skip 2

select banner
from   sys.v$version;

ttitle left 'Installed Options:' skip 2

select parameter
from   sys.v$option
where  value = 'TRUE';

ttitle left 'Not Installed Options:' skip 2

select parameter
from   sys.v$option
where  value <> 'TRUE';

prompt
begin
    dbms_output.put_line('Specific Port Information: '||dbms_utility.port_string);
end;
/
prompt

set head on feed on

