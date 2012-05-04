#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 1.2.7.1 $
#
# Revision 1.1  2000-07-10 18:43:43+02  rb
# Initial revision
#
##

#
# I M P O R T  M O D U L E S
#
try:
  import sys
  import time
  import string
  
  import misc
  import aDataStruct
  import dtp
except:
  print '[auction: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()

#####################################################################
#
# C L A S S E S
#
#####################################################################

#
# Functions/triggers for AuctionJoin
#
class join(aDataStruct.aDS):

  #
  # Constructor
  #
  def __init__(self):
    
    # Call constructor of aDS
    aDataStruct.aDS.__init__(self)
    
    # Check session
    self.checkSession(SessionMustExist = 1, SessionMustBeAuthorized = 1)

    # Set timestamp of 'now'
    self.timeNow = time.time()

  #
  # Generate <option>-fields for m2-problem
  #
  def m2Options(self, ID = 0, selected = '', pcsString = '', m2String = ''):
  
    # Init
    options = []
    
    # Prepare statement
    stmt = 'SELECT m2, m2MinStueck, m2Stueck FROM AuctionBid WHERE ID = %i' % ID
    
    # Query database
    self.SQLAL.query(stmt)

    # Fetch result
    result = self.SQLAL.fetchall()
    
    # Go through rows of result
    for x in range(result[0][1], result[0][2] + 1):

      # Init
      option = ''
      
      # Set m2 * x
      m2x = result[0][0] * x
    
      # Generate <option>-string
      option = '<option value="%i"' % x

      # Check if value should be selected
      if selected:
        if result[i][0] == selected:
          option = '%s selected>' % option

      option = '%s>%i %s (%.4f %s)</option>' % (option, x, pcsString, m2x, m2String)

      # Append to list
      options.append(option)
    
    # Return list with <option>-tags
    return options

