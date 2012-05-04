#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 1.2.7.2 $'
#
##

#
# I M P O R T  M O D U L E S
#
try:
  import sys
  import os
  import string
  import types
  import tempfile
  import time

  import aDataStruct
  import myexcept
  import misc
  import dtp
except:
  print '[dtp: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()

#####################################################################
#
# C L A S S E S
#
#####################################################################

#
# Send emails for questions and answers
#
class QA(aDataStruct.aDS):

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
  # Get login/email from Kunde
  #
  def GetLogin(self, ID):
    pass

  #
  # Get login/email from customer of AuktionBid.ID
  #
  def GetCustomerIDFromAuctionBidID(self, ID):
    pass

  #
  # Look for new questions: frageantwort.Beantwortet == 0
  #
  def LookForNewQuestions(self):
    # Get email from Kunde.email WHERE Kunde.ID = AuctionBid.CustomerID AND AuctionBid.ID == frageantwort.AuktionBidID
    stmt = 'SELECT Kunde.Login, Kunde.Language, frageantwort.ID FROM Kunde, AuctionBid, frageantwort WHERE Kunde.ID = AuctionBid.CustomerID AND AuctionBid.ID = frageantwort.AuktionBidID AND Beantwortet = 0 AND Sendestatus = 0'
    rowCount = self.SQLAL.query(stmt)

    # No rows? Return
    if not rowCount:
      return
    else:
      result = self.SQLAL.fetchall()

    # Process every row
    for row in range(rowCount):
      self.log.write('Q&A: NewQuestion: PROCESSING ID %s FOR %s'
                     % (result[row][2], result[row][0]), printLevel = 1)
      # Send email template
      self.sendTmpl(qTemplate = 'email,question',
                    mainTable = 'frageantwort',
                    qSuperSearch = 'ID*%i' % result[row][2],
                    toaddr = result[row][0], lang = result[row][1])

      # Set 'Sendestatus' to 1
      stmt = 'UPDATE frageantwort SET Sendestatus = 1 WHERE ID = %i' % result[row][2]
      self.SQLAL.query(stmt)

  #
  # Look for new answer: frageantwort.Beantwortet == 1
  #
  def LookForNewAnsweres(self):
    # Get email from Kunde.email WHERE Kunde.ID = AuctionBid.CustomerID AND AuctionBid.ID == frageantwort.AuktionBidID
    stmt = 'SELECT Kunde.Login, Kunde.Language, frageantwort.ID FROM Kunde, frageantwort WHERE Kunde.ID = frageantwort.FragenderID AND Beantwortet = 1 AND Sendestatus = 1'
    rowCount = self.SQLAL.query(stmt)

    # No rows? Return
    if not rowCount:
      return
    else:
      result = self.SQLAL.fetchall()

    # Process every row
    for row in range(rowCount):
      self.log.write('Q&A: NewAnswere: PROCESSING ID %s' % result[row][2], printLevel = 1)
      # Send email template
      self.sendTmpl(qTemplate = 'email,answer',
                    mainTable = 'frageantwort',
                    qSuperSearch = 'ID*%i' % result[row][2],
                    toaddr = result[row][0], lang = result[row][1])

      # Set 'Sendestatus' to 2
      stmt = 'UPDATE frageantwort SET Sendestatus = 2, Beantwortet = 1 WHERE ID = %i' % result[row][2]
      self.SQLAL.query(stmt)

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
      self.log.write('SENDING EMAIL TEMPLATE (%s)%s TO %s, mainTable=%s,qSuperSearch=%s'
                     % (lang, qTemplate, toaddr, mainTable, qSuperSearch), printLevel = 1)
      # Remove temporary file
      os.remove(d.fdoutName)

    # Any error      
    except:
      # Log
      self.log.write('ERROR SENDING MAIL: %s(%s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
    
    # Delete instance
    del d

#
# M A I N
#
if __name__ == '__main__':
  q = QA()
  q.LookForNewQuestions()
  q.LookForNewAnsweres()
