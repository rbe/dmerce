#!/usr/bin/env python
#

import DMS.SQL
import DMS.SysTbl
import Guardian.Config
import sys
import os
import os.path
import time
import string
import getopt
import DNSArpa
import DNSNS
from DNSZone import ZoneData
from BuildNDCReloadScript import ReloadFile
import DMS.InitDmerce
import vars
import DMS.Lock
import NCCServers
import DMS.ConfPath


def ti():
    """ return local iso time string """
    return time.strftime('%a %b %d %X %Z %Y:', time.localtime(time.time()))

class DBData:
    def GetNSServerID(self, query, id):
        rc, r = query["SELECT ID FROM DnsNs " \
                      "WHERE ServerID = '%s'" %id]
        return r
    
    def GetNsData(self, query, serverID):
        """ Get primary nameservers plus IP """
        rc, r =query["SELECT DnsNs.ID AS NS_ID, " + \
                     "DnsNs.Name AS NS_Name, " + \
                     "DnsNs.IP AS NS_IP, " + \
                     "DnsNs.BaseDir, " + \
                     "Agent.ID AS AgentID, " + \
                     "Agent.Name AS AgentName, " + \
                     "Agent.Vorname AS AgentVorname " + \
                     "FROM DnsNs, Agent WHERE DnsNs.AgentID = Agent.ID " + \
                     "AND DnsNs.ID='%s'" % serverID]
        return r

    def GetSlaveZones(self, query, serverNr):
        """ Get zone for slave """
        rc, r = query["SELECT d.Name AS ZoneName, " + \
                      "n.IP AS MasterIP "
                      "FROM DnsZones AS d, DnsNs AS n " + \
                      "WHERE (d.NS2='%s' OR d.NS3='%s' OR d.NS4='%s') " %(serverNr, serverNr, serverNr) + \
                      "AND d.NS1 = n.ID " \
                      "AND d.active = 1"]
        return r

    def GetNSList(self, query, zone):
        """ Get all nameservers for special domain """
        rc, r = query["SELECT DnsNs.Name AS NS_Name " +\
                      "FROM DnsNs, DnsZones " +\
                      "WHERE DnsZones.Name like '%s%%' " % zone + \
                      "AND (DnsNs.ID=DnsZones.NS1 " + \
                      "OR DnsNs.ID=DnsZones.NS2 " + \
                      "OR DnsNs.ID=DnsZones.NS3 " + \
                      "OR DnsNs.ID=DnsZones.NS4) " + \
                      "ORDER BY DnsNs.Name"]
        return r
    
    def UpdateReverse(self, query, serial, subSerial, id):
        rc, r = query["UPDATE DnsReverse SET ToDo = 0, SOASerial='%s', SOASubserial='%s' " % (serial, subSerial) + \
                      "WHERE DnsReverse.ID = '%s'" % id]
        
    
    def GetZones(self, query, AgentID, serverNr):
        """ Get zones """
        rc, r = query["SELECT DnsZones.ID AS Zone_ID, " + \
                      "DnsZones.Name AS ZoneName, " + \
                      "DnsZones.SOASerial, " + \
                      "DnsZones.SOASubserial, " + \
                      "DnsZones.SOARefresh, " + \
                      "DnsZones.SOARetry, " + \
                      "DnsZones.SOAExpire, " + \
                      "DnsZones.SOAMaximum, " + \
                      "DnsZones.TTL, " + \
                      "DnsZones.ZoneC, " + \
                      "DnsZones.NS1 Zone_NS1, " + \
                      "DnsNs.Name AS Ns2Name " \
                      "FROM DnsZones, DnsNs WHERE DnsZones.AgentID='%s' " %(AgentID) + \
                      "AND DnsZones.NS1='%s' " %serverNr + \
                      "AND DnsZones.NS2 = DnsNs.ID " \
                      "AND DnsZones.active = 1"]
        return r

    def GetDeletedZones(self, query, AgentID, serverNr):
        stmt = "SELECT ID, Name FROM DnsZones " \
               "                WHERE AgentID = '%s' " % AgentID + \
               "                  AND NS1 = '%s' " % serverNr + \
               "                  AND ToDo = 1 " \
               "                  AND active = 0"
        rc, r = query[stmt]
        return r

    def GetChangedZones(self, query):
        todo = []
        rc, r = query["SELECT DnsZones.Name AS ZoneName " + \
                      "FROM DnsZones WHERE "  + \
                      "DnsZones.active = 1 " + \
                      "AND DnsZones.ToDo = 1 "]

        rk, s = query["SELECT DnsReverse.arpa " + \
                      "FROM DnsReverse WHERE "  + \
                      "DnsReverse.active = 1"]
        for k in r:
            if k['ZoneName'] not in todo:
                todo.append(k['ZoneName'])
        for f in s:
            if f['arpa'] not in todo:
                todo.append(f['arpa'])
        return todo

    def SetDelFlag(self, zone):
        """ set todo flag = 0 for deleted zone """
        stmt = "UPDATE DnsZones SET ToDo = 0 " \
               "              WHERE Name = '%s' " % zone + \
               "                AND active = 0"

    def SetToDoFlag(self, query, zone, serial, subSerial):
        rc, r = query["UPDATE DnsZones SET ToDo = 0, SOASerial = '%s', SOASubserial = '%s' " %(serial, subSerial)+ \
                      "WHERE Name = '%s'" % (zone)]

    def GetRR(self, query, ZoneID):
        """ Get records """
        rc, r = query["SELECT DnsRecords.Name AS RecordName, " + \
                      "DnsRrtypes.Name RType, " + \
                      "DnsRecords.Value RecordValue " + \
                      "FROM DnsRecords,DnsRrtypes " + \
                      "WHERE ZoneID='%s' AND " % ZoneID + \
                      "DnsRecords.Type=DnsRrtypes.ID AND " + \
                      "DnsRecords.active=1"]
        return r

    def GetForwarders(self, query, ns_ID):
        """ Get forwarder ip's """
        rc, r = query["SELECT fw AS Forwarder " + \
                      "FROM DnsForwarders WHERE DnsNsID='%s'" %ns_ID]
        return r

    def GetReverse(self, query, id):
        """ Get reverse maps """
        rc, r = query["SELECT DnsReverse.arpa, " + \
                      "DnsReverse.SOASerial, " + \
                      "DnsReverse.ID, " + \
                      "DnsReverse.SOASubserial, " + \
                      "DnsReverse.DefaultZoneC AS ZoneC, " + \
                      "DnsNs.Name AS NS_Name, " + \
                      "DnsReverse.ToDo, "
                      "Agent.Name AS AgentName, " + \
                      "Agent.Vorname AS AgentVorname " + \
                      "FROM DnsReverse, DnsNs, Agent WHERE " + \
                      "DnsNs.ID='%s' AND DnsReverse.ns1ID='%s' " % (id, id) + \
                      "AND DnsReverse.active = 1 " + \
                      "AND (DnsReverse.ToDo = 1 OR DnsReverse.ToDo = 0) " + \
                      "AND Agent.NS1 = '%s' " % id]
        return r


    def GetReverseNSList(self, query, arpa):
        """ Get reverse maps """
        rc, r = query["SELECT DnsNs.Name AS NS_Name " + \
                      "FROM DnsReverse, DnsNs WHERE " + \
                      "DnsReverse.active = 1 " + \
                      "AND DnsReverse.ToDo = 1 " + \
                      "AND DnsReverse.arpa = '%s' " % arpa + \
                      "AND (DnsNs.ID=DnsReverse.ns1ID " + \
                      "OR DnsNs.ID=DnsReverse.ns2ID " + \
                      "OR DnsNs.ID=DnsReverse.ns3ID " + \
                      "OR DnsNs.ID=DnsReverse.ns4ID) " + \
                      "ORDER BY DnsNs.Name"]
        return r

    def GetSlaveReverse(self, query, id):
        """ Get slave reverse maps """
        rc, r = query["SELECT DnsReverse.arpa, " + \
                      "DnsNs.IP AS MasterIP " + \
                      "FROM DnsReverse, DnsNs "
                      "WHERE " + \
                      "DnsReverse.ns1ID = DnsNs.ID " \
                      "AND DnsReverse.active = 1 "
                      "AND (ns2ID = '%s' OR ns3ID = '%s' OR ns4ID = '%s')" % (id, id, id)]
        return r

    def GetReverseNS(self, query,  ip):
        """ Get ns for reverse maps """
        rc, r = query["SELECT DnsNs.Name AS NS_Name, " + \
                      "DnsReverse.Serial, " + \
                      "DnsReverse.DefaultZoneC " + \
                      "FROM DnsNs, DnsReverse " + \
                      "WHERE " + \
                      "DnsReverse.arpa='%s' " % ip + \
                      "AND (DnsReverse.ns1ID=DnsNs.ID " + \
                      "OR DnsReverse.ns2ID=DnsNs.ID " + \
                      "OR DnsReverse.ns3ID=DnsNs.ID " + \
                      "OR DnsReverse.ns4ID=DnsNs.ID)"]
        return r
    
    def GetPTR(self, query, ip):
        """ Get all PTR for arpa file """
        oldip = ip
        arpa = DNSArpa.ReverseMap()
        if 'in-addr' in string.split(ip, '.'):
            ip = arpa.FilterIP(ip)
        rc, r = query["SELECT DnsRecords.Name AS RecordName, " + \
                      "DnsRecords.Value AS RecordValue, " + \
                      "DnsZones.Name AS ZoneName, " + \
                      "DnsZones.ZoneC, " + \
                      "DnsRrtypes.Name AS RType, " + \
                      "DnsReverse.arpa " + \
                      "FROM DnsRecords, DnsZones, DnsRrtypes, DnsReverse " + \
                      "WHERE DnsRecords.Value LIKE '%s' " % (ip + "%") + \
                      "AND DnsRecords.active=1 " + \
                      "AND DnsRecords.ZoneID=DnsZones.ID "+ \
                      "AND DnsRecords.Type=DnsRrtypes.ID "+ \
                      "AND DnsReverse.arpa='%s'" %oldip]
        return r

    def GetListenOn(self, query, nsID):
        ips = []
        stmt = "SELECT IP from DnsNsListenOn WHERE DnsNsID = %i AND active = 1" % nsID
        rc, r = query[stmt]
        for i in r:
            ips.append(i['IP'])
        return ips
    
    def CheckService(self, query, serverID):
        """ Check if DNS is a service for current server """
        rc, r = query["SELECT * FROM SrvServerSvcs " + \
                      "WHERE ServerID = '%s' " %serverID + \
                      "AND ServiceID = '7' " + \
                      "AND active = '1'"]
        return r

