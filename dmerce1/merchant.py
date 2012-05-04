#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 1.2.7.1 $'
#
# Revision 1.1  2000-05-11 15:04:05+02  rb
# Initial revision
#
##

#
# I M P O R T  M O D U L E S
#
try:
  import sys
  import os
  import string
  import time
  import operator

  import aDataStruct
  import misc
  import dtp
except:
  print '[merchant: ERROR LOADING MODULES: (%s, %s)' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()

#####################################################################
#
# C L A S S E S
#
#####################################################################

#
# The articles in a basket
#
class Article(aDataStruct.aDS):

  #
  # Constructor
  #
  def __init__(self, SAM = 1):
  
    # Call constructor of class aDS
    aDataStruct.aDS.__init__(self, CGI = 0)

    # Check web or script?
    m = misc.MISC()
    if m.env('REMOTE_ADDR'):
      SAM = 1
    else:
      SAM = 0
    del m

    if SAM:
      self.checkSession()
    
    # Assign values from qVars to local vars, if present
    # qArtNr
    if self.qVars.has_key('qArtNr'):
      self._qArtNr = self.qVars['qArtNr']
      
    # qQty
    if self.qVars.has_key('qQty'):
      self._qQty = float(self.qVars['qQty'])
    else:
      self._qQty = 1
    
    # Set timestamp
    self.timeNow = time.time()

  #####################################################################
  #
  # P R I V A T E
  #
  #####################################################################

  #
  # Get discount rate for customer depending on group of article
  #
  def _applyDiscountRate(self, whoID = 0, qArtNr = 0):
  
    # Init
    discountRate = 0
    applyDR = 1
  
    # Check for given arguments
    if not qArtNr:
      return '[NO ARTICLE NO GIVEN]'
    
    # Check whoID
    if not whoID:
      whoID = self.whoID

    try:

      # Get discount rate of customer    
      discountRate = self.getDiscountRate(whoID = whoID, qArtNr = qArtNr)

      # We found a discount rate
      if discountRate > 0:
        applyDR = 1 - (discountRate / 100)

      # Return discount rate in percent and operand to use as a multiplicator
      return discountRate, applyDR
    
    # Any error
    except:
      
      # Log
      self.log.write('merchant.Article._applyDiscountRate: %s(%s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
      
      # Return false
      return 0, 1

  #
  # Apply percent on price
  #
  def __applyPercentOnPrice(self, percent, price):
    return price * (1 - (percent / 100))

  #
  # Get group of article
  #
  def __getGroup(self, qArtNr):
  
    # Query database
    count, result = self.SQLAL.search(table = 'Artikel', fields = 'Warengruppe', ArtNr = qArtNr)
    
    # Yeeha
    if count:
      return result[0]['Warengruppe']
    
    # Meeeek
    else:
      return 0

  #
  # Raise available qty of article
  #
  def _raiseAvailQty(self, qArtNr = 0, qQty = 0):

    # Check for given arguments
    if not qArtNr:
    
      # Print error message
      print '[NO ARTICLE NO GIVEN]'
      
      # Return false
      return 0

    # Check qQty
    if not qQty:
      qQty = 1
      
    # Raise available qty of article 'Mengeverfuegbar'
    stmt = 'UPDATE Artikel SET Mengeverfuegbar = Mengeverfuegbar + %i WHERE ArtNr = "%s"' % (qQty, qArtNr)

    try:

      # Query database
      self.SQLAL.query(stmt)
    
      # Log
      self.log.write('merchant.Basket._raiseAvailQty: QUANTITY FOR ARTICLE %s RAISED BY %s' % (qArtNr, qQty), printLevel = 1)

    # Any error
    except:

      # Log
      self.log.write('merchant.Article._raiseAvailQty: %s(%s) UPDATING QUANTITY OF ARTICLE %s' % (sys.exc_info()[0], sys.exc_info()[1], self._qArtNr), printLevel = 1)

      # Return error
      return '[error]'

  #
  # Reduce available qty of article
  #
  def _reduceAvailQty(self, qArtNr = 0, qQty = 0):

    # Check for given arguments
    if not qArtNr:
    
      # Print error message
      print '[merchant.Article.reduceAvailQty: NO ARTICLE NO GIVEN]'
      
      # Return false
      return 0

    # Set qCoun to 1 if not set or 0
    if not qQty:
      qQty = 1
      
    # Reduce available qty of article 'Mengeverfuegbar'
    stmt = 'UPDATE Artikel SET Mengeverfuegbar = Mengeverfuegbar - %f WHERE ArtNr = "%s"' % (qQty, qArtNr)

    try:

      # Query database
      self.SQLAL.query(stmt)

      # Log
      self.log.write('merchant.Article.reduceAvailQty: %s %s REDUCED QUANTITY FOR ARTICLE %s BY %f' % (self.whoType, self.whoID, qArtNr, qQty), printLevel = 1)

    # Any error
    except:

      # Log
      self.log.write('merchant.Article.reduceAvailQty: %s(%s) UPDATING QUANTITY OF ARTICLE %s' % (sys.exc_info()[0], sys.exc_info()[1], self._qArtNr), printLevel = 1)

      # Return error
      return '[error]'

  #
  # Is article a special offer?
  #
  def isSpecialOffer(self, qArtNr = 0):
  
    # Get 'AktionVon', 'AktionBis', 'AktionProzent' of article
    count, result = self.SQLAL.search(table = 'Artikel', fields = 'AktionProzent, AktionAnfang, AktionEnde', ArtNr = qArtNr)
    
    # If 'AktionProzent' is set and times are correct
    if result[0]['AktionProzent'] and self.timeNow >= result[0]['AktionAnfang'] and self.timeNow <= result[0]['AktionEnde']:

      # Return percent value
      return result[0]['AktionProzent']

    else:

      # Return false
      return 0

  #
  # Special offer?
  #
  def __specialOffer(self, qArtNr, field):

    # Get percent value of discount rate
    percent = self.isSpecialOffer(qArtNr = qArtNr)
    
    if percent:
    
      # Query database
      stmt = 'SELECT %s FROM Artikel WHERE ArtNr = "%s"' % (field, qArtNr)
      self.SQLAL.query(stmt)
      result = self.SQLAL.fetchall()
      
      # Calculate reduced price
      price = self.__applyPercentOnPrice(percent = percent, price = result[0][0])

    # Return price
    return price

  #####################################################################
  #
  # P U B L I C
  #
  #####################################################################

  #
  # Calculate coverage of article
  #
  def calcCoverage(self, type = 'netto'):
  
    # ID of article
    ArtNr = self.cgiFieldStorage['ArtNr']
    
    # Sum sold
    sumSold = self.sumSold(ArtNr, type = type)
    
    # Calculate coverage
    coverage = sumSold - float(self.cgiFieldStorage['DeBErloesschm']) - float(self.cgiFieldStorage['varKosten'])
    
    try:

      # Update database
      stmt = 'UPDATE Artikel SET Deckungsbeitrag = %f, DeBChangedDateTime = %.6f WHERE ArtNr = "%s"' % (coverage, self.timeNow, ArtNr)
      self.SQLAL.query(stmt)
     
      # Return true
      return 1
    
    # Any error
    except:
    
      # Raise error
      raise '1225'

  #
  # Calculate DPR of article
  #
  def calcDPR(self):
  
    # ID of article
    ArtNr = self.cgiFieldStorage['ArtNr']
    
    # Calculate DPR
    DPR = float(self.getPrice(qArtNr = ArtNr, field = 'PreisVKnetto')) - self.getVAT(qArtNr = ArtNr) - float(self.cgiFieldStorage['DPRNachlaesse']) - float(self.cgiFieldStorage['DPRErloesschm']) - float(self.cgiFieldStorage['DPRRabatte']) - float(self.cgiFieldStorage['DPRWerbekostenzusch']) - float(self.cgiFieldStorage['DPK'])
    
    try:

      # Update database
      stmt = 'UPDATE Artikel SET DPR = %f, DPRChangedDateTime = %.6f WHERE ArtNr = "%s"' % (DPR, self.timeNow, ArtNr)
      self.SQLAL.query(stmt)
     
      # Return true
      return 1
    
    # Any error
    except:
    
      # Raise error
      raise '1225'

  #
  # Cost benefit analysis of an article
  #
  def costBenefit(self, qArtNr = 0, type = ''):
  
    # Calculate
    bought = self.timesSold(qArtNr) * float(self.getPrice(qArtNr = qArtNr, field = 'PreisEKbrutto'))    
    sold = self.timesSold(qArtNr) * float(self.getPrice(qArtNr = qArtNr, field = 'PreisVKbrutto'))
    costBenefit = sold - bought

    # Return cost benefit value
    return costBenefit

  #
  # Get price (standard: incl. VAT => 'PreisVKbrutto') of article in different currencies
  # Standard is to get EUR for gross price
  #
  def currency(self, price = 0, precision = '2', Curr = ''):

    # Check for given arguments
    if not price:
      return 0

    # Build format string    
    formatString = '%.' + precision + 'f'

    # No currency given?
    if not Curr:
      
      # Return price as is -> own currency
      return formatString % price
      
    # Initialize variable
    priceInOtherCurrency = 0

    # Use table 'Currency' to get values to calculate new price
    countCurrency, resultCurrency = self.SQLAL.search(table = 'Currency', fields = 'Operator, Value', Curr = Curr)
                                                
    # If we found price of article and calculation value for currency
    # => x in own currency = y in other currency, calculated with division '/'
    if countCurrency:

      # Division
      if resultCurrency[0]['Operator'] == '/':
        priceInOtherCurrency = operator.div(price, resultCurrency[0]['Value'])
      
      # Multiplication
      if resultCurrency[0]['Operator'] == '*':
        priceInOtherCurrency = operator.mul(price, resultCurrency[0]['Value'])

      # Return
      return formatString % priceInOtherCurrency

    # There was no such currency
    else:
    
      # Log
      self.log.write('merchant.Article.currency: CURRENCY %s NOT FOUND' % Curr, printLevel = 1)
      
      # Return false
      return 0

  #
  # Get discount rate
  #
  def getDiscountRate(self, whoID = 0, qArtNr = 0):
  
    # Check whoID
    if not whoID:
      whoID = self.whoID
  
    # Make instance of customer
    c = Customer()

    try:

      # Get discount for group of article depending on group of customer
      countDiscount, resultDiscount = self.SQLAL.search(table = 'Warengruppe', fields = 'Prozent%s' % c.getGroup(whoID = whoID), Bezeichnung = self.__getGroup(qArtNr))

      # Get discount rate in percent and calculate multiplicator
      return resultDiscount[0][resultDiscount[0].keys()[0]]
    
    except:
      return 0

  #
  # Get price for customer
  #
  def getPrice(self, qArtNr = 0, field = '', precision = 2, whoID = 0, Curr = ''):
  
    # Init
    price = 0
    discountRate = 0
    applyDR = 1
    formatString = '%%.%if' % precision # Format: %.<precision>f

    # Check for given arguments
    if not qArtNr or not field:
      return '[MISSING ARGUMENTS]'

    # Get price of article; check for special offer
    if self.isSpecialOffer(qArtNr = qArtNr):
      price = self.__specialOffer(qArtNr, field)

    else:
    
      # Get price of article
      stmt = 'SELECT %s FROM Artikel WHERE ArtNr = "%s"' % (field, qArtNr)
      self.SQLAL.query(stmt)
      result = self.SQLAL.fetchall()
      price = result[0][0]
      
      # Do we want to get the reduced price?
      if whoID and self.whoType == 'Customer' and self.whoID:

        # Get discount rate
        discountRate, applyDR = self._applyDiscountRate(qArtNr = qArtNr)

    # Apply discount rate to price; return value
    return formatString % (float(self.currency(price = price, Curr = Curr)) * applyDR)

  #
  # Get VAT of article
  #
  def getVAT(self, qArtNr = ''):
    
    # Query database for 'PreisVKnetto' and 'PreisVKbrutto'
    stmt = 'SELECT PreisVKnetto, PreisVKbrutto FROM Artikel WHERE ArtNr = "%s"' % qArtNr
    self.SQLAL.query(stmt)
    result = self.SQLAL.fetchall()
    
    # PreisVKbrutto - PreisVKnetto = MwSt
    return result[0][1] - result[0][0]

  #
  # Summarize money (of times an article was sold)
  #
  def sumSold(self, qArtNr = '', type = ''):
  
    # How often article was sold?
    timesSold = self.timesSold(qArtNr)

    # Query database
    stmt = 'SELECT PreisEK%s, PreisVK%s FROM Artikel WHERE ArtNr = "%s"' % (type, type, qArtNr)
    rowCount = self.SQLAL.query(stmt)
    result = self.SQLAL.fetchall()
    
    # Return
    return float((result[0][1] - result[0][0]) * timesSold)

  #
  # Times an article was sold
  #
  def timesSold(self, qArtNr = ''):
    
    # Select closed baskets
    stmt = 'SELECT BasketID FROM Baskets WHERE closed = 1'
    rowCount = self.SQLAL.query(stmt)
    result = self.SQLAL.fetchall()
    
    sold = 0.0
    
    for row in range(rowCount):

      # Select count of article
      stmt = 'SELECT Anzahl FROM Warenkorb WHERE ID = %i AND ArtNr = "%s"' % (result[row][0], qArtNr)
      rowCountArticle = self.SQLAL.query(stmt)
      if rowCountArticle:
        resultArticle = self.SQLAL.fetchall()
        sold = sold + resultArticle[0][0]
      
    # Return times of an article sold
    return sold

  #
  # Update
  #
  def updateActionPrice(self):

    # Convert 'AktionAnfang', 'AktionEnde' into timestamp
    t = misc.TIME()

    # 'AktionAnfang': Check for type of date and convert
    if t.isISODate(self.cgiFieldStorage['AktionAnfang']):
    
      # Convert from ISO
      timeBegin = t.isoToTimestamp(self.cgiFieldStorage['AktionAnfang'])

    elif t.isGermanDate(self.cgiFieldStorage['AktionAnfang']):
    
      # Convert from german
      timeBegin = t.germanToTimestamp(self.cgiFieldStorage['AktionAnfang'])

    else:
    
      # Default to 0
      timeBegin = 0
      
      # Log
      self.log.write('merchant.Article.updateActionPrice: CAN\'T DETERMINE TYPE OF AktionAnfang', printLevel = 1)
        
    # 'AktionEnde': Check for type of date and convert
    if t.isISODate(self.cgiFieldStorage['AktionEnde']):
    
      # Convert from ISO
      timeEnd = t.isoToTimestamp(self.cgiFieldStorage['AktionEnde'])

    elif t.isGermanDate(self.cgiFieldStorage['AktionEnde']):
    
      # Convert from german
      timeEnd = t.germanToTimestamp(self.cgiFieldStorage['AktionEnde'])

    else:
    
      # Default to 0
      timeEnd = 0
      
      # Log
      self.log.write('merchant.Article.updateActionPrice: CAN\'T DETERMINE TYPE OF AktionEnde', printLevel = 1)
        
    # Update database
    stmt = 'UPDATE Artikel SET AktionAnfang = %.6f, AktionEnde = %.6f, AktionProzent = %f WHERE ID = %i' % (timeBegin, timeEnd, float(self.cgiFieldStorage['AktionProzent']), int(self.cgiFieldStorage['ID']))
    self.SQLAL.query(stmt)

  #
  # Update prices net -> gross
  #
  def updatePrices(self):

    # Prepare statement
    stmt = 'UPDATE Artikel SET PreisEKbrutto = (PreisEKnetto / 100) * (100 + PreisEKmwst), PreisVKbrutto = (PreisVKnetto / 100) * (100 + PreisVKmwst)'

    try:

      # Query database
      self.SQLAL.query(stmt)
     
      # Check for 'AktionAnfang', 'AktionEnde' and 'AktionProzent'
      if self.cgiFieldStorage.has_key('AktionAnfang') and self.cgiFieldStorage.has_key('AktionEnde') and self.cgiFieldStorage.has_key('AktionProzent'):
        self.updateActionPrice()

      # Return true
      return 1

    # Any error
    except:

      # Log
      self.log.write('merchant.Article.updatePrices: %s(%s) UPDATING ARTICLE PRICES' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)

      # Return false
      return 0

  #
  # Increase quantity of article with cgiFieldStorage['MengeErhoehung']
  #
  def updateQty(self):

    # If 'MengeErhoehung' is given
    if self.cgiFieldStorage.has_key('MengeErhoehung'):
      
      # Prepare statement
      stmt = 'UPDATE Artikel SET BestAktuell = BestAktuell + %f, Mengeverfuegbar = Mengeverfuegbar + %f, DatumErhoehung = %.6f WHERE ID = "%s"' \
             % (float(self.cgiFieldStorage['MengeErhoehung']), float(self.cgiFieldStorage['MengeErhoehung']), time.time(), self.cgiFieldStorage['ID'])

      try:

        # Query database
        self.SQLAL.query(stmt)
        
        # Log
        self.log.write('merchant.Article.updateQty: %s %s CHANGED QUANTITY OF ARTICLE ID %s CHANGED BY %f' % (self.whoType, self.whoID, self.cgiFieldStorage['ID'], float(self.cgiFieldStorage['MengeErhoehung'])), printLevel = 1)

        # Return 1, everything was ok
        return 1
    
      # Any error
      except:

        # Log
        self.log.write('merchant.Article.updateQty: %s(%s) UPDATING QUANTITY OF ARTICLE %s' % (sys.exc_info()[0], sys.exc_info()[1], self.cgiFieldStorage['ID']), printLevel = 1)

        # Return error
        return '[error]'

    # No key 'MengeErhoehung'
    else:
      return 0

#
# The basket
#
class Basket(Article):

  # Init some vars
  __ID = 0
  _qArtNr = 0
  _qQty = 0

  #
  # Constructor
  #
  def __init__(self):
  
    # Set actual time
    self.timeNow = time.time()
  
    # Check web or script?
    m = misc.MISC()
    if m.env('REMOTE_ADDR'):
      SAM = 1
    else:
      SAM = 0
    del m

    # Call constructor of class article
    Article.__init__(self, SAM = SAM)
    
    # Init basket
    if SAM:
      self.__initBasket()

  #####################################################################
  #
  # P R I V A T E
  #
  #####################################################################

  #
  # Calculate all articles in the basket
  # care of discount rates for customer 'whoID'
  #
  def __calcArticles(self):
  
    # Go through list of entries in basket
    entries = self.__entries()
    for entry in entries:
      
      # Get discount rate for article
      discountRate = 0
      applyDR = 1
      if self.getWho(qWhat = 'Type') == 'Customer':
        discountRate, applyDR = self._applyDiscountRate(whoID = self.getWho(qWhat = 'ID'), qArtNr = entry[0])
      
      # Get prices of article (getPrice() takes care on discounts!!!)
      PreisVKnetto = float(self.getPrice(qArtNr = entry[0], field = 'PreisVKnetto', whoID = self.getWho(qWhat = 'ID')))
      PreisVKnettoGesamt = PreisVKnetto * entry[1]
      PreisVKbrutto = float(self.getPrice(qArtNr = entry[0], field = '(PreisVKnetto/100)*(100+PreisVKmwst)', whoID = self.getWho(qWhat = 'ID')))
      PreisVKbruttoGesamt = PreisVKbrutto * entry[1]
      
      # Update basket
      stmt = 'UPDATE Warenkorb SET PreisVKnetto = %f, PreisVKnettoGesamt = %f, PreisVKbrutto = %f, PreisVKbruttoGesamt = %f, Rabatt = %f WHERE ArtNr = "%s" AND ID = %i' \
              % (PreisVKnetto, PreisVKnettoGesamt, PreisVKbrutto, PreisVKbruttoGesamt, discountRate, entry[0], self.getID())

      self.SQLAL.query(stmt)
      
  #
  # Warn about actions of a non-agent on a closed basket
  #
  def __checkAction(self):

    if not self.whoType == 'Agent':

      # Check if basket is closed
      if self.isClosed():
      
        # Log
        self.log.write('merchant.Basket.__checkAction: NON-AGENT TRIED TO ACCESS A CLOSED BASKET!', printLevel = 1)
      
        # Raise error
        raise '3431'
      
      else:
        
        # Return true
        return 1
  
  #
  # Get entries in actual basket
  #
  def __entries(self):
  
    # Prepare SQL statement
    stmt = 'SELECT ArtNr, Anzahl FROM Warenkorb WHERE ID = %i' % self.getID()
    
    # Query database
    self.SQLAL.query(stmt)
    
    # Fetch result
    result = self.SQLAL.fetchall()
    
    # Return list of rows
    return result

  #
  # Get new ID for basket
  #
  def __getNewID(self):
  
    # Get MAX(ID) FROM Baskets
    stmt = 'SELECT MAX(BasketID) FROM Baskets'
  
    # Query database
    self.SQLAL.query(stmt)

    # Fetch result
    result = self.SQLAL.fetchall()
    
    # We found baskets
    if result[0][0]:

      self.log.write('getNewID: %s' % stmt, 1, result)

      # Increase ID by one
      return result[0][0] + 1
    
    # First basket
    else:
      return 1

  #
  # Get last basket ID for user which is an offer and not closed
  #
  def __getOlderBasket(self):
  
    # Prepare SQL statement
    stmt = 'SELECT BasketID, closed FROM Baskets WHERE whoType = "%s" AND whoID = %i AND Type = 2 AND closed = 0' % (self.whoType, self.whoID)
    
    self.log.write('__getOlderBasket: %s' % stmt, printLevel = 1)

    # Query database
    rowCount = self.SQLAL.query(stmt)
    
    if rowCount:

      # Fetch result
      result = self.SQLAL.fetchall()

      # Log
      self.log.write('merchant.Basket.__getOlderBasket: OLDER BASKET FOUND (%s)' % stmt, 1, result)
    
      # If we have an older basket
      if result[0][0]:

        # Return BasketID
        return result[0][0]
    
    else:
        
      # Log
      self.log.write('merchant.Basket.__getOlderBasket: NO OLDER BASKET FOUND (%s)' % stmt, printLevel = 1)
    
      # Return false: open new basket
      return 0

  #
  # Do we have an open basket for actual session?
  #
  def __getRegisteredID(self):
    
    # Select all open baskets for actual session
    stmt = 'SELECT BasketID FROM SessionBasket WHERE SID = %i' % self.SID
    
    # Query database; check for count
    if not self.SQLAL.query(stmt):
      return 0
    
    # Fetch result
    result = self.SQLAL.fetchall()
    BasketID = result[0][0]
    
    # If we have not qTrigger and qID set, look, if Basket is closed
    if BasketID:

      stmt = 'SELECT Type FROM Baskets WHERE BasketID = %i' % BasketID

      if not self.SQLAL.query(stmt):
        return 0

      result = self.SQLAL.fetchall()

      # Return ID, if 'Type' is not 1
      if result[0][0] != 1:
        return BasketID
      else:
        return 0

    else:
      return 0

  #
  # Look for entries in actual basket
  #
  def __hasContent(self):
  
    # Prepare SQL statement
    stmt = 'SELECT COUNT(*) FROM Warenkorb WHERE ID = %i' % self.getID()
    
    # Query database
    self.SQLAL.query(stmt)
    
    # Fetch result
    result = self.SQLAL.fetchall()
    
    # Return count of rows
    return result[0][0]

  #
  # Init basket
  #
  def __initBasket(self):

    # Check, if someone wants to see an order
    # in normal case, this would cause opening a new basket, because orders can't
    # be modified anymore
    # also set ID of order to view it one time!
    if self.qVars.has_key('qTrigger') and self.qVars.has_key('qID'):
      self.__setID(int(self.qVars['qID']))
    
    else:

      # Assign values from qVars to local vars, if present
      # qID -> self.__ID
      if self.qVars.has_key('qID'):
    
        # Register ID qVars['qID'] in database
        self.__registerID(int(self.qVars['qID']))

      # Determine/set __ID if we have an open basket
      self.__setID(self.__getRegisteredID())

      # No ID could be registered    
      if not self.getID():

        # Check for an older basket, which is not closed
        if not self.whoType == 'NOBODY':
          self.log.write('NOT NOBODY: TRY TO GET OLDER BASKET', printLevel = 1)
          olderBasketID = self.__getOlderBasket()
        else:
          self.log.write('NOBODY: OPENING NEW BASKET', printLevel = 1)
          olderBasketID = 0

        # Set new actual basket
        if not olderBasketID:

          # If not open a new one
          self.openNew()
      
        else:

          # Set older basket as actual
          self.__setID(olderBasketID)

    # Check owner of actual basket, if we are not 'NOBODY' and whoType of basket is 'NOBODY'
    if self.whoType != 'NOBODY' and self.getWho(qWhat = 'Type') == 'NOBODY':
    
      # Set whoType of basket to self.whoType
      self.setWho(qWhoType = self.whoType, qWhoID = self.whoID)

  #
  # Register basket ID for current session
  #
  def __registerID(self, ID):

    ##### SessionBasket
    # Is there an entry for that session?
    # Query database
    stmt = 'SELECT BasketID FROM SessionBasket WHERE SID = %i' % self.SID
    rowCount_SessionBasket = self.SQLAL.query(stmt)
    
    ##### Baskets
    # Is there an entry for that session?
    # Query database
    stmt = 'SELECT BasketID FROM Baskets WHERE BasketID = %i' % ID
    rowCount_Baskets = self.SQLAL.query(stmt)

    # No entry in 'SessionBasket'
    if not rowCount_SessionBasket:

      # Insert; SessionBasket
      stmt = 'INSERT INTO SessionBasket (SID, BasketID) VALUES (%i, %i)' % (self.SID, ID)
      
      # Query database
      self.SQLAL.query(stmt)

    else:
    
      # Update
      stmt = 'UPDATE SessionBasket SET BasketID = %i WHERE SID = %i' % (ID, self.SID)

      # Query database
      self.SQLAL.query(stmt)

    # No entry in 'Baskets'
    if not rowCount_Baskets:

      # Insert; Baskets
      stmt = 'INSERT INTO Baskets (CreatedDateTime, ChangedDateTime, BasketID, Type, whoType, whoID) VALUES (%f, %f, %i, 2, "%s", %i)' % (self.timeNow, self.timeNow, ID, self.whoType, self.whoID)

      # Query database
      self.SQLAL.query(stmt)

    # Log
    self.log.write('merchant.Basket.__registerID: REGISTERED BASKET ID %i FOR SESSION %i' % (ID, self.SID), printLevel = 1)
    
  #
  # Set ID of actual basket
  #
  def __setID(self, ID):
  
    # Set __ID
    self.__ID = ID
    
    self.log.write('set ID:', 1, self.__ID)
    
  #####################################################################
  #
  # P U B L I C
  #
  #####################################################################

  #
  # Close basket
  #
  def close(self):
  
    # Prepare SQL statement to delete basket
    stmt = 'UPDATE Baskets SET closed = 1 WHERE BasketID = %i' % self.getID()

    # Query database
    self.SQLAL.query(stmt)
    
    # Log
    self.log.write('merchant.Basket.close: CLOSED BASKET ID %i' % self.getID(), printLevel = 1)

  #
  # Decrease count of something in the basket
  #
  def decQty(self):

    # Check for given arguments
    if not self._qArtNr:
      return 0

    # Get actual count
    count, result = self.SQLAL.search(table = 'Warenkorb', fields = 'Anzahl', ID = self.getID(), ArtNr = self._qArtNr)

    # Return, if not result (session timeout?)
    if not count:
      return 0

    # Decrease
    countNew = result[0]['Anzahl'] - self._qQty

    # Check if qQty is 0
    if countNew == 0:

      # Remove article from basket
      self.removeArticle(qArtNr = self._qArtNr)
      
    else:
      # Prepare statement
      stmt = 'UPDATE Warenkorb SET Anzahl = %f, PreisVKnettoGesamt = Anzahl * PreisVKnetto, PreisVKbruttoGesamt = Anzahl * PreisVKbrutto WHERE ID = %i AND ArtNr = "%s"' % (countNew, self.getID(), self._qArtNr)

      # Update database
      self.SQLAL.query(stmt)

    # Set actual basket ID -> qSuperSearch
    self.show()

    # Return true
    return 1

  #
  # Get count of articles
  #
  def getCount(self, qID = 0):
  
    # Set qID
    if not qID:
      qID = self.getID()
    
    # Prepare statement
    stmt = 'SELECT COUNT(*) FROM Warenkorb WHERE ID = %i' % qID
    
    # Query database
    self.SQLAL.query(stmt)
    
    # Fetch and return result
    result = self.SQLAL.fetchall()
    return result[0][0]

  #
  # Get ID of basket
  #
  def getID(self):
    return self.__ID

  #
  # Get name of basket
  #
  def getName(self, ID = 0):
  
    # Check ID
    if not ID:
    
      # Get ID of actual basket
      ID = self.getID()
    
    # Prepare statement
    stmt = 'SELECT Name FROM Baskets WHERE BasketID = %i' % ID
    
    # Query database
    self.SQLAL.query(stmt)
    
    # Fetch result
    result = self.SQLAL.fetchall()
    
    # Return
    if result:
      return result[0][0]
    else:
      return ''

  #
  # Get type of basket
  #
  def getType(self, ID = 0, qWhat = '', format = '%Y-%m-%d %H:%M:%S'):
  
    # Check ID
    if not ID:
    
      # Get ID of actual basket
      ID = self.getID()
    
    # Fetch ID of type
    stmt = 'SELECT Type FROM Baskets WHERE BasketID = %i' % ID

    # Query database
    self.SQLAL.query(stmt)
    
    # Fetch result
    result = self.SQLAL.fetchall()
    
    # Fetch name of type
    if qWhat == 'Name':

      # Prepare statement
      stmt = 'SELECT Name FROM BasketType WHERE ID = %i' % result[0][0]
    
    # Fetch date of type
    elif qWhat == 'Date':

      # Prepare statement
      stmt = 'SELECT TypeDate FROM Baskets WHERE BasketID = %i' % ID
    
    # Query database
    self.SQLAL.query(stmt)
    
    # Fetch result
    result = self.SQLAL.fetchall()
    
    # Return
    if qWhat == 'Date':
      t = misc.TIME()
      return t.unixToLocal(secs = result[0][0], format = format)
    else:
      return result[0][0]

  #
  # Get 'who' identity of basket
  #
  def getWho(self, ID = 0, qWhat = '', qTable = ''):
  
    # Check ID
    if not ID:
    
      # Get ID of actual basket
      ID = self.getID()
    
    # Check qWhat
    if not qWhat:
    
      # Get qhat from qVars
      qWhat = self.qVars['qWhat']

    # ID or type of who identity
    if qWhat == 'ID' or qWhat == 'Type':

      # Prepare statement
      stmt = 'SELECT who%s FROM Baskets WHERE BasketID = %i' % (qWhat, ID)
    
      # Query database
      rowCount = self.SQLAL.query(stmt)
    
      if rowCount:

        # Fetch result
        result = self.SQLAL.fetchall()
    
        # Return
        return result[0][0]
      
      else:
        if qWhat == 'Type':
          return 'NOBODY'
        else:
          return 0
     
    # Name of who identity
    else:

      # Prepare statement
      stmt = 'SELECT whoID FROM Baskets WHERE BasketID = %i' % ID
    
      # Query database
      rowCount = self.SQLAL.query(stmt)
    
      # Fetch result
      result = self.SQLAL.fetchall()

      if rowCount:

        # Prepare statement
        stmt = 'SELECT %s FROM %s WHERE ID = %i' % (qWhat, qTable, result[0][0])

        try:

          # Query database
          rowCount = self.SQLAL.query(stmt)
    
          # Fetch result
          result = self.SQLAL.fetchall()
    
          # Return
          return result[0][0]
        
        except:
          return ''
      
      else:
        return 'NOBODY'
     
  #
  # Increase count of something in the basket
  #
  def incQty(self):
    
    # Check for given arguments
    if not self._qArtNr:
      return 0

    # Check action
    self.__checkAction()

    # Get actual count
    stmt = 'SELECT Anzahl FROM Warenkorb WHERE ID = %i AND ArtNr = "%s"' % (self.getID(), self._qArtNr)
    self.SQLAL.query(stmt)
    result = self.SQLAL.fetchall()

    # Increase
    countNew = result[0][0] + self._qQty
      
    # Prepare statement
    stmt = 'UPDATE Warenkorb SET Anzahl = %f, PreisVKnettoGesamt = Anzahl * PreisVKnetto, PreisVKbruttoGesamt = Anzahl * PreisVKbrutto WHERE ID = %i AND ArtNr = "%s"' % (countNew, self.getID(), self._qArtNr)

    # Update database
    self.SQLAL.query(stmt)
    
    # Set actual basket ID -> qSuperSearch
    self.show()

    # Return true
    return 1

  #
  # Is actual basket closed?
  #
  def isClosed(self):
    
    # Prepare SQL statement
    stmt = 'SELECT closed FROM Baskets WHERE BasketID = %i' % self.getID()
    
    # Query database
    self.SQLAL.query(stmt)
    
    # Fetch result
    result = self.SQLAL.fetchall()
    
    # Return closed
    return result[0][0]
  
  #
  # Init/open new basket
  #
  def openNew(self):
  
    self.log.write('merchant.Basket.openNew', printLevel = 1)
  
    # Get next/new ID
    newID = self.__getNewID()
  
    # Reserve newID
    self.__registerID(newID)

    # Set new ID
    self.__setID(newID)

    # Log
    self.log.write('merchant.Basket.openNew: OPENED BASKET ID %i' % self.getID(), printLevel = 1)
    
    # Set qSuperSearch
    self.qVars['qSuperSearch'] = 'ID*%i' % self.getID()
    
    # Return true
    return 1

  #
  # Process an reservation or order
  #
  def process(self, qID = 0, qType = 0):
  
    # Check qID
    if not qID:
    
      # Get ID of actual basket
      qID = self.getID()

    # Check qType
    if not qType:
    
      # Get qType from qVars
      qType = int(self.qVars['qType'])
  
    # Init
    purchaser = ''
    deliveryDate = ''
    ordererEMail = ''
    ordererInfo = ''
    template = ''
    
    # On order check
    if qType == 1:

      # 'BestellerName'
      if not self.cgiFieldStorage.has_key('BestellerName'):
    
        # Raise error
        raise '3422'
    
      else:
    
        # Set purchaser
        purchaser = self.cgiFieldStorage['BestellerName']
      
      # ordererEMail
      if self.cgiFieldStorage.has_key('ordererEMail'):
    
        # Set ordererEMail
        ordererEMail = self.cgiFieldStorage['ordererEMail']

      # ordererInfo
      if self.cgiFieldStorage.has_key('ordererInfo'):
    
        # Set ordererInfo
        ordererInfo = self.cgiFieldStorage['ordererInfo']

      # deliveryDate
      if self.cgiFieldStorage.has_key('deliveryDate'):
    
        # Set deliveryDate
        deliveryDate = self.cgiFieldStorage['deliveryDate']

    # Query database
    stmt = 'UPDATE Baskets SET Type = %i, TypeDate = %.6f, deliveryDate = "%s", BestellerName = "%s", ordererEMail = "%s", ordererInfo = "%s" WHERE BasketID = %i' % (qType, time.time(), deliveryDate, purchaser, ordererEMail, ordererInfo, qID)
    self.SQLAL.query(stmt)
    self.log.write('process: %s' % stmt, printLevel = 1)

    # Go thourgh list of entries of basket and reduce quantity of articles
    entries = self.__entries()
    for entry in entries:

      # Reduce quantity of article by value of difference between new count and actual count in of it in the basket
      self._reduceAvailQty(qArtNr = entry[0], qQty = entry[1])

    # On order: close actual basket
    if qType == 1:
      self.close()

    # Return true
    return 1

  #
  # Put something into the basket
  #
  def putArticle(self):

    # Check action
    self.__checkAction()
    
    # Check for given arguments
    if not self._qArtNr:
      
      # Return false
      return 0

    # Check, if we have this already in the basket
    if self.SQLAL.search(table = 'Warenkorb', fields = 'Anzahl', ID = self.getID(), ArtNr = self._qArtNr)[0]:
      
      # Return true
      return 1

    # Insert into basket
    else:

      # Get data of article
      count, result = self.SQLAL.search(table = 'Artikel', fields = 'PreisVKnetto', ArtNr = self._qArtNr)

      # Prepare statement for insert
      # Replace " with \" for database query in strings with string.replace
      stmt = 'INSERT INTO Warenkorb (ID, ArtNr, Anzahl, PreisVKnetto) VALUES (%i, "%s", %i, %f)' \
             % (self.getID(), self._qArtNr, self._qQty, result[0]['PreisVKnetto'])

      # Query database
      self.SQLAL.query(stmt)
      
      # Reduce available qty of article
      self._reduceAvailQty(qArtNr = self._qArtNr)

      # Log put into basket
      self.log.write('merchant.Basket.putArticle: %s ID %s PUT ARTICLE NO %s INTO BASKET ID %i' % (self.whoType, self.whoID, self._qArtNr, self.getID()), printLevel = 1)
      
    # Return true
    return 1

  #
  # Remove basket
  #
  def remove(self, qID = 0):
  
    # Check qID
    if not qID:
      if self.qVars.has_key('qID'):
        qID = int(self.qVars['qID'])

        # Delete qID
        del self.qVars['qID']
      
      else:
        qID = self.getID()
  
    # Check action
    self.__checkAction()

    # Remove content
    self.removeContent()

    try:
    
      # Prepare SQL statements
      stmts = []
      stmts.append('DELETE FROM Baskets WHERE BasketID = %i' % qID)
      stmts.append('DELETE FROM SessionBasket WHERE BasketID = %i' % qID)
      
      # Execute all statements
      for stmt in stmts:
        self.SQLAL.query(stmt)

      # Log empty basket
      self.log.write('merchant.Basket.remove: %s ID %s REMOVED BASKET ID %i' % (self.whoType, self.whoID, qID), printLevel = 1)
      
      # Call __initBasket
      self.__initBasket()
      
      # Return true
      return 1

    # Any error
    except:
    
      # Log
      self.log.write('merchant.Basket.remove: %s(%s) REMOVING BASKET ID %i' % (sys.exc_info()[0], sys.exc_info()[1], qID), printLevel = 1)
      
      # Return error message
      return '[ERROR DELETING ENTIRE BASKET]'
    
  #
  # Remove entire content of basket
  #
  def removeContent(self):

    # Check action
    self.__checkAction()

    try:

      # Get every article number of article(s) in basket
      count, result = self.SQLAL.search(table = 'Warenkorb', fields = 'ArtNr, Anzahl', ID = self.getID())

      # Call self.remove for every article in basket
      for Art2Remove in range(0, count):
        self.removeArticle(qArtNr = result[Art2Remove]['ArtNr'])
        self._raiseAvailQty(qArtNr = result[Art2Remove]['ArtNr'], qQty = result[Art2Remove]['Anzahl'])

      # Log empty basket
      self.log.write('merchant.Basket.removeContent: %s ID %s EMPTY BASKET ID %i' % (self.whoType, self.whoID, self.getID()), printLevel = 1)
      
      # Return true
      return 1

    # Any error
    except:
    
      # Log
      self.log.write('merchant.Basket.removeContent: %s(%s) REMOVING ENTIRE CONTENT OF BASKET ID %i' % (sys.exc_info()[0], sys.exc_info()[1], self.getID()), printLevel = 1)
      
      # Return error message
      return '[ERROR DELETING ENTIRE BASKET]'

  #
  # Remove article from the basket
  #
  def removeArticle(self, qArtNr = 0):
    
    # Check for given arguments
    if not qArtNr:
    
      # No article no. given, get from self._qArtNr
      qArtNr = self._qArtNr
      
      # Is still not set...
      if not qArtNr:
        
        # Return
        return 0

    # Check action
    self.__checkAction()

    # Get count of article in basket
    count, result = self.SQLAL.search(table = 'Warenkorb', fields = 'Anzahl', ID = self.getID(), ArtNr = qArtNr)
                
    # Raise qty of article by value of count in basket
    self._raiseAvailQty(qArtNr = qArtNr, qQty = result[0]['Anzahl'])
    
    # Prepare statement
    stmt = 'DELETE FROM Warenkorb WHERE ID = "%s" AND ArtNr = "%s"' % (self.getID(), qArtNr)

    # Query database
    self.SQLAL.query(stmt)

    # Log remove from basket
    self.log.write('merchant.Basket.removeArticle: %s ID %s REMOVED ARTICLE NO %s FROM BASKET ID %i' % (self.whoType, self.whoID, qArtNr, self.getID()), printLevel = 1)
    
    # Return true
    return 1

  #
  # Set name of basket
  #
  def setName(self, ID = 0, qName = ''):
  
    # Check ID
    if not ID:
    
      # Get ID of actual basket
      ID = self.getID()
    
    # Check name
    if not qName:
    
      # Get qName
      qName = self.qVars['qName']
    
    # Check action
    self.__checkAction()

    # Prepare statement
    stmt = 'UPDATE Baskets SET Name = "%s" WHERE BasketID = %i' % (qName, ID)
    
    # Query database
    self.SQLAL.query(stmt)
    
    # Log
    self.log.write('merchant.Basket.setName: SET NAME OF BASKET ID %i TO %s' % (ID, qName), printLevel = 1)
    
    # Set actual basket ID -> qSuperSearch
    self.show()

    # Return true
    return 1

  #
  # Set type of basket
  #
  def setType(self, ID = 0, qType = 0):
  
    # Check ID
    if not ID:
    
      # Get ID of actual basket
      ID = self.getID()
    
    # Check type
    if not qType:
    
      # Get qType
      qType = int(self.qVars['qType'])
    
    # Check action
    self.__checkAction()

    # Prepare statement
    stmt = 'UPDATE Baskets SET Type = %s WHERE BasketID = %i' % (qType, ID)
    
    # Query database
    self.SQLAL.query(stmt)
    
    # Log
    self.log.write('merchant.Basket.setType: SET TYPE OF BASKET ID %i TO %s' % (ID, qType), printLevel = 1)
    
    # Process type of basket; if type is 1 or 3
    if qType == 1 or qType == 3:
      self.process(qType = qType)

    # Set actual basket ID -> qSuperSearch
    self.show()

    # Return true
    return 1

  #
  # Set quantity of an article in the basket
  #
  def setQty(self):
    
    # Check for given arguments
    if not self._qArtNr:
      return 0

    # Check action
    self.__checkAction()

    # Remove article, when qQty is 0
    if self._qQty <= 0:
      return self.removeArticle()

    # Get actual count
    count, result = self.SQLAL.search(table = 'Warenkorb', fields = 'Anzahl', ID = self.getID(), ArtNr = self._qArtNr)
    
    # If count is not greater than 0; the article is not in the basket
    if not count:
      raise '3411'

    try:
    
      # Prepare statement
      stmt = 'UPDATE Warenkorb' \
             ' SET Anzahl = %f, PreisVKnettoGesamt = Anzahl * PreisVKnetto, PreisVKbruttoGesamt = Anzahl * PreisVKbrutto' \
             ' WHERE ID = %i AND ArtNr = "%s"' % (self._qQty, self.getID(), self._qArtNr)

      # Update database
      self.SQLAL.query(stmt)
      
      # Set actual basket ID -> qSuperSearch
      self.show()

      # Return true
      return 1
    
    # Any error
    except:
    
      # Log
      self.log.write('merchant.Basket.setQty: %s(%s) SETTING QUANTITY OF ARTICLE NO %s IN BASKET ID %i' % (sys.exc_info()[0], sys.exc_info()[1], self._qArtNr, self.getID()), printLevel = 1)
      
      # Set actual basket ID -> qSuperSearch
      self.show()

      # Return error '3412'
      return '3412'

  #
  # Set 'who' of basket
  #
  def setWho(self, ID = 0, qWhoID = 0, qWhoType = ''):
  
    # Check ID
    if not ID:
    
      # Get ID of actual basket
      ID = self.getID()
    
    # Check qWhoID
    if not qWhoID:
    
      # Get qVars['qWhoID']
      if self.qVars.has_key('qWhoID'):
        qWhoID = int(self.qVars['qWhoID'])
    
    # Check type
    if not qWhoType:
    
      # Get qVars['qWhoType']
      if self.qVars.has_key('qWhoType'):
        qWhoType = self.qVars['qWhoType']
    
    # Check action
    self.__checkAction()

    # Prepare statement
    stmt = 'UPDATE Baskets SET'
    
    # whoID to set?
    if qWhoID:
      stmt = '%s whoID = %i' % (stmt, qWhoID)
    
    # whoType to set?
    if qWhoType:

      # Add comma if needed    
      if qWhoID:
        stmt = '%s,' % stmt

      stmt = '%s whoType = "%s"' % (stmt, qWhoType)
    
    # WHERE-condition
    stmt = '%s WHERE BasketID = %i' % (stmt, ID)

    # Query database
    self.SQLAL.query(stmt)
    
    # Log
    logStr = 'merchant.Basket.setWho: SET'
    if qWhoType:
      logStr = '%s WHO TYPE: %s' % (logStr, qWhoType)
    if qWhoID:
      logStr = '%s WHO ID: %i' % (logStr, qWhoID)
    logStr = '%s OF BASKET ID %i' % (logStr, ID)
    self.log.write(logStr, printLevel = 1)

    # Call calcArticles
    if self.__hasContent():
      self.__calcArticles()

    # Return true
    return 1

  #
  # Use as a trigger:
  # Set actual basket id in qVars['qSuperSearch']
  #
  def show(self):
  
    # If basket has content, recalculate all entries in 'Warenkorb'
    if self.__hasContent():
      self.__calcArticles()
      
    # Set qSuperSearch
    self.qVars['qSuperSearch'] = 'ID*%i' % self.getID()
    self.log.write('SETTING ID*%i' % self.getID(), printLevel = 1)
    
    # Return true
    return 1

  #
  # Summarize basket from session SID excl. VAT
  #
  # types:
  # ======
  # Totalnet: Summarize prices net in own currency
  # Totalgross: Summarize prices gross of basket in own currency
  # VAT: Summarize VAT in own currency
  #
  def sum(self, qID = 0, type = '', Curr = ''):
  
    # Check ID
    if not qID:
      if self.qVars.has_key('qID'):
        qID = int(self.qVars['qID'])
      else:
        qID = self.getID()

    # Check for given arguments
    if not type:
      return '[NO TYPE GIVEN]'
    
    if type == 'Totalnet':
      stmt = 'SELECT SUM(PreisVKnetto * Anzahl) FROM Warenkorb WHERE ID = %i' % qID

    elif type == 'Totalgross':
      stmt = 'SELECT SUM(PreisVKbrutto * Anzahl) FROM Warenkorb WHERE ID = %i' % qID

    elif type == 'VAT':
      stmt = 'SELECT ROUND(SUM(PreisVKbrutto * Anzahl) - SUM(PreisVKnetto * Anzahl), 2) FROM Warenkorb WHERE ID = %i' % qID
    
    # Query database
    rowCount = self.SQLAL.query(stmt)

    # Check for result
    if rowCount:

      # Fetch result
      result = self.SQLAL.fetchall()

      # Result is not NULL?
      if result[0][0]:

        # Return
        return self.currency(price = result[0][0], Curr = Curr)

      # NULL
      else:

        # Return 0.00
        return 0.00

  #
  # Summarize all orders of customer
  #
  def sumOrders(self, whoID = 0, type = '', Curr = ''):
  
    # Check arguments
    if not type:
      return '[NO TYPE GIVEN]'

    # Check ID
    if not whoID:
      if self.qVars.has_key('whoID'):
        whoID = int(self.qVars['whoID'])
      else:
        whoID = self.whoID

    # Get all BasketID for customer
    stmt = 'SELECT BasketID FROM Baskets WHERE whoID = %i' % whoID
    rowCount = self.SQLAL.query(stmt)
    if not rowCount:
      return 0.00

    # Fetch result
    result = self.SQLAL.fetchall()

    # Init
    sum = 0.00

    # Net
    if type == 'net':
      stmt = 'SELECT SUM(PreisVKnetto * Anzahl) FROM Warenkorb WHERE ID = %i'

    # Gross
    elif type == 'gross':
      stmt = 'SELECT SUM(PreisVKbrutto * Anzahl) FROM Warenkorb WHERE ID = %i'
    	      
    # Summarize all baskets
    for row in range(rowCount):
    
      # Summarize contents of basket
      self.SQLAL.query(stmt % result[row][0])
      resultBasket = self.SQLAL.fetchall()

      # Add to sum of all baskets
      if resultBasket[0][0]:
        sum = sum + resultBasket[0][0]

    # Return
    return self.currency(price = sum, Curr = Curr)

#
# Customer
#
class Customer(aDataStruct.aDS):

  #
  # Constructor
  #
  def __init__(self):
  
    # Call constructor of class aDS
    aDataStruct.aDS.__init__(self, CGI = 0)
    self.checkSession()

  #
  # Get group of customer
  #
  def getGroup(self, whoID = 0):

    # Check argument whoID
    if not whoID:
      whoID = self.whoID

    # Query database
    count, result = self.SQLAL.search(table = 'Kunde', fields = 'Gruppe', ID = whoID)
    
    # Return
    if count:
      return result[0]['Gruppe']
    else:
      return 0

#####################################################################
#
# M A I N
#
#####################################################################

# Called as script
if __name__ == '__main__':

  # Arguments?
  if len(sys.argv) == 2:

    # Print revision and exit
    if sys.argv[1] == 'revision':
      print revision
      sys.exit()
