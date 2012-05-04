#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.9 $'
#
##

#import sys
#sys.path.append('/usr/local/1Ci/dmerce/2.1.1')
import time
import Core.Error
import Core.OS
import DMS.Q
import DMS.SQL
import DMS.SysTbl
import DMS.Messaging
import Guardian.Config
import DTL.Template
import DTL.TemplateParse
import DTL.Processor

class Requests:

    """ retrieve requests for sending passwords """

    def __init__(self, sqlSys):
        self.__sqlSys = sqlSys
        self.__fqhns = {}

    def GetRequests(self):
        """
        retrieve countries and continents from database
        and return them as a dictionary
        """
        stmt = "SELECT ID, FQHN, T, UserField, User, PasswdField FROM dmerce_sys.LoginSendPasswd WHERE ToDo = 1"
        rc, r = self.__sqlSys[stmt]
        for row in r:
            self.__fqhns[row['FQHN']] = {
                'ID' : row['ID'],
                'T' : row['T'],
                'UF' : row['UserField'], 'U' : row['User']
                }

    def __str__(self):
        return self.__fqhns

    def Get(self, fqhn):
        return self.__fqhns[fqhn]

class Send:

    """
    Send passwords of logins on request
    """

    def __init__(self):
        self.m = Core.OS.Base()
        #self.__fqhn = 'www.de.xstone.1ci.de'
        self.__fqhn = self.m.Env('SERVER_NAME')
        self.__config = Guardian.Config.RFC822(self.m.Env('DMERCE_DOCUMENT_ROOT'))
        sysTbl = DMS.SysTbl.Retrieve(self.__fqhn, self.__config)
        sysTbl.Init()
        self.__sqlSys = sysTbl.GetSQLSys()
        self.__prjcfg = sysTbl.GetConfiguration()
        self.__sqlData = sysTbl.GetSQLData()
        self.__lr = Requests(self.__sqlSys)
        self.__lr.GetRequests()

    def Template(self):
        """ return template handle """
        self.__ct = DTL.Template.Template(self.__sqlSys, self.__prjcfg)
        self.__ct.SetFQHN(self.__fqhn)
        self.__ct.SetFQTN('email,register')
        self.__ct.SetTemplateRoot('%s/templates/' % self.m.Env('DMERCE_DOCUMENT_ROOT'))
        self.__ct.Init()
        self.__ct.OpenTemplate()
        self.__ct.ReadTemplate()

    def Process(self):
        """ process requests """
        for fqhn in str(self.__lr).keys():
            rq = self.__lr.Get(fqhn)
            stmt = "SELECT ID FROM %s WHERE %s = '%s'" % (rq['T'], rq['UF'], rq['U'])
            rc, r = self.__sqlData[stmt]
            q = DMS.Q.Vars()
            q.Set('qLoginSendPasswd_ID', r[0]['ID'])
            self.__parser = DTL.TemplateParse.ParseTemplate(debug = self.__prjcfg['Debug'], cgi = q,
                                                            sql = self.__sqlData, sqlSys = self.__sqlSys,
                                                            sam = None, uxs = None,
                                                            inputBuffer = self.__ct.GetLines())
            parserResult = self.__parser.Parse()
            evalt = DTL.Processor.EvalTemplate(parserResult = parserResult,
                                               debug = self.__prjcfg['Debug'],
                                               cgi = q,
                                               sql = self.__sqlData, sqlSys = self.__sqlSys,
                                               prjcfg = self.__prjcfg,
                                               sam = None, uxs = None)
            evalt.SetOutputMethod(1)
            l = evalt.Go()
            eh = DMS.Messaging.EmailHeader()
            eh.GenerateDate()
            eh.SetFrom(self.__ct['EmailFrom'])
            eh.SetTo(rq['U'])
            eh.SetSubject(self.__ct['EmailSubject'])
            es = DMS.Messaging.EmailSend(eh, l)
            #print str(es)
            es.Send()
            stmt = "UPDATE LoginSendPasswd SET ChangedDateTime = %f, ToDo = 0 WHERE ID = '%s'" \
                   % (time.time(), rq['ID'])
            rc, r = self.__sqlSys[stmt]

if __name__ == '__main__':
    s = Send()
    s.Template()
    s.Process()
