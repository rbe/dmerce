-- #############################################################################################
--
-- %Purpose: Show Views for Schmea-Owner
--
-- #############################################################################################
--
spool show_views.lst
ttitle left 'All Database Views' skip 2
set feed off
set pagesize 30000
set linesize 200
break on owner skip 1
column owner format A5 heading 'View|Owner'
column view_name format A20 heading 'View|Name'
column text_length format 9999999 heading 'View-Length|[Bytes]'

select  substr(owner,1,5) owner,
      substr(view_name,1,60) view_name,
      text_length
from dba_views
where owner not in ('SYS','DBSNMP','SYSTEM')
order by 1,2,3;
spool off;
