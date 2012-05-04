connect sys/change_on_install as sysdba
set echo on
spool C:\oracle\admin\dmerce\postdbcreation.log
create spfile='?\database\spfiledmerce.ora' FROM pfile='C:\oracle\admin\dmerce\pfile\initdmerce.ora';
connect sys/change_on_install as sysdba
set echo on
spool C:\oracle\admin\dmerce\postdbcreation.log
shutdown ;
startup ;

create user dmerce identified by dmerce
    default tablespace users
    temporary tablespace temp
    quota unlimited on users
    quota unlimited on temp;

grant create session to dmerce;
grant resource to dmerce;
