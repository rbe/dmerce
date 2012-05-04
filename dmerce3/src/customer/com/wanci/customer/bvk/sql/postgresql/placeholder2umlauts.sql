CREATE FUNCTION placeholder2umlauts(INTEGER) RETURNS INTEGER
AS '

DECLARE
	memberid ALIAS FOR $1;

BEGIN

	IF memberid > 0 THEN

		UPDATE member SET firstname = REPLACE(firstname, ''*'', ''�''), name = REPLACE(name, ''*'', ''�''), company = REPLACE(company, ''*'', ''�''), company2 = REPLACE(company2, ''*'', ''�''), street = REPLACE(street, ''*'', ''�''), bvkname1 = REPLACE(bvkname1, ''*'', ''�''), bvkname2 = REPLACE(bvkname2, ''*'', ''�''), bvkname3 = REPLACE(bvkname3, ''*'', ''�''), city = REPLACE(city, ''*'', ''�''), bank = REPLACE(bank, ''*'', ''�''), accountowner = REPLACE(accountowner, ''*'', ''�''), owner1 = REPLACE(owner1, ''*'', ''�''), owner2 = REPLACE(owner2, ''*'', ''�''), owner3 = REPLACE(owner3, ''*'', ''�'') WHERE id = memberid;
		UPDATE member SET firstname = REPLACE(firstname, ''!'', ''�''), name = REPLACE(name, ''!'', ''�''), company = REPLACE(company, ''!'', ''�''), company2 = REPLACE(company2, ''!'', ''�''), street = REPLACE(street, ''!'', ''�''), bvkname1 = REPLACE(bvkname1, ''!'', ''�''), bvkname2 = REPLACE(bvkname2, ''!'', ''�''), bvkname3 = REPLACE(bvkname3, ''!'', ''�''), city = REPLACE(city, ''!'', ''�''), bank = REPLACE(bank, ''!'', ''�''), accountowner = REPLACE(accountowner, ''!'', ''�''), owner1 = REPLACE(owner1, ''!'', ''�''), owner2 = REPLACE(owner2, ''!'', ''�''), owner3 = REPLACE(owner3, ''!'', ''�'') WHERE id = memberid;
		UPDATE member SET firstname = REPLACE(firstname, ''$'', ''�''), name = REPLACE(name, ''$'', ''�''), company = REPLACE(company, ''$'', ''�''), company2 = REPLACE(company2, ''$'', ''�''), street = REPLACE(street, ''$'', ''�''), bvkname1 = REPLACE(bvkname1, ''$'', ''�''), bvkname2 = REPLACE(bvkname2, ''$'', ''�''), bvkname3 = REPLACE(bvkname3, ''$'', ''�''), city = REPLACE(city, ''$'', ''�''), bank = REPLACE(bank, ''$'', ''�''), accountowner = REPLACE(accountowner, ''$'', ''�''), owner1 = REPLACE(owner1, ''$'', ''�''), owner2 = REPLACE(owner2, ''$'', ''�''), owner3 = REPLACE(owner3, ''$'', ''�'') WHERE id = memberid;
		UPDATE member SET firstname = REPLACE(firstname, ''#'', ''�''), name = REPLACE(name, ''#'', ''�''), company = REPLACE(company, ''#'', ''�''), company2 = REPLACE(company2, ''#'', ''�''), street = REPLACE(street, ''#'', ''�''), bvkname1 = REPLACE(bvkname1, ''#'', ''�''), bvkname2 = REPLACE(bvkname2, ''#'', ''�''), bvkname3 = REPLACE(bvkname3, ''#'', ''�''), city = REPLACE(city, ''#'', ''�''), bank = REPLACE(bank, ''#'', ''�''), accountowner = REPLACE(accountowner, ''#'', ''�''), owner1 = REPLACE(owner1, ''#'', ''�''), owner2 = REPLACE(owner2, ''#'', ''�''), owner3 = REPLACE(owner3, ''#'', ''�'') WHERE id = memberid;
		UPDATE member SET firstname = REPLACE(firstname, ''{'', ''�''), name = REPLACE(name, ''{'', ''�''), company = REPLACE(company, ''{'', ''�''), company2 = REPLACE(company2, ''{'', ''�''), street = REPLACE(street, ''{'', ''�''), bvkname1 = REPLACE(bvkname1, ''{'', ''�''), bvkname2 = REPLACE(bvkname2, ''{'', ''�''), bvkname3 = REPLACE(bvkname3, ''{'', ''�''), city = REPLACE(city, ''{'', ''�''), bank = REPLACE(bank, ''{'', ''�''), accountowner = REPLACE(accountowner, ''{'', ''�''), owner1 = REPLACE(owner1, ''{'', ''�''), owner2 = REPLACE(owner2, ''{'', ''�''), owner3 = REPLACE(owner3, ''{'', ''�'') WHERE id = memberid;
		UPDATE member SET firstname = REPLACE(firstname, ''}'', ''�''), name = REPLACE(name, ''}'', ''�''), company = REPLACE(company, ''}'', ''�''), company2 = REPLACE(company2, ''{'', ''�''), street = REPLACE(street, ''}'', ''�''), bvkname1 = REPLACE(bvkname1, ''}'', ''�''), bvkname2 = REPLACE(bvkname2, ''}'', ''�''), bvkname3 = REPLACE(bvkname3, ''}'', ''�''), city = REPLACE(city, ''}'', ''�''), bank = REPLACE(bank, ''}'', ''�''), accountowner = REPLACE(accountowner, ''}'', ''�''), owner1 = REPLACE(owner1, ''}'', ''�''), owner2 = REPLACE(owner2, ''}'', ''�''), owner3 = REPLACE(owner3, ''}'', ''�'') WHERE id = memberid;
		UPDATE member SET firstname = REPLACE(firstname, ''?'', ''�''), name = REPLACE(name, ''?'', ''�''), company = REPLACE(company, ''?'', ''�''), company2 = REPLACE(company2, ''?'', ''�''), street = REPLACE(street, ''?'', ''�''), bvkname1 = REPLACE(bvkname1, ''?'', ''�''), bvkname2 = REPLACE(bvkname2, ''?'', ''�''), bvkname3 = REPLACE(bvkname3, ''?'', ''�''), city = REPLACE(city, ''?'', ''�''), bank = REPLACE(bank, ''?'', ''�''), accountowner = REPLACE(accountowner, ''?'', ''�''), owner1 = REPLACE(owner1, ''?'', ''�''), owner2 = REPLACE(owner2, ''?'', ''�''), owner3 = REPLACE(owner3, ''?'', ''�'') WHERE id = memberid;
		UPDATE member SET firstname = REPLACE(firstname, ''<'', ''�''), name = REPLACE(name, ''<'', ''�''), company = REPLACE(company, ''<'', ''�''), company2 = REPLACE(company2, ''<'', ''�''), street = REPLACE(street, ''<'', ''�''), bvkname1 = REPLACE(bvkname1, ''<'', ''�''), bvkname2 = REPLACE(bvkname2, ''<'', ''�''), bvkname3 = REPLACE(bvkname3, ''<'', ''�''), city = REPLACE(city, ''<'', ''�''), bank = REPLACE(bank, ''<'', ''�''), accountowner = REPLACE(accountowner, ''<'', ''�''), owner1 = REPLACE(owner1, ''<'', ''�''), owner2 = REPLACE(owner2, ''<'', ''�''), owner3 = REPLACE(owner3, ''<'', ''�'') WHERE id = memberid;

		RETURN 1;

	END IF;

	RETURN 0;

END;
' LANGUAGE 'plpgsql';
