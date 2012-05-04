#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.3 $'
#
##

import sys
import string
import Core.OS
import Core.Log
import Core.Math
import Core.Error

class BitRights:

    """ control rights represented by bits """

    def __init__(self, _rights = 0):
        self.__bit = Core.Math.BitOperation()
        self.__rights = _rights

    def SetSingle(self, _right):
        """ set single right """
        if isBit(self.__rights, _right) == 0:
            self.__rights = self.__rights + 2L**_right

    def SetMultiple(self, _rights):
        """ set many rights """
        _result = 0
        for i in range(self.__bit.Length(self.__rights) + self.__bit.Length(_rights) + 1):
            if self.__bit.isBit(self.__rights, i) == 1 or self.__bit.isBit(_rights, i) == 1:
                _result = _result + 2**i
        self.__rights = _result

    def KillSingle(self, _right):
        """ kill a single right """
        if self.HasSingle(_right):
            self.__rights = self.__rights - 2**_right

    def KillMultiple(self, _rights):
        """ kill more than one right """
        for i in range(0, self.__bit.Length(self.__rights)):
            if self.HasSingle(i) and self.__bit.isBit(_rights, i):
                self.__rights = self.__rights - 2**i

    def HasSingle(self, _digit):
        """ ask if a right is set """
        return self.__bit.isBit(self.__rights, _digit)

    def ShowRights(self):
        """ return list of rights """
        return self.__bit.ListSetBitPos(self.__rights)

    def ShowRightsAsBits(self):
        """ return list of bits """
        return self.__bit.List(self.__rights)

    def Get(self):
        """ return actual rights """
        return self.__rights

class UXSObject:

    """ work with UXS objects """

    def __init__(self, _o):
        self.__object = _o
        self.__wc = []
        self.__Analyse()

    def __MakeWildcards(self, sep):
        """
        analyse an object:
        determine type of object and generate
        all types of wildcards for given object
        """
        try:
            s = string.split(self.__object, sep)
            last = ''
            for item in range(len(s)):
                a = s[:]
                a[item] = '*'
                self.__wc.append(string.join(a, sep))
                last = '%s*%c' % (last, sep)
            self.__wc.append(last[:-1])
        except:
            pass

    def __Analyse(self):
        """
        . -> table and field
        : -> trigger
        , -> tempalte
        """
        if string.find(self.__object, '.') > 0:
            self.__MakeWildcards('.')
        elif string.find(self.__object, ':') > 0:
            self.__MakeWildcards(':')
        elif string.find(self.__object, ',') > 0:
            self.__MakeWildcards(',')

    def Get(self):
        """ get informations about object """
        return self.__wc

