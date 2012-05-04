#!/usr/local/env python
#

""" Import recent Modules """
import sys
import os
import string
import getopt
import time

""" import dmerce modules"""
import DMS.SysTbl
import Guardian.Config
import DMS.SQL
import DMS.InitDmerce
import DMS.Lock
import DMS.ConfPath
import NCCServers
import DMS.Webalizer
import vars


vars.vars()

class DBData:

    def GetWebSrvConf(self, query, serverID):
        rc, r = query["SELECT "\
                      "SrvWebsrvConfig.ID AS WebserverID, " \
                      "SrvWebsrvConfig.ConfPath, " \
                      "SrvWebsrvConfig.DocumentRoot, " \
                      "SrvWebsrvConfig.LogDir, " \
                      "SrvWebsrvConfig.ConfFileName, " \
                      "SrvWebsrvConfig.ServerID " \
                      "FROM SrvWebsrvConfig " \
                      "WHERE " \
                      "SrvWebsrvConfig.active = 1 AND " \
                      "SrvWebsrvConfig.ServerID = '%s'" % serverID]
        return r

    def GetVhostDmerce(self, query, serverID, websrvID):
        rc, r = query["SELECT "  \
                      "SrvWebsrvVhosts.ID AS VhostID, "  \
                      "DnsZones.Name AS ZoneName, "  \
                      "DnsRecords.Name AS RecordName, "  \
                      "DnsRrtypes.Name AS RecordType, "  \
                      "DnsRecords.Value AS RRecord, "  \
                      "SrvWebsrvVhosts.ServerName, "  \
                      "SrvWebsrvVhosts.ServerAdmin, "  \
                      "SrvWebsrvVhosts.DocumentRoot, "  \
                      "SrvWebsrvVhosts.ErrorLog, "  \
                      "SrvWebsrvVhosts.AccessLog, "  \
                      "SrvWebsrvVhosts.webalizer, " \
                      "SrvWebsrvALT.Name AS AccLogType, "  \
                      "SrvWebsrvVhosts.AdditionalConfigs, "  \
                      "SrvWebsrvDmerceVersion.ExecCGIPath, " \
                      "SrvWebsrvDmerceVersion.DmerceVhostConfig " \
                      "FROM "  \
                      "SrvWebsrvVhosts, DnsZones, DnsRecords, " \
                      "DnsRrtypes, SrvWebsrvALT, SrvWebsrvDmerceVersion "  \
                      "WHERE "  \
                      "SrvWebsrvVhosts.DnsZoneID = DnsZones.ID AND "  \
                      "SrvWebsrvVhosts.DnsRecordID = DnsRecords.ID AND "  \
                      "DnsRecords.Type = DnsRrtypes.ID AND "  \
                      "SrvWebsrvALT.ID = SrvWebsrvVhosts.AccessLogType AND "  \
                      "SrvWebsrvVhosts.DmerceVersion = SrvWebsrvDmerceVersion.ID " \
                      "AND SrvWebsrvVhosts.SrvWebsrvConfigID = '%s' " % websrvID + \
                      "AND SrvWebsrvVhosts.ServerID = '%s' " % serverID + \
                      "AND SrvWebsrvVhosts.active = 1 " \
                      "ORDER BY DnsZones.Name"]
        return r

    def GetVhostOther(self, query, serverID, websrvID):
        rc, r = query["SELECT "  \
                      "SrvWebsrvVhosts.ID AS VhostID, "  \
                      "DnsZones.Name AS ZoneName, "  \
                      "DnsRecords.Name AS RecordName, "  \
                      "DnsRrtypes.Name AS RecordType, "  \
                      "DnsRecords.Value AS RRecord, "  \
                      "SrvWebsrvVhosts.ServerName, "  \
                      "SrvWebsrvVhosts.ServerAdmin, "  \
                      "SrvWebsrvVhosts.DocumentRoot, "  \
                      "SrvWebsrvVhosts.ErrorLog, "  \
                      "SrvWebsrvVhosts.AccessLog, "  \
                      "SrvWebsrvVhosts.webalizer, " \
                      "SrvWebsrvALT.Name AS AccLogType, "  \
                      "SrvWebsrvVhosts.AdditionalConfigs "  \
                      "FROM "  \
                      "SrvWebsrvVhosts, DnsZones, DnsRecords, " \
                      "DnsRrtypes, SrvWebsrvALT "  \
                      "WHERE "  \
                      "SrvWebsrvVhosts.DnsZoneID = DnsZones.ID AND "  \
                      "SrvWebsrvVhosts.DnsRecordID = DnsRecords.ID AND "  \
                      "DnsRecords.Type = DnsRrtypes.ID AND "  \
                      "SrvWebsrvALT.ID = SrvWebsrvVhosts.AccessLogType AND "  \
                      "SrvWebsrvVhosts.DmerceVersion = '0' " \
                      "AND SrvWebsrvVhosts.SrvWebsrvConfigID = '%s' " % websrvID + \
                      "AND SrvWebsrvVhosts.ServerID = '%s' " % serverID + \
                      "AND SrvWebsrvVhosts.active = 1 "
                      "ORDER BY DnsZones.Name"]
        return r

    def GetSrvAlias(self, query, id):
        rc, r = query["SELECT " + \
                      "Alias " + \
                      "FROM " + \
                      "SrvWebsrvAliases, SrvWebsrvVhosts " + \
                      "WHERE " + \
                      "SrvWebsrvAliases.WebSrvID = SrvWebsrvVhosts.ID AND " + \
                      "SrvWebsrvAliases.WebSrvID = '%s'" % id]
        return r
    
    def GetIP(self, query, zone, name):
        if string.count(name, '.') > 1:
            name = string.split(name, '.')[0]
        self.__ip = ''
        rc, r = query["SELECT DnsRecords.Name AS RecordName, " +\
                      "DnsRrtypes.Name AS RecordType, " + \
                      "DnsRecords.Value AS RecordValue " + \
                      "FROM " + \
                      "DnsRecords, DnsRrtypes, DnsZones " + \
                      "WHERE DnsRecords.Name = '%s' AND " % name + \
                      "DnsRecords.Type = DnsRrtypes.ID AND " + \
                      "DnsZones.Name = '%s' AND " % zone + \
                      "DnsZones.ID = DnsRecords.ZoneID " \
                      "AND DnsZones.active = 1 " \
                      "AND DnsRecords.active = 1"]
	if len(r) > 0:
            if r[0]['RecordType'] != 'A':
                print "---->",zone,"/",r[0]['RecordValue']
                self.GetIP(query, zone, r[0]['RecordValue'])
            elif r[0]['RecordType'] == 'A':
                self.__ip = r[0]['RecordValue']

    def ReturnIP(self):
        if self.__ip:
            return self.__ip


    def CheckService(self, query, serverID):
        """ Check if DNS is a service for current server """
        rc, r = query["SELECT * FROM SrvServerSvcs " + \
                      "WHERE ServerID = '%s' " %serverID + \
                      "AND ServiceID = '1' " + \
                      "AND active = '1'"]
        return rc


