#!/usr/bin/env python
##
#
# $Author: ka $
revision =  '$Revision: 2.6 $'
#
# Revision 1.1  2000-05-11 15:04:06+02  rb
# Initial revision
#
##

class Line:

    """
    a line:
      - plain text
      - some additional information about that line
    """

    def __init__(self, macros, isRepeat, isIf, line):
        self.__macros = macros
        self.__isIf = isIf
        self.__isRepeat = isRepeat
        self.__line = line

    def GetMacros(self):
        """ has this line macros? """
        return self.__macros

    def SetMacros(self, macros):
        """ this line has macros! """
        self.__macros = macros

    def isIf(self):
        return self.__isIf

    def isRepeat(self):
        return self.__isRepeat

    def Get(self):
        return self.__line

class ParsedBuffer:

    """
    buffer that holds lines (instances of Line) and additional information
    this class takes some data structures as arguments and creates a evaluated
    copy of the original buffer
    """

    def __init__(self, buf = None):
        if not buf:
            self.__buf = []
        else:
            self.__buf = buf
        self.__pointer = 0

    def GetLinesCount(self):
        return len(self.__buf)

    def GetLinePointer(self):
        return self.__pointer

    def SetLinePointer(self, l):
        self.__pointer = l

    def AddLine(self, macros, isIf, isRepeat, line):
        """ add instance of line to buffer """
        self.__buf.append(Line(macros, isRepeat, isIf, line))

    def GetLine(self, lp):
        """ get a line specified through lp
        from stack """
        return self.__buf[lp]

    def GetNextLine(self):
        """
        increases line pointer and return that line
        returns None if no more lines are present
        """
        try:
            l = self.__buf[self.__pointer]
            self.__pointer = self.__pointer + 1
            return l
        except:
            return None

    def NextLine(self):
        """ increase line pointer """
        self.__pointer = self.__pointer + 1
        return self.__pointer

    def GetLines(self):
        for i in self.__buf:
            print i.Get()
