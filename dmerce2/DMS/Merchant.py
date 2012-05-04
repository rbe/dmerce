#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.57 $'
#
# Revision 1.1  2000-05-11 15:04:05+02  rb
# Initial revision
#
##

import sys
import os
import time
import math
import string
import Core.Log
import DMS.SQL
import DMS.MBase
import DMS.Object

class ArticleBase:

    """ base class that represents an article """

    def __init__(self, sql, artId):
        self.__sql = sql
        self.__artId = artId
        self.__log = Core.Log.File(debug = 1, module = '1[Merchant].Article')

    def Init(self):
        """ retrieve article data from database """
        rc, r = self.__sql["SELECT ID, Name, UnixPriceNet, UnitTax, UnitPriceGross," \
                           " PriceNet, TaxRate, PriceGross, SpecialOffer, SpecialOfferPercent" \
                           " FROM Article WHERE ID = '%s'" % self.__artId]
        #self.__log.Write(msg = 'INFO ABOUT ARTICLE (%s) RC=%s R=%s' % (self.__artId, str(rc), str(r)))
        try:
            self.__article = r[0]
        except:
            raise Core.Error.MerchantArticleNotFound(1, 'ARTICLE ArtID=%s NOT FOUND' % self.__artId)

    def SetAttr(self, attr, value):
        """ set attribute of article """
        self.__article[attr] = value

    def GetAttr(self, attr):
        """ get attribute of article """
        if not self.__article.has_key(attr):
            rc, r = self.__sql["SELECT %s FROM Article WHERE ID = '%s'" % (attr, self.__artId)]
            return r[0][attr]
        else:
            return self.__article[attr]

    def isSpecialOffer(self):
        """ is article a special offer? """
        if self.GetAttr('SpecialOffer') \
           and self.__timeNow >= self.GetAttr('SpecialOfferFrom') \
           and self.__timeNow <= self.GetAttr('SpecialOfferTill'):
            return 1
        else:
            return 0

    def GetSpecialOffer(self):
        """ return percentage off of article """
        if self.isSpecialOffer():
            return self.GetAttr('SpecialOfferPercent')
        else:
            return 0

    def ApplySpecialOfferPercent(self):
        price = 0
        discountRate = 0
        applyDR = 1
        # Get price of article; check for special offer
        if self.isSpecialOffer():
            price = self.GetSpecialOffer()
        else:
            # Get price of article
            price = self.GettAttr('Price')
            # Do we want to get the reduced price?
            #if self._sam.GetWho('ID'):
                # whoID and self.whoType == 'Customer' and self.whoID:
                # Get discount rate
                #discountRate, applyDR = self._applyDiscountRate(qArtNr = qArtNr)
        # Apply discount rate to price; return value
        #return formatString % (float(self.currency(price = price, Curr = Curr)) * applyDR)
        return price

