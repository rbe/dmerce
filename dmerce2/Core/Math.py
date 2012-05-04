#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.3 $'
#
##

import sys
import string
import operator

class OrdRepr:

    """ ord representation of strings """

    def String(self, _string):
        "converts a string of arbitrary length into a long integer"
        _num = 0L
        for i in range(0, len(_string)):
            _num = _num + (2L**(8 * i)) * ord(_string[i])
        return _num

    def Long(self, _num):
        "converts a long integer number into a string"
        _string = ''
        while _num > 0:
            j = _num % 256
            _num = _num >> 8 
            _string = _string + chr(j)
        return _string

class BitOperation:

    """ dealing with bits """

    def Length(self, _input):
        """ get length of bits of _input """
        n=0
        while _input > 2**n:
            n = n + 1
        return n

    def isBit(self, _id, _flag):
        """ ask if bits _flag are present in _id """
        _id = _id >> _flag
        if _id % 2 == 0:
            return 0
        else:
            return 1

    def List(self, _input):
        """ return list of bits """
        l = []
        while _input:
            if _input % 2 == 0:
                l.append(0)
            else:
                l.append(1)
            _input = _input >> 1
        return l

    def ListSetBitPos(self, _input):
        """ return list of bit positions which are set """
        l = []
        pos = 0
        for p in self.List(_input):
            if p:
                l.append(pos)
            pos = pos + 1
        return l

class BigNumber62:

    """ a 'number' that can be 0-9,A-Z,a-z """

    def __init__(self, no):
        self.__no = str(no)
        self.__noList = []
        self.NumberToList()

    def __str__(self):
        return self.ListToNumber()

    def NumberToList(self):
        """ create list of number """
        for i in range(len(self.__no)):
            self.__noList.append(ord(self.__no[i]))
        return self.__noList

    def ListToNumber(self):
        n = ''
        for i in self.__noList:
            n = n + str(chr(i))
        return n

    def IncrDigit(self, ordDigit):
        """ increment a digit """
        incrNext = 0
        newDigit = ordDigit + 1
        if newDigit == 58:
            newDigit = 65
        elif newDigit == 91:
            newDigit = 97
        elif newDigit == 123:
            newDigit = 48
            incrNext = 1
        return (incrNext, newDigit)

    def Incr(self):
        """ increment the whole number """
        l = self.__noList
        l.reverse()
        for i in range(len(l)):
            digit = l[i]
            incrNext, l[i] = self.IncrDigit(digit)
            if incrNext:
                continue
            else:
                break
        l.reverse()
        self.__noList = l

    def Convert(self, no):
        """ convert decimal number to bn62 """
        l = self.__noList
        l.reverse()
        for i in range(len(l)):
            digit = l[i]
        l.reverse()
        self.__noList = l        

#b = BigNumber62('00000')
#b.Incr()
#print b
#for i in range(999999):
#    b.Incr()
    #print i, '=', b
#print b
