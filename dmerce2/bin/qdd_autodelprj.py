#!/usr/local/bin/python1.5

import sys
import string
import os
import time
import vars
import ConfigParser
import DMS.SQL
import DMS.Lock

vars.vars()

def t():
    return time.strftime('%a %b %d %X %Z %Y:', time.localtime(time.time()))

def welcome():
    print vars.COPYRIGHT_SCRIPT
    print 'AUTO DELETE DMERCE CUSTOMER PROJECTS'
    print

class DBData:

    """ Connection to recent database """

    def __init__(self, sql):
        self.__q = sql

    def GetToDelete(self, ipnet):
        """ get all customer to delete """
        stmt = "SELECT m.ID, " \
               "       m.PROJECTNAME, " \
               "       m.PROJECTDBNAME, " \
               "       m.PROJECTIP, " \
               "       m.nccVhostID " \
               "  FROM dmerce_sys.ManageProjects AS m, dmerce_rcc.Registrations AS r " \
               " WHERE m.VALIDUNTIL < %f " % float(time.time()) + \
               "   AND m.PROJECTIP LIKE '%s' " % ( str(ipnet) + '%') + \
               "   AND m.ID = r.ManageProjectsID " \
               "   AND r.AllowDelete = 1 " \
               "   AND r.Deleted = 0 " \
               "   AND m.Deleted = 0"
        rc, r = self.__q[stmt]
        return r

    def DelFreeIP(self, ipnet, ip):
        """ delete ip out of table of assigned ip's """
        stmt = "SELECT ID " \
               "  FROM IPPool " \
               " WHERE Net = '%s'" % ipnet
        rc, r = self.__q[stmt]
        stmt = "DELETE FROM IPPoolAddr " \
               "      WHERE IPPoolID = '%s' " % str(r[0]['ID']) + \
               "        AND IP = '%s'" % str(ip)
        cr, c = self.__q[stmt]

    def DelProjectName(self, prjname):
        """ delete assigned projectname """
        stmt = "DELETE FROM ProjectNames " \
               "      WHERE PROJECTNAME = '%s'" % prjname
        rc, r = self.__q[stmt]
               
    def GetNCCVhostID(self, projectName):
        """ if nccVhostID = 0 get it """
        stmt = "SELECT v.ID FROM SrvWebsrvVhosts AS v, DnsRecords AS r " \
               "           WHERE r.ID = v.DnsRecordID " \
               "             AND r.Name = 'www.%s.customer'" % projectName
        rc, r = self.__q[stmt]
        if len(r) > 0:
            return r[0]['ID']
        else:
            return None

    def UpdateRCC(self, manID):
        """ give information for RCC """
        stmt = "UPDATE dmerce_rcc.Registrations " \
               "   SET Deleted = 1, DeletedDate = %f" % float(time.time()) + \
               " WHERE ManageProjectsID = %i" % int(manID)
        rc, r = self.__q[stmt]

    def UpdateNCC(self, projectname, nccVhostID):
        """ UPDATE recent values in NCC system to delete project """
        stmt = "DELETE FROM DnsRecords " \
               "      WHERE Name = 'www.%s.customer' " % projectname
        rc, r = self.__q[stmt]
        if nccVhostID:
            stmt = "DELETE FROM SrvWebsrvVhosts " \
                   "      WHERE ID = %i" % int(nccVhostID)
            rc, r = self.__q[stmt]
        stmt = "UPDATE DnsZones " \
               "   SET ToDo = 1 " \
               " WHERE Name = '1ci.de'"
        rc, r = self.__q[stmt]

    def UpdateManageProjects(self, id):
        stmt = "UPDATE ManageProjects SET Deleted = 1 WHERE ID = '%s'" % str(id)
        rc, r = self.__q[stmt]

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
        d['projectdestdir'] = cp.get('reg', 'projectdestdir')
        d['nccConnStr'] = cp.get('q', 'Sys')
        return d
    except:
        print 'ERROR: environment variables not correct or missing entries in $DMERCE_HOME/dmerce.cfg!'
        print 'ERROR: need $DMERCE_HOME'
        print 'ERROR: need entries in dmerce.cfg for: db, pwd, ipnet, hostserial, domainprefix, domainsuffix,'
        print 'ERROR:                                 projectdestdir'
        sys.exit()


def DropProject(projectname, projectdestdir, mysqlrootpwd):
    """ function to drop project with dmercedrpprj """
    fqhn = 'www.' + projectname + '.customer.1ci.de'
    print t(), "START DMERCE DRPPRJ FOR PROJECT %s" % fqhn
    prjdir = projectdestdir + '/' + projectname + '/' + 'www'
    pwd = mysqlrootpwd
    os.system('dmerce drpprj projectfqhn=%s projectdestdir=%s mysqlrootpwd=%s verbose=n' % ( fqhn, prjdir, pwd))
    print t(), "DMERCE DRPPRJ FOR PROJECT %s DONE" % fqhn

def GetIP(addr):
    return string.split(addr, '.')[-1]

def main():
    conf = check()

    # connect to database as dmerce_sys
    c = DMS.SQL.Layer1('MySQL:dmerce_sys:dmerce_sys@honey.hamburg2.de.1ci.net:dmerce_sys')
    c.Init()
    c.Connect()
    sysq = c.GetQuery()

    # connect to database as dmerce_ncc
    s = DMS.SQL.Layer1(conf['nccConnStr'])
    s.Init()
    s.Connect()
    nccq = s.GetQuery()

    manage = DBData(sysq)
    todel = manage.GetToDelete(conf['ipnet'])

    ncc = DBData(nccq)
    print t(), "FOUND %i CUSTOMER TEST WEBS TO DELETE" % len(todel)
    for i in todel:
        print t(), "NOW DELETE PROJECT www.%s.customer.1ci.de" % i['PROJECTNAME']
        DropProject(i['PROJECTNAME'], conf['projectdestdir'], conf['pwd'])
        manage.DelFreeIP(conf['ipnet'], GetIP(i['PROJECTIP']))
        manage.DelProjectName(i['PROJECTNAME'])
        if int(i['nccVhostID']) == 0:
            nccVhostID = ncc.GetNCCVhostID(i['PROJECTNAME'])
        else:
            nccVhostID = i['nccVhostID']
        print t(), "NOW UPDATING NCC SYSTEM"
        ncc.UpdateNCC(i['PROJECTNAME'], nccVhostID)
        print t(), "NOW UPDATING RCC SYSTEM"
        ncc.UpdateRCC(i['ID'])
        print t(), "DELETE PROCESS FOR PROJECT www.%s.customer.1ci.de WAS SUCCESSFULL" % i['PROJECTNAME']
        manage.UpdateManageProjects(i['ID'])
        print t(), ""

if __name__=='__main__':
    welcome()
    lock = DMS.Lock.Lock('dmerceautodel.lock')
    if not lock.CheckLock():
        print t(), 'dmerceautodel script such running'
        sys.exit()
    lock.AquireLock()
    main()
    lock.ReleaseLock()
