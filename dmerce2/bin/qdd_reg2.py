#!/usr/bin/env python

import sys
import vars
vars.vars()
import os
import time
import string
import ConfigParser
import DMS.Time
import DMS.SQL
import DMS.IP
import urllib
import DMS.Lock

class VitradoBilling:
    def __init__(self, url):
        self.__url = url
        self.__typ = 'vitrado_01'
        self.__proName = ''           # we can choose the name
        self.__bAktion = 'B'          # harcoded vitrado varible
        self.__pWert = ''             # praemie, die noch berechnet werden muss
        self.__pTyp = 'umsatz'        # hardcoded vitrado variable
        self.__proPartnerID = ''      # our partner id
        self.__offerID = '60'         # id of offer person
        self.__aVgnr = ''             # Vorgangsnummer
        self.__aVname = ''            # alternativ zur vorgangsnummer der Vorname des Kunden
        self.__aNname = ''            # alternativ zur vorgangsnummer der Nachname des Kunden

    def CheckURL(self):
        try:
            urllib.urlopen(self.__url)
        except:
            print 'Error opening URL: ', self.__url
            return None
        
    def SetProjectID(self, proID):
        self.__proID = proID

    def SetDateTime(self):
        self.__bDate = ''
        self.__bTime = ''
        a = time.localtime(time.time())
        self.__bDate = self.__bDate + '%04i%02i%02i' %(a[0], a[1], a[2])
        self.__bTime = self.__bTime + '%02i:%02i:%02i' %(a[3], a[4], a[5])

    def CreateGetRequest(self):
        self.__req = '?'
        self.__req = self.__req + 'typ=' + self.__typ
        self.__req = self.__req + '&b_date=' + self.__bDate
        self.__req = self.__req + '&b_time=' + self.__bTime
        self.__req = self.__req + '&b_aktion=' + self.__bAktion
        self.__req = self.__req + '&p_wert=' + str(int(self.__pWert))
        self.__req = self.__req + '&p_typ=' + self.__pTyp
        self.__req = self.__req + '&pro_id=' + self.__proID
        self.__req = self.__req + '&pro_name=' + self.__proName
        self.__req = self.__req + '&pro_partnerid=' + self.__proPartnerID
        self.__req = self.__req + '&pro_anbieterid=' + self.__offerID
        self.__req = self.__req + '&a_vgnr=' + str(self.__aVgnr)
        self.__req = self.__req + '&a_vname=' + self.__aVname
        self.__req = self.__req + '&a_nname=' + self.__aNname

    def SetproName(self, value):
        self.__proName = value

    def SetproPartnerID(self, value):
        self.__proPartnerID = value

    def SetaVgnr(self, value):
        self.__aVgnr = value

    def SetaVname(self, value):
        self.__aVname = value

    def SetaNname(self, value):
        self.__aNname = value

    def GetbAktion(self):
        return self.__bAktion

    def GetbDate(self):
        return self.__bDate

    def GetbTime(self):
        return self.__bTime
        
    def GetTyp(self):
        return self.__typ
        
    def GetpTyp(self):
        return self.__pTyp

    def GetproName(self):
        return self.__proName

    def GetproPartnerID(self):
        return self.__proPartnerID

    def GetproAnbieterID(self):
        return self.__offerID

    def GetaVgnr(self):
        return self.__aVgnr

    def GetaVname(self):
        return self.__aVname

    def GetaNname(self):
        return self.__aNname

    def GetpWert(self):
        return self.__pWert

    def GetproID(self):
        return self.__proID
    
    def CalcPrice(self, price):
        self.__pWert = float(price) * 12 * 20 

    def SendGetRequest(self):
        try:
            a = urllib.urlopen(str(self.__url) + str(self.__req))
            return a.readlines()
        except IOError, msg:
            return msg

    def GetURL(self):
        return self.__url

    def GetRequest(self):
        return self.__req

