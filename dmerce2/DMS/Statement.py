#!/usr/bin/env python
##
#
# $Author: rb $
revision = "$Revision: 2.7 $"
#
##

import sys
sys.path.append('/export/home/rb/dmerce2')
import string
import types
import Core.Error

class Analyse:

    """ analyse a sql statement """

    # SQL keywords
    __sqlKeywords = [
        'SELECT', \
        'INSERT', 'INTO', 'VALUES', \
        'UPDATE', 'SET', \
        'DELETE', \
        'FROM', 'WHERE', 'ORDER BY', 'GROUP BY', 'HAVING', 'LIMIT', \
        'ORDER BY', 'GROUP BY', 'HAVING'
        ]

    def __init__(self, s):
        # Positions of SQL key words
        self.__position = {}
        # Sorted list of positions
        self.__sortedPositions = []
        # Keywords with their corresponding value
        self.__keywords = {}
        # Type of query
        self.__queryType = None
        # Set statement
        self.__stmt = s
        # Save positions and go
        self.__SaveKeywordPositions()
        self.__Analyse()

    def __str__(self):
        """ build statement from analysed components """
        s = ''
        for sp in range(len(self.__sortedPositions)):
            kw = self.__position[self.__sortedPositions[sp]]
            s = '%s%s' % (s, kw)
            if self.__keywords[kw]:
                s = '%s %s' % (s, self.__keywords[kw])
            if sp < len(self.__sortedPositions):
                s = '%s ' % s
        return s

    def __SaveKeywordPositions(self):
        """
        get positions of SQL key words and save them in a dictionary
        sort positions of SQL key words in string in numPos
        """
        for kw in self.__sqlKeywords:
            # Determine positions of SQL key word
            p = string.find(string.upper(self.__stmt), string.upper(kw))
            if p >= 0:
                self.__position[p] = string.upper(kw)
        # Sort positions of found SQL key words
        self.__sortedPositions = self.__position.keys()
        self.__sortedPositions.sort()

    def __Analyse(self):
        """ analyse statement """
        # For every position of a found keyword
        for i in range(len(self.__sortedPositions)):
            try:
                # Assign values of key words into dictionary
                kwName = self.__position[self.__sortedPositions[i]]
                kwPos = self.__sortedPositions[i] + len(kwName) + 1
                kwNextPost = self.__sortedPositions[i + 1] - 1
                self.__keywords[kwName] = string.strip(self.__stmt[kwPos:kwNextPost])
            except IndexError:
                self.__keywords[kwName] = string.strip(self.__stmt[kwPos:])
        # Set type of query: SELECT/INSERT/UPDATE/DELETE
        self.__queryType = string.upper(self.__position[0])

    def Get(self):
        """ return analysed data """
        return (self.__queryType, self.__sortedPositions, self.__position, self.__keywords)

class Select:

    """ SELECT """

    def __init__(self, s):
        self.tables = []
        self.fields = []
        self.where = None
        self.__analysedStmt = s.Get()
        self.__Analyse()

    def __str__(self):
        """ build statement """
        stmt = "SELECT %s" % string.join(filter(lambda x: x, self.fields), ',')
        stmt = "%s FROM %s" % (stmt, string.join(self.tables, ','))
        if self.where:
            stmt = "%s WHERE %s" % (stmt, self.where)
        return stmt

    def __Analyse(self):
        """ analyse """
        try:
            self.fields = string.split(string.replace(self.__analysedStmt[3]['SELECT'], ' ', ''), ',')
        except:
            pass
        try:
            self.tables = string.split(self.__analysedStmt[3]['FROM'], ',')
        except:
            pass
        try:
            self.where = Where(self.__analysedStmt[3]['WHERE'])
        except:
            pass

    def Get(self):
        """ return analysed data """
        return (self.table, self.fields, self.where)

