CREATE TABLE sendemail (
	ID				numeric			NOT NULL	DEFAULT nextval('sendemail_id_seq'),
	CreatedBy			numeric			NOT NULL	DEFAULT 0,
	CreatedDateTime			numeric			NOT NULL	DEFAULT 0,
	ChangedBy			numeric			NOT NULL	DEFAULT 0,
	ChangedDateTime			numeric			NOT NULL	DEFAULT 0,
	FQHN				varchar(200)		NOT NULL,
	PrjDocRoot			varchar(200)		NOT NULL,
	Template			varchar(200)		NOT NULL,
	Query				varchar(4000)		NOT NULL,
	HtmlFormatted			numeric(1)		NOT NULL	DEFAULT 0,
	ToDo				numeric(1)		
);
