@echo off
call set.bat
%ORACLE_HOME%\bin\sqlplus /nolog @%DMERCE_SQL_ORACLE%/stoporacle.sql
%ORACLE_HOME%\bin\lsnrctl stop
net stop oracleservice%ORACLE_SID%
exit
