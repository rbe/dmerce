#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.30 $'
#
# Revision 1.1  2000-07-05 20:48:32+02  rb
# Initial revision
#
##

import types
import string
import pickle
import os.path
import Core.Log
#import Core.OS
import Core.RegExp
import DMS.SuperSearch
import DMS.Statement
import DMS.Filesystem
import DMS.Object
import DTL.TemplateRegExp
import DTL.TemplateFc

class Lib:

    """ recognise macros and constructs """

    def __init__(self):
        self.regexp = Core.RegExp.Lib()
    
    def ScanInclude(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['include'])

    def ScanForm(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['form'])

    def ScanVar(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['var'])

    def ScanVarRav(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['var-rav'])

    def ScanQ(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['q'])

    def ScanGetMyVar(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['get-myvar'])

    def ScanSetMyVar(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['set-myvar'])

    def ScanAppendMyVar(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['append-myvar'])

    def ScanInclude(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['include'])

    def ScanWho(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['who'])

    def ScanSQLDBOID(self, line):
        var = self.regexp.FindAll(line, DTL.TemplateRegExp.macros['sql-dboid'])
        if not var:
            return 0
        else:
            return var
    
    def ScanTableField(self, line):
        var = self.regexp.FindAll(line, DTL.TemplateRegExp.macros['sql-get'])
        if not var:
            return 0
        else:
            return var
    
    def ScanTableFieldFormatted(self, line):
        var = self.regexp.FindAll(line, DTL.TemplateRegExp.macros['sql-getf'])
        if not var:
            return 0
        else:
            return var
    
    def ScanTableFieldXref(self, line):
        var = self.regexp.FindAll(line, DTL.TemplateRegExp.macros['sql-get-xref'])
        if not var:
            return 0
        else:
            return var

    def ScanTableFieldXrefFormatted(self, line):
        var = self.regexp.FindAll(line, DTL.TemplateRegExp.macros['sql-getf-xref'])
        if not var:
            return 0
        else:
            return var

    def ScanSQLCount(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['sql-count'])

    def ScanSQLRowCount(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['sql-rowcount'])

    def ScanSQLIndex(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['sql-index'])

    def ScanSQLIndex1(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['sql-index1'])

    def ScanSQLSelect(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['sql-select'])

    def ScanExec(self, line):
        var = self.regexp.FindAll(line, DTL.TemplateRegExp.macros['exec'])
        if not var:
            return 0
        return var

    def ScanIfCond(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['if'])

    def ScanElseCond(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['else'])

    def ScanEndifCond(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['endif'])

    def ScanRepeat(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['repeat'])

    def ScanEndrepeat(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['endrepeat'])

    def ScanSetSkin(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['set-skin'])

    def ScanSkinImg(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['skin-img'])

    def ScanSkinImgTag(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['skin-imgtag'])

    def ScanSkinStylesheet(self, line):
        return self.regexp.FindAll(line, DTL.TemplateRegExp.macros['skin-stylesheet'])

class MacroTypes:

    """
    macros that can be used in dmerce templates
    this class hold information about macros
    you can look for macros in strings
    found macros are counted
    """

    def __init__(self):
        self.__macros = {
            '(form' : 1, '(var' : 2, '(set-myvar' : 3, '(get-myvar' : 4,
            '(append-myar' : 5,
            '(q' : 6, '(who' : 7,
            '(exec' : 8, '(if' : 9, '(else' : 10,
            '(endif' : 11, '(repeat' : 12,
            '(endrepeat' : 13,
            '(sql' : 14, '(sql count' : 15, '(sql rowcount' : 16,
            '(sql index' : 17, '(sql index1' : 18, '(sql dboid' : 19, '(sql dboid-inserted' : 20,
            '(sql SELECT' : 21,
            '(include' : 22,
            '(set-skin' : 23, '(skin-img' : 24, '(skin-imgtag' : 25, '(skin-stylesheet' : 26,
            '(sqlf': 27
            }

    def __getitem__(self, item):
        return self.__macros[item]

    def Find(self, s):
        """ find a macro in string s and count macros found """
        macrosFound = []
        for m in self.__macros.keys():
            if string.find(s, m) >= 0:
                macrosFound.append(self.__macros[m])
        return macrosFound

    def RecognizeMacros(self, line):
        """ recognizes macros in a line """
        macrosInLine = []
        if string.find(line, '(') >= 0 and string.find(line, ')') >= 0:
            macrosInLine = self.Find(line)
        return macrosInLine

