#!/opt/sfw/bin/python

import types
import operator
import string
#import DS.SQL
import sys

class Ip4Mask:

    """ convert CIDR <-> Netmasks """
    
    def __init__(self, w = '0.0.0.0'):
        self.Set(w)

    def Set(self, w):
        if type(w) is types.IntType:
            self.__cidr = w
            self.__netmask = self.Cidr2Netmask()
        elif type(w) is types.StringType:
            self.__netmask = w
            self.__cidr = self.Netmask2Cidr()

    def SetCidr(self, c):
        self.__cidr = c
        self.__netmask = self.Cidr2Netmask()

    def SetNetmask(self, n):
        self.__netmask = n
        self.__cidr = self.Netmask2Cidr()
        
    def GetCidr(self):
        return self.__cidr

    def GetNetmask(self):
        return self.__netmask

    def __Cidr2Netmask(self, cidr):
        """ convert one byte of CIDR to netmask value """
        number = 0
        for i in range(cidr):
            number = number + operator.rshift(128, i)
        return number

    def Cidr2Netmask(self):
        """ convert CIDR to subnetmask """
        cidr = self.__cidr
        netmask = []
        for i in range(8, cidr, 8):
            cidr = cidr - 8
            netmask.append(self.__Cidr2Netmask(i))
        netmask.append(self.__Cidr2Netmask(cidr))
        i = Ip4(netmask)
        return i.Get()

    def __Netmask2Cidr(self, n):
        """ convert one byte of netmask to CIDR value """
        j = 0
        for i in [128, 64, 32, 16, 8, 4, 2, 1]:
            n = n - i
            if n >= 0:
                j = j + 1
        return j

    def Netmask2Cidr(self):
        """ convert subnetmask to CIDR """
        snm = self.__netmask
        cidr = 0
        sn = string.split(snm, '.')
        for s in sn:
            cidr = cidr + self.__Netmask2Cidr(int(s))
        return cidr

class Ip4:

    """ an ipv4-address """

    def __init__(self, ip = '0.0.0.0'):
        if type(ip) is types.ListType:
            self.__ip = ip
            self.__rep = 1
        elif type(ip) is types.StringType:
            self.__ip = string.split(ip, '.')
            self.__rep = 0

    def SetRep(self, r):
        """
        set representation/return type
        0 = string
        1 = list
        """
        self.__rep = r

    def Set(self, l):
        if type(l) is types.StringType:
            self.__ip = string.split(l, '.')
        else:
            self.__ip = l

    def Get(self):
        self.Check()
        if self.__rep == 0:
            return string.join(self.__ip, '.')
        else:
            return self.__ip

    def Check(self):
        l = len(self.__ip)
        for i in range(4 - l):
            if self.__rep == 0:
                self.__ip.append('0')
            else:
                self.__ip.append(0)
        for i in range(l):
            if self.__rep == 0:
                self.__ip[i] = str(self.__ip[i])
                if not self.__ip[i]:
                    self.__ip[i] = '0'
            else:
                if not self.__ip[i]:
                    self.__ip[i] = 0

    def __str__(self):
        return self.Get()

class Ports:

    """ handle ip ports """

    def __init__(self, p):
        """
        p :== list of tuples with port/port ranges
        e.g.:
              p = [(0, 1024), (123)]
        """
        self.__ports = p
        self.__sep = '-'

    def SetSeparator(self, s):
        self.__sep = s

    def Check(self):
        """
        checks given ports/port ranges and deletes
        wrong ones
        """
        delete = []
        for i in range(len(self.__ports)):
            test = self.__ports[i]
            for j in test:
                if j < 0 or j > 65535:
                    if i not in delete:
                        delete.append(i)
        for d in delete:
            del self.__ports[d]

    def Convert(self):
        """ convert checked ports to string """
        s = ''
        l = len(self.__ports)
        for p in range(l):
            s = s + str(self.__ports[p][0])
            if len(self.__ports[p]) > 1:
                s = s + self.__sep + str(self.__ports[p][1])
            if p < l - 1:
                s = s + ','
        return s
                
    def __str__(self):
        return self.Convert()

