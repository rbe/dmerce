/*
 * Assign and unassign DNS server to a zone
 *
 */
create or replace procedure sp_assignzonetodns(
v_domainname in varchar2,
v_position in number,
v_nameserver in varchar2)
as
    v_count          number;
    v_nameserverid   number;
    v_dnszonesid     number;
begin

    select id into v_nameserverid from t_dnsns where name = v_nameserver;
    
    select id into v_dnszonesid from t_dnszones where name = v_domainname;
    
    select count(*) into v_count from t_dnszonesns
     where dnszonesid = v_dnszonesid
       and dnsnsid = v_nameserverid;
       
    if v_count = 0
    then
        insert into t_dnszonesns (id, position, dnszonesid, dnsnsid)
        values (t_dnszonesns_seq.nextval, v_position, v_dnszonesid, v_nameserverid);
    end if;

end;
/

create or replace procedure sp_unassignzonefromdns(
v_domainname in varchar2)
as
	v_dnszonesid	number;
begin

	select id into v_dnszonesid
	  from t_dnszones
	 where name = v_domainname;

	delete from t_dnszonesns
	 where dnszonesid = v_dnszonesid;

end;
/

/*
 * Assign and unassign persons to zones
 *
 */
create or replace procedure sp_assignzonetoperson(
v_domainname in varchar2,
v_legalperson in varchar2,
v_legalpersontype in varchar2 default null)
as
	v_dnszoneid		number;
	v_legalpersonid	number;
begin

	select id into v_dnszoneid
	  from t_dnszones
	 where name = v_domainname;

	select id into v_legalpersonid
	  from t_legalperson
	 where lastname = v_legalperson;
	 
	 if upper(v_legalpersontype) = 'ADMINC'
	 then
		 update t_dnszones
		    set admincpersonid = v_legalpersonid
		  where id = v_dnszoneid;
	 elsif upper(v_legalpersontype) = 'TECHC'
	 then
		 update t_dnszones
		    set techcpersonid = v_legalpersonid
		  where id = v_dnszoneid;
	 elsif upper(v_legalpersontype) = 'ZONEC'
	 then
		 update t_dnszones
		    set zonecpersonid = v_legalpersonid
		  where id = v_dnszoneid;
	 elsif v_legalpersontype is null or upper(v_legalpersontype) = 'AGENT'
	 then
		 update t_dnszones
		    set legalpersonid = v_legalpersonid
		  where id = v_dnszoneid;
	 end if;
	 
end;
/

/*
 * Removes an assignment of a domain to a person
 *
 */
create or replace procedure sp_unassignzonefromperson(
v_domainname in varchar2,
v_legalpersontype in varchar2 default null)
as
	v_dnszoneid	number;
begin

	select id into v_dnszoneid
	  from t_dnszones
	 where name = v_domainname;

	 if upper(v_legalpersontype) = 'ADMINC'
	 then
		 update t_dnszones
		    set admincpersonid = null
		  where id = v_dnszoneid;
	 elsif upper(v_legalpersontype) = 'TECHC'
	 then
		 update t_dnszones
		    set techcpersonid = null
		  where id = v_dnszoneid;
	 elsif upper(v_legalpersontype) = 'ZONEC'
	 then
		 update t_dnszones
		    set zonecpersonid = null
		  where id = v_dnszoneid;
	 elsif v_legalpersontype is null or upper(v_legalpersontype) = 'AGENT'
	 then
		 update t_dnszones
		    set legalpersonid = null
		  where id = v_dnszoneid;
	 end if;
	 
end;
/

/*
 * Create a legal person
 *
 */
create or replace procedure sp_createlegalperson(
v_login in varchar2,
v_password in varchar2,
v_logindisabled number default 0, 
v_company in varchar2 default null,
v_firstname in varchar2,
v_lastname in varchar2,
v_street in varchar2,
v_zipcode in varchar2,
v_city in varchar2,
v_email in varchar2,
v_legalpersontype in varchar2)
as
    v_lptypeid    number;
begin

    if v_login is null or v_password is null or v_firstname is null
    or v_lastname is null or v_legalpersontype is null
    then
        raise value_error;
    end if;
    
    select id into v_lptypeid from t_lptypes where lptype = v_legalpersontype;

    insert into t_legalperson (id, login, passwd, logindisabled,
    lptypeid, company, firstname, lastname, street, zipcode, city,
    email, active)
    values (t_legalperson_seq.nextval, v_login, v_password, v_logindisabled,
    v_lptypeid, v_company, v_firstname, v_lastname, v_street, v_zipcode, v_city,
    v_email, 1);

end;
/

