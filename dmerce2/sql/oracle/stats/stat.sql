SET LINESIZE 132
SET PAGESIZE 50000

@$DMERCE_HOME/sql/oracle/stats/statviews.sql

--
-- INSTANCE
--

TTITLE "Database Information"
COL host_name FORMAT a20
COL banner FORMAT a30
SELECT * FROM dbinfo;
TTITLE OFF

TTITLE "Database Uptime"
SELECT * FROM dbuptime;
TTITLE OFF

TTITLE "Database Options"
COL parameter FORMAT a40
COL value FORMAT a60

SELECT * FROM dboptions;
TTITLE OFF

TTITLE "Database Parameters"
COL name FORMAT a40
SELECT * FROM dbparameter;
TTITLE OFF

TTITLE "Database Parameters - Files"
SELECT * FROM dbparameterfiles;
TTITLE OFF

TTITLE "init.ora Parameters"
COL name FORMAT a30
COL value FORMAT a30
COL isdefault FORMAT a10
COL type FORMAT a10
COL description FORMAT a45
SELECT * FROM dblistinitoraparams;
TTITLE OFF

TTITLE "init.ora Parameters 2"
SELECT * FROM dblistinitoraparams2;
TTITLE OFF

TTITLE "Database Background Processes"
SELECT * FROM dbbgproc;
TTITLE OFF

TTITLE "Database System Global Area"
COL value FORMAT 9.99
SELECT name, value "Bytes", value/1024/1024 "MB" FROM dbsga;
TTITLE OFF

TTITLE "Database Block Size"
SELECT * FROM dbblocksize;
SELECT * FROM dbmaxpossibleextents;
SELECT * FROM dbrecommendedminextsize;
TTITLE OFF

TTITLE "Database Control Files"
COL name FORMAT a60
SELECT * FROM dbctrlfiles;
TTITLE OFF

TTITLE "Database Log Files"
COL member FORMAT a40
SELECT * FROM dblogfiles;
TTITLE OFF

TTITLE "Database Rollback Segments"
COL segment_name FORMAT a15
COL status FORMAT a7
SELECT * FROM dbrbs;
TTITLE OFF

TTITLE "Database Datafiles"
COL file_name FORMAT a40
COL status FORMAT a15
SELECT * FROM dbdatafiles;
TTITLE OFF

TTITLE "Statistics - Database Tablespaces"
COL max FORMAT a20
SELECT * FROM dbtbs;
TTITLE OFF

--
-- STATS
--

TTITLE "Statistics - Database System Global Area"
COL value FORMAT 9.99
SELECT * FROM dbsgamemstat;
TTITLE OFF

TTITLE "Statistics - Database Cache Hit Ratios"
SELECT * FROM dbsgalibcachehr;
SELECT * FROM dbsgalibcachepins;
SELECT * FROM dbsgarowcachehr;
SELECT * FROM dbsgabufcachehr;
TTITLE OFF

TTITLE "Statistics - Database Control Files"
COL type FORMAT a30
SELECT * FROM dbctrlfilestat;
TTITLE OFF

TTITLE "Statistics - Database Log Files"
COL member FORMAT a40
SELECT * FROM dblogfilecontention;
TTITLE OFF

TTITLE "Statistics - Database Rollback Segments"
COL segment_name FORMAT a15
COL status FORMAT a7
SELECT * FROM dbrbsstats;
TTITLE OFF

TTITLE "Statistics - Database Tablespaces"
COL max FORMAT a20
SELECT * FROM dbtbsusage;
TTITLE OFF

TTITLE "Statistics - Database Space Usage"
SELECT * FROM dbgrowth;
SELECT * FROM dbspaceusage;
SELECT * FROM dbspacetotal;
TTITLE OFF

TTITLE "Database Fragmentation"
SELECT * FROM dbtablefragmentation;
SELECT * FROM dbindexfragmentation;
TTITLE OFF

--
-- LOCKS/LATCHES
--

TTITLE "Database Locks, Latches, Waits"
PROMPT Locks and Latches
SELECT * FROM dblockslatches;
PROMPT Show Locks
SELECT * FROM dbshowlocks;
PROMPT Session Waits
SELECT * FROM dbsessionwait;
PROMPT Active Transactions
SELECT * FROM dbactivetransactions;
TTITLE OFF

TTITLE "Database System Events"
COL event FORMAT a40
SELECT * FROM v$system_event ORDER BY event;
TTITLE OFF

--
-- USERS/ROLES
--

TTITLE "Database Users and Roles"
COL username FORMAT a30
COL default_tablespace FORMAT a20
COL temporary_tablespace FORMAT a20
SELECT * FROM dbusers;
SELECT * FROM dbroles;
TTITLE OFF

--@$DMERCE_HOME/sql/oracle/stats/dropstatviews.sql
