-- #############################################################################################
--
-- %Purpose: Show Sequences for Schema Owner
--
-- #############################################################################################
--
ACCEPT user_namen CHAR PROMPT 'Schema Owner: '

set feed off;
set pagesize 10000;
set wrap off;
set linesize 200;
set heading on;
set tab on;
set scan on;
set verify off;
--
spool show_sequences.lst
ttitle off

column datum new_value datum noprint
column sequence_owner format A20 heading 'Sequence|Owner'
column sequence_name noprint new_value sequence
column min_value format 99 heading 'Minimal|Value  '
column max_value format 9.999EEEE heading 'Maximal    |Value      '
column increment_by format 99 heading 'Incr|By  '
column last_number format 9999999 heading 'Last    |Number  '
column cache_size format 9999 heading 'Cache|Size '
column order_flag format a7 heading 'Order ?'
column cycle_flag format a7 heading 'Cycle ?'

ttitle left 'Properties for Sequence: 'sequence skip 2

break on sequence_name skip page -
on sequence_owner skip 2 -

select  to_char(sysdate, 'MM/DD/YY') datum,
    substr(sequence_owner,1,12) sequence_owner,
    substr(sequence_name,1,27) sequence_name,
    min_value,
    max_value,
    increment_by,
    last_number,
    cache_size,
                decode(order_flag,
                  'Y','Yes',
                  'N','No') order_flag,
                decode(cycle_flag,
                  'Y','Yes',
                  'N','No') cycle_flag
from dba_sequences
where sequence_owner LIKE UPPER('&user_namen')
order by 1,2,3,4;
spool off;
