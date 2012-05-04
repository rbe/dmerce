SET feedback OFF
SET termout OFF
SET echo OFF
SET serveroutput ON SIZE 100000
SET linesize 132

SPOOL mclginit-spool.sql

BEGIN
	FOR rs1 IN (SELECT table_name FROM user_tables) LOOP
		FOR rs2 IN (SELECT column_name
                              FROM user_tab_columns
                             WHERE table_name = rs1.table_name
                               AND column_name = 'MANDANT') LOOP
			DBMS_OUTPUT.PUT_LINE('UPDATE ' || LOWER(rs1.table_name) || ' SET mandant = ''0-'';');
		END LOOP;
	END LOOP;
END;
/

EXIT;
