CREATE TABLE ippool (
	ID				numeric		NOT NULL,
	Net				varchar(200)	NOT NULL,
	PoolStart			numeric		NOT NULL,
	PoolEnd				numeric		NOT NULL,
	HighestIP			numeric
);
