create table t_am_kunde (
	id				integer identity primary key,
	name			varchar(100) not null,
	vorname			varchar(100),
	email			varchar(100) not null,
	ort				varchar(100) ,
	strasse			varchar(100),
	plz				char(5),
	telefon			varchar(20),
	geburtsdatum	date,
	foto_original	varchar(200),
	foto_server		varchar(200)
);

insert into t_am_kunde values (1,'Hasenfuß','Ottokar','ottokar@hasenfuss.de','Hasenfurth','Zur Grube 1','12345','0','','');
insert into t_am_kunde values (2,'Mustermann','Hans','hans@mustermann.de','','','','','','');
insert into t_am_kunde values (3,'Müller','Werner','info@werner-mueller.de','','','','','','');
insert into t_am_kunde values (4,'Musterfrau','Gabi','gabi.musterfrau@abcde.de','Musterstadt','Hilfe 1','77777','02345-3456','','');
