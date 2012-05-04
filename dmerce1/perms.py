#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 1.2.7.1 $
#
# Revision 1.1  2000-06-11 16:36:00+02  rb
# Initial revision
#
##

#
# I M P O R T  M O D U L E S
#
try:
  import sys
  import time

  import aDataStruct
except:
  print '[perms: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()

#####################################################################
#
# C L A S S E S
#
#####################################################################

#
# Control login
#
class login(aDataStruct.aDS):

  #
  # Constructor
  #
  def __init__(self):

    # Call constructor of class aDS
    aDataStruct.aDS.__init__(self, CGI = 1)

  #
  # Check given login and password against agent database
  #
  def checkAgent(self, qLogin = '', qPasswd = ''):

    # Check arguments
    if not qLogin or not qPasswd:
      # Return false
      return 0

    try:
      AgentTable = self.cp.get('sam', 'AgentTable')
    except:
      AgentTable = 'Agent'

    # Prepare statement for check
    if self.qVars.has_key('qLoginField'):
      stmt = 'SELECT ID FROM %s WHERE %s = "%s" AND passwd = "%s"' % (AgentTable, self.qVars['qLoginField'], qLogin, qPasswd)
    else:
      stmt = 'SELECT ID FROM %s WHERE Login = "%s" AND passwd = "%s"' % (AgentTable, qLogin, qPasswd)

    try:
      # Query database
      rowCount = self.SQLAL.query(stmt)

    # Any error
    except:
      # Log and return error
      self.log.write('CAN\'T VERIFY AGENT: %s(%s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
      return 0

    # Set string for logging
    logStr = 'AGENT LOGIN %s LOGIN' % qLogin

    # If we got a result
    if rowCount:
      # Log successful login
      self.log.write('%s SUCCESSFUL' % logStr, printLevel = 1)
      # Return OK
      return 1

    # No result
    else:
      # Log unsuccessful login
      self.log.write('%s FAILURE' % logStr, printLevel = 1)
      # Return false
      return 0

  #
  # Check given login and password against customer database
  #
  def checkCustomer(self, qLogin = '', qPasswd = ''):

    # Check arguments
    if not qLogin or not qPasswd:
      # Return false
      return 0

    try:
      CustomerTable = self.cp.get('sam', 'CustomerTable')
    except:
      CustomerTable = 'Kunde'

    # Prepare statement for check
    if self.qVars.has_key('qLoginField'):
      stmt = 'SELECT ID, LoginDisabled FROM %s WHERE %s = "%s" AND passwd = "%s"' % (CustomerTable, self.qVars['qLoginField'], qLogin, qPasswd)
    else:
      stmt = 'SELECT ID, LoginDisabled FROM %s WHERE KDNr = "%s" AND passwd = "%s"' % (CustomerTable, qLogin, qPasswd)

    try:
      # Query database
      rowCount = self.SQLAL.query(stmt)
      # Fetch result
      result = self.SQLAL.fetchall()
      # Check if disabled
      if result[0][1]:
        return 0

    # Any error
    except:
      # Log and return error
      self.log.write('CAN\'T VERIFY CUSTOMER: %s(%s) / %s' % (sys.exc_info()[0], sys.exc_info()[1], stmt), printLevel = 1)
      return 0

    # Set string for logging
    logStr = 'CUSTOMER LOGIN %s LOGIN' % qLogin

    # If we got a result
    if rowCount:
      # Log successful login
      self.log.write('%s SUCCESSFUL' % logStr, printLevel = 1)
      # Return OK
      return 1

    # No result
    else:
      # Log unsuccessful login
      self.log.write('%s FAILURE' % logStr, printLevel = 1)
      # Return false
      return 0

  #
  # May user login at this time?
  #
  def checkTime(self, qLogin = ''):

    # Check arguments
    if not qLogin:
      return 0

    # Get actual hour
    hour = time.localtime(time.time())[3]
    
    # Get times for agent
    count, result = self.SQLAL.search(table = 'AgentTimes', \
      fields = 'Zeiten', \
      AgentID = self.AgentID)
    
    # Find actual hour in allowed hours for agent
    if string.find(hour, result[0]['Zeiten']) >= 0:
      return 1
    else:
      return 0

#
# User-actions on database
#
class userDatabase(aDataStruct.aDS):

  #
  # Constructor
  #
  def __init__(self):

    # Call constructor of class aDS
    aDataStruct.aDS.__init__(self)

  #
  # May user select/insert/update/delete data from/in/to a table?
  #
  def can(self, table, type):
  
    try:

      # Select right from database
      count, result = self.SQLAL.search(table = 'Agent', fields = '%s%s' % (table, type))
    
      if result[0]['%s%s' % (table, type)] == 1:
        return 1
      else:
        return 0

    # Any error
    except:
      self.log.write('userDatabase.can: %s(%s) WITH %s%s' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
      return 0

#
# User-actions on database
#
class triggerDatabase(aDataStruct.aDS):

  #
  # Constructor
  #
  def __init__(self):

    # Call constructor of class aDS
    aDataStruct.aDS.__init__(self)

  #
  # May trigger insert/update/delete data from/in/to a table?
  #
  def can(self, table):
  
    # Look for right in cfg.file
    try:
    
      # Return value from cfg.file section 'trigger', option 'table'
      return self.cp.getint('trigger', table)

    except:
    
      # Log
      self.log.write('%s(%s) TRIGGER HASN\'T ACCESS TO %s' % (sys.exc_info()[0], sys.exc_info()[1], table), printLevel = 1)
      
      # Return false
      return 0
