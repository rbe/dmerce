
CREATE TABLE vkunde (
  kundennr INTEGER IDENTITY PRIMARY KEY,
  name varchar(255) NOT NULL,
  vorname varchar(255) NOT NULL,
  email varchar(255) NOT NULL,
  ort varchar(50) ,
  strasse varchar(255) ,
  plz varchar(5) ,
  telefon varchar(50),
  filename_foto_original varchar(200),
  filename_foto_server varchar(200)
);

INSERT INTO vkunde VALUES(1,'Hasenfuß','Ottokar','ottokar@hasenfuss.de','Hasenfurth','Zur Grube 1','12345','0','','');
INSERT INTO vkunde VALUES(2,'Mustermann','Hans','hans@mustermann.de','','','','','','');
INSERT INTO vkunde VALUES(3,'Müller','Werner','info@werner-mueller.de','','','','','','');
INSERT INTO vkunde VALUES(4,'Musterfrau','Gabi','gabi.musterfrau@abcde.de','Musterstadt','Hilfe 1','77777','02345-3456','','');
