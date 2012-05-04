#!/usr/bin/env python
#
import os
import os.path
import vars

class Lock:
    def __init__(self, lock, path = None):
        """ constructor initializes the lock file name """
        vars.vars()
        self.__lock = lock
        if path == None:
            self.__path = '/tmp/'
        else:
            self.__path = self.CheckPath(path)

    def CheckPath(self, path):
        """ looks if path has a / at the end """
        if path[-1] != '/':
            path = path + '/'
            return path
        else:
            return path
        
    def SetPath(self, path):
        """ you can set path any time """
        self.__path = self.CheckPath(path)

        
    def CheckLock(self):
        """ Check, if process still running """
        if os.path.isfile('%s%s' % (self.__path, self.__lock)):
            return None
        else:
            return 1

    def AquireLock(self):
        """ if process not running, set lock file """
        try:
            os.system('touch %s%s' % (self.__path, self.__lock))
            return 1
        except:
            return None

    def ReleaseLock(self):
        """ if process runs out, release its lock file """
        try:
            os.system('rm %s%s' % (self.__path, self.__lock))
        except:
            return None
