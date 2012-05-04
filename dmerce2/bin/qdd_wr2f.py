#!/usr/bin/env python1.5
##
#
# $Author: rb $
revision = '$Revision: 1.2 $'
#
##

#import sys
#sys.path.append('/export/home/r/rb/dmerce2')
import vars
vars.vars()
import time
import os
import urllib
import DMS.SQL
#import Guardian.Config

def t():
    return time.strftime('%a %b %d %X %Z %Y:', time.localtime(time.time()))

if __name__ == '__main__':
    print vars.COPYRIGHT_SCRIPT
    #conf = Guardian.Config.RFC822(os.environ['DMERCE_HOME'])
    #connStr = conf.cp.get('q', 'Sys')
    print t(), 'CONNECTING DATABASE'
    connStr = "MySQL:dmerce_wat:yO%/mK@localhost:dmerce_wat"
    l1 = DMS.SQL.Layer1(connStr)
    l1.Init()
    sql = l1.GetQuery()
    print t(), 'QUERY DATABASE'
    rc, r = sql["SELECT ID FROM dmerce_wat.Article"]
    for i in range(rc):
        id1 = r[i]['ID']
       	id = str(id1)
       	url = [
	       ('de-detail-b2b', 'http://www.de.wat-shop.de/1Ci/dmerce?qTemplate=Artikel,detail_cd&qSuperSearch=ID*' + id),
	       ('en-detail-b2b', 'http://www.en.wat-shop.de/1Ci/dmerce?qTemplate=Artikel,detail_cd&qSuperSearch=ID*' + id),
	       ('de-zoom', 'http://www.de.wat-shop.de/1Ci/dmerce?qTemplate=Artikel,zoom_cd&qSuperSearch=ID*' + id),
       	       ('en-zoom', 'http://www.en.wat-shop.de/1Ci/dmerce?qTemplate=Artikel,zoom_cd&qSuperSearch=ID*' + id),
               ('de-detail-b2c', 'http://www.de.wat-shop.de/1Ci/dmerce?qTemplate=Article,detail_cd_b2c&qSuperSearch=ID*' + id),
               ('en-detail-b2c', 'http://www.en.wat-shop.de/1Ci/dmerce?qTemplate=Article,detail_cd_b2c&qSuperSearch=ID*' + id)

	]
	for u in url:
       		print t(), 'RETRIEVING URL:', u
       		data = urllib.urlopen(u[1])
       		lines = data.readlines()
       		fileName = id + '.html'
       		print t(), 'WRITING FILE:', fileName
       		fdOut = open(u[0] + '/' + fileName, 'w')
       		fdOut.writelines(lines)
       		fdOut.close()
