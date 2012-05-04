BEGIN
	FOR rc1 IN (SELECT trigger_name FROM user_triggers) LOOP
		DBMS_OUTPUT.PUT_LINE('* Disabling trigger ' || rc1.trigger_name);
		EXECUTE IMMEDIATE 'ALTER TRIGGER ' || rc1.trigger_name || ' DISABLE';
	END LOOP;
END;
/
