connect sys/change_on_install as sysdba
set echo on
spool C:\oracle\admin\dmerce\createdbfiles.log
CREATE TABLESPACE "INDX" LOGGING DATAFILE 'C:\oracle\oradata\dmerce\indx01.dbf' SIZE 10M REUSE AUTOEXTEND ON NEXT  1280K MAXSIZE UNLIMITED EXTENT MANAGEMENT LOCAL;
CREATE TEMPORARY TABLESPACE "TEMP" TEMPFILE 'C:\oracle\oradata\dmerce\temp01.dbf' SIZE 10M REUSE AUTOEXTEND ON NEXT  640K MAXSIZE UNLIMITED EXTENT MANAGEMENT LOCAL;
ALTER DATABASE DEFAULT TEMPORARY TABLESPACE "TEMP";
CREATE TABLESPACE "USERS" LOGGING DATAFILE 'C:\oracle\oradata\dmerce\users01.dbf' SIZE 25M REUSE AUTOEXTEND ON NEXT  1280K MAXSIZE UNLIMITED EXTENT MANAGEMENT LOCAL;
exit;
