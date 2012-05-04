#!/usr/bin/env python
#
""" Import needed moduels """
import sys
import os
import os.path
import time
import string
from DNSRRecords import RR

class Zone:
    """ Class to handle SOA-records """
    def __init__(self):
        self.__zone = None
        
    def SetTTL(self, ttl):
        """ Check ttl """
        if ttl == '' or ttl == 0 or ttl == '0' or not ttl:
            self.__ttl = '3h'
        else:
            self.__ttl = ttl

    def SetZoneName(self, zoneName):
        """ Check zoneName """
        if zoneName[-1:] != '.':
            self.__zoneName = zoneName + '.'
        else:
            self.__zoneName = zoneName
            
    def SetPrimaryNs(self, primaryNs):
        """ Check primaryNs """
        if primaryNs[-1:] != '.':
            self.__primaryNs = primaryNs + '.'
        else:
            self.__primaryNs = primaryNs

    def SetZoneC(self, zoneContact):
        """ Check zoneContact """
        if string.find(zoneContact, '@') > 0:
            zoneContact = string.replace(zoneContact, '@', '.')
        if zoneContact[-1:] != '.':
            zoneContact = zoneContact + '.'
        self.__zoneC = zoneContact

    def SetSerial(self, serial, sub):
        """ Check serial """
        if int(sub) <= 9:
            self.__serial = str(serial) + "0" + str(int(sub))
        else:
            self.__serial = str(serial) + str(sub)

    def SetRefresh(self, info):
        """ Check refresh """
        if not info or info == '':
            info = '24h' # set refresh to default
        self.__refresh = info
    
    def SetRetry(self, info):
        """ Check retry """
        if not info or info == '':
            info = '2h' # set retry to default
        self.__retry = info

    def SetExpire(self, info):
        """ Check expire """
        if not info or info == '':
            info = '30d' # set expire to default
        self.__expire = info

    def SetNegativeCacheTTL(self, info):
        """ Check default ttl """
        if not info or info == '':
            info = '4d' # set negative cache ttl to default
        self.__negTTL = info

    def SetHeader(self):
        """ Return SOA record in a formatted form """
        if self.__zoneName and self.__primaryNs and self.__zoneC:
            self.__zone = '$TTL %s \n%s \t IN \t SOA \t %s \t %s ( \n \t \t %s \t ; serial ' \
                          '\n \t \t %s \t \t ; refresh '\
                          '\n \t \t %s \t \t ; retry '\
                          '\n \t \t %s \t ; expire '\
                          '\n \t \t %s ) \t \t ; Negative caching TTL\n\n' \
                          %(self.__ttl, self.__zoneName, self.__primaryNs, self.__zoneC,
                            self.__serial, self.__refresh, self.__retry, self.__expire, self.__negTTL)
        else:
            self.__zone = None

    def AddRR(self, rr):
        if rr:
            self.__zone = self.__zone + rr

    def __str__(self):
        if self.__zone:
            return '%s' % self.__zone
        else:
            return None
        
    def __repr__(self):
        return 'SOA("%s", "%s", "%s", %s)' %(self.__zoneName, self.__primaryNs,
                                             self.__zoneContact,
                                             [self.__serial, self.__refresh, self.__retry,
                                              self.__expire, self.__negativeCacheTTL])

class ZoneData:
    def __init__(self, agents, priZones, rr, nsList):
        """ Constructor """
        self.__agents = agents
        self.__priZones = priZones
        self.__records = rr
        self.__nsList = nsList

    def CreateData(self):
        self.__zone = Zone()
        """ go through primary zones -> create and write them """
        self.__zone.SetTTL(self.__priZones['SOAMaximum'])
        self.__zone.SetZoneName(self.__priZones['ZoneName'])
        self.__zone.SetPrimaryNs(self.__agents['NS_Name'])
        self.__zone.SetZoneC(self.__priZones['ZoneC'])
        self.__zone.SetSerial(self.__priZones['SOASerial'], self.__priZones['SOASubserial'])
        self.__zone.SetRefresh(self.__priZones['SOARefresh'])
        self.__zone.SetRetry(self.__priZones['SOARetry'])
        self.__zone.SetExpire(self.__priZones['SOAExpire'])
        self.__zone.SetNegativeCacheTTL(self.__priZones['TTL'])
        self.__zone.SetHeader()
        for ns in self.__nsList:
            self.__zone.AddRR('\tIN\tNS\t%s.\n' % ns['NS_Name'])
        for j in range(len(self.__records)):
            self.__zone.AddRR(str(RR(self.__records[j]['RecordName'],
                                     self.__records[j]['RType'],
                                     self.__records[j]['RecordValue'])))
        self.__zone.AddRR('localhost\tIN\tA\t127.0.0.1\n')
    def __str__(self):
        return str(self.__zone)

