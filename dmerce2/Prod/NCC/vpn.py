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

    def GetVPN(self):
        query = 'SELECT ' + \
                'SrvVPN.ID, ' + \
                'SrvServer.Name, ' + \
                'SrvServer.Name, ' + \
                'SrvVPN.Name ' + \
                'FROM ' + \
                'SrvVPN, SrvServer ' + \
                'WHERE ' + \
                'SrvVPN.ServerID1 = SrvServer.ID AND ' + \
                'SrvVPN.ServerID2 = SrvServer.ID'
        self.c.execute(query)
        return self.c.fetchall()

    def GetVPNConfig(self, id):
        pass

class Ipsec:
    def __init__(self):
        pass
    
    def SetSourceIP(self, value):
        self.__sourceIP = value

    def SetDestinationIP(self, value):
        self.__destinationIP = value

    def SetDir(self, value):
        self.__dir = value

    def SetMode(self, value):
        self.__mode = value
        
    def __str__(self):
        print '%s %s' % (self.__sourceIP,
                         self.__destinationIP)

class Racoon:
    def __init__(self):
        pass

if __name__ == '__main__':
    db = DBData('MySQL:root:X1/yP7@localhost:dmerce_ncc')
    result = db.GetVPN()
    
