CREATE TABLE log (
	ID				numeric			NOT NULL,
	CreatedDateTime			timestamp,
	ServerAddr			varchar(15),
	RemoteAddr			varchar(15),
	HttpXForwardedFor		varchar(15),
	Template			varchar(200),
	Handler				varchar(200),
	MsgType				varchar(20),
	Msg				varchar(500)
);
