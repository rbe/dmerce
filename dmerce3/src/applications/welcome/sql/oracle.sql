create table t_am_kunde (
  id				number not null,
  nachname			varchar2(100) not null,
  vorname			varchar2(100),
  email				varchar2(100) not null,
  ort				varchar2(50),
  strasse			varchar2(100),
  plz				char(5),
  telefon			varchar2(20),
  geburtsdatum		date,
  foto_original		varchar2(200),
  foto_server		varchar2(200)
);

alter table t_am_kunde add constraint t_am_kunde_pk primary key (id));

create sequence t_am_kunde_seq start with 1 increment by 1 nomaxvalue nocycle nocache order;

insert into t_am_kunde values (t_am_kunde_seq.nextval,'Hasenfuß','Ottokar','ottokar@hasenfuss.de','Hasenfurth','Zur Grube 1','12345','0',to_date('01.01.2004','dd.mm.yyyy'),'','');
insert into t_am_kunde values (t_am_kunde_seq.nextval,'Mustermann','Hans','hans@mustermann.de','','','','',to_date('01.02.2004','dd.mm.yyyy'),'','');
insert into t_am_kunde values (t_am_kunde_seq.nextval,'Müller','Werner','info@werner-mueller.de','','','','',to_date('01.03.2004','dd.mm.yyyy'),'','');
insert into t_am_kunde values (t_am_kunde_seq.nextval,'Musterfrau','Gabi','gabi.musterfrau@abcde.de','Musterstadt','Hilfe 1','77777','02345-3456',to_date('01.04.2004','dd.mm.yyyy'),'','');