class General:
    def __init__(self):
        self.__head = ''

    def GenGeneral(self):
        self.__head = self.__head + '<Files *.cfg>\n' + \
                      '  Deny from All\n' + \
                      '</Files>\n\n' + \
                      '<Directory "templates/">\n' + \
                      '  Deny from all\n' + \
                      '</Directory>\n\n' + \
                      '<Directory /opt/dmerce/product/dmerce>\n' + \
                      '  AddHandler python-program .pyc\n' + \
                      '  PythonHandler dmerce\n' + \
                      '  PythonDebug On\n' + \
                      '</Directory>\n\n'
        
    def __str__(self):
        return '%s' % self.__head

class Vhost:
    def __init__(self, type):
        self.__type = type
        self.__addCfg = ''
        
    def SetInfoHeader(self, value):
        """Set the vhost info header"""
        self.__info = '# ' + value + '\n\n'

    def SetListen(self, value):
        """ Set Listen entry """
        self.__listen = 'Listen ' + value + ':80' + '\n'

    def SetNameVirtHost(self, value):
        """ Set Name Virtual Host """
        self.__nameVirtHost = 'NameVirtualHost ' + value + '\n\n'

    def SetVhost(self, value):
        """ Set VirtualHost value """
        self.__vhost = '<VirtualHost ' + value + '>\n'

    def SetFoot(self):
        self.__foot = '</VirtualHost>\n\n'

    def SetServerAdmin(self, value):
        self.__srvAdmin = '\t' + 'ServerAdmin ' + value + '\n'

    def SetSrvName(self, value):
        """ Set ServerName entry """
        self.__srvName = '\t' + 'ServerName ' + value + '\n'

    def SetAliases(self, value):
        if value != '':
            self.__aliases = '\t' + 'ServerAlias ' + value + '\n'
        else:
            self.__aliases = ''

    def CreateDocRoot(self, value):
        self.__newDoc = ''
        if string.count(value, '.') >= 1:
            self.__split = string.split(value, '.')
            for i in range(len(self.__split)-1,-1,-1):
                self.__newDoc = self.__newDoc + '/' + str(self.__split[i])
            #print self.__newDoc
            return self.__newDoc
        else:
            return value
            
    def SetDocRoot(self, value):
        """ Set DocumentRoot entry """
        self.__docRoot = '\t' + 'DocumentRoot ' + value + '\n' + '\t' + 'ServerPath ' + value + '\n'
        self.__docRoot = string.replace(self.__docRoot, '//', '/')
        
    def SetAccLog(self,value):
        """ Set AccessLog enrty """
        self.__accLog = 'CustomLog ' + str(value)

    def SetAccLogType(self,value):
        """ Set AccessLog enty """
        self.__accLog = '\t' + self.__accLog + ' ' + str(value) + '\n'

    def SetErrLog(self, value):
        """ Set ErrorLog entry """
        self.__errLog = '\t' + 'ErrorLog ' + value + '\n'

    def SetAddCfg(self, value):
        if value:
            """ Set additional configuration entries """
            if str(value) != '':
                self.__addCfg = self.__addCfg + '\n' + string.replace(value, '\r', '') + '\n'
            else:
                self.__addCfg = ''

    def __str__(self):
        if self.__type == 'withHeader':
            return '%s%s%s%s%s%s%s%s%s%s%s%s' % (self.__info,
                                                 self.__listen,
                                                 self.__nameVirtHost,
                                                 self.__vhost,
                                                 self.__docRoot,
                                                 self.__srvName,
                                                 self.__aliases,
                                                 self.__srvAdmin,
                                                 self.__accLog,
                                                 self.__errLog,
                                                 self.__addCfg,
                                                 self.__foot)
        if self.__type == 'noHeader':
            return '%s%s%s%s%s%s%s%s%s%s' % (self.__info,
                                             self.__vhost,
                                             self.__docRoot,
                                             self.__srvName,
                                             self.__aliases,
                                             self.__srvAdmin,
                                             self.__accLog,
                                             self.__errLog,
                                             self.__addCfg,
                                             self.__foot)


