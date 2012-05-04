#!/bin/sh
NCCDATA=/export/honey/opt/dmerce/NCC
SERVER=bunny.frankfurt1.de.1ci.net
CMDPATH=/usr/local/bin
DESTPATH=/opt/dmerce/product/apache

$CMDPATH/scp $NCCDATA/$SERVER/http/apache/vhost*.conf $SERVER:$DESTPATH
$CMDPATH/ssh $SERVER "chown dmerce:dmerce /opt/dmerce/product/apache/vhost*"
$CMDPATH/ssh $SERVER "sh /etc/init.d/dmerce-apache-1.3.12 stop"
$CMDPATH/ssh $SERVER "sh /etc/init.d/dmerce-apache-1.3.12 start"
