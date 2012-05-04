#!/usr/bin/env python
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
from DNSSerial import Serial
import DMS.InitDmerce
import DMS.SnmpMac
import vars


class ReloadFile:
    def __init__(self):
        self.__head = '#!/bin/sh\n'
        self.__file = ''
        self.__scp = ''

    def AddSCP(self, agent, path, ns, zone, basedir):
        self.__scp = self.__scp + 'scp ka@213.128.150.1:%s%s/%s/pri/%s %s/%s/%s/pri/\n' %(path,
                                                                                          agent,
                                                                                          ns,
                                                                                          zone,
                                                                                          basedir,
                                                                                          agent,
                                                                                          ns)
        
    def AddZone(self, ns, zone):
        self.__file = self.__file + '/usr/sbin/ndc -c /var/run/ndc.%s reload %s\n' %(ns, zone)
        
    def Write(self, agent, path, ns):
        self.__all = self.__head + '\n' + \
                     self.__scp + 'sleep 4\n' + \
                     self.__file
        f = open('%s%s/%s/ncc-ndc-reload.sh' %(path, agent, ns), 'w')
        #print '%s%s/%s/ncc-ndc-reload.sh' %(path, agent, ns)
        f.write(self.__all)
        #print self.__all

    def Create(self, priZones, agent, ns, confPath, changedZones, db, query, rrZones, basedir):
        ser = Serial()
        for j in range(len(priZones)):
            if priZones[j]['ZoneName'] in changedZones:
                newSerial, newSubSerial = ser.Check(priZones[j]['SOASerial'],
                                                    priZones[j]['SOASubserial'])
                self.AddZone( ns, priZones[j]['ZoneName'])
                self.AddSCP(agent, confPath, ns, priZones[j]['ZoneName'], basedir)
                db.SetToDoFlag(query, priZones[j]['ZoneName'], newSerial, newSubSerial)
        for zone in rrZones:
            if zone not in changedZones:
                self.AddZone(ns, zone)
                self.AddSCP(agent, confPath, ns, zone, basedir)
        self.Write(agent, confPath, ns)
