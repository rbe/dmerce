import sys
import os
import string
import DMS.SysTbl
import Guardian.Config
import DMS.SQL
import commands
import vars
import DMS.InitDmerce

from DMS.PySNMP import snmpcommands


class NCCSnmpMac:

    """ 1Ci - NCC - Get All MAC Addresses of the actuall server """

    def __init__(self):
        vars.vars()
        dmercecfg = DMS.InitDmerce.NCC()
        dmercecfg.InitNCC()
        self.__sqlData = dmercecfg.InitDBConnection()
        self.__instance = snmpcommands.SnmpNumeric('localhost','public') 
        self.__osname = os.uname()[0]
        self.__hostname = os.uname()[1]
        self.__dboid = DMS.SQL.DBOID(self.__sqlData, self.__sqlData)
        self.__index = []
        
    def GetMacAddresses(self):
        if self.__osname == 'Linux' or self.__osname == 'SunOS':
            a = string.split(commands.getstatusoutput(
                'snmpwalk -Ov localhost public .1.3.6.1.2.1.2.2.1.6')[1],'\012')
        if self.__osname == 'FreeBSD':
            a = string.split(commands.getstatusoutput(
                'snmpwalk -Ov localhost public .1.3.6.1.2.1.55.1.5.1.8')[1],'\012')

        #Index for InterfaceNames
        for i in range(len(a)):
            if string.count(a[i], ':') == 5:
                if i !=0 and a[i] != a[i-1]:
                    self.__index.append(i)
        b = []
        for j in range(len(self.__index)):
            if a[self.__index[j]] not in b:
                b.append(a[self.__index[j]])
        return b

    def GetIfNameOfMac(self):
        if self.__osname == 'Linux' or self.__osname == 'SunOS':
            data = self.__instance.walk(['.1.3.6.1.2.1.2.2.1.2'])
        if self.__osname == 'FreeBSD':
            data = self.__instance.walk(['.1.3.6.1.2.1.55.1.5.1.2'])
        a = []
        b = 0
        for i in range(len(data)):
            if i == self.__index[b]:
                a.append(data[i][1])
                if b < (len(self.__index)-1):
		   b = b + 1
        return a

    def GetServer(self,mac):
        """ Get a Server identifying by its MAC """
        servercount, server = self.__sqlData["SELECT i.ServerID,s.Name" +
                                             " FROM SrvServerInterfaces AS i, SrvServer AS s" +
                                             " WHERE i.IfPhysAddress = '%s' AND i.ServerID = s.ID"
                                             % mac]
        if not servercount:
            return {'ServerID': None, 'Name': None}
        else:
            return server[0]
       
    def GetSrvServer(self):
        """ Gets a Server identifying by its Hostname """
        servercount, server = self.__sqlData["SELECT ID AS ServerID,Name" +
                                             " FROM SrvServer WHERE Description = '%s'"
                                             % self.__hostname]
        if not servercount:
            return {'ServerID': None }
        else:
            return server[0]
            
    def GetServices(self,srvid):
        servercount, server = self.__sqlData["SELECT ServiceID FROM SrvServerSvcs" +
                                             "WHERE ServerID = '%s'" % srvid]
        return servercount, server

    def AddNewServer(self):
        newsid = self.__dboid['SrvServer']
        if newsid == 0:
            newsid = self.__dboid['SrvServer']
        self.__sqlData["INSERT INTO SrvServer (ID,active,new,Description) " +
                       "VALUES (%i,%i,%i,'%s')" % (newsid,0,1,self.__hostname)] 
        return newsid
    
    def AddMacAddress(self, serverid,data):
        newid = self.__dboid['SrvServerInterfaces']
        if newid == 0:
            newid = self.__dboid['SrvServerInterfaces']
        self.__sqlData["INSERT INTO SrvServerInterfaces" +
                       " (ID,active,new,ServerID,IfPhysAddress,IfName)" +
                       "VALUES (%i,%i,%i,%i,'%s','%s')" % (newid,0,1,serverid,data[0],data[1])]


