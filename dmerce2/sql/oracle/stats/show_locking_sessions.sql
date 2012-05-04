-- ###########################################################################
--
-- %Purpose: Show sessions (to be killed ...) locking other sessions
--
--  Remarks: To be run while connected as SYS.
--           If you get 'no rows selected', then there are no
--           blocking sessions.
--
-- ###########################################################################
--
SELECT  s.username "User",
        s.sid "SID",
        o.owner "Object Owner",
        o.object_name "Object Name",
        a.sql_text "SQL-Text",
        s.row_wait_file# "File-Nr",
        s.row_wait_block# "Block-Nr",
        s.row_wait_row# "Record-Nr"
FROM    v$session s, v$sqlarea a, dba_objects o
WHERE   o.object_id = s.row_wait_obj#
AND     s.sql_address = a.address
AND     s.row_wait_obj# > 0
/
