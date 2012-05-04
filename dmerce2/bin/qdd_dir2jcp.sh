#!/usr/bin/ksh

#set -x
DIR="/export/home/rb/eclipse-workspace/dmerce3/lib/core /export/home/rb/eclipse-workspace/dmerce3/lib/shared"
for i in $DIR/*
do
	echo $i
	CLASSPATH="$CLASSPATH:$i"
done
export CLASSPATH
