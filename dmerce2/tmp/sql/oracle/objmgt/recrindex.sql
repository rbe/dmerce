rem -----------------------------------------------------------------------
rem # File Name: mkindex.sql
rem #
rem # Purpose:   Script that produces index creation script
rem #            Based on User Schema (user name specified).
rem #            Input Value 1:  The name of the user which to
rem #                            dump an index creation script.
rem #
rem #            This is script is useful for cases where
rem #            Reverse Engineering is required. The resulting
rem #            SQL is sent to an output file:
rem #
rem #                         ind_<SCHEMA_NAME>.lst
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
spool ind_&&SCHEMA_NAME
set termout off
col y noprint
col x noprint
col z noprint
select  'rem   ****    Create Index DDL for '||chr(10)||
        'rem   ****    '||username||''''||'s tables'||chr(10)||chr(10)
from    dba_users
where   username      = upper ('&&1')
/
select  table_name z,
        index_name y,
        -1 x,
        'create ' || rtrim(decode(uniqueness,'UNIQUE','UNIQUE',null))
        || ' index ' ||
        rtrim(index_name)
from    dba_indexes
where   table_owner = upper('&&1')
union
select  table_name z,
        index_name y,
        0 x,
        'on ' ||
        rtrim(table_name) ||
        '('
from    dba_indexes
where   table_owner = upper('&&1')
union
select  table_name z,
        index_name y,
        column_position x,
        rtrim(decode(column_position,1,null,','))||
        rtrim(column_name)
from    dba_ind_columns
where   table_owner = upper('&&1')
union
select  table_name z,
        index_name y,
        999999 x,
        ')'  || chr(10)
        ||'unrecoverable ' || chr(10)
        ||'STORAGE('                            || chr(10)
        ||'INITIAL '     || initial_extent      || chr(10)
        ||'NEXT '        || next_extent         || chr(10)
        ||'MINEXTENTS ' || '1' || chr(10)
        ||'MAXEXTENTS ' || max_extents  || chr(10)
        ||'PCTINCREASE '|| '0'  ||')'   || chr(10)
        ||'INITRANS '   || ini_trans         || chr(10)
        ||'MAXTRANS '   || max_trans         || chr(10)
        ||'PCTFREE '    || '0' || chr(10)
        ||'TABLESPACE ' || tablespace_name ||chr(10)
        ||'PARALLEL (DEGREE ' || DEGREE || ') ' || chr(10)
        ||'/'||chr(10)||chr(10)
from    dba_indexes
where   table_owner = upper('&&1')
order by 1,2,3
/
