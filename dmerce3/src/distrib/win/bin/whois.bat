@echo off
call set.bat
java -cp %DMERCE_PRODUCT%\dmerce\whois.jar com.wanci.dmerce.cli.Whois %1%
