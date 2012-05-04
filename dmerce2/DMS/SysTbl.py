#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.36 $'
#
##

import string
import Core.Error
import Core.Log
import DMS.Cache
import DMS.SQL

class Configuration(DMS.Cache.Cache):

    """ a cache holding information about project configuration """
    def __init__(self):
        DMS.Cache.Cache.__init__(self, ci = 1)

class Retrieve:

    """
    retrieve configuration for FQHN from dmerce_sys
    and make it available
    """

    def __init__(self, fqhn = None, config = None):
        self.__fqhn = fqhn
        self.__config = config
        self.__log = Core.Log.File(debug = 1, module = '1[SysTbl].Retrieve')

    def SetFQHN(self, f):
        """ set fully qualified host name """
        self.__fqhn = f

    def SetConfig(self, c):
        """ set config parser; Guardian.Config.RFC822 """
        self.__config = c

    def Init(self):
        self.InitSQLDataConn()
        self.InitSQLSysConn()
        self.SetSysConfig()

    def InitSQLSysConn(self):
        """ initialize SQL connection to system database """
        sc = self.__config.Get('q', 'Sys')
        sc2 = string.split(sc, ':')
        sc = string.join(sc2[:-1], ':') + ':dmerce_sys'
        if string.upper(sc2[0]) == 'POSTGRESQL':
            #self.__log.Write(msg = 'CONNECTING TO SYSTEM DATABASE=%s.' % sc)
            self.__sqlSysConn = DMS.SQL.Layer1(sc)
            self.__sqlSysConn.Init()
            self.__sqlSys = self.__sqlSysConn.GetQuery()            
            #self.__log.Write(msg = 'CONNECTION OK. CREATED QUERY OBJECT.')
        else:
            self.__sqlSys = self.__sqlData

    def InitSQLDataConn(self):
        """ set sql data connection """
        sc = self.__config.Get('q', 'Sys')
        #self.__log.Write(msg = 'CONNECTING TO DATABASE=%s.' % sc)
        self.__sqlDataConn = DMS.SQL.Layer1(sc)
        self.__sqlDataConn.Init()
        self.__sqlData = self.__sqlDataConn.GetQuery()
        # self.__sqlSys["ALTER SESSION SET SQL_TRACE = TRUE"]
        #self.__log.Write(msg = 'CONNECTION OK. CREATED QUERY OBJECT.')

    def SetSysConfig(self):
        """ set system variables and sql system connection """
        table = 'Configuration'
        if self.__sqlSys.GetType() != 'POSTGRESQL':
            table = 'dmerce_sys.' + table
        stmt = "SELECT Debug, HostSerial, FileUploadDir, OwnerConnectString," \
               "       dmerceSysConnectString, SamInactiveTimeout, UxsRolePrefix" \
               "  FROM %s" \
               " WHERE FQHN = '%s'" % (table, self.__fqhn)
        try:
            rc, r = self.__sqlSys[stmt]
            #self.__log.Write(msg = 'RETRIEVED CONFIGURATION FROM SYSTEM DATABASE: %s=%s' % (stmt, str(r[0])))
            self.__configuration = Configuration()
            for k in r[0].keys():
                self.__configuration[k] = r[0][k]
        except IndexError:
            raise Core.Error.ConfigurationError(1, 'NO CONFIGURATION FOUND FOR %s' % self.__fqhn)

    def DeInit(self):
        """ deinit """
        try:
            self.__sqlSysConn.Close()
            self.__log.Write(msg = 'CLOSED DATABASE CONNECTION TO dmerce_sys')
        except:
            pass
        self.__sqlDataConn.Close()
        self.__log.Write(msg = 'CLOSED DATABASE CONNECTION TO PROJECT DATABASE')

    def GetConfiguration(self):
        """ return result row of query to dmerce_sys aka the configuration """
        return self.__configuration

    def GetCfgKey(self, k):
        return self.__configuration[k]

    def GetSQLSys(self):
        """ return sql query instance to dmerce_sys """
        return self.__sqlSys

    def GetSQLData(self):
        """ return sql query instance to database for this project """
        return self.__sqlData