#
# Bid in auction
#
class AuctionBid(aDataStruct.aDS):

  #
  # Constructor
  #
  def __init__(self):
    
    # Call constructor of aDS
    aDataStruct.aDS.__init__(self)
    
    # Check session
    self.checkSession(SessionMustExist = 1, SessionMustBeAuthorized = 1)

    # Set timestamp of 'now'
    self.timeNow = time.time()

  def reformatFloat(self, f, prec):
    f_s = '%s' % f
    return float(f_s[:string.index(f_s, '.') + prec + 1])

  #
  # Auction
  #
  def cne_auction(self):
  	
    # Set 'Gebot' and 'naechstesGebot' to 'Einstiegspreis'
    self.cgiFieldStorage['Gebot'] = self.cgiFieldStorage['Mindestgebot']
    self.cgiFieldStorage['naechstesGebot'] = self.cgiFieldStorage['Mindestgebot']

  #
  # Tendering bid
  #
  def cne_tendering(self):
  
    # Set 'Gebot' = 'Maximalpreis'
    self.cgiFieldStorage['Gebot'] = self.cgiFieldStorage['Maximalpreis']

    # Set 'naechstesGebot' = 'Maximalpreis'
    self.cgiFieldStorage['naechstesGebot'] = self.cgiFieldStorage['Maximalpreis']

  #
  # Fixed price bid
  #
  def cne_fixedPrice(self):
  
    # Set 'Gebot' and 'naechstesGebot' = 'Maximalpreis'
    self.cgiFieldStorage['Gebot'] = self.cgiFieldStorage['Maximalpreis']
    self.cgiFieldStorage['naechstesGebot'] = self.cgiFieldStorage['Maximalpreis']

  #
  # Check start timestamp
  #
  def checkStartTimestamp(self):

    # Calculate difference between now and given start timestamp
    # Tolerance is 900 seconds (= 15 minutes; Session inactive timeout)
    time_diff = self.timeNow - float(self.cgiFieldStorage['Startdatum'])
    
    # If difference is in our tolerance
    if time_diff >= 900:
    
      # Set 'Startdatum' to 'now'
      self.cgiFieldStorage['Startdatum'] = self.timeNow
        
    # If 'Startdatum' > 'now', set 'active' to 0
    if float(self.cgiFieldStorage['Startdatum']) > self.timeNow:
      self.cgiFieldStorage['active'] = 0
        
    # Start
    else:
      self.cgiFieldStorage['active'] = 1

  #
  # Calculate end timestamp
  #
  def calcEndTimestamp(self):    

    # Calculate end timestamp (add 'Laufzeit' to start timestamp)
    self.cgiFieldStorage['Enddatum'] = float(self.cgiFieldStorage['Startdatum']) + float(self.cgiFieldStorage['Laufzeit'])

  #
  # Check/calculate value for m2-problem
  #
  def checkM2Problem(self):
  
    # Check existence of 'Breite' and 'Hoehe'
    if not self.cgiFieldStorage.has_key('Breite') or not self.cgiFieldStorage.has_key('Hoehe') or not self.cgiFieldStorage.has_key('m2MinStueck'):
      # Raise error
      raise '3521'
    else:
    
      try:

        # Calculate 'AuctionBid.m2', 'AuctionBid.Mindestabnahme', 'AuctionBid.AngeboteneMenge'
        # Convert to exactly %.4f
        self.cgiFieldStorage['m2'] = self.reformatFloat(float(self.cgiFieldStorage['Breite']) / 100 * float(self.cgiFieldStorage['Hoehe']) / 100, 4)
        self.cgiFieldStorage['Mindestabnahme'] = self.reformatFloat(int(self.cgiFieldStorage['m2MinStueck']) * self.cgiFieldStorage['m2'], 4)
        self.cgiFieldStorage['AngeboteneMenge'] = self.reformatFloat(int(self.cgiFieldStorage['m2Stueck']) * self.cgiFieldStorage['m2'], 4)
      
        self.log.write('Mindestabnahme,AngeboteneMenge:', 1, self.cgiFieldStorage['Mindestabnahme'], self.cgiFieldStorage['AngeboteneMenge'])
      
        # Return true
        return 1
    
      # Any error
      except:
      
        # Log
        self.log.write('auction.AuctionBid.checkM2Problem: %s(%s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
      
        # Return false
        return 0

  #
  # Check data
  #
  def checkNewEntry(self):
  
    # Init type
    bidType = '???'
  
    try:
    
      # Check 'Startdatum'/'now'
      self.checkStartTimestamp()
      
      # Calculate 'Enddatum'
      self.calcEndTimestamp()
      
      # Check for 'm2Stueck'
      if self.cgiFieldStorage.has_key('m2Stueck'):
        self.checkM2Problem()
      
      # Check 'Mindestabnahme' (if less than or equal to 0, set to 'AngeboteneMenge')
      if self.cgiFieldStorage['Mindestabnahme'] <= 0:
        self.cgiFieldStorage['Mindestabnahme'] = self.cgiFieldStorage['AngeboteneMenge']
  
      # Which type?
      # Auction bid
      if self.cgiFieldStorage['Auktionstyp'] == '1':
        bidType = 'AUCTION BID'
        self.cne_auction()

      # Tendering bid
      elif self.cgiFieldStorage['Auktionstyp'] == '2':
        bidType = 'TENDERING BID'
        self.cne_tendering()

      # Fixed price bid
      elif self.cgiFieldStorage['Auktionstyp'] == '3':
        bidType = 'FIXED PRICE BID'
        self.cne_fixedPrice()

      # Set CustomerID to id of logged in user
      self.cgiFieldStorage['CustomerID'] = self.whoID

      # Log
      self.log.write('INSERTING NEW %s INTO DATABASE' % bidType, printLevel = 1)
      
      # Return true
      return 1
    
    # Any error
    except:
      
      # Log
      self.log.write('%s(%s) INSERTING AUCTION INTO DATABASE' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
      
      # Return false
      raise sys.exc_info()[0], sys.exc_info()[1]

  #
  # UPDATE/DELETE an entry only if no bids exist
  #
  def updateDeleteEntry(self):

    self.log.write('qVars = ' % self.qVars, printLevel = 1)
  
    # Prepare SQL statement
    stmt = 'SELECT COUNT(*) FROM AuctionJoin WHERE AuktionsID = %i' % int(self.cgiFieldStorage['ID'])
    
    # Query database
    rowCount = self.SQLAL.query(stmt)
    
    # Fetch result
    result = self.SQLAL.fetchall()
    
    # Check for count
    if result[0][0]:
      # Bids exist, no update/delete: raise error
      raise '3515'

    elif self.qVars.has_key('qDelete'):
      return 1

    else:
      # Check for 'm2Stueck'
      if self.cgiFieldStorage.has_key('m2Stueck'):
        self.checkM2Problem()

      # Check 'Startdatum'/'now'
      self.checkStartTimestamp()
      
      # Calculate 'Enddatum'
      self.calcEndTimestamp()
  
      # Return true, no bids exist, update/delete is ok
      return 1

#
# Join an auction
#
class AuctionJoin(aDataStruct.aDS):

  #
  # Constructor
  #
  def __init__(self):
    
    # Call constructor of aDS
    aDataStruct.aDS.__init__(self)
    
    # Check session
    self.checkSession(SessionMustExist = 1, SessionMustBeAuthorized = 1)

    # Call constructor of class dTP
    self.__dtp = dtp.dTP()
    
    # Make instance of misc.email
    self.email = misc.email()
    
    # Set timestamp of 'now'
    self.timeNow = time.time()

  def reformatFloat(self, f, prec):
    f_s = '%s' % f
    return float(f_s[:string.index(f_s, '.') + prec + 1])

  #
  # Auction/tender:
  #
  # Delete old bids of user where status = 2
  #
  def deleteOldBids(self):

    # Log
    self.log.write('DELETING OLD BIDS OF CustomerID %i' % self.whoID, printLevel = 1)

    # Prepare SQL statement
    stmt = 'DELETE FROM AuctionJoin WHERE CustomerID = %i AND status = 2'  % self.whoID

    # Query database
    self.SQLAL.query(stmt)
    
  #
  # Auction
  #
  def auction(self):
  
    # 'Stueckpreis' must be >= 'naechstesGebot'
    if not float(self.cgiFieldStorage['Stueckpreis']) >= self.result[0]['naechstesGebot']:
      
      # Error, to less money
      raise '3511'
    
    # 'Menge' in range between 'Mindestmenge' and 'AngeboteneMenge'
    if not (float(self.cgiFieldStorage['Menge']) >= self.result[0]['Mindestabnahme'] and float(self.cgiFieldStorage['Menge']) <= self.result[0]['AngeboteneMenge']):
    
      # Error, to less or to much pieces
      raise '3512'
    
    # AuctionBid: Set 'Gebot' to 'Stueckpreis'
    self.SQLAL.act(action = 'U', table = 'AuctionBid', ID = self.result[0]['ID'], Gebot = float(self.cgiFieldStorage['Stueckpreis']))
    
    # AuctionJoin: Calculate 'Gesamtpreis' ('Stueckpreis' * 'Menge')
    self.cgiFieldStorage['Gesamtpreis'] = float(self.cgiFieldStorage['Stueckpreis']) * float(self.cgiFieldStorage['Menge'])

    # AuctionBid: Set 'naechstesGebot' to 'Gebot' + 'Mindesterhoehung'
    self.SQLAL.act(action = 'U', table = 'AuctionBid', ID = self.result[0]['ID'], naechstesGebot = float(self.cgiFieldStorage['Stueckpreis']) + float(self.result[0]['Mindesterhoehung']))
    
    # Auction/tender: Set status 4 (best bid at this time)
    self.cgiFieldStorage['status'] = 4

    # any other set to 2
    stmt = 'UPDATE AuctionJoin SET status = 2 WHERE AuktionsID = %i' % self.result[0]['ID']
    self.SQLAL.query(stmt)

    # Delete old bids of user
    self.deleteOldBids()

  #
  # Tendering
  #
  def tendering(self):
  
    # 'Stueckpreis' <= 'naechstesGebot'
    if not float(self.cgiFieldStorage['Stueckpreis']) <= (float(self.result[0]['naechstesGebot'])):
      
      # Error, bid must be less than
      raise '3513'
    
    # AuctionBid: Set 'Gebot' to 'Stueckpreis'
    self.SQLAL.act(action = 'U', table = 'AuctionBid', ID = self.result[0]['ID'], Gebot = float(self.cgiFieldStorage['Stueckpreis']))
    
    # AuctionBid: Set 'naechstesGebot' to 'Stueckpreis' - 0.1
    self.SQLAL.act(action = 'U', table = 'AuctionBid', ID = self.result[0]['ID'], naechstesGebot = float(self.cgiFieldStorage['Stueckpreis']) - 0.1)
      
    # AuctionJoin: Set 'Gesamtpreis' = 'Stueckpreis' * 'Mindestabnahme'
    self.cgiFieldStorage['Gesamtpreis'] = float(self.cgiFieldStorage['Stueckpreis']) * self.result[0]['Mindestabnahme']

    # 'Menge' must be == 'AngeboteneMenge'
    self.cgiFieldStorage['Menge'] = self.result[0]['Mindestabnahme']

    # Auction/tender: Set status 4 (best bid at this time)
    self.cgiFieldStorage['status'] = 4

    # any other set to 2
    stmt = 'UPDATE AuctionJoin SET status = 2 WHERE AuktionsID = %i' % self.result[0]['ID']
    self.SQLAL.query(stmt)
      
    # Delete old bids of user
    self.deleteOldBids()

  #
  # Fixed price bid
  #
  def fixedPrice(self):

    # 'Menge' in range between 'Mindestabnahme' and 'AngeboteneMenge'?
    if not (float(self.cgiFieldStorage['Menge']) >= self.result[0]['Mindestabnahme'] and float(self.cgiFieldStorage['Menge']) <= self.result[0]['AngeboteneMenge']):
    
      self.log.write('MENGENVERGL.: Menge,Mindestabnahme,AngeboteneMenge', 1, float(self.cgiFieldStorage['Menge']), self.result[0]['Mindestabnahme'], self.result[0]['AngeboteneMenge'])
    
      # Error, to less or to much
      raise '3512'
    
    # Calculate new value for 'AngeboteneMenge'
    AngeboteneMenge_new = self.result[0]['AngeboteneMenge'] - float(self.cgiFieldStorage['Menge'])

    # AuctionBid: Set 'AngeboteneMenge' to 'AngeboteneMenge' - 'Menge'
    self.SQLAL.act(action = 'U', table = 'AuctionBid', ID = self.result[0]['ID'], AngeboteneMenge = AngeboteneMenge_new)

    # Check m2
    if self.cgiFieldStorage.has_key('m2Stueck'):
      m2Stueck_new = self.result[0]['m2Stueck'] - int(self.cgiFieldStorage['m2Stueck'])
      self.SQLAL.act(action = 'U', table = 'AuctionBid', ID = self.result[0]['ID'], m2Stueck = m2Stueck_new)

    # AuctionJoin: set 'Stueckpreis' to 'Maximalpreis'
    self.cgiFieldStorage['Stueckpreis'] = self.result[0]['Maximalpreis']

    # AuctionJoin: set 'Gesamtpreis' to 'Maximalpreis' * 'Menge'
    self.cgiFieldStorage['Gesamtpreis'] = self.result[0]['Maximalpreis'] * float(self.cgiFieldStorage['Menge'])

    # AuctionJoin: Set 'status' to 3
    self.cgiFieldStorage['status'] = 3

    # AuctionJoin: disable fixed price bid, if 'AngeboteneMenge' == 0
    if AngeboteneMenge_new == 0:
    
      # Set 'aktiv' to 0
      self.cgiFieldStorage['aktiv'] = 0
      
      # Disable fixed price bid
      self.SQLAL.act(action = 'U', table = 'AuctionBid', ID = self.result[0]['ID'], active = 0, beendet = 1)
      
      # Log
      self.log.write('AuctionBid ID %i: EVERYTHING SOLD, DISABLING' % self.result[0]['ID'], printLevel = 1)

  #
  # m2-problem
  #
  def checkM2Problem(self):
    
    # If we got 'm2Stueck'
    if self.cgiFieldStorage.has_key('m2Stueck'):
    
      try:

        # Query database for 'm2' in 'AuctionBid.AuktionsID'
        stmt = 'SELECT m2 FROM AuctionBid WHERE ID = %i' % int(self.cgiFieldStorage['AuktionsID'])
        self.SQLAL.query(stmt)
        result = self.SQLAL.fetchall()
      
      # Any error
      except:
      
        # Log
        self.log.write('auction.AuctionJoin.checkM2Problem: %s(%s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)

        # Raise error
        raise '1221'

      # Calculate 'Menge'
      self.cgiFieldStorage['Menge'] = self.reformatFloat(float(int(self.cgiFieldStorage['m2Stueck']) * result[0][0]), 4)
      
  #
  # Check data
  #
  def checkNewEntry(self):
  
    # Get data from database for auction 'AuktionsID'
    rowCount, self.result = self.SQLAL.search(table = 'AuctionBid', \
      fields = 'ID, CustomerID, Auktionstyp, Startdatum, Enddatum, naechstesGebot, Maximalpreis, Mindesterhoehung, Mindestabnahme, AngeboteneMenge, MengederBezuege, m2Stueck', \
      ID = self.cgiFieldStorage['AuktionsID'])
    
    # Check rowCount
    if not rowCount:
    
      # Error, no result from database
      raise '1221'
  
    # 'now' <= 'Enddatum' && 'now' >= 'Startdatum'
    ###if not (self.timeNow <= self.result[0]['Enddatum'] and self.timeNow >= self.result[0]['Startdatum']):
    startNowDiff = self.timeNow - self.result[0]['Startdatum']
    if not (self.timeNow <= self.result[0]['Enddatum'] and startNowDiff >= -5):
    
      # Error, times are not correct
      raise '1111'
    
    # AuctionBid: Check 'Enddatum': if < 600 seconds, add 600 seconds (= 10 minutes) to 'Enddatum'
    time_diff = self.timeNow - float(self.result[0]['Enddatum'])
    if time_diff <= 600:
    
      # Increase 'Enddatum' by 600 seconds
      self.SQLAL.act(action = 'U', table = 'AuctionBid', ID = self.result[0]['ID'], Enddatum = float(self.result[0]['Enddatum']) + 600)
    
    # Check for m2-problem
    self.checkM2Problem()

    # Check for 'Auktionstyp'
    # Auction
    if self.result[0]['Auktionstyp'] == 1:
      self.auction()

    # Tendering bid
    elif self.result[0]['Auktionstyp'] == 2:
      self.tendering()

    # Fixed price bid
    elif self.result[0]['Auktionstyp'] == 3:
      self.fixedPrice()
    
    # AuctionBid: Increase 'MengederBezuege'
    self.SQLAL.act(action = 'U', table = 'AuctionBid', ID = self.result[0]['ID'], MengederBezuege = int(self.result[0]['MengederBezuege']) + 1)
    self.log.write('AuctionBid ID %i INCREASED MengederBezuege BY 1, NOW %i' % (self.result[0]['ID'], int(self.result[0]['MengederBezuege']) + 1), printLevel = 1)

    # AuctionJoin: Set OriginatorID to id of user originating auction/tender/fixed price bid
    self.cgiFieldStorage['OriginatorID'] = self.result[0]['CustomerID']
    
    # AuctionJoin: Set CustomerID to id of logged in user
    self.cgiFieldStorage['CustomerID'] = self.whoID
    
    # AuctionJoin: Set 'Abgabezeitpunkt' to 'now'
    self.cgiFieldStorage['Abgabezeitpunkt'] = self.timeNow
    
    # Return true
    return 1
