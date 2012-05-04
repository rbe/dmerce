#!/usr/bin/env python2.2
##
#
# $Author: rb $
revision = '$Revision: 2.20 $'
#
# Revision 1.1  2000-05-11 16:01:47+02  rb
# Initial revision
#
##

import sys
#sys.path.append('/export/home/r/rb/dmerce2')
import types
import string
import os
import Core.Error
import Core.OS
import Core.Log
import Core.Math
import DMS.MBase
import DMS.Object
import DMS.SQL

class MCLG(DMS.MBase.Class):

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = '1[Login].MCLG')

    def GetRange(self, group = '', schema = None, table = '', field = ''):
        group = str(group)
        s = ''
        if schema:
            s = schema + '.'
        s = s + table + '.' + field + "*'0'"
        if group:
            s = s + '|'
            if schema:
                s = s + schema + '.'
            #s = s + table + '.' + field + "~'" + group  + "-%'"
            s = s + table + '.' + field + "~'" + group  + "%'"
            #self.__log.Write(msg = 'SQL=%s' % s)
        return s

    def Create(self):
        oc = DMS.Object.Convert()
        givenIdent = self._cgi.GetqVar('qMCLGIdent')
        givenStage = oc.ChangeType(self._cgi.GetqVar('qMCLGStage'), 'int')
        giventf = self._cgi.GetqVar('qMCLGTableField')
        if givenIdent and givenStage and giventf:
            rc, r = self._sql["SELECT ident, digits FROM dmerce_sys.mclg WHERE fqhn = '%s' AND stage = %i" % (self._fqhn, givenStage)]
            identNew = r[0]['ident']
            digitsNew = r[0]['digits']
            lenGivenIdent = len(string.split(givenIdent, '-'))
            if lenGivenIdent == digitsNew:
                raise Core.Error.MCLGWrongStage(3313, '')
            dboid = DMS.SQL.DBOID(self._sql, self._sql)
            newIdent = ''
            delta = (givenStage - lenGivenIdent) - 1
            if delta > 0:
                for i in range(delta):
                    newIdent = newIdent + '-'
            #rc, r = self._sql["SELECT dmerce_sys.s_dboid_mclg_%s.NextVal FROM dual" % identNew]
            #newIdent = newIdent + str(r[0]['NEXTVAL'])
            #newIdent = newIdent + str(dboid['dmerce_sys__mclg' + identNew])[:-1]
            newIdent = newIdent + str(dboid['dmerce_sys__mclg'])[:-1]
            self._cgi.SetField(giventf, givenIdent + '-' + newIdent)
        return 1

    def GetHigher(self, ident):
        return string.join(string.split(ident, '-')[:-1], '-')

class User(DMS.MBase.Class):

    """ perform and control logins """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = '1[Login].User',
                                   httpEnv = self._httpEnv)

    def __CheckCGI(self):
        """ check CGI form for values """
        self.__qLoginSchema = self._cgi.GetqVar('qLoginSchema')
        self.__qLoginTable = self._cgi.GetqVar('qLoginTable')
        if not self.__qLoginTable:
            self.__qLoginTable = 'Users'
        self.__qLoginField = self._cgi.GetqVar('qLoginField')
        if not self.__qLoginField:
            self.__qLoginField = 'Login'
        self.__qLogin = self._cgi.GetqVar('qLogin')
        self.__qPasswdField = self._cgi.GetqVar('qPasswdField')
        if not self.__qPasswdField:
            self.__qPasswdField = 'Passwd'
        self.__qPasswd = self._cgi.GetqVar('qPasswd')

    def Check(self):
        """ check given login and password against database """
        self.__CheckCGI()
        if self.__qLoginSchema:
            stmt = "SELECT ID FROM %s.%s" % (self.__qLoginSchema, self.__qLoginTable)
        else:
            stmt = "SELECT ID FROM %s" % self.__qLoginTable
        stmt = stmt + " WHERE LoginDisabled = 0 AND %s = '%s' AND %s = '%s'" \
               % (self.__qLoginField, self.__qLogin,
                  self.__qPasswdField, self.__qPasswd)
        rc, r = self._sql[stmt]
        if rc:
            # Authenticate and authorize session
            auth = '%s.%i' % (self.__qLoginTable, r[0]['ID'])
            if self.__qLoginSchema:
                auth = self.__qLoginSchema + '.' + auth
            self._sam.Authenticate(auth)
            # self._sam.Authorize(r[0]['UXSI'])
            self._sam.Authorize('0.1')
            self.__log.Write(submodule = 'Check', msgType = 'INFO', msg = 'LOGIN OF %s/%s' \
                             ' SUCCESSFUL' % (self.__qLoginTable, self.__qLogin))
            return 1
        else:
            self.__log.Write(submodule = 'Check', msgType = 'ERROR', msg = 'LOGIN OF %s/%s NOT' \
                             ' SUCCESSFUL: %s %s' % (self.__qLoginTable, self.__qLogin,
                                                     sys.exc_info()[0], sys.exc_info()[1]))
            raise Core.Error.LoginInvalidError(3311, self.__qLogin)

    def GetClientGroupRange(self, group = 10, schema = None, table = '', field = ''):
        return '1*1'
        c = 0
        group = str(group)
        if group[:-1] == 'L':
            group = group[:-1]
        lgroup = len(group)
        for digit in range(lgroup):
            if group[digit] != '0':
                c = 1
            if c and group[digit] == '0':
                found = digit
                c = 0
        group2 = ''
        for i in range(lgroup):
            if i >= found:
                group2 = group2 + '9'
            else:
                group2 = group2 + group[i]
        stf = ''
        if schema:
            stf = schema + '.'
        stf = stf + table + '.' + field
        return '%s]*%s:%s[*%s' % (stf, group, stf, group2)
