@echo off
call set.bat
if /i %START_FILEZILLA%==YES start /min stopfilezilla.bat
if /i %START_MYSQL%==YES start /min stopmysql.bat
if /i %START_ORACLE%==YES start /min stoporacle.bat
