@echo off
call set.bat
pushd %DMERCE_BASE%\product\xampp\FileZillaFTP
start "dmerce 3/XAMPP FileZilla" FileZillaServer.exe /stop
popd
exit
