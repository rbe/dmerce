
CREATE TABLE vkunde (
  kundennr number NOT NULL,
  name varchar2(255) NOT NULL,
  vorname varchar2(255) NOT NULL,
  email varchar2(255) NOT NULL,
  ort varchar2(50),
  strasse varchar2(255),
  plz varchar2(5),
  telefon varchar2(50),
  filename_foto_original varchar2(200),
  filename_foto_server varchar2(200)
);

ALTER TABLE vkunde ADD (CONSTRAINT pk_vkunde PRIMARY KEY (kundennr));


INSERT INTO vkunde VALUES(1,'Hasenfuß','Ottokar','ottokar@hasenfuss.de','Hasenfurth','Zur Grube 1','12345','0','','');
INSERT INTO vkunde VALUES(2,'Mustermann','Hans','hans@mustermann.de','','','','','','');
INSERT INTO vkunde VALUES(3,'Müller','Werner','info@werner-mueller.de','','','','','','');
INSERT INTO vkunde VALUES(4,'Musterfrau','Gabi','gabi.musterfrau@abcde.de','Musterstadt','Hilfe 1','77777','02345-3456','','');
