rem -----------------------------------------------------------------------
rem Filename:   dropcol.sql
rem Purpose:    Demonstrate Oracle8i drop column functionality
rem Date:       28-Aug-1998
rem Author:     Frank Naude (frank@ibi.co.za)
rem -----------------------------------------------------------------------

drop table x
/

create table x(a date, b date, c date)
/

-- Mark col as UNUSED
alter table x set unused column b
/

select * from sys.dba_unused_col_tabs
/

alter table x drop unused columns
/

alter table x drop column c cascade constraints
/
