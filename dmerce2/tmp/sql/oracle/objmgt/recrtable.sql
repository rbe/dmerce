rem -----------------------------------------------------------------------
rem # File Name: mktable.sql
rem #
rem # Purpose:   Script to dump table creation script
rem #            for the username (schema) provided as
rem #            the parameter.
rem #
rem #            This is script is useful for cases where
rem #            Reverse Engineering is required. The resulting
rem #            SQL is sent to an output file:
rem #
rem #                    tbl_<SCHEMA_NAME>.lst
rem #
rem -----------------------------------------------------------------------

set arraysize 1
set echo off
set heading off
set feedback off
set verify off
set pagesize 0
set linesize 79
define 1 = &&SCHEMA_NAME
spool tbl_&&SCHEMA_NAME
set termout off
col x noprint
col y noprint
select  'rem   ****    Create Table DDL for '||chr(10)||
        'rem   ****    '||username||''''||'s tables'||chr(10)||chr(10)
from    dba_users
where     username      = upper ('&&1')
/
select  table_name y,
        0 x,
        'create table ' ||
        rtrim(table_name) ||
        '('
from    dba_tables
where     owner = upper('&&1')
union
select  tc.table_name y,
        column_id x,
        rtrim(decode(column_id,1,null,','))||
        rtrim(column_name)|| ' ' ||
        rtrim(data_type) ||
        rtrim(decode(data_type,'DATE',null,'LONG',null,
               'NUMBER',decode(to_char(data_precision),null,null,'('),
               '(')) ||
        rtrim(decode(data_type,
               'DATE',null,
               'CHAR',data_length,
               'VARCHAR2',data_length,
               'NUMBER',decode(to_char(data_precision),null,null,
                 to_char(data_precision) || ',' || to_char(data_scale)),
               'LONG',null,
               '******ERROR')) ||
        rtrim(decode(data_type,'DATE',null,'LONG',null,
               'NUMBER',decode(to_char(data_precision),null,null,')'),
               ')')) || ' ' ||
        rtrim(decode(nullable,'N','NOT NULL',null))
from    dba_tab_columns tc,
        dba_objects o
where   o.owner = tc.owner
and     o.object_name = tc.table_name
and     o.object_type = 'TABLE'
and     o.owner = upper('&&1')
union
select  table_name y,
        999999 x,
        ')'  || chr(10)
        ||' STORAGE('                           || chr(10)
        ||' INITIAL '    || initial_extent      || chr(10)
        ||' NEXT '       || next_extent         || chr(10)
        ||' MINEXTENTS ' || min_extents         || chr(10)
        ||' MAXEXTENTS ' || max_extents         || chr(10)
        ||' PCTINCREASE '|| pct_increase        || ')' ||chr(10)
        ||' INITRANS '   || ini_trans         || chr(10)
        ||' MAXTRANS '   || max_trans         || chr(10)
        ||' PCTFREE '    || pct_free          || chr(10)
        ||' PCTUSED '    || pct_used          || chr(10)
        ||' PARALLEL (DEGREE ' || DEGREE || ') ' || chr(10)
        ||' TABLESPACE ' || rtrim(tablespace_name) ||chr(10)
        ||'/'||chr(10)||chr(10)
from    dba_tables
where   owner = upper('&&1')
order by 1,2
/
