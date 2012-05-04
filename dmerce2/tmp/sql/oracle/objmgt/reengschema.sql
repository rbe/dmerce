SET termout OFF
SET feedback OFF
SET linesize 132
SET serveroutput ON SIZE 100000

SPOOL schema.sql

BEGIN
	DBMS_OUTPUT.PUT_LINE('--');
	DBMS_OUTPUT.PUT_LINE('-- DROP TABLES --');
	DBMS_OUTPUT.PUT_LINE('--');
        FOR rt IN (SELECT tname FROM tab ORDER BY tname) LOOP
                DBMS_OUTPUT.PUT_LINE('DROP TABLE ' || rt.tname || ' CASCADE CONSTRAINTS;');
        END LOOP;
END;
/               

DECLARE 
        v_tname  VARCHAR2(30);
        v_cname  CHAR(32);
        v_type   CHAR(20);
        v_null   VARCHAR2(10);
        v_maxcol NUMBER;
        v_virg   VARCHAR2(1);
BEGIN
	DBMS_OUTPUT.PUT_LINE('--');
	DBMS_OUTPUT.PUT_LINE('-- CREATE TABLES --');
	DBMS_OUTPUT.PUT_LINE('--');
        FOR rt IN (SELECT table_name FROM user_tables ORDER BY 1) LOOP
                v_tname := rt.table_name;
                v_virg := ',';
                DBMS_OUTPUT.PUT_LINE('CREATE TABLE ' || v_tname || ' (');
                FOR rc IN (SELECT table_name, column_name, data_type, data_length, 
                                  data_precision, data_scale, nullable, column_id
                             FROM user_tab_columns tc
                            WHERE tc.table_name = rt.table_name
                         ORDER BY table_name,column_id) LOOP
                	v_cname := rc.column_name;
                        IF rc.data_type='VARCHAR2' THEN
				v_type := 'VARCHAR2(' || rc.data_length || ')';
                        ELSIF rc.data_type='NUMBER' AND rc.data_precision IS NULL AND rc.data_scale = 0 THEN
                        	v_type := 'INTEGER';
                        ELSIF rc.data_type='NUMBER' AND rc.data_precision IS NULL AND rc.data_scale IS NULL THEN
                                v_type := 'NUMBER';
                        ELSIF rc.data_type='NUMBER' AND rc.data_scale = '0' THEN
				v_type := 'NUMBER(' || rc.data_precision || ')';
                        ELSIF rc.data_type='NUMBER' AND rc.data_scale <> '0' THEN
        			v_type := 'NUMBER(' || rc.data_precision || ',' || rc.data_scale || ')';
                        ELSIF rc.data_type='CHAR' THEN
				v_type := 'CHAR(' || rc.data_length || ')';
                        ELSE
				v_type := rc.data_type;
                        END IF;
                                        
                        IF rc.nullable = 'Y' THEN
	                        v_null := 'NULL';
                        ELSE
                                v_null := 'NOT NULL';
	                END IF;
                        SELECT max(column_id) INTO v_maxcol
                          FROM user_tab_columns c
                         WHERE c.table_name=rt.table_name;
                        IF rc.column_id=v_maxcol THEN
                        	v_virg := '';
                        END IF;
                        DBMS_OUTPUT.PUT_LINE(v_cname || v_type || v_null || v_virg);
                END LOOP;
                DBMS_OUTPUT.PUT_LINE(');');
        END LOOP;
END;
/

DECLARE 
        v_virg    VARCHAR2(1);
        v_maxcol  NUMBER;
BEGIN
	DBMS_OUTPUT.PUT_LINE('--');
	DBMS_OUTPUT.PUT_LINE('-- PRIMARY KEYS --');
	DBMS_OUTPUT.PUT_LINE('--');
        FOR rcn IN (SELECT table_name,constraint_name 
                      FROM user_constraints 
                     WHERE constraint_type='P' 
                  ORDER BY table_name) LOOP
                DBMS_OUTPUT.PUT_LINE('ALTER TABLE ' || rcn.table_name || ' ADD (');
                DBMS_OUTPUT.PUT_LINE('CONSTRAINT ' || rcn.constraint_name);
                DBMS_OUTPUT.PUT_LINE('PRIMARY KEY (');
                v_virg := ',';
                FOR rcl IN (SELECT column_name,position 
                              FROM user_cons_columns cl 
                             WHERE cl.constraint_name=rcn.constraint_name
                          ORDER BY position) LOOP
                        SELECT max(position) INTO v_maxcol
                          FROM user_cons_columns c
                         WHERE c.constraint_name=rcn.constraint_name;
                        IF rcl.position=v_maxcol THEN
                                v_virg := '';
                        END IF;
                        DBMS_OUTPUT.PUT_LINE(rcl.column_name || v_virg);
                END LOOP;
                DBMS_OUTPUT.PUT_LINE(')');
                DBMS_OUTPUT.PUT_LINE('USING INDEX);');
        END LOOP;
END;
/

DECLARE
        v_virg    VARCHAR2(1);
        v_maxcol  NUMBER;
        v_tname   VARCHAR2(30);
