#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.5 $'
#
##

import sys
sys.path.append('/home/rb/dmerce2')
import string
import Core.Log

class SSExpr:

    """ a supersearch-expression """

    def __init__(self):
        self.__beforeOp = ''
        self.__var = ''
        self.__op = ''
        self.__val = ''
        self.__braceLeft = 0
        self.__braceRight = 0

    def SetBeforeOperator(self, bo):
        self.__beforeOp = bo

    def SetVar(self, v):
        self.__var = v

    def GetVar(self):
        return self.__var

    def SetOp(self, o):
        self.__op = o

    def SetVal(self, v):
        self.__val = v

    def GetVal(self):
        return self.__val

    def SetBraceLeft(self):
        self.__braceLeft = 1

    def SetBraceRight(self):
        self.__braceRight = 1

    def Get(self):
        s = self.__beforeOp
        if self.__braceLeft:
            s = s + '('
        s = s + self.__var + self.__op + self.__val
        if self.__braceRight:
            s = s + ')'
        return s

    def __str__(self):
        return self.Get()
    
class SuperSearch:

    """ class to deal with qSuperSearch syntax """

    __sqlReplace = [
        (':', ' AND '),
        ('|', ' OR '),
        ('~', ' LIKE '),
        ('*', ' = '),
        ('!', ' NOT '),
        (' NOT  = ', ' != '),
        (' NOT = ', ' != '),
        ('[', ' < '),
        (']', ' > '),
        (' <  = ', ' <= '),
        (' >  = ', ' >= '),
        ('@', ' LIMIT '),
        ('}', ' GROUP BY '),
        ('{', ' ORDER BY ')
        ]

    def __init__(self, s = None):
        """ takes a string as argument """
        self.__q = s
        self.__c = ''
        self.__qDict = {}
        self.__components = []
        self.__suffix = ''
        self.__ops = ['[*', ']*', '*', '[', ']', '~', '!']
        self.__opsPos = []
        self.__lops = [':', '|']
        self.__lopsPos = []
        self.__mods = ['}', '{', '@']
        self.__log = Core.Log.File(debug = 1, module = 'DMS.SuperSearch')
        self.__GetOperators()
        self.__ChopComponents()

    def __str__(self):
        return '%s' % self.__c

    def __repr__(self):
        return 'SuperSearch(%s)' % self.__c

    def __GetOperators(self):
        q = self.__q
        for i in range(len(q)):
            for r in self.__ops:
                if q[i] == r:
                    self.__opsPos.append(i)
            for r in self.__lops:
                if q[i] == r:
                    self.__lopsPos.append(i)
        for r in self.__mods:
            p = string.find(q, r)
            if p >= 0:
                self.__suffix = self.__suffix + q[p:]
                q = q[:p]
        self.__lopsPos.append(len(q))

    def __ChopComponents(self):
        q = self.__q
        for i in range(len(self.__lopsPos)):
            var = ''
            val = ''
            if i > 0:
                p1 = self.__lopsPos[i - 1] + 1
            else:
                p1 = 0
            p2 = self.__lopsPos[i]
            if i > 0:
                op = q[p1 - 1]
            else:
                op = ''
            pair = q[p1:p2]
            for o in self.__ops:
                p = string.find(pair, o)
                if p >= 0:
                    var, val = string.split(pair, o)
                    break
            if not var:
                break
            e = SSExpr()
            p = string.find(var, '(')
            if p >= 0:
                e.SetBraceLeft()
                var = var[p + 1:]
            p = string.find(val, ')')
            if p >= 0:
                e.SetBraceRight()
                val = val[:p]
            e.SetBeforeOperator(op)
            y, uv = self.Unsharp(var, val)
            if y:
                e.SetVal(uv)
            else:
                e.SetVar(var)
                e.SetOp(o)
                e.SetVal(uv)
            self.__components.append(e)

    def GetComponent(self, component):
        """ get component of SuperSearch """
        for c in self.__components:
            if c.GetVar() == component:
                return c.GetVal()
        return ''

    def isUnsharp(self, v):
        """ get positions of operators """
        return string.find(v, '#U#')

    def UnsharpGetValues(self, v, p):
        """ get values """
        v1 = v[:p]
        v2 = v[p + 3:]
        uPlus = string.find(v2, '+')
        uMinus = string.find(v2, '-')
        uPercent = string.find(v2, '%')
        if uPlus >= 0:
            v2 = string.replace(v2, '+', '')
        if uMinus >= 0:
            v2 = string.replace(v2, '-', '')
        if uPercent:
            v2 = string.replace(v2, '%', '')
        return float(v1), float(v2), uMinus, uPlus, uPercent

    def UnsharpCalculate(self, l):
        """ calculate values """
        vp = None
        vm = None
        v, uv, uPlus, uMinus, uPercent = l
        if uPlus >= 0:
            if uPercent:
                vp = v + v * (uv / 100)
            else:
                vp = v + uv
        if uMinus >= 0:
            if uPercent:
                vm = v - v * (uv / 100)
            else:
                vm = v - uv
        return vp, vm

    def UnsharpConvert(self, k, uPlus, uMinus):
        """ convert unsharp search to SQL WHERE clause """
        uPlusWhere = ''
        uMinusWhere = ''
        if uPlus is not None:
            uPlusWhere = '%s[*%s' % (k, uPlus)
        if uMinus is not None:
            uMinusWhere = '%s]*%s' % (k, uMinus)
        sql = uMinusWhere
        if sql:
            sql = sql + ':'
        return sql + uPlusWhere

    def Unsharp(self, var, val):
        """
        unsharp
        p = position of #U#
        v = value to unsharp
        uv = unsharper value
        uPlus = unsharper operator +?
        uMinus = unsharper operator -?
        uPercent = unsharper value given in percent?
        """
        p = self.isUnsharp(val)
        if p >= 0:
            uPlus, uMinus = self.UnsharpCalculate(self.UnsharpGetValues(val, p))
            return 1, self.UnsharpConvert(var, uPlus, uMinus)
        else:
            return 0, val

    def ConvertToSQLWhere(self):
        """ convert qSuperSearch-String into SQL WHERE condition """
        self.__c = ''
        self.__components.append(self.__suffix)
        for i in self.__components:
            s = str(i)
            for r in self.__sqlReplace:
                s = string.replace(s, r[0], r[1])
            self.__c = self.__c + s
        #self.__log.Write(msg = 'CONVERTED %s TO %s' % (self.__q, self.__c))
        return self.__c

#p = 'AuctionBid.ProduktgruppenTabelleID*99:AuctionBid.Auktionstyp*2:AuctionBid.active*1}A.I{A.I'
#s = SuperSearch(p)
#print s.ConvertToSQLWhere()
#print s.GetComponent('AuctionBid.active')
