create or replace view v_dnsnsforwarders as
select n.name,
       f.ip
  from t_dnsns n,
       t_dnsforwarders f
 where f.dnsnsid = n.id;

create or replace view v_dnsnshandles as
select n.name,
       h.handle
  from t_dnsns n,
       t_dnshandles h
 where h.dnsnsid = n.id;

create or replace view v_dnsnslistenon as
select n.name,
       l.ipaddress
  from t_dnsns n,
       t_dnslistenon l
 where l.dnsnsid = n.id;

create or replace view v_dnszonesns as
select z.name as zonename,
       n.name as nameserver,
       n.ipaddress,
       lp.email as zonecemail,
       zn.position,
       z.soaserial,
       z.soasubserial,
       z.soarefresh,
       z.soaretry,
       z.soaexpire,
       z.soamaximum
  from t_dnsns n,
       t_dnszonesns zn,
       t_dnszones z,
       t_legalperson lp
 where zn.dnszonesid = z.id
   and zn.dnsnsid = n.id
   and z.zonecpersonid = lp.id;

create or replace view v_dnszonesrecords as
select r.name as recordname,
       z.name as zonename,
       t.name as recordtype,
       r.value as recordvalue,
       r.mxprio,
       r.validfrom,
       r.validuntil
  from t_dnszones z,
       t_dnsrecords r,
       t_dnsrrtypes t
 where r.dnszoneid = z.id
   and r.type = t.id;
