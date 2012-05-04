rem -----------------------------------------------------------------------
rem Filename:   try-conn.sql
rem Purpose:    Try to connect to well known database users
rem Usage:	sqlplus /nolog @try-conn.sql
rem Date:       04-Nov-2001
rem Author:     Frank Naude (frank@ibi.co.za)
rem -----------------------------------------------------------------------

set feed off pages 0 head off

select '*** User '||USER||' uses default password on database '||
       GLOBAL_NAME||' ***'
from   global_name
-- The next line must be left blank!!!

conn sys/change_on_install
/
conn system/manager
/
conn hr/hr
/
conn oe/oe
/
conn sh/sh
/
conn scott/tiger
/
conn adams/wood
/
conn jones/steel
/
conn clark/cloth
/
conn blake/paper
/
conn outln/outln
/
conn ctxsys/ctxsys
/
conn tracesvr/trace
/
conn dbsnmp/dbsnmp
/
conn ordplugins/ordplugins
/
conn ordsys/ordsys
/
conn mdsys/mdsys
/
conn dssys/dssys
/
conn perfstat/perfstat
/
conn csmig/csmig
/
