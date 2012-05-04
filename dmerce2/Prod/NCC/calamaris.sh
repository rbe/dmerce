#!/bin/sh

export calamaris=`which calamaris`

cat /usr/local/squid/logs/access.log | ${calamaris} -d 100 -P 30 -r 50 -s -t 50 -w > /usr/local/www/data/squid/`date +%Y%m%d`.html
/usr/local/sbin/squid -k rotate 