BEGIN
	DBMS_OUTPUT.PUT_LINE('--');
	DBMS_OUTPUT.PUT_LINE('-- FOREIGN KEYS --');
	DBMS_OUTPUT.PUT_LINE('--');
        FOR rcn IN (SELECT table_name, constraint_name, r_constraint_name 
                      FROM user_constraints 
                     WHERE constraint_type = 'R'
                  ORDER BY table_name) LOOP

                DBMS_OUTPUT.PUT_LINE('ALTER TABLE ' || rcn.table_name || ' ADD (');
                DBMS_OUTPUT.PUT_LINE('CONSTRAINT ' || rcn.constraint_name);
                DBMS_OUTPUT.PUT_LINE('FOREIGN KEY (');
                v_virg := ',';

                FOR rcl IN (SELECT column_name, position 
                              FROM user_cons_columns cl 
                             WHERE cl.constraint_name = rcn.constraint_name
                          ORDER BY position) LOOP
                        SELECT max(position) INTO v_maxcol
                          FROM user_cons_columns c
                         WHERE c.constraint_name = rcn.constraint_name;
                        IF rcl.position = v_maxcol THEN
                                v_virg := '';
                        END IF;
                        DBMS_OUTPUT.PUT_LINE(rcl.column_name || v_virg);
                END LOOP;

                SELECT table_name INTO v_tname
                  FROM user_constraints c
                 WHERE c.constraint_name = rcn.r_constraint_name;
                DBMS_OUTPUT.PUT_LINE(') REFERENCES ' || v_tname || ' (');

                SELECT max(position) INTO v_maxcol
                  FROM user_cons_columns c
                 WHERE c.constraint_name = rcn.r_constraint_name;
                v_virg := ',';
                SELECT max(position) INTO v_maxcol
                  FROM user_cons_columns c
                 WHERE c.constraint_name = rcn.r_constraint_name;
                FOR rcr IN (SELECT column_name,position 
                              FROM user_cons_columns cl
                             WHERE rcn.r_constraint_name = cl.constraint_name
                          ORDER BY position) LOOP
                        IF rcr.position = v_maxcol THEN
                                v_virg := '';
                        END IF;
                        DBMS_OUTPUT.PUT_LINE(rcr.column_name || v_virg);
                END LOOP;
                DBMS_OUTPUT.PUT_LINE('));');
        END LOOP;
END;
/

DECLARE
	s  VARCHAR2(200);
BEGIN
	DBMS_OUTPUT.PUT_LINE('--');
	DBMS_OUTPUT.PUT_LINE('-- DROP SEQUENCES --');
	DBMS_OUTPUT.PUT_LINE('--');
        FOR rs IN (SELECT sequence_name 
                     FROM user_sequences
                 ORDER BY sequence_name) LOOP
                DBMS_OUTPUT.PUT_LINE('DROP SEQUENCE ' || rs.sequence_name || ';');
        END LOOP;
	DBMS_OUTPUT.PUT_LINE('--');
	DBMS_OUTPUT.PUT_LINE('-- CREATE SEQUENCES --');
	DBMS_OUTPUT.PUT_LINE('--');
        FOR rs IN (SELECT sequence_name, min_value, max_value, increment_by,
                          cycle_flag, order_flag, cache_size, last_number
                     FROM user_sequences
                 ORDER BY sequence_name) LOOP
		s := 'CREATE SEQUENCE ' || rs.sequence_name || ' START WITH ';
		IF rs.last_number > rs.min_value THEN
			s := s || rs.last_number;
		ELSE
			s := s || rs.min_value;
		END IF;
		s := s || ' INCREMENT BY ' || rs.increment_by;
		IF rs.cycle_flag = 'Y' THEN
			s := s || ' CYCLE';
		ELSE
			s := s || ' NOCYCLE';
		END IF;
		IF rs.order_flag = 'Y' THEN
			s := s || ' ORDER';
		ELSE
			s := s || ' NOORDER';
		END IF;
		IF rs.cache_size > 0 THEN
			s := s || ' CACHE ' || rs.cache_size;
		ELSE
			s := s || ' NOCACHE';
		END IF;
		DBMS_OUTPUT.PUT_LINE(s || ';');
        END LOOP;
END;
/

DECLARE
        v_virg   VARCHAR2(1);
        v_maxcol NUMBER;
BEGIN
	DBMS_OUTPUT.PUT_LINE('--');
	DBMS_OUTPUT.PUT_LINE('-- INDEXES --');
	DBMS_OUTPUT.PUT_LINE('--');
        FOR rid IN (SELECT index_name, table_name 
                      FROM user_indexes
                     WHERE index_name NOT IN (SELECT constraint_name FROM user_constraints) 
                       AND index_type <> 'LOB'
                  ORDER BY index_name) LOOP
                v_virg := ',';
                DBMS_OUTPUT.PUT_LINE('CREATE INDEX ' || rid.index_name || ' ON ' || rid.table_name || ' (');    
                FOR rcl IN (SELECT column_name,column_position 
                              FROM user_ind_columns cl 
                             WHERE cl.index_name=rid.index_name
                          ORDER BY column_position) LOOP
                        SELECT max(column_position) INTO v_maxcol
                          FROM user_ind_columns c
                         WHERE c.index_name=rid.index_name;
                        IF rcl.column_position=v_maxcol THEN
                                v_virg := '';
                        END IF;
                        DBMS_OUTPUT.PUT_LINE(rcl.column_name || v_virg);
                END LOOP;
                DBMS_OUTPUT.PUT_LINE(');');
        END LOOP;
END;
/

SPOOL OFF
SET feedback ON
SET termout ON
EXIT;