/*
 * Create types for legal persons
 *
 */
create or replace procedure sp_createlegalpersontypes
as
begin

    insert into t_lptypes (id, lptype)
    values (1, 'Agent');

    insert into t_lptypes (id, lptype)
    values (2, 'Reseller');

    insert into t_lptypes (id, lptype)
    values (3, 'Customer');

    insert into t_lptypes (id, lptype)
    values (4, 'DNS - Administrative Contact');

    insert into t_lptypes (id, lptype)
    values (5, 'DNS - Zone Contact');

    insert into t_lptypes (id, lptype)
    values (6, 'DNS - Technical Contact');

end;
/

/*
 * Create a nameserver
 *
 */
create or replace procedure sp_createnameserver(
v_nameserver in varchar2,
v_ip in varchar2)
as
    v_count     number;
begin

    if v_nameserver is null or v_ip is null
    then
        raise value_error;
    end if;
    
    select count(*) into v_count from t_dnsns
     where name = v_nameserver;
    
    if v_count = 0
    then
        insert into t_dnsns (id, name, ipaddress)
        values (t_dnsns_seq.nextval, v_nameserver, v_ip);
    end if;

end;
/

/*
 * Create a resource record in a domain
 *
 */
create or replace procedure sp_createarecord(
v_domainname in varchar2,
v_recordname in varchar2 default null,
v_recordvalue in varchar2)
as
    v_count           number;
    v_dnszoneid       number;
    v_recordtypeid    number;
begin

    if v_recordvalue is null or v_recordname is null
    then
        raise value_error;
    end if;
    
    select id into v_dnszoneid
      from t_dnszones
     where name = v_domainname;
     
    if v_dnszoneid = 0
    then
        raise value_error;
    end if;

    select id into v_recordtypeid
      from t_dnsrrtypes
     where name = 'A';
    
    select count(*) into v_count
      from t_dnsrecords
     where dnszoneid = v_dnszoneid
       and name = v_recordname
       and type = v_recordtypeid
       and value = v_recordvalue;
    
    if v_count = 0
    then
        insert into t_dnsrecords (id, dnszoneid, name, type, value)
        values (t_dnsrecords_seq.nextval, v_dnszoneid, v_recordname,
        v_recordtypeid, v_recordvalue);
    end if;

end;
/

create or replace procedure sp_createcnamerecord(
v_domainname in varchar2,
v_recordname in varchar2 default null,
v_recordvalue in varchar2)
as
    v_count           number;
    v_dnszoneid       number;
    v_recordtypeid    number;
begin

    if v_recordvalue is null or v_recordname is null
    then
        raise value_error;
    end if;
    
    select id into v_dnszoneid
      from t_dnszones
     where name = v_domainname;
     
    if v_dnszoneid = 0
    then
        raise value_error;
    end if;

    select id into v_recordtypeid
      from t_dnsrrtypes
     where name = 'CNAME';
    
    select count(*) into v_count
      from t_dnsrecords
     where dnszoneid = v_dnszoneid
       and name = v_recordname
       and type = v_recordtypeid
       and value = v_recordvalue;
    
    if v_count = 0
    then
        insert into t_dnsrecords (id, dnszoneid, name, type, value)
        values (t_dnsrecords_seq.nextval, v_dnszoneid, v_recordname,
        v_recordtypeid, v_recordvalue);
    end if;

end;
/

create or replace procedure sp_createmxrecord(
v_domainname in varchar2,
v_mxprio in number default 10,
v_recordvalue in varchar2)
as
    v_count           number;
    v_dnszoneid       number;
    v_recordtypeid    number;
begin

    if v_recordvalue is null
    then
        raise value_error;
    end if;
    
    select id into v_dnszoneid
      from t_dnszones
     where name = v_domainname;
     
    if v_dnszoneid = 0
    then
        raise value_error;
    end if;

    select id into v_recordtypeid
      from t_dnsrrtypes
     where name = 'MX';
    
    select count(*) into v_count
      from t_dnsrecords
     where dnszoneid = v_dnszoneid
       and type = v_recordtypeid
       and value = v_recordvalue;
    
    if v_count = 0
    then
        insert into t_dnsrecords (id, dnszoneid, type, mxprio, value)
        values (t_dnsrecords_seq.nextval, v_dnszoneid, v_recordtypeid,
        v_mxprio, v_recordvalue);
    end if;

end;
/

/*
 * Create a zone
 *
 */
