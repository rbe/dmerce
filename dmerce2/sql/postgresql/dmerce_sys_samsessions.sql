CREATE TABLE samsessions (
	SessionID			varchar(200)		NOT NULL,
	FirstTime			numeric(16, 6)		NOT NULL,
	LastTime			numeric(16, 6)		NOT NULL,
	DisabledTime			numeric(16, 6),
	ServerName			varchar(200),
	ServerAddr			varchar(15)		NOT NULL,
	RemoteAddr			varchar(15)		NOT NULL,
	HttpXForwardedFor		varchar(15),
	UserAgent			varchar(200),
	FQUN				varchar(200),
	HttpReferer			varchar(200),
	UXSI				varchar(10)
);
