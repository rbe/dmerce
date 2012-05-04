#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 1.10 $
#
##

#import sys
#sys.path.append('/export/home/r/rb/dmerce2')
import whrandom
import string
import Core.Log
import DMS.MBase
import DMS.SuperSearch

class Random(DMS.MBase.Class):

    """ create random number """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__r = whrandom.whrandom()
        self.__log = Core.Log.File(debug = self._debug, module = '1[Math].Random')

    def Get(self, a, b):
        return self.__r.randint(a, b)

    def GetFromList(self, l):
        return l[self.__r.randint(0, len(l) - 1)]

    def ExcludeFromList(self, l, e):
        r = []
        for i in l:
            if i != e:
                r.append(i)
        return r

    def Create(self, rangeFrom, rangeTo, exclude = None):
        r = range(rangeFrom, rangeTo + 1)
        if exclude:
            r = self.ExcludeFromList(r, exclude)
        return self.GetFromList(r)

    def CreateFromQuery(self, table, field = 'ID', qSuperSearch = 'ID]0', exclude = None):
        if string.find(table, '__'):
            table = string.replace(table, '__', '.')
        ss = DMS.SuperSearch.SuperSearch(qSuperSearch)
        stmt = "SELECT %s FROM %s WHERE %s ORDER BY %s" % (field, table, ss.ConvertToSQLWhere(), field)
        rc, r = self._sql[stmt]
        values = []
        for i in r:
            values.append(i['ID'])
        if exclude:
            values = self.ExcludeFromList(values, exclude)
        self.__log.Write(msg = 'CHOOSING RANDOM FROM %s STMT=%s RC=%s' % (r, stmt, rc))
        return self.GetFromList(values)

#r = Random({})
#for i in range(20):
#    print i, ' = ', r.Create(0, 10, 1)
