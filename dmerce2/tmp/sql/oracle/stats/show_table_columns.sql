-- #############################################################################################
--
-- %Purpose: Show Table Structure (Column-Name, Datentyp, etc) for all Schema-Owners
--
-- #############################################################################################
--
spool show_table_columns.lst
set feed off
set pagesize 10000
ttitle off
--
column table_name noprint new_value tab
column owner format a10 heading 'Table|Owner'
column column_name format a30 heading 'Column|Name'
column data_type format a9 heading 'Data|Type'
column nullable format a8 heading 'Nulls ?'
column data_length format a14 heading 'Maximum Data|Length [Bytes]'
column data_precision format a9 heading 'Data|Precision'
column data_scale format a5 heading 'Data|Scale'
--
ttitle center 'Columns of Table: 'tab skip 2
--
break on table_name skip page -
on owner skip 2 -
--
select  owner,
        table_name,
      column_name,
        data_type,
        to_char(data_length) data_length,
        to_char(data_precision) data_precision,
        to_char(data_scale) data_scale,
      decode(nullable,'Y','','N','NOT NULL') nullable
 from dba_tab_columns
where table_name like upper('%')
  and upper(owner) not in ('SYSTEM','SYS','DBSNMP')
order by owner,
   table_name,
     column_name;
--
spool off;
