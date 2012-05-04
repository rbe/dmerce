#!/usr/bin/env python
#
import string

class RR:
    """ Class to handle resource-records """
    def __init__(self, value1, type, value2):
        """ Constructor """
        self.__type = self.__CheckType(type)
        if self.__type == 'PTR':
            self.__value1 = value1
            self.__value2 = value2

        elif self.__type == 'MX':
            if not value1:
                self.__value1 = ''
                self.__value2 = value2
            else:
                self.__value1 = value1
                self.__value2 = value2
            if value2[-1:] != '.':
                value2 = value2 + '.'
                self.__value2 = value2

        else:
            self.__value1 = self.__CheckValue1(value1, value2)
            self.__value2 = self.__CheckValue2(value2)

    def __CheckValue1(self, value):
        if value:
            return value
        else:
            return ''

    def __CheckValue1(self, value, value2):
        """
        Check value1 and value2 of plausibility
        Check, if last character is a . / if not add a .
        """
        if value[-1:] == '.':
            return value + '.'
        return value
    
    def __CheckValue2(self, value):
        if self.__type == 'NS':
            if value[-1:] != '.':
                value = value + '.'
            return value
        
        if self.__type == 'A':
            """ delete '.' from ip in A record """
            if value[-1:] == '.':
                return value[0:-1]
            else:
                return value

        if self.__type == 'CNAME':
            """ add '.' at the end of CNAME """
            if string.count(value, '.') > 1:
                if value[-1:] != '.':
                    return value + '.'
            try:
                int(value[-2])
                return None
            except:
                return value

        if self.__type == 'MX':
            if value[-1:] != '.':
                value = value + '.'
            return value
            
        
        if self.__type == 'PTR':
            return value

    def __CheckType(self, t):
        """ Check type for right value """
        t = string.upper(t)
        """ convert lower letters to upper letters """
        if not t in ['MX', 'CNAME', 'A', 'NS', 'PTR']:
            return None
        else:
            return t

    def __str__(self):
        """ Return RR set """
        if self.__value1 and self.__value2 and self.__type:
            return '%s\t10800\tIN\t%s\t\t%s\n' % (self.__value1, self.__type, self.__value2)
        elif self.__value2 and self.__type == 'MX':
            return '\t10800\tIN\t%s\t\t%s\n' % (self.__type, self.__value2)
        else:
            return None

    def __repr__(self):
        if  self.__value1 and self.__value2 and self.__type:
            return 'RR("%s", "%s", "%s")' % (self.__value1, self.__type, self.__value2)
        else:
            return None
