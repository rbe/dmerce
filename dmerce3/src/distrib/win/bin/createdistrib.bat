@echo off
call set.bat
if "%1"=="" call %ANT_CALL% create-distrib
if "%1"=="clean" call %ANT_CALL% create-clean-distrib
