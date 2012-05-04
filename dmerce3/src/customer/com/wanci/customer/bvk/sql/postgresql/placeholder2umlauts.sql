CREATE FUNCTION placeholder2umlauts(INTEGER) RETURNS INTEGER
AS '

DECLARE
	memberid ALIAS FOR $1;

BEGIN

	IF memberid > 0 THEN

		UPDATE member SET firstname = REPLACE(firstname, ''*'', ''ß''), name = REPLACE(name, ''*'', ''ß''), company = REPLACE(company, ''*'', ''ß''), company2 = REPLACE(company2, ''*'', ''ß''), street = REPLACE(street, ''*'', ''ß''), bvkname1 = REPLACE(bvkname1, ''*'', ''ß''), bvkname2 = REPLACE(bvkname2, ''*'', ''ß''), bvkname3 = REPLACE(bvkname3, ''*'', ''ß''), city = REPLACE(city, ''*'', ''ß''), bank = REPLACE(bank, ''*'', ''ß''), accountowner = REPLACE(accountowner, ''*'', ''ß''), owner1 = REPLACE(owner1, ''*'', ''ß''), owner2 = REPLACE(owner2, ''*'', ''ß''), owner3 = REPLACE(owner3, ''*'', ''ß'') WHERE id = memberid;
		UPDATE member SET firstname = REPLACE(firstname, ''!'', ''ü''), name = REPLACE(name, ''!'', ''ü''), company = REPLACE(company, ''!'', ''ü''), company2 = REPLACE(company2, ''!'', ''ü''), street = REPLACE(street, ''!'', ''ü''), bvkname1 = REPLACE(bvkname1, ''!'', ''ü''), bvkname2 = REPLACE(bvkname2, ''!'', ''ü''), bvkname3 = REPLACE(bvkname3, ''!'', ''ü''), city = REPLACE(city, ''!'', ''ü''), bank = REPLACE(bank, ''!'', ''ü''), accountowner = REPLACE(accountowner, ''!'', ''ü''), owner1 = REPLACE(owner1, ''!'', ''ü''), owner2 = REPLACE(owner2, ''!'', ''ü''), owner3 = REPLACE(owner3, ''!'', ''ü'') WHERE id = memberid;
		UPDATE member SET firstname = REPLACE(firstname, ''$'', ''ö''), name = REPLACE(name, ''$'', ''ö''), company = REPLACE(company, ''$'', ''ö''), company2 = REPLACE(company2, ''$'', ''ö''), street = REPLACE(street, ''$'', ''ö''), bvkname1 = REPLACE(bvkname1, ''$'', ''ö''), bvkname2 = REPLACE(bvkname2, ''$'', ''ö''), bvkname3 = REPLACE(bvkname3, ''$'', ''ö''), city = REPLACE(city, ''$'', ''ö''), bank = REPLACE(bank, ''$'', ''ö''), accountowner = REPLACE(accountowner, ''$'', ''ö''), owner1 = REPLACE(owner1, ''$'', ''ö''), owner2 = REPLACE(owner2, ''$'', ''ö''), owner3 = REPLACE(owner3, ''$'', ''ö'') WHERE id = memberid;
		UPDATE member SET firstname = REPLACE(firstname, ''#'', ''ä''), name = REPLACE(name, ''#'', ''ä''), company = REPLACE(company, ''#'', ''ä''), company2 = REPLACE(company2, ''#'', ''ä''), street = REPLACE(street, ''#'', ''ä''), bvkname1 = REPLACE(bvkname1, ''#'', ''ä''), bvkname2 = REPLACE(bvkname2, ''#'', ''ä''), bvkname3 = REPLACE(bvkname3, ''#'', ''ä''), city = REPLACE(city, ''#'', ''ä''), bank = REPLACE(bank, ''#'', ''ä''), accountowner = REPLACE(accountowner, ''#'', ''ä''), owner1 = REPLACE(owner1, ''#'', ''ä''), owner2 = REPLACE(owner2, ''#'', ''ä''), owner3 = REPLACE(owner3, ''#'', ''ä'') WHERE id = memberid;
		UPDATE member SET firstname = REPLACE(firstname, ''{'', ''Ü''), name = REPLACE(name, ''{'', ''Ü''), company = REPLACE(company, ''{'', ''Ü''), company2 = REPLACE(company2, ''{'', ''Ü''), street = REPLACE(street, ''{'', ''Ü''), bvkname1 = REPLACE(bvkname1, ''{'', ''Ü''), bvkname2 = REPLACE(bvkname2, ''{'', ''Ü''), bvkname3 = REPLACE(bvkname3, ''{'', ''Ü''), city = REPLACE(city, ''{'', ''Ü''), bank = REPLACE(bank, ''{'', ''Ü''), accountowner = REPLACE(accountowner, ''{'', ''Ü''), owner1 = REPLACE(owner1, ''{'', ''Ü''), owner2 = REPLACE(owner2, ''{'', ''Ü''), owner3 = REPLACE(owner3, ''{'', ''Ü'') WHERE id = memberid;
		UPDATE member SET firstname = REPLACE(firstname, ''}'', ''Ö''), name = REPLACE(name, ''}'', ''Ö''), company = REPLACE(company, ''}'', ''Ö''), company2 = REPLACE(company2, ''{'', ''Ü''), street = REPLACE(street, ''}'', ''Ö''), bvkname1 = REPLACE(bvkname1, ''}'', ''Ö''), bvkname2 = REPLACE(bvkname2, ''}'', ''Ö''), bvkname3 = REPLACE(bvkname3, ''}'', ''Ö''), city = REPLACE(city, ''}'', ''Ö''), bank = REPLACE(bank, ''}'', ''Ö''), accountowner = REPLACE(accountowner, ''}'', ''Ö''), owner1 = REPLACE(owner1, ''}'', ''Ö''), owner2 = REPLACE(owner2, ''}'', ''Ö''), owner3 = REPLACE(owner3, ''}'', ''Ö'') WHERE id = memberid;
		UPDATE member SET firstname = REPLACE(firstname, ''?'', ''Ä''), name = REPLACE(name, ''?'', ''Ä''), company = REPLACE(company, ''?'', ''Ä''), company2 = REPLACE(company2, ''?'', ''Ä''), street = REPLACE(street, ''?'', ''Ä''), bvkname1 = REPLACE(bvkname1, ''?'', ''Ä''), bvkname2 = REPLACE(bvkname2, ''?'', ''Ä''), bvkname3 = REPLACE(bvkname3, ''?'', ''Ä''), city = REPLACE(city, ''?'', ''Ä''), bank = REPLACE(bank, ''?'', ''Ä''), accountowner = REPLACE(accountowner, ''?'', ''Ä''), owner1 = REPLACE(owner1, ''?'', ''Ä''), owner2 = REPLACE(owner2, ''?'', ''Ä''), owner3 = REPLACE(owner3, ''?'', ''Ä'') WHERE id = memberid;
		UPDATE member SET firstname = REPLACE(firstname, ''<'', ''é''), name = REPLACE(name, ''<'', ''é''), company = REPLACE(company, ''<'', ''é''), company2 = REPLACE(company2, ''<'', ''é''), street = REPLACE(street, ''<'', ''é''), bvkname1 = REPLACE(bvkname1, ''<'', ''é''), bvkname2 = REPLACE(bvkname2, ''<'', ''é''), bvkname3 = REPLACE(bvkname3, ''<'', ''é''), city = REPLACE(city, ''<'', ''é''), bank = REPLACE(bank, ''<'', ''é''), accountowner = REPLACE(accountowner, ''<'', ''é''), owner1 = REPLACE(owner1, ''<'', ''é''), owner2 = REPLACE(owner2, ''<'', ''é''), owner3 = REPLACE(owner3, ''<'', ''é'') WHERE id = memberid;

		RETURN 1;

	END IF;

	RETURN 0;

END;
' LANGUAGE 'plpgsql';
