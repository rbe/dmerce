SET PAGESIZE 600
SET LINESIZE 132
SET HEAD ON
SET ECHO OFF
SET VERIFY OFF

BREAK ON Created_on

COLUMN Created_on NEW_VALUE _DATE
SELECT TO_CHAR(SYSDATE, 'fmMonth DD, YYYY : hh:mi') created_on FROM DUAL;
CLEAR BREAKS

PROMPT ***** Database information *****
COLUMN instance_name FORMAT a8 Heading "Instance"
COLUMN name FORMAT a8 Heading "DB Name"
COLUMN host_name FORMAT a12 Heading "Hostname"
COLUMN banner FORMAT a40;
SELECT *
  FROM dbinfo;

PROMPT ***** Database options available *****
COLUMN parameter FORMAT a40
COLUMN value FORMAT a7
SELECT *
  FROM dboptions;
CL COLUMN

PROMPT ***** Active Background processes *****
COLUMN description FORMAT a30
SELECT *
  FROM dbbgproc;

PROMPT ***** Database SGA allowcation *****
SELECT * FROM v$sga;

PROMPT ***** Control file locations *****
COLUMN name FORMAT a80
COLUMN status FORMAT a20
SELECT *
  FROM dbctrlfiles;

PROMPT ***** Info about the REDO log files *****
COLUMN member FORMAT a80
SELECT *
  FROM dblogfiles;

PROMPT ***** Information about the Rollback segments *****
COLUMN segment_name FORMAT a10
COLUMN tablespace_name FORMAT a15
SELECT *
  FROM dbrollbacksegs;

PROMPT ***** Tablespace and data file locations *****
COLUMN max FORMAT a10
COLUMN pct FORMAT 999
COLUMN min FORMAT 999
SELECT *
  FROM dbtbs;

COLUMN file_name FORMAT a50
BREAK ON tablespace_name SKIP 1
COMPUTE SUM LABEL 'Total' OF MB ON report
BREAK ON report
SELECT *
  FROM dbdatafiles;

PROMPT ***** User information *****
COLUMN default_tablespace FORMAT a15
COLUMN temporary_tablespace FORMAT a15
COLUMN profile FORMAT a15
SELECT *
  FROM dbusers;

PROMPT ***** User Roles *****
SELECT *
  FROM dbroles;

REM SELECT the parameters in two different way

PROMPT ***** init.ora parameters *****
COLUMN name FORMAT a35
COLUMN value FORMAT a15
SELECT *
  FROM dbparameter;

CL COLUMN
COLUMN name FORMAT a33
PROMPT ***** settings for the init.ora about the locations of the paths *****
SELECT *
  FROM dbparameterfiles;

CL COLUMN
CL BREAK
PROMPT ***** Database report creation completed *****

EXIT;
