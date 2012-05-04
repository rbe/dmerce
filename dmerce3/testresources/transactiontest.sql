-- $Id: transactiontest.sql,v 1.1 2003/08/26 13:17:23 pg Exp $
-- init sql skript für datenbank
insert into status (id, colorcode) values (status_seq.nextval, 'blue')
/
insert into status (id, colorcode) values (status_seq.nextval, 'green')
/
insert into os (id, name, version, active) values (os_seq.nextval, 'all', '0', 0)
/
insert into transactions (id, name, description, module) values (transactions_seq.nextval, 'WRITEPORTSTATUS', 'write port status into database', 'nccscanner')
/
insert into transactionsteps (id, name, description) values (transactionsteps_seq.nextval, 'SQLPORTSTATUS', 'Insert statement for port state')
/
insert into transactionossteps (id, transactionId, osId, type, command) values (transactionossteps_seq.nextval, 1, 1, 'sql', 'EXECUTE sp_nccsaveportscanresult (%ip%, %port%, %status%, %answertime%, %service%)')
/
insert into transactionsteplinks (id, transactionid, transactionstepid, position) values (transactionsteplinks_seq.nextval, 1, 1, 1)
/
insert into ports (id, portnumber, protocol) values (ports_seq.nextval, 80, 'tcp')
/
insert into srvservices (id, name, portid) values (srvservices_seq.nextval, 'http', 1)
/
-- real data
execute sp_nccsaveserver(v_name => 'gandalf', v_statusid => 1)
/
insert into srvserverinterfaces (id, ifName, serverid, ifphysaddress) values (srvserverinterfaces_seq.nextval, 'eth0', 1, '00:00')
/
insert into srvserverinterfaceips (id, ipv4, interfaceid) values (srvserverinterfaceips_seq.nextval, '10.48.35.3', 1)
/
execute sp_nccsaveportscanresult (v_ip => '10.48.35.3', v_port => '80', v_status => 'open')
/
execute sp_nccsaveportscanresult('10.48.35.3',80,'tcp','open',1000, null)
/