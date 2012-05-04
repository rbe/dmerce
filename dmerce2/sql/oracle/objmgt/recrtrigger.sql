rem -----------------------------------------------------------------------
rem Filename:   mktrig.sql
rem Purpose:    This script will create a SQL script to recreate all
rem             triggers in the current schema.                                              --
rem Date:       12-Feb-2000
rem Author:     Frank Naude (frank@ibi.co.za)
rem -----------------------------------------------------------------------

set feedback off
set head     off
set echo     off
set recsep   off
set pages    50000
set long     5000
set lines    200

column trigger_body format a9999 wrap word;

-- Write the script to a file, otherwise it is useless
spool mktrig.run

-- Write the SQL stastements to rebuild the triggers
select 'create or replace trigger ' || description, trigger_body,'/'
from   user_triggers;

spool off;

-- Reset some stuff
set feedback on
set head on
set echo on
