#!/usr/bin/env python
##
#
# $Author: rb $
revision =  '$Revision: 2.25 $'
#
# Revision 1.1  2000-05-11 15:04:06+02  rb
# Initial revision
#
##

import sys
import string
import Core.Log
#import Core.OS
import Core.Error
import DMS.Counter
import DTL.TemplateMacro
import DTL.TemplateBuffer
import DTL.TemplateRepeat
import DTL.TemplateIf

class ParseTemplate(DTL.TemplateMacro.Lib):

    """ first time parse a dmerce template """

    def __init__(self, **kw):
        """
        call constructor of DTL.Macro.Lib
        get vars for: debug, sql, sam, cgi, macros, parsing, dboids,
        counter, ...
        """
        DTL.TemplateMacro.Lib.__init__(self)
        self.__kw = kw
        self.__debug = kw['debug']
        self.__sam = kw['sam']
        self.__cgi = kw['cgi']
        self.__sqlSys = kw['sqlSys']
        self.__sql = kw['sql']
        self.__httpEnv = kw['httpEnv']
        self.__inputBuffer = kw['inputBuffer']
        self.fdout = sys.stdout
        self.__log = Core.Log.File(debug = self.__debug, module = '1[dmerce].TemplateParse',
                                   httpEnv = self.__httpEnv)
        self.__mt = DTL.TemplateMacro.MacroTypes()
        self.__parsedBuffer = DTL.TemplateBuffer.ParsedBuffer()
        self.__c = DMS.Counter.Counter()
        self.__depthRepeat = 0
        self.__depthIf = 0
        self.__repeats = []
        self.__ifs = []
        #self.m = Core.OS.Base()

    def __Subst(self, m, s, l):
        """
        substitute macro m with string s in line l using the
        method regexp.Subst
        """
        return self.regexp.Subst(l, DTL.TemplateRegExp.macros[m], str(s))

    def __ProcessMacroForm(self, line):
        """
        (form): find and process
        substitute macro with values from __cgi.GetField()
        """
        var = self.ScanForm(line)
        if not var:
            return line
        for element in range(len(var)):
            f = DTL.TemplateMacro.MacroForm(self.__cgi, var[element])
            line = self.__Subst('form', f.Get(), line)
        return line

    def __ProcessMacroVar(self, line):
        """
        (var) or (var ... rav): find and process
        substitute macro with values from __cgi.GetqVar()
        log warning when request var was not found in __cgi.qVars
        """
        var = self.ScanVar(line)
        if var:
            for element in range(len(var)):
                v = DTL.TemplateMacro.MacroVar(self.__cgi, var[element])
                line = self.__Subst('var', v.Get(), line)
        varrav = self.ScanVarRav(line)
        if varrav:
            for element in range(len(varrav)):
                v = DTL.TemplateMacro.MacroVar(self.__cgi, varrav[element])
                line = self.__Subst('var-rav', v.Get(), line)
        return line

    def __ProcessMacroQ(self, line):
        """
        (q): find and process
        get every requested component from qSuperSearch
        """
        var = self.ScanQ(line)
        if not var:
            return line
        for element in range(len(var)):
            superSearch = var[element][0]
            component = var[element][1]
            q = DTL.TemplateMacro.MacroQ(superSearch, component)
            line = self.__Subst('q', q.Get(), line)
        return line

    def __ProcessMacroInclude(self, line):
        """
        (include): find and process
        include all lines from another file
        """
        var = self.ScanInclude(line)
        if not var:
            return line
        for element in range(len(var)):
            lc = self.__inputBuffer
            template = self.__httpEnv['DOCUMENT_ROOT'] + '/' + var[element]
            lines = open(template, 'r').readlines()
            for line in lines:
                self.__log.Write(msg = 'INSERTING AT POSITION %i' % lc)
                self.__inputBuffer.insert(lc, line)
                lc = lc + 1
            self.__log.Write(msg = 'INCLUDED %s LINES FROM FILE %s, %s'
                             % (len(lines), template, str(self.__inputBuffer)))
        return ''

    def __ProcessMacroWho(self, line):
        """ (who): find and process """
        var = self.ScanWho(line)
        if not var:
            return line
        for element in range(len(var)):
            if self.__sam:
                line = self.__Subst('who', self.__sam.GetWho(var[element]), line)
            else:
                line = self.__Subst('who', '', line)
        return line

    def __ProcessMacroDBOID(self, line):
        """
        (sql dboid x): find and process
        count sql queries
        write dboid to debug log
        """
        var = self.ScanSQLDBOID(line)
        if not var:
            return line
        for element in var:
            #self.__c.Count('SQL', 2)
            dboid = self.__dboid[table]
            line = self.__Subst('sql-dboid', dboid, line)
            self.__log.Write(submodule = 'DBOID', msg = 'DBOID FOR TABLE %s=%i'
                             % (table, dboid))
        return line

    def __CacheSQLVar(self, var):
        for element in range(len(var)):
            try:
                ve = var[element]
                if ve[0]:
                    schema = ve[0][:-2]
                else:
                    schema = None
                table = ve[1]
                field = ve[2]
            except:
                raise Core.Error.CacheSQLCannotGetElements(0, 'ELEMENTS NOT CORRECT TO CACHE TABLE.FIELD FROM %s' % str(var))
            try:
                self.__log.Write(msgType = 'INFO', submodule = '__CacheSQL', msg = 'CACHING %s%s.%s%s FROM SCHEMA=%s' % (func1, table, field, func2, schema))
                we = self.__WhichEndrepeat()
                #self.__log.Write(msgType = 'INFO', submodule = '__CacheSQL', msg = 'CACHING %s%s.%s%s FROM SCHEMA=%s IN REPEAT=%s' % (func1, table, field, func2, schema, str(we)))
                self.__repeats[we].CacheSQL(schema, table, field)
            except:
                raise Core.Error.RepeatNotFoundError(1, 'DON\'T KNOW REPEAT FOR TABLE %s FIELD %s.'
                                                     ' MAYBE OUTSIDE OF A REPEAT?' % (table, field))

    def __CacheSQL(self, line):
        """
        look for (sql t.f) / (sqlf t.f) and cache it
        write caching of table.field to debug log
        """
        var = self.ScanTableField(line)
        var2 = self.ScanTableFieldFormatted(line)
        if not var and not var2:
            return
        if var:
            self.__CacheSQLVar(var)
        if var2:
            self.__CacheSQLVar(var2)

    def __IncCountRepeat(self):
        """ increase repeat counter """
        self.__c.Count('Repeat')

    def __GetCountRepeat(self):
        return self.__c['Repeat']

    def __IncDepthRepeat(self):
        """ increase repeat depth """
        self.__depthRepeat = self.__depthRepeat + 1

    def __DecDepthRepeat(self):
        """ decrease repeat depth """
        self.__depthRepeat = self.__depthRepeat - 1

    def __GetDepthRepeat(self):
        """ return value of repeat depth """
        return self.__depthRepeat

    def __IncCountIf(self):
        """ increase if counter """
        self.__c.Count('M_if')

    def __GetCountIf(self):
        """ return value of if-count """
        return self.__c['M_if']

    def __IncDepthIf(self):
        """ increase if depth """
        self.__depthIf = self.__depthIf + 1

    def __DecDepthIf(self):
        """ decrease if depth """
        self.__depthIf = self.__depthIf - 1

    def __GetDepthIf(self):
        """ return value of if depth """
        return self.__depthIf

    def __AddRepeat(self, l):
        """ we found a repeat;
        - count
        - are we in an inner-repeat?
        - check it and generate objects
        """
        r = DTL.TemplateRepeat.Repeat(self.__kw, l)
        if self.__GetDepthRepeat() > 0:
            r.SetInner()
            r.SetParent(self.__GetCountRepeat() - 1)
        self.__repeats.append(r)

    def __WhichEndrepeat(self):
        """
        reduce depthRepeat-counter
        and determine which repeat is closed here
        step back through the list of repeat-instances
        look which repeat has no lineTo set
        if all are closed, return actual repeat
        """
        for item in range(len(self.__repeats) - 1, -1, -1):
            if not self.__repeats[item].GetLineTo():
                return item

    def __WhichEndif(self):
        """
        reduce depthIf-counter
        and determine which repeat is closed here
        step back through the list of if-instances
        look which if has no lineTo set
        if all are closed, return actual if
        """
        for item in range(len(self.__ifs) - 1, -1, -1):
            if not self.__ifs[item].GetLineTo():
                return item

    def __ParseIf(self, l):
        self.__c.Count('M_if')
        self.__ifs.append(DTL.TemplateIf.If(self.__debug, l))
        self.__IncCountIf()
        self.__IncDepthIf()
        #self.__log.Write(msg = '__ParseIf: %s COUNT=%s DEPTH=%s' % (l, self.__c['M_if'], self.__depthIf))

    def __ParseElse(self, l):
        self.__c.Count('M_else')
        try:
            self.__ifs[self.__WhichEndif()].SetElseLineFrom(l)
        except:
            self.__log.Write(msgType = 'ERROR', msg = 'FOUND (else) WITHOUT (if)')

    def __ParseEndif(self, l):
        we = self.__WhichEndif()
        try:
            self.__ifs[we].SetLineTo(l)
        except:
            self.__log.Write(msgType = 'ERROR', msg = 'FOUND (endif) WITHOUT (if)')
        self.__DecDepthIf()

    def __WhatIfIsLine(self, l):
        for item in range(len(self.__ifs) -1, -1, -1):
            glf = self.__ifs[item].GetLineFrom()
            glt = self.__ifs[item].GetLineTo()
            if not glt:
                glt = 9999999999
            if l >= glf and l < glt:
                return item + 1
        return -1

    def __ParseRepeat(self, l):
        self.__c.Count('M_repeat')
        self.__AddRepeat(l)
        self.__IncCountRepeat()
        self.__IncDepthRepeat()
        #self.__log.Write(msg = '__ParseRepeat: %s COUNT=%s DEPTH=%s' % (l, self.__c['M_repeat'], self.__depthRepeat))
    
    def __ParseEndrepeat(self, l):
        we = self.__WhichEndrepeat()
        try:
            self.__repeats[we].SetLineTo(l)
        except:
            self.__log.Write(msgType = 'ERROR', msg = 'FOUND (endrepeat) WITHOUT (repeat)')
        self.__DecDepthRepeat()

    def __WhatRepeatIsLine(self, l):
        for item in range(len(self.__repeats) -1, -1, -1):
            glf = self.__repeats[item].GetLineFrom()
            glt = self.__repeats[item].GetLineTo()
            if not glt:
                glt = 9999999999
            if l >= glf and l < glt:
                return item + 1
        return -1

    def __EvaluateMacros(self, macros, line):
        """
        evaluate macros in line:
        form, var, q
        """
        line = self.__ProcessMacroWho(line)
        line = self.__ProcessMacroForm(line)
        line = self.__ProcessMacroVar(line)
        line = self.__ProcessMacroQ(line)
        line = self.__ProcessMacroInclude(line)
        return line

    def Parse(self):
        """
        parse a dmerce template and generate objects
        set line counter
        look for macros, count macros if any
        add parsed line to instance of parsedBuffer
        """
        for line in self.__inputBuffer:
            l = self.__parsedBuffer.GetLinesCount()
            macros = self.__mt.RecognizeMacros(line)
            self.__c.Count('Macros', len(macros))
            if macros:
                line = self.__EvaluateMacros(macros, line)
                if self.__GetCountRepeat() > -1:
                    self.__CacheSQL(line)
                if self.__mt['(if'] in macros:
                    self.__ParseIf(l)
                elif self.__mt['(else'] in macros:
                    line = ''
                    self.__ParseElse(l)
                elif self.__mt['(endif'] in macros:
                    line = ''
                    self.__ParseEndif(l)
                if self.__mt['(repeat'] in macros:
                    self.__ParseRepeat(l)
                    #self.__log.Write(msg = 'PARSING REPEAT LINE=%s' % line)
                elif self.__mt['(endrepeat'] in macros:
                    line = ''
                    self.__ParseEndrepeat(l)
            #self.__log.Write(msg = 'ADDING LINE=%s TO PARSED BUFFER: WHAT-IF-IS-LINE=%s'
            #                 % (l, self.__WhatIfIsLine(l)))
            self.__parsedBuffer.AddLine(macros, self.__WhatIfIsLine(l), self.__WhatRepeatIsLine(l), line)
        #self.__c.Count('AllLines', self.__parsedBuffer.GetLinesCount())
        return (self.__kw, self.__c, self.__parsedBuffer,
                self.__GetCountRepeat(), self.__repeats,
                self.__GetCountIf(), self.__ifs)
