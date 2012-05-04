rem
rem dmerce(R)
rem
rem ATTENTION:
rem Do not adjust this values. They will be changed when
rem running reconfigure or setup!
rem
set START_JBOSS=YES
set START_APACHE=NO
set START_MYSQL=NO
set START_ORACLE=NO
set START_FILEZILLA=NO
set START_PHPMYADMIN=NO
set START_DMERCEDOC=NO
set START_ECLIPSE=NO
if "%DMERCE_BASE%"=="" set DMERCE_BASE=C:\dmerce
set ORACLE_BASE=C:\oracle
set ORACLE_HOME=%ORACLE_BASE%\ora90
set ORACLE_SID=dmerce
rem
rem ATTENTION:
rem DO NOT TOUCH THESE VALUES
rem
set DMERCE_PRODUCT=%DMERCE_BASE%\product\dmerce
set DMERCE_SYS=%DMERCE_PRODUCT%\sys
set DMERCE_SQL_MYSQL=%DMERCE_SYS%\sql\mysql
set DMERCE_SQL_ORACLE=%DMERCE_SYS%\sql\oracle
set ORACLE_LIB=%ORACLE_HOME%\lib
set ORACLE_ADMIN=%ORACLE_HOME%\admin
set ORACLE_DATA=%ORACLE_HOME%\oradata
set JAVA_HOME=%DMERCE_BASE%\product\j2sdk
set ANT_HOME=%DMERCE_BASE%\product\ant
set ANT_CALL=%ANT_HOME%\bin\ant.bat -f %DMERCE_SYS%\ant\build.xml
set JWSDP_HOME=%DMERCE_BASE%\product\jwsdp
set JBOSS_HOME=%DMERCE_BASE%\product\jboss
set ECLIPSE_HOME=%DMERCE_BASE%\product\eclipse
set ECLIPSE_WORKSPACE=%DMERCE_BASE%\eclipse-workspace
set ECLIPSE_CALL=%ECLIPSE_HOME%\eclipse -data %ECLIPSE_WORKSPACE%
set PATH=%JAVA_HOME%\bin;%PATH%