class Article(DMS.MBase.Class):

    """ do something with an article """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__artId = self._cgi.GetField('Article_ID')
        self.__log = Core.Log.File(debug = self._debug, module = '1[Merchant].Article')
        try:
            self.__article = ArticleBase(self._sql, self.__artId)
            self.__article.Init()
        except:
            pass # we suppose to be called as a (exec) function

    def CalcPrices(self, artId = None):
        """ updates the price gross for an article """
        if artId is None:
            artId = self.__artId
            self.__article = ArticleBase(self._sql, artId)
            self.__article.Init()
        oc = DMS.Object.Convert()
        priceGross = oc.ChangeType(self._cgi.GetField('Article_PriceGross', 0), 'float')
        priceGrossB2B = oc.ChangeType(self._cgi.GetField('Article_PriceGrossB2B', 0), 'float')
        priceNet = oc.ChangeType(self._cgi.GetField('Article_PriceNet', 0), 'float')
        priceNetB2B = oc.ChangeType(self._cgi.GetField('Article_PriceNetB2B', 0), 'float')
        taxRate = oc.ChangeType(self._cgi.GetField('Article_TaxRate', 0), 'float')
        if not taxRate:
            taxRate = self.__article.GetAttr('TaxRate')
        if priceNet:
            priceGross = priceNet + ((taxRate / 100) * priceNet)
        elif priceGross:
            priceNet = priceGross / (1 + taxRate / 100)
        if priceNetB2B:
            priceGrossB2B = priceNetB2B + ((taxRate / 100) * priceNetB2B)
        elif priceGrossB2B:
            priceNetB2B = priceGrossB2B / (1 + taxRate / 100)            
        if priceNet and priceGross:
            stmt = "UPDATE Article SET PriceNet = %f, PriceGross = %f WHERE ID = '%s'" \
                   % (priceNet, priceGross, artId)
            self.__log.Write('B2C: STMT=%s' % stmt)
            rc, r = self._sql[stmt]
        if priceNetB2B and priceGrossB2B:
            stmt = "UPDATE Article SET PriceNetB2B = %f, PriceGrossB2B = %f WHERE ID = '%s'" \
                   % (priceNetB2B, priceGrossB2B, artId)
            self.__log.Write('B2B: STMT=%s' % stmt)
            rc, r = self._sql[stmt]
        return 1

    def isSpecialOffer(self, artId = None):
        if artId is None:
            artId = self.__artId
            article = ArticleBase(self._sql, artId)
            article.Init()
        return article.isSpecialOffer()

    def GetPriceNet(self, artId, precision = 2):
        formatString = '%%.%if' % precision
        article = ArticleBase(self._sql, artId)
        article.Init()
        return formatString % float(article.GetAttr('PriceNet'))
        
    def GetTaxRate(self, artId, precision = 2):
        formatString = '%%.%if' % precision
        article = ArticleBase(self._sql, artId)
        article.Init()
        return formatString % float(article.GetAttr('TaxRate'))

    def GetPriceGross(self, artId, precision = 2):
        formatString = '%%.%if' % precision
        article = ArticleBase(self._sql, artId)
        article.Init()
        return formatString % float(article.GetNetPrice(artId) * article.GetAttr('TaxRate'))

    def GetVAT(self, artId, precision = 2):
        """ get VAT of article """
        formatString = '%%.%if' % precision
        article = ArticleBase(self._sql, artId)
        article.Init()
        return formatString % float(article.GetAttr('PriceGross') - article.GetAttr('PriceGross'))

