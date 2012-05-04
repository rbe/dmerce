#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.44 $'
#
##

import sys
import vars
vars.vars()
import string
import time
import types
#import Core.OS
import Core.Log
import Core.Error
import Core.ShowError
import Core.Timer
import Guardian.Config
import Guardian.License
import Guardian.UXS
import Guardian.SAM
import DTL.TemplateParse
import DTL.Processor
import DTL.Operator
import DTL.PDBA
import DTL.Template
import DMS.HTTP
import DMS.SysTbl
import DMS.MBase
import DMS.HTML
import DMS.Object

class InsertedIDs(DMS.Cache.Cache):

    """
    a cache which informs about
    new ids that were used with INSERTs
    """
    pass

class Stats:

    """
    statistics about a dmerce-run
    we get two timers: one for the complete
    run of dmerce, a second one for the run of
    the parser
    """

    def __init__(self, template, timers, counters):
        self.__template = template
        self.__timers = timers
        self.__counters = counters
        self.__objs = None
        self.__ops = None
        self.__log = Core.Log.File(module = '1[dmerce].Stats')

    def IncreaseCountersBy1(self):
        """ increase every counter by 1 (because it starts with -1) """
        if self.__counters:
            for c in self.__counters.keys():
                self.__counters.Count(c)

    def SumCounters(self):
        sum = 0
        for c in self.__counters.keys():
            sum = sum + self.__counters[c]
        return sum

    def Compute(self):
        """
        compute runtime
        - calculate total runtime
        - calculate deltas
        """
        self.__totalRun = self.__timers.Delta(0)
        self.__evalRun = self.__timers.Delta(1)
        self.__totalDelta = self.__timers.AllDelta()
        if self.__counters:
            self.__objs = self.SumCounters()
            self.__ops = self.__objs / self.__totalRun

    def __GenBase(self):
        self.__s = 'OBJ %s: START=%s STOP=%s' % (self.__template,
                                                 self.__timers.GetStart(0)[0],
                                                 self.__timers.GetStop(0)[0])

    def __GenRuntime(self):
        self.__s = '%s - RUNTIME: T=%f, E=%f, OH=%f' % (self.__s, self.__totalDelta,
                                                        self.__evalRun,
                                                        self.__totalRun - self.__evalRun)

    def __GenEval(self):
        self.__s = '%s - EVAL.: %s OBJS=' % (self.__s, self.__objs)
        for c in self.__counters.keys():
            self.__s = '%s, %s=%s' % (self.__s, c, self.__counters[c])

    def __GenOPS(self):
        self.__s = '%s - OPS=%f' % (self.__s,
                                    self.__ops)

    def Gen(self):
        self.__GenBase()
        self.__GenRuntime()
        if self.__counters:
            self.__GenEval()
        if self.__ops:
            self.__GenOPS()

    def Log(self):
        self.__log.Write(msgType = 'ACCOUNTING', msg = self.__s)

    def __str__(self):
        return self.__s

