#!/usr/local/bin/python1.5

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
import DMS.Whois

vars.vars()

def ti():
    """ return local iso time string """
    return time.strftime('%a %b %d %X %Z %Y:', time.localtime(time.time()))

class DBData:

    def __init__(self, q):
        self.__q = q
        
    def GetAllZones(self):
        stmt = "SELECT ID, Name FROM DnsZones"
        rc, r = self.__q[stmt]
        return r

    def UpdateZoneInfo(self, info, id):
        stmt = "UPDATE DnsZones SET WhoisInfo = '%s' WHERE ID = '%s'" %(info, id)
        rc, r = self.__q[stmt]


def check(file):
    d = {}
    try:
        dmerce_home = os.environ['DMERCE_HOME']
        cp = ConfigParser.ConfigParser()
        cp.read('%s/dmerce.cfg' % dmerce_home)
        d['Sys'] = cp.get('q', 'Sys')
        return d
    except:
        file.write('ERROR: environment variables not correct or missing entries in $DMERCE_HOME/dmerce.cfg!\n')
        file.write('ERROR: need $DMERCE_HOME, $DMERCE_HOME is NOW -> %s\n' % dmerce_home)
        file.write('ERROR: need entries in dmerce.cfg for: Sys, majordomolistdir, majordomolistname\n')
        file.write('ERROR:                                \n')
        sys.exit()


def cleanInfo(info):
    blackList = ['%','&','$','"',"'",'/','\\','§','?', '(', ')']
    for i in blackList:
        info =  string.replace(info, i, ' ')
    return info
        
def doOne(db, log, zone):
    wh = DMS.Whois.Whois()
    domains = zone
    for i in domains:
        log.write("%sGET WHOIS INFORMATION FOR : %s\n" % (ti(), i['Name']))
        info = wh.Get(None, i['Name'])
        log.write("%sINSERT WHOIS INFORMATION INTO DB\n" % ti())
        db.UpdateZoneInfo(cleanInfo(info), i['ID'])

def do(db, log):
    wh = DMS.Whois.Whois()
    domains = db.GetAllZones()
    for i in domains:
        log.write("%sGET WHOIS INFORMATION FOR : %s\n" % (ti(), i['Name']))
        info = wh.Get(None, i['Name'])
        log.write("%sINSERT WHOIS INFORMATION INTO DB\n" % ti())
        db.UpdateZoneInfo(cleanInfo(info), i['ID'])
    log.close()
            
""" Main section """
def main(zone = []):
    log = open("/opt/dmerce/log/whois_trigger.log" , "a")
    #log = open("/tmp/whois_trigger.log" , "a")
    try:
        conf = check(log)
    except:
        log.write('%sERROR READING dmerce.cfg\n' % ti())
        conf = None
    try:
        if conf:
            c = DMS.SQL.Layer1(conf['Sys'])
            log.write('%sUSING DMERCE.CFG ENTRIES\n' %ti())
        else:
            c = DMS.SQL.Layer1('MySQL:dmerce_ncc:lk(/$8@localhost:dmerce_ncc')
            log.write('%sNCC HARDCODED CONNECTION\n' %ti())
        c.Init()
        c.Connect()
        query = c.GetQuery()
    except:
        log.write('%sERROR WHILE CREATING INSTANCE TO DATABASE\n' % ti())
        log.write('%sSTOP PROGRAM\n\n' % ti())
        sys.exit()
    db = DBData(query)
    try:
        if len(zone) == 0:
            log.write("%sNOW START FUNCTION FOR ALL DOMAINS\n" % ti())
            do(db, log)
            log.write("%sFINISHED UPDATING ALL DOMAINS\n" % ti())
            return 1
        else:
            log.write("%sNOW START FUNCTION FOR ONE DOMAIN\n" % ti())
            doOne(db, log, zone)
            log.write("%sFINISHED UPDATING ONE DOMAIN\n" % ti())
            return 1
    except:
        log.write("%sAN ERROR OCCURRED WHILE FUNCTION DO\n" % ti())
        return None
    
#main()
