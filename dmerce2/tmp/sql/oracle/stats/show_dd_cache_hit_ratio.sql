-- #############################################################################################
--
-- %Purpose: Show Data Dictionary Cache Hit % for active Instance since last Startup
--
-- #############################################################################################
--
-- Der Data Dictionary Cache ist Teil des Shared Pools. Nach dem Instance
-- Startup werden die Data Dictionary Informationen ins Memory geladen.
-- Nach einer gewissen Betriebszeit sollte sich ein stabiler Zustand
-- einstellen. Der Data Dictionary Cache Miss sollte kleiner als 10 % sein.
--
-- #############################################################################################
--
set feed off;
set pagesize 10000;
set wrap off;
set linesize 200;
set heading on;
set tab on;
set scan on;
set verify off;
--
ttitle left 'Show Data Dictionary Cache Hit %' -
skip 2

select (1- (sum(getmisses)/sum(gets))) * 100 "Data Dictionary Cache Hit %"
  from v$rowcache;
