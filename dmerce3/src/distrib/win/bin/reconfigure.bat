@echo off
call set.bat
if "%1"=="" goto settings
if not "%1"=="" goto appl
:appl
set DMERCE_APPNAME=%1
call %ANT_CALL% reconfigure-webservice
goto end
:settings
call %ANT_CALL% reconfigure-settings-win
:end
