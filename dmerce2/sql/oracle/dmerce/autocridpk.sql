SET feedback OFF
SET echo OFF
SET linesize 132
SET serveroutput ON SIZE 1000000

SPOOL autoidpk.sql

BEGIN
	FOR rs1 IN (SELECT table_name FROM user_tables WHERE table_name NOT LIKE '%AUDIT') LOOP
		FOR rs2 IN (SELECT column_name 
                              FROM user_tab_columns
                             WHERE table_name = rs1.table_name
                               AND column_name = 'ID') LOOP
			DBMS_OUTPUT.PUT_LINE('ALTER TABLE ' || LOWER(rs1.table_name) || ' ADD (CONSTRAINT c_'|| dmerce_sys.p_string.f_stripaeiou(LOWER(rs1.table_name)) ||'_pk PRIMARY KEY (id));');
		END LOOP;
	END LOOP;
END;
/

SPOOL OFF
