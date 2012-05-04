#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.2 $'
#
##

import sys
import time
import os
import os.path
import string
import traceback

class Extracted:

    """ return data from extracted traceback """

    def __init__(self, e):
        self.__e = e
        self.__sum = {}
        self.__sumOrder = []

    def SetModuleName(self):
        """ get name of module that raised the error """
        self.__moduleName = os.path.normpath(os.path.splitext(self.__e[-1][0])[0])

    def GetModuleName(self):
        return self.__moduleName

    def SetLineNo(self):
        """ set line number that raised the error """
        self.__lineNo = self.__e[-1][1]

    def GetLineNo(self):
        return self.__lineNo

    def GetSum(self):
        return self.__sumOrder, self.__sum

    def Summarize(self):
        """
        summarize the traceback stack
        if one function fails at the same line more than one time
        summarize it
        """
        save = {}
        order = []
        for item in self.__e:
            prg, lineno, func = item[0], item[1], item[2]
            if not save.has_key((prg, func)):
                save[(prg, func)] = [1, lineno, func]
                order.append((prg, func))
            elif lineno != save[(prg, func)][1]:
                save[(prg, func)][0] = save[(prg, func)][0] + 1
        self.__sum = save
        self.__sumOrder = order

class UserMessage:

    """ """

    def __init__(self, tb):
        self.__tb = tb
        self.__number = 1
        self.__message = ''
        self.__messageOpt = ''

    def SetNumber(self):
        """ set error number got from traceback """
        try:
            self.__number = self.__tb[0]
        except:
            self.__number = 1

    def GetNumber(self):
        return self.__number

    def SetMessage(self):
        try:
            self.__message = self.__tb[1]
        except:
            self.__message = 'Sorry - no message'

    def GetMessage(self):
        return self.__message

    def SetMessageOpt(self):
        try:
            self.__messageOpt = self.__tb[2]
        except:
            self.__messageOpt = ''

    def GetMessageOpt(self):
        return self.__messageOpt

    def Init(self):
        self.SetNumber()
        self.SetMessage()
        self.SetMessageOpt()

class Traceback:

    """ handle informations of a traceback """

    def __init__(self, tb):
        self.__tb = tb
        self.extracted = None
        self.userMessage = None

    def Extract(self):
        self.__stack = traceback.extract_stack()
        self.extracted = Extracted(traceback.extract_tb(self.__tb[2]))

    def UserMessage(self):
        self.userMessage = UserMessage(self.__tb[1])

    def SetRaisedError(self):
        """ get name/class of raised error """
        self.__raisedError = self.__tb[0]

    def GetRaisedError(self):
        return self.__raisedError

    def GetTraceback(self):
        return str(self.__tb[0]) + " " + str(self.__tb[1]) + " " + str(traceback.extract_tb(self.__tb[2]))

    def Init(self):
        """ retrieve all information from traceback """
        self.Extract()
        self.UserMessage()
        self.SetRaisedError()
        self.extracted.SetModuleName()
        self.extracted.SetLineNo()
        self.userMessage.SetNumber()
        self.userMessage.SetMessage()
        self.userMessage.SetMessageOpt()
