#!/bin/ksh

MYSQL="/opt/dmerce/product/mysql/3.23.47"
MYSQL_DATA="/opt/dmerce/product/mysql/3.23.47/var"
#MYSQL_DATA="$MYSQL/var"
MYSQLDUMP="$MYSQL/bin/mysqldump -u root --password=%v4h5f0"
DATABASES="`find $MYSQL_DATA -type d | sed 's/\/opt\/dmerce\/product\/mysql\/3.23.47\/var\///g' `"
DUMP_PATH="/opt/dmerce/sql/mysql/dump/gandalf"
DATETIME="`date +%Y%m%d-%H%M`"

#find $DUMP_PATH -atime 3 -name "*.dump-*" -exec rm -rf {} \;

for i in $DATABASES
  do
        $MYSQLDUMP $i > $DUMP_PATH/$i.dump-$DATETIME
  done