class MacroForm:

    """ variables from cgi form """

    def __init__(self, cgi, element):
        self.__cgi = cgi
        self.__element = element

    def Get(self):
        v = str(self.__cgi.GetField(self.__element))
        if v == 'None':
            v = ''
        return v

class MacroVar:

    """ dmerce special variables """

    def __init__(self, qvar, element):
        self.__qvar = qvar
        self.__element = element

    def Get(self):
        try:
            v = str(self.__qvar.GetqVar(self.__element))
        except:
            v = str(self.__qvar.Get(self.__element))
        if v == 'None':
            v = ''
        return v

class MacroMyVar:

    """ session variables """

    def __init__(self, documentRoot, sessionId):
        self.__documentRoot = documentRoot
        self.__sIdFile = '%s/SAM/myvar.%s' % (documentRoot, sessionId)
        self.__myvar = {}
        self.__log = Core.Log.File(debug = 1, module = '1[TemplateMacro].MyVar')

    def OpenFile(self):
        """ open/create file """
        try:
            self.Read()
        except:
            self.Write()

    def Get(self, var):
        """ get var """
        try:
            v = str(self.__myvar[var])
        except:
            v = '[myvar %s not found]' % var
        if v == 'None':
            v = ''
        return v

    def Set(self, var, value):
        """ set var """
        self.__myvar[var] = value
        #self.Write()

    def Append(self, var, value):
        """ append value to actual value of var """
        self.__myvar[var] = self.__myvar[var] + value
        #self.Write()

    def ayt(self, var):
        return self.__myvar.has_key(var)

    def Read(self):
        """ unserialize """
        f = open(self.__sIdFile, 'r')
        up = pickle.Unpickler(f)
        self.__myvar = up.load()
        #self.__log.Write(msg = 'READ SESSION.MYVAR-FILE %s, %s' % (self.__sIdFile, str(self.__myvar.keys())))

    def Write(self):
        """ serialize myvar """
        f = open(self.__sIdFile, 'w')
        p = pickle.Pickler(f)
        p.dump(self.__myvar)
        f.close()
        #self.__log.Write(msg = 'WROTE SESSION.MYVAR-FILE %s, %s' % (self.__sIdFile, str(self.__myvar.keys())))

    def Copy(self, oldSid, newSid):
        pre = '%s/SAM/myvar.' % self.__documentRoot
        return os.symlink(pre + oldSid, pre + newSid)
        self.__log.Write(msg = 'SYMLINK SESSION.MYVAR-FILE IN %s: %s -> %s' % (pre, oldSid, newSid))

class MacroQ:

    def __init__(self, superSearch, component):
        self.__superSearch = superSearch
        self.__component = component

    def Get(self):
        qss = DMS.SuperSearch.SuperSearch(self.__superSearch)
        return string.replace(qss.GetComponent(self.__component), '\'', '')

class MacroWho:

    """ get information about who is logged in a session """

    def __init__(self, sam, what):
        self.__sam = sam
        self.__what = what

    def Get(self):
        return self.__sam.GetWho(self.__what)

    def __str__(self):
        return self.Get()

class MacroSQLDBOID:
    pass

class MacroSQL:
    pass

class MacroSQLCount:

    """ process macro sql count """

    def __init__(self, debug, sql, table, cond):
        self.__log = Core.Log.File(debug = debug, module = '1[SQLCount]')
        self.__sql = sql
        self.__table = table
        self.__cond = cond
        self.__cache = DMS.Cache.Cache()

    def Get(self):
        k = '%s-%s' % (self.__table, self.__cond)
        if not self.__cache[k]:
            qss = DMS.SuperSearch.SuperSearch(self.__cond)
            self.__cache[k] = self.__sql.CountOf(table = self.__table, cond = qss.ConvertToSQLWhere())
            self.__log.Write(msg = 'GOT COUNT OF TABLE=%s COND=%s, RESULT=%s'
                             % (self.__table, qss.ConvertToSQLWhere(), self.__cache[k]))
        return self.__cache[k]

