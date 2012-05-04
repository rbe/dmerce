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

""" import dmerce modules"""
import DMS.SysTbl
import Guardian.Config
import DMS.SQL
import DMS.InitDmerce
import NCCServers
import vars


vars.vars()


def ti():
    """ return local iso time string """
    return time.strftime('%a %b %d %X %Z %Y:', time.localtime(time.time()))

class DBData:

    def __init__(self, query):
        self.__q = query
        self.__dboid = DMS.SQL.DBOID(self.__q, self.__q)

    def CheckRecord(self, name, type, zoneID):
        stmt = "SELECT Name, Type, Value, ZoneID FROM DnsRecords WHERE " \
               "Name = '%s' AND ZoneID = '%s'" % (name, str(zoneID))
        rc, r = self.__q[stmt]
        return r

    def GetZoneID(self, name):
        stmt = "SELECT ID FROM DnsZones WHERE Name = '%s'" % name
        rc, r = self.__q[stmt]
        if r:
            return r[0]['ID']
        else:
            return None

    def GetAgentData(self, login):
        stmt = "SELECT ID, NS1, NS2, NS3, NS4, ZoneC FROM Agent " \
               "WHERE Login = '%s'" % login
        rc, r = self.__q[stmt]
        return r

    def InsertZone(self, name, agent):
        dboid = self.__dboid['DnsZones']
        stmt = "INSERT INTO DnsZones (ID, Name, AgentID, NS1, NS2, NS3, NS4, ZoneC) VALUES " \
               "('%s', '%s', %i, %i, %i, %i, %i, '%s')" % (dboid, name, agent['ID'], agent['NS1'], agent['NS2'],
                                                           agent['NS3'], agent['NS4'], agent['ZoneC'])
        rc, r = self.__q[stmt]
        return dboid
    
    def UpdateRecord(self, name, type, value, zoneID):
        stmt = "UPDATE DnsRecords SET Value = '%s' WHERE " % (value) + \
               "Name = '%s' AND type = '%s' AND ZoneID = '%s'" %(name, str(type), str(zoneID))
        rc, r = self.__q[stmt]

    def InsertRecord(self, name, type, value, zoneID):
        dboid = self.__dboid['DnsRecords']
        stmt = "INSERT INTO DnsRecords (ID, Name, Type, Value, ZoneID, active) " \
               "VALUES " \
               "('%s', '%s', '%s', '%s', '%s', 1)" % (dboid, name, type, value, zoneID)
        rc, r = self.__q[stmt]


class Zone:
    
    def __init__(self, name, query, agent):
        self.__zn = name
        self.__aRecords = []
        self.__cnRecords = []
        self.__mxRecords = []
        self.__db = DBData(query)
        self.__agent = agent
        
    def ProcessRecords(self):
        print ti(), "PROCESSING RECORDS FOR ZONE '%s'" % self.__zn
        """ process mx records """
        print ti(), "PROCESS MX RECORDS"
        for i in self.__mxRecords:
            if self.__db.CheckRecord('', 5, self.__zoneID) or self.__db.CheckRecord(' ', 5, self.__zoneID):
                print ti(), "UPDATE MX RECORD SET VALUE TO '%s'" % i[len(i) - 1]
                self.__db.UpdateRecord('', 5, i[len(i) - 1], self.__zoneID)
            else:
                print ti(), "INSERT NEW MX RECORD WITH VALUE '%s'" % i[len(i) - 1]
                self.__db.InsertRecord('', 5, i[len(i) - 1], self.__zoneID)
        """ process a records """
        print ti(), "PROCESS A RECORDS"
        for i in self.__aRecords:
            if self.__db.CheckRecord(i[0], 6, self.__zoneID):
                print ti(), "UPDATE A RECORD '%s'" % i[0]
                self.__db.UpdateRecord(i[0], 6, i[len(i) - 1], self.__zoneID)
            else:
                print ti(), "INSERT NEW A RECORD '%s'" % i[0]
                self.__db.InsertRecord(i[0], 6, i[len(i) - 1], self.__zoneID)
        """ process cname records """
        print ti(), "PROCESS CNAME RECORDS"
        for i in self.__cnRecords:
            if self.__db.CheckRecord(i[0], 7, self.__zoneID):
                print ti(), "UPDATE CNAME RECORD '%s'" % i[0]
                self.__db.UpdateRecord(i[0], 7, i[len(i) - 1], self.__zoneID)
            else:
                print ti(), "INSERT NEW CNAME RECORD '%s'" % i[0]
                self.__db.InsertRecord(i[0], 7, i[len(i) - 1], self.__zoneID)

    def CheckZone(self):
        ok = 0
        if self.__db.GetZoneID(self.__zn):
            self.__zoneID = self.__db.GetZoneID(self.__zn)
            print ti(), "FOUND ZONE '%s' WITH ID '%s'" % ( self.__zn, str(self.__zoneID))
            ok = 1
        else:
            print ti(), "ZONE '%s' NOT FOUND IN DATABASE" % (self.__zn)
            agentData = self.__db.GetAgentData(self.__agent)
            if agentData:
                print ti(), "FOUND Agent '%s', CREATING ZONE" % self.__agent
                self.__zoneID = self.__db.InsertZone(self.__zn, agentData)
                ok = 1
            else:
                print ti(), "AGENT NOT '%s' FOUND" % self.__agent
                ok = 0
            print ti(), "INSERTED ZONE '%s' WITH ID '%s'" % (name, str(self.__zoneID))
        return ok

    def SetData(self, data):
        self.__data = data

    def FilterData(self):
        self.__filter = []
        for i in self.__data:
            self.__filter.append(string.split(string.strip(i), '\t'))
        for i in self.__filter:
            if 'CNAME' in i:
                if len(i) > 1:
                    self.__cnRecords.append(i)
            if 'MX' in i:
                if len(i) > 1:
                    self.__mxRecords.append(i)
            if 'IN' in i and 'A' in i:
                if i[0] != 'localhost' and len(i) > 1:
                    self.__aRecords.append(i)

    def GetARecords(self):
        return self.__aRecords

    def GetMXRecords(self):
        return self.__mxRecords

    def GetCNRecords(self):
        return self.__cnRecords

    def GetFilterData(self):
        return self.__filter

    def HandleData(self):
        db = DBData()


class File:

    def __init__(self, path, name):
        self.__name = name
        self.__path = path

    def Read(self):
        return open(self.__path +  '/' + self.__name, 'r').readlines()

def main(dir, agent):
    dmercecfg = DMS.InitDmerce.NCC()
    dmercecfg.InitNCC()
    query = dmercecfg.InitDBConnection()
    files = os.listdir(dir)
    for i in files:
        f = File(dir, i)
        data = f.Read()
        z = Zone(i, query, agent)
        z.SetData(data)
        z.FilterData()
        if z.CheckZone():
            print ti(), "*** START OF PROCESSING FILE: '%s'" % i
            z.ProcessRecords()
            print ti(), "*** END OF PROCESSING FILE: '%s'" % i
        else:
            print ti(), "!!! ZONE '%s' COULD NOT BE CREATED: Agent NOT in DATABASE !!!"

if __name__ == '__main__':
    dir = sys.argv[1]
    agent = sys.argv[2]
    main(dir, agent)
