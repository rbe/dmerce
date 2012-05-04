#!/usr/local/env python
#

""" Import recent Modules """
try:
    import sys
    import os
    import string
    import MySQLdb
    import getopt
except:
    print 'Errors occured while importing modules !'


class ConnectDB:
    """ Class to make an connection to database """
    def __init__(self,db = '', host = '', user = '', passwd = ''):
        """ Constructor """
        db = MySQLdb.connect(db = db, host = host, user = user, passwd = passwd) 
        self.c = db.cursor()

class DBData(ConnectDB):
    """ DB Handling : Get user and group data """
    def __init__(self, connectStr):
        """ Constructor """
        self.__connectStr = connectStr
        self.AnalyseConnectStr()
        ConnectDB.__init__(self, self.__db, self.__host, self.__user, self.__passwd)

    def AnalyseConnectStr(self):
        """ Analyse connect string """
        try:
            """ analyse connect string (database type:user:password@host:database name) """
            a, b = string.split(self.__connectStr, '@')
            self.__dbType, self.__user, self.__passwd = string.split(a, ':')
            self.__host, self.__db = string.split(b, ':')
        except:
            print 'Error in connection string: ' , self.__connectStr

    def CreateDefault(self, table):
        query = 'CREATE TABLE %s '% table + \
                '(ID INT(11) NOT NULL,' + \
                'CreatedDateTime DOUBLE(16,6) NOT NULL,' + \
                'CreatedBy INT(11) NOT NULL,' + \
                'ChangedDateTime DOUBLE(16,6) NOT NULL,' + \
                'ChangedBy INT(11) NOT NULL)'
        self.c.execute(query)
        
class VPN:
    def __init__(self):
        pass

if __name__ == '__main__':
    dbName = 'dmerce_ncc'
    tableToCreate = 'SrvVPNConfig'
    db = DBData('MySQL:root:X1/yP7@localhost:' + dbName)
    db.CreateDefault(tableToCreate)
