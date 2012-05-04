@echo off
set JAVA_HOME=C:\j2sdk1.4.2
set PATH=%JAVA_HOME%\bin;%PATH%
java -cp whois.jar com.wanci.dmerce.cli.Whois %1%
