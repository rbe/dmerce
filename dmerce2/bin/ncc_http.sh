#!/bin/sh

dmerceHome=/export/honey/opt/dmerce
 
/etc/init.d/dmerce-apache-1.3.12 stop
/etc/init.d/apache-1.3.20 stop


dapache=/export/honey/opt/dmerce/product/apache/vhost.conf
napache=/export/honey/opt/apache/vhost.conf

cp $dmerceHome/NCC/honey.hamburg2.de.1ci.net/http/apache-1.3.20/vhost.conf $napache
cp $dmerceHome/NCC/honey.hamburg2.de.1ci.net/http/dmerce-apache/vhost.conf $dapache

chown root:other $napache
chown dmerce:dmerce $dapache

sleep 2

/etc/init.d/dmerce-apache-1.3.12 start
/etc/init.d/apache-1.3.20 start

