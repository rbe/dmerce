import sys
import string

class UcdSnmpDsk:

    """ a single disk device """
    
    def __init__(self):
        self.__osName = '' 
        self.__srvID = 0
        self.__mntPoint = ''
        self.__device = ''
        self.__dskMin = 0
        self.__dskMinPercent = 0
        self.__dskTotal = 0
        self.__dskAvail = 0
        self.__dskUsed = 0
        self.__dskPercent = 0
        self.__dskPercentNode = 0
        self.__dskErrFlag = 0
        self.__dskErrMsg = ''

    
    def SetOsName(self, o):
        self.__osName = o

    def GetOsName(self):
        return self.__osName

    def SetSrvID(self, i):
        self.__srvID = i

    def GetSrvID(self):
        return self.__srvID

    def SetMntPoint(self, w):
        self.__mntPoint = w

    def GetMntPoint(self):
        return self.__mntPoint

    def SetDevice(self, d):
        self.__device = d

    def GetDevice(self):
        return self.__device

    def SetDskMin(self, m):
        self.__dskMin = m

    def GetDskMin(self):
        return self.__dskMin

    def SetDskMinPercent(self, m):
        self.__dskMinPercent = m

    def GetDskMinPercent(self):
        return self.__dskMinPercent

    def SetDskTotal(self, t):
        self.__dskTotal = t

    def GetDskTotal(self):
        return self.__dskTotal

    def SetDskAvail(self, a):
        self.__dskAvail = a
        
    def GetDskAvail(self):
        return self.__dskAvail
    
    def SetDskUsed(self, u):
        self.__dskUsed = u

    def GetDskUsed(self):
        return self.__dskUsed
    
    def SetDskPercent(self, p):
        self.__dskPercent = p

    def GetDskPercent(self):
        return self.__dskPercent
    
    def SetDskPercentNode(self, n):
        self.__dskPercentNode = n

    def GetDskPercentNode(self):
        return self.__dskPercentNode    

    def SetDskErrFlag(self, f):
        self.__dskErrFlag = f

    def GetDskErrFlag(self):
        return self.__dskErrFlag
    
    def SetDskErrMsg(self, m):
        self.__dskErrMsg = m

    def GetDskErrMsg(self):
        return self.__dskErrMsg

    def AppendData(self, s):
        self.__data.append(s)

    def GetData(self):
        if self.GetOsName() == 'FreeBSD':
            return self.GetDataFreeBSD()
        if self.GetOsName() == 'Debian GNU/Linux':
            return self.GetDataLinux()

    def GetDataFreeBSD(self):
        self.__data = []
        self.AppendData(self.GetOsName())
        self.AppendData(self.GetSrvID())
        self.AppendData(self.GetMntPoint())
        self.AppendData(self.GetDevice())
        self.AppendData(self.GetDskMin())
        self.AppendData(self.GetDskMinPercent())
        self.AppendData(self.GetDskTotal())
        self.AppendData(self.GetDskAvail())
        self.AppendData(self.GetDskUsed())
        self.AppendData(self.GetDskPercent())
        self.AppendData(self.GetDskErrFlag())
        self.AppendData(self.GetDskErrMsg())
        return self.__data

    def GetDataLinux(self):
        self.__data = []
        self.AppendData(self.GetOsName())
        self.AppendData(self.GetSrvID())
        self.AppendData(self.GetMntPoint())
        self.AppendData(self.GetDevice())
        self.AppendData(self.GetDskMin())
        self.AppendData(self.GetDskMinPercent())
        self.AppendData(self.GetDskTotal())
        self.AppendData(self.GetDskAvail())
        self.AppendData(self.GetDskUsed())
        self.AppendData(self.GetDskPercent())
        self.AppendData(self.GetDskPercentNode())
        self.AppendData(self.GetDskErrFlag())
        self.AppendData(self.GetDskErrMsg())
        return self.__data
    
