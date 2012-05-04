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
    import DMS.InitDmerce
    import DMS.Lock
    import DMS.ConfPath
    import NCCServers
    import time
except:
    print 'Errors occured while importing modules !'
    sys.exit()

class DBData:

    def GetAliases(self, query, serverID):
        """ Get all aliases out of database """
        rc, r = query["SELECT MailAliases.Alias, " + \
                      "DnsZones.Name AS ZoneName, " + \
                      "AccUser.Login AS AccUserLogin, " + \
                      "MailAliases.Access, " + \
                      "MailAliases.ToAddress " + \
                      "FROM MailAliases,DnsZones,AccUser " + \
                      "WHERE MailAliases.AccUserID=AccUser.ID " + \
                      "AND MailAliases.DnsZonesID=DnsZones.ID " + \
                      "AND MailAliases.active = 1 " + \
                      "AND MailAliases.ServerID = '%s'" % serverID]
        return r

    def GetSpam(self, query):
        """ GetSpamAdresses out of database """
        rc, r = query["SELECT SrvMailSrvSpam.Address, SrvMailSrvSpamType.Name " \
                      "FROM SrvMailSrvSpam, SrvMailSrvSpamType " \
                      "WHERE SrvMailSrvSpam.active = 1 " \
                      "AND SrvMailSrvSpam.TypeID = SrvMailSrvSpamType.ID"]
        return r

    def CheckService(self, query, serverID):
        """ Check if DNS is a service for current server """
        rc, r = query["SELECT * FROM SrvServerSvcs " + \
                      "WHERE ServerID = '%s' " %serverID + \
                      "AND (ServiceID = '4' OR " + \
                      "ServiceID = '5') "
                      "AND active = '1'"]
        return rc

    def GetLocalHostNames(self, query):
        """ get all local host names for sendmail """
        rc, r = query["SELECT DnsZones.Name FROM DnsZones, DnsRecords, DnsRrtypes " \
                      "WHERE DnsZones.ID = DnsRecords.ZoneID AND " \
                      "DnsRecords.Type = DnsRrtypes.ID AND " \
                      "DnsRrtypes.Name = 'MX' AND " \
                      "DnsZones.LocalSendmail = 1 " \
                      "GROUP BY DnsZones.Name"]
        return r

class GenerateAliasLine:

    """ Class to generate File """

    def SetAlias(self, alias):
        self.__alias = alias

    def CheckAlias(self, alias):
        pass
        
    def SetZone(self, zone):
        self.__zone = zone

    def CheckZone(self, zone):
        pass
        
    def SetLogin(self, login):
        self.__login = login

    def CheckLogin(self, login):
        pass

    def CheckToAddress(self, value):
        if value != '' or len(value) != 0:
            self.__login = value

    def GetCurrentAlias(self):
        return '%s@%s' %(self.__alias, self.__zone)
    
    def __str__(self):
        return '%s@%s\t\t%s\n' % (self.__alias, self.__zone, self.__login)

class GenerateAccessLine:

    def __init__(self):
        self.__lines = ''

    def AddLine(self, value):
        """ Add lines which come out of domain mail system """
        self.__lines = self.__lines + 'From:' + str(value) \
                       + '\tRELAY\n'

    def AddSpamLine(self, value, type):
        """ Add lines which come out of SpamTable """
        self.__lines = self.__lines + str(value) \
                       + '\t%s\n' % type

    def __str__(self):
        return '%s' % self.__lines

class GenerateLhLine:

    def __init__(self):
        self.__lines = ''

    def AddLine(self, value):
        """ Add lines which come out of domain mail system """
        self.__lines = self.__lines + str(value) + '\n'

    def __str__(self):
        return '%s' % self.__lines
        
class GenerateFile:

    """ Class to add lines and write into file """

    def __init__(self, path, filename):
        self.__path = self.CheckPath(path)
        self.__filename = filename
        self.__file = ''
        
    def CheckPath(self, path):
        if path[-1] != '/':
            return path + '/'
        else:
            return path

    def AddLine(self, line):
        self.__line = line
        self.__file = self.__file + '%s' % self.__line

    def Write(self):
        try:
            self.__f = open(self.__path + self.__filename, 'w')
            self.__f.write(self.__file)
            self.__f.close()
        except:
            print 'Error writing file: ', self.__path + self.__filename

    def MakeMap(self, maptype):
        """ Create the hash db file"""
        command = '/usr/sbin/makemap ' + maptype + ' ' + self.__path +  \
                  self.__filename + ' < ' + self.__path + self.__filename
        print command
        try:
            os.system(command)
        except:
            print 'Cannot create hash db file of ', self.__path + self.__filename

    def __str__(self):
        return self.__file


