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
import DMS.InitDmerce
import vars
import DMS.Lock
import NCCServers
import DMS.ConfPath

vars.vars()

class DBData:
    def GetAliases(self, query, what):
        what = string.lower(what)
        if what == 'a':
            rc, r = query["SELECT MailAliases.Alias, MailAliases.ToAddress, DnsZones.Name, " + \
                          "AccUser.Login FROM MailAliases, AccUser, DnsZones " + \
                          "WHERE MailAliases.DnsZonesID = DnsZones.ID AND MailAliases.active = 1 " + \
                          "AND MailAliases.AccUserID = AccUser.ID " + \
                          "ORDER BY DnsZones.Name"]
        if what == 'o':
            rc, r = query["SELECT MailAliases.ToAddress FROM MailAliases WHERE MailAliases.active = 1 " + \
                          "GROUP BY MailAliases.ToAddress"]

        return r

        
class Mailing:
    def __init__(self, r, what):
        self.__what = string.lower(what)
        self.__alias = self.SetAlias(r)
        self.__zone = self.SetZone(r)
        self.__toAddress = self.SetToAddress(r)
        self.__login = self.SetLogin(r)

    def SetAlias(self, value):
        return value['Alias']

    def GetAlias(self):
        return self.__alias

    def SetZone(self, value):
        return value['Name']

    def GetZone(self):
        return self.__zone

    def SetToAddress(self, value):
        return value['ToAddress']

    def GetToAddress(self):
        return self.__toAddress

    def SetLogin(self, value):
        return value['Login']

    def GetLogin(self):
        return self.__login

    def __str__(self):
        if string.count(self.__toAddress,'@') == 1 or self.__toAddress != '':
            return "%s@%s,%s,(alias or forwarding)" % (self.__alias, self.__zone, self.__toAddress)
        elif self.__toAddress == '':
            return "%s@%s,%s,(local account)" % (self.__alias, self.__zone, self.__login)

class File:
    def __init__(self, what):
        self.__dir = ''
        self.__filename = ''
        if what == 'a':
            self.__lines = 'Server Alias\t->\tWeiterleitung / Account\n-------------------------------------\n'
        if what == 'o':
            self.__lines = 'Liste aller Weiterleitungen\n-----------------------------\n'
    def SetDir(self, value):
        if value == '':
            self.__dir = './'
        else:
            self.__dir = value

    def GetDir(self):
        return self.__dir

    def SetFilename(self, value):
        self.__filename = value

    def GetFilename(self):
        return self.__filename

    def AddLine(self, value):
        self.__lines = self.__lines + value + '\n'

    def GetLines(self):
        return self.__lines

    def Write(self):
        if self.__filename != '':
            f = open(self.__dir + '/' + self.__filename, 'w')
            f.write(self.__lines)
            f.close()

def main(query, dir, filename, zone, what):
    db = DBData()
    aliasResult = db.GetAliases(query, what)
    print aliasResult
    for i in aliasResult:
        print i['Name'] 
    file = File(what)
    file.SetDir(dir)
    file.SetFilename(filename)
    if what == 'a':
        for i in aliasResult:
            mail = Mailing(i, what)
            if str(mail):
                file.AddLine(str(mail))
    if what == 'o':
        for i in aliasResult:
            if '@' in str(i['ToAddress']):
                file.AddLine(str(i['ToAddress']))
                
    print file.GetLines()
    file.Write()

if __name__=='__main__':
    lock = DMS.Lock.Lock('dmerce.maillist.lock')
    """ Create time string for log entry """
    logTime = time.localtime(time.time())
    print '[%02i-%02i-%02i  %02i:%02i:%02i]\n\n' % (logTime[0], logTime[1], logTime[2],
                                                    logTime[3], logTime[4], logTime[5])
    """ now we have to check, if this script is still running """
    if not lock.CheckLock():
        print 'maillist script such running'
        sys.exit()
    lock.AquireLock()
    dmercecfg = DMS.InitDmerce.NCC()
    dmercecfg.InitNCC()
    query = dmercecfg.InitDBConnection()
    dir = string.strip(raw_input('Please enter full path to save file in / or leave it empty for cur dir: '))
    file = string.strip(raw_input('Please enter name of file to write data in: '))
    what = string.strip(string.lower(raw_input('(a = all data / o = only divider): ')))
    zone = ''
    if file == '':
        print 'Sorry, minimum is to define a filename !!'
        sys.exit()
    else:
        main(query, dir, file, zone, what)
    lock.ReleaseLock()
