#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.38 $'
#
# Revision 1.1  2000-05-11 15:02:25+02  rb
# Initial revision
#
##

import sys
import string
import time
import whrandom
import types
#import Core.OS
import Core.Log
import Core.Math
import Core.Error

class SessionId:

    """ class represents a session id """

    def __init__(self, httpEnv = None, sid = None):
        #self.m = Core.OS.Base()
        self.__httpEnv = httpEnv
        self.__log = Core.Log.File(module = '1[SAM].SessionId')
        self.__or = Core.Math.OrdRepr()
        self.__GetEnv()
        self.__sid = sid

    def __GetEnv(self):
        """
        get values from environment for generating a session id
        SERVER_ADDR, REMOTE_ADDR, HTTP_X_FORWARDED_FOR, HTTP_REFERER,
        HTTP_USER_AGENT
        """
        self.serverName = self.__httpEnv['SERVER_NAME']
        if not self.serverName:
            self.serverName = 'localhost'
        self.serverAddr = self.__httpEnv['SERVER_ADDR']
        if not self.serverAddr:
            self.serverAddr = '0.0.0.0'
        self.remoteAddr = self.__httpEnv['REMOTE_ADDR']
        if not self.remoteAddr:
            self.remoteAddr = '0.0.0.0'
        self.httpXForwardedFor = '0.0.0.0'
        #self.httpXForwardedFor = self.__httpEnv('HTTP_X_FORWARDED_FOR')
        #if not self.httpXForwardedFor:
        #    self.httpXForwardedFor = '0.0.0.0'
        #self.httpReferer = self.__httpEnv['HTTP_REFERER'][7:]
        #if not self.httpReferer:
        #    self.httpReferer = 'http://www.1ci.de/test1/test2'[7:]
        #slash = string.find(self.httpReferer, '/')
        #if slash:
        #    self.httpReferer = self.httpReferer[:slash]
        self.userAgent = 'Agent/0.0 [xx] (0; 0; 0)'
        #self.userAgent = self.__httpEnv['HTTP_USER_AGENT']
        #if not self.userAgent:
        #    self.userAgent = 'Agent/0.0 [xx] (0; 0; 0)'

    def Generate(self):
        """
        generate a new session id
        - Convert timestamp to hex
        - Add random number between 0 and 65535 (0xffff hex) in hex
        """
        t = '%s' % time.time()
        dot = string.find(t, '.')
        self.__sid = '%8x%03x' % (long(t[:dot]), int(t[dot + 1:]))
        # Add information from HTTP_USER_AGENT
        # self.__posUserAgent = len(self.__sid) + 1
        # self.__sid = '%s,%s' % (self.__sid, str(self.__or.String(self.userAgent))[:-1])
        # Add information from HTTP_REFERER
        # self.__posHttpReferer = len(self.__sid) + 1
        # self.__sid = '%s,%s' % (self.__sid, str(self.__or.String(self.httpReferer))[:-1])
        r = whrandom.whrandom()
        self.__posRandom = len(self.__sid) + 1
        self.__sid = '%s,%04x' % (self.__sid, r.randint(0, 65535))
        return self.__sid

    def ConvertBack(self):
        """ convert hex session id string back to its original components """
        try:
            # return (self.GetTimestamp(), self.GetUserAgent(), self.GetHttpReferer(), self.GetRandom())
            return (self.GetTimestamp(), self.GetRandom())
        except:
            raise Core.Error.SessionIdNotValidError(1, 'Cannot convert id back to its components')

    def GetTimestamp(self):
        """ get timestamp component from session id """
        timestamp1 = string.atoi(self.__sid[:8], 16)
        timestamp2 = string.atoi(self.__sid[8:11], 16)
        if not (timestamp1 and timestamp2) == None:
            return float('%s.%s' % (timestamp1, timestamp2))
        else:
            raise Core.Error.TimestampNotValidError(1, self.__sid[:11])

    def GetUserAgent(self):
        """ get user agent component from session id """
        ua = self.__or.Long(long(self.__sid[self.__posUserAgent:self.__posHttpReferer - 1]))
        if not ua == None:
            return ua
        else:
            raise Core.Error.UserAgentNotValidError(1, self.__sid[self.__posUserAgent:self.__posHttpReferer - 1])

    def GetHttpReferer(self):
        """ get http referer component from session id """
        hr = self.__or.Long(long(self.__sid[self.__posHttpReferer:self.__posRandom - 1]))
        if not hr == None:
            return hr
        else:
            raise Core.Error.HttpRefererNotValidError(1, self.__sid[self.__posHttpReferer:self.__posRandom - 1])

    def GetRandom(self):
        """ get random component from session id """
        random = string.atoi(self.__sid[self.__posRandom:], 16)
        if not random == None:
            return random
        else:
            return None

    def isValid(self):
        """ is current self.__sid a valid session id? """
        try:
            self.ConvertBack()
            return 1
        except:
            return 0

    def Get(self):
        """ return actual session id """
        #self.__log.Write(msg = 'GET: TYPE OF self.__sid=%s' % type(self.__sid))
        # workaround: warum kann self.__sid zur liste werden?
        if type(self.__sid) is types.ListType:
            self.__sid = str(self.__sid[0])
        return self.__sid

    def Set(self, sid):
        """ set session id """
        self.__sid = sid

