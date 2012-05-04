
import sys
import string
import time
import commands
import DMS.SysTbl
import Guardian.Config
import DMS.SQL
import Snmplog
from DMS.PySNMP import snmpcommands

class NCCServerDsk:

    """ 1Ci - NCC - Server Disks """

    def __init__(self):
        self.__ucdDsk = Snmplog.UcdSnmp()
        self.__time = time.time()
        sysTbl = DMS.SysTbl.Retrieve()
        sysTbl.SetConfig(Guardian.Config.RFC822('/usr/local/1Ci/dmerce/conf'))
        sysTbl.SetFQHN('www.ncc.1ci.de')
        sysTbl.InitSQLSysConn()
        sysTbl.SetSysConfig()
        sysTbl.InitSQLDataConn()
        self.__sqlSys = sysTbl.GetSQLSys()
        self.__sqlData = sysTbl.GetSQLData()
        self.__dboid = DMS.SQL.DBOID(self.__sqlSys,self.__sqlData)

    def GetServer(self):
        """ fetches rules todo from database """
        servercount, server = self.__sqlData["SELECT s.ID, i.IPv4, o.Name" +
                                             " FROM SrvServer AS s,SrvServerInterfacesIP AS i," +
                                             " SrvServerInterfaces AS n, Os AS o" +
                                             " WHERE s.active=1 AND s.new=0 AND i.Management=1" +
                                             " AND o.ID=s.OsID AND i.InterfaceID=n.ID" +
                                             " AND s.ID=n.ServerID"]
        return servercount, server
    
    def SnmpConnect(self,server,community):
        self.__instance = snmpcommands.SnmpNumeric(server,community)
        
    def SnmpGetDataIndex(self, objid):
        self.__index = self.__instance.walk([objid])
        
    def SnmpGetData(self,objid):
        self.__data = self.__instance.walk([objid])

    def MakeEntry(self,osname,serverid):
        step = len(self.__index)
        for i in range(len(self.__index)):
            if osname == 'FreeBSD':
                self.MakeEntryFreeBSD(i,step,serverid)
            if osname == 'Debian GNU/Linux':
                self.MakeEntryLinux(i,step,serverid)
                
    def MakeEntryFreeBSD(self,i,step,serverid):
        r = Snmplog.UcdSnmpDsk()
        r.SetOsName('FreeBSD')
        r.SetSrvID(serverid)
        r.SetMntPoint(self.__data[i+step][1])
        r.SetDevice(self.__data[i+2*step][1])
        r.SetDskMin(self.__data[i+3*step][1])
        r.SetDskMinPercent(self.__data[i+4*step][1])
        r.SetDskTotal(self.__data[i+5*step][1])
        r.SetDskAvail(self.__data[i+6*step][1])
        r.SetDskUsed(self.__data[i+7*step][1])
        r.SetDskPercent(self.__data[i+8*step][1])
        r.SetDskErrFlag(self.__data[i+9*step][1])
        r.SetDskErrMsg(self.__data[i+10*step][1])
        self.__ucdDsk.AddEntry(r.GetData())

    def MakeEntryLinux(self,i,step,serverid):
        r = Snmplog.UcdSnmpDsk()
        r.SetOsName('Debian GNU/Linux')
        r.SetSrvID(serverid)
        r.SetMntPoint(self.__data[i+step][1])
        r.SetDevice(self.__data[i+2*step][1])
        r.SetDskMin(self.__data[i+3*step][1])
        r.SetDskMinPercent(self.__data[i+4*step][1])
        r.SetDskTotal(self.__data[i+5*step][1])
        r.SetDskAvail(self.__data[i+6*step][1])
        r.SetDskUsed(self.__data[i+7*step][1])
        r.SetDskPercent(self.__data[i+8*step][1])
        r.SetDskPercentNode(self.__data[i+9*step][1])
        r.SetDskErrFlag(self.__data[i+10*step][1])
        r.SetDskErrMsg(self.__data[i+11*step][1])
        self.__ucdDsk.AddEntry(r.GetData())

    def AddEntries(self):
        entries = self.__ucdDsk.Get()
        for i in range(len(entries)):
            if entries[i][0] == 'FreeBSD':
                self.AddEntriesFreeBSD(entries[i])
            if entries[i][0] == 'Debian GNU/Linux':
                self.AddEntriesLinux(entries[i])
            
    def AddEntriesFreeBSD(self,entries):
        self.__newid = self.__dboid['SrvServerWatchDisk']        
        rc,r = self.__sqlData["INSERT INTO SrvServerWatchDisk" +
                              " (ID,CreatedDateTime,ServerId,DskPath,DskDevice,DskMinimum," +
                              " DskMinPercent,DskTotal,DskAvail,DskUsed,DskPercent," +
                              " DskErrorFlag,DskErrorMsg)" +
                              " VALUES (%i,%i,%i,'%s','%s',%i,%i,%i,%i,%i,%i,%i,'%s')"
                              % (self.__newid,self.__time,entries[1],entries[2],entries[3],
                                 entries[4],entries[5],entries[6],entries[7],entries[8],
                                 entries[9],entries[10],entries[11])]

    def AddEntriesLinux(self,entries):
        self.__newid = self.__dboid['SrvServerWatchDisk']
        rc,r = self.__sqlData["INSERT INTO SrvServerWatchDisk" +
                              " (ID,CreatedDateTime,ServerId,DskPath,DskDevice,DskMinimum," +
                              " DskMinPercent,DskTotal,DskAvail,DskUsed,DskPercent," +
                              " DskPercentNode,DskErrorFlag,DskErrorMsg)" +
                              " VALUES (%i,%i,%i,'%s','%s',%i,%i,%i,%i,%i,%i,%i,%i,'%s')"
                              % (self.__newid,self.__time,entries[1],entries[2],entries[3],
                                 entries[4],entries[5],entries[6],entries[7],entries[8],
                                 entries[9],entries[10],entries[11],entries[12])]
    
