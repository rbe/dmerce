#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 1.2.7.1 $
#
# Revision 1.1  2000-08-30 18:05:24+02  rb
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

class merchantCronJob(aDataStruct.aDS):

  #
  # Constructor
  #
  def __init__(self):
  
    # Set actual time
    self.timeNow = time.time()
  
    # Call constructor of class aDS
    aDataStruct.aDS.__init__(self)

  #
  # Check for new orders and send email
  #
  def checkNewOrder(self):
  
    # Prepeare SQL statement
    stmt = 'SELECT BasketID, TypeDate, whoID, whoType, ordererEMail FROM Baskets WHERE (whoType = "Customer" OR whoType = "NOBODY") AND Type = 1 AND closed = 1 AND sentOrderNotify = 0'
    
    # Query database
    rowCount = self.SQLAL.query(stmt)
    
    # Log
    self.log.write('merchantCronJob: FOUND %i NEW ORDERS' % rowCount, printLevel = 1)
  
    if rowCount:

      # Fetch result
      result = self.SQLAL.fetchall()

    else:
      
      # Return
      return 0

    # Set template    
    template = 'email,order'

    # Process every row
    for row in range(rowCount):

      lang = ''

      # Init toaddr
      toaddr = [self.cp.get('Basket', 'to'),]

      # An order of a customer?
      if result[row][3] == 'Customer':

        # Get email address of customer
        stmt = 'SELECT KDmail1, Language FROM Kunde WHERE ID = %i' % result[row][2]
        rowCount = self.SQLAL.query(stmt)
        if rowCount:
          resultCustomer = self.SQLAL.fetchall()
          toaddr.append(resultCustomer[0][0])
          lang = resultCustomer[0][1]

      elif result[0][4]:
        toaddr.append(result[0][4])

      # Send template: mainTable = Warenkorb, qSuperSearch ID*Warenkorb.ID
      self.sendTmpl(qTemplate = template, mainTable = 'Baskets', qSuperSearch = 'BasketID*%i' % result[row][0], toaddr = toaddr, lang = lang)
      
      # Update database
      stmt = 'UPDATE Baskets SET sentOrderNotify = %.6f WHERE BasketID = %i' % (time.time(), result[row][0])
      self.SQLAL.query(stmt)

  #
  # Send an email template
  #
  def sendTmpl(self, qTemplate = '', mainTable = '', qSuperSearch = '', toaddr = [], lang = ''):

    # Make instance of email
    mail = misc.email()

    # Make instance of dTP
    d = dtp.dTP(lang)
    
    d.dTLtmpl.resetLoadedModules()
    
    # Process template
    d.dTLtmpl.qVars['qTemplate'] = qTemplate
    d.setMainTable(mainTable)
    d.dTLtmpl.qVars['qSuperSearch'] = qSuperSearch
    d.go(script = 1)

    try:

      # Send mail
      mail.sendFileAsMail(fromaddr = self.cp.get('Basket', 'from'), toaddr = toaddr, file = d.fdoutName)

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

  #
  # Main entry method
  #
  def main(self):

    # Log
    self.log.write('merchantCronJob: START', printLevel = 1)
    
    # Call checkNewOrder()
    self.checkNewOrder()
  
    # Log
    self.log.write('merchantCronJob: END', printLevel = 1)

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
  
  else:
    m = merchantCronJob()
    m.main()