create or replace procedure sp_createzone(
v_domainname in varchar2,
v_soaserial in varchar2 default null,
v_soarefresh in number default 10800,
v_soaretry in number default 3600,
v_soaexpire in number default 3600000,
v_soamaximum in number default 345600,
v_ttl in number default 10800)
as
    v_count          number;
    v_sq_dnszones    number;
    v_sq_dnsrecords  number;
    v_recordtypeid   number;
    v_realserial     date;
begin

    if v_domainname is null
    then
        raise value_error;
    end if;
    
    if v_soaserial is null
    then
        select sysdate into v_realserial from dual;
    else
        v_realserial := v_soaserial;
    end if;
    
    select count(*) into v_count from t_dnszones where name = v_domainname;
    if v_count = 0
    then
        select t_dnszones_seq.nextval into v_sq_dnszones from dual;
        select t_dnsrecords_seq.nextval into v_sq_dnsrecords from dual;
        select id into v_recordtypeid from t_dnsrrtypes where upper(name) = 'A';

        insert into t_dnszones (id, name, soaserial, soasubserial, soarefresh,
        soaretry, soaexpire, soamaximum, ttl)
        values (v_sq_dnszones, v_domainname, v_realserial, 1, v_soarefresh, v_soaretry,
        v_soaexpire, v_soamaximum, v_ttl);
    
        insert into t_dnsrecords (id, dnszoneid, name, type, value)
        values (v_sq_dnsrecords, v_sq_dnszones, 'localhost', v_recordtypeid, '127.0.0.1');
    end if;

end;
/

/*
 * Drop a zone: delete all records, unassign zone from nameservers
 * and delete it
 *
 */
create or replace procedure sp_dropzone(
v_domainname in varchar2)
as
	v_dnszoneid	number;
begin

	select id into v_dnszoneid
	  from t_dnszones
	 where name = v_domainname;
	 
	sp_unassignzonefromperson(v_domainname);
	sp_unassignzonefromdns(v_domainname);
	 
	delete from t_dnsrecords
	 where dnszoneid = v_dnszoneid;
	  
	delete from t_dnszones
	 where id = v_dnszoneid;

end;
/

/*
 * Change domain data
 *
 * - Relocate a whole domain: change ip addresses (old -> new)
 * - Change certain IP address in all A records
 * - Change certain MX records
 * - Change certan A records
 * - Change certain CNAME records
 *
 */
create or replace procedure sp_changeallipsindomain(
v_domainname in varchar2,
v_newip in varchar2)
as
	v_dnszoneid	number;
begin

	select id into v_dnszoneid
	  from t_dnszones
	 where name = v_domainname;
	 
	update t_dnsrecords
	   set value = v_newip
	 where dnszoneid = v_dnszoneid
	   and type in (select id
	                  from t_dnsrrtypes
	                 where name in ('A', 'MX'));

end;
/

create or replace procedure sp_changemxindomain(
v_domainname in varchar2,
v_oldmx in varchar2,
v_newmx in varchar2)
as
	v_dnszoneid	number;
begin

	select id into v_dnszoneid
	  from t_dnszones
	 where name = v_domainname;
	 
	update t_dnsrecords
	   set value = v_newmx
	 where dnszoneid = v_dnszoneid
	   and value = v_oldmx
	   and type in (select id
	                  from t_dnsrrtypes
	                 where name = 'MX');

end;
/

create or replace procedure sp_changeaindomain(
v_domainname in varchar2,
v_olda in varchar2,
v_newa in varchar2)
as
	v_dnszoneid	number;
begin

	select id into v_dnszoneid
	  from t_dnszones
	 where name = v_domainname;
	 
	update t_dnsrecords
	   set value = v_newa
	 where dnszoneid = v_dnszoneid
	   and value = v_olda
	   and type in (select id
	                  from t_dnsrrtypes
	                 where name = 'A');

end;
/

create or replace procedure sp_changecnameindomain(
v_domainname in varchar2,
v_oldcname in varchar2,
v_newcname in varchar2)
as
	v_dnszoneid	number;
begin

	select id into v_dnszoneid
	  from t_dnszones
	 where name = v_domainname;
	 
	update t_dnsrecords
	   set value = v_newcname
	 where dnszoneid = v_dnszoneid
	   and value = v_oldcname
	   and type in (select id
	                  from t_dnsrrtypes
	                 where name = 'CNAME');

end;
/

/*
 * Setup 1[NCC]
 *
 */