class NCCServerProc:

    """ 1Ci - NCC - Server Processes """

    def __init__(self):
        self.__ucdProc = Snmplog.UcdSnmp()
        self.__time = time.time()
        sysTbl = DMS.SysTbl.Retrieve('www.ncc.1ci.de',
                                     Guardian.Config.RFC822('/usr/local/1Ci/dmerce/conf'))
        sysTbl.Init()
        self.__sqlSys = sysTbl.GetSQLSys()
        self.__sqlData = sysTbl.GetSQLData()
        self.__dboid = DMS.SQL.DBOID(self.__sqlSys,self.__sqlData)     

    def GetServer(self):
        """ fetches rules todo from database """
        servercount, server = self.__sqlData["SELECT s.ID,i.IPv4 FROM SrvServer AS s," +
                                             " SrvServerInterfaces AS n," +
                                             " SrvServerInterfacesIP AS i" +
                                             " WHERE s.active=1 AND s.new=0 AND i.Management=1" +
                                             " AND i.InterfaceID=n.ID AND s.ID=n.ServerID"]
        return servercount, server

    def SnmpConnect(self,server,community):
        self.__instance = snmpcommands.SnmpNumeric(server, community)

    def SnmpGetDataIndex(self, objid):
        self.__index = self.__instance.walk([objid])

    def SnmpGetData(self,objid):
        self.__data = self.__instance.walk([objid])

    def MakeEntry(self,serverid):
        step = len(self.__index)
        for i in range(len(self.__index)):
            a = Snmplog.UcdSnmpProc()
            a.SetSrvID(serverid)
            a.SetPrNames(self.__data[i+step][1])
            a.SetPrMin(self.__data[i+2*step][1])
            a.SetPrMax(self.__data[i+3*step][1])
            a.SetPrCount(self.__data[i+4*step][1])
            a.SetPrErrFlag(self.__data[i+5*step][1])
            a.SetPrErrMessage(self.__data[i+6*step][1])
            a.SetPrErrFix(self.__data[i+7*step][1])
            a.SetPrErrFixCmd(self.__data[i+8*step][1])
            self.__ucdProc.AddEntry(a.GetData())

    def AddEntries(self):
        entries = self.__ucdProc.Get()
        for i in range(len(entries)):
            self.__newid = self.__dboid['SrvServerWatchProc']
            rc,r =self.__sqlData["INSERT INTO SrvServerWatchProc" +
                                 " (ID,CreatedDateTime,ServerID,PrNames,PrMin,PrMax,PrCount," +
                                 " PrErrFlag,PrErrMessage,PrErrFix,PrErrFixCmd)" +
                                 " VALUES (%i,%i,%i,'%s',%i,%i,%i,%i,'%s',%i,'%s')"
                                 % (self.__newid,self.__time,entries[i][0],entries[i][1],
                                    entries[i][2],entries[i][3],entries[i][4],entries[i][5],
                                    entries[i][6],entries[i][7],entries[i][8])]

