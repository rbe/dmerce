#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 1.1 $
# $Log: iso2Date2.py,v $
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
        year = int(iso_date[0][0:4])
        month = int(iso_date[0][5:7])
        day = int(iso_date[0][8:10])
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

    def checkPresents(self, table, field):
        query = 'SELECT %s from %s' %(field, table)
        try:
            self.c.execute(query)
            print 'Check result of table', table , ' --> ' ,self.c.execute(query)
            if len(self.c.execute(query)) > 0:
                value =  1 # data in table
            else:
                value =  2 # table exists but no data inside

        except:
            print 'No such field ', field, ' in table ', table
            value = None
        return value
            
    def readData(self, fieldName, tblName, IDField):
        read = "SELECT %s, %s from %s" %(fieldName, IDField, tblName)
        try:
            self.c.execute(read)
            value = self.c.fetchall()
        except:
            value =  None
        return value

    def Null(self, table, field):
        query = 'ALTER TABLE %s CHANGE %s %s datetime not null' %(table, field, field)
        try:
            self.c.execute(query)            
        except:
            print 'No such field ', field, '  in table :' , table
        
    def update(self,tblName = '', fieldName = ''):
        update = "ALTER TABLE %s DROP %s " %(tblName,  fieldName)
        
        try:
            self.c.execute(update)
        except:
            print 'Error while dropping field ' , fieldName, ' from table ', tblName
            
        create = 'ALTER TABLE %s ADD %s double(16,6) not null AFTER ID' %(tblName, fieldName)
        try:
            self.c.execute(create)
        except:
            print 'Error while creating new field with name: ', fieldName ,' in table ', tblName
            
    def change(self, tblName = '', fieldName = '', ID='',timestamp = ''):
        update = "UPDATE %s SET %s=%f WHERE ID=%i" %(tblName, fieldName,timestamp,ID)
        try:
            self.c.execute(update)
        except:
            print 'Error while putting data timestamp = ', timestamp, ' into table ' , tblName
    
    def toDouble(self,tblName='',fieldName=''):
        toDouble = "ALTER TABLE %s CHANGE %s %s double(16,6) not null" %(tblName,fieldName,fieldName)
        self.c.execute(toDouble)
    
    def showTables(self):
        query = "SHOW TABLES"
        self.c.execute(query)
        return self.c.fetchall()