class Session(SessionId):

    """
    the session itself
    handling of: creating, deleting, checking
    """

    def __init__(self, debug = 0, httpEnv = None, sqlSAM = None, sid = None,
                 timeMaximum = 3600 * 4, timeInactive = 3600 / 4):
        """ db: instance of database object to operate on table SAM """
        self.__httpEnv = httpEnv
        self.__timeMaximum = timeMaximum
        self.__timeInactive = timeInactive
        SessionId.__init__(self, httpEnv, sid)
        self.__log = Core.Log.File(debug = debug, module = '1[SAM].Session')
        self.__sqlSAM = sqlSAM
        self.__samTable = 'SAMSessions'
        if self.__sqlSAM.GetType() != 'POSTGRESQL':
            self.__samTable = 'dmerce_sys.' + self.__samTable

    def GetTimeInactive(self):
        """ get value for a session can be inactive """
        return self.__timeInactive

    def Create(self):
        """ create a session """
        stmt = "INSERT INTO %s" \
                      " (SessionId, FirstTime, LastTime, ServerName, ServerAddr, RemoteAddr," \
                      " HttpXForwardedFor, UserAgent)" \
                      " VALUES ('%s', %.6f, %.6f, '%s', '%s', '%s', '%s', '%s')" \
                      % (self.__samTable, self.Get(), time.time(), time.time(), self.serverName,
                         self.serverAddr, self.remoteAddr, self.httpXForwardedFor, self.userAgent)
        self.__sqlSAM[stmt]
        self.__log.Write(submodule = 'Create', msg = 'ACTIVATED NEW SESSION %s WITH' \
                         ' INACTIVE TIMEOUT=%f AND MAXIMUM TIMEOUT=%f STMT=%s'
                         % (self.Get(), self.__timeInactive, self.__timeMaximum, stmt))
        return 1

    def Disable(self, sessionId = None):
        """
        disable session
        set disabled flag in database for session for all sessions/ip addresses of client
        don't use 'session_id = %s AND disabled = 0' % sid to disable all sessions for client
        """
        if not sessionId:
            sessionId = self.Get()
        dt = time.time() - 900
        rc, r = self.__sqlSAM["UPDATE %s" \
                              "   SET LastTime = %.6f, DisabledTime = %.6f" \
                              " WHERE SessionId = '%s'" % (self.__samTable, dt, time.time(), sessionId)]
        self.__log.Write(submodule = 'Disable', msg = 'DISABLED SESSION %s AT %.6f' \
                         % (sessionId, dt))
        return 1

    def isDisabled(self):
        """ is session disabled? """
        try:
            rowCount, result = self.__sqlSAM["SELECT DisabledTime" \
                                             "  FROM %s" \
                                             " WHERE SessionId = '%s'" \
                                             "   AND DisabledTime > 0" % (self.__samTable, self.Get())]
            return result[0]['DisabledTime']
        except:
            raise Core.Error.CannotFindSessionError(1, self.Get())

    def CheckTimelimit(self):
        """
        check session for time limit
        - get timestamps from database
        - calculate difference between lasttime and firsttime in seconds
        - is maximum session time exceeded?
        - calculate difference between actual time and lasttime in seconds
        - is inactive session time exceeded?
        """
        stmt = "SELECT FirstTime, LastTime" \
               "  FROM %s" \
               " WHERE SessionId = '%s'" \
               "   AND DisabledTime = 0" % (self.__samTable, self.Get())
        rowCount, result = self.__sqlSAM[stmt]
        # self.__log.Write(msg = 'CheckTimeLimit: STMT=%s R=%s' % (stmt, result))
        deltaMaximum = result[0]['LastTime'] - result[0]['FirstTime']
        #if deltaMaximum > self.__timeMaximum:
        #    self.__log.Write(submodule = 'CheckTimelimit',
        #                     msg = 'MAXIMUM TIMEOUT REACHED FOR SESSION %s (%.2f OF %.2f max)' \
        #                     % (self.Get(), deltaMaximum, self.__timeMaximum))
        #    raise Core.Error.MaximumTimeoutError(1, self.Get())
        actualTime = time.time()
        deltaInactive = actualTime - result[0]['LastTime']
        if deltaInactive > self.__timeInactive:
            self.__log.Write(submodule = 'CheckTimelimit',
                             msg = 'INACTIVE TIMEOUT REACHED FOR SESSION %s (%.2f OF %.2f max)' \
                             % (self.Get(), deltaInactive, self.__timeInactive))
            raise Core.Error.InactiveTimeoutError(1, self.Get())
        return 1

    def Refresh(self):
        """
        refresh times of an open session
        set lasttime to actual time and date
        """
        try:
            rt = time.time()
            self.__sqlSAM["UPDATE %s" \
                          "   SET LastTime = %.6f" \
                          " WHERE SessionId = '%s'" % (self.__samTable, rt, self.Get())]
            self.__sqlSAM["COMMIT"]
            #self.__log.Write(submodule = 'Refresh', msg = 'REFRESHED SESSION %s AT %s'
            #                 % (self.Get(), rt))
            return 1
        except:
            raise Core.Error.CannotRefreshSessionError(1, '%s %s' % (sys.exc_info()[0], sys.exc_info()[1]))

    def Authenticate(self, user):
        """
        authenticate a session
        - set authorized user: table.id
        """
        try:
            self.__log.Write(submodule = 'Authenticate', msg = 'AUTHENTICATING SESSION %s WITH USER %s'
                             % (self.Get(), user))
            self.__sqlSAM["UPDATE %s" \
                          "   SET FQUN = '%s'" \
                          " WHERE SessionId = '%s'" % (self.__samTable, user, self.Get())]
            return 1
        except:
            self.__log.Write(submodule = 'Authenticate', msgType = 'ERROR', \
                             msg = 'ERROR AUTHENTICATING SESSION WITH USER %s' % user)
            raise Core.Error.AuthenticationFailedError(1, user)

    def isAuthenticated(self):
        """ is session authenticated? """
        try:
            rc, r = self.__sqlSAM["SELECT FQUN" \
                                  "  FROM %s" \
                                  " WHERE SessionId = '%s'" \
                                  "   AND DisabledTime = 0" % (self.__samTable, self.Get())]
            if r[0]['FQUN']:
                return r[0]['FQUN']
            else:
                return 'NOBODY.0'
        except:
            self.__log.Write(submodule = 'isAuthenticated', msgType = 'ERROR', \
                             msg = 'CANNOT VERIFY AUTHENTICATION OF SESSION')
            raise Core.Error.CannotGetAuthorizationError(1, self.Get())

    def Authorize(self, uxsi):
        """ authorize a session """
        try:
            self.__log.Write(submodule = 'Authorize', msg = 'AUTHORIZING SESSION %s WITH UXSI %s'
                             % (self.Get(), uxsi))
            self.__sqlSAM["UPDATE %s" \
                          "   SET UXSI = '%s'" \
                          " WHERE SessionId = '%s'" % (self.__samTable, uxsi, self.Get())]
            return 1
        except:
            raise Core.Error.AuthorizationFailedError(1, uxsi)

    def isAuthorized(self):
        """ is session authorized? """
        try:
            rc, r = self.__sqlSAM["SELECT UXSI" \
                                  "  FROM %s" \
                                  " WHERE SessionId = '%s'" \
                                  "   AND DisabledTime = 0" % (self.__samTable, self.Get())]
            return r[0]['UXSI']
        except:
            self.__log.Write(submodule = 'isAuthorized', msgType = 'ERROR', \
                             msg = 'CANNOT VERIFY AUTHORIZATION OF SESSION %s' % str(self.Get()))
            raise Core.Error.CannotGetAuthorizationError(1, self.Get())

