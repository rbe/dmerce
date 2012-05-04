-- #############################################################################################
--
-- %Purpose: Show Number of physical Reads and Writes per Sec for each DB-File (I/O Details)
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
--
ttitle left 'File I/O Rate Details since last Instance Startup' -
skip 2

select substr(NAME,1,30) "NAME",PHYRDS,PHYWRTS,PHYBLKRD,PHYBLKWRT
  from V$DBFILE DF, V$FILESTAT FS
 where DF.FILE#=FS.FILE#
 order by NAME;