class File:
    def __init__(self):
        self.__file = ''
        self.__gen = ''

    def GenGeneral(self):
        g = General()
        g.GenGeneral()
        self.__gen = self.__gen + str(g)

    def AddExecCGI(self, value):
        self.__gen = self.__gen + "<Directory %s>\n" % value + \
                     "  Options ExecCGI\n" \
                     "</Directory>\n\n"

    def AddLine(self, value):
        self.__file = self.__file + value

    def __str__(self):
        return '%s' % self.__file

    def CheckPath(self, path):
        if os.path.exists(path):
            return 1
        else:
            return None

    def CheckPathVariable(self, path):
        if path[-1] != '/':
            return path + '/'
        else:
            return path

    def Write(self, webSrvDir, confFileName):
        if self.__file == '':
            pass
        else:
            f = open(self.CheckPathVariable(webSrvDir) + confFileName , 'w')
            f.write(self.__gen + self.__file)
            f.close()

class Function:
    def ChangeElement(self, list, new, count):
        new_list = []
        for i in range(len(list)):
            if i == count:
                new_list.append(new)
            else:
                new_list.append(list[i])
        return new_list

    def CreateList(self, elements, seperator, number):
        list = ''
        """
        method to create a space-seperated string for aliases
        out of a list at special space in list
        """
        for i in range(len(elements)):
            list = list + elements[i]['Alias'] + seperator
        return list

