-- #############################################################################################
--
-- %Purpose: Show Database Space used for all Schema-Owners
--
-- #############################################################################################
--
spool show_used_space_by_users.lst

ttitle  left  'Used Space for each ORACLE User' -
skip 2

set feed off
set linesize 80
set pagesize 5000
set underline '-'

break on owner skip 1 on tablespace_name on segment_type
column owner format A16 heading 'Owner'
column segment_type format A10 heading 'Object'
column tablespace_name format A26 heading 'Tablespace'
column bytes format 9,999,999,999 heading 'Used Space|[Bytes]'
column blocks format      999,999 heading 'Used Space|[Blocks]'
compute sum of bytes blocks on owner
--
-- Count Space for each Oracle Object
--
select  substr(owner,1,16) owner,
  substr(tablespace_name,1,26) tablespace_name,
  substr(segment_type,1,10) segment_type,
  sum(bytes) bytes,
  sum(blocks) blocks
from sys.dba_extents
group by
  substr(owner,1,16),
  substr(tablespace_name,1,26),
  substr(segment_type,1,10)
order by 1,2,3;
clear breaks
clear computes
clear column

spool off;
