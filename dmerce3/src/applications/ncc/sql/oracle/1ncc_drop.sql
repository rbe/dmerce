drop view v_dnszonesrecords;
drop view v_dnszonesns;
drop view v_dnsnslistenon;
drop view v_dnsnshandles;
drop view v_dnsnsforwarders;

drop table t_dnshandles cascade constraints;
drop table t_dnsrecords cascade constraints;
drop table t_legalperson cascade constraints;
drop table t_dnsforwarders cascade constraints;
drop table t_dnslistenon cascade constraints;
drop table t_profile cascade constraints;
drop table t_lptypes cascade constraints;
drop table t_lpcomm cascade constraints;
drop table t_dnszonesns cascade constraints;
drop table t_dnszones cascade constraints;
drop table t_dnsrrtypes cascade constraints;
drop table t_dnsns cascade constraints;
drop table t_addrcomtype cascade constraints;

drop sequence t_dnsns_seq;
drop sequence t_dnsrecords_seq;
drop sequence t_dnszones_seq;
drop sequence t_dnszonesns_seq;
drop sequence t_legalperson_seq;

drop procedure sp_setupncc
/
drop procedure sp_createzone
/
drop procedure sp_createarecord
/
drop procedure sp_createcnamerecord
/
drop procedure sp_createmxrecord
/
drop procedure sp_createnameserver
/
drop procedure sp_createlegalpersontypes
/
drop procedure sp_createlegalperson
/
drop procedure sp_assignzonetoperson
/
drop procedure sp_assignzonetodns
/