class Function:
    def CheckArpa(self, arpa):
        """ if first count is not a ip, return without first one """
        test = 0
        newarpa = ''
        temp = string.split(arpa, '.')
        try:
            a = int(temp[0])
        except:
            test = 1
        if test == 1:
            for i in range(1, len(temp)):
                if i == len(temp) - 1:
                    newarpa = newarpa + str(temp[i])
                else:
                    newarpa = newarpa + str(temp[i]) + '.'
        if test == 0:
            newarpa = arpa
        return newarpa
        
    def CheckPath(self, confPath, agentIdent, ns = ''):
        """ Check if directory still exists, else create directory """
        if not os.path.exists(confPath + agentIdent):
            os.mkdir(confPath + agentIdent)
        if ns and ns != '':
            if not os.path.exists(confPath + agentIdent + '/' + ns):
                os.mkdir(confPath + agentIdent + '/' + ns)
            if not os.path.exists(confPath + agentIdent + '/' + ns + '/' + 'pri'):
                os.mkdir(confPath + agentIdent + '/' + ns + '/' + 'pri')
            if not os.path.exists(confPath + agentIdent + '/' + ns + '/' + 'sec'):
                os.mkdir(confPath + agentIdent + '/' + ns + '/' + 'sec')


    def AppendKey(self, dict, key, value):
        for i in range(len(dict)):
            dict[i][key] = value
        return dict

    def InvertIP(self, ip):
        """ Check for class c net """
        self.__ip = ip
        self.__convertedIP = ''
        if ip[-1] != '.':
            self.__ip = self.__ip + '.'
        each = string.split(self.__ip, '.')
        for i in range(len(each)-1, -1, -1):
            self.__convertedIP = self.__convertedIP + each[i]
            if i != len(each)-1:
                self.__convertedIP = self.__convertedIP + '.'
        return self.__convertedIP
        
    def BuildArpaZone(self, arpa):
        self.__arpa = self.InvertIP(arpa)
        return self.__arpa + 'in-addr.arpa'
    
