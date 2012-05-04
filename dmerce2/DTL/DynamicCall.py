#!/usr/bin/env python2.2
##
#
# $Author: rb $
revision =  '$Revision: 2.13 $'
#
##

import sys
#sys.path.append('/export/home/r/rb/dmerce2/DMS')
import vars
vars.vars()
import string
import imp
import Core.Log

class Python:

    """ call a method from a python class """

    def __init__(self, kw):
        self.__kw = kw
        self.__log = Core.Log.File(debug = kw['debug'], module = '1[DynamicCall].Python')

    def Import(self, package, clazz):
        """
        import a package
        - find the module and load it
        - make instance
        """
        package = string.strip(package)
        clazz = string.strip(clazz)
        try:
            find_mod = imp.find_module(package)
            module = imp.load_module(package, find_mod[0], find_mod[1], find_mod[2])
            clazz = module.__dict__[clazz]
            if self.__kw:
                return clazz(self.__kw)
            else:
                return clazz()
        except ImportError:
            self.__log.Write(msg = 'CANNOT IMPORT MODULE %s (WHILE CALLING %s): %s %s %s'
                             % (package, clazz, sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2]))

    def Call(self, method = None, args = ''):
        """ call a method """
        self.__log.Write(submodule = 'Call', msg = 'CALLING %s' % instance)
        exec('returnValue = instance.%s(%s)' % (method, args))
        return returnValue

#p = Python({'debug':1})
#p.Import('Login', 'User')