class NCCServerInterfaces:

    """ 1Ci - NCC - Server Interfaces """

    def __init__(self):
        self.__ucdInterfaces = Snmplog.UcdSnmp()
        self.__time = time.time()
        sysTbl = DMS.SysTbl.Retrieve('www.ncc.1ci.de',
                                     Guardian.Config.RFC822('/usr/local/1Ci/dmerce/conf'))
        sysTbl.Init()
        self.__sqlSys = sysTbl.GetSQLSys()
        self.__sqlData = sysTbl.GetSQLData()
        self.__dboid = DMS.SQL.DBOID(self.__sqlSys,self.__sqlData)     

    def GetServer(self):
        """ fetches rules todo from database """
        servercount, server = self.__sqlData["SELECT s.ID,i.IPv4,o.Name FROM SrvServer AS s," +
                                             " Os AS o,SrvServerInterfaces AS n," +
                                             " SrvServerInterfacesIP AS i"+
                                             " WHERE s.active=1 AND s.new=0 AND i.Management=1" +
                                             " AND o.ID=s.OsID AND i.InterfaceID=n.ID" +
                                             " AND s.ID=n.ServerID"]
        return servercount, server

    def SnmpConnect(self,server,community):
        self.__instance = snmpcommands.SnmpNumeric(server,community)

    def SnmpGetDataIndex(self, objid):
        self.__index = self.__instance.walk([objid])
        
    def SnmpGetData(self,objid):
        self.__data = self.__instance.walk([objid])

    def MakeEntry(self,osname,serverid):
        step = len(self.__index)
        for i in range(len(self.__index)):
            if osname == 'FreeBSD':
                self.MakeEntryFreeBSD(i,step,serverid)
            if osname == 'Debian GNU/Linux':
                self.MakeEntryLinux(i,step,serverid)   

    def MakeEntryFreeBSD(self,i,step,serverid):
        b = Snmplog.UcdSnmpInterfaces()
        b.SetOsName('FreeBSD')
        b.SetSrvID(serverid)
        b.SetIfDescr(self.__data[i+step][1])
        b.SetIfType(self.__data[i+2*step][1])
        b.SetIfMtu(self.__data[i+3*step][1])
        b.SetIfSpeed(self.__data[i+4*step][1])
        b.SetIfAdminStatus(self.__data[i+5*step][1])
        b.SetIfOperStatus(self.__data[i+6*step][1])
        b.SetIfLastChange(self.__data[i+7*step][1])
        b.SetIfInOctets(self.__data[i+8*step][1])
        b.SetIfInUcastPkts(self.__data[i+9*step][1])
        b.SetIfInNUcastPkts(self.__data[i+10*step][1])
        b.SetIfInDiscards(self.__data[i+11*step][1])
        b.SetIfInErrors(self.__data[i+12*step][1])
        b.SetIfOutOctets(self.__data[i+14*step][1])
        b.SetIfOutUcastPkts(self.__data[i+15*step][1])
        b.SetIfOutNUcastPkts(self.__data[i+16*step][1])
        b.SetIfOutErrors(self.__data[i+17*step][1])
        self.__ucdInterfaces.AddEntry(b.GetData())
        
    def MakeEntryLinux(self,i,step,serverid):
        b = Snmplog.UcdSnmpInterfaces()
        b.SetOsName('Debian GNU/Linux')
        b.SetSrvID(serverid) 
        b.SetIfDescr(self.__data[i+step][1])
        b.SetIfType(self.__data[i+2*step][1])
        b.SetIfMtu(self.__data[i+3*step][1])
        b.SetIfSpeed(self.__data[i+4*step][1])
        b.SetIfAdminStatus(self.__data[i+6*step][1])
        b.SetIfOperStatus(self.__data[i+7*step][1])
        b.SetIfInOctets(self.__data[i+8*step][1])
        b.SetIfInUcastPkts(self.__data[i+9*step][1])
        b.SetIfInErrors(self.__data[i+10*step][1])
        b.SetIfOutOctets(self.__data[i+11*step][1])
        b.SetIfOutUcastPkts(self.__data[i+12*step][1])
        b.SetIfOutDiscards(self.__data[i+13*step][1])
        b.SetIfOutErrors(self.__data[i+14*step][1])
        self.__ucdInterfaces.AddEntry(b.GetData())

    def AddEntries(self):
        entries = self.__ucdInterfaces.Get()
        for i in range(len(entries)):
            if entries[i][0] == 'FreeBSD':
                self.AddEntriesFreeBSD(entries[i])
            if entries[i][0] == 'Debian GNU/Linux':
                self.AddEntriesLinux(entries[i])
            
    def AddEntriesFreeBSD(self,entries):
        self.__newid = self.__dboid['SrvServerWatchInterface']        
        rc,r = self.__sqlData["INSERT INTO SrvServerWatchInterface" +
                              " (ID,CreatedDateTime,ServerID,IfDescr,IfType,IfMtu,IfSpeed," +
                              " IfAdminStatus,IfOperStatus,IfLastChange,IfInOctets," +
                              " IfInUcastPkts,IfInNUcastPkts,IfInDiscards,IfInErrors," +
                              " IfOutOctets,IfOutUcastPkts,IfOutNUcastPkts,IfOutErrors)" +
                              " VALUES" +
                              "(%i,%i,%i,'%s',%i,%i,%i,%i,%i,%i,'%s','%s','%s',%i,%i,'%s','%s','%s',%i)"
                              % (self.__newid,self.__time,entries[1],entries[2],entries[3],
                                 entries[4],entries[5],entries[6],entries[7],entries[8],
                                 entries[9],entries[10],entries[11],entries[12],entries[13],
                                 entries[14],entries[15],entries[16],entries[17])]
                                 
    def AddEntriesLinux(self,entries):
        self.__newid = self.__dboid['SrvServerWatchInterface']
        rc,r = self.__sqlData["INSERT INTO SrvServerWatchInterface" +
                              " (ID,CreatedDateTime,ServerID,IfDescr,IfType,IfMtu,IfSpeed," +
                              " IfAdminStatus,IfOperStatus,IfInOctets,IfInUcastPkts,IfInErrors," +
                              " IfOutOctets,IfOutUcastPkts,IfOutDiscards,IfOutErrors)" +
                              " VALUES (%i,%i,%i,'%s',%i,%i,%i,%i,%i,'%s','%s',%i,'%s','%s',%i,%i)"
                              % (self.__newid,self.__time,entries[1],entries[2],entries[3],
                                 entries[4],entries[5],entries[6],entries[7],entries[8],
                                 entries[9],entries[10],entries[11],entries[12],entries[13],
                                 entries[14])]
        