class Handler:

    """ handler for _S_ecure _A_rea _M_anagement """

    def __init__(self, kw, sid = None, mBeAuthorized = None, timeInactive = 3600 / 4):
        self.__sqlSys = kw['sqlSys']
        self.__sqlData = kw['sqlData']
        self.__samTable = 'SAMSessions'
        if self.__sqlSys.GetType() != 'POSTGRESQL':
            self.__samTable = 'dmerce_sys.' + self.__samTable
        self.mBeAuthorized = mBeAuthorized
        self.__log = Core.Log.File(debug = kw['debug'], module = '1[SAM].Handler')
        self.__session = Session(debug = kw['debug'], httpEnv = kw['httpEnv'],
                                 sqlSAM = self.__sqlSys, sid = sid, timeInactive = timeInactive)
        self.__checkTimeout = 1
        self.__who = {}

    def GetSessionId(self):
        """ return actual session id """
        return self.__session.Get()

    def Disable(self, sessionId = None):
        """
        disable a session
        TODO: only the client itself or a management client can do this!
        """
        return self.__session.Disable(sessionId)

    def CopySession(self, sessionId):
        """ copy data: FQUN from another session """
        rc, r = self.__sqlSys["SELECT FQUN" \
                              "  FROM %s" \
                              " WHERE SessionId = '%s'" \
                              "   AND DisabledTime = 0" % (self.__samTable, sessionId)]
        fqun = r[0]['FQUN']
        self.GenerateNew()
        newSessionId = self.GetSessionId()
        rc, r = self.__sqlSys["UPDATE %s" \
                              "   SET FQUN = '%s'" \
                              " WHERE SessionId = '%s'" \
                              "   AND DisabledTime = 0" % (self.__samTable, fqun, newSessionId)]
        return newSessionId

    def Authenticate(self, user):
        """ authenticate a user """
        return self.__session.Authenticate(user)

    def isAuthenticated(self):
        return self.__session.isAuthenticated()

    def Authorize(self, uxsi):
        """ authorize a user """
        return self.__session.Authorize(uxsi)

    def isAuthorized(self):
        return self.__session.isAuthorized()

    def GenerateNew(self):
        """ generate a new session id and create it """
        self.__session.Set(self.__session.Generate())
        self.__session.Create()

    def __CheckAlive(self):
        """
        do things when we have a alive session
        - check if session must be authorized
        - check timelimit
          - if ok refresh session
        """
        if self.mBeAuthorized:
            auth = self.__session.isAuthorized()
            self.__log.Write(submodule = '__CheckAlive', msg = 'SESSION %s MUST BE AUTHORIZED. AUTH=%s'
                             % (self.__session.Get(), auth))
            if auth != '0.1':
                self.__log.Write(submodule = '__CheckAlive', msgType = 'ERROR',
                                 msg = 'SESSION %s NOT AUTHORIZED' % self.__session.Get())
                raise Core.Error.MustBeAuthorizedError(1, self.__session.Get())
        if self.__checkTimeout:
            if self.__session.CheckTimelimit():
                if not self.__session.Refresh():
                    self.__log.Write(submodule = '__CheckAlive', msgType = 'ERROR',
                                     msg = 'TIMELIMIT OF SESSION %s NOT OK' % self.__session.Get())
                    raise Core.Error.TimelimitNotOKError(1, self.__session.Get())
        return self.__session.Get()

    def GetActiveSessionForClient(self):
        """ look for an active session of client """
        t = time.time()
        stmt = "SELECT SessionId, LastTime, FQUN, UXSI" \
               "  FROM %s" \
               " WHERE ServerName = '%s'" \
               "   AND ServerAddr = '%s'" \
               "   AND RemoteAddr = '%s'" \
               "   AND HttpXForwardedFor = '%s'" \
               "   AND DisabledTime = 0" \
               "   AND %f - LastTime <= %i" \
               % (self.__samTable, self.__session.serverName, self.__session.serverAddr,
                  self.__session.remoteAddr, self.__session.httpXForwardedFor, t,
                  self.__session.GetTimeInactive())
        rc, r = self.__sqlSys[stmt]
        if rc:
            self.__session.Set(r[0]['SessionId'])
            self.__log.Write(submodule = 'GetActiveSessionForClient', msg = 'GOT ACTIVE SESSION %s' \
                             ' USER=%s, UXSI=%s, %f - %f = %f DELTA/%f MAX'
                             % (r[0]['SessionId'], r[0]['FQUN'], r[0]['UXSI'], t, r[0]['LastTime'],
                                t - r[0]['LastTime'], self.__session.GetTimeInactive()))
            return self.__session.Get()
        else:
            self.__log.Write(submodule = 'GetActiveSessionForClient', msg = 'CANNOT GET ACTIVE SESSION: SQL=%s' % stmt)
            return 0

    def __DontCheckTimeout(self):
        self.__checkTimeout = 0

    def Check(self):
        """ check session """
        id = self.GetSessionId()
        if id:
            #self.__log.Write(msg = 'ALREADY HAVE SESSION ID=%s' % id)
            self.__DontCheckTimeout()
            return self.__CheckAlive()
        else:
            asfc = self.GetActiveSessionForClient()
            if asfc:
                # If possible, set active session id of client as session id
                return self.__CheckAlive()
            elif self.GetSessionId() and not self.__session.isDisabled():
                # Is session alive?
                self.__log.Write(submodule = 'Check', msg = 'SESSION %s ALIVE' % (self.GetSessionId()))
                try:
                    return asfc #self.__CheckAlive()
                except Core.Error.TimelimitNotOKError:
                    # Timelimit not ok, disable session
                    self.__session.Disable()
                except:
                    self.__log.Write(submodule = '__CheckAlive', msgType = 'ERROR', \
                                     msg = '%s %s' % (sys.exc_info()[0], sys.exc_info()[1]))
                    raise Core.Error.SessionError()
        if not self.mBeAuthorized:
            # No session alive, generate new session
            self.GenerateNew()
        return self.__session.Get()

    def GetWho(self, what):
        """ get informations about authenticated user """
        id = 0
        try:
            auth = string.split(self.__session.isAuthenticated(), '.')
            if len(auth) == 2:
                table, id = auth
            elif len(auth) == 3:
                schema, table, id = auth
                table = schema + '.' + table
            self.__who['ID'] = int(id)
            if not self.__who.has_key(what):
                stmt = "SELECT %s FROM %s WHERE ID = %s" % (what, table, id)
                try:
                    rc, r = self.__sqlData[stmt]
                    if rc:
                        self.__who[what] = r[0][what]
                    else:
                        self.__who[what] = ''
                except:
                    self.__who[what] = ''
            return self.__who[what]
        except:
            return 0
