#!/usr/local/env python
#

"""
Import recent Modules
"""
import sys
try:
    import DMS.SysTbl
    import Guardian.Config
    import DMS.SQL
    import os
    import string
    import MySQLdb
    import DMS.SnmpMac
    import DMS.InitDmerce
    import DMS.Lock
except:
    print 'Errors occured while importing modules !'
    sys.exit()

class Servers:
    def __init__(self, query):
        """ constructor sets db instance and query instance  """
        self.__query = query
        
    def GetAll(self):
        """
        snmp works not on every server, we have
        to get data out of db filled in manually
        """
        rc, r = self.__query["SELECT SrvServer.ID AS ServerID, SrvServer.Name AS ServerName, " + \
                             "SrvServerInterfaces.IfPhysAddress AS MacAddress " + \
                             "FROM SrvServer, SrvServerInterfaces " + \
                             "WHERE SrvServer.ID = SrvServerInterfaces.ServerID " + \
                             "AND SrvServer.active = 1 AND SrvServer.new = 0 " + \
                             "AND SrvServerInterfaces.active = 1 " + \
                             "AND SrvServerInterfaces.new = 0"]
        return r
