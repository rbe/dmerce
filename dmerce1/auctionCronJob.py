#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 1.2.7.2 $'
#
# Revision 1.1  2000-07-14 17:02:21+02  rb
# Initial revision
#
##

#
# I M P O R T  M O D U L E S
#
try:
  import sys
  import time
  import os
  
  import misc
  import aDataStruct
  import dtp
except:
  print '[auctionCronJob: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()
  
#####################################################################
#
# C L A S S E S
#
#####################################################################
class auction(aDataStruct.aDS):

  #
  # Constructor
  #
  def __init__(self):
  
    # Call constructor of class aDS
    aDataStruct.aDS.__init__(self)
    
    # Make instance of misc.TIME
    self.TIME = misc.TIME()
    
    # Make instance of misc.email
    self.email = misc.email()
    
    # Get actual timestamp
    self.timeNow = time.time()

  #
  # Check count of bids
  #
  def checkCountOfBids(self):
  
    # (select count(*) from AuctionJoin where AuktionsID = AuctionBid.ID) == MengederBezuege?
    stmt = 'SELECT COUNT(*) FROM AuctionJoin WHERE AuktionsID = %i' % self.result[self.row]['ID']
    rowCount = self.SQLAL.query(stmt)
    result = self.SQLAL.fetchall()
    
    # If we found something
    if result[0][0]:
      # Is count of bids in table AuctionJoin == 'MengederBezuege'?
      if self.result[self.row]['MengederBezuege'] == result[0][0]:
        # Return 'MengederBezuege'
        return self.result[self.row]['MengederBezuege']
      
      # We have bids
      else:
        # Return true
        return 1

    # No...
    else:
      # Return false
      return 0

  #
  # Check repeat
  #
  def checkRepeat(self):

    # If 'WiederholungNr' < 'ErlaubteWiederholungen': Set new 'Startdatum', 'Enddatum', 'active'
    if not self.result[self.row]['WiederholungNr'] < self.result[self.row]['ErlaubteWiederholungen']:
      return 0
    
    else:
      # Set 'WiederholungNr' += 1, 'Startdatum' to 'now' + 24h, 'Enddatum' to 'Startdatum' + 'Laufzeit'
      ###startDateNew = self.TIME.calc(timestamp = 1, day = 1)
      startDateNew = self.TIME.calc(timestamp = 1, min = 1)
      endDateNew = startDateNew + self.result[self.row]['Laufzeit']
      repeatTime = self.result[self.row]['WiederholungNr'] + 1
     
      # Log
      self.log.write('REPEATING ID %i IN AuctionBid: WiederholungNr=%i' % (self.result[self.row]['ID'], repeatTime), printLevel = 1)
   
      # Update database
      self.SQLAL.act(action = 'U', table = 'AuctionBid', ID = self.result[self.row]['ID'], WiederholungNr = repeatTime, Startdatum = startDateNew, Enddatum = endDateNew, active = 0)

      # Return true
      return 1

  #
  # Auction or tender
  # Check start status
  #
  def checkStartStatus(self):
  
    # TODO: (check count of entry in AuctionJoin for 'AuktionsID')
    if self.result[self.row]['Enddatum'] > self.timeNow and self.result[self.row]['Startdatum'] < self.timeNow:
      # Set active to 1
      self.SQLAL.act(action = 'U', table = 'AuctionBid', ID = self.result[self.row]['ID'], active = 1)
      # Log
      self.log.write('ACTIVATED ID %i IN AuctionBid: STARTDATE IS %s' % (self.result[self.row]['ID'], self.TIME.unixToLocal(secs = self.result[self.row]['Startdatum'])), printLevel = 1)

  #
  # Check end status
  #
  def checkEndStatus(self):
  
    # Init flag
    c = 0
  
    # Repeat if no one bet
    if not self.checkCountOfBids():
      # Check for repeat, returns false if no more repeats will follow
      c = self.checkRepeat()

      # Tell initiator about restart
      if c:
        # Get email adress of customer
        stmt = 'SELECT Login, Language FROM Kunde WHERE ID = %i' % self.result[self.row]['CustomerID']
        rowCount = self.SQLAL.query(stmt)
        resultCustomer = self.SQLAL.fetchall()

        # Send email template      
        self.sendTmpl(qTemplate = 'email,repeat_123', mainTable = 'AuctionBid', qSuperSearch = 'ID*%i' % self.result[self.row]['ID'], toaddr = resultCustomer[0][0], lang = resultCustomer[0][1])

      # Tell initiator about ending, no one bet
      else:
        # Get email adress of customer
        stmt = 'SELECT Login, Language FROM Kunde WHERE ID = %i' % self.result[self.row]['CustomerID']
        rowCount = self.SQLAL.query(stmt)
        resultCustomer = self.SQLAL.fetchall()
        if rowCount:
          email = resultCustomer[0][0]
          lang = resultCustomer[0][1]
        else:
          email = self.cp.get('email', 'from')
          lang = 'de'

        # Send email template
        self.sendTmpl(qTemplate = 'email,end_empty_123', mainTable = 'AuctionBid', qSuperSearch = 'ID*%i' % self.result[self.row]['ID'], toaddr = resultCustomer[0][0], lang = resultCustomer[0][1])

    # Timed out
    if not c:
      # Disable bid: set AuctionBid.active to 0 and 'beendet' to 1
      self.SQLAL.act(action = 'U', table = 'AuctionBid', ID = self.result[self.row]['ID'], active = 0, beendet = 1)

      # and AuctionJoin.aktiv to 0
      stmt = 'UPDATE AuctionJoin SET active = 0 WHERE AuktionsID = %i' % self.result[self.row]['ID']
      self.SQLAL.query(stmt)

      # Log
      self.log.write('DISABLED ID %i IN AuctioBid: ENDDATE IS %s' % (self.result[self.row]['ID'], self.TIME.unixToLocal(secs = self.result[self.row]['Enddatum'])), printLevel = 1)
  
  #
  # Auction/tender: better bids: inform customer who was outbidden
  #
  def checkOutbids(self):
  
    # Select all running auctions and tenders
    stmt = 'SELECT ID FROM AuctionBid WHERE AuktionsTyp = 1 OR AuktionsTyp = 2 AND active = 1 AND beendet = 0'
    
    # Query database
    rowCountAuctionBid = self.SQLAL.query(stmt)

    # Fetch result
    resultAuctionBid = self.SQLAL.fetchall()
    
    # Process every row
    for rowAuctionBid in range(rowCountAuctionBid):
      # Select all bids to that row
      stmt = 'SELECT ID, CustomerID FROM AuctionJoin WHERE AuktionsID = %i AND status = 2 AND Benachrichtigung = 2 AND informiertUeberboten = 0 ORDER BY Abgabezeitpunkt' % resultAuctionBid[rowAuctionBid][0]
  
      # Query database
      rowCountAuctionJoin = self.SQLAL.query(stmt)
      
      # If no bids in AuctionJoin were found, continue with next row
      if not rowCountAuctionJoin:
        continue
      
      # Log
      ###self.log.write('CHECKING %i BIDS FOR AuctionBid ID %i' % (rowCountAuctionJoin, resultAuctionBid[rowAuctionBid][0]), printLevel = 1)
      
      # Fetch result
      resultAuctionJoin = self.SQLAL.fetchall()
          
      # If we have bids
      for rowAuctionJoin in range(rowCountAuctionJoin):
        # Log
        self.log.write('CHECKING AuctionJoin ID %i FOR OUTBIDS' % resultAuctionJoin[rowAuctionJoin][0], printLevel = 1)
      
        # Get email adress of customer
        stmt = 'SELECT Login, Language FROM Kunde WHERE ID = %i' % resultAuctionJoin[rowAuctionJoin][1]
        self.SQLAL.query(stmt)
        resultCustomer = self.SQLAL.fetchall()
      
        # Send email template      
        self.sendTmpl(qTemplate = 'email,better_bid_123', mainTable = 'AuctionJoin', qSuperSearch = 'ID*%i' % resultAuctionJoin[rowAuctionJoin][0], toaddr = resultCustomer[0][0], lang = resultCustomer[0][1])

        # Update AuctionJoin to indicate that email was sent
        stmt = 'UPDATE AuctionJoin SET informiertUeberboten = %f WHERE ID = %i' % (time.time(), resultAuctionJoin[rowAuctionJoin][0])
        self.SQLAL.query(stmt)

  #
  # Check for winners of an closed auction
  #
  def checkWinners(self):

    # Select all ended auctions
    stmt = 'SELECT ID, AngeboteneMenge, AuktionsTyp, CustomerID, AuktionsTyp, Mindestabnahme FROM AuctionBid WHERE (AuktionsTyp = 1 OR AuktionsTyp = 2) AND active = 0 AND beendet = 1 AND absolutBeendet = 0'
    
    # Query database
    rowCountAuctionBid = self.SQLAL.query(stmt)
    
    # Fetch result
    resultAuctionBid = self.SQLAL.fetchall()
    
    # Process every row
    for rowAuctionBid in range(rowCountAuctionBid):
      # Log
      ###self.log.write('CHECKING CLOSED AUCTION/TENDER AuctionBid ID %i' % resultAuctionBid[rowAuctionBid][0], printLevel = 1)
      
      # Select all bids of auction or tender
      stmt = 'SELECT ID, Menge, CustomerID FROM AuctionJoin WHERE AuktionsID = %i ORDER BY status DESC, Stueckpreis DESC, Menge DESC' % resultAuctionBid[rowAuctionBid][0]
      
      # Query database
      rowCountAuctionJoin = self.SQLAL.query(stmt)
      
      # Log
      ###self.log.write('FOUND %i BIDS FOR AuctionBid ID %i' % (rowCountAuctionJoin, resultAuctionBid[rowAuctionBid][0]), printLevel = 1)
      
      # Check rowCount
      if not rowCountAuctionJoin:
        continue
    
      # Fetch result
      resultAuctionJoin = self.SQLAL.fetchall()
    
      # Init flags
      neueMengeBerechnet = 0
      MengeBerechnet = 0
      
      # Process every row until we have calculated entries so that (almost) 'AngeboteneMenge' is sold
      for rowAuctionJoin in range(rowCountAuctionJoin):
        # Log
        self.log.write('CHECKING AuctionJoin ID %i Menge=%f' % (resultAuctionJoin[rowAuctionJoin][0], resultAuctionJoin[rowAuctionJoin][1]), printLevel = 1)

        # Add 'Menge' to 'MengeBerechnet'
        neueMengeBerechnet = MengeBerechnet + resultAuctionJoin[rowAuctionJoin][1]
        
        # Check, if MengeBerechnet will be greater than 'AngeboteneMenge'
        if resultAuctionBid[rowAuctionBid][4] == 1:
          if neueMengeBerechnet > resultAuctionBid[rowAuctionBid][1]:
            # Log
            self.log.write('REACHED MAXIMAL QTY: %f > %f' % (neueMengeBerechnet, resultAuctionBid[rowAuctionBid][1]), printLevel = 1)

            # Set 'neueMengeBerechnet' to old value
            neueMengeBerechnet = MengeBerechnet
        
            # Continue with next bid from AuctionJoin
            continue

        elif resultAuctionBid[rowAuctionBid][4] == 2:
          if neueMengeBerechnet > resultAuctionBid[rowAuctionBid][5]:
            # Log
            self.log.write('REACHED MAXIMAL QTY: %f > %f' % (neueMengeBerechnet, resultAuctionBid[rowAuctionBid][5]), printLevel = 1)

            # Set 'neueMengeBerechnet' to old value
            neueMengeBerechnet = MengeBerechnet
        
            # Continue with next bid from AuctionJoin
            continue

        # Set 'MengeBerechnet' to neueMengeBerechnet
        MengeBerechnet = neueMengeBerechnet
        
        # Log
        self.log.write('MengeBerechnet=%f/AngeboteneMenge=%f' % (MengeBerechnet, resultAuctionBid[rowAuctionBid][1]), printLevel = 1)

        # Prepare SQL statement
        stmt = 'UPDATE AuctionJoin SET status = 3 WHERE ID = %i' % resultAuctionJoin[rowAuctionJoin][0]
          
        # Query database
        self.SQLAL.query(stmt)
          
        # Log
        self.log.write('SET AuctionJoin ID %i TO status 3' % resultAuctionJoin[rowAuctionJoin][0], printLevel = 1)

        ######### buy_bid ##########
        # Get email adress of customer
        stmt = 'SELECT Login, Language FROM Kunde WHERE ID = %i' % resultAuctionJoin[rowAuctionJoin][2]
        self.SQLAL.query(stmt)
        resultCustomer = self.SQLAL.fetchall()
      
        # Send email template
        if resultAuctionBid[rowAuctionBid][2] == 2:
          self.sendTmpl(qTemplate = 'email,buy_bid_2', mainTable = 'AuctionJoin', qSuperSearch = 'ID*%i' % resultAuctionJoin[rowAuctionJoin][0], toaddr = resultCustomer[0][0], lang = resultCustomer[0][1])
        else:
          self.sendTmpl(qTemplate = 'email,buy_bid_13', mainTable = 'AuctionJoin', qSuperSearch = 'ID*%i' % resultAuctionJoin[rowAuctionJoin][0], toaddr = resultCustomer[0][0], lang = resultCustomer[0][1])

        # Update AuctionJoin to indicate that email was sent
        stmt = 'UPDATE AuctionJoin SET informiertGewonnen = %f WHERE ID = %i' % (time.time(), resultAuctionJoin[rowAuctionJoin][0])
        self.SQLAL.query(stmt)
            
        ######### bid_sold ##########
        # Get email adress of customer
        stmt = 'SELECT Login, Language FROM Kunde WHERE ID = %i' % resultAuctionBid[rowAuctionBid][3]
        self.SQLAL.query(stmt)
        resultCustomer = self.SQLAL.fetchall()
      
        # Send email template      
        if resultAuctionBid[rowAuctionBid][2] == 2:
          self.sendTmpl(qTemplate = 'email,bid_sold_2', mainTable = 'AuctionJoin', qSuperSearch = 'ID*%i' % resultAuctionJoin[rowAuctionJoin][0], toaddr = resultCustomer[0][0], lang = resultCustomer[0][1])
        else:
          self.sendTmpl(qTemplate = 'email,bid_sold_13', mainTable = 'AuctionJoin', qSuperSearch = 'ID*%i' % resultAuctionJoin[rowAuctionJoin][0], toaddr = resultCustomer[0][0], lang = resultCustomer[0][1])

        ######### makemoneyfast ##########
        # Send email template
        self.sendTmpl(qTemplate = 'email,makemoneyfast', mainTable = 'AuctionJoin', qSuperSearch = 'ID*%i' % resultAuctionJoin[rowAuctionJoin][0], toaddr = self.cp.get('email', 'from'))

      # Set absolutBeendet = 1
      stmt = 'UPDATE AuctionBid SET absolutBeendet = 1 WHERE ID = %i' % resultAuctionBid[rowAuctionBid][0]
      self.SQLAL.query(stmt)
      
      # Log
      self.log.write('AuctionBid ID %i IS ABSOLUTELY DISABLED' % resultAuctionBid[rowAuctionBid][0], printLevel = 1)

  #
  # Check for winners of an closed fixed price bid
  #
  def checkFixedPriceWinners(self):
  
    # Select all ended auctions
    stmt = 'SELECT ID, AngeboteneMenge, AuktionsTyp, CustomerID FROM AuctionBid WHERE AuktionsTyp = 3 AND absolutBeendet = 0'
    
    # Query database
    rowCountAuctionBid = self.SQLAL.query(stmt)
    
    # Log
    ###self.log.write('checkFixedPriceWinners(): FOUND %i ENTRIES IN AuctionBid' % rowCountAuctionBid, printLevel = 1)

    # Fetch result
    resultAuctionBid = self.SQLAL.fetchall()
    
    # Process every row
    for rowAuctionBid in range(rowCountAuctionBid):
      # Absolutely disable AuctionBid entry?
      if resultAuctionBid[rowAuctionBid][1] == 0:
        # Set absolutBeendet = 1
        stmt = 'UPDATE AuctionBid SET absolutBeendet = 1 WHERE ID = %i' % resultAuctionBid[rowAuctionBid][0]
        self.SQLAL.query(stmt)
      
        # Log
        self.log.write('checkFixedPriceWinners(): AuctionBid ID %i IS ABSOLUTELY DISABLED' % resultAuctionBid[rowAuctionBid][0], printLevel = 1)

      # Log
      ###self.log.write('checkFixedPriceWinners(): CHECKING CLOSED FIXED PRICE BID AuctionBid ID %i' % resultAuctionBid[rowAuctionBid][0], printLevel = 1)
      
      # Select all bids of auction or tender
      stmt = 'SELECT ID, Menge, CustomerID FROM AuctionJoin WHERE AuktionsID = %i AND informiertGewonnen = 0 ORDER BY status ASC, Stueckpreis ASC, Menge ASC' % resultAuctionBid[rowAuctionBid][0]
      
      # Query database
      rowCountAuctionJoin = self.SQLAL.query(stmt)
      
      # Check rowCount
      if not rowCountAuctionJoin:
        continue
    
      # Log
      ###self.log.write('checkFixedPriceWinners(): FOUND %i BIDS FOR AuctionBid ID %i' % (rowCountAuctionJoin, resultAuctionBid[rowAuctionBid][0]), printLevel = 1)

      # Fetch result
      resultAuctionJoin = self.SQLAL.fetchall()
    
      # Process every row until we have calculated entries so that (almost) 'AngeboteneMenge' is sold
      for rowAuctionJoin in range(rowCountAuctionJoin):
        # Log
        ###self.log.write('checkFixedPriceWinners(): CHECKING AuctionJoin ID %i Menge=%f' % (resultAuctionJoin[rowAuctionJoin][0], resultAuctionJoin[rowAuctionJoin][1]), printLevel = 1)

        ######## buy_bid ########
        # Get email adress of customer
        stmt = 'SELECT Login, Language FROM Kunde WHERE ID = %i' % resultAuctionJoin[rowAuctionJoin][2]
        self.SQLAL.query(stmt)
        resultCustomer = self.SQLAL.fetchall()
      
        # Send email template      
        self.sendTmpl(qTemplate = 'email,buy_bid_13', mainTable = 'AuctionJoin', qSuperSearch = 'ID*%i' % resultAuctionJoin[rowAuctionJoin][0], toaddr = resultCustomer[0][0], lang = resultCustomer[0][1])

        # Update AuctionJoin to indicate that email was sent
        stmt = 'UPDATE AuctionJoin SET informiertGewonnen = %f WHERE ID = %i' % (time.time(), resultAuctionJoin[rowAuctionJoin][0])
        self.SQLAL.query(stmt)
            
        ######## bid_sold ########
        # Get email adress of customer
        stmt = 'SELECT Login, Language FROM Kunde WHERE ID = %i' % resultAuctionBid[rowAuctionBid][3]
        self.SQLAL.query(stmt)
        resultCustomer = self.SQLAL.fetchall()
      
        # Send email template
        self.sendTmpl(qTemplate = 'email,bid_sold_13', mainTable = 'AuctionJoin', qSuperSearch = 'ID*%i' % resultAuctionJoin[rowAuctionJoin][0], toaddr = resultCustomer[0][0], lang = resultCustomer[0][1])

        ######## makemoneyfast ########
        # Send email template      
        self.sendTmpl(qTemplate = 'email,makemoneyfast', mainTable = 'AuctionJoin', qSuperSearch = 'ID*%i' % resultAuctionJoin[rowAuctionJoin][0], toaddr = self.cp.get('email', 'from'))

  #
  # Main entry method
  #
  def check(self):
  
    # Log
    self.log.write('AUCTION CRONJOB: START', printLevel = 1)
  
    # Fields to select from AuctionBid
    fieldsAuctionBid = 'ID, active, Auktionstyp, Subject, CustomerID, Startdatum, Enddatum, Laufzeit, MengederBezuege, WiederholungNr, ErlaubteWiederholungen'

    #
    # START
    #

    # Query for all active entries: 'Startdatum' <= 'now' and 'MengederBezuege' == 0?
    stmt = 'SELECT %s FROM AuctionBid WHERE active = 0 AND beendet = 0 AND Startdatum < %f AND Enddatum > %f' % (fieldsAuctionBid, self.timeNow, self.timeNow)
    rowCount = self.SQLAL.query(stmt)
    result = self.SQLAL.fetchall()
    self.result = self.SQLAL.assignRS_FL(resultSet = result, fieldList = fieldsAuctionBid)
    
    # Process all rows
    for self.row in range(rowCount):
    
      # Start
      self.checkStartStatus()
        
    #
    # END
    #

    # Query for all active entries: 'Enddatum' < 'now'
    stmt = 'SELECT %s FROM AuctionBid WHERE active = 1 AND Enddatum < %f' % (fieldsAuctionBid, self.timeNow)
    rowCount = self.SQLAL.query(stmt)
    result = self.SQLAL.fetchall()
    self.result = self.SQLAL.assignRS_FL(resultSet = result, fieldList = fieldsAuctionBid)
    
    # Process all rows
    for self.row in range(rowCount):
    
      # End
      self.checkEndStatus()

    #
    # OUTBIDS
    #
    self.checkOutbids()

    #
    # WINNERS
    #
    self.checkWinners()
    self.checkFixedPriceWinners()

    # Log
    self.log.write('AUCTION CRONJOB: END', printLevel = 1)

  #
  # Send an email template
  #
  def sendTmpl(self, qTemplate = '', mainTable = '', qSuperSearch = '', toaddr = '', lang = ''):

    # Make instance of dTP
    d = dtp.dTP(lang)
    
    # Process template
    d.dTLtmpl.qVars['qTemplate'] = qTemplate
    d.setMainTable(mainTable)
    d.dTLtmpl.qVars['qSuperSearch'] = qSuperSearch
    d.go(script = 1)
         
    try:
      # Send mail
      self.email.sendFileAsMail(fromaddr = self.cp.get('email', 'from'), toaddr = [toaddr,], file = d.fdoutName)
      # Log
      self.log.write('SENDING EMAIL TEMPLATE (%s)%s TO %s' % (lang, qTemplate, toaddr), printLevel = 1)
      # Remove temporary file
      os.remove(d.fdoutName)

    # Any error      
    except:
      # Log
      self.log.write('ERROR SENDING MAIL: %s(%s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
    
    # Delete instance
    del d

#####################################################################
#
# M A I N
#
#####################################################################
def main():

  # Make instance of class auction
  a = auction()
  
  # Call main method check()
  a.check()

# Called as script
if __name__ == '__main__':

  # Arguments?
  if len(sys.argv) == 2:

    # Print revision and exit
    if sys.argv[1] == 'revision':
      print revision
      sys.exit()

  # Call main()
  main()
