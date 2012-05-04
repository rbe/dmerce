#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.4 $'
#
# Revision 1.1  2000-07-03 13:11:38+02  rb
# Initial revision
#
##

import string
import Core.ErrorMessages
import Core.Log
import DMS.HTTP

class Message:

    def __init__(self, kw):
        self.__log = Core.Log.File(module = 'Error.Message')
        sn = DMS.HTTP.ServerName(kw['httpEnv'])
        sn.Analyse()
        self.__lang = sn.GetLanguage()

    def Show(self, errNo, lang = None):
        """ return message of error number """
        if lang:
            l = lang
        elif lang in ['de', 'en', 'es', 'fr', 'it']:
            l = self.__lang
        else:
            l = 'en'
        em = Core.ErrorMessages.ErrMsg(l)
        m = em.Get(errNo)
        self.__log.Write(msgType = 'ERROR', msg = 'LANGUAGE=' + l + ' MESSAGE=' + m)
        return m
