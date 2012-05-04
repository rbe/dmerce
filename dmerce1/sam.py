#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 1.2.7.1 $'
#
# Revision 1.1  2000-05-11 15:02:25+02  rb
# Initial revision
#
##

#
# I M P O R T  M O D U L E S
#
try:
  import sys
  import string
  import time
  import ConfigParser
  
  import myexcept
  import misc
  import log
  import sqlal
except:
  print '[sam: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()

#####################################################################
#
# C L A S S E S
#
#####################################################################

#
# _S_ecure _A_rea _M_anagement
#
class SAM(sqlal.SQLAL):

  # Maximum session length in seconds; 0 disables checking
  # Time of inactive in seconds; 0 disables checking
  # timeMaximum: 3600 (1 hour) * hours = time a user may work in the system
  # timeInactive: 3600 (1 hour) / x = time a user may be inactive
  timeMaximum = 3600 * 4
  timeInactive = 3600 / 4

  # Session flags
  SessionMustExist = 0          # SID must exist a valid SID must exist

  # Authentification flags
  SessionMustBeAuthorized = 0   # Must session be authorized?

  #
  # Constructor
  #
  def __init__(self, **SQLALcd):
    
    # Make instance
    self.misc = misc.MISC()
    self.log = log.LOGGER(fn = '/tmp/dmerce-SAM.log')

    try:
      # Parse configuration-file
      self.parseConfig()

    # Error in configfile: No such section
    except ConfigParser.NoSectionError, msg:
      # Log error
      self.log.write('SAM: NO SUCH SECTION %s IN CONFIGURATION FILE %s' % (msg, self.cpfn), printLevel = 1)
      # Exit
      sys.exit()

    # Error in configfile: No such option
    except ConfigParser.NoOptionError, msg:
      # Log error
      self.log.write('SAM: NO SUCH OPTION %s IN CONFIGURATION FILE %s' % (msg, self.cpfn), printLevel = 1)
      # Exit
      sys.exit()

    # Any other error
    except NameError:
      # Log error
      self.log.write('SAM: ERROR: (%s, %s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
      # Exit
      sys.exit()

    # Make instance of sqlal
    try:

      # Make instance of SQL object
      # Create database connection
      sqlal.SQLAL.__init__(self, dbtype = SQLALcd['dbtype'], \
        host = SQLALcd['host'], \
        db = SQLALcd['db'], \
        user = SQLALcd['user'], \
        passwd = SQLALcd['passwd'])
     
    # Any other error
    except:
      print '[sam: ERROR USING SQLAL: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
      sys.exit()

    # Get enviroment variables for info about client
    self.remote_addr = self.misc.env('REMOTE_ADDR')
    self.httpxforwardedfor = self.misc.env('HTTP_X_FORWARDED_FOR')
    self.httphost = self.misc.env('HTTP_HOST')
    
    # Get SID
    self.SID = self.getSID()

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
      self.log.write('SAM: %s(%s) OPENING OR PARSING CONFIGURATION FILE %s' % (sys.exc_info()[0], sys.exc_info()[1], self.cpfn), printLevel = 1)
      # Return
      raise sys.exc_info()[0], sys.exc_info()[1]

  #
  # Create database table
  # Try to drop and then to create table; wrapper to drop/create
  #
  def createtable(self):
    
    # Try to drop table
    try:
      self.droptable()

    # Pass, because table musn't exist
    except myexcept.EDatabaseDropTable, msg:
      pass

    # Try to create table
    try:
      stmt = 'CREATE TABLE sam' \
             '(' \
             '  session_id int NOT NULL auto_increment,' \
             '  first_time datetime NOT NULL float(12,6),' \
             '  last_time datetime NOT NULL float(12,6),' \
             '  remote_addr varchar(15) NOT NULL DEFAULT "0.0.0.0",' \
             '  httpxforwardedfor varchar(15) NOT NULL DEFAULT "0.0.0.0",' \
             '  httphost varchar(255) NOT NULL DEFAULT "0.0.0.0",' \
             '  disabled int NOT NULL DEFAULT 0,' \
             '  disabled_time datetime NOT NULL DEFAULT "0000-00-00 00:00:00",' \
             '  auth int NOT NULL DEFAULT 0,' \
             '  agent_id int NOT NULL DEFAULT "0",' \
             '  data text NOT NULL DEFAULT "", PRIMARY KEY(session_id)' \
             ')'

      # Execute SQL statement
      self.query(stmt)

    # Error with database
    except myexcept.EDatabaseQuery, msg:
      raise myexcept.EDatabaseCreateTable, msg

  #
  # Drop database table
  #
  def droptable(self):
          
    # Create statement
    stmt = 'DROP TABLE sam'
    
    # Try to execute statement
    try:
      self.query(stmt)
      
    # Error dropping table: does not exist
    except myexcept.EDatabaseQuery, msg:
      print myexcept.EDatabaseDropTable, msg

  #
  # Get SID from database for active session of client
  #
  def getSID(self):

    try:

      # Check database
      rowCount = self.query('SELECT session_id' \
        ' FROM sam' \
        ' WHERE remote_addr = "%s"' \
        ' AND httpxforwardedfor = "%s"' \
        ' AND httphost = "%s"' \
        ' AND disabled = 0' \
        % (self.remote_addr, self.httpxforwardedfor, self.httphost))

      try:

        # Get SID from result
        if rowCount > 0:

          # Fetch result
          result = self.fetchall()
          self.log.write('CLIENT %s GOT SID' % self.remote_addr, 1, result)
          return result[0][0]

      # Any error -> 'No valid session'
      except:
        self.log.write('GOT ERROR %s %s' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)

      # Any error or no positive row count -> 'No valid session'
      return 0
      
    # Error dealing with database
    except myexcept.EDatabaseQuery, msg:
      return 0

  #
  # Enable, create session in database
  #
  def enable(self):

    # Only enable, if
    #   - No session must exist
    #   or
    #   - Session must not be authorized
    if self.SessionMustExist != 0 or self.SessionMustBeAuthorized != 0:
      return 0

    # Try to insert new session in database
    try:
      
      # SID is generated by MySQL auto_increment feature
      self.query('INSERT INTO sam' \
        ' (first_time, last_time, remote_addr, httpxforwardedfor, httphost, disabled)' \
        ' VALUES' \
        ' (%.6f, %.6f, "%s", "%s", "%s", 0)' \
        % (time.time(), time.time(), self.remote_addr, self.httpxforwardedfor, self.httphost))

      # Get SID
      self.SID = self.getSID()
      
      # Log
      self.log.write('SID %s: ACTIVATED NEW SESSION FOR CLIENT %s ON %s' % (self.SID, self.remote_addr, self.httphost), printLevel = 1)
      
      # Return it
      return self.SID

    # Error dealing with database
    except myexcept.EDatabaseQuery, msg:
      return 0

  #
  # Disable, delete session
  #
  def disable(self):

    try:

      # Set disabled flag in database for session for all sessions/ip addresses of client
      # Don't use "'session_id = %s AND disabled = 0' % sid" to disable all sessions for client
      self.query('UPDATE sam' \
        ' SET disabled = 1, disabled_time = %f' \
        ' WHERE remote_addr = "%s" AND httphost = "%s"' % (time.time(), self.remote_addr, self.httphost))

      # Log
      self.log.write('SID %s: DISABLED FOR CLIENT %s ON %s' % (self.SID, self.remote_addr, self.httphost), printLevel = 1)

    # Error with database
    except myexcept.EDatabaseQuery, msg:
      return 0
      
    # Successfully disabled
    return 1

  #
  # Is session disabled?
  #
  def isDisabled(self, SID = 0):
  
    # Check SID
    if not SID:
      return 1

    # Get disabled flag for session ID SID
    rowCount = self.SQLAL.query('SELECT disabled FROM sam WHERE session_id = %i' % SID)
    if rowCount:
      result = self.SQLAL.fetchall()
      return result[0][0]
    else:
      return 1

  #
  # Set auth flag in active session
  #
  def setAuth(self):

    # Set auth field in database to 1 and agent_id
    stmt = 'UPDATE sam' \
        ' SET auth = 1' \
        ' WHERE session_id = "%s"' \
        ' AND disabled = 0' % self.SID

    try:

      # Query database
      self.query(stmt)
      
      # Return success
      return 1

    except:

      # Log
      self.log.write('SAM: CAN\'T AUTHORIZE SESSION ID %i: (%s, %s)' % (self.SID, sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)

      # Return false
      return 0

  #
  # Authenticate session
  # force is used to auth a session without login
  #
  def auth(self, qLoginField = 'Login', qLogin = ''):
  
    self.log.write('TRYING TO AUTHENTICATE SESSION: qLoginField=%s, qLogin=%s' % (qLoginField, qLogin), printLevel = 1)

    # If there is no active session for client
    if not self.SID or not qLoginField or not qLogin:
      # Log
      self.log.write('SAM: MISSING ARGUMENTS FOR AUTHORIZING SESSION: SID %i, qLoginField %s, qLogin %s' % (self.SID, qLoginField, qLogin), printLevel = 1)
      # Return
      return 0

    try:

      try:
        AgentTable = self.cp.get('sam', 'AgentTable')
      except:
        AgentTable = 'Agent'

      # Get ID for agent with qLogin
      stmt = 'SELECT ID FROM %s WHERE %s = "%s"' % (AgentTable, qLoginField, qLogin)
      rowCount = self.query(stmt)
      result = self.fetchall()

      # Set agent_id
      stmt = 'UPDATE sam' \
        ' SET agent_id = %i' \
        ' WHERE session_id = "%s"' \
        ' AND disabled = 0' % (result[0][0], self.SID)

      # Query database
      self.query(stmt)
      
      # Set auth flag
      self.setAuth()
        
      # Return success
      return 1

    # Any error
    except:
      # Log
      self.log.write('SAM: ERROR AUTHORIZING SESSION FOR AGENT: (%s, %s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)

    #
    # No agent found, try customer
    #
    try:

      try:
        CustomerTable = self.cp.get('sam', 'CustomerTable')
      except:
        CustomerTable = 'Kunde'

      # Get ID for customer with qLogin
      stmt = 'SELECT ID FROM %s WHERE %s = "%s"' % (CustomerTable, qLoginField, qLogin)
      rowCount = self.query(stmt)
      result = self.fetchall()

      # Set auth field in database to 1 and agent_id
      stmt = 'UPDATE sam' \
        ' SET auth = 1, customer_id = %i' \
        ' WHERE session_id = %s' \
        ' AND httphost = "%s"' \
        ' AND disabled = 0' % (result[0][0], self.SID, self.httphost)

      # Query database
      self.query(stmt)

      # Return success
      return 1

    # Any error
    except:
      # Log
      self.log.write('SAM: ERROR AUTHORIZING SESSION FOR CUSTOMER: (%s, %s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)

    # No agent or customer could be authorized
    return 0

  #
  # Is session authenticated?
  #
  def isAuth(self):

    # If there is no active session for client
    if self.SID == 0:
      return 0

    try:

      # Get auth field from database
      rowCount = self.query('SELECT agent_id, customer_id FROM sam WHERE session_id = "%s" AND httphost = "%s" AND auth = 1 AND disabled = 0' % (self.SID, self.httphost))

      # Fetch result
      result = self.fetchall()
      
      # Is result OK?
      if rowCount > 0:
      
        # Return AgentID
        if result[0][0] > 0:
          return 'Agent', result[0][0]

        # Return CustomerID
        elif result[0][1] > 0:
          return 'Customer', result[0][1]
        
      # No result, return 0: not authorized
      else:

        # Return false
        return 'NOBODY', 0

    # Any error
    except:
    
      # Log
      self.log.write('SAM: %s(%s) CAN\'T VERIFY AUTHENTIFICATION IN SESSION' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)

      # Return false
      return 0, 0

  #
  # Check for an active session
  #
  def check(self):
  
    # Check required variables
    if not self.remote_addr:
      self.log.write('SAM: NO REMOTE ADDR!', printLevel = 1)
      return 0

    # Log
    self.log.write('SAM: CHECKING CLIENT %s ON %s' % (self.remote_addr, self.httphost), printLevel = 1)

    # Session active?
    if self.SID:
    
      # Log
      self.log.write('SAM: ALREADY HAVE ACTIVE SESSION ID %i FOR CLIENT %s ON %s' % (self.SID, self.remote_addr, self.httphost), printLevel = 1)

      # Check if session must be authorized
      if self.SessionMustBeAuthorized:
      
        # Log
        self.log.write('SAM: SESSION MUST BE AUTHORIZED FOR CLIENT %s ON %s' % (self.remote_addr, self.httphost), printLevel = 1)

        # Session not authorized?
        whoType, whoID = self.isAuth()
        if not whoID:

          # Log
          self.log.write('SAM: REQUESTED BUT FAILED: NO AUTHORIZED SESSION FOR CLIENT %s ON %s' % (self.remote_addr, self.httphost), printLevel = 1)

          # Return 0 (= error)
          return 0
    
      # Check timelimit
      # OK
      if self.timelimit():

        # Refresh session
        if self.refresh():

          # Log
          ###self.log.write('SAM: REFRESHING SESSION FOR CLIENT %s ON %s' % (self.remote_addr, self.httphost), printLevel = 1)

          # Return SID
          return self.SID

        else:

          # Log
          self.log.write('SAM: REQUESTED BUT FAILED: REFRESH SESSION FOR CLIENT %s ON %s' % (self.remote_addr, self.httphost), printLevel = 1)

          # Return 0 (= error)
          return 0

      # Timelimit not ok
      else:

        # Disable session
        self.disable()

        # Should new session be enabled (without auth?)
        if not self.SessionMustExist and not self.SessionMustBeAuthorized:

          # SID = Enable and get new
          return self.enable()

        # No
        else:

          # SID = 0
          return 0

    # No session active
    else:

      # Try to enable session
      # enable() checks if this is allowed or not
      return self.enable()

  #
  # Check session for time limit
  #
  def timelimit(self):

    # Return 1 (OK) if no checking should be made
    if self.timeMaximum <= 0 and self.timeInactive <= 0:
      return 1

    try:

      # Get timestamps from database
      count, result = self.search(table = 'sam', fields = 'first_time, last_time', session_id = self.SID, orderby = '')

      # Put result in variables
      if not count:

        # We got an empty result from database
        return 0

    # Error with database
    except myexcept.EDatabaseQuery, msg:
      return 0

    # Check for maximum session time, if enabled
    if self.timeMaximum > 0:

      # Calculate difference between last_time and first_time in seconds
      delta_maximum = result[0]['last_time'] - result[0]['first_time']

      # Is maximum session time exceeded?
      if delta_maximum > self.timeMaximum:
              
        # Log
        self.log.write('SID %s: MAXIMUM TIMEOUT REACHED (%.2f OF %.2f max) FOR CLIENT %s ON %s' % (self.SID, delta_maximum, self.timeMaximum, self.remote_addr, self.httphost), printLevel = 1)

        # Return
        return 0

    # Check for inactive session time, if enabled
    if self.timeInactive > 0:

      # Calculate difference between actual time and last_time in seconds
      actual_time = time.time()
      delta_inactive = actual_time - result[0]['last_time']

      # Is inactive session time exceeded?
      if delta_inactive > self.timeInactive:

        # Log
        self.log.write('SID %s: INACTIVE TIMEOUT REACHED (%.2f OF %.2f max) FOR CLIENT %s ON %s' % (self.SID, delta_inactive, self.timeInactive, self.remote_addr, self.httphost), printLevel = 1)
              
        # Return
        return 0
              
    # Else everything is alright
    return 1

  #
  # Refresh times of an open session
  # Set last_time to actual time and date
  #
  def refresh(self):

    try:

      # Update timestamp in database for last access time
      self.query('UPDATE sam' \
        ' SET last_time = %.6f' \
        ' WHERE session_id = "%s"' \
        ' AND httphost = "%s"' \
        ' AND disabled = 0' % (time.time(), self.SID, self.httphost))
      
    # Error with database
    except myexcept.EDatabaseQuery, msg:
      return 0
    
    return 1

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