def main(query, nsServerID, confPath):
    reloadFile = ReloadFile()
    db = DBData()
    func = Function()
    agents = db.GetNsData(query, nsServerID)
    slaveReverse = db.GetSlaveReverse(query, nsServerID)
    arpaNS = None
    changedZones = db.GetChangedZones(query)
    for i in range(len(agents)):
        nslist = None
        forwarders = None
        priZones = None
        zoneContent = ''
        slaveZoneContent = ''
        forwarders = db.GetForwarders(query, nsServerID)
        arpaNS = db.GetReverse(query, nsServerID)
        ptr = {}
        if len(arpaNS) > 0:
            ptr = {}
            for t in range(len(arpaNS)):
                ptr[arpaNS[t]['arpa']] = func.AppendKey(db.GetPTR(query, arpaNS[t]['arpa']),\
                                                        'RType','PTR')
        priZones = db.GetZones(query, agents[i]['AgentID'], nsServerID)
        """ Get all zones for actual agent """
        slaveZones = db.GetSlaveZones(query, nsServerID)
        """ Get all slave zones depending on actual server id """
        deletedZones = db.GetDeletedZones(query, agents[i]['AgentID'], nsServerID)
        if ptr:
            if ptr.has_key(agents[i]['NS_Name']):
                thisPTR = ptr[agents[i]['NS_Name']]
        else:
            thisPTR = []
        if len(priZones) > 0 or len(thisPTR) > 0 or len(slaveZones) > 0:
            ns = DNSNS.NS(agents[i]['NS_Name'], confPath, agents[i]['BaseDir'])
            """ make an instance of ns and set nameservername """
            ns.SetIdent(agents[i]['AgentName'], agents[i]['AgentVorname'])
            """ set directory <agentname-agentforename> """
            func.CheckPath(confPath, ns.GetIdent(), agents[i]['NS_Name'])
            """ delete all zones which have to be deleted """
            for todel in deletedZones:
                try:
                    ns.DeleteZoneFile(todel['Name'])
                except:
                    print ti(), 'ZONE FILE FOR ZONE: %s STILL DELETED' % todel['Name']
                db.SetDelFlag(todel['Name'])
            rrZoneList = []
            listenon = db.GetListenOn(query, nsServerID)
            listenon.append(agents[i]['NS_IP'])
            ns.CreateNamedConf(listenon, forwarders)
            if len(arpaNS) > 0:
                for y in range(len(arpaNS)):
                    rev = DNSArpa.ReverseMap()
                    if 'in-addr' not in string.split(arpaNS[y]['arpa'], '.'):
                        ns.AddZone(func.BuildArpaZone(arpaNS[y]['arpa']), 'master')
                        print ti(), 'Added arpa zone -> ', func.BuildArpaZone(arpaNS[y]['arpa'])
                    else:
                        ns.AddZone(arpaNS[y]['arpa'])
                        print ti(), 'Added arpa zone -> ', arpaNS[y]['arpa']
                    """ Add each arpa zone to named.conf.master for this nameserver """
                    for z in range(len(ptr[arpaNS[y]['arpa']])):
                        """ Change some values of key """
                        ptr[arpaNS[y]['arpa']][z]['arpa'] = func.BuildArpaZone(ptr[arpaNS[y]['arpa']][z]['arpa'])
                        ptr[arpaNS[y]['arpa']][z]['RecordValue'] = func.BuildArpaZone(\
                        ptr[arpaNS[y]['arpa']][z]['RecordValue'])
                    zone = func.BuildArpaZone(arpaNS[y]['arpa'])
                    nsList = db.GetReverseNSList(query, arpaNS[y]['arpa'] )
                    if arpaNS[y]['ToDo'] == 1:
                        if 'in-addr' not in string.split(arpaNS[y]['arpa'], '.'):
                            rev.SetHeader(zone, arpaNS[y]['NS_Name'], arpaNS[y]['ID'],
                                          arpaNS[y]['ZoneC'], nsList,
                                          arpaNS[y]['SOASerial'], arpaNS[y]['SOASubserial'], db, query)
                            for arpa in ptr[arpaNS[y]['arpa']]:
                                rev.AddPTR(arpa['RecordValue'], arpa['RecordName'], arpa['ZoneName'])
                            ns.Write(zone, str(rev))
                            rrZoneList.append(zone)
                        else:
                            rev.SetHeader(func.CheckArpa(arpaNS[y]['arpa']), arpaNS[y]['NS_Name'], arpaNS[y]['ID'],
                                          arpaNS[y]['ZoneC'], nsList,
                                          arpaNS[y]['SOASerial'], arpaNS[y]['SOASubserial'], db, query)
                            for arpa in ptr[arpaNS[y]['arpa']]:
                                rev.AddPTR(arpa['RecordValue'], arpa['RecordName'], arpa['ZoneName'])
                            ns.Write(arpaNS[y]['arpa'], str(rev))
                            rrZoneList.append(arpaNS[y]['arpa'])

            reloadFile.Create(priZones,
                              ns.GetIdent(),
                              agents[i]['NS_Name'],
                              confPath, changedZones,
                              db, query, rrZoneList, agents[i]['BaseDir'])

            if len(priZones) > 0:
                for j in range(len(priZones)):
                    nsList = db.GetNSList(query, priZones[j]['ZoneName'])
                    """ Get all nameservers recent for actual zone """
                    """ Go through primary zones and get resources records
                    Set and get zone data file """
                    """ Create the default named.conf """
                    ns.AddZone(priZones[j]['ZoneName'], 'master')
                    """ Add actual zone to named.conf.master """
                    rr = db.GetRR(query, priZones[j]['Zone_ID'])
                    """ Get all resource records for actual zone """
                    #zone = ZoneData(agents[i], nsList, priZones[j])
                    zone = ZoneData(agents[i], priZones[j], rr, nsList)
                    """ Use Module to Create data for zone files """
                    zone.CreateData()
                    """ Write zone file """
                    #zone.WriteZoneFile(ns)
                    ns.Write(priZones[j]['ZoneName'], str(zone))
                    #""" Beginning of section to create arpa-zone files """
                ns.AddMasterConf()
            ns.CreateConfigFile('pri')

            if len(slaveZones) > 0 or len(slaveReverse) > 0:
                """ Section to create named.conf.slave """
                slaveNS = DNSNS.NS(agents[i]['NS_Name'], confPath, agents[i]['BaseDir'])
                """ Make an instance of NS with nameserver name """
                slaveNS.SetIdent(agents[i]['AgentName'], agents[i]['AgentVorname'])
                ns.AddSlaveConf()
                """ Set the agent identifier """
                func.CheckPath(confPath, slaveNS.GetIdent(), agents[i]['NS_Name'])
                if len(slaveZones) > 0:
                    for j in range(len(slaveZones)):
                        """ go through the list of slave zones """
                        slaveNS.AddZone(slaveZones[j]['ZoneName'], 'slave', slaveZones[j]['MasterIP'])
                        """ Add each zone """
                if len(slaveReverse) > 0:
                    for h in range(len(slaveReverse)):
                        if 'in-addr' not in string.split(slaveReverse[h]['arpa'], '.'):
                            slaveNS.AddZone(func.BuildArpaZone(slaveReverse[h]['arpa']),'slave', slaveReverse[h]['MasterIP'])
                        else:
                            slaveNS.AddZone(slaveReverse[h]['arpa'],'slave', slaveReverse[h]['MasterIP'])
                slaveNS.CreateConfigFile('sec')
                """ write the configuration file """
            ns.WriteNamedConf()
            
