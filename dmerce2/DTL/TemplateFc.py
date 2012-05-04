#!/usr/bin/env python
##
#
# $Author: rb $
revision =  '$Revision: 2.7 $'
#
# Revision 1.1  2000-05-11 15:04:06+02  rb
# Initial revision
#
##

import sys
import Core.Log
import DMS.Cache
import DTL.DynamicCall

class Function(DMS.Cache.Module):

    """ loades, caches and executes functions with the use of DTL.DynamicCall """

    def __init__(self, kw):
        DMS.Cache.Module.__init__(self)
        self.__kw = kw
        self.__debug = kw['debug']
        self.__log = Core.Log.File(debug = kw['debug'], module = 'Processor.Function')
        self.__ddc = DTL.DynamicCall.Python(kw)

    def Call(self, package, clazz, method, args):
        """
        full qualified function call
        cache instance
        call method
        """
        fqfc = '%s.%s' % (package, clazz)
        if not self.Get(fqfc):
            self.Add(fqfc, self.__ddc.Import(package, clazz))
        t = self.Get(fqfc)
        try:
            rv = eval('t.%s(%s)' % (method, args))
        except:
            rv = '[ERROR CALLING FUNCTION %s.%s.%s(%s) - SEE LOG FILE]' % (package, clazz, method, args)
            s = sys.exc_info()
            self.__log.Write(msgType = 'ERROR', msg = 'CANNOT EXECUTE %s.%s.%s(%s): %s %s %s'
                             % (package, clazz, method, args, s[0], s[1], s[2]))
        return rv
