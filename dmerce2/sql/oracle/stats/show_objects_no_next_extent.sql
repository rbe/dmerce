-- #############################################################################################
--
-- %Purpose: Show Objects which cannot allocate NEXT Extent (ORA-01653)
--
-- alter table credit allocate extent;
-- ORA-01653: unable to extend table PPB.CREDIT by 5000 in tablespace CRE
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
spool show_objects_no_next_extent.lst

ttitle 'Show Objects which cannot allocate the next extent' -
skip 2

column owner format a10;
column segment_name format a22;
column segment_type format a10;
column tablespace_name format a14;
column next_extent format 999,999,999;

SELECT seg.owner, seg.segment_name,
       seg.segment_type, seg.tablespace_name,
       t.next_extent
   FROM sys.dba_segments seg,
        sys.dba_tables   t
WHERE  (seg.segment_type = 'TABLE'
  AND    seg.segment_name = t.table_name
  AND    seg.owner        = t.owner
  AND    NOT EXISTS
            (select tablespace_name
               from dba_free_space free
              where free.tablespace_name =  t.tablespace_name
                and bytes               >=  t.next_extent     ))
UNION
SELECT seg.owner, seg.segment_name,
       seg.segment_type, seg.tablespace_name,
       DECODE (seg.segment_type,
               'CLUSTER',  c.next_extent)
   FROM sys.dba_segments seg,
        sys.dba_clusters c
WHERE   (seg.segment_type = 'CLUSTER'
  AND    seg.segment_name = c.cluster_name
  AND    seg.owner        = c.owner
  AND    NOT EXISTS
            (select tablespace_name
               from dba_free_space free
              where free.tablespace_name =  c.tablespace_name
                and bytes               >=  c.next_extent     ))
UNION
SELECT seg.owner, seg.segment_name,
       seg.segment_type, seg.tablespace_name,
       DECODE (seg.segment_type,
               'INDEX',    i.next_extent )
   FROM sys.dba_segments seg,
        sys.dba_indexes  i
WHERE  (seg.segment_type = 'INDEX'
  AND    seg.segment_name = i.index_name
  AND    seg.owner        = i.owner
  AND    NOT EXISTS
            (select tablespace_name
               from dba_free_space free
              where free.tablespace_name =  i.tablespace_name
                and bytes               >=  i.next_extent     ))
UNION
SELECT seg.owner, seg.segment_name,
       seg.segment_type, seg.tablespace_name,
       DECODE (seg.segment_type,
               'ROLLBACK', r.next_extent)
   FROM sys.dba_segments seg,
        sys.dba_rollback_segs r
where  (seg.segment_type = 'ROLLBACK'
  AND    seg.segment_name = r.segment_name
  AND    seg.owner        = r.owner
  AND    NOT EXISTS
            (select tablespace_name
               from dba_free_space free
              where free.tablespace_name =  r.tablespace_name
                and bytes               >=  r.next_extent     ))
/
ttitle 'Segments that are sitting on the Maximum Extents Allowable' -
skip 2

select  e.owner, e.segment_name, e.segment_type, count(*), avg(max_extents)
 from  dba_extents e, dba_segments s
where  e.segment_name = s.segment_name
  and  e.owner        = s.owner
group by  e.owner, e.segment_name, e.segment_type
having count(*) = avg(max_extents)
/
spool off;
set feed on echo off termout on pages 24 verify on
ttitle off


