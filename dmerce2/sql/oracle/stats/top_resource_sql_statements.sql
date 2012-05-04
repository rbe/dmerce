-- #############################################################################################
--
-- %Purpose: Show the most resource intensive SQL statements that have been recently executed
--
-- Displays a list of the most resource intensive SQL statements
-- that have been recently executed.  Resource use is ranked by the
-- number of SGA buffer gets, which is a good indicator of the work done.
-- Only statements that are still cached in the SGA are searched -
-- statements are discarded using an LRU algorithim.
--
-- #############################################################################################
--
set linesize 1200 verify off feedback 100

accept gets default 100000 prompt "Min buffer gets [100,000] "

col sql_text for a1000

select
    s.BUFFER_GETS,
    s.DISK_READS,
    s.ROWS_PROCESSED,
    s.EXECUTIONS,
    substr(u.NAME,1,10) Username,
    s.SQL_TEXT
from
    v$sqlarea s,
    sys.user$ u
where
    s.buffer_gets > &&gets and
    s.parsing_user_id = u.user# and
    u.name <> 'SYS'
order by
    s.buffer_gets desc
/
set feedback on
