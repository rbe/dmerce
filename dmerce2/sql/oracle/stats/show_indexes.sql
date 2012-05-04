-- #############################################################################################
--
-- %Purpose: Show Indexes for a Schema Owner
--
-- Use:      Needs Oracle DBA Access
--
-- #############################################################################################
--
spool show_indexes.lst
set pause off
set feed off;
set pagesize 10000;
set wrap off;
set linesize 200;
set heading on;
set tab on;
set scan on;
set verify off;

column index_name format a25 wrap heading 'Index|Name'
column uni format a5 heading 'Uniq-|ness'
column tablespace_name format a10 wrap heading 'Tablespace'
column table_name noprint new_value tab
column column_name format a26 heading 'Column|Name'
column table_owner format a10 heading 'Table|Owner'

ttitle left 'Indexes of Tabelle: 'tab skip 2

ACCEPT user_namen CHAR PROMPT 'Schema Owner: '

break on table_name skip page -
on table_owner skip 2 -
on index_name skip -
on uni -
on tablespace_name

  select i.table_owner,
    i.index_name,
    c.table_name,
    c.column_name,
    decode(i.uniqueness,'UNIQUE','YES','NONUNIQUE','NO','???') uni,
    i.tablespace_name
  from dba_ind_columns c, dba_indexes i
  where i.table_name = c.table_name
  and i.index_name = c.index_name
  and i.table_name like upper('%')
  and i.table_owner = c.table_owner
    and i.table_owner LIKE UPPER('&user_namen')
  order by i.table_owner,
      c.table_name,
      i.uniqueness desc,
      c.index_name,
      c.column_position;
spool off;
