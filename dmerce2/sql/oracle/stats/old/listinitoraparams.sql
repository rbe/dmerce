rem -----------------------------------------------------------------------
rem Filename:   initora.sql
rem Purpose:    List all supported INIT.ORA parameters
rem Date:       03-Apr-1998
rem Author:     Frank Naude (frank@bi.co.za)
rem -----------------------------------------------------------------------

select a.ksppinm name, b.ksppstvl value, b.ksppstdf isdefault,
       decode(a.ksppity, 1, 'boolean', 2, 'string', 3, 'number', 4, 'file', 
	      a.ksppity) type,
       a.ksppdesc description
from   sys.x$ksppi a, sys.x$ksppcv b
where  a.indx = b.indx
  and  a.ksppinm not like '\_%' escape '\'
order  by name
/

rem -----------------------------------------------------------------------
rem Filename:   inithide.sql
rem Purpose:    List all un-supported INIT.ORA parameters
rem Date:       03-Apr-1998
rem Author:     Frank Naude (frank@ibi.co.za)
rem -----------------------------------------------------------------------

select a.ksppinm name, b.ksppstvl value, b.ksppstdf isdefault,
       decode(a.ksppity, 1, 'boolean', 2, 'string', 3, 'number', 4, 'file',
	      a.ksppity) type,
       a.ksppdesc description
from   sys.x$ksppi a, sys.x$ksppcv b
where  a.indx = b.indx
  and  a.ksppinm like '\_%' escape '\'
order  by name
/

