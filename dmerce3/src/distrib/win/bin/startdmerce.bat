@echo off
call set.bat
if /i %START_MYSQL%==YES start /min startmysql.bat
if /i %START_APACHE%==YES start /min startapache.bat
if /i %START_ORACLE%==YES start /min /wait startoracle.bat
start /min /wait startjboss.bat
if /i %START_FILEZILLA%==YES start /min startfilezilla.bat
if /i %START_PHPMYADMIN%==YES start "\Programme\Internet Explorer\iexplore" http://localhost/phpmyadmin
if /i %START_DMERCEDOC%==YES start "\Programme\Internet Explorer\iexplore" http://localhost:8080/dmerce-doc
if /i %START_ECLIPSE%==YES start /min starteclipse.bat