def main2(query, serverID, confPath, webserverID, docRoot, result, LogDir, filename):
    if len(str(LogDir)) > 0 and LogDir[-1] != '/':
        LogDir = LogDir + '/'
    iplist = []
    file = File()
    file.GenGeneral()
    iplist = []
    execCGI = []
    func = Function()
    for i in range(len(result)):
        """ go through all webserver entries """
        if result[i]['RecordType'] != 'A':
            """ replace cname with valid ip address """
            db.GetIP(query, result[i]['ZoneName'], result[i]['RRecord'])
            ip = db.ReturnIP()
	    if not ip:
		continue
            result[i]['RRecord'] = ip
        else:
            ip = result[i]['RRecord']
        """ Get Aliases for actual ID """
        if len(db.GetSrvAlias(query, result[i]['VhostID'])) > 0:
            aliases = func.CreateList(db.GetSrvAlias(query, result[i]['VhostID']), ' ' , 0)
        else:
             aliases = ''
        if ip in iplist:
            vhost = Vhost('noHeader')
        else:
            vhost = Vhost('withHeader')
            iplist.append(ip)
        vhost.SetNameVirtHost(ip)
        vhost.SetListen(ip)
        vhost.SetVhost(ip)
        if result[i]['DocumentRoot'] != '':
            vhost.SetDocRoot(docRoot + '/' + vhost.CreateDocRoot(result[i]['DocumentRoot']))
        else:
            vhost.SetDocRoot(docRoot + '/' + result[i]['ZoneName'] + '/' + \
                             vhost.CreateDocRoot(result[i]['RecordName']))
        vhost.SetAliases(aliases)
        vhost.SetSrvName(result[i]['RecordName'] + '.' + result[i]['ZoneName'])
        vhost.SetInfoHeader(result[i]['RecordName'] + '.' + result[i]['ZoneName'])
        vhost.SetServerAdmin(result[i]['ServerAdmin'])
        vhost.SetAccLog(LogDir + result[i]['RecordName'] + '.' + result[i]['ZoneName'] + '-access_log')
        vhost.SetAccLogType(result[i]['AccLogType'])
        vhost.SetErrLog(LogDir + result[i]['RecordName'] + '.' +  result[i]['ZoneName'] +'-error_log')
        vhost.SetFoot()
        vhost.SetAddCfg(result[i]['AdditionalConfigs'])
        if result[i]['webalizer'] == 1:
            web = DMS.Webalizer.Webalizer(result[i]['RecordName'] + '.' + result[i]['ZoneName'],
                                          docRoot + '/' + result[i]['ZoneName'] + '/' + \
                                          vhost.CreateDocRoot(result[i]['RecordName']),
                                          LogDir)
            web.CreateConf()
            web.WriteConf()
        if result[i].has_key('DmerceVhostConfig'):
            vhost.SetAddCfg(result[i]['DmerceVhostConfig'])
        if result[i].has_key('ExecCGIPath'):
            if result[i]['ExecCGIPath'] not in execCGI:
                execCGI.append(result[i]['ExecCGIPath'])
        file.AddLine(str(vhost))
    for e in execCGI:
        file.AddExecCGI(e)
    file.Write(confPath, filename)
    
def main(query, serverID, confPath, webserverID, docRoot, LogDir, serverInf, filename):
    db = DBData()
    dmerceHome = os.environ['DMERCE_HOME']
    conf = dmerceHome + '/' + 'NCC' + '/' + serverInf['ServerName'] + '/' + 'http'
    conf = conf + '/' + confPath
    a = DMS.ConfPath.Path(conf)
    a.Create()
    resultDmerce = None
    resultNormal = None
    if db.GetVhostDmerce(query, serverID, webserverID) != []:
        resultDmerce = db.GetVhostDmerce(query, serverID, webserverID)
    if db.GetVhostOther(query, serverID, webserverID) != []:
        resultNormal = db.GetVhostOther(query, serverID, webserverID)
    func = Function()
    clean_data = []
    if resultDmerce:
        main2(query, serverID, conf, webserverID, docRoot, resultDmerce, LogDir, filename)
        del resultDmerce
    if resultNormal:
        main2(query, serverID, conf, webserverID, docRoot, resultNormal, LogDir, filename)
        del resultNormal
        
""" Main section """
if __name__=='__main__':
    lock = DMS.Lock.Lock('dmerce.http.lock')
    """ Create time string for log entry """
    logTime = time.localtime(time.time())
    print '[%s-%s-%s  %s:%s:%s]\n' % (logTime[0], logTime[1], logTime[2],
                                      logTime[3], logTime[4], logTime[5])
    """ now we have to look, if this script is still running """
    if not lock.CheckLock():
        print 'dns script such running'
        sys.exit()
    lock.AquireLock()
    dmercecfg = DMS.InitDmerce.NCC()
    dmercecfg.InitNCC()
    query = dmercecfg.InitDBConnection()
    db = DBData()
    servers = NCCServers.Servers(query)
    data = servers.GetAll()
    for i in data:
        if db.CheckService(query, i['ServerID']) == 1:
            websrv = db.GetWebSrvConf(query, i['ServerID'])
            for inf in websrv:
                """ Start main procedure """
                main(query, inf['ServerID'], inf['ConfPath'], inf['WebserverID'], \
                     inf['DocumentRoot'], inf['LogDir'], i, string.strip(str(inf['ConfFileName'])))
    """ end of script, remove lock file """
    lock.ReleaseLock()
