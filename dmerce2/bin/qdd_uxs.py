#!/usr/bin/env python

import sys
import operator

def Length(_input):
    n=0
    while _input > 2**n:
        n = n+1
    return n

def Bitval(_id, _flag):
    _id = _id >> _flag
    if _id % 2 == 0:
        return 0
    else:
        return 1
    
class Acl:
    def __init__(self, _startvalue=0):
        self.__rights = _startvalue

    def GiveSingleRight(self, _right): # _right ist Nr. eines Bitregisters (also >=0)
        _result = 0
        if Bitval(self.__rights, _right) == 0:
            self.__rights = self.__rights + 2L**_right

    def GiveRights(self, _rights):
        _result = 0
        for i in range (0, Length(self.__rights) + Length(_rights)+1):
            if Bitval(self.__rights, i) == 1 or Bitval(_rights, i) == 1:
                _result = _result + 2**i
        self.__rights = _result
       
    def HasRight(self, _digit):
        return Bitval(self.__rights, _digit)

    def Get(self):
        return self.__rights
        
    def KillRight(self, _right): # _right ist Nr. eines Bitregisters (also >= 0)
        if self.HasRight(_right):
            self.__rights = self.__rights - 2**_right

    def KillRights(self, _rights):
        for i in range(0, Length(self.__rights)):
            if self.HasRight(i) and Bitval(_rights, i):
                self.__rights = self.__rights - 2**i

    def ShowRights(self):
        r = self.__rights
        l = []
        while r:
            if r % 2 == 0:
                l.append(0)
            else:
                l.append(1)
            r = r >> 1
        return l

COPYR = '\nCopyright (C) 2001 1Ci GmbH, http://www.1ci.de/\n'

def HelpText():
    print """usage: %s <rights>
    """ % sys.argv[0]

try:
    print COPYR
    #r = int(sys.argv[1])
    r = 27
    a = Acl()
    rights = (2, r + 1)
    for i in range(rights[0], rights[1]):
        a.GiveSingleRight(i)
    z = a.Get()
    print 'Calculating R for grouping rights 2 - %i' % r
    print z, z - 4
    print '2**%i-4 = %s' % (r + 1, 2**long((r + 1)) - 4)
    #print 'R=%s, LENGTH OF R=%s' % (str(z), len(str(z)))
except IndexError:
    HelpText()
