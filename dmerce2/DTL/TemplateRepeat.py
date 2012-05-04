#!/usr/bin/env python
##
#
# $Author: rb $
revision =  '$Revision: 2.8 $'
#
# Revision 1.1  2000-05-11 15:04:06+02  rb
# Initial revision
#
##

import sys
import Core.Log
import DTL.Operator

class Repeat:

    """
    class that represents a repeat
    """

    def __init__(self, kw, lineFrom, lineTo = None):
        """
        - init attributes with None
        - line from, line to, position in result set, result count, inner-repeat?
        - we have one SQL object
        """
        self.__debug = kw['debug']
        self.__sql = kw['sql']
        #self.__uxs = kw['uxs']
        self.__httpEnv = kw['httpEnv']
        self.__lineFrom = lineFrom
        self.__lineTo = lineTo
        self.__linePointer = lineFrom
        self.__result = []
        self.__resultRow = 0
        self.__resultCount = 0
        self.__inner = None
        self.__parent = None
        self.__log = Core.Log.File(debug = self.__debug, module = '1[TemplateRepeat]')
        self.__sqlOp = DTL.Operator.SQL(debug = self.__debug,
                                        sql = self.__sql, stmt = 'SELECT') #, uxs = self.__uxs)

    """ set and get attributes """

    def SetLineFrom(self, lf):
        self.__lineFrom = self.__linePointer = lf

    def GetLineFrom(self):
        return self.__lineFrom

    def SetLineTo(self, lt):
        self.__lineTo = lt

    def GetLineTo(self):
        return self.__lineTo

    def SetInner(self):
        self.__inner = 1

    def isInner(self):
        return self.__inner

    def SetParent(self, p):
        self.__parent = p

    def GetParent(self):
        return self.__parent

    def UnsetInnerRepeat(self):
        self.__innerRepeat = None

    def SetLinePointer(self, lp):
        self.__linePointer = lp

    def GetNextLine(self, n = 1):
        """ return next line """
        #self.__linePointer = self.__linePointer + n
        #self.__log.Write(msg = 'repeat lp=lt: %s=%s, resultrow=%s/%s => %s'
        #                 % (self.__linePointer, self.__lineTo,
        #                    self.GetActualResultRow(), self.GetResultCount(),
        #                    self.__result[self.GetActualResultRow()]))
        if self.__linePointer == self.__lineTo - 1:
            self.__linePointer = self.__lineFrom
            self.NextResultRow()
        return self.__linePointer

    def isEnd(self):
        """ return true if linePointer == lineTo? """
        if self.__linePointer >= self.__lineTo:
            return 1
        else:
            return 0

    def Check(self):
        """
        check if Repeat-construct is ok:
        1. do we have lineFrom
        2. and lineTo?
        3. __sqlOp.stmt set?
        """
        ok = 1
        if not self.__lineFrom:
            ok = 0
        if not self.__lineTo:
            ok = 0
        if not str(self.__sqlOp.stmt):
            ok = 0
        return ok

    """ sql """

    def CacheSQL(self, schema, table, field):
        """ cache [schema.]table.field for sqlOp """
        try:
            self.__sqlOp.stmt.SetSchema(schema)
            self.__sqlOp.stmt.AddTable(table)
            self.__sqlOp.stmt.AddField('%s.%s' % (table, field))
            self.__newQuery = 1
        except:
            raise Core.Error.CannotCacheSQL(0, 'CANNOT CACHE %s.%s: NO REPEAT HERE?'
                                            % (table. field))

    def SetSQLCondition(self, where):
        """
        we only accept new conditions when it
        has changed
        """
        oldWhere = self.__sqlOp.stmt.GetCondition()
        if oldWhere == where:
            self.__newQuery = 0
        else:
            self.__sqlOp.stmt.SetCondition(where)
            self.__newQuery = 1

    def ExecSQLQuery(self):
        """
        check if we must send a new query to database
        and execute sql query for this repeat
        """
        if self.__newQuery:
            qok, self.__resultCount, self.__result = self.__sqlOp.Exec()
            if self.__debug:
                s = '<!-- SQL-QUERY --- %s --- HAD %s RESULT(S) -->\n' \
                    % (str(self.__sqlOp.stmt), str(self.__resultCount))   #### [:-1]
                self.__httpEnv['req'].write(s)
            self.__resultRow = 0
            return 1

    def GetResultCount(self):
        return self.__resultCount

    def GetActualResultRow(self):
        """ return actual number of result row """
        return self.__resultRow

    def GetResultRow(self):
        """ return actual row of result """
        return self.__result[self.__resultRow]

    def GetNextResultRow(self):
        """ set pointer to next row of result and return row """
        self.NextResultRow()
        return self.GetResultRow()

    def NextResultRow(self):
        """ set pointer to next row of result """
        self.__resultRow = self.__resultRow + 1

    def isResult(self):
        """ checks if we still have result rows left """
        if self.__resultCount:
            if self.__resultRow < self.__resultCount:
                return self.__resultRow
            else:
                return -1
        else:
            return -1

    def GetSQL(self, table, field):
        """
        return value of table.field from actual row
        of result set
        """
        #self.__log.Write(msg = 'RESULTROW=%s RESULT/ROW=%s' % (self.__resultRow, self.__result[self.__resultRow]))
        return self.__result[self.__resultRow][field]

    def Check(self):
        """
        check if (repeat)-construct is ok:
        1. do we have lineFrom
        2. and lineTo?
        3. sql condition set?
        """
        ok = 1
        if not self.__lineFrom:
            ok = 0
        if not self.__lineTo:
            ok = 0
        if not self.__sqlOp.stmt:
            ok = 0
        return ok
