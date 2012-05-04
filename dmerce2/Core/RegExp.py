#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.2 $'
#
##

import re
import Core.OS

class Lib:

    """ regular expression functions """

    def FindAll(self, line, regex, compile = 0):
        """
        find all regex 'regex' in string 'line'
        compile: must we compile a regex before, or is it compiled?
        """
        if compile:
            regex = re.compile(regex)
        mm = regex.findall(line)
        if mm:
            return mm
        else:
            return 0

    def Match(self, line, regex, compile = 0):
        """
        find regex 'regex' in string 'line'
        compile: must we compile a regex before, or is it compiled?
        """
        if compile:
            regex = re.compile(regex)
        mm = regex.match(line)
        if mm != None:
            return mm.groups()
        else:
            return 0

    def Subst(self, line, regex, s, compile = 0):
        """ substitute regex regex in string line with string s """
        if compile:
            regex = re.compile(regex)
        line = regex.sub(s, line, count = 1)
        return line