class NewScratch:

    def __init__(self, conf):
        self.__conf = conf
        tconv = DMS.Time.Conversion({})
        self.__tPlus15Days = float(tconv.Calc(day = 15, timestamp = 1))
        self.__scratch = None
        self.__projectName = None
        self.__projectIpAddr = None
        self.__sql = None
        self.__rqID = None
        self.__localSql = None

    def SetLocalSQL(self, sql):
        self.__localSql = sql

    def SetSQL(self, sql):
        self.__sql = sql
        self.__dboid = DMS.SQL.DBOID(sqlSys = sql, sql = sql)

    def SetNCCSQL(self, sql):
        self.__nccSql = sql
        self.__nccDboid = DMS.SQL.DBOID(sqlSys = sql, sql = sql)
    
    def SetScratch(self, scratch):
        self.__scratch = scratch

    def SetIP(self, ipaddr):
        self.__projectIpAddr = ipaddr

    def SetRequestID(self, id):
        self.__rqID = id

    def GetIP(self):
        return self.__projectIpAddr

    def SetProjectDbType(self, pdt):
        self.__projectDbType = pdt
        
    def GetProjectDbType(self):
        return self.__projectDbType

    def SetDbPwd(self, pdp):
        self.__dbPwd = pdp

    def GetDbPwd(self):
        return self.__dbPwd

    def SetProjectDbName(self, pdn):
        self.__projectDbName = pdn

    def GetProjectDbName(self):
        return self.__projectDbName

    def SetHostSerial(self, hs):
        self.__projectHostSerial = hs

    def GetHostSerial(self):
        return self.__projectHostSerial

    def SetProjectFQHN(self, fqhn):
        self.__projectFQHN = fqhn

    def GetProjectFQHN(self):
        return self.__projectFQHN

    def SetProjectDestDir(self, destDir):
        self.__projectDestDir = destDir

    def GetProjectDestDir(self):
        return self.__projectDestDir

    def SetProjectName(self, s):
        s = string.replace(s, 'ä', 'ae')
        s = string.replace(s, 'ö', 'oe')
        s = string.replace(s, 'ü', 'ue')
        s = string.strip(s)
        s = s[:8]
        items = [' ', '/',]
        for i in items:
            s = string.replace(s, i, '')
        stmt = "SELECT PROJECTNAME FROM ProjectNames WHERE ProjectName LIKE '%s%%'" % s[0]
        rc, r = self.__sql[stmt]
        if rc:
            rcs = len(str(rc))
            s = s[:len(s) - rcs] + string.replace(str(rc),'L','')
        self.__sql["INSERT INTO ProjectNames (ID, ProjectName) VALUES (%i, '%s')" % (self.__nccDboid['ProjectNames'], s)]
        self.__projectName = s

    def SetDNSRecordName(self, record):
        self.__recordName = record

    def GetDNSRecordName(self):
        return self.__recordName

    def GetProjectName(self):
        return self.__projectName

    def ActivateEmail(self, fqhn, email, id):
        query = "toAddr=%s&" % email
        query = query + "fromAddr=register@1ci.de&"
        query = query + "subject=Anmeldebestaetigung&"
        query = query + "fqhn=%s&ManageProjectsID=%s" % (fqhn, id)
        dboid = self.__dboid['SendEmail']
        s = "INSERT INTO dmerce_sys.SendEmail (ID, FQHN, Template, Query, ToDo, PrjDocRoot) VALUES ('%s', 'www.register.1ci.de', 'Email,email_scratch_okay', '%s', 1, '/export/honey/www/1ci.de/register/www')" % (str(dboid), query)
        self.__sql[s]

        query = "toAddr=register@1ci.de&"
        query = query + "fromAddr=register@1ci.de&"
        query = query + "subject=Anmeldebestaetigung&"
        query = query + "fqhn=%s&ManageProjectsID=%s" % (fqhn, id)
        dboid = self.__dboid['SendEmail']
        s = "INSERT INTO dmerce_sys.SendEmail (ID, FQHN, Template, Query, ToDo, PrjDocRoot) VALUES ('%s', 'www.register.1ci.de', 'Email,email_scratch_okay', '%s', 1, '/export/honey/www/1ci.de/register/www')" % (str(dboid), query)
        self.__sql[s]

    def UpdateDatabase(self):
        t = time.time()
        s = "INSERT INTO %s.Agent (ID, CreatedDateTime, active, Login, passwd) VALUES (1, %f, 1, 'demo', '%s')" % (self.__projectDbName, t, self.__projectDbName + str(os.getpid()))
        self.__localSql[s]
        dnsRecordId = self.__nccDboid['DnsRecords']
        s = "INSERT INTO dmerce_ncc.DnsRecords (ID, ZoneID, active, Name, Type, Value) VALUES (%i, 4, 1, '%s', 6, '%s')" % (dnsRecordId, self.__recordName, self.__projectIpAddr)
        self.__sql[s]
        self.__vhostID = self.__nccDboid['SrvWebsrvVhosts']
        s = "INSERT INTO dmerce_ncc.SrvWebsrvVhosts (ID, active, ToDo, ServerID, SrvWebsrvConfigID, AccessLogType, DmerceVersion, ServerAdmin, DnsZoneID, DnsRecordID) VALUES (%s, 1, 1, 12, 3, 2, 4, 'webmaster@customer.1ci.de', 4, %i)" % (self.__vhostID, dnsRecordId)
        self.__sql[s]
        s = "UPDATE dmerce_sys.ManageProjects SET ToDo = 0, ChangedDateTime = %f, VALIDFROM = %f, VALIDUNTIL = %f, EMAILSENDDATE = %f, PROJECTIP = '%s', PROJECTNAME = '%s', PROJECTDBNAME = '%s', nccVhostID = '%s' WHERE ID = '%s'" % (t, t, self.__tPlus15Days, t, self.__projectIpAddr, self.__projectName, self.__projectDbName, self.__vhostID, str(self.__rqID))
        self.__sql[s]
        s = "UPDATE dmerce_ncc.DnsZones SET ToDo = 1 WHERE Name = '1ci.de'"
        self.__sql[s]

    def SetToDo(self, id):
        s = "UPDATE dmerce_sys.ManageProjects SET ToDo = 0, nccVhostID = '%s' WHERE ID = %i" % (self.__vhostID, id)
        self.__sql[s]

    def Scratch(self):
        return os.system('%s/bin/dmerce crprj quiet=1 scratch=%s db=%s mysqlrootpwd=%s hostserial=%s projectfqhn=%s projectdestdir=%s projectdbname=%s projectuploaddir=/opt/dmerce/websites/%s/pic/upload' % (self.__conf['dmerce_home'], self.__scratch, self.__projectDbType, self.__dbPwd, self.__projectHostSerial, self.__projectFQHN, self.__projectDestDir, self.__projectDbName, self.__projectDestDir))

