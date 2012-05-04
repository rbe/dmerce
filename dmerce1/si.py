#!/usr/bin/env python
##
#
# $Author: rb $
revision = "$Revision: 1.2.7.1 $"
#
# Revision 1.1  2000-05-11 14:29:56+02  rb
# Initial revision
#
##

#####################################################################
#
# Provides access to:
# - Log file
# - Configuration file -> cp
# - Data about session -> SID
# - Logged in people (agent or customer)
#
#####################################################################

#
# I M P O R T  M O D U L E S
#
try:
  import sys
  import os
  import ConfigParser

  import myexcept
  import misc
  import log
  import sqlal
  import sam
except SyntaxError:
  print '[si: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()

#####################################################################
#
# C L A S S E S
#
#####################################################################

#
# Standard Interface
#
class SI:

  # G L O B A L - N A M E S P A C E
  #
  # self.GLOBAL      - Dictionary - variables which should be accessible
  #                    by all modules/functions
  GLOBAL = {}
  
  # Init session ID with 0
  SID = 0
  
  # Init variables for agent/customer
  whoID = 0
  whoType = 'NOBODY'
  whoData = {}

  #
  # Constructor
  #
  def __init__(self, UseSAM = 1, SessionMustExist = 0, SessionMustBeAuthorized = 0):
          
    # Make instances
    self.misc = misc.MISC()                              # Miscellanous functions
    self.log = log.LOGGER(fn = '/tmp/dmerce-1.2.6a.log') # Logger
    self.HTML = misc.HTML()                              # HTML functions

    try:
      # Parse configuration-file
      self.parseConfig()

      # Call constructor of SQLAL object
      # Use values from configuration file
      self.SQLAL = sqlal.SQLAL(dbtype = self.cp.get('sql', 'dbtype'), \
        host = self.cp.get('sql', 'host'), \
        db = self.cp.get('sql', 'database'), \
        user = self.cp.get('sql', 'user'), \
        passwd = self.cp.get('sql', 'passwd'))
      
    # Error in configfile: No such section
    except ConfigParser.NoSectionError, msg:
      # Log error
      self.log.write('si: NO SUCH SECTION %s IN CONFIGURATION FILE %s' % (msg, self.cpfn), printLevel = 1)
      # Exit
      sys.exit()

    # Error in configfile: No such option
    except ConfigParser.NoOptionError, msg:
      # Log error
      self.log.write('si: NO SUCH OPTION %s IN CONFIGURATION FILE %s' % (msg, self.cpfn), printLevel = 1)
      # Exit
      sys.exit()

    # Any other error
    except NameError:
      # Log error
      self.log.write('si: ERROR: (%s, %s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
      # Exit
      sys.exit()

    # Check local host license
    self.checkLocalLicense()

    # Use SAM?
    if UseSAM:
      # Make instance of class SAM
      # Use values from configuration file
      self.SAM = sam.SAM(dbtype = self.cp.get('sql', 'dbtype'), \
        host = self.cp.get('sql', 'host'), \
        db = self.cp.get('sql', 'database'), \
        user = self.cp.get('sql', 'user'), \
        passwd = self.cp.get('sql', 'passwd'))

      # Check session
      self.checkSession(SessionMustExist = SessionMustExist, SessionMustBeAuthorized = SessionMustBeAuthorized)

    # Log ID og object
    ###self.log.write('SI.__init__: %i' % id(self), printLevel = 1)

  #####################################################################
  #
  # M E T H O D S
  #
  #####################################################################

  #
  # Check for valid license
  #
  def checkLocalLicense(self):

    # Stat /etc/hostname
    t = os.stat('/etc/hostname')

    # Calculate = Size of file * 2009
    check = t[6] * 2009

    # If calculated value 'check' is not the same
    # as in configuration file section 'q' value 's'
    if int(check) != int(self.cp.get('q', 's')):
      # Log and print error and exit
      self.log.write('si: LOCAL LICENSE NOT VALID: You are not authorized to use this license', printLevel = 2)
      sys.exit()

  #
  # Authorize session
  #
  def authSession(self, qLoginField = 'Login', qLogin = ''):
    self.SAM.auth(qLoginField = qLoginField, qLogin = qLogin)

  #
  # Check session
  #
  def checkSession(self, SessionMustExist = 0, SessionMustBeAuthorized = 0):
    # Make instance of class SAM
    # Use values from configuration file
    self.SAM = sam.SAM(dbtype = self.cp.get('sql', 'dbtype'), \
      host = self.cp.get('sql', 'host'), \
      db = self.cp.get('sql', 'database'), \
      user = self.cp.get('sql', 'user'), \
      passwd = self.cp.get('sql', 'passwd'))

    # Set SessionMustExist for SAM  
    self.SAM.SessionMustExist = SessionMustExist
    
    # Set SessionMustBeAuthorized for SAM
    self.SAM.SessionMustBeAuthorized = SessionMustBeAuthorized

    # Get SID
    self.SID = self.SAM.check()

    # Check session
    if not self.SID:

      try:
        # Try to get [sam]: failURL
        newURL = self.cp.get('sam', 'failURL')

      # Any error
      except:
        # Set standard
        newURL = '/'

      # Log
      self.log.write('si: SESSION NOT VALID (SID=%s), GOING TO %s' % (self.SID, newURL), printLevel = 1)

      # Jump to 'newURL'
      self.HTML.refresh(newURL)

      # Exit
      sys.exit()

    # Get ID of active agent or customer
    self.whoType, self.whoID = self.SAM.isAuth()
    
    # Return true(!, because session is valid), if no sufficient data was returned
    if not self.whoType or not self.whoID:
      return 1
    
    try:

      AgentTable = 'Agent'
      CustomerTable = 'Kunde'

      # If agent has logged in, get data
      if self.whoType is 'Agent':
        try:
          AgentTable = self.cp.get('sam', 'AgentTable')
        except:
          AgentTable = 'Agent'
          
        # Get data of agent from table Agent
        count, result = self.SQLAL.search(table = AgentTable, fields = self.cp.get('who', AgentTable), ID = self.whoID)

      # If customer has logged in, get data
      elif self.whoType is 'Customer':
        try:
          CustomerTable = self.cp.get('sam', 'CustomerTable')
        except:
          CustomerTable = 'Kunde'

        # Get data of customer from table Kunde
        count, result = self.SQLAL.search(table = CustomerTable, fields = self.cp.get('who', CustomerTable), ID = self.whoID)

      # Put all data fields in GLOBAL
      for ResultKey in result[0].keys():
        self.whoData[ResultKey] = result[0][ResultKey]

    # ConfigParser.NoSectionError
    except ConfigParser.NoSectionError:
      pass

    # ConfigParser.NoOptionError
    except ConfigParser.NoOptionError:
      pass
    
    # Any error
    except:
      # Log
      self.log.write('si: ERROR GETTING DATA FOR %s: (%s, %s)' % (self.whoType, sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)

    # Return
    return 1

  #
  # Open and parse configuration file
  #
  def parseConfig(self, cfgfile = 'dmerce.cfg'):
  
    # Init cpfn
    self.cpfn = ''
  
    # Parse configuration-file:
    # 1. in local directory
    # 2. <DOCUMENT_ROOT>/dmerce.cfg
    try:

      # Use file decriptor cpfn (= ConfigParser_FileName)
      tryFN = ['./%s' % cfgfile, '%s/%s' % (self.misc.env('DOCUMENT_ROOT'), cfgfile), '']

      # Go through list
      for cpfn in tryFN:
      
        # Try to access cfg.file
        try:

          # Try to access 'cfg'
          fd = open(cpfn, 'r')
          fd.close()
          
          self.cpfn = cpfn
          
          # Break
          break

        except:
          pass

      if not self.cpfn:
        raise IOError, 'NO CONFIGURATION FILE FOUND'

      # Make instance of ConfigParser
      self.cp = ConfigParser.ConfigParser()

      # Read it
      self.cp.read(cpfn)

      # Return ConfigParser.ConfigParser object
      return 1

    # Any error
    except:
      
      # Log error
      self.log.write('si: %s(%s) OPENING OR PARSING CONFIGURATION FILE %s' % (sys.exc_info()[0], sys.exc_info()[1], self.cpfn), printLevel = 1)

      # Return
      raise sys.exc_info()[0], sys.exc_info()[1]

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
