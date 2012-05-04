-- #############################################################################################
--
-- %Purpose: Show Characteristics for SYSTEM and other Rollback Segments
--
-- #############################################################################################
--
spool show_rollback_segment.lst
ttitle  left 'Rollback-Segment Status' -
skip 2
set feed off
set pagesize 10000
column datum new_value datum noprint
column tablespace_name format A32 heading 'Tablespace'
column segment_name format a16 heading "Rollback-Segment"
column status format a15 heading "Status"
column owner format a10 heading "Owner"

SELECT  to_char(sysdate, 'MM/DD/YY') datum,
        segment_name,
        tablespace_name,
        status,
        owner
FROM sys.dba_rollback_segs;
ttitle off;

SELECT 'Number of Extents in SYSTEM Rollback Segment (1): ' || COUNT(*) "Extents"
  FROM dba_extents
 WHERE segment_name = 'SYSTEM'
   AND segment_type = 'ROLLBACK';

SELECT 'Number of Extents in SYSTEM Rollback Segment (2): ' || extents "Extents"
  FROM v$rollstat WHERE usn = 0;


SELECT 'Maximal Number of Extents in SYSTEM Rollback Segment: ' || max_extents "Max Extents"
  FROM dba_rollback_segs
 WHERE segment_name = 'SYSTEM';

ttitle  left 'SYSTEM Rollback-Segment Status' -
skip 2

SELECT usn ,extents,rssize,hwmsize,optsize
  FROM v$rollstat WHERE usn = 0;

spool off;

-- USN Rollback Segment Number (0 = SYSTEM RS)
-- EXTENTS Number of allocated Extents.
-- RSSIZE  Total size of the Rollback Segment in Bytes.
-- HWMSIZE High water mark of Rollback Segment in Bytes
-- OPTSIZE Optimal size of Rollback Segment (should be NULL).


