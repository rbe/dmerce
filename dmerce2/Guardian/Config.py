#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.5 $'
#
##

import sys
import ConfigParser
import Core.Log
import Core.OS

class RFC822:

    """ read a file in format of RFC 822 """

    def __init__(self, path = None, httpEnv = None):
        self.cp = None
        self.__log = Core.Log.File(module = 'Guardian.Config')
        if not path:
            if not httpEnv:
                m = Core.OS.Base()
                path = m.Env('DOCUMENT_ROOT')
            else:
                path = httpEnv['DOCUMENT_ROOT']
        return self.Parse(path)

    def Parse(self, path):
        """ open and parse configuration file """
        try:
            self.cp = ConfigParser.ConfigParser()
            self.cp.read('%s/dmerce.cfg' % path)
        except:
            self.__log.Write(submodule = 'Parse', msgType = 'ERROR',
                             msg = '%s %s' % (sys.exc_info()[0], sys.exc_info()[1]))
            return None

    def Get(self, section, var):
        """ get values from config file """
        return self.cp.get(section, var)
