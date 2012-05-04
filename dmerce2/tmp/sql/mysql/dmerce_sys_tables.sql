DROP TABLE IF EXISTS Configuration;
CREATE TABLE Configuration (
  FQHN varchar(250) NOT NULL default '',
  HostSerial int(11) NOT NULL default '0',
  SQL varchar(250) NOT NULL default '',
  OwnerConnectString varchar(250) default '',
  DmerceSysConnectString varchar(250) default '',
  saminactivetimeout int(11) default '900',
  FileUploadDir varchar(250) NOT NULL default '',
  UxsRolePrefix varchar(27) NOT NULL,
  Debug int(1) NOT NULL default '0'
);

DROP TABLE IF EXISTS SendEmail;
CREATE TABLE SendEmail (
  ID int(1) NOT NULL default '0',
  CreatedBy int(11) NOT NULL default '0',
  CreatedDateTime double(16,6) NOT NULL default '0.000000',
  ChangedBy int(11) NOT NULL default '0',
  ChangedDateTime double(16,6) NOT NULL default '0.000000',
  FQHN varchar(200) NOT NULL default '',
  Template varchar(200) NOT NULL default '',
  Query text NOT NULL default '',
  HTMLFormatted int(1) NOT NULL DEFAULT 0,
  ToDo int(1) NOT NULL default '1',
  PrjDocRoot varchar(255) NOT NULL default '',
  PRIMARY KEY ID (ID)
);

DROP TABLE IF EXISTS DBOID;
CREATE TABLE DBOID (
  D varchar(250) NOT NULL default '',
  S varchar(250) NOT NULL default '',
  T varchar(250) NOT NULL default '',
  ID int(11) NOT NULL default '0'
);

DROP TABLE IF EXISTS SAMSessions;
CREATE TABLE SAMSessions (
  SessionID varchar(250) NOT NULL default '0',
  FirstTime double(16,6) NOT NULL default '0.000000',
  LastTime double(16,6) NOT NULL default '0.000000',
  DisabledTime double(16,6) NOT NULL default '0.000000',
  ServerName varchar(250) NOT NULL default '',
  ServerAddr varchar(15) NOT NULL default '',
  RemoteAddr varchar(15) NOT NULL default '',
  HttpXForwardedFor varchar(250) NOT NULL default '',
  UserAgent int(11) NOT NULL default '0',
  FQUN varchar(250) NOT NULL default '0',
  HttpReferer varchar(255) NOT NULL default '',
  UXSI VARCHAR(5) NOT NULL DEFAULT '0.1',
  PRIMARY KEY SessionId (SessionID)
);

DROP TABLE IF EXISTS Templates;
CREATE TABLE Templates (
  FQHN varchar(250) NOT NULL default '',
  FQTN varchar(250) NOT NULL default '',
  SAM int(1) NOT NULL default '0',
  SAMFailURL varchar(250) NOT NULL default '',
  GivenFields text NOT NULL,
  IgnoreEmptyFields varchar(200) NOT NULL default '',
  EmailFrom varchar(200) NOT NULL default '',
  EmailSubject varchar(200) NOT NULL default ''
);

CREATE TABLE AvailProjects (
  ID int(1) NOT NULL default '0',
  CreatedBy int(11) NOT NULL default '0',
  CreatedDateTime double(16,6) NOT NULL default '0.000000',
  ChangedBy int(11) NOT NULL default '0',
  ChangedDateTime double(16,6) NOT NULL default '0.000000',
  Name varchar(255) NOT NULL default '',
  Scratch varchar(255) NOT NULL default '',
  Description text NOT NULL,
  Price float(10,2) NOT NULL default '0.00',
  PRIMARY KEY ID (ID)
);

CREATE TABLE ManageProjects (
  ID int(11) NOT NULL default '0',
  CreatedBy int(11) NOT NULL default '0',
  CreatedDateTime double(16,6) NOT NULL default '0.000000',
  ChangedBy int(11) NOT NULL default '0',
  ChangedDateTime double(16,6) NOT NULL default '0.000000',
  active int(1) NOT NULL default '0',
  PROJECTNAME varchar(255) NOT NULL default '',
  PROJECTDBNAME varchar(255) NOT NULL default '',
  PROJECTID int(11) NOT NULL default '0',
  EMAILSENDDATE double(16,6) NOT NULL default '0.000000',
  VALIDFROM double(16,6) NOT NULL default '0.000000',
  VALIDUNTIL double(16,6) NOT NULL default '0.000000',
  Login varchar(255) NOT NULL default '',
  Password varchar(255) NOT NULL default '',
  Company varchar(255) NOT NULL default '',
  FirstName varchar(255) NOT NULL default '',
  LastName varchar(255) NOT NULL default '',
  Street varchar(255) NOT NULL default '',
  ZipCode varchar(10) NOT NULL default '',
  City varchar(255) NOT NULL default '',
  Country varchar(255) NOT NULL default '',
  Phone varchar(255) NOT NULL default '',
  Fax varchar(255) NOT NULL default '',
  Email varchar(255) NOT NULL default '',
  Comment text NOT NULL,
  ProjectType int(1) NOT NULL default '0',
  Domain int(1) NOT NULL default '0',
  TACRead int(1) NOT NULL default '0',
  ToDo int(1) NOT NULL default '0',
  VITRADORESELLERID varchar(255) NOT NULL default '',
  PRIMARY KEY ID (ID)
);

CREATE TABLE ProjectNames (
  ID int(11) NOT NULL default '0',
  PROJECTNAME varchar(10) NOT NULL default '',
  PRIMARY KEY ID (ID),
  UNIQUE KEY PROJECTNAME (PROJECTNAME)
);

CREATE TABLE IPPool (
  ID int(11) NOT NULL default '0',
  Net varchar(200) NOT NULL default '',
  PoolStart int(11) NOT NULL default '0',
  PoolEnd int(11) NOT NULL default '0',
  HighestIP int(11) NOT NULL default '0'
);

CREATE TABLE IPPool (
  IP int(11) NOT NULL default '0',
  IPPoolID int(11) NOT NULL default '0'
);

GRANT ALL ON dmerce_sys.* TO dmerce_sys@localhost IDENTIFIED BY 'dmerce_sys';
