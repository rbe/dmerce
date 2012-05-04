-- #############################################################################################
--
-- %Purpose: Show fragmented Objects (more than 3 Extents)
--
-- Use:      Needs Oracle DBA Access
--
-- #############################################################################################
--
set feed off
set pagesize 5000
break on owner skip 1 on tablespace_name on segment_type
column datum new_value datum noprint
column owner format A10 heading 'Owner'
column tablespace_name format A20 heading 'Tablespace'
column segment_type format A10 heading 'Segment-|Type'
column segment_name format A30 heading 'Segment-|Name'
column extent_id format 999 heading 'Number of|Extents'
column bytes format 999,999,999,999 heading 'Size|[Bytes]'
--
-- Looking for fragmented objects
--
select  to_char(sysdate, 'MM/DD/YY') datum,
    owner,
    tablespace_name,
    segment_type,
    segment_name,
    count(extent_id) extent_id,
    sum(bytes) bytes
  from sys.dba_extents
 where substr(owner,1,10) not in ('SYS')
group by
  owner,
  tablespace_name,
  segment_type,
  segment_name
having count(extent_id) > 3
order by 1,2,3,4;
