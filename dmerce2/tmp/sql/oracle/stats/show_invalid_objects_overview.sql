-- #############################################################################################
--
-- %Purpose: Overview (OWNER, OBJECT_NAME, OBJECT_TYPE) of all INVALID Objects
--
-- Use:      Needs Oracle DBA Access
--
-- #############################################################################################
--
spool show_invalid_objects_summary.lst
set pause off
set feed off;
set pagesize 10000;
set wrap off;
set linesize 200;
set heading on;
set tab on;
set scan on;
set verify off;

ttitle left 'Overview of invalid objects' -
skip 2

column object_type format a25 wrap heading 'Object|Type'
column object_name format a25 wrap heading 'Object|Name'
column status format a8 heading 'Status'
column owner format a10 wrap heading 'Owner'

SELECT owner, object_name, object_type, status
 FROM dba_objects
WHERE status != 'VALID';

spool off;
