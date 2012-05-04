@echo off
call set.bat
set DMERCE_APPNAME=%1
if [%1] == [] goto fail
:create
call %ANT_CALL% create-new-application
goto end
:fail
echo Please supply project name.
:end