def welcome():
    print vars.COPYRIGHT_SCRIPT
    print 'REGISTER DMERCE PROJECTS'
    print

def t():
    return time.strftime('%a %b %d %X %Z %Y:', time.localtime(time.time()))

def check():
    d = {}
    try:
        dmerce_home = os.environ['DMERCE_HOME']
        dmerce_websites = os.environ['DMERCE_WEBSITES']
        cp = ConfigParser.ConfigParser()
        cp.read('%s/dmerce.cfg' % dmerce_home)
        d['dmerce_home'] = dmerce_home
        d['db'] = cp.get('reg', 'db')
        d['pwd'] = cp.get('reg', 'pwd')
        d['ipnet'] = cp.get('reg', 'ipnet')
        d['hostserial'] = cp.get('reg', 'hostserial')
        d['domainprefix'] = cp.get('reg', 'domainprefix')
        d['domainsuffix'] = cp.get('reg', 'domainsuffix')
        d['projectdestdir'] = cp.get('reg', 'projectdestdir')
        d['dnssuffix'] = cp.get('reg', 'dnssuffix')
        d['nccConnStr'] = cp.get('q', 'Sys')
        return d
    except:
        print 'ERROR: environment variables not correct or missing entries in $DMERCE_HOME/dmerce.cfg!'
        print 'ERROR: need $DMERCE_HOME'
        print 'ERROR: need entries in dmerce.cfg for: db, pwd, ipnet, hostserial, domainprefix, domainsuffix,'
        print 'ERROR:                                 projectdestdir'
        sys.exit()