create or replace procedure sp_setupncc
as
begin

    dbms_output.put_line('creating legal person types');
    sp_createlegalpersontypes;
    
    dbms_output.put_line('creating master agent');
    sp_createlegalperson(v_legalpersontype => 'Agent',
    v_login => 'master',
    v_password => 'master',
    v_firstname => 'Master',
    v_lastname => 'of Desaster',
    v_street => 'Nowhere Ally 5',
    v_zipcode => '12345',
    v_city => 'Anywhere',
    v_email => 'master@ofdisaster.com');
    
    dbms_output.put_line('creating dns rr entries');
    insert into t_dnsrrtypes (id, name)
    values (1, 'A');
    insert into t_dnsrrtypes (id, name)
    values (2, 'CNAME');
    insert into t_dnsrrtypes (id, name)
    values (3, 'MX');
    insert into t_dnsrrtypes (id, name)
    values (4, 'PTR');
/*    
    dbms_output.put_line('creating nameserver ns1.1xsp.com');
    sp_createnameserver(v_nameserver => 'ns1.1xsp.com',
    v_ip => '213.128.138.193');
    
    dbms_output.put_line('creating nameserver ns2.1xsp.com');
    sp_createnameserver(v_nameserver => 'ns2.1xsp.com',
    v_ip => '80.237.202.69');
    
    dbms_output.put_line('creating nameserver ns3.1xsp.com');
    sp_createnameserver(v_nameserver => 'ns3.1xsp.com',
    v_ip => '62.72.64.193');
    
    dbms_output.put_line('creating nameserver ns4.1xsp.com');
    sp_createnameserver(v_nameserver => 'ns4.1xsp.com',
    v_ip => '213.203.245.100');
    
    dbms_output.put_line('creating zone example-domain.tld');
    sp_createzone(v_domainname => 'example-domain.tld');
    sp_assignzonetoperson(v_domainname => 'example-domain.tld',
    v_legalperson => 'of Desaster');
    sp_assignzonetoperson(v_domainname => 'example-domain.tld',
    v_legalperson => 'of Desaster',
    v_legalpersontype => 'adminc');
    sp_assignzonetoperson(v_domainname => 'example-domain.tld',
    v_legalperson => 'of Desaster',
    v_legalpersontype => 'techc');
    sp_assignzonetoperson(v_domainname => 'example-domain.tld',
    v_legalperson => 'of Desaster',
    v_legalpersontype => 'zonec');
    sp_assignzonetodns(v_domainname => 'example-domain.tld',
    v_position => 1,
    v_nameserver => 'ns1.1xsp.com');
    sp_assignzonetodns(v_domainname => 'example-domain.tld',
    v_position => 2,
    v_nameserver => 'ns2.1xsp.com');
    sp_assignzonetodns(v_domainname => 'example-domain.tld',
    v_position => 3,
    v_nameserver => 'ns3.1xsp.com');
    sp_assignzonetodns(v_domainname => 'example-domain.tld',
    v_position => 4,
    v_nameserver => 'ns4.1xsp.com');
    
    dbms_output.put_line('creating record www A 192.168.1.1 in zone example-domain.tld');
    sp_createrrecord(v_domainname => 'example-domain.tld',
    v_recordname => 'www',
    v_recordtype => 'A',
    v_recordvalue => '192.168.1.1');

    dbms_output.put_line('creating zone example-domain2.tld');
    sp_createzone(v_domainname => 'example-domain2.tld');
    sp_assignzonetoperson(v_domainname => 'example-domain2.tld',
    v_legalperson => 'of Desaster');
    sp_assignzonetoperson(v_domainname => 'example-domain2.tld',
    v_legalperson => 'of Desaster',
    v_legalpersontype => 'adminc');
    sp_assignzonetoperson(v_domainname => 'example-domain2.tld',
    v_legalperson => 'of Desaster',
    v_legalpersontype => 'techc');
    sp_assignzonetoperson(v_domainname => 'example-domain2.tld',
    v_legalperson => 'of Desaster',
    v_legalpersontype => 'zonec');
    sp_assignzonetodns(v_domainname => 'example-domain2.tld',
    v_position => 1,
    v_nameserver => 'ns1.1xsp.com');
    sp_assignzonetodns(v_domainname => 'example-domain2.tld',
    v_position => 2,
    v_nameserver => 'ns2.1xsp.com');
    sp_assignzonetodns(v_domainname => 'example-domain2.tld',
    v_position => 3,
    v_nameserver => 'ns3.1xsp.com');
    sp_assignzonetodns(v_domainname => 'example-domain2.tld',
    v_position => 4,
    v_nameserver => 'ns4.1xsp.com');
    
    dbms_output.put_line('creating record www A 192.168.1.2 in zone example-domain.tld');
    sp_createrrecord(v_domainname => 'example-domain2.tld',
    v_recordname => 'www',
    v_recordtype => 'A',
    v_recordvalue => '192.168.1.2');
*/
end;
/
