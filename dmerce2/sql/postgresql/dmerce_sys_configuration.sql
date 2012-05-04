CREATE TABLE configuration (
	FQHN				varchar(250)		NOT NULL,
	HostSerial			integer			NOT NULL	DEFAULT 0,
	OwnerConnectString		varchar(250),
	DmerceSysConnectString		varchar(250),
	SAMInactiveTimeout		integer			NOT NULL	DEFAULT 900,
	FileUploadDir			varchar(250),
	UxsRolePrefix			varchar(27),
	Debug				integer			NOT NULL	DEFAULT 0
);