def main():
    conf = check()
    # connect to database
    c = DMS.SQL.Layer1('MySQL:dmerce_sys:dmerce_sys@honey.hamburg2.de.1ci.net:dmerce_sys')
    c.Init()
    c.Connect()
    q = c.GetQuery()
    # connect to local database for scratching
    cl = DMS.SQL.Layer1('MySQL:root:%v4h5f9@localhost:dmerce_sys')
    cl.Init()
    cl.Connect()
    ql = cl.GetQuery()
    # connect to NCC Master Server    
    ncc = DMS.SQL.Layer1(conf['nccConnStr'])
    ncc.Init()
    ncc.Connect()
    nccQ = ncc.GetQuery()
    # read new requests from database dmerce_sys.ManageProjects
    s = "SELECT ManageProjects.ID AS RequestID, ManageProjects.Email, ManageProjects.VITRADORESELLERID, ManageProjects.ProjectType, ManageProjects.LastName, ManageProjects.ToDo, AvailProjects.VitradoProjectID, AvailProjects.Scratch, AvailProjects.Price FROM dmerce_sys.AvailProjects, dmerce_sys.ManageProjects WHERE ToDo = 1 AND ManageProjects.AVAILPROJECTID = AvailProjects.ID"
    rc, r = q[s]
    if not rc:
        rc = 0
    if rc:
        welcome()
        print t(), '****** BEGIN ******'
        print t(), 'FOUND', rc, 'NEW REQUESTS.'
        for i in range(rc):
            print t(), '--- BEGIN PROCESSING REQUEST NUMBER', i + 1, '---'
            print t(), '--- PROCESS OF DATA ID -> ', r[i]['RequestID'], '---'
            s = NewScratch(conf)
            ippool = DMS.IP.Pool(q, conf['ipnet'])
            ippool.GetNet()
            ipaddr = ippool.GetIP()
            if ipaddr:
                scratch = r[i]['Scratch']
                s.SetLocalSQL(ql)
                s.SetSQL(q)
                s.SetNCCSQL(nccQ)
                s.SetIP(conf['ipnet'] + '.' + str(ipaddr))
                s.SetRequestID(r[i]['RequestID'])
                s.SetScratch(scratch)
                s.SetProjectName(string.lower(r[i]['LastName']))
                s.SetProjectDbType(conf['db'])
                s.SetDbPwd(conf['pwd'])
                s.SetProjectDbName('dmerce_' + string.replace(s.GetProjectName(), '-', '_'))
                print t(), "  NEW PROJECTNAME IS '%s'" % s.GetProjectName()
                s.SetProjectFQHN(conf['domainprefix'] + '.' + s.GetProjectName() + '.' + conf['domainsuffix'])
                s.SetDNSRecordName(conf['domainprefix'] + '.' + s.GetProjectName() + '.' + conf['dnssuffix'])
                s.SetProjectDestDir(conf['projectdestdir'] + '/' + s.GetProjectName() + '/' + conf['domainprefix'])
                s.SetHostSerial(conf['hostserial'])
                if string.replace(str(r[i]['ProjectType']) ,'L', '') == '1':
                    print t(), "  IP-ADDRESS FROM IP-POOL: %s" % str(ipaddr)
                    print t(), "  SCRATCHING " + scratch + " FOR " + s.GetProjectName()
                    p = s.Scratch()
                    print t(), "  UPDATING DATABASE"                    
                    s.UpdateDatabase()
                    print t(), "  SENDING EMAIL TO REQUESTER"
                    s.ActivateEmail(s.GetProjectFQHN(), r[i]['Email'], r[i]['RequestID'])
                else:
                    print t(), "  SKIPPING ID ", r[i]['RequestID'], " BECAUSE IS NOT A TEST PROJECT"
                if string.replace(str(r[i]['ProjectType']) ,'L', '') == '2' and len(string.strip(str(r[i]['VITRADORESELLERID']))) >= 0 :
                    vitrado = VitradoBilling('http://www.vitrado.de/cgi-bin/billing/import_form.pl')
                    vitrado.SetProjectID(r[i]['VitradoProjectID'])
                    vitrado.SetproName(scratch)
                    vitrado.SetproPartnerID(r[i]['VITRADORESELLERID'])
                    vitrado.SetaVgnr(string.replace(str(r[i]['RequestID']),'L',''))
                    vitrado.SetaVname('VorName')
                    vitrado.SetaNname('NachName')
                    vitrado.CalcPrice(r[i]['Price'])
                    vitrado.SetDateTime()
                    vitrado.CreateGetRequest()
                    f = vitrado.SendGetRequest()
                    print t(), "  SENDING VITRADO REQUEST '" + scratch + "' FOR '" + s.GetProjectName() + "'"
                    print t(), "  SENT REQUEST TO -> ", vitrado.GetURL(), " WITH FOLLOWING DATA "
                    print t(), "  ------------- BEGINING OF DATA OUTPUT ------  "
                    print t(), "  typ=", vitrado.GetTyp()
                    print t(), "  b_date=", vitrado.GetbDate()
                    print t(), "  p_time=", vitrado.GetbTime()
                    print t(), "  b_aktion=", vitrado.GetbAktion()
                    print t(), "  p_wert=", int(vitrado.GetpWert())
                    print t(), "  p_typ=", vitrado.GetpTyp()
                    print t(), "  pro_id=", vitrado.GetproID()
                    print t(), "  pro_name=", vitrado.GetproName()
                    print t(), "  pro_partnerid=", vitrado.GetproPartnerID()
                    print t(), "  pro_anbieterid=", vitrado.GetproAnbieterID()
                    print t(), "  a_vgnr=", vitrado.GetaVgnr()
                    print t(), "  a_aname=", vitrado.GetaVname()
                    print t(), "  a_nname=", vitrado.GetaNname()
                    print t(), "  ------------- END OF DATA OUTPUT ------------ "
                    print t(), "  GOT FOLLOWING SIGNAL FROM VITRADO -> ", f
                    print t(), "  SETTING TODO FLAG TO 0 WHERE ID = ",r[i]['RequestID']
                    s.SetToDo(r[i]['RequestID'])
            else:
                print t(), '  !!! GOT NO IP-ADDRESS FROM POOL. PORJECT NOT SUCCESFULLY CREATED !!!'
            print t(), '--- END PROCESSING REQUEST NUMBER', i + 1, '---'
        print t(), '****** END ******'
        print

if __name__ == '__main__':
    lock = DMS.Lock.Lock('dmercereg2.lock')
    if not lock.CheckLock():
        print t(), 'dmercereg2 script such running'
        sys.exit()
    lock.AquireLock()
    try:
        main()
    except:
        print t(), 'SOME ERRORS OCCURED'
        print t(), 'RELEASE LOCK FILE AND EXIT'
    lock.ReleaseLock()
