#!/usr/bin/env python
##
#
# $Author: rb $
revision =  '$Revision: 1.1 $'
#
##

import sys
import os.path
import time
import string
import DMS.SQL

def t():
    return time.ctime(time.time())

class Header:

    """ line that describes fields and their order """

    def __init__(self):
        self.__fieldSeparator = ';'
        self.__fieldStringsEnclosedBy = '"'
        self.__line = ''

    def SetLine(self, l):
        l = string.replace(l, '"', '')
        l = string.replace(l, "'", '')
        self.__line = string.strip(l)

    def SetSeparator(self, s):
        self.__fieldSeparator = string.strip(s)

    def GetSeparator(self):
        return self.__fieldSeparator

    def SetFieldStringsEnclosedBy(self, e):
        self.__fieldStringsEnclosedBy = string.strip(e)

    def GetFieldStringsEnclosedBy(self):
        return self.__fieldStringsEnclosedBy

    def Analyse(self):
        co = string.find(self.__line, ',')
        se = string.find(self.__line, ';')
        if co > 0:
            self.SetSeparator(',')
        elif se > 0:
            self.SetSeparator(';')            
        self.__fields = string.split(self.__line, self.__fieldSeparator)

    def Get(self, f = None):
        """ return field at pos f """
        return self.__fields[f]

    def GetAll(self):
        return self.__fields

class ImportCSV:

    """ import cvs data """

    def __init__(self, f):
        self.__fd = open(f, 'r')
        self.__tableName = os.path.basename(f[:string.find(f, '.')])
        self.__content = []
        self.__stmts = []
        d = DMS.SQL.Layer1('Oracle:masy5:afc@localhost:wanci1')
        d.Init()
        self.__q = d.GetQuery()
        self.__th = self.__q.GetTableHandler(schema = 'MASY5', table = self.__tableName)
        self.__th.Describe()

    def ReadFile(self):
        self.__lines = self.__fd.readlines()

    def ReadHeader(self):
        self.__h = Header()
        self.__h.SetLine(self.__lines[0])
        self.__h.Analyse()

    def ReadRest(self):
        fs = self.__h.GetSeparator()
        fe = self.__h.GetFieldStringsEnclosedBy()
        for n in range(1, len(self.__lines)):
            content = string.strip(self.__lines[n])
            if fe:
                content = string.replace(content, fe, "'")
            content = string.split(content, fs)
            self.__content.append(content)

    def InsertIntoAddFieldNames(self):
        f = self.__h.GetAll()
        s = ''
        k = 0
        j = len(f) - 1
        for i in f:
            s = s + i
            if k < j:
                s = s + ', '
            k = k + 1
        return s

    def CheckDate(self, i, t):
        try:
            if i[2] == '/' and i[5] == '/':
                m = int(i[0:2])
                d = int(i[3:5])
                y = int(i[6:10])
            elif i[2] == '.' and i[5] == '.':
                d = int(i[0:2])
                m = int(i[3:5])
                y = int(i[6:10])
            if len(i) > 10:
                h = int(i[11:13])
                mi = int(i[14:16])
                s = int(i[17:19])
            else:
                h, mi, s = 0, 0, 0
            if not t:
                r = str(time.mktime(y, m, d, h, mi, s, 0, 0, -1))
            else:
                r = "TO_DATE('%s-%s-%s %s:%s:%s', 'YYYY-MM-DD HH24:MI:SS')" % (y, m, d, h, mi, s)
        except:
            if not t:
                r = '0'
            else:
                r = "TO_DATE(NULL, 'YYYY-MM-DD')"
        return r

    def CheckTrueFalse(self, l):
        u = string.upper(l)
        t1 = string.find(u, 'TRUE')
        t2 = string.find(u, 'WAHR')
        f1 = string.find(u, 'FALSE')
        f2 = string.find(u, 'FALSCH')
        if t1 >= 0 or t2 >= 0:
            return '1'
        elif f1 >= 0 or f2 >= 0:
            return '0'
        else:
            return l

    def CheckValue(self, fieldName, value):
        fieldType = self.__th.GetRealFieldType(fieldName)
        if fieldType == 'VARCHAR2':
            if value:
                value = string.replace(value, '"', '')
                value = string.replace(value, "&", " + ")
                if self.__h.GetFieldStringsEnclosedBy():
                    value = "'" + string.replace(value[1:-1], "'", "") + "'"
                else:
                    value = "'" + string.replace(value, "'", "") + "'"
            else:
                #value = "''"
                value = 'NULL'
        elif fieldType == 'DATE':
            value = self.CheckDate(value, 1)
        elif fieldType == 'NUMBER':
            if value:
                if len(value) >= 10:
                    value = self.CheckDate(value, 0)
                else:
                    value = string.replace(value, ' ', '')
                    #value = string.replace(value, '.', '')
                    value = string.replace(value, ',', '.')
            else:
                #value = '0'
                value = 'NULL'
        return string.strip(value)

    def printList(self, l):
        s = ''
        for i in l:
            s = s + i + ' --- '
        return s

    def Eval(self):
        fieldNames = self.__h.GetAll()
        lenFieldNames = len(fieldNames)
        for c in range(len(self.__content)):
            values = self.__content[c]
            s = 'INSERT INTO ' + self.__tableName + ' (' + self.InsertIntoAddFieldNames() + ') VALUES ('
            i = 0
            lenValueLine = len(values)
            if lenValueLine < lenFieldNames:
                print t(), '!!!      INVALID DATA: LENGTHS: HEADER=%i > VALUES=%i, ID=%s' % (lenFieldNames, lenValueLine, values[0])
            if lenValueLine > lenFieldNames:
                print t(), '!!!      INVALID DATA: LENGTHS: HEADER=%i < VALUES=%i, ID=%s' % (lenFieldNames, lenValueLine, values[0])
            for i in range(lenFieldNames):
                fieldName = fieldNames[i]
                try:
                    value = values[i]
                    value = str(value)
                except:
                    value = None
                if lenValueLine > lenFieldNames and i == lenFieldNames - 1:
                    for i in range(lenFieldNames, lenValueLine):
                        value = value + values[i]
                value = self.CheckValue(fieldName, value)
                value = self.CheckTrueFalse(value)
                s = s + value
                if i < lenFieldNames - 1:
                    s = s + ', '
                i = i + 1
            s = s + ');'
            self.__stmts.append(s)

    def Get(self):
        return self.__stmts
            
def main():
    # fdout_sqlplus = open('masy-data.sql', 'a+')
    for arg in sys.argv[1:]:
        print t(), '***      START', arg
        i = ImportCSV(arg)
        i.ReadFile()
        i.ReadHeader()
        i.ReadRest()
        try:
            i.Eval()
        except:
            print t(), '!!!      ERROR', arg, sys.exc_info()[0], sys.exc_info()[1]
        # fdout_sqlplus.write('@' + arg + '.sql' + '\n')
        fdout = open(arg + '.sql', 'w')
        fdout.write(
            """SET termout ON
SET feedback OFF
SET echo OFF
SET serveroutput ON
SET heading OFF
SET verify OFF
SET pagesize 0
SET linesize 132

SPOOL %s.import.lst
""" % arg
            )
        j = 0
        for s in i.Get():
            j = j + 1
            fdout.write(s + '\n')
            fdout.write('\n')
        fdout.write('COMMIT;\n')
        fdout.close()
        # fdout_sqlplus.close()
        print t(), '***  PROCESSED', j, 'ROWS FOR', arg
        print t(), '***       STOP', arg
        print '-' * 80
        print

main()
