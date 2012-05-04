import sys
import string
import DMS.SysTbl
import Guardian.Config
import DMS.SQL
import commands
import DMS.InitDmerce

class Dhcp:

    """ 1Ci - NCC - Get dhcp config file """

    def __init__(self):
        dmercecfg = DMS.InitDmerce.NCC()
        dmercecfg.InitNCC()
        self.__sqlData = dmercecfg.InitDBConnection()
        self.__dboid = DMS.SQL.DBOID(self.__sqlData, self.__sqlData)

    def GetDhcpServers(self):
        rc, r = self.__sqlData["SELECT SrvServerInterfaces.IfPhysAddress AS mac, SrvServer.Name " + \
                               "FROM SrvServerInterfaces, SrvServerSvcs, SrvServer " + \
                               "WHERE SrvServerInterfaces.ServerID = SrvServerSvcs.ServerID "+ \
                               "AND SrvServerSvcs.ServiceID = 6 " + \
                               "AND SrvServer.ID = SrvServerInterfaces.ServerID"]
        return r

    def GetGlobalSettings(self, mac):
        glsettingscount, glsettings = self.__sqlData["SELECT i.IfName,gl.DefaultLeaseTime,gl.MaxLeaseTime"+ \
                                                     " FROM SrvServerInterfaces AS i,SrvDhcpGlSettings AS gl " + \
                                                     " WHERE i.IfPhysAddress = '%s' AND i.ServerID = gl.ServerID" % mac] 
        return glsettings

    def GetSubnetSettings(self, mac):
        subcount, sub = self.__sqlData["SELECT su.SubnetIP, su.Netmask, su.Broadcast, su.Router, su.Nameserver, " +
                                       " su.NetbiosNs, su.Domain, su.RangelowIP, su.RangehighIP, i.IfName" +
                                       " FROM SrvServerInterfaces AS i, SrvDhcpGlSettings AS gl," +
                                       " SrvDhcpSubnets AS su WHERE i.IfPhysAddress = '%s'" % mac +
                                       " AND su.DeviceID = i.ID"]
        return sub

    def GetHostSettings(self, mac):
        hostcount, host = self.__sqlData["SELECT hn.Hostname, hn.MacAddress, hn.ClientIP" +
                                         " FROM SrvServerInterfaces AS i, " +
                                         " SrvDhcpGlSettings AS gl, SrvDhcpHosts AS hn" +
                                         " WHERE i.IfPhysAddress = '%s'" % mac +
                                         " AND hn.DeviceID = i.ID"]
        return host
