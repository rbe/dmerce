SET PAGESIZE 50000
tti "Database growth per month for last year"
COL month FORMAT a20
SELECT *
  FROM dbgrowth;
tti off
