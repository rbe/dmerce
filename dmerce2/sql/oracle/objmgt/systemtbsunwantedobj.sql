rem -----------------------------------------------------------------------
rem Filename:   tswrong.sql
rem Purpose:    List objects in the SYSTEM tablespace that doesn't belong
rem             to SYS or SYSTEM
rem Date:	12-Jan-2000
rem Author:	Frank Naude (frank@ibi.co.za)
rem -----------------------------------------------------------------------

select *
 from sys.dba_segments
where owner not in ('PUBLIC', 'SYS', 'SYSTEM')
  and tablespace_name = 'SYSTEM'
/
