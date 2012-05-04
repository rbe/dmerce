#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.28 $'
#
##

#import sys
#sys.path.append('/export/home/r/rb/dmerce2')
import string
import types
import operator
import Core.Log

class Check:

    """ perform checks on objects """

    def __init__(self, kw = {}):
        pass #self.__log = Core.Log.File(debug = kw['debug'], module = 'Object.Check')

    def AppendDotToString(self, s):
        """ append dot to end of string """
        if type(s) is types.StringType:
            if not s[:-1] == '.':
                s = s + '.'
        return s

    def Occur(self, must = [], noway = [], where = None):
        """
        check for occurence of what in where
        (strings or list)
        """
        conv = Convert()
        ok = 1
        # Convert
        where = conv.ListToStringList(where)
        if not type(must) is types.ListType:
            must = list(str(must))
        if not type(noway) is types.ListType:
            noway = list(str(noway))
        if must:
            # Test
            must = conv.ListToStringList(must)
            for item in must:
                if not item in where:
                    ok = 0
        if noway:
            noway = conv.ListToStringList(noway)
            for item in noway:
                if item in where:
                    ok = 0
        return ok

class Convert:

    """ converts vars """
    
    def StripWhitespaces(self, w):
        """
        strips whitespaces
        - w :== tuple, list, dictionary, string
        - all string elements are stripped, others remain untouched
        """
        if type(w) is types.StringType:
            w = string.strip(w)
        elif type(w) is types.ListType or type(w) is types.TupleType:
            for e in range(len(w)):
                if type(w[e]) is types.StringType:
                    w[e] = string.strip(w[e])
        elif type(w) is types.DictType:
            for e in w.keys():
                if type(w[e]) is types.StringType:
                    w[e] = string.strip(w[e])
        return w

    def GermanUmlautReplace(self, s):
        """ parses a string for german umlauts and replaces them """
        u = [
            (chr(196), 'AE'),
            (chr(214), 'OE'),
            (chr(220), 'UE'),
            (chr(223), 'ss'),
            (chr(228), 'ae'),
            (chr(246), 'oe'),
            (chr(252), 'ue')
            ]
        for umlaut in u:
            try:
                if string.find(s, umlaut[0]) >= 0:
                    s = string.replace(s, umlaut[0], umlaut[1])
            except:
                pass
        return s

    def ChangeType(self, var, ty):
        """ convert variable var to type ty """
        if var:
            return eval('%s(var)' % ty)
        return var

    def ChangeTypeWithCheck(self, val, ty):
        #if ty is types.IntType or ty is types.LongType:
        #    if val:
        #        val = int(val)
        #    else:
        #        val = 0
        if ty is types.IntType:
            if val:
                val = int(val)
            else:
                val = 0
        elif ty is types.LongType:
            if val:
                val = long(val)
            else:
                val = 0
        elif ty is types.FloatType:
            if val:
                val = float(val)
            else:
                val = 0.0
        elif ty is types.StringType:
            if val:
                val = str(val)
            else:
                val = ''
        return val

    def NumberToString(self, var):
        if type(var) is types.IntType \
           or type(var) is types.FloatType:
            return str(var)
        #elif type(var) is types.LongType:
            #1.5.2: return str(var)[:-1]
        else:
            return str(var)

    def ListToStringList(self, var):
        if type(var) is types.ListType \
           or type(var) is types.TupleType:
            l = []
            for item in var:
                l.append(str(item))
            return l
        else:
            return var

    def ListToStringWithNewlines(self, l):
        """
        convert a list into a string with each
        element separated by a newline
        """
        s = ''
        for item in l:
            s = '%s%s\n' % (s, item)
        return s

    def StringOrListToList(self, sl):
        """
        if we get a string or a list,
        ensure that a list is returned:
        - convert string (separated by commas)
        """
        if type(sl) is types.StringType:
            r = string.split(sl, ',')
        elif type(sl) is types.ListType \
             or type(sl) is types.TupleType:
            r = sl
        return r