class UXS:

    """ unified access """

    """
    bits representing rights
    1-4: operating system, file system, etc.
    5-8: databases
    """
    __rights = {
        'None' : 0,
        'Read': 1,
        'Write': 2,
        'Delete': 3,
        'Execute': 4,
        'SELECT': 5,
        'INSERT': 6,
        'UPDATE': 7,
        'DELETE': 8,
        }

    def __init__(self, prjcfg, kw):
        """ constructor takes a sql instance and an UXSI as argument """
        self.__rightIdBits = 0
        self.__groupIdBits = 0
        self.object = {}
        prjcfg = kw['prjcfg']
        self.__sql = kw['sqlSys']
        self.__uxsi = kw['uxsi']
        self.__fqhn = kw['fqhn']
        self.__bit = Core.Math.BitOperation()
        self.__log = Core.Log.File(debug = prjcfg['debug'], module = 'UXS')
        self.__InitRights()

    def __InitRights(self):
        """ initially resolve rights, groups for UXSI """
        try:
            userRightBits, groups = string.split(self.__uxsi, '.')
            self.userRights = BitRights(int(userRightBits))
            self.groups = self.__bit.ListSetBitPos(int(groups))
            self.__ResolveGroups()
        except:
            self.__log.Write(msgType = 'ERROR', msg = 'ERROR RESOLVING UXSI=%s: %s %s'
                             % (self.__uxsi, sys.exc_info()[0], sys.exc_info()[1]))
            if not self.__uxsi:
                raise Core.Error.UXSNoUXSIError(1, 'NO UXSI SET FOR USER!')
            else:
                raise Core.Error.UXSNoUXSIError(1, 'ERROR RESOLVING UXSI=%s' % self.__uxsi)

    def __ResolveGroups(self):
        """ resolve rights for user depending on his groups """
        for id in self.groups:
            rowCount, result = self.__sql["SELECT R FROM UXSGroups WHERE FQHN = '%s' AND GroupID = %i"
                                          % (self.__fqhn, id)]
            if len(result):
                self.__log.Write(msg = 'RIGHTS FOR GROUP %i@%s = %s' % (id, self.__fqhn, result[0]['R']))
                for r in self.__bit.ListSetBitPos(long(result[0]['R'])):
                    self.LookupRightByObjectId(r)

    def LookupRightByObjectId(self, _r):
        """ lookup a right per id and its possible relating duty """
        rowCountR, resultR = self.__sql["SELECT Object, R FROM UXSRights WHERE FQHN = '%s' AND RightID = %s"
                                        % (self.__fqhn, _r)]
        rowCountD, resultD = self.__sql["SELECT SQLRestriction FROM UXSDuties WHERE FQHN = '%s' AND RightID = %s"
                                        % (self.__fqhn, _r)]
        if rowCountR:
            try:
                if resultD[0]['SQLRestriction']:
                    sqlRestrWord = 'WITH'
                    sqlRestr = resultD[0]['SQLRestriction']
                else:
                    sqlRestrWord = 'WITHOUT'
                    sqlRestr = ''
            except:
                sqlRestrWord = 'WITHOUT'
                sqlRestr = ''
            #self.__log.Write(submodule = 'LookupRight', msg = 'FOUND RIGHT %s FOR TABLE %s %s DUTY-SQL-RESTRICTION %s'
            #                 % (resultR[0]['R'], resultR[0]['Object'], sqlRestrWord, sqlRestr))
        else:
            self.__log.Write(submodule = 'LookupRight', msgType = 'ERROR', msg = 'RIGHT %i NOT FOUND' % _r)
        try:
            self.object[resultR[0]['Object']] = (BitRights(resultR[0]['R']), resultD[0]['SQLRestriction'])
        except IndexError:
            self.object[resultR[0]['Object']] = (BitRights(resultR[0]['R']), None)

    def LookupRightByName(self, object):
        """ lookup right for object name """
        rc, r = self.__sql["SELECT ID FROM UXSRights WHERE FQHN = '%s' AND Name = '%s'"
                           % (self.__fqhn, object)]
        if rc:
            return self.LookupRightByObjectId(r[0]['ID'])
        else:
            return None
        
    def GetUXSI(self):
        """ get UXSI """
        return self.__uxsi

    def GetRights(self, o):
        """ get rights for object """
        return self.object[o][0]

    def GetDuty(self, o):
        """ get duty for object """
        return self.object[o][1]

    def ResolveRight(self, _r):
        """ resolve right by name """
        return self.__rights[_r]

    def ReverseResolveRight(self, _r):
        """ reverse resolve right by ID """
        for item in self.__rights.keys():
            if self.__rights[item] == _r:
                return item

    def CheckObject(self, _o, _r):
        """ lookup rights and duties relating to an object """
        self.LookupRightByName(_o)
        try:
            a, b = self.object[_o][0].HasSingle(_r), self.GetDuty(_o)
            return a, b
        except:
            m = 'RIGHT %s NOT APPLICABLE ON OBJECT %s' % (self.ReverseResolveRight(_r), _o)
            self.__log.Write(submodule = 'CheckObject', msgType = 'ERROR', msg = m)
            raise Core.Error.RightNotApplicableError(1, m)
        return None, None
