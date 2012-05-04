rem -----------------------------------------------------------------------
rem Filename:   exterror.sql
rem Purpose:    Segments that will cause errors when they try to extent!!!
rem Author:     Frank Naude (frank@ibi.co.za)
rem -----------------------------------------------------------------------

Prompt Segments that will cause errors when they try to extent!!!

select  a.owner, a.segment_name, b.tablespace_name,
        decode(ext.extents,1,b.next_extent,
        a.bytes*(1+b.pct_increase/100)) nextext,
        freesp.largest
  from  dba_extents a,
        dba_segments b,
        (select owner, segment_name, max(extent_id) extent_id,
                count(*) extents
           from dba_extents
          group by owner, segment_name
        ) ext,
        (select tablespace_name, max(bytes) largest
           from dba_free_space
          group by tablespace_name
        ) freesp
 where  a.owner=b.owner
   and  a.segment_name=b.segment_name
   and  a.owner=ext.owner
   and  a.segment_name=ext.segment_name
   and  a.extent_id=ext.extent_id
   and  b.tablespace_name = freesp.tablespace_name
   and decode(ext.extents,1,b.next_extent, a.bytes*(1+b.pct_increase/100)) > freesp.largest
/
