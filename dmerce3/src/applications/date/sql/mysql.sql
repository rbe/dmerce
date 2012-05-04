-- MySQL dump 9.09
--
-- Host: localhost    Database: dmerce
---------------------------------------------------------
-- Server version	4.0.15-nt

--
-- Table structure for table `vdate`
--

CREATE TABLE vdate (
  id int(11) NOT NULL auto_increment,
  datetime datetime default NULL,
  date date default NULL,
  time time default NULL,
  timestamp timestamp(14) NOT NULL,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

