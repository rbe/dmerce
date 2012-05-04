-- #############################################################################################
--
-- %Purpose: Show Primary and Foreign Key Relationsships
--
-- Use:      Needs Oracle DBA Access
--
-- #############################################################################################
--
spool show_fk_pk_relations.lst
set feed off
set pagesize 10000
ttitle off
--
ttitle left  'Primary and Foreign Key Relationsships' skip 2
set feed off
set pagesize 10000
--
column datum new_value datum noprint
column for_owner format A5 heading 'Table|Owner'
column pri_tsname format A10 heading 'Tablespace'
column for_table format A17 heading 'From|Foreign|Table'
column for_col format A16 heading 'From|Foreign|Column'
column pri_table format A17 heading 'To|Primary|Table'
column pri_col format A16 heading 'To|Primary|Column'
break on for_owner skip 1

select  a.owner for_owner,
        e.tablespace_name pri_tsname,
        a.table_name for_table,
        c.column_name for_col,
        b.table_name pri_table,
        d.column_name pri_col
from dba_constraints a,
     dba_constraints b,
     dba_cons_columns c,
     dba_cons_columns d,
     dba_tables e
where a.owner not in ('SYS','SYSTEM')
  and a.r_constraint_name = b.constraint_name
  and a.constraint_type = 'R'
  and b.constraint_type = 'P'
  and a.r_owner = b.owner
  and a.constraint_name = c.constraint_name
  and a.owner = c.owner
  and a.table_name = c.table_name
  and b.constraint_name = d.constraint_name
  and b.owner = d.owner
  and b.table_name = d.table_name
  and b.table_name = e.table_name
order by a.owner,a.table_name;
