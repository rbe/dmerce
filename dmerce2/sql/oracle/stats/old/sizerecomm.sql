prompt Database block size:

select to_number(value) "Block size in bytes"
from   sys.v_$parameter
where  name = 'db_block_size'
/

prompt Max number of possible extents (if not set to UNLIMITED)
prompt is db_block_size/16-7

select to_number(value)/16-7 "MaxExtents"
from   sys.v_$parameter
where  name = 'db_block_size'
/

prompt The recommended min extent size is a multiple of
prompt db_block_size * db_file_multiblock_read_count. This gives
prompt the chunks Oracle ask from the OS when doing read-ahead
prompt with full table scans.

select to_number(a.value) * to_number(b.value) / 1024 "Min extent size in K"
from   sys.v_$parameter a, sys.v_$parameter b
where  a.name = 'db_block_size'
  and  b.name = 'db_file_multiblock_read_count'
/