class UcdSnmpProc:

    """ A single proc entry """
    
    def __init__(self):
        self.__srvID = 0        
        self.__prNames = ''
        self.__prMin = 0
        self.__prMax = 0
        self.__prCount = 0
        self.__prErrorFlag = 0
        self.__prErrMessage = ''
        self.__prErrFix = 0
        self.__prErrFixCmd = ''

    def SetSrvID(self, i):
        self.__srvID = i

    def GetSrvID(self):
        return self.__srvID

    def SetPrNames(self,n):
        self.__prNames = n

    def GetPrNames(self):
        return self.__prNames

    def SetPrMin(self,m):
        self.__prMin = m

    def GetPrMin(self):
        return self.__prMin

    def SetPrMax(self,m):
        self.__prMax = m

    def GetPrMax(self):
        return self.__prMax

    def SetPrCount(self,c):
        self.__prCount = c

    def GetPrCount(self):
        return self.__prCount

    def SetPrErrFlag(self,f):
        self.__prErrFlag = f

    def GetPrErrFlag(self):
        return self.__prErrFlag

    def SetPrErrMessage(self,m):
        self.__prErrMessage = m

    def GetPrErrMessage(self):
        return self.__prErrMessage

    def SetPrErrFix(self,f):
        self.__prErrFix = f

    def GetPrErrFix(self):
        return self.__prErrFix
    
    def SetPrErrFixCmd(self,c):
        self.__prErrFixCmd = c

    def GetPrErrFixCmd(self):
        return self.__prErrFixCmd

    def AppendData(self, s):
        self.__data.append(s)

    def GetData(self):
        self.__data = []
        self.AppendData(self.GetSrvID())
        self.AppendData(self.GetPrNames())
        self.AppendData(self.GetPrMin())
        self.AppendData(self.GetPrMax())
        self.AppendData(self.GetPrCount())
        self.AppendData(self.GetPrErrFlag())
        self.AppendData(self.GetPrErrMessage())
        self.AppendData(self.GetPrErrFix())
        self.AppendData(self.GetPrErrFixCmd())
        return self.__data
    
