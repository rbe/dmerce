#!/bin/ksh

MYSQL="/opt/dmerce/product/mysql/3.23.55"
MYSQL_DATA="/opt/dmerce/product/mysql/3.23.55/var"
#MYSQL_DATA="$MYSQL/var"
MYSQLDUMP="$MYSQL/bin/mysqldump -u root --password=%v4h5f0"
DATABASES="`find $MYSQL_DATA -type d | sed 's/\/opt\/dmerce\/product\/mysql\/3.23.55\/var\///g' `"
DUMP_PATH="/opt/dmerce/sql/mysql/dump/cini"
DATETIME="`date +%Y%m%d-%H%M`"

#find $DUMP_PATH -atime 3 -name "*.dump-*" -exec rm -rf {} \;

for i in $DATABASES
  do
        $MYSQLDUMP $i > $DUMP_PATH/$i.dump-$DATETIME
  done
