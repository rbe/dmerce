#!/usr/bin/env python
##
#
# $Author: ka $
revision = '$Revision: 1.4 $'
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
import DMS.Sendmail
import Guardian.Config
import DTL.Template
import DTL.TemplateParse
import DTL.Processor

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
        self.__rc, self.__lr = self.__q["SELECT * FROM dmerce_sys.SendEmail WHERE ToDo = 1"]

    def __ParseQuery(self, q):
        p = {}
        q = string.split(q, '&')
        for item in q:
            k, v = string.split(item, '=', 1)
            p[k] = v
        return p

    def ValidateTemplate(self, prjcfg1, q, rq, query):
        ct = DTL.Template.Template(self.__q, prjcfg1)
        ct.SetFQHN(rq['FQHN'])
        ct.SetFQTN(rq['Template'])
        ct.SetTemplateRoot('%s/templates/' % rq['PrjDocRoot'])
        ct.Init()
        ct.OpenTemplate()
        ct.ReadTemplate()
        mycgi = DMS.Sendmail.CGI(debug = 0, cgiForm = q, qvars = DMS.Q.Vars())
        parser = DTL.TemplateParse.ParseTemplate(debug = 0, cgi = mycgi,
                                                 sql = query, sqlSys = query,
                                                 sam = None, uxs = None,
                                                 inputBuffer = ct.GetLines())
        parserResult = parser.Parse()
        evalt = DTL.Processor.EvalTemplate(parserResult = parserResult,
                                           debug = 0,
                                           cgi = mycgi,
                                           sql = query, sqlSys = query,
                                           prjcfg = prjcfg1,
                                           sam = None, uxs = None)
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
        print '%s SENT EMAIL FROM=%s TO=%s CC=%s BCC=%s SUBJECT=%s' \
              % (time.ctime(time.time()), q['fromAddr'], q['toAddr'], q['ccAddr'], q['bccAddr'], q['subject'])
        es.Send()

    def Process(self):
        """ process requests """
        for rq in self.__lr:
            rc, result = self.__q["SELECT * FROM dmerce_sys.Configuration WHERE FQHN = '%s'" % rq['FQHN']]
            print '%s FOUND REQUESTS FOR FQHN=%s' % (time.ctime(time.time()),
                                                     rq['FQHN'])
            prjcfg1 = result[0]
            prjcfg1['debug'] = prjcfg1['Debug']
            l1 = DMS.SQL.Layer1(prjcfg1['SQL'])
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
                stmt = "UPDATE dmerce_sys.SendEmail SET ChangedDateTime = %f, ToDo = 0 WHERE ID = '%s'" \
                       % (time.time(), rq['ID'])
            except:
                stmt = "UPDATE dmerce_sys.SendEmail SET ChangedDateTime = %f, ToDo = 2 WHERE ID = '%s'" \
                       % (time.time(), rq['ID'])
            rc, r = self.__q[stmt]

if __name__ == '__main__':
    print vars.COPYRIGHT_SCRIPT
    conf = Guardian.Config.RFC822(os.environ['DMERCE_HOME'])
    connStr = conf.cp.get('q', 'SendEmailMySQL')
    s = Send(connStr)
    s.Process()
