@echo off
call set.bat
set DMERCE_APPNAME=%1
if "%1"=="" goto fail
:load
call %ANT_CALL% load-example-application
goto end
:fail
echo Please supply application name
:end
