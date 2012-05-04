@echo off
call set.bat
set DMERCE_APPNAME=%1
if [%1] == [] goto fail
call %ANT_CALL% deploy-war
goto end
:fail
echo Please specify the application to deploy.
:end