class MacroSQLXref:

    """ process macro xref """

    def __init__(self, debug, sql, schema, table, field, xref):
        self.__sql = sql
        self.__schema = schema
        self.__table = table
        self.__field = field
        self.__xref = xref
        self.__log = Core.Log.File(debug = debug, module = '1[SQLXref]')
    
    def Get(self):
        """
        get table.field with additional query
        if found, process normal (sql t.f) in xref
        count sql query
        return value or log error
        """
        qss = DMS.SuperSearch.SuperSearch(self.__xref)
        #stmt = "SELECT %s FROM %s WHERE %s LIMIT 0,1" % (self.__field, self.__table, qss.ConvertToSQLWhere())
        if self.__schema:
            stmt = "SELECT %s FROM %s.%s WHERE %s" % (self.__field, self.__schema[:-2], self.__table,
                                                      qss.ConvertToSQLWhere())
        else:
            stmt = "SELECT %s FROM %s WHERE %s" % (self.__field, self.__table, qss.ConvertToSQLWhere())
        #self.__log.Write(msg = 'GOT XREF: SQL STMT=%s' % stmt)
        rc, r = self.__sql[stmt]
        #self.__log.Write(msg = 'GOT XREF: RC=%s, R=%s' % (str(rc), str(r)))
        if rc:
            return r[0][self.__field]
        else:
            self.__log.Write(submodule = 'Get', msg = 'CANNOT GET %s%s.%s WHERE %s: %s'
                             % (self.__schema, self.__table, self.__field, self.__xref, stmt))
            return ''

class MacroSQLSelect:

    """ process macro sql-select """

    def __init__(self, debug, sql, stmt):
        self.__sql = sql
        self.__stmt = stmt
        self.__log = Core.Log.File(debug = debug, module = 'SQLSelect')
    
    def Get(self):
        rc, r = self.__sql[self.__stmt]
        k = r[0].keys()
        #self.__log.Write(msg = 'SQL SELECT: STMT=%s R=%s' % (self.__stmt, r))
        return r[0][k[0]]

class MacroExec(DTL.TemplateFc.Function):

    """ execute a function """

    def __init__(self, kw, package, clazz, method, args):
        self.__kw = kw
        self.__package = package
        self.__clazz = clazz
        self.__method = method
        self.__args = args
        self.__function = DTL.TemplateFc.Function(kw)

    def __str__(self):
        return '%s' % self.__returnValue

    def Get(self):
        self.__returnValue = self.__function.Call(package = self.__package,
                                                  clazz = self.__clazz,
                                                  method = self.__method,
                                                  args = self.__args)

    def CheckArguments(self):
        """
        checks a function call
        - when a mad function call is detected:
          - substitute =, with =0,
          - substitute = at the end of arguments without a value
            with =0
        """
        p = 1
        while p != -1:
            p = string.find(self.__args, '=,')
            if p == -1:
                break
            self.__args = self.__args[:p + 1] + '0' + self.__args[p + 1:]
        if self.__args[-1:] == '=':
            self.__args = self.__args + '0'

    def CheckReturnValue(self):
        """
        check return value of function call
        - convert list as a result to string with newlines
        """
        if type(self.__returnValue) is types.ListType:
            o = DMS.Object.Convert()
            self.__returnValue = o.ListToStringWithNewlines(self.__returnValue)

class MacroSkin:

    """ deal with several skins """

    def __init__(self, kw = {}):
        self.__kw = kw
        self.__httpEnv = kw['httpEnv']
        self.__skin = 0
        #self.__m = Core.OS.Base()
        self.__dw = DMS.Filesystem.Directory()
        self.__skin = ''
        self.__files = []

    def Set(self, s):
        self.__skin = s
        if not self.__files:
            self.__files = self.__dw.SearchFiletype('%s/skins/%s'
                                                    % (self.__httpEnv['DOCUMENT_ROOT'], s),
                                                    ext = ['.jpeg', '.jpg', '.gif'])
            self.__files.sort()

    def GetImage(self, img):
        imageFile = ''
        ch = DMS.Object.Check()
        img = ch.AppendDotToString(img)
        for file in self.__files:
            if string.find(file, img) >= 0:
                imageFile = file
        if self.__skin and imageFile:
            s = '/skins/' + self.__skin + '/' + os.path.basename(imageFile)
        else:
            s = 'notfound.gif'
        return s

    def GetImageTag(self, img):
        return '<img src="%s">' % self.GetImage(img)

    def GetStylesheet(self):
        if self.__skin:
            s = '<link rel="stylesheet" href="/skins/%s/style.css" type="text/css">' % self.__skin
        else:
            s = '<!-- 1Ci/dmerce: stylesheet for skin not found -->'
        return s
