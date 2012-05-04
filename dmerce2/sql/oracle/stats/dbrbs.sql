COLUMN "Rollback Segment"       format a16
COLUMN "Size (Kb)"              format 9,999,999
COLUMN "Gets"                   format 999,999,990
COLUMN "Waits"                  format 9,999,990
COLUMN "% Waits"                format 90.00
COLUMN "# Shrinks"              format 999,990
COLUMN "# Extends"              format 999,990

PROMPT Rollback Segment Statistics
PROMPT ***************************

SELECT *
  FROM dbrbsstat;
