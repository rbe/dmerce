#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 1.2.7.1 $'
#
# Revision 1.1  2000-05-11 16:01:47+02  rb
# Initial revision
#
##

#
# I M P O R T  M O D U L E S
#
try:
  import sys
  import string
  import os
  
  import misc
  import perms
  import dtp
except:
  print '[login: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()

#####################################################################
#
# C L A S S E S
#
#####################################################################
class login(perms.login):

  #
  # Constructor
  #
  def __init__(self):
  
    # Call constructor of perms.login
    perms.login.__init__(self)
    
    # Make instance of dtp
    self.dtp = dtp.dTP()

    try:
      # Try to set qLogin
      self.params = self.params + 'qLogin=' + self.qVars['qLogin'] + '&'

    # Any error
    except:
      # Set standard
      self.params = ''

    try:
      # Try to get failURL
      self.failURL = self.cp.get('login', 'failURL')

    # Any error
    except:
      # Log
      self.log.write('SECTION login OPTION failURL NOT SET, USING STANDARD /', printLevel = 1)
      # Set standard
      self.failURL = '/'

    try:
      # Try to get failURL
      self.sendPasswdOKURL = self.cp.get('login', 'sendPasswdOKURL')

    # Any error
    except:
      # Log
      self.log.write('SECTION login OPTION sendPasswdOKURL NOT SET, USING STANDARD /', printLevel = 1)
      # Set standard
      self.sendPasswdOKURL = '/'

    try:
      # Return new URL
      self.sendPasswdErrorURL = self.cp.get('login', 'sendPasswdErrorURL')

    # Any error
    except:
      # Log
      self.log.write('SECTION login OPTION sendPasswdErrorURL NOT SET, USING STANDARD /', printLevel = 1)
      # Set standard
      self.sendPasswdErrorURL = '/'

  #
  # Send password by email
  #
  # qLoginField = Field where login name is stored (email-address)
  # qLogin = The login itself
  #
  def sendPasswd(self, qLoginField = 'Login', qLogin = ''):

    try:
      # Get email address and password from database
      stmt = 'SELECT ID FROM Kunde WHERE %s = "%s"' % (qLoginField, qLogin)
      rowCount = self.SQLAL.query(stmt)

      # Did we receive an result?
      if not rowCount:
        # Go back
        return self.sendPasswdErrorURL

      # Fetch result
      result = self.SQLAL.fetchall()
      
      # Make instance of 'email'
      email = misc.email()
      
    except:
      # Log and return failURL
      self.log.write('%s(%s) CAN\'T SEND PASSWORD BY EMAIL' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
      return self.failURL

    # Process template
    self.dtp.dTLtmpl.qVars['qTemplate'] = 'email,register'
    self.dtp.setMainTable('Kunde')
    self.dtp.dTLtmpl.qVars['qSuperSearch'] = 'ID*%i' % result[0][0]
    self.dtp.go(script = 1)

    try:
      # Send mail
      email.sendFileAsMail(fromaddr = self.cp.get('email', 'from'), toaddr = [qLogin,], file = self.dtp.fdoutName)
      # Log
      self.log.write('SENDING EMAIL TEMPLATE email,register TO %s' % qLogin, printLevel = 1)
      # Remove temporary file
      os.remove(self.dtp.fdoutName)
      # Go to OK URL
      return self.sendPasswdOKURL

    # Any error      
    except:
      # Log
      self.log.write('ERROR SENDING MAIL: %s(%s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
      # Go back
      return self.sendPasswdErrorURL

  #
  # Check login and decide what to do
  #
  def checkLogin(self):
  
    # Check agent or customer
    try:
      # URL to jump to when authentification was done or not
      newURL = ''

      # Init flags
      CA = 0 # Agent
      CC = 0 # Customer

      # Check login and password against database
      CA = self.checkAgent(qLogin = self.qVars['qLogin'], qPasswd = self.qVars['qPasswd'])

      # When agent login was not successful
      if not CA:
        # Check for customer
        CC = self.checkCustomer(qLogin = self.qVars['qLogin'], qPasswd = self.qVars['qPasswd'])

      # If someone has authenticated successful
      if CC or CA:
        # OK
        newURL = self.qVars['qRedirect'] ### + '?qOK=1'

        # Check session -> enable
        cs = self.checkSession()

        # Authenticate session
        if self.qVars.has_key('qLoginField'):
          self.authSession(qLoginField = self.qVars['qLoginField'], qLogin = self.qVars['qLogin'])
        else:
          self.authSession(qLogin = self.qVars['qLogin'])

      # Login failure
      else:
        # Log
        self.log.write('login.checkLogin: INVALID LOGIN %s' % self.qVars['qLogin'], printLevel = 1)
        # Jump to failURL with error flag set; 4 = invalid login
        ###newURL = '%s?%s&%s' % (self.failURL, self.params, 'qErrNo=4')
        newURL = self.failURL

    # Any error
    except SyntaxError:
      # Log
      self.log.write('login.checkLogin: CAN\'T VALIDATE LOGIN %s(%s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
      # Jump to failURL with error flag set; 3 = cannot validate login
      ###newURL = '%s?%s&%s' % (self.failURL, self.params, 'qErrNo=3')
      newURL = self.failURL

    # Return to new URL
    return newURL

  #
  # Decide what do to by looking at form variables
  #
  def decide(self):

    # Should we send the password by email?
    if self.qVars.has_key('qLogin') and self.qVars.has_key('qSendPasswd'):
      newURL = self.sendPasswd(qLoginField = self.qVars['qLoginField'], qLogin = self.qVars['qLogin'])
  
    # Do we have login and password entered?
    elif self.qVars.has_key('qLogin') and self.qVars.has_key('qPasswd'):
      newURL = self.checkLogin()

    # Login and or password were not entered
    else:
    
      # Jump to failURL with error flag set to 2 = login or passwd not entered
      ###newURL = '%s?%s&%s' % (self.failURL, self.params, 'qErrNo=1')
      newURL = self.failURL

    # Log
    self.log.write('login: GOING TO %s' % newURL, printLevel = 1)

    # Use HTML functions
    HTML = misc.HTML()
    HTML.refresh(newURL)

#####################################################################
#
# M A I N
#
#####################################################################
def main():

  # Print HTTP header
  print 'Content-type: text/html'
  print

  # Make instance of class login
  l = login()
  
  # Call main function
  l.decide()

# Called as script
if __name__ == '__main__':

  # Arguments?
  if len(sys.argv) == 2:

    # Print revision and exit
    if sys.argv[1] == 'revision':
      print revision
      sys.exit()

  # Call main
  main()
