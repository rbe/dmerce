rem -----------------------------------------------------------------------
rem Filename:   freesp.sql
rem Purpose:    List segments with more than 200 Meg of free DB Blocks
rem             These segments must be re-build to release space and
rem             speed-up table scans.
rem Date:       19-Apr-98
rem Author:     Frank Naude (frank@ibi.co.za)
rem -----------------------------------------------------------------------

set serveroutput on size 40000

declare
  total_blocks number;
  total_bytes number;
  unused_blocks number;
  unused_bytes number;
  last_used_extent_file_id number;
  last_used_extent_block_id number;
  last_used_block number;
  free_pct number;
begin
  for c1 in (select owner, segment_type, segment_name
             from   sys.dba_segments
             where  owner not in ('SYS', 'SYSTEM', 'PUBLIC') 
               and  segment_type not like '%LOB%')
  loop
    -- dbms_output.put_line('Testing '||c1.segment_type||': '||
    --                     c1.owner||'.'||c1.segment_name||'...');
    dbms_space.unused_space(c1.owner, c1.segment_name, c1.segment_type,
                total_blocks, total_bytes, unused_blocks,
                unused_bytes, last_used_extent_file_id,
                last_used_extent_block_id, last_used_block);

    free_pct := trunc((1 - (unused_blocks/total_blocks)) * 100);

    if unused_bytes > 200*1024*1024 then
      dbms_output.put(c1.segment_type||' '||c1.owner||'.'||c1.segment_name);
      dbms_output.put(' - ');
      dbms_output.put(to_char(trunc(unused_blocks)));
      dbms_output.put(' from '||to_char(trunc(total_blocks)));
      dbms_output.put(' blocks are empty (');
      dbms_output.put(trunc(unused_bytes/1024/1024));
      dbms_output.put(' Meg/ '||free_pct||'%)');
      dbms_output.new_line;
    end if;
  end loop;
end;
/
