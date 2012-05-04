SET SERVEROUTPUT ON
SET HEADING OFF
SET FEEDBACK ON
--OFF

SPOOL $ORACLE_HOME/hot_backup.sql

DECLARE
	fname	VARCHAR2(55);
	tname	VARCHAR2(55);
	tname1	VARCHAR2(55);
	cpprg	VARCHAR(10) := 'scp'
	cpdest	VARCHAR2(200) := 'oracle@anotherhost:/tmp';

	CURSOR tspaces IS
		SELECT tablespace_name, file_name
		  FROM v$datafile, sys.dba_data_files
		 WHERE enabled LIKE '%WRITE%'
		   AND file# = file_id
		 ORDER BY 1;

BEGIN
	DBMS_OUTPUT.ENABLE(32000);
	DBMS_OUTPUT.PUT_LINE('spool /tmp/hotback');
	OPEN tspaces;
	FETCH tspaces INTO tname, fname;
	tname1 := tname;
	DBMS_OUTPUT.PUT_LINE('alter system switch logfile;');
	DBMS_OUTPUT.PUT_LINE('alter system switch logfile;');
	DBMS_OUTPUT.PUT_LINE('alter system switch logfile;');
	DBMS_OUTPUT.PUT_LINE('alter tablespace ' || tname || ' begin backup;');
	WHILE tspaces%FOUND LOOP
		IF tname1 != tname THEN
			DBMS_OUTPUT.PUT_LINE('alter tablespace ' || tname1 || ' end backup;');
			DBMS_OUTPUT.PUT_LINE('alter tablespace ' || tname || ' begin backup;');
			tname1 := tname;
		END IF;
		DBMS_OUTPUT.PUT_LINE('!' || cpprg || ' || fname || ' ' || cpdest);
		FETCH tspaces INTO tname, fname;
	END LOOP;
	DBMS_OUTPUT.PUT_LINE('alter tablespace ' || tname1 || ' end backup;');
	CLOSE tspaces;
	DBMS_OUTPUT.PUT_LINE('alter system switch logfile;');
	DBMS_OUTPUT.PUT_LINE('alter system switch logfile;');
	DBMS_OUTPUT.PUT_LINE('alter system switch logfile;');
	DBMS_OUTPUT.PUT_LINE('alter database backup controlfile to trace;');
	DBMS_OUTPUT.PUT_LINE('spool off');
END;
/

SPOOL OFF

SET HEADING ON
SET FEEDBACK ON
SET SERVEROUTPUT OFF