class Basket(DMS.MBase.Class):

    """ a basket """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = '1[Merchant].Basket')
        self.__contents = []
        try:
            self.__artId = self._cgi.GetField('artId')
            self.__qty = self._cgi.GetqVar('qty', 1)
            self.__sid = self._sam.GetSessionId()
            self.GetContents(self.__sid)
            self.__whereSid = "WHERE SessionId = '%s'" % self.__sid
            self.__article = ArticleBase(self._sql, self.__artId)
            self.__article.Init()
        except:
            pass # we suppose to be called as a (exec) function

    def GetWhere(self):
        """ return condition for (repeat) in Basket,list """
        return "BasketContents.SessionId*'%s':BasketContents.ArtNo*Article.ID" % self.__sid

    def GetContents(self, sid):
        """ retrieve and store contents of basket """
        stmt = "SELECT ID, active, SessionID, Qty, PriceNet, PriceGross, TaxRate," \
               "       SpecialOffer, SpecialOfferPercent, ArtID" \
               "  FROM BasketContents" \
               " WHERE SessionID = '%s'" % sid
        rc, r = self._sql[stmt]
        if rc:
            self.__contents = r
        #self.__log.Write(msg = 'CONTENTS OF BASKET: RC=%s R=%s' % (str(rc), self.__contents))

    def hasArticle(self, artId):
        """ has basket already this article """
        #self.__log.Write(msg = 'LOOKING FOR ARTICLE ID %s IN BASKET CONTENTS=%s'
        #                 % (artId, str(self.__contents)))
        for i in range(len(self.__contents)):
            #self.__log.Write(msg = 'LOOKING FOR ARTICLE ID %s IN BASKET CONTENTS %s=%s, %s'
            #                 % (artId, i, str(self.__contents[i]), self.__contents[i].keys()))
            if str(self.__contents[i]['ArtID']) == str(artId):
                return 1
            else:
                continue
        return 0

    def GetArticle(self, artId):
        """ get article from basket """
        for i in range(len(self.__contents)):
            if str(self.__contents[i]['ArtID']) == str(artId):
                return self.__contents[i]
            else:
                continue

    def UpdatePricesOfArticle(self, qty, article, priceSuffix = ''):
        article.Init()
        if not priceSuffix:
            priceSuffix = self._cgi.GetField('PriceSuffix', '')
        stmt = "UPDATE BasketContents SET" \
               " PriceNet = %f * %f," \
               " PriceGross = %f * %f" \
               " %s AND ArtID = '%s'" \
               % (float(qty), float(article.GetAttr('PriceNet%s' % priceSuffix)),
                  float(qty), float(article.GetAttr('PriceGross%s' % priceSuffix)),
                  self.__whereSid, article.GetAttr('ID'))
        self.__log.Write(msg = stmt)
        rc, r = self._sql[stmt]

    def PutArticle(self, artId = None, qty = None, priceNet = '', priceGross = ''):
        if artId is None:
            artId = self.__artId
            self.__article = ArticleBase(self._sql, artId)
            self.__article.Init()
        if qty is None:
            qty = self.__qty
        priceSuffix = self._cgi.GetField('PriceSuffix', '')
        if not self.hasArticle(artId):
            self.__log.Write(msg = 'INSERTING ARTICLE=%s QTY=%s INTO BASKET=%s'
                             ' PRICESUFFIX=%s' % (artId, qty, self.__sid, priceSuffix))
            dbOid = DMS.SQL.DBOID(self._sqlSys, self._sql)
            priceNet = self.__article.GetAttr('PriceNet%s' % priceSuffix)
            priceGross = self.__article.GetAttr('PriceGross%s' % priceSuffix)
            stmt = "INSERT INTO BasketContents" \
                   " (ID, Name, SessionId, ArtID, Qty, PriceNet, TaxRate, PriceGross," \
                   " UnitPriceNet, UnitTax, UnitPriceGross," \
                   " SpecialOffer, SpecialOfferPercent)" \
                   " VALUES (%s, '%s', %s, %s, %f, %f, %f, %i, %f)" \
                   % (dbOid['BasketContents'], self.__article.GetAttr('Name'), self.__sid, artId, qty,
                      float(qty) * priceNet,
                      self.__article.GetAttr('TaxRate'),
                      float(qty) * priceGross,
                      self.__article.GetAttr('UnitPriceNet'),
                      self.__article.GetAttr('UnitTax'),
                      self.__article.GetAttr('UnitPriceGross'),
                      self.__article.isSpecialOffer(),
                      self.__article.GetSpecialOffer())
            #self.__log.Write(msg = stmt)
            rc, r = self._sql[stmt]
            #self.UpdatePricesOfArticle(qty, self.__article)
        return 1

    def SetQty(self, artId = None, qty = None):
        """ set quantity of article (absolute, positive) """
        if artId is None:
            artId = self.__artId
            article = self.__article
        else:
            article = ArticleBase(self._sql, artId)
            article.Init()
        if qty is None:
            qty = self.__qty
        self.__log.Write(msg = 'SETTING QUANTITY %s FOR ARTICLE artId=%s IN BASKET %s'
                         % (qty, artId, self.__sid))
        if self.hasArticle(artId):
            art = self.GetArticle(artId)
            qty = int(qty)
            if qty <= 0:
                self.RemoveArticle(artId)
            elif qty > 0:
                stmt = "UPDATE BasketContents SET Qty = %f %s AND ArtID = '%s'" \
                       % (float(qty), self.__whereSid, artId)
                rc, r = self._sql[stmt]
                self.UpdatePricesOfArticle(qty, article)
        return 1

    def IncQty(self, artId = None, qty = None):
        """ increase quantity of article in basket """
        if artId is None:
            artId = self.__artId
            article = self.__article
        else:
            article = ArticleBase(self._sql, artId)
            article.Init()
        if qty is None:
            qty = self.__qty
        h = self.hasArticle(artId)
        self.__log.Write(msg = 'INCREASING QUANTITY BY %s FOR ARTICLE=%s IN BASKET=%s H=%s'
                         % (qty, artId, self.__sid, h))
        if h:
            #self.__log.Write(msg = 'ABOUT TO INCREASE QTY 1')
            if qty > 0:
                #self.__log.Write(msg = 'ABOUT TO INCREASE QTY 2')
                stmt = "UPDATE BasketContents SET Qty = Qty + %s %s AND ArtID = '%s'" \
                       % (qty, self.__whereSid, artId)
                rc, r = self._sql[stmt]
                #self.__log.Write(msg = 'STMT=%s' % stmt)
                self.UpdatePricesOfArticle(float(self.GetArticle(artId)['Qty']) + float(qty), article)
        return 1

    def DecQty(self, artId = None, qty = None):
        """ decrease quantity of article in basket """
        if artId is None:
            artId = self.__artId
            article = self.__article
        else:
            article = ArticleBase(self._sql, artId)
            article.Init()
        if qty is None:
            qty = self.__qty
        self.__log.Write(msg = 'DECREASING QUANTITY BY %s FOR ARTICLE artId=%s IN BASKET %s'
                         % (qty, artId, self.__sid))
        if self.hasArticle(artId):
            art = self.GetArticle(artId)
            if art['Qty'] <= 1:
                self.RemoveArticle(artId)
            if qty > 0:
                stmt = "UPDATE BasketContents SET Qty = Qty - %s %s AND ArtID = '%s'" \
                       % (qty, self.__whereSid, artId)
                rc, r = self._sql[stmt]
                self.UpdatePricesOfArticle(float(self.GetArticle(artId)['Qty']) - float(qty), article)
        return 1

    def SumQty(self, t = None):
        """ summarize quantity of articles in basket for active session """
        sq = 0.0
        for i in range(len(self.__contents)):
            sq = sq + self.__contents[i]['Qty']
        if math.floor(sq) == sq:
            return '%.0f' % sq
        else:
            return '%.2f' % sq

    def RemoveArticle(self, artId = None):
        """ remove article from basket """
        if artId is None:
            artId = self.__artId
        if self.hasArticle(artId):
            stmt = "DELETE FROM BasketContents %s AND ArtID = '%s'" % (self.__whereSid, artId)
            rc, r = self._sql[stmt]
        return 1

    def Remove(self):
        """ remove entire basket of a session """
        stmt = "DELETE FROM BasketContents WHERE SessionID = '%s'" % self.__sid
        rc, r = self._sql[stmt]
        return 1

    def SetName(self, name = None):
        if name is None:
            name = self._cgi.GetField('name')
        stmt = "UPDATE Baskets SET Name = '%s' %s" % (self.__whereSid, name)
        rc, r = self._sql[stmt]
        return 1

    def GetSumNet(self, sessionId = '', curr = ''):
        if sessionId:
            self.GetContents(sessionId)
        sum = 0.0
        for i in range(len(self.__contents)):
            sum = sum + float(self.__contents[i]['PriceNet'])
        return '%.2f' % float(sum)

    def GetSumVAT(self, sessionId = '', curr = ''):
        if sessionId:
            self.GetContents(sessionId)
        sum = 0.0
        for i in range(len(self.__contents)):
            sum = sum + float(self.__contents[i]['PriceGross'] - self.__contents[i]['PriceNet'])
        return '%.2f' % float(sum)

    def GetSumGross(self, sessionId = '', curr = ''):
        if sessionId:
            self.GetContents(sessionId)
        sum = 0.0
        for i in range(len(self.__contents)):
            sum = sum + float(self.__contents[i]['PriceGross'])
        return '%.2f' % float(sum)
