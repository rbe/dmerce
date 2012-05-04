@echo off
call set.bat
mkdir %ORACLE_ADMIN%\dmerce
mkdir %ORACLE_ADMIN%\dmerce\bdump
mkdir %ORACLE_ADMIN%\dmerce\cdump
mkdir %ORACLE_ADMIN%\dmerce\udump
copy %DMERCE_SQL_ORACLE%\dmerce\initdmerce.ora %ORACLE_HOME%\database\initdmerce.ora
mkdir %ORACLE_DATA%\dmerce
%ORACLE_HOME%\bin\oradim -new  -sid DMERCE -startmode m  -pfile %DMERCE_ADMIN%\dmerce\pfile\init.ora
%ORACLE_HOME%\bin\oradim -edit  -sid DMERCE -startmode a 
%ORACLE_HOME%\bin\orapwd file=%ORACLE_HOME%\database\PWDdmerce.ORA entries=3 password=oracle
%ORACLE_HOME%\bin\sqlplus /nolog @%DMERCE_SQL_ORACLE%\dmerce\createdb.sql
%ORACLE_HOME%\bin\sqlplus /nolog @%DMERCE_SQL_ORACLE%\dmerce\createdbfiles.sql
%ORACLE_HOME%\bin\sqlplus /nolog @%DMERCE_SQL_ORACLE%\dmerce\createdbcatalog.sql
%ORACLE_HOME%\bin\sqlplus /nolog @%DMERCE_SQL_ORACLE%\dmerce\postdbcreation.sql
