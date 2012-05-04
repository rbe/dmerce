#!/usr/bin/env python
##
#
# $Author: ka $
#revision = '$Revision: 1.12 $'
#
##

import sys
import vars
vars.vars()
import os
import time
import string
import Core.Error
import Core.OS
import DMS.Q
import DMS.SQL
import DMS.SysTbl
import DMS.Messaging
import DMS.HTTPSendEmail
import Guardian.Config
import DTL.Template
import DTL.TemplateParse
import DTL.Processor

def ti():
    """ return local iso time string """
    return time.strftime('%a %b %d %X %Z %Y:', time.localtime(time.time()))


class Send:

    """
    Send templates as email
    """

    def __init__(self, connStr):
        self.__prjcfg = {}
        self.__prjcfg['debug'] = 1
        self.__l1 = DMS.SQL.Layer1(connStr)
        self.__l1.Init()
        self.__q = self.__l1.GetQuery()
        self.__rc, self.__lr = self.__q["SELECT * FROM SendEmail WHERE todo = 1"]
        print ti(), "FOUND %s REQUESTS" % str(self.__rc)

    def __ParseQuery(self, q):
        p = {}
        q = string.split(q, '&')
        for item in q:
            k, v = string.split(item, '=', 1)
            p[k] = v
        return p

    def SetHTTPEnv(self, prjdocroot):
        self.__httpEnv = {}
        self.__httpEnv['DOCUMENT_ROOT'] = prjdocroot
        self.__httpEnv['REFERERER'] = ''
        self.__httpEnv['req'] = ''

    def ValidateTemplate(self, prjcfg1, q, rq, query):
        ct = DTL.Template.Template(self.__q, prjcfg1)
        ct.SetFQHN(rq['FQHN'])
        ct.SetFQTN(rq['Template'])
        ct.SetTemplateRoot('%s/templates/' % rq['PrjDocRoot'])
        ct.Init()
        ct.OpenTemplate()
        ct.ReadTemplate()
        mycgi = DMS.HTTPSendEmail.CGI(debug = 0, cgiForm = q, qvars = DMS.Q.Vars())
        self.SetHTTPEnv(rq['PrjDocRoot'])
        parser = DTL.TemplateParse.ParseTemplate(debug = 0, cgi = mycgi,
                                                 sql = query, sqlSys = query,
                                                 sam = None, uxs = None,
                                                 inputBuffer = ct.GetLines(), httpEnv = self.__httpEnv)
        parserResult = parser.Parse()
        evalt = DTL.Processor.EvalTemplate(parserResult = parserResult,
                                           debug = 0,
                                           cgi = mycgi,
                                           sql = query, sqlSys = query,
                                           prjcfg = prjcfg1,
                                           sam = None, uxs = None,  httpEnv = self.__httpEnv)
        return evalt

    def DoSendProcess(self, l, q, htmlFormatted):
        eh = DMS.Messaging.EmailHeader()
        eh.GenerateDate()
        eh.SetFrom(q['fromAddr'])
        eh.SetTo(q['toAddr'])
        if q.has_key('ccAddr'):
            eh.SetCc(q['ccAddr'])
        else:
            eh.SetCc('')
            q['ccAddr'] = ''
        if q.has_key('bccAddr'):
            eh.SetBcc(q['bccAddr'])
        else:
            eh.SetBcc('')
            q['bccAddr'] = ''
        eh.SetSubject(q['subject'])
        eh.CreateAllRcpt()
        if htmlFormatted:
            eh.SetHeader('MIME-Version', '1.0')
            eh.SetHeader('Content-type', 'text/html')
        es = DMS.Messaging.EmailSend(eh, l)
        print es
        print '%s SENT EMAIL: HTML=%s FROM=%s TO=%s CC=%s BCC=%s SUBJECT=%s' \
              % (time.ctime(time.time()), htmlFormatted, q['fromAddr'], q['toAddr'],
                 q['ccAddr'], q['bccAddr'], q['subject'])
        es.Send()

    def Process(self):
        """ process requests """
        for rq in self.__lr:
            print ti(), "FOUND REQUEST FOR FQHN %s" % rq['FQHN']
            rc, result = self.__q["SELECT * FROM configuration WHERE fqhn = '%s'" % rq['FQHN']]
            print '%s FOUND REQUESTS FOR FQHN=%s' % (time.ctime(time.time()),
                                                     rq['FQHN'])
            prjcfg1 = result[0]
            prjcfg1['debug'] = prjcfg1['Debug']
            l1 = DMS.SQL.Layer1(prjcfg1['ownerconnectstring'])
            l1.Init()
            query = l1.GetQuery()
            print '%s PROCESSING REQUEST FOR FQHN=%s ID=%s TEMPLATE=%s QUERY=%s' \
                  % (time.ctime(time.time()), rq['FQHN'], rq['ID'], rq['Template'], rq['Query'])
            try:
                q = self.__ParseQuery(rq['Query'])
                evalt = self.ValidateTemplate(prjcfg1, q, rq, query)
                evalt.SetOutputMethod(1)
                l = evalt.Go()
                self.DoSendProcess(l, q, rq['HTMLFormatted'])
                stmt = "UPDATE sendemail SET changeddatetime = %f, todo = 0 WHERE id = '%s'" \
                       "AND template = '%s' AND fqhn = '%s' AND query = '%s'" \
                       % (time.time(), rq['ID'], rq['Template'], rq['FQHN'], rq['Query'])
            except:
                stmt = "UPDATE sendemail SET changeddatetime = %f, todo = 2 WHERE id = '%s' " \
                       "AND template = '%s' AND fqhn = '%s' AND query = '%s'" \
                       % (time.time(), rq['ID'], rq['Template'], rq['FQHN'], rq['Query'])
                print ti(), "ERROR OCCURED FOR QUERY: ",rq['Query'], " -> ", sys.exc_info()[0], sys.exc_info()[1], "SET TODO = 2"
            rc, r = self.__q[stmt]

if __name__ == '__main__':
    print vars.COPYRIGHT_SCRIPT
    print "SEND EMAILS"
    print
    print ti(), "START"
    conf = Guardian.Config.RFC822(os.environ['DMERCE_HOME'])
    connStr = conf.cp.get('q', 'SendEmailPSQL')
    s = Send(connStr)
    s.Process()
    print ti(), "END"
