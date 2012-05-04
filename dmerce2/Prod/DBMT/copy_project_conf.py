#! /usr/local/env python
#


""" Import recent Modules"""
try:
    import sys
    import os
    import crypt
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
    """ DB Handling: get macro data """
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

    def GetFQHNData(self, fqhn, table):
        query = 'SELECT * FROM %s WHERE FQHN="%s"' % (table, fqhn)
        self.c.execute(query)
        return self.c.fetchall()

    def GetMaxID(self, table):
        query = 'SELECT max(ID) FROM %s' % table
        self.c.execute(query)
        maxID = self.c.fetchall()
        return int(maxID)

    def SetFQHN(self, fqhn, table, maxID, data):
        query = 'INSERT INTO %s VALUES (%i, data)'
        self.c.execute(query)

if __name__ == '__main__':
    tables = ('Templates', 'UXSDuties', 'UXSGroups', 'UXSRights', 'UXSUsers')
    fqhn = ('www.demo2.1ci.de')
    for i in range(len(fqhn)):
        for j in range(len(tables)):
            db = DBData('MySQL:root:X1/yP7@localhost:dmerce_sys')
            data = db.GetFQHNData(fqhn[i], tables[j])
            print data
        
