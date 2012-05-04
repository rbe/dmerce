SET termout OFF
SET feedback OFF
SET linesize 132
SET serveroutput ON SIZE 100000

SPOOL dropalluserseq.sql

BEGIN
	FOR rs IN (SELECT sequence_name FROM user_sequences) LOOP
		DBMS_OUTPUT.PUT_LINE('DROP SEQUENCE ' || rs.sequence_name || ';');
	END LOOP;
END;
/

SPOOL OFF
