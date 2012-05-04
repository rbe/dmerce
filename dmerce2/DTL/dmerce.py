#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.66 $'
#
# Revision 1.1  2000-05-11 15:02:25+02  rb
# Initial revision
#
##

import sys
#sys.path.append('/export/home/r/rb/dmerce2')
import vars
vars.vars()
import Core.Error
import Core.ShowError
import Core.Traceback
#import Core.OS
import Core.Log
import DTL.Template
import DTL.TemplateParse
import DTL.Processor
import DTL.Dispatcher
import DMS.Q
import DMS.SysTbl
import DMS.HTTP
import DMS.HTML
import Guardian.Config
from mod_python import apache

class Cgi:

    """ dmerce called as cgi """
    
    def __init__(self, req):
        self.__unsuccessful = 0
        self.__putOutHeader = 0
        self.__req = req
        self.__httpEnv = None
        self.__log = Core.Log.File(debug = 1, module = '1[dmerce.Cgi]')

    def Init(self):
        #self.__log.Write(msg = 'ABOUT TO INITIALIZE HTTP ENVIRONMENT: 1')
        self.__httpEnv = DMS.HTTP.Env(self.__req)
        #self.__log.Write(msg = 'ABOUT TO INITIALIZE HTTP ENVIRONMENT: 2')
        self.__httpEnv.Init()
        #self.__log.Write(msg = 'INITIALIZED HTTP ENVIRONMENT')
        #self.__log.Write(msg = 'ABOUT TO INITIALIZE VARIABLES: 1')
        self.__cgi = DMS.HTTP.CGI(debug = 1, req = self.__req, qvars = DMS.Q.Vars(httpEnv = self.__httpEnv))
        #self.__log.Write(msg = 'ABOUT TO INITIALIZE VARIABLES: 2')
        self.__cgi.Init()
        #self.__log.Write(msg = 'INITIALIZED VARIABLES')
        self.SetSuccessful()

    def InitDispatcher(self):
        self.__dtl = DTL.Dispatcher.Dispatcher(self.__cgi, self.__httpEnv, self.__req)

    def GetHttpEnv(self):
        return self.__httpEnv

    def SetSuccessful(self):
        self.__successful = 1

    def SetUnsuccessful(self):
        self.__successful = 0

    def wasSuccessful(self):
        return self.__successful

    def SetTemplate(self, template):
        self.__dtl.cgi.SetqVar('qTemplate', template)
        self.__dtl.InitTemplate()

    def ProcessTrigger(self):
        samSid = self.__dtl.CheckTemplate()
        if samSid:
            self.__dtl.cgi.SetqVar('qs', samSid)
            self.__dtl.RemoveEmptyFieldsToIgnore()
            if self.__cgi.RetrieveqVar('qButton_'):
                return self.__dtl.qButtonTrigger()
            if self.__cgi.GetqVar('qTrigger'):
                self.__dtl.Trigger()
            if self.__cgi.GetqVar('qUpdate') \
               or self.__cgi.GetqVar('qInsert') \
               or self.__cgi.GetqVar('qDelete'):
                self.__dtl.PerformDatabaseActions()
            if self.__cgi.GetqVar('qTriggerAfter'):
                self.__dtl.Trigger('After')

    def ProcessTemplate(self):
        self.WriteHeader()
        self.__dtl.ct.OpenTemplate()
        stats = self.__dtl.ProcessTemplate()
        #self.__dtl.timer.Stop(0)
        return stats

    def WriteHeader(self):
        #sys.stdout.write('Content-type: text/html\n\n')
        #sys.stdout.write(vars.COPYRIGHT_HTML_COMMENT)
        #self.__req.send_http_header()
        self.__req.write(vars.COPYRIGHT_HTML_COMMENT)
        self.SetPutHeaderOut()

    def SetPutHeaderOut(self):
        """ we put out a HTTP header """
        self.__putOutHeader = 1

    def headerWasPutOut(self):
        """ did we already put out a HTTP header """
        return self.__putOutHeader

    def Go(self):
        if not self.__cgi.GetqVar('qTemplate'):
            raise Core.Error.NoTemplateError(1, '1[dmerce(R)]: SORRY, NO TEMPLATE GIVEN')
        try:
            pt = self.ProcessTrigger()
            if pt:
                #self.__log.Write(msgType = 'INFO', msg = 'REDIRECTED.')
                sys.exit()
            pt = self.ProcessTemplate()
            if pt:
                #s = DTL.Dispatcher.Stats(self.__dtl.ct.GetFQTN(), self.__dtl.timer, pt)
                #s.IncreaseCountersBy1()
                #s.Compute()
                #s.Gen()
                #s.Log()
                try:
                    self.__req.write('\n<!--\n\t1Ci/dmerce: %s\n-->\n' % str(pt))
                except:
                    pass
        except SystemExit:
            pass

    def DeInit(self):
        try:
            self.__dtl.DeInit()
        except:
            pass

