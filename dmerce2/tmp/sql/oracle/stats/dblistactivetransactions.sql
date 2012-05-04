COLUMN name FORMAT a8
COLUMN username FORMAT a8
COLUMN osuser FORMAT a8
COLUMN start_time FORMAT a17
COLUMN status FORMAT a12

TTITLE 'Active transactions'
SELECT *
  FROM dblistactivetransactions;
TTITLE OFF
