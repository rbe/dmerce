#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.24 $'
#
##

import sys
import os
import string
import Core.Log
import DMS.SQL
import DMS.Statement
import DMS.SuperSearch
import DTL.DynamicCall

class SQL:

    """ control operations on sql database """

    def __init__(self, **kw):
        if kw.has_key('debug'):
            debug = kw['debug']
        else:
            debug = 0
        self.table = None
        self.fields = []
        self.condition = None
        self.__rowCount = None
        self.__result = None
        self.__sql = kw['sql']
        if isinstance(kw['stmt'], DMS.Statement.Statement):
            self.stmt = kw['stmt']
        else:
            self.stmt = DMS.Statement.Statement(kw['stmt'])
        #self.__uxs = kw['uxs']
        self.__log = Core.Log.File(debug = debug, module = '1[Operator].SQL')

    def Info(self):
        """ return information about query """
        try:
            return (self.__queryOk, self.__rowCount, self.__result)
        except:
            return (-1, -1, [])

    def __GetRightsDuties(self, objects, right):
        """
        takes a list of objects and
        returns a correspondending list of rights
        and duties for the objects
        """
        ol = []
        for o in objects:
            ol.append(self.__uxs.CheckObject('%s.*' % o, right))
        return ol

    def __ExecError(self, u):
        """ when a query fails log and raise error """
        self.__queryOk = 0
        self.__rowCount = None
        self.__result = None
        self.__log.Write(submodule = 'Exec', msgType = 'ERROR', \
                         msg = 'CANNOT EXECUTE %s-OPERATION WITH UXSI %s ON TABLE %s (%s)' \
                         % (self.stmt.queryType, self.__uxs.GetUXSI(), self.stmt.GetTable(),
                            str(self.stmt)))
        if u == 5:
            raise Core.Error.SQLSelectPermissionDenied(1, self.stmt.GetTable())
        if u == 6:
            raise Core.Error.SQLInsertPermissionDenied(1, self.stmt.GetTable())
        if u == 7:
            raise Core.Error.SQLUpdatePermissionDenied(1, self.stmt.GetTable())
        if u == 8:
            raise Core.Error.SQLDeletePermissionDenied(1, self.stmt.GetTable())

    def Exec(self):
        """
        excecute query
        1. get query type
        2. check tables when type=select or any other
        3. check rights, duties on object(s)
        4. raise error when no rights/duties were found
        5. modify statements with duties when applicable
        5.1. SELECT/UPDATE/DELETE save unmodified condition and restore after query
        to not confuse other programs (like processor/repeat)
        6. execute query
        7. restore old condition if we had to do no. 5
        8. set parameters for actual result row, queryOk
        9. return info about query
        """
##         oldCond = None
##         try:
##             u = self.__uxs.ResolveRight(self.stmt.queryType)
##         except:
##             u = None
##         if u is not None:
##             if u == 5:
##                 obj = self.__GetRightsDuties(self.stmt.GetTablesList(), u)
##             else:
##                 obj = self.__GetRightsDuties([self.stmt.GetTable()], u)
##             if not obj:
##                 self.__ExecError(u)
##             if u == 5:
##                 oldCond = self.stmt.GetCondition()
##             for o in obj:
##                 right, duty = o[0], o[1]
##                 if not right:
##                     self.__ExecError(u)
##                 if (u == 5 or u == 7 or u == 8) and duty:
##                     qss = DMS.SuperSearch.SuperSearch(duty)
##                     c = self.stmt.GetCondition()
##                     #self.__log.Write(msg = 'DUTY=%s' % duty)
##                     if c:
##                         self.stmt.SetCondition('%s AND %s' % (qss.ConvertToSQLWhere(), c))
##                     else:
##                         self.stmt.SetCondition(qss.ConvertToSQLWhere())
        s = str(self.stmt)
        self.__log.Write(submodule = 'Exec', msg = 'ABOUT TO EXECUTE SQL QUERY=%s'
                         % str(self.stmt))
        s = s.replace("'None'", 'NULL')
        s = s.replace("'NULL'", 'NULL')
        self.__rowCount, self.__result = self.__sql[s]
        self.__log.Write(submodule = 'Exec', msg = 'EXECUTE SQL QUERY=%s'
                         % str(s))
        self.__sql.Commit()
        #self.__sql['COMMIT']
##         if oldCond:
##             self.stmt.SetCondition(oldCond)
        self.__queryOk = 1
        return self.Info()

class Trigger:

    """ control operation on triggers """

    def __init__(self, kw, trigger):
        self.__kw = kw
        self.__trigger = trigger
        self.__log = Core.Log.File(debug = kw['debug'], module = '1[Operator].Trigger')

    def __Analyse(self):
        """
        analyse components of given trigger
        - split trigger components by '.'
        - try to find () in method
        - split method([<args>])
        - add '(' (it was removed by using string.split)
        """
        package, module, method = string.split(self.__trigger, '.', 3)
        if string.find(method, '(') >= 0:
            method, args = string.split(method, '(')
            args = '(%s' % args
        else:
            args = ''
        return (package, module, method, args)

    def Exec(self):
        """ execute a trigger """
        package, module, method, args = self.__Analyse()
        q = DTL.DynamicCall.Python(self.__kw)
        t = q.Import(package, module)
        self.__log.Write(msg = 'EXECUTING TRIGGER=%s.%s.%s(%s)' % (package, module, method, args))
        try:
            rv = eval('t.%s(%s)' % (method, args))
            return rv
        except ImportError:
            raise Core.Error.TriggerExecutionError(2, 'COULD NOT CREATE INSTANCE FROM %s.%s' \
                                                   ' OR CALL %s WITH ARGS=%s: %s'
                                                   % (package, module, method, args,
                                                      str(sys.exc_info()[0]), str(sys.exc_info()[1])))
