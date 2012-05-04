rem -----------------------------------------------------------------------
rem Filename:   lock.sql
rem Purpose:    Display database locks and latches (with tables names, etc)
rem Date:       12-Apr-1998
rem Author:     Frank Naude (frank@ibi.co.za)
rem -----------------------------------------------------------------------

set pagesize 23
col sid format 999999
col serial# format 999999
col username format a12 trunc
col process format a8 trunc
col terminal format a12 trunc
col type format a12 trunc
col lmode format a4 trunc
col lrequest format a4 trunc
col object format a73 trunc

SELECT *
  FROM dblockslatches;
