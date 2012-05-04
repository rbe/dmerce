SET feedback OFF
SET echo OFF
SET serveroutput ON SIZE 1000000
SET linesize 132

SPOOL autoforeignkeys.sql

DECLARE
	i		NUMBER;
        fld_name        VARCHAR2(100);
        pos_id          NUMBER;
        tbl_name        VARCHAR2(100);
        tbl_name_count  NUMBER;
        --c_count         NUMBER;
        c_name          VARCHAR2(100);

BEGIN
	i := 0;
	FOR rs1 IN (SELECT table_name FROM user_tables) LOOP
	        FOR rs2 IN (SELECT column_name
                              FROM user_tab_columns
                             WHERE table_name = rs1.table_name
                               AND column_name LIKE '%ID') LOOP
	                fld_name := LOWER(rs2.column_name);
        	        IF LENGTH(fld_name) > 2 THEN
                	        --SELECT COUNT(*) INTO c_count
                        	--  FROM user_constraints
	                        -- WHERE constraint_name LIKE 'c_%' || rs1.table_name;
                        	pos_id := INSTR(fld_name, 'id');
	                        tbl_name := REPLACE(fld_name, 'id', '');
        	                SELECT COUNT(*) INTO tbl_name_count
                	          FROM user_tables
                        	 WHERE table_name = UPPER(tbl_name);
	                        IF tbl_name_count = 1 THEN
					i := i + 1;
        		                c_name := 'c_' || LOWER(rs1.table_name) || i || '_fk';
	                	        c_name := dmerce_sys.p_string.f_stripaeiou(c_name);
        	                        DBMS_OUTPUT.PUT_LINE('ALTER TABLE ' || LOWER(rs1.table_name) || ' ADD (CONSTRAINT ' || c_name || ' FOREIGN KEY (' || LOWER(rs2.column_name)  || ') REFERENCES ' || tbl_name || ' (id));');
                	        END IF;
	                END IF;
		END LOOP;
        END LOOP;
END;
/

SPOOL OFF
