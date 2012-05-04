--WHENEVER SQLERROR CONTINUE

DECLARE
	s	VARCHAR(300);
	tn	VARCHAR(30);
	cn	VARCHAR(30);
BEGIN

	FOR rc1 IN (SELECT table_name
		     FROM user_tables) LOOP

		FOR rc2 IN (SELECT column_name
			      FROM all_tab_columns
			     WHERE table_name = rc1.table_name
			       AND data_type = 'VARCHAR2') LOOP
			tn := LOWER(rc1.table_name);
			cn := LOWER(rc2.column_name);
			s := 'UPDATE ' || tn || ' SET ' || cn || ' = TRANSLATE(' || cn || ',''0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'',''9876543210zyxwvutsrqponmlkjihgfedcbaZYXWVUTSRQPONMLKJIHGFEDCBA'')';
			DBMS_OUTPUT.PUT_LINE(s);
			EXECUTE IMMEDIATE s;
		END LOOP;
	END LOOP;
	EXCEPTION
		WHEN OTHERS THEN
			DBMS_OUTPUT.PUT_LINE('! EXCEPTION: CODE=' || SQLCODE || ' MESSAGE=' || SQLERRM);
END;
/

DECLARE
	s	VARCHAR(300);
	tn	VARCHAR(30);
	cn	VARCHAR(30);
BEGIN

	FOR rc1 IN (SELECT table_name
		     FROM user_tables) LOOP

		FOR rc2 IN (SELECT column_name
			      FROM all_tab_columns
			     WHERE table_name = rc1.table_name
			       AND data_type = 'VARCHAR2') LOOP
			tn := LOWER(rc1.table_name);
			cn := LOWER(rc2.column_name);
			s := 'UPDATE ' || tn || ' SET ' || cn || ' = TRANSLATE(' || cn || ',''9876543210zyxwvutsrqponmlkjihgfedcbaZYXWVUTSRQPONMLKJIHGFEDCBA'',''0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'')';
			DBMS_OUTPUT.PUT_LINE(s);
			EXECUTE IMMEDIATE s;
		END LOOP;
	END LOOP;
	EXCEPTION
		WHEN OTHERS THEN
			DBMS_OUTPUT.PUT_LINE('! EXCEPTION: CODE=' || SQLCODE || ' MESSAGE=' || SQLERRM);
END;
/
