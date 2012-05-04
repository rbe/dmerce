@echo off
call set.bat
set PATH=%JAVA_HOME%\bin;%PATH%
start %ECLIPSE_HOME%\eclipse.exe -data %ECLIPSE_WORKSPACE%
