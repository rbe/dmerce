#!/usr/local/bin/python
##
#
# $Author: rb $
revision = "$Revision: 1.6 $"
#
##

#import sys
#sys.path.append('/export/home/r/rb/dmerce2')
import os
import Core.Log
import DMS.MBase
import Guardian.SAM

class Session(DMS.MBase.Class):

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__kw = kw
        self.__kw['sqlData'] = kw['sqlSys']
        self.__log = Core.Log.File(debug = self._debug, module = '1[Guardian].SAM')

    def CopyMyVar(self, oldSid, newSid):
        pre = '%s/SAM/myvar.' % self._httpEnv['DOCUMENT_ROOT']
        self.__log.Write(msg = 'SYMLINK SESSION-MYVAR-FILE IN %s: FROM %s TO %s' % (pre, oldSid, newSid))
        return os.symlink(pre + oldSid, pre + newSid)

    def Clear(self, sessionId = None):
        if sessionId is None:
            sessionId = self._cgi.GetqVar('qGuardianSAMClearSessionId', None)
        if sessionId:
            self._sam.Disable(sessionId)
            self.__log.Write(msg = 'SESSION %s CLEARED' % (sessionId, newSessionId))
        return 1

    def ClearAndCopy(self, sessionId = None):
        if sessionId is None:
            sessionId = self._cgi.GetqVar('qGuardianSAMClearSessionId', None)
        if sessionId:
            self._sam.Disable(sessionId)
            newSessionId = self._sam.CopySession(sessionId)
            self.CopyMyVar(sessionId, newSessionId)
            self.__log.Write(msg = 'SESSION %s CLEARED AND COPIED TO NEW SESSION %s'
                             % (sessionId, newSessionId))
        return 1

    def CreateNew(self):
        s = Guardian.SAM.Handler(self.__kw)
        s.GenerateNew()
        id = s.GetSessionId()
        self.__log.Write(msgType = 'INFO', msg = 'FORCED CREATION OF SESSION: SID=%s' % id)
        return id
