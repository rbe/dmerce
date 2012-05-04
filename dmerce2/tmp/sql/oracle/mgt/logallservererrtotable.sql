rem -----------------------------------------------------------------------
rem Filename:   db-error.sql
rem Purpose:    Log all database errors to a table
rem             Oracle8i or above/ DBA or CREATE ANY TRIGGER privs/ and
rem		GRANT SELECT ON SYS.V_$SESSION required 
rem Date:       21-Mar-2000
rem Author:     Nico Booyse (booysen@saps.org.za)
rem -----------------------------------------------------------------------

drop trigger log_errors_trig;
drop table   log_errors_tab;

create table log_errors_tab (
	error     varchar2(30),
	timestamp date,
	username  varchar2(30),
        osuser    varchar2(30),
        machine   varchar2(64),
	process   varchar2(8),
	program   varchar2(48));

create or replace trigger log_errors_trig 
	after servererror on database
declare
	var_user     varchar2(30);
	var_osuser   varchar2(30);
	var_machine  varchar2(64);
	var_process  varchar2(8);
	var_program  varchar2(48);
begin
	select username, osuser, machine, process, program
	into   var_user, var_osuser, var_machine, var_process, var_program
	from   sys.v_$session
	where  audsid = userenv('sessionid');

	insert into log_errors_tab
	  values(dbms_standard.server_error(1),sysdate,var_user,
	         var_osuser,var_machine,var_process,var_program);
end;
/