class Insert:

    """ INSERT """

    def __init__(self, s):
        self.table = ''
        self.fields = []
        self.values = []
        self.__analysedStmt = s.Get()
        self.__AnalyseTableFields()

    def __str__(self):
        """ build statement """
        stmt = "INSERT INTO %s" % self.table
        if self.fields:
            stmt = "%s (%s)" % (stmt, string.join(self.fields, ','))
        # map(lambda x: str(x), self.values)
        stmt = "%s VALUES (" % stmt
        for v in range(len(self.values)):
            if type(self.values[v]) is types.StringType:
                stmt = "%s'%s'" % (stmt, self.values[v])
            else:
                stmt = "%s%s" % (stmt, self.values[v])
            if v < len(self.values) - 1:
                stmt = "%s, " % stmt
        stmt = "%s)" % stmt
        return stmt

    def __AnalyseTableFields(self):
        """ analyse table and fields in current query """
        try:
            self.into = self.__analysedStmt[3]['INTO']
            self.values = string.split(self.__analysedStmt[3]['VALUES'][1:-1], ',')
            if string.find(self.into, '(') >= 1:
                self.table = string.strip(self.into[:string.find(self.into, '(')])
                self.fields = string.split(self.into[string.find(self.into, '(') + 1:string.find(self.into, ')')], ',')
            else:
                self.table = self.into
                self.fields = None
        except:
            pass

    def Get(self):
        """ return analysed data """
        return (self.__table, self.__fields)

class Update:

    """ UPDATE """

    def __init__(self, s):
        self.table = ''
        self.fields = []
        self.values = []
        self.__setPairs = {}
        self.__analysedStmt = s.Get()
        self.__Analyse()

    def __str__(self):
        """ build statement """
        stmt = "UPDATE %s SET " % self.table
        # Pairs of SET field = value
        l = self.__GetSetPairs()
        i = 0
        for item in l.keys():
            if type(l[item]) is types.StringType:
                stmt = "%s%s = '%s'" % (stmt, item, l[item])
            else:
                stmt = "%s%s = %s" % (stmt, item, l[item])
            if i < len(l.keys()) - 1:
                stmt = "%s, " % stmt
            i = i + 1
        # WHERE condition
        if self.where:
            stmt = "%s WHERE %s" % (stmt, self.where)
        return stmt

    def __Analyse(self):
        """ analyse components """
        self.table = self.__analysedStmt[3]['UPDATE']
        try:
            w = Where(self.__analysedStmt[3]['WHERE'])
            self.where = w.Analyse()
        except:
            self.where = None
        try:
            for p in string.split(self.__analysedStmt[3]['SET'], ','):
                # Look at SET ...
                f, v = string.split(p, '=')
                if v[0] == ' ':
                    v = v [1:]
                self.__setPairs[string.strip(f)] = v
        except:
            self.__setPairs = {}

    def __GetSetPairs(self):
        """ get SET field = value pairs """
        if self.__setPairs:
            return self.__setPairs
        else:
            for i in range(len(self.fields)):
                self.__setPairs[self.fields[i]] = self.values[i]
            return self.__setPairs

    def Get(self):
        """ return analysed data """
        return (self.table, self.__setPairs, self.where)

class Delete:

    """ DELETE """

    def __init__(self, s):
        self.table = None
        self.where = None
        self.__analysedStmt = s.Get()
        self.__Analyse()

    def __str__(self):
        """ build statement """
        stmt = 'DELETE FROM %s' % self.table
        if self.where:
            stmt = '%s WHERE %s ' % (stmt, self.where)
        return stmt

    def __Analyse(self):
        """ analyse components """
        try:
            self.table = self.__analysedStmt[3]['FROM']
        except:
            self.table = None
        try:
            w = Where(self.__analysedStmt[3]['WHERE'])
            self.where = w.Analyse()
        except:
            self.where = None

    def Get(self):
        """ return analysed data """
        return (self.table, self.where)