class Point:

    """ add points to a number """

    def __init__(self, n = 0):
        self.__n = n
        self.__ns = ''
        self.__ns_fp = ''
        self.__nf = ''

    def __str__(self):
        if not self.__nf:
            self.Format()
        return self.GetFormattedNumber()

    def __Check(self):
        """ check given number """
        #if type(self.__n) is types.LongType:
        #1.5.2:    self.__ns = str(self.__n)[:-1]
        #else:
        self.__ns = str(self.__n)
        p = string.find(self.__ns, '.')
        if p >= 0:
            self.__ns_fp = self.__ns[p + 1:]
            self.__ns = self.__ns[:p]

    def Format(self):
        """ format number """
        self.__Check()
        l = len(self.__ns)
        if l > 3:
            lm3 = operator.mod(l, 3)
            ld3 = l / 3
            if lm3:
                s = self.__ns[:lm3] + '.'
                self.__ns = self.__ns[lm3:]
            else:
                s = ''
            for i in range(ld3):
                p = i * 3
                s = s + self.__ns[p:p + 3] + '.'
            self.__nf = s[:-1] + self.__ns[p + 3:]
        else:
            self.__nf = self.__ns
        if self.__ns_fp:
            self.__nf = self.__nf + ',' + self.__ns_fp

    def SetNumber(self, n):
        self.__n = n

    def GetNumber(self):
        return self.__n

    def GetFormattedNumber(self):
        return self.__nf

class Values:

    """ format values: int, long, float, string """

    def __init__(self):
        self.__log = Core.Log.File(debug = 1, module = 'Object.Values')
        self.__conv = Convert()

    def FormatDetermineTypeNumber(self, value):
        """ determines type of a value; returns true if it is a number """
        if type(value) is types.IntType:
            r = 1
        elif type(value) is types.LongType:
            r = 2
        elif type(value) is types.FloatType:
            r = 3
        elif type(value) is types.StringType:
            r = 0
        else:
            r = 0
        return r

    def FormatPreserveFloat(self, value):
        """ preserve two numbers after the point """
        if len(value[string.index(value, '.') + 1:]) == 1:
            p = Point('%.2f' % float(value))

    def Format(self, value, formatList, formatValues = 0):
        """
        returns a formatted value
        """
        if value is None:
            return 0
        dt = self.FormatDetermineTypeNumber(value)
        fl0 = 0
        fl1 = 0
        lfl = len(formatList)
        if lfl:
            try:
                fl0 = int(formatList[0])
            except:
                fl0 = 0
            try:
                fl1 = int(formatList[1])
            except:
                fl1 = 0
        if not formatValues:
            if dt == 3 and fl0:
                # Float: if format[0] is set print given format
                p = '%.' + str(fl0) + 'f'
                p = p % value
            elif dt == 3:
                p = '%.2f' % value
            else:
                # value is string or treat value as string (formatValues = 0)
                if fl0 and fl1:
                    # [position_from]-[position_to]
                    p = '%s' % str(value)[fl0:fl1]
                elif fl0:
                    # [position_to]
                    p = '%s' % str(value)[:fl0]
                elif fl1:
                    # [position_from]-
                    p = '%s' % str(value)[fl1:]
                else:
                    # Without formatting
                    if dt:
                        p = self.__conv.NumberToString(value)
                    else:
                        p = str(value)
            r = str(p)
        else:
            if dt == 3 and lfl:
                # Float: if format[0] is set print given format
                formatString = '%.' + str(fl0) + 'f'
                p = Point(formatString % value)
            elif dt == 3:
                # Float: print as %f
                # Convert to string to get rid of trailing zeros
                s = str(value)
                if len(s[string.index(s, '.') + 1:]) == 1:
                    # Preserve at least 2 floating points
                    p = Point('%.2f' % value)
                else:
                    p = Point(value)
            else:
                # Integer: print as %i
                p = Point(value) #'%i' % value
            p.Format()
            r = p.GetFormattedNumber()
        #self.__log.Write(msg = 'FORMATTED VALUE=%s TYPE=%s DT=%s POS=%s FORMAT_VALUES=%s RETURNING=%s'
        #                 % (str(value), type(value), dt, str(formatList), formatValues, r))
        return r

#v = Values()
#a = v.Format(15000.28, [2, ''], 1)
#print a, type(a)
#b = v.Format('15000.00', [], 0)
#print b, type(b)
#c = v.Format(15000L, ['', ''])
#print c, type(c)
#d = v.Format(15000.005656, [3], 1)
#print d, type(d)
#e = v.Format(None, [])
#print e, type(e)
#f = v.Format(2.0, ['', ''], 1)
#print f, type(f)
#g = v.Format(1234567890123456789012345678901234567890L, ['', ''], 0)
#print g, type(g)
#c = Convert()
#print c.NumberToString(11L)
