#!/usr/local/bin/python

import sys
import time
import DMS.MBase

class Domain(DMS.MBase.Class):

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__dboid = DMS.SQL.DBOID(self._sqlSys, self._sql)
        self.__log = open('/opt/dmerce/log/trigger.reseller', 'a')

    def Ti(self):
        """ return local iso time string """
        return time.strftime('%a %b %d %X %Z %Y:', time.localtime(time.time()))

    
    def Insert(self):
        self.__log.write("%sSTART RESELLER TRIGGER\n" % self.Ti())
        var = {}
        var['qDnsZonesSOASerial'] = self._cgi.GetqVar('qDnsZonesSOASerial', 1)
        var['qDnsZonesResellerID'] = self._cgi.GetqVar('qDnsZonesResellerID', 1)
        var['qDnsZonesAgentID'] = self._cgi.GetqVar('qDnsZonesAgentID', 1)
        var['qDnsZonesZoneC'] = self._cgi.GetqVar('qDnsZonesZoneC', 1)
        var['qDnsZonesNS1'] = self._cgi.GetqVar('qDnsZonesNS1', 1)
        var['qDnsZonesNS2'] = self._cgi.GetqVar('qDnsZonesNS2', 1)
        var['qDnsZonesNS3'] = self._cgi.GetqVar('qDnsZonesNS3', 1)
        var['qDnsZonesNS4'] = self._cgi.GetqVar('qDnsZonesNS4', 1)
        var['qDnsZonesName'] = self._cgi.GetqVar('qDnsZonesName', 1)
        var['qDnsRecordsIP'] = self._cgi.GetqVar('qDnsRecordsIP', 1)
        try:
            #self.__log.write("%sgot following template data -> " % self.Ti())
            #self.__log.write("%s" % str(var))
            #self.__log.write("%sINSERT ZONE DATA INTO DB\n" % self.Ti())
            self.InsertZone(var)
            #self.__log.write("%sINSERT RECORD DATA INTO DB\n" % self.Ti())
            self.InsertDefaultRecords(var)
            #self.__log.write("%sSTOP RESELLER TRIGGER\n" % self.Ti())
            return 1
        except:
            return None


    def InsertZone(self, var):
        self.__ZoneID = self.__dboid['DnsZones']
        stmt = "INSERT INTO DnsZones " \
               "(ID, AgentID, ResellerID, Name, SOASerial, SOASubserial, " \
               " ZoneC, NS1, NS2, NS3, NS4, active, LocalSendmail) " \
               "VALUES " \
               "(" \
               " '%s', " % str(self.__ZoneID) + \
               " '%s',"  % str(var['qDnsZonesAgentID']) + \
               " '%s',"  % str(var['qDnsZonesResellerID']) + \
               " '%s',"  % str(var['qDnsZonesName']) + \
               " '%s',"  % str(var['qDnsZonesSOASerial']) + \
               " '00',"  \
               " '%s',"  % str(var['qDnsZonesZoneC']) + \
               " '%s',"  % str(var['qDnsZonesNS1']) + \
               " '%s',"  % str(var['qDnsZonesNS2']) + \
               " '%s',"  % str(var['qDnsZonesNS3']) + \
               " '%s',"  % str(var['qDnsZonesNS4']) + \
               " 1," \
               " 1"  \
               " )"
        rc, r = self._sql[stmt]

    def InsertDefaultRecords(self, var):
        # first we insert the mx record
        #self.__log.write("%sINSERT MX RECORD\n" % self.Ti())
        stmt = "INSERT INTO DnsRecords " \
               "(ID, ZoneID, active, Name, Type, Value, ToDo) " \
               "VALUES " \
               "( " \
               "'%s'," % str(self.__dboid['DnsRecords']) + \
               "'%s'," % str(self.__ZoneID) + \
               " 1," + \
               "''," + \
               " 5," + \
               "'10 mail.%s.'," % str(var['qDnsZonesName']) + \
               " 1" + \
               ")"
        rc, r = self._sql[stmt]
        # now we insert the wildcard record as A
        #self.__log.write("%sINSERT WILDCARD RECORD\n" % self.Ti())
        stmt = "INSERT INTO DnsRecords " \
               "(ID, ZoneID, active, Name, Type, Value, ToDo) " \
               "VALUES " \
               "( " \
               "'%s'," % str(self.__dboid['DnsRecords']) + \
               "'%s'," % str(self.__ZoneID) + \
               " 1," + \
               "'@'," + \
               " 6," + \
               "'%s'," % str(var['qDnsRecordsIP']) + \
               " 1" + \
               ")"
        rc, r = self._sql[stmt]
        # now we insert the mail record as A
        #self.__log.write("%sINSERT MAIL RECORD\n" % self.Ti())
        stmt = "INSERT INTO DnsRecords " \
               "(ID, ZoneID, active, Name, Type, Value, ToDo) " \
               "VALUES " \
               "( " \
               "'%s'," % str(self.__dboid['DnsRecords']) + \
               "'%s'," % str(self.__ZoneID) + \
               " 1," + \
               "'mail'," + \
               " 6," + \
               "'%s'," % str(var['qDnsRecordsIP']) + \
               " 1" + \
               ")"
        rc, r = self._sql[stmt]
        # now we insert the www record as CNAME
        #self.__log.write("%sINSERT WWW RECORD\n" % self.Ti())
        stmt = "INSERT INTO DnsRecords " \
               "(ID, ZoneID, active, Name, Type, Value, ToDo) " \
               "VALUES " \
               "( " \
               "'%s'," % str(self.__dboid['DnsRecords']) + \
               "'%s'," % str(self.__ZoneID) + \
               " 1," + \
               "'www'," + \
               " 7," + \
               "'mail'," + \
               " 1" + \
               ")"
        rc, r = self._sql[stmt]
        #self.__log.write("%sSTOP RECORD HANDLING\n" % self.Ti())
    
                  
