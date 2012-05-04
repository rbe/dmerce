rem -----------------------------------------------------------------------
rem Filename:   sec-prof.sql
rem Purpose:    List security related profile information
rem Date:       04-Nov-2001
rem Author:     Frank Naude (frank@ibi.co.za)
rem -----------------------------------------------------------------------

conn / as sysdba

col profile format a20 
col limit   format a20

select profile, resource_name, limit 
from   dba_profiles
where  resource_name like '%PASSWORD%' 
   or  resource_name like '%LOGIN%'
/
