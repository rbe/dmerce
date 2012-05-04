SET termout OFF
SET feedback OFF
SET linesize 132
SET serveroutput ON SIZE 100000

SPOOL alter-indexes.sql

BEGIN
	FOR rs IN (SELECT index_name FROM user_indexes) LOOP
		DBMS_OUTPUT.PUT_LINE('ALTER INDEX ' || rs.index_name || ' REBUILD;');
	END LOOP;
END;
/

SPOOL OFF
