CREATE TABLE templates (
	FQHN				varchar(200)		NOT NULL,
	FQTN				varchar(200)		NOT NULL,
	SAM				integer			NOT NULL	DEFAULT 0,
	SAMFailURL			varchar(200)		NOT NULL	DEFAULT '/',
	GivenFields			varchar(2000),
	IgnoreEmptyFields		varchar(2000),
	EmailFrom			varchar(200),
	EmailSubject			varchar(200)
);
