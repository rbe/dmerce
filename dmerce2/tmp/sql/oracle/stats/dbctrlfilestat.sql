SET PAGES 50000
COL PCT_USED FORMAT 990.09

-- Controlfile creation parameters:
-- Type DATAFILE    is for MAXDATAFILES
-- Type REDO LOG    is for MAXLOGFILES
-- Type LOG HISTORY is for MAXLOGHISTORY
-- Type REDO THREAD is for MAXINSTANCES
-- No entry for MAXLOGMEMBERS (?)
tti "Control File Statistics"
SELECT *
  FROM dbctrlfilestat;
tti off;

