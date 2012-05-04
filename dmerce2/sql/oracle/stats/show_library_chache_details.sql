-- #############################################################################################
--
-- %Purpose: Show detailled Report of Library Cache Usage in the Shared Pool of the Instance
--
-- #############################################################################################
--
-- Das Diagramm «Library Cache Details» zeigt Detailinformationen
-- des Library Cache im Shared Pool der Instance. Der Library Cache
-- enthält SQL und PL/SQL Code in geparster Form. Es ist wichtig, dass
-- die Ratio für diese Bereiche nahezu 100% beträgt.
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
ttitle left 'Library Cache Details' -
skip 2

select namespace,gets,gethits,
  round(gethitratio*100) "RATIO%",
  pins,pinhits,round(pinhitratio*100) "RATIO%"
from v$librarycache
 order by namespace;