class Dispatcher:

    """ dispatch and process request to dmerce """

    __SAMStageLogStr = {
        1: ('DEBUG', 'SESSION MUST EXIST AND BE AUTHORIZED'),
        2: ('ERROR', 'ACCESS VIOLATION! TEMPLATE NOT APPLICABLE FOR VIEW BY HTTP CLIENT')
        }

    htmlFooter = '</body></html>'
    
    def __init__(self, cgi = None, httpEnv = None, req = None):
        self.__htmlHead = DMS.HTML.Head(req)
        #self.timer = Core.Timer.Statistics()
        #self.timer.AddTimer()
        #self.timer.Start()
        self.timerStart = time.time()
        #self.m = Core.OS.Base()
        self.__httpEnv = httpEnv
        self.cgi = cgi
        self.__fqhn = self.__httpEnv['SERVER_NAME']
        self.__config = Guardian.Config.RFC822(httpEnv = httpEnv)
        self.__sysTbl = DMS.SysTbl.Retrieve(self.__fqhn, self.__config)
        self.__sysTbl.Init()
        self.__sqlSys = self.__sysTbl.GetSQLSys()
        self.__prjcfg = self.__sysTbl.GetConfiguration()
        self.__sqlData = self.__sysTbl.GetSQLData()
        if self.__prjcfg.has_key('DEBUG'):
            self.__debug = self.__prjcfg['DEBUG']
        else:
            self.__debug = self.__prjcfg['Debug']
        self.__CheckLicense()
        self.__kw = {
            'debug'     : self.__debug,
            'fqhn'      : self.__fqhn,
            'prjcfg'    : self.__prjcfg,
            'cgi'       : self.cgi,
            'sql'       : self.__sqlData,
            'sqlData'   : self.__sqlData,
            'sqlSys'    : self.__sqlSys,
            'httpEnv'   : self.__httpEnv,
            }
        #self.__InitUXS()
        #self.__kw['uxs'] = self.__uxs
        self.InitTemplate()
        self.__log = Core.Log.File(debug = self.__debug, module = '1[dmerce].Dispatcher',
                                   fqtn = self.ct.GetFQTN())
        self.__kw['log'] = self.__log
        self.__InitSAM()
        self.__kw['sam'] = self.__sam
        self.__kw['uxsi'] = self.__sam.isAuthorized()
        self.__log.Write(msg = 'INIT SAM WITH INACTIVE TIMEOUT=%s' % self.__prjcfg['SAMINACTIVETIMEOUT'])

    def __CheckLicense(self):
        """ check license """
        s = Guardian.License.HostSerial(self.__prjcfg)
        s.Check()

    def __InitUXS(self):
        """ initiliaze UXS """
        self.__uxs = Guardian.UXS.UXS(prjcfg = self.__prjcfg, kw = self.__kw)

    def __InitSAM(self, smba = 0):
        """ initiliaze SAM """
        # self.__log.Write(msg = 'Initializing SAM with SID=%s' % self.cgi.GetqVar('qs'))
        self.__sam = Guardian.SAM.Handler(kw = self.__kw, mBeAuthorized = smba,
                                          sid = self.cgi.GetqVar('qs'),
                                          timeInactive = self.__prjcfg['SAMINACTIVETIMEOUT'])
        return self.__sam.Check()
    
    def __CheckSAM(self, smba, samFailUrl):
        """ check sam stage """
        if smba != 1:
            smba = 0
        try:
            self.__log.Write(submodule = '__CheckSAM', msg = 'TEMPLATE PROTECTED BY 1[SAM]')
            return self.__InitSAM(smba = smba)
        except:
            return 0

    def PerformDatabaseActions(self):
        """ perform actions on database """
        stmts = []
        kw = self.__kw
        kw['t'] = self.ct
        objChk = DMS.Object.Convert()
        if self.cgi.GetqVar('qInsert'):
            kw['tables'] = objChk.StringOrListToList(self.cgi.GetqVar('qInsert'))
            p = DTL.PDBA.Insert(kw)
            p.Generate()
            s = p.GetStmts()
            stmts.append(s)
            self.__log.Write(msg = 'GENERATED %i SQL-INSERT-STATEMENTS' % len(s))
        if self.cgi.GetqVar('qUpdate'):
            kw['tables'] = tables = objChk.StringOrListToList(self.cgi.GetqVar('qUpdate'))
            p = DTL.PDBA.Update(kw)
            p.Generate()
            s = p.GetStmts()
            stmts.append(s)
            self.__log.Write(msg = 'GENERATED %i SQL-UPDATE-STATEMENTS' % len(s))
        if self.cgi.GetqVar('qDelete'):
            kw['tables'] = objChk.StringOrListToList(self.cgi.GetqVar('qDelete'))
            p = DTL.PDBA.Delete(kw)
            p.Generate()
            s = p.GetStmts()
            stmts.append(s)
            self.__log.Write(msg = 'GENERATED %i SQL-DELETE-STATEMENTS' % len(s))
        for t in stmts:
            for stmt in t:
                s = DTL.Operator.SQL(debug = self.__debug, sql = self.__sqlData, stmt = stmt)
                s.Exec()

    def __TriggerExec(self, which):
        """ execute a trigger """
        t = DTL.Operator.Trigger(self.__kw, trigger = which)
        return t.Exec()

    def Trigger(self, type = ''):
        """
        process qTrigger or qTriggerAfter
        - remove any successfully called trigger from qVars[]
        - if after processing triggers are left, we had errors
        """
        q = 'qTrigger%s' % type
        for t in string.split(self.cgi.GetqVar(q), ','):
            rv = self.__TriggerExec(t)
            if rv:
                p = string.find(t, self.cgi.GetqVar(q))
                self.cgi.SetqVar(q, self.cgi.GetqVar(q)[len(t) + 1:])
        if len(self.cgi.GetqVar(q)):
            raise Core.Error.TriggerExecutionError(1, self.cgi.GetqVar(q))

    def qButtonTrigger(self):
        """ looks for triggers that are indicated by 'qButtons' """
        rc = 0
        q = self.cgi.RetrieveqVar('qButton_')
        for k in q.keys():
            trigger = string.split(k, '_')[1]
            if trigger == 'qLoginSendPasswd':
                qLoginTable = str(self.cgi.GetqVar('qLoginTable'))
                qLoginField = str(self.cgi.GetqVar('qLoginField'))
                qLogin = str(self.cgi.GetqVar('qLogin'))
                qPasswdField = str(self.cgi.GetqVar('qPasswdField'))
                stmt = "INSERT INTO LoginSendPasswd (CreatedDateTime, FQHN, T, UserField, User, PasswdField, ToDo)" \
                       " VALUES (%f, '%s', '%s', '%s', '%s', '%s', 1)" \
                       % (time.time(), self.__fqhn, qLoginTable, qLoginField, qLogin, qPasswdField)
                self.__log.Write(msg = 'INSERTING LOGIN-SEND-PASSWD REQUEST INTO DATABASE FOR %s' % qLogin)
                self.__sqlSys[stmt]
                self.__htmlHead.Refresh(destination = self.cgi.GetqVar('qLoginSendPasswd_Redirect'))
                rc = 1
        return rc

    def RemoveEmptyFieldsToIgnore(self):
        """
        remove fields from cgi which could be empty
        and should be ignore by further operations
        """
        efti = self.ct['IgnoreEmptyFields']
        if efti:
            eftiFields = string.split(string.replace(efti, ' ', ''), ',')
            for field in eftiFields:
                f = self.cgi.GetField(field)
                if f == '' or f is None or not f:
                    self.__log.Write(msg = 'REMOVING EMPTY FIELD TO IGNORE %s=%s' % (field, f))
                    self.cgi.RemoveField(field)

    def InitTemplate(self):
        """ initialize template """
        self.ct = DTL.Template.Template(self.__sqlSys, self.__prjcfg)
        self.ct.SetFQHN(self.__fqhn)
        self.ct.SetFQTN(self.cgi.GetqVar('qTemplate'))
        self.ct.SetTemplateRoot('%s/templates/' % self.__httpEnv['DOCUMENT_ROOT'])
        self.ct.Init()

    def CheckTemplate(self):
        """ check template """
        if self.ct['SAMFailUrl']:
            samFailUrl = self.ct['SAMFailUrl']
        else:
            samFailUrl = '/'
        c = self.__CheckSAM(self.ct['SAM'], samFailUrl)
        self.__log.Write(msg = 'CHECKING SAM: SAMSTAGE=%s, SAMFAILURL=%s RETURNS %s'
                         % (self.ct['SAM'], samFailUrl, c))
        if not c:
            self.__htmlHead.Refresh(destination = samFailUrl)
            sys.exit()
        else:
            return self.__sam.Check()

    def DeInit(self):
        self.__sysTbl.DeInit()

    def ProcessTemplate(self):
        """ process template """
        timerA = time.time()
        # self.cgi.AppendIgnoredFields()
        timerB = time.time()
        self.ct.ReadTemplate()
        timerC = time.time()
        self.__parser = DTL.TemplateParse.ParseTemplate(
            debug = self.__debug, cgi = self.cgi, httpEnv = self.__httpEnv,
            sql = self.__sqlData, sqlSys = self.__sqlSys,
            sam = self.__sam, 
            inputBuffer = self.ct.GetLines()) #uxs = self.__uxs,
        timerD = time.time()
        parserResult = self.__parser.Parse()
        timerE = time.time()
        evalt = DTL.Processor.EvalTemplate(parserResult = parserResult,
                                           debug = self.__debug, log = self.__log,
                                           fqhn = self.__fqhn,
                                           cgi = self.cgi, httpEnv = self.__httpEnv,
                                           sql = self.__sqlData, sqlSys = self.__sqlSys,
                                           prjcfg = self.__prjcfg,
                                           sam = self.__sam)
        timerF = time.time()
        #self.timer.AddTimer()
        #self.timer.Start()
        #self.__log.Write(msg = 'Initializing MyVar: DOCUMENT_ROOT=%s qs=%s'
        #                 % (self.__httpEnv['DOCUMENT_ROOT'], self.cgi.GetqVar('qs')))
        evalt.InitMyVar(self.__httpEnv['DOCUMENT_ROOT'], self.cgi.GetqVar('qs'))
        try:
            evalt.Go()
            #self.timer.Stop(1)
        except IOError, msg:
            self.__log.Write(submodule = '__ProcessTemplate', msgType = 'INFO',
                             msg = 'TEMPLATE %s: EVALUATION/OUTPUT ABORTED: %s'
                             % (self.cgi.GetqVar('qTemplate'), msg))
            r = None
        except Core.Error.TemplateEvalComplete, msg:
            r = msg[2]
        timerG = time.time()
        #self.__sqlSys["COMMIT"]
        self.__sqlSys.Commit()
        evalt.DeinitMyVar()
        self.DeInit()
        return self.cgi.GetqVar('qTemplate'), 'TOTAL=', time.time() - self.timerStart, \
               'B-A=', timerB - timerA, 'C-B=', timerC - timerB, 'D-C', timerD - timerC, \
               'E-D', timerE - timerD, 'F-E=', timerF - timerE, 'G-F=', timerG - timerF
