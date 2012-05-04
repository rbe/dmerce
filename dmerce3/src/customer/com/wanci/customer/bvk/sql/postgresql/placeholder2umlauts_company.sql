CREATE FUNCTION placeholder2umlauts_company(INTEGER) RETURNS INTEGER
AS '

DECLARE

BEGIN

	UPDATE company SET name = REPLACE(name, ''*'', ''ß'');
	UPDATE company SET name = REPLACE(name, ''!'', ''ü'');
	UPDATE company SET name = REPLACE(name, ''$'', ''ö'');
	UPDATE company SET name = REPLACE(name, ''#'', ''ä'');
	UPDATE company SET name = REPLACE(name, ''{'', ''Ü'');
	UPDATE company SET name = REPLACE(name, ''}'', ''Ö'');
	UPDATE company SET name = REPLACE(name, ''?'', ''Ä'');
	UPDATE company SET name = REPLACE(name, ''<'', ''é'');

	RETURN 1;

END;
' LANGUAGE 'plpgsql';