def Sorry(e, req):
    msg = 'Sorry to heckle you, but I have got an important' + \
          ' <a href="/dmerce?qTemplate=error,%s">information</a> for you.' % e.GetId()
    req.write(msg)

def Sorry2(exc, req):
    req.write('Sorry to bother you, but I had an error and I am not able'
              ' to complete the request.\n'
              + str(exc[0]) + ': ' + str(exc[1]))

def NoConfigurationFound(req, msg):
    req.write(msg[2])

def HostSerialNotValid(req, msg):
    req.write(msg[2])

def SystemErrorMessage(sei, req, httpEnv, wasSuccessful):
    e = Core.ShowError.Handler(tb = sei, httpEnv = httpEnv)
    e.OpenFile()
    e.WriteHeader()
    e.WriteMessage()
    e.WriteDetails()
    e.WriteFooter()
    e.WriteFile()
    e.CloseFile()
    e.Log()
    if not wasSuccessful: # or not c.headerWasPutOut():
        e.ShowPage()
    else:
        Sorry(e, req)

def CustomErrorMessage(t, req, httpEnv):
    l = Core.Log.File(debug = 1, module = 'CustomError')
    l.Write(msgType = 'ERROR', msg = 'CUSTOMERROR: ' + str(t.GetTraceback()))
    q = DMS.Q.Vars()
    q.Set('qErrNo', t.userMessage.GetNumber())
    config = Guardian.Config.RFC822(httpEnv = httpEnv)
    sysTbl = DMS.SysTbl.Retrieve(httpEnv['SERVER_NAME'], config)
    sysTbl.Init()
    sqlData = sysTbl.GetSQLData()
    sqlSys = sysTbl.GetSQLSys()
    prjcfg = sysTbl.GetConfiguration()
    ct = DTL.Template.Template(sqlSys, prjcfg)
    ct.SetFQHN(httpEnv['SERVER_NAME'])
    ct.SetFQTN('error')
    ct.SetTemplateRoot('%s/templates/' % httpEnv['DOCUMENT_ROOT'])
    l.Write(msgType = 'INFO', msg = 'USING ERROR TEMPLATE %s/templates/error.html' % httpEnv['DOCUMENT_ROOT'])
    ct.Init()
    ct.OpenTemplate()
    ct.ReadTemplate()
    parser = DTL.TemplateParse.ParseTemplate(debug = prjcfg['Debug'],
                                             cgi = q,
                                             sql = sqlData, sqlSys = sqlSys,
                                             sam = None, uxs = None,
                                             inputBuffer = ct.GetLines(),
                                             httpEnv = httpEnv)
    parserResult = parser.Parse()
    evalt = DTL.Processor.EvalTemplate(parserResult = parserResult,
                                       debug = prjcfg['Debug'],
                                       cgi = q,
                                       sql = sqlData, sqlSys = sqlSys,
                                       prjcfg = prjcfg,
                                       sam = None, uxs = None,
                                       httpEnv = httpEnv)
    try:
        l = evalt.Go()
    except Core.Error.TemplateEvalComplete:
        pass
    sysTbl.DeInit()

def handler(req):
    log = Core.Log.File(debug = 1, module = '1[dmerce]')
    #log.Write(msg = 'START')
    req.content_type = "text/html"
    req.send_http_header()
    log.Write(msg = 'SENT HEADER')
    httpEnv = None
    try:
        #log.Write(msg = 'ABOUT TO INITIALIZE dmerce: 1')
        c = Cgi(req)
        #log.Write(msg = 'ABOUT TO INITIALIZE dmerce: 2')
        c.Init()
        #log.Write(msg = 'ABOUT TO INITIALIZE dmerce: 3')
        httpEnv = c.GetHttpEnv()
        #log.Write(msg = 'INITIALIZED dmerce')
        c.InitDispatcher()
        c.Go()
        c.DeInit()
    except Core.Error.ConfigurationError, msg:
        c.DeInit()
        NoConfigurationFound(req, msg)
    except Core.Error.HostSerialNotValidError, msg:
        c.DeInit()
        HostSerialNotValid(req, msg)
    except:
        c.DeInit()
        t = Core.Traceback.Traceback(sys.exc_info())
        t.Init()
        if httpEnv:
            try:
                CustomErrorMessage(t, req, httpEnv)
            except:
                SystemErrorMessage(t, req, httpEnv, c.wasSuccessful())
        else:
            Sorry2(sys.exc_info(), req)
    #log.Write(msg = 'STOP')
    return apache.OK