def main(db, query, server, dbType, accessMapType):
    dmerceHome = os.environ['DMERCE_HOME']
    conf = dmerceHome + '/' + 'NCC' + '/' + server['ServerName'] + '/' + 'mail'
    a = DMS.ConfPath.Path(conf)
    a.Create()
    print 'Path where configuration is written ->', conf
    print 'Generating for Server ID -> ', server['ServerID']
    aliases = db.GetAliases(query, server['ServerID'])
    accessList = []
    if aliases:
        af = GenerateFile(conf, 'virtusertable')
        for i in range(len(aliases)):
            """ Set alias, zone, login for each line and add to file """
            a = GenerateAliasLine()
            a.SetAlias(aliases[i]['Alias'])
            a.SetZone(aliases[i]['ZoneName'])
            a.SetLogin(aliases[i]['AccUserLogin'])
            a.CheckToAddress(aliases[i]['ToAddress'])
            if aliases[i]['Access'] == 1:
                accessList.append(a.GetCurrentAlias())
            af.AddLine(str(a))
        af.Write()
    return accessList
        
def createAccess(server, accessMapType, accessList, spamList):
    dmerceHome = os.environ['DMERCE_HOME']
    conf = dmerceHome + '/' + 'NCC' + '/' + server['ServerName'] + '/' + 'mail'
    a = DMS.ConfPath.Path(conf)
    a.Create()
    access = GenerateAccessLine()
    accessFile = GenerateFile(conf, 'access')
    for relay in accessList:
        access.AddLine(relay)
    for spam in spamList:
        access.AddSpamLine(spam['Address'], spam['Name'])
        print spam['Address'], spam['Name']
    accessFile.AddLine(str(access))
    accessFile.Write()
        
def createLocalHostNames(server, localhostnames):
    dmerceHome = os.environ['DMERCE_HOME']
    conf = dmerceHome + '/' + 'NCC' + '/' + server['ServerName'] + '/' + 'mail'
    a = DMS.ConfPath.Path(conf)
    a.Create()
    lh = GenerateLhLine()
    lhFile = GenerateFile(conf, 'local-host-names')
    for l in localhostnames:
        if l['Name'] != '1ci.de':
            lh.AddLine(l['Name'])
    lhFile.AddLine(str(lh))
    lhFile.Write()
    
if __name__ == '__main__':
    lock = DMS.Lock.Lock('dmerce.mail_alias.lock')
    """ Create time string for log entry """
    logTime = time.localtime(time.time())
    print '[%s-%s-%s  %s:%s:%s]\n' % (logTime[0], logTime[1], logTime[2],
                                      logTime[3], logTime[4], logTime[5])
    """ now we have to check, if this script is still running """
    if not lock.CheckLock():
        print 'dns script such running'
        sys.exit()
    lock.AquireLock()
    dmercecfg = DMS.InitDmerce.NCC()
    dmercecfg.InitNCC()
    query = dmercecfg.InitDBConnection()
    db = DBData()
    try:
        accessmaptype = dmercecfg.GetVar('accessmaptype')
        print 'set access map type  to -> ', accessmaptype
    except:
        print 'No configuration "accessmaptype" found. Check dmerce.cfg !!'
        sys.exit()
    try:
        virtusertablemaptype = dmercecfg.GetVar('virtusertablemaptype')
        print 'set virtusertable map type to -> ', virtusertablemaptype
    except:
        print 'No configuration "virtusertablemaptype" found. Check dmerce.cfg !!'
        sys.exit()
    servers = NCCServers.Servers(query)
    data = servers.GetAll()
    for i in data:
        """ go through all active servers """
        if db.CheckService(query, i['ServerID']) == 1:
            """ the server is configured for imap or pop, generate now  """
            spamList = db.GetSpam(query)
            accessList = main(db, query, i, virtusertablemaptype,
                              accessmaptype)
            lhnames = db.GetLocalHostNames(query)
        if lhnames and len(lhnames) > 0:
            createLocalHostNames(i, lhnames)
        if accessList and len(accessList) > 0:
            createAccess(i, accessmaptype, accessList, spamList)
    """ end of script, remove lock file """
    lock.ReleaseLock()
