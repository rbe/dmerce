SET echo OFF
SET heading OFF
SET linesize 132
SET serveroutput ON SIZE 100000

ACCEPT l_tbl_name PROMPT 'Enter table name: '

DECLARE
	r_tbl_name	VARCHAR2(30);
	r_col_name	VARCHAR2(30);
	col_name	VARCHAR2(30);
BEGIN
	DBMS_OUTPUT.PUT_LINE('TABLE (CONSTRAINT) -> REF. TABLE.FIELD');
	FOR rc1 IN (SELECT table_name, constraint_name, r_constraint_name
                      FROM user_constraints
                     WHERE table_name = UPPER('&l_tbl_name')
                       AND constraint_type = 'R') LOOP
		SELECT column_name INTO col_name
		  FROM user_cons_columns
	         WHERE constraint_name = rc1.constraint_name;
		FOR rc3 IN (SELECT table_name, column_name
		              FROM user_cons_columns
		             WHERE constraint_name = rc1.r_constraint_name) LOOP
			r_tbl_name := rc3.table_name;
			r_col_name := rc3.column_name;
		END LOOP;
	DBMS_OUTPUT.PUT_LINE(rc1.table_name || '.' || col_name || ' (' || rc1.constraint_name || ') -> ' || r_tbl_name || '.' || r_col_name);
	END LOOP;
END;
/
