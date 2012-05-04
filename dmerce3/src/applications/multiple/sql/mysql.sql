
CREATE TABLE vkunde (
  kundennr int(3) unsigned NOT NULL auto_increment,
  name varchar(255) NOT NULL default '0',
  vorname varchar(255) NOT NULL default '0',
  email varchar(255) NOT NULL default '0',
  ort varchar(50) default '0',
  strasse varchar(255) default '0',
  plz varchar(5) default '0',
  telefon varchar(50) default '0',
  filename_foto_original varchar(200),
  filename_foto_server varchar(200),
  PRIMARY KEY  (kundennr)
) TYPE=MyISAM;

INSERT INTO vkunde VALUES(1,'Hasenfu�','Ottokar','ottokar@hasenfuss.de','Hasenfurth','Zur Grube 1','12345','0','','');
INSERT INTO vkunde VALUES(2,'Mustermann','Hans','hans@mustermann.de','','','','','','');
INSERT INTO vkunde VALUES(3,'M�ller','Werner','info@werner-mueller.de','','','','','','');
INSERT INTO vkunde VALUES(4,'Musterfrau','Gabi','gabi.musterfrau@abcde.de','Musterstadt','Hilfe 1','77777','02345-3456','','');