class Pool:

    """ a database aided ip pool """

    def __init__(self, sql, net = ''):
        """ sql = DMS.SQL.Layer1 / GetQuery() """
        self.__sql = sql
        self.__dboid = DMS.SQL.DBOID(self.__sql, self.__sql)
        self.__network = net

    def Create(self, net, ps = 1, pe = 254):
        """ create an ip pool """
        stmt = "INSERT INTO dmerce_sys.IPPool (ID, Net, PoolStart, PoolEnd) VALUES (%i, '%s', %i, %i)" % (self.__dboid['$ippool'], net, ps, pe)
        self.__sql[stmt]
        
        
    def GetNet(self):
        """ Get all net's out of Database """
        stmt = "SELECT * FROM dmerce_sys.IPPool WHERE Net = '%s'" % self.__network
        rc, self.__net = self.__sql[stmt]

    def GetBased(self, ippoolID):
        """ Get all IP's based on IPPool.ID """
        stmt = "SELECT * FROM dmerce_sys.IPPoolAddr WHERE IPPoolID = %i" % ippoolID
        rc, self.__ipBased = self.__sql[stmt]

    def GetIP(self):
        """ Check for a free IP Address """
        ips = []
        newip = None
        for nets in self.__net:
            self.GetBased(nets['ID'])
            print 'Nets result ', nets
            print
            if len(self.__ipBased) > 0:
                for based in self.__ipBased:
                    ips.append(based['IP'])
                for ip in range(int(nets['PoolStart']), int(nets['PoolEnd']) + 1):
                    if ip not in ips:
                        self.UpdatePool(ip, nets['ID'])
                        return ip
            else:
                self.UpdatePool(nets['PoolStart'], nets['ID'])
                sys.exit()
        return None

    def UpdatePool(self, ip, IPpoolID):
        """ Insert new IP into IPPoolAddr """
        stmt = "INSERT INTO dmerce_sys.IPPoolAddr (IP, IPPoolID) VALUES (%i, %i)" % (ip, IPpoolID)
        print stmt
        rc, r = self.__sql[stmt]

    def DeleteIP(self, ip):
        """ Deleting an IP from Pool """
        orstmt = ""
        stmt = "SELECT ID FROM dmerce_sys.IPPool WHERE Net = '%s'" % self.__network
        rc, r = self.__sql[stmt]
        for i in range(len(r)):
            if i != len(r) - 1:
                orstmt = orstmt + ' dmerce_sys.IPPoolAddr.IPPoolID = %i OR ' % r[i]['ID']
            else:
                orstmt = orstmt + ' dmerce_sys.IPPoolAddr.IPPoolID = %i' % r[i]['ID']
        stmt = "DELETE FROM dmerce_sys.IPPoolAddr WHERE dmerce_sys.IPPoolAddr.IP = %i AND (%s)" % (ip, orstmt)
        if orstmt != "":
            rc, r = self.__query[stmt]

    def GetFreeIP(self, r, used):
        for f in range(len(r)):
            ps = r[f]['PoolStart']
            pe = r[f]['PoolEnd']
            ipPoolID = r[f]['ID']
            for i in range (ps, pe):
                for j in used:
                    if i not in j['IP']:
                        print 'Unused IP wil be inserted ->', i
                        print 'from Pool -> ', k
                        return i
                    else:
                        print 'IP ', i , 'is still used'
                        print 'in Pool -> ', k
    

    def ScanPool(self, r):
        for i in range(len(r)):
            ps = r[i]['PoolStart']
            pe = r[i]['PoolEnd']
            hip = r[i]['HighestIP']
            if hip:
                hip = hip + 1
            else:
                hip = ps
            if hip <= pe:
                return hip, r[i]['ID']
        return None

    def GetUsed(self, id):
        """ return all used ip addresses of net """
        stmt = "SELECT ID, IP, IPPoolID FROM dmerce_sys.IPPoolUsed "
        stmt = stmt + "WHERE dmerce_sys.IPPool.ID = dmerce_sys.IPPoolUsed.IPPoolID"
        rc, r = self.__sql[stmt]
        return rc

    def Get(self, net):
        """ returns next free/unused ip of network 'net' """
        stmt = "SELECT ID, PoolStart, PoolEnd, HighestIP FROM dmerce_sys.IPPool WHERE net = '%s'" % net
        rc, r = self.__sql[stmt]
        if rc:
            hip, id = self.ScanPool(r)
            if hip:
                stmt = "UPDATE dmerce_sys.IPPool SET HighestIP = %i WHERE ID = '%s'" % (hip, id)
                self.__sql[stmt]
                ret = net + '.' + str(hip)
            else:
                ret = None
        else:
            ret = None
        return ret

#ip4mask = Ip4Mask()
#ip4mask.SetCidr(16)
#print ip4mask.Cidr2Netmask()
