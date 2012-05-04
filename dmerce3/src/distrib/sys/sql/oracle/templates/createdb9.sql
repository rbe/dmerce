connect sys/change_on_install as sysdba
set echo on
spool C:\oracle\admin\dmerce\createdb.log
startup nomount pfile="C:\oracle\admin\dmerce\pfile\initdmerce.ora";
CREATE DATABASE dmerce
MAXINSTANCES 1
MAXLOGHISTORY 1
MAXLOGFILES 5
MAXLOGMEMBERS 5
MAXDATAFILES 100
DATAFILE 'C:\oracle\oradata\dmerce\system01.dbf' SIZE 325M REUSE AUTOEXTEND ON NEXT  10240K MAXSIZE UNLIMITED
UNDO TABLESPACE "UNDOTBS" DATAFILE 'C:\oracle\oradata\dmerce\undotbs01.dbf' SIZE 100M REUSE AUTOEXTEND ON NEXT  5120K MAXSIZE UNLIMITED
CHARACTER SET WE8ISO8859P15
NATIONAL CHARACTER SET AL16UTF16
LOGFILE GROUP 1 ('C:\oracle\oradata\dmerce\redo01.log') SIZE 10M,
GROUP 2 ('C:\oracle\oradata\dmerce\redo02.log') SIZE 10M,
GROUP 3 ('C:\oracle\oradata\dmerce\redo03.log') SIZE 10M;
spool off
exit;
