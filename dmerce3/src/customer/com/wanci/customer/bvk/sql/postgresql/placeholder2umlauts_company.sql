CREATE FUNCTION placeholder2umlauts_company(INTEGER) RETURNS INTEGER
AS '

DECLARE

BEGIN

	UPDATE company SET name = REPLACE(name, ''*'', ''�'');
	UPDATE company SET name = REPLACE(name, ''!'', ''�'');
	UPDATE company SET name = REPLACE(name, ''$'', ''�'');
	UPDATE company SET name = REPLACE(name, ''#'', ''�'');
	UPDATE company SET name = REPLACE(name, ''{'', ''�'');
	UPDATE company SET name = REPLACE(name, ''}'', ''�'');
	UPDATE company SET name = REPLACE(name, ''?'', ''�'');
	UPDATE company SET name = REPLACE(name, ''<'', ''�'');

	RETURN 1;

END;
' LANGUAGE 'plpgsql';
