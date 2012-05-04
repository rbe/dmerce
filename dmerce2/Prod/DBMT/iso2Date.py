#!/usr/bin/env python
##
#
# $Author: ka $
# $Revision: 1.2 $
# $Log: iso2Date.py,v $
# Revision 1.2  2001/12/14 15:18:02  ka
# cleaned syntax error
#
# Revision 1.1  2001/11/09 17:34:33  rb
# added new version of NCC, DGP, some tools (bin)
#
# Revision 1.11  2000-08-30 11:05:45+02  ka
# *** Empty log message ***
#
##

import time
import string
import MySQLdb

#
# Class to convert iso-date to timestamp
#
class isoDate:
  
  def convert(self,iso_date=''):
    day = int(iso_date[0][0:4])
    month = int(iso_date[0][5:7])
    year = int(iso_date[0][8:10])
    hour = int(iso_date[0][11:13])
    min = int(iso_date[0][14:16])
    sec = int(iso_date[0][17:19])
    
    return time.mktime(year, month, day, hour, min, sec, 0, 0, -1)
    


#
#Class to connect to database
#
class connectDB:
  
  def __init__(self, db_name='', host='', user='', passwd=''):
    db = MySQLdb.connect(db = db_name , host = host, user = user , passwd = passwd)
    self.c = db.cursor()
    print 'Established connection: ' , db_name , host , user , passwd


#
#Class to read the wanted data
#
class queryDB(connectDB):
  
  def __init__(self , db_name='', host='', user='', passwd='' ):
    connectDB.__init__(self, db_name , host , user , passwd)
 
  def readData(self, fieldName='', tblName='', IDField = ''):
    read = "SELECT %s,%s from %s" %(fieldName,IDField,tblName) 
    self.c.execute(read)
    return self.c.fetchall()
   
  
  def update(self,tblName = '', fieldName = ''):
    dropData = "UPDATE %s SET %s= 0" %(tblName, fieldName) 
    self.c.execute(dropData)
    update = "ALTER TABLE %s CHANGE %s %s varchar(255) not null" %(tblName, fieldName, fieldName)
    self.c.execute(update)
     
  
  def change(self, tblName = '', fieldName = '', ID='',timestamp = ''):
    update = "UPDATE %s SET %s=%f WHERE ID=%i" %(tblName, fieldName,timestamp,ID)  
    self.c.execute(update)
    
  def toDouble(self,tblName='',fieldName=''):
    toDouble = "ALTER TABLE %s CHANGE %s %s double(16,6) not null" %(tblName,fieldName,fieldName)
    self.c.execute(toDouble)
    
  def showTables(self, dn_name):
    pass
