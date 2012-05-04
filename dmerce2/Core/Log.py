#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.31 $'
#
# Revision 1.1  2000-05-11 15:04:06+02  rb
# Initial revision
#
##

import sys
import vars
vars.vars()
import os
import string
import time
import Core.OS
import Core.Error

class ManageFilesystemFile:

    """ manage a filesystem file we log to """

    def __init__(self, debug = None, applicationName = None):
        self.fd = None
        self.filename = ''
        self.m = Core.OS.Base()
        #if debug:
        if not applicationName:
            self.applicationName = self.m.Env('SERVER_NAME')
            if not self.applicationName:
                self.applicationName = 'localhost'
        else:
            self.applicationName = applicationName
        t = time.localtime(time.time())
        # Set absolute path to logfile dir (basepath + applicationName + month)
        self.path = '/opt/dmerce/log/%s/%s/%s' % (self.applicationName, \
                                                  time.strftime('%Y', t), time.strftime('%m', t))

    def CheckPathAccess(self):
        """ check access to path """
        if not os.access(self.path, 0):
            try:
                # Does self.path exist? Try to create dir self.path
                os.makedirs(self.path, 0750)
            except:
                raise Core.Error.CannotCreateLogPathError(1, self.path)
        #if os.access(self.path, 7):
            # Can we access self.path for reading and writing?
            #return 1
        #else:
            #raise Core.Error.Log.CannotAccessLogPathForWriting(1, self.path)

    def Check(self):
        """ check logfile to write to """
        try:
            self.CheckPathAccess()
            self.filename = '%s/%s.log' % (self.path, time.strftime('%d', time.localtime(time.time())))
        except IOError, msg:
            #raise Core.Error.CannotAccessLog(1, '%s: %s' % (self.filename, msg))
            pass
        except Core.Error.CannotCreateLogPathError:
            pass

    def __getitem__(self, item):
        """ write to logfile """
        if self.fd:
            self.fd.write(item)
            self.fd.flush()

    def Open(self):
        self.fd = open(self.filename, 'a+')

    def Close(self):
        if self.fd:
            self.fd.close()

class File:

    """ logging to a file """

    def __init__(self, debug = 1, applicationName = None, module = None, fqtn = None,
                 httpEnv = None):
        self.__debug = debug
        self.module = 'None'
        self.fqtn = fqtn
        self.httpEnv = httpEnv
        #self.m = Core.OS.Base()
        if module:
            self.module = module
        self.mfsf = ManageFilesystemFile(debug, applicationName)
        # Check logfiles
        self.mfsf.Check()

    def GetIPAddrs(self):
        """ get IP addresses of client and server """
        if self.httpEnv:
            self.serverAddr = self.httpEnv['SERVER_ADDR']
            if not self.serverAddr:
                self.serverAddr = '0.0.0.0'
            self.remoteAddr = self.httpEnv['REMOTE_ADDR']
            if not self.remoteAddr:
                self.remoteAddr = '0.0.0.0'
            self.httpXForwardedFor = '0.0.0.0'
            #self.httpXForwardedFor = self.httpEnv['HTTP_X_FORWARDED_FOR']
            #if not self.httpXForwardedFor:
            #    self.httpXForwardedFor = '0.0.0.0'
        else:
            self.serverAddr = '0.0.0.0'
            self.remoteAddr = '0.0.0.0'
            self.httpXForwardedFor = '0.0.0.0'

    def Write(self, submodule = 'DEBUG', msgType = 'DEBUG', msg = 'None', **debugMsg):
        """ writes string 'str' with timestamp """
        self.mfsf.Open()
        self.GetIPAddrs()
        if not self.fqtn:
            self.fqtn = 'System'
        logMsg = '%f %s->%s %s %s %s %s' % (time.time(),
                                            self.remoteAddr,
                                            self.serverAddr,
                                            self.fqtn,
                                            self.module,
                                            msgType,
                                            msg)
        if debugMsg:
            logMsg = '%s DEBUG:' % logMsg
            for d in debugMsg.keys():
                logMsg = '%s %s=%s' % (logMsg, d, debugMsg[d])
        logMsg = '%s\n' % logMsg
        self.mfsf[logMsg]
        self.mfsf.Close()