""" Main section """
if __name__ == '__main__':
    lock = DMS.Lock.Lock('dmerce.dns.lock')
    """ Create time string for log entry """
    logTime = time.localtime(time.time())
    print ti(), "START DNS SCRIPT"
    """ now we have to check, if this script is still running """
    if not lock.CheckLock():
        print ti(), 'DNS SCRIPT SUCH RUNNING'
        sys.exit()
    lock.AquireLock()
    dmercecfg = DMS.InitDmerce.NCC()
    dmercecfg.InitNCC()
    query = dmercecfg.InitDBConnection()
    db = DBData()
    servers = NCCServers.Servers(query)
    data = servers.GetAll()
    serverIDs = []
    for i in data:
        if db.CheckService(query, i['ServerID']):
            if len(db.GetNSServerID(query, i['ServerID'])) > 0:
                dmerceHome = os.environ['DMERCE_HOME']
                confPath = dmerceHome + '/' + 'NCC' + '/' + i['ServerName'] + '/' + 'dns' + '/'
                a = DMS.ConfPath.Path(confPath)
                a.Create()
                """ if ServerID has an nameserver ID """
                for f in db.GetNSServerID(query, i['ServerID']):
                    main(query, f['ID'], confPath)
    """ end of script, remove lock file """
    lock.ReleaseLock()
    print ti(), "STOP DNS SCRIPT"
