CREATE TABLE uxsgroups (
	ID				numeric			NOT NULL,
	CreatedBy			numeric,
	CreatedDateTime			timestamp,
	ChangedBy			numeric,
	ChangedDateTime			timestamp,
	active				numeric(1)		NOT NULL	DEFAULT 1,
	UXSGroup			varchar(200)		NOT NULL,
	Schema				varchar(30)		NOT NULL,
	Description			varchar(2000)
);
