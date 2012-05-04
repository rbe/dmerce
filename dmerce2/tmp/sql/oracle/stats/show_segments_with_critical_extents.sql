-- #############################################################################################
--
-- %Purpose: Show Segments with critical Number of Extents, soon reaching MAX_EXTENTS
--
-- #############################################################################################
--
clear   columns -
        breaks -
        computes
set pagesize 100

column owner        format a15
column segment_name format a20
column segment_type format a20

SELECT owner,segment_name,segment_type,extents,max_extents
  FROM dba_segments
 WHERE max_extents <= 10*(extents)
   AND max_extents != 0;

column owner        clear
column segment_name clear
column segment_type clear
