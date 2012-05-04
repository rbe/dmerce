@echo off
call set.bat
%ORACLE_HOME%\bin\lsnrctl start
net start oracleservice%ORACLE_SID%
%ORACLE_HOME%\bin\sqlplus /nolog @%DMERCE_SQL_ORACLE%/startoracle.sql
exit
