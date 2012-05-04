-- #############################################################################################
--
-- %Purpose: Show INITIAL, NEXT, Total Extents, Total Blocks of DB-Objects
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
spool show_object_storage_info.lst

ttitle 'Object Storage Information' -
skip 2

SELECT SUBSTR(s.owner,1,20) || '.' ||SUBSTR(s.segment_name,1,20) "Object Name",
       SUBSTR(s.segment_type,1,10) "Type",
       SUBSTR(s.tablespace_name,1,10) Tspace,
       NVL(NVL(t.initial_extent, i.initial_extent),r.initial_extent) "FstExt",
       NVL(NVL(t.next_extent,i.next_extent),R.NEXT_EXTENT) "NxtExt",
       s.extents "TotExt",
       s.blocks "TotBlks"
 FROM  dba_rollback_segs R,
       dba_indexes I,
       dba_tables T,
       dba_segments S
WHERE s.segment_type           IN ('CACHE','CLUSTER','INDEX','ROLLBACK','TABLE','TEMPORARY')
  AND s.owner                  NOT IN ('SYSTEM')
  AND s.owner            =     t.owner (+)
  AND s.segment_name     =     t.table_name (+)
  AND s.tablespace_name  =     t.tablespace_name (+)
  AND s.owner            =     i.owner (+)
  AND s.segment_name     =     i.index_name (+)
  AND s.tablespace_name  =     i.tablespace_name (+)
  AND s.owner            =     r.owner (+)
  AND s.segment_name     =     r.segment_name (+)
  AND s.tablespace_name  =     r.tablespace_name (+)
ORDER BY 2,1;
spool off;
set feed on echo off termout on pages 24 verify on
ttitle off
