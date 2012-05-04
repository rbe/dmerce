#!/usr/bin/env python
##
#
# $Author: rb $
revision =  '$Revision: 2.3 $'
#
# Revision 1.1  2000-05-11 15:04:06+02  rb
# Initial revision
#
##

import Core.Log

class If:

    """ evaluate an if-expression """

    def __init__(self, debug, lineFrom, elseLineFrom = None, lineTo = None):
        self.__lineFrom = lineFrom
        self.__elseLineFrom = elseLineFrom
        self.__else = 0
        self.__lineTo = lineTo
        self.__expr = None
        self.__lp = None
        self.__log = Core.Log.File(debug = debug, module = '1[Template].If')

    """ set and get attributes """

    def SetLineFrom(self, lf):
        self.__lineFrom = lf

    def GetLineFrom(self):
        return self.__lineFrom

    def SetElseLineFrom(self, lf):
        self.__elseLineFrom = lf

    def GetElseLineFrom(self):
        return self.__elseLineFrom

    def SetLineTo(self, lt):
        self.__lineTo = lt

    def GetLineTo(self):
        return self.__lineTo

    def SetLinePointer(self, lp):
        self.__linePointer = lp

    def GetLinePointer(self):
        return self.__linePointer

    def GetNextLine(self, n = 1):
        """ return next line """
        #self.SetLinePointer(self.GetLinePointer() + n)
        if self.GetLinePointer() > self.GetLineTo():
            self.SetLinePointer(self.GetLineTo() + 1)
        if self.GetLinePointer() == self.GetElseLineFrom() and not self.isElse():
            self.SetLinePointer(self.GetLineTo() + 1)
        return self.GetLinePointer()

    def __SetElse(self):
        """ expr was not true: use else block when possible """
        self.__else = 1

    def __UnsetElse(self):
        self.__else = 0

    def isElse(self):
        if self.__else:
            return 1
        else:
            return 0

    def Set(self, e):
        """ set expression to eval """
        self.__expr = e

    def Get(self):
        """ return expression """
        return self.__expr

    def Eval(self):
        """
        evaluate expression
        if true, return lineFrom
        else if we have a line for (else) return that line
        else return lineTo
        """
        logLines = 'IN LINES: %s-%s-%s' % (str(self.GetLineFrom()),
                                           str(self.GetElseLineFrom()),
                                           str(self.GetLineTo()))
        try:
            #self.__log.Write(submodule = 'Eval', msg = 'IF-EXPR.: %s %s, GOING TO LINE %s'
            #                 % (str(self.__expr), logLines, str(self.GetLinePointer())))
            if eval(self.__expr):
                self.__UnsetElse()
                self.SetLinePointer(self.GetLineFrom())
            else:
                if not self.GetElseLineFrom():
                    self.SetLinePointer(self.GetLineTo())
                else:
                    self.__SetElse()
                    self.SetLinePointer(self.GetElseLineFrom())
        except SyntaxError:
            self.__log.Write(submodule = 'Eval', msgType = 'WARNING', msg = 'SYNTAX ERROR IN IF-EXPR. %s: %s'
                             % (logLines, str(self.__expr)))
            self.SetLinePointer(self.GetLineTo())

    def Check(self):
        """
        check if (if)-construct is ok:
        1. do we have lineFrom
        2. and lineTo?
        3. expr set?
        """
        ok = 1
        if not self.__lineFrom:
            ok = 0
        if not self.__lineTo:
            ok = 0
        if not self.__expr:
            ok = 0
        return ok
