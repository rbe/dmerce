-- #############################################################################################
--
-- %Purpose: Show Library Cache Hit % for the Shared Pool of the Instance
--
-- #############################################################################################
--
-- Der Library Cache ist Teil des Shared Pools.
-- Cache Misses im Library Cache sind «sehr teuer», da das SQL-Statement
-- geladen, geparst und ausgeführt werden muss. Hier gilt die Regel, dass
-- 99 % aller SQL-Statements in geparster Form im Memory vorliegen müssen.
-- Ist dies nicht der Fall so muss der Wert SHARED_POOL_SIZE erhöht werden.
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
ttitle left 'Show Library Cache Hit %' -
skip 2

select (1-(sum(reloads)/sum(pins))) *100 "Library Cache Hit %"
  from v$librarycache;
