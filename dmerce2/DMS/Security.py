#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.4 $'
#
# Revision 1.1  2000-07-04 16:27:01+02  rb
# Initial revision
#
##

import sys
import string
import types
import random

class Password:

    """ generate random, secure passwords """

    def __init__(self, kw):
        """
        allowed values for ASCII chars:
        33-47: !\"#$%'()*+,-. (without &)
        48-57: 0..9 (without 1)
        65..89: A..Z (without I, L)
        97..122: a..z (without i, l)
        """
        self.__special = [(34, 37), (39, 47)]
        self.__digits = [(48, 48), (50, 57)]
        self.__ucChars = [(65, 72), (74, 75), (77, 89)]
        self.__lcChars = [(97, 104), (106, 107), (109, 122)]

    def __Random(self, l):
        """
        return random character
        l is a list with a 2-tuple of ranges of ascii codes
        """
        i = random.randint(0, len(l) - 1)
        return random.randint(l[i][0], l[i][1])

    def __Special(self):
        """ return random special character """
        return self.__Random(self.__special)

    def __Digit(self):
        """ return random digit """
        return self.__Random(self.__digits)

    def __Ucchar(self):
        """ return random upper case character """
        return self.__Random(self.__ucChars)

    def __Lcchar(self):
        """ return random lower case character """
        return self.__Random(self.__lcChars)

    def Generate(self, **opt):
        """
        generate secure password
        type of password to generate
         L = low
         M = medium
         H = high
         P = phonetic
         Default to 'H'
        """
        pwd = ''
        which = 1
        self.PWD_LEN = 8
        if opt.has_key('len'):
                self.PWD_LEN = opt['len']
        self.PWD_TYPE = 'H'
        if opt.has_key('type'):
                self.PWD_TYPE = opt['type']
        print self.PWD_TYPE, self.PWD_LEN
        for i in range(self.PWD_LEN):
            print i
            if self.PWD_TYPE.upper() == 'H':
                which = random.randint(1, 4)
            elif self.PWD_TYPE.upper() == 'M':
                which = random.randint(1, 3)
            elif self.PWD_TYPE.upper() == 'L':
                which = random.randint(2, 3)
            if which == 1:
                R = self.__Digit()
            elif which == 2:
                R = self.__Ucchar()
            elif which == 3:
                R = self.__Lcchar()
            elif which == 4:
                R = self.__Special()
            pwd = '%s%s' % (pwd, chr(R))
        return pwd

#p = Password({})
#print p.Generate(type='L')
