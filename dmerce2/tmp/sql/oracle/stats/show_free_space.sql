-- #############################################################################################
--
-- %Purpose: Show total, free and used space in all tablespaces / database files
--
-- Use:      Needs Oracle DBA Access
--
-- #############################################################################################
--
clear buffer
clear columns
clear breaks
set linesize 500
set pagesize 5000
column a1 heading 'Tablespace' format a15
column a2 heading 'Data File' format a45
column a3 heading 'Total|Space [MB]' format 99999.99
column a4 heading 'Free|Space [MB]' format 99999.99
column a5 heading 'Free|%' format 9999.99
break on a1 on report
compute sum of a3 on a1
compute sum of a4 on a1
compute sum of a3 on report
compute sum of a4 on report
SELECT a.tablespace_name a1,
       a.file_name a2,
       a.avail a3,
       NVL(b.free,0) a4,
       NVL(ROUND(((free/avail)*100),2),0) a5
  FROM (SELECT tablespace_name,
               SUBSTR(file_name,1,45) file_name,
               file_id,
               ROUND(SUM(bytes/(1024*1024)),3) avail
          FROM sys.dba_data_files
      GROUP BY tablespace_name,
               SUBSTR(file_name,1,45),
               file_id) a,
       (SELECT tablespace_name,
               file_id,
               ROUND(SUM(bytes/(1024*1024)),3) free
          FROM sys.dba_free_space
      GROUP BY tablespace_name, file_id) b
WHERE a.file_id = b.file_id (+)
ORDER BY 1, 2
/
