-- #############################################################################################
--
-- %Purpose: Show free Space in all Datafiles and if AUTOEXTENT is ON
--
-- #############################################################################################
--
clear columns -
      breaks -
      computes
set pagesize 100

column file_name format a32
column tablespace_name format a15
column status format a3 trunc
column t format 999,999.000 heading "Total MB"
column a format a4 heading "Aext"
column p format 990.00 heading "% Free"

SELECT  df.file_name,
        df.tablespace_name,
        df.status,
        (df.bytes/1024000) t,
        (fs.s/df.bytes*100) p,
        decode (ae.y,1,'YES','NO') a
  FROM  dba_data_files df,
        (SELECT file_id,SUM(bytes) s
           FROM dba_free_space
           GROUP BY file_id) fs,
        (SELECT file#, 1 y
           FROM sys.filext$
           GROUP BY file#) ae
  WHERE df.file_id = fs.file_id
    AND ae.file#(+) = df.file_id
ORDER BY df.tablespace_name, df.file_id;

column file_name clear
column tablespace_name clear
column status clear
column t clear
column a clear
column p clear
ttitle off
