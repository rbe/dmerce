-- #############################################################################################
--
-- %Purpose: Show all Schmea Objects (Tables,Synonyms,Views,Sequences,Indexes)
--
-- Use:      Jeder Oracle User
--
-- #############################################################################################
--
spool show_all_objects.lst
set feed off
set pagesize 10000
ttitle off

column datum new_value datum noprint
column owner format A10 heading 'Owner'
column object_name format A38 heading 'Object-|Name'
column object_type format A10 heading 'Object-|Type'
column created format A10 heading 'Created'
column status format A8 heading 'Status'

ttitle left 'Show all Schema Objects (Tables,Synonyms,Views,Sequences,Indexes)' skip 2

break on owner skip 1 on object_type

select to_char(sysdate, 'MM/DD/YY') datum,
    substr(owner,1,10) owner,
    substr(object_type,1,10) object_type,
    substr(object_name,1,38) object_name,
    substr(created,1,11) created,
    substr(status,1,8) status
from dba_objects
where substr(owner,1,10) not in ('SYS','SYSTEM','PUBLIC','DBSNMP')
order by 1,2,3,4;

spool off;
exit;
