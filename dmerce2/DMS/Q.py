#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.20 $'
#
##

#import sys
#sys.path.append('/usr/local/1Ci/dmerce/2.1.1')
import vars
vars.vars()
import string
import Core.OS
import Core.Log
import DMS.Cache

class Vars(DMS.Cache.Cache):

    """
    holds dmerce's special q-variables
    """

    def __init__(self, httpEnv = {}, q = {}):
        self.__c = DMS.Cache.Cache()
        self.__q = q
        self.__q['VERSION'] = vars.VERSION
        self.__httpEnv = httpEnv
        self.__log = Core.Log.File(debug = 1, module = '1[Q]')

    def SetQ(self, q):
        self.__q = q
        self.Parse()

    def GetQ(self):
        return self.__q

    def Parse(self):
        """
        parses q*-variables
        only hold vars beginning with q
        """
        if self.__q:
            #self.__log.Write(msg = 'WILL PARSE %s' % str(self.__q.keys()))
            for k in self.__q.keys():
                #self.__log.Write(msg = 'PARSING ' + k)
                if k[0] == 'q':
                    self.__c[k] = self.__q[k]

    def SetEnvVars(self):
        """ get environment variables """
        m = Core.OS.Base()
        for e in m.EnvAll():
            self.__c[e[0]] = e[1]
        s = '[no info about SSL]'
        try:
            if self.__httpEnv['SERVER_PORT'] == '443':
                s = 1
            elif self.__httpEnv['SERVER_PORT'] == '80':
                s = 0
        except:
            pass
        self.__c['SSL'] = s
        for k in self.__httpEnv.keys():
            self.__c[k] = self.__httpEnv[k]

    def Get(self, var, returnType = None):
        """ return value of dmerce variable """
        if self.__c.has_key(var):
            return self.__c[var]
        else:
            return returnType

    def Set(self, var, value):
        self.__c[var] = value

    def Remove(self, var):
        del self.__c[var]

    def Append(self, var, value):
        self.__c[var] = self.__c[var] + value

    def Copy(self, var, varNew):
        """ copy var to varNew """
        self.__c[varNew] = self.__c[var]

    def Rename(self, var, name):
        """ sets name of var to name and removes old field """
        self.Copy(var, name)
        self.Remove(var)

    def GetKeys(self):
        return self.__c.keys()

    def Retrieve(self, wc):
        """
        retrieve fields from cache beginning with string 'wc'
        (like a wildcard: eg. 'wc' = qSearch, we look for qSearch*)
        """
        r = {}
        for field in self.GetKeys():
            if string.find(field, wc) == 0:
                r[field] = self.__c[field]
        return r
