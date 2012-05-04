CREATE FUNCTION checkmemberafterimport(INTEGER) RETURNS INTEGER
AS '

DECLARE
	memberid ALIAS FOR $1;
	status CHAR;

BEGIN

	SELECT membershipstatus INTO status
	  FROM member
	 WHERE id = memberid;

	IF status = ''M'' THEN

		UPDATE member
		   SET logindisabled = 0
		 WHERE id = memberid;
	 
	ELSE IF status = ''A'' or status = ''K'' THEN

		UPDATE member
		   SET logindisabled = 1
		 WHERE id = memberid;
	    
	END IF;
	END IF;

	UPDATE member
	   SET membershipnumber = id, login = id
	 WHERE id = memberid;

	RETURN 1;

END;
' LANGUAGE 'plpgsql';
