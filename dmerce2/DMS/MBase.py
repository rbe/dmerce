#!/usr/bin/env python
##
#
# $Author: rb $
revision =  '$Revision: 2.4 $'
#
##

class Class:

    """
    base class for dmerce modules
    any class that inherit from this class
    has all objects that dmerce/dtl/processor
    provide
    """

    def __init__(self, kw):
        self.__kw = kw
        self._debug = self.__IfKey('debug')
        self._log = self.__IfKey('log')
        self._fqhn = self.__IfKey('fqhn')
        self._sql = self.__IfKey('sql')
        self._sqlSys = self.__IfKey('sqlSys')
        self._sqlData = self._sqlSys
        self._resultSys = self.__IfKey('resultSys')
        self._prjcfg = self.__IfKey('prjcfg')
        self._cgi = self.__IfKey('cgi')
        self._sam = self.__IfKey('sam')
        self._uxs = self.__IfKey('uxs')
        self._httpEnv = self.__IfKey('httpEnv')

    def __IfKey(self, key):
        """
        if a dictionary has a key return its value
        else return None
        """
        if self.__kw.has_key(key):
            return self.__kw[key]
        else:
            return None
