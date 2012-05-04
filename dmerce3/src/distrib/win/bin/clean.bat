@echo off
call set.bat
if [%1] == [] goto standard
:param
call %ANT_CALL% clean-%1
goto end
:standard
call %ANT_CALL% clean
:end
