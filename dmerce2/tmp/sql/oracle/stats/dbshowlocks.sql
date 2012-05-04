SET TRIMSPOOL ON
SET LINESIZE 132
SET PAGESIZE 60
TTITLE "SHOW LOCKS"
COLUMN osuser HEADING 'OS|Username' FORMAT a7 truncate
COLUMN process HEADING 'OS|Process' FORMAT a7 truncate
COLUMN machine HEADING 'OS|Machine' FORMAT a10 truncate
COLUMN program HEADING 'OS|Program' FORMAT a25 truncate
COLUMN object HEADING 'Database|Object' FORMAT a25 truncate
COLUMN lock_type HEADING 'Lock|Type' FORMAT a4 truncate
COLUMN mode_held HEADING 'Mode|Held' FORMAT a15 truncate
COLUMN mode_requested HEADING 'Mode|Requested' FORMAT a10 truncate
COLUMN sid HEADING 'SID' FORMAT 999
COLUMN username HEADING 'Oracle|Username' FORMAT a7 truncate
COLUMN image HEADING 'Active Image' FORMAT a20 truncate
COLUMN sid FORMAT 99999
COLUMN waiting_session HEADING 'WAITER' FORMAT 9999
COLUMN holding_session HEADING 'BLOCKER' FORMAT 9999
SELECT *
  FROM dbshowlocks;
TTITLE OFF
