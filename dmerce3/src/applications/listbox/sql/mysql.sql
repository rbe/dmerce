-- MySQL dump 9.09
--
-- Host: localhost    Database: dmerce
---------------------------------------------------------
-- Server version	4.0.15-nt

--
-- Table structure for table `vkunde`
--

CREATE TABLE vkunde (
  kundennr int(3) unsigned NOT NULL auto_increment,
  name varchar(255) NOT NULL default '0',
  vorname varchar(255) NOT NULL default '0',
  email varchar(255) NOT NULL default '0',
  ort varchar(50) default '0',
  strasse varchar(255) default '0',
  plz varchar(5) default '0',
  telefon varchar(50) default '0',
  PRIMARY KEY  (kundennr)
) TYPE=MyISAM;

