@echo off
cd ..\product\xampp
start "dmerce 3/XAMPP MySQL - stop using stopmysql.bat" mysql_start.bat
cd ..\..
exit
