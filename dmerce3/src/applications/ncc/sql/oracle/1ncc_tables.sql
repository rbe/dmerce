create table t_addrcomtype  (
   id                 number                           not null,
   name               varchar2(50)                     not null
);

create table t_dnsns  (
   id                 number                           not null,
   name               varchar2(128)                    not null,
   ipaddress          varchar2(64)                     not null
);

create table t_dnslistenon  (
   id                 number                           not null,
   dnsnsid            number                           not null,
   ipaddress          varchar2(64)                     not null
);

create table t_dnsforwarders  (
   id                 number                           not null,
   dnsnsid            number                           not null,
   ip                 varchar2(15)
);

create table t_dnsrrtypes  (
   id                 number                           not null,
   name               varchar2(250)                    not null
);

create table t_dnszones  (
   id                 number                           not null,
   active             number(1),
   name               varchar2(64)                     not null,
   soaserial          date                             not null,
   soasubserial       number(2)                        not null,
   soarefresh         number                           not null,
   soaretry           number                           not null,
   soaexpire          number                           not null,
   soamaximum         number                           not null,
   ttl                number                           not null,
   legalpersonid      number,
   admincpersonid     number,
   zonecpersonid      number,
   techcpersonid      number,
   validfrom          date,
   validuntil         date
);

create table t_dnszonesns  (
   id                 number                           not null,
   position           number,
   dnszonesid         number,
   dnsnsid            number
);

create table t_dnsrecords  (
   id                 number                           not null,
   dnszoneid          number                           not null,
   active             number,
   name               varchar2(64),
   type               number                           not null,
   mxprio             number(3),
   value              varchar2(64)                     not null,
   ptr                number,
   validfrom          date,
   validuntil         date
);

create table t_dnshandles  (
   id                 number                           not null,
   dnsnsid            number                           not null,
   handle             varchar2(25)                     not null,
   comments           varchar2(1000)
);

create table t_lpcomm  (
   id                 number                           not null,
   legalpersonid      number                           not null,
   addrcommtypeid     number                           not null,
   value              varchar2(100)                    not null
);

create table t_lptypes  (
   id                 number                           not null,
   lptype             varchar2(100)                    not null
);

create table t_profile  (
   id                 number                           not null,
   popmail            number(1)             default 1  not null,
   imapmail           number(1)             default 0  not null,
   maxpopaccounts     number                default 5  not null,
   maximapaccounts    number                default 5  not null,
   maxdiskquota       number                           not null
);

create table t_legalperson  (
   id                 number                           not null,
   active             number                           not null,
   lptypeid           number                           not null,
   company            varchar2(50),
   lastname           varchar2(30)                     not null,
   firstname          varchar2(30)                     not null,
   street             varchar2(100)                    not null,
   zipcode            varchar2(10)                     not null,
   city               varchar2(30)                     not null,
   email              varchar2(150)                    not null,
   profileid          number,
   login              varchar2(150)                    not null,
   passwd             varchar2(12)                     not null,
   logindisabled      number(1)             default 0  not null,
   comments           varchar2(1000)
);