class UcdSnmpInterfaces:

    """ a single interface """

    def __init__(self):
        self.__osName = '' 
        self.__srvID = 0    
        self.__ifDescr = ''
        self.__ifType = ''
        self.__ifMtu = 0
        self.__ifSpeed = 0
        self.__ifPhysAddress = ''
        self.__ifAdminStatus = ''
        self.__ifOperStatus = ''
        self.__ifLastChange = ''
        self.__ifInOctets = 0
        self.__ifInUcastPkts = 0
        self.__ifInNUcastPkts = 0
        self.__ifInDiscards = 0
        self.__ifInErrors = 0
        self.__ifOutOctets = 0
        self.__ifOutUcastPkts = 0
        self.__ifOutNUcastPkts = 0
        self.__ifOutDiscards = 0
        self.__ifOutErrors = 0

    def SetOsName(self, o):
        self.__osName = o

    def GetOsName(self):
        return self.__osName

    def SetSrvID(self, i):
        self.__srvID = i

    def GetSrvID(self):
        return self.__srvID

    def SetIfDescr(self,d):
        self.__ifDescr = d

    def GetIfDescr(self):
        return self.__ifDescr

    def SetIfType(self,t):
        self.__ifType = t

    def GetIfType(self):
        return self.__ifType

    def SetIfMtu(self,m):
        self.__ifMtu = m

    def GetIfMtu(self):
        return self.__ifMtu

    def SetIfSpeed(self,s):
        self.__ifSpeed = s

    def GetIfSpeed(self):
        return self.__ifSpeed

    def SetIfPhysAddress(self,a):
        self.__ifPhysAddress = a

    def GetIfPhysAddress(self):
        return self.__ifPhysAddress

    def SetIfAdminStatus(self,a):
        self.__ifAdminStatus = a
        
    def GetIfAdminStatus(self):
        return self.__ifAdminStatus

    def SetIfOperStatus(self,o):
        self.__ifOperStatus = o

    def GetIfOperStatus(self):
        return self.__ifOperStatus

    def SetIfLastChange(self,l):
        self.__ifLastChange = l

    def GetIfLastChange(self):
        return self.__ifLastChange
    
    def SetIfInOctets(self,o):
        self.__ifInOctets = o

    def GetIfInOctets(self):
        return self.__ifInOctets

    def SetIfInUcastPkts(self,u):
        self.__ifInUcastPkts = u

    def GetIfInUcastPkts(self):
        return self.__ifInUcastPkts

    def SetIfInNUcastPkts(self,n):
        self.__ifInNUcastPkts = n

    def GetIfInNUcastPkts(self):
        return self.__ifInNUcastPkts

    def SetIfInDiscards(self,d):
        self.__ifInDiscards = d

    def GetIfInDiscards(self):
        return self.__ifInDiscards

    def SetIfInErrors(self,e):
        self.__ifInErrors = e

    def GetIfInErrors(self):
        return self.__ifInErrors

    def SetIfOutOctets(self,o):
        self.__ifOutOctets = o

    def GetIfOutOctets(self):
        return self.__ifOutOctets

    def SetIfOutUcastPkts(self,u):
        self.__ifOutUcastPkts = u

    def GetIfOutUcastPkts(self):
        return self.__ifOutUcastPkts

    def SetIfOutNUcastPkts(self,n):
        self.__ifOutNUcastPkts = n

    def GetIfOutNUcastPkts(self):
        return self.__ifOutNUcastPkts

    def SetIfOutDiscards(self,d):
        self.__ifOutDiscards = d

    def GetIfOutDiscards(self):
        return self.__ifOutDiscards

    def SetIfOutErrors(self,e):
        self.__ifOutErrors = e

    def GetIfOutErrors(self):
        return self.__ifOutErrors

    def AppendData(self, s):
        self.__data.append(s)

    def GetData(self):
        if self.GetOsName() == 'FreeBSD':
            return self.GetDataFreeBSD()
        if self.GetOsName() == 'Debian GNU/Linux':
            return self.GetDataLinux()

    def GetDataFreeBSD(self):
        self.__data = []
        self.AppendData(self.GetOsName())
        self.AppendData(self.GetSrvID())
        self.AppendData(self.GetIfDescr())
        self.AppendData(self.GetIfType())
        self.AppendData(self.GetIfMtu())
        self.AppendData(self.GetIfSpeed())
        self.AppendData(self.GetIfAdminStatus())
        self.AppendData(self.GetIfOperStatus())                
        self.AppendData(self.GetIfLastChange())
        self.AppendData(self.GetIfInOctets())
        self.AppendData(self.GetIfInUcastPkts())
        self.AppendData(self.GetIfInNUcastPkts())
        self.AppendData(self.GetIfInDiscards())
        self.AppendData(self.GetIfInErrors())
        self.AppendData(self.GetIfOutOctets())
        self.AppendData(self.GetIfOutUcastPkts())
        self.AppendData(self.GetIfOutNUcastPkts())
        #FreeBSD has no OutDiscards
        self.AppendData(self.GetIfOutErrors())
        return self.__data
        
    def GetDataLinux(self):
        self.__data = []
        self.AppendData(self.GetOsName())
        self.AppendData(self.GetSrvID())
        self.AppendData(self.GetIfDescr())
        self.AppendData(self.GetIfType())
        self.AppendData(self.GetIfMtu())
        self.AppendData(self.GetIfSpeed())
        self.AppendData(self.GetIfAdminStatus())
        self.AppendData(self.GetIfOperStatus())                
        self.AppendData(self.GetIfInOctets())
        self.AppendData(self.GetIfInUcastPkts())
        #Linux has no InNUcastPkts
        #Linux has no InDiscards
        self.AppendData(self.GetIfInErrors())
        self.AppendData(self.GetIfOutOctets())
        self.AppendData(self.GetIfOutUcastPkts())
        #Linux has no OutNUcastPkts
        self.AppendData(self.GetIfOutDiscards())
        self.AppendData(self.GetIfOutErrors())
        return self.__data

class UcdSnmp:

    """ a set of data """

    def __init__(self):
        self.__entries = []

    def AddEntry(self, entry):
        self.__entries.append(entry)

    def Get(self):
        return self.__entries