class Where:

    """ WHERE """

    def __init__(self, w):
        self.__where = w
        self.__condition = {}

    def __str__(self):
        """ build condition """
        return '%s' % self.__where

    def Analyse(self):
        """ analyse WHERE condition """
        cond = self.__where
        # Delete keywords
        for item in ['AND', 'OR']:
            cond = string.replace(cond, item, '')
        # Split by whitespace
        cond = string.split(cond)
        # Process three items at once: value OPERATOR value
        for item in range(0, len(cond), 3):
            # Assign in dictionary
            self.__condition[cond[item]] = (cond[item + 1], cond[item + 2])
        return self.__condition

    def CheckForTableField(self, what):
        """
        check if we have a table.field and return
        a tuple (table, field)
        """
        p = string.find(what, '.')
        if p:
            t, f = what[:p], what[p + 1:]
            if t and f:
                return (t, f)
        return (None, None)

    def GetTables(self):
        """ return tables used in condition """
        l = []
        for k in self.__condition.keys():
            checkK = self.CheckForTableField(k)
            if checkK[0] is not None:
                l.append(checkK[0])
            op, v = self.__condition[k]
            if op == '=':
                checkV = self.CheckForTableField(v)
                if checkV[0] is not None:
                    l.append(checkV[0])
        return l
    
class Statement:

    """ handle SQL statements """

    def __init__(self, stmt):
        self.__analysedStmt = Analyse(stmt)
        self.__aStmt = self.__analysedStmt.Get()
        self.queryType = self.__aStmt[0]
        if self.__aStmt[0] == 'SELECT':
            self.__s = Select(self.__analysedStmt)
        elif self.__aStmt[0] == 'INSERT':
            self.__s = Insert(self.__analysedStmt)
        elif self.__aStmt[0] == 'UPDATE':
            self.__s = Update(self.__analysedStmt)
        elif self.__aStmt[0] == 'DELETE':
            self.__s = Delete(self.__analysedStmt)

    def __str__(self):
        """ return statement """
        return '%s' % self.__s

    """ set and get table of query """

    def GetTables(self):
        return string.join(self.__s.tables, ',')

    def GetTablesList(self):
        return self.__s.tables

    def GetTable(self):
        return self.__s.table

    def SetTable(self, table):
        self.__s.table = table

    def SetTables(self, tables):
        self.__s.tables = tables

    def isTable(self, table):
        if table in self.__s.tables:
            return 1
        else:
            return 0

    def AddTable(self, table):
        if not self.isTable(table):
            self.__s.tables.append(table)

    """ set and get fields of query """

    def GetFields(self):
        return self.__s.fields

    def SetFields(self, fields):
        self.__s.fields = fields

    def isField(self, field):
        for f in self.__s.fields:
            # Check if field exists in list
            if f == field:
                return 1
        return 0

    def AddField(self, field):
        if not self.isField(field):
            # Field not in list, append
            self.__s.fields.append(field)

    """ set and get VALUES of insert statments """

    def GetValues(self):
        return self.__s.values

    def SetValues(self, values):
        self.__s.values = values

    def AddValue(self, value):
        self.__s.values.append(value)

    def GetTableFields(self):
        """ get complete table dot field names of current query """
        l = []
        t = self.__s.table
        for f in self.__s.fields:
            l.append('%s.%s' % (t, f))
        return l

    """ set and get condition of a statement """

    def GetCondition(self):
        return self.__s.where

    def SetCondition(self, where):
        self.__s.where = where

    def AddCondition(self, where):
        self.__s.where = '%s %s' % (self.__s.where, where)

    def Get(self):
        """ return statement """
        return self.__s.Get()

    def EscapeSQLValues(self, s):
        """
        check string and escape characters which can be dangerous
        """
        SQLChars = ('"', "'")
        for ToEscape in SQLChars:
            if type(s) is types.StringType:
                s = string.replace(s, ToEscape, '\%s' % ToEscape)
        return s

#s = Where('t1.f2 = t2.f1 AND t3.f5 = 1')
#s.Analyse()
#print s.GetTables()
#s = Statement("SELECT")
#s.SetFields(['a', 'b'])
#s.SetTable('t')
#s.SetCondition("a = '1'")
#print s
