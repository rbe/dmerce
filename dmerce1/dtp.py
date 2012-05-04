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
  import os
  import string
  import time
  import types
  import tempfile
  
  import myexcept
  import misc
  import perms
  import dtl
except:
  print '[dtp: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()

#####################################################################
#
# C L A S S E S
#
#####################################################################

#
# dmerce Template Processor
#
class dTP:

  # Flag: how are we called
  SCRIPT = 0
  WEB = 0

  # Template file descriptor
  tmplfd = 0
  fdoutName = ''

  #
  # Constructor
  #
  def __init__(self, lang = ''):
  
    # Make instance of class misc
    self.misc = misc.MISC()
    
    # Make instance of class Template
    self.dTLtmpl = dtl.Template(lang)

    # Make instance of class perms.database
    self.permsDatabase = perms.userDatabase()
    self.permsTrigger = perms.triggerDatabase()
    
    # Get my PID
    self.PID = os.getpid()

  #
  # Set main table (tn = table name)
  #
  def setMainTable(self, tn):
    self.dTLtmpl.mainTable = tn

  #
  # Compare given fields by form with
  # section for template, option fields
  #
  # Any not given field will be given to
  # database as empty field
  #
  def cmpGivenFields(self):
    
    try:
      # Set tmplIdent      
      tmplIdent = string.replace(self.dTLtmpl.qVars['qTemplate'], ',', '_')

      # Check for section '[tmplIdent]'
      # Get option 'fields' (replace spaces by nothing)
      if self.dTLtmpl.cp.has_section('tmpl2Db'):
        dbFields = string.split(string.replace(self.dTLtmpl.cp.get('tmpl2Db', tmplIdent), ' ', ''), ',')

      # No option/no check
      else:
        return 0
      
      # Log
      ###self.dTLtmpl.log.write('CHECKING FIELDS FROM FORM: TEMPLATE IDENT IS %s' % tmplIdent, printLevel = 1)

      # Go through all database fields
      for dF in dbFields:
        # Check: is dF in cgiFieldStorage?
        if dF not in self.dTLtmpl.cgiFieldStorage.keys():
          # No, add empty key to self.dTLtmpl.cgiFieldStorage
          self.dTLtmpl.cgiFieldStorage[dF] = ''

    # Any error
    except:
      # Log
      self.dTLtmpl.log.write('%s(%s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)

  #
  # Perform action: DELETE
  #
  def logPerformDatabaseActionDelete(self):
    # Log delete
    self.dTLtmpl.log.write('TABLE %s DELETED ID %s' % (self.dTLtmpl.mainTable, self.dTLtmpl.cgiFieldStorage['ID']), printLevel = 1)

  def performDatabaseActionDelete(self):

    # Check rights of user
    if self.dTLtmpl.whoID > 0:
      if not self.can(table = self.dTLtmpl.mainTable, type = 'Aendern'):
        return ''
  
    try:
              
      # Try to update database; keys from form = value of keys from form
      self.dTLtmpl.SQLAL.act(action = 'D', table = self.dTLtmpl.mainTable, ID = self.dTLtmpl.cgiFieldStorage['ID'])
      
      # Log action
      self.logPerformDatabaseActionDelete()

    # Error querying database
    except myexcept.EDatabaseQuery, msg:
      # Log error and return -1
      self.dTLtmpl.log.write('%s(%s) DELETING ENTRY IN DATABASE TABLE %s: %s' % (self.dTLtmpl.mainTable, msg), printLevel = 1)
      return -1
        
    # Any error
    except:
      # Log and return -1
      self.dTLtmpl.log.write('%s(%s) DELETE FAILED' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
      return -1
        
  #
  # Perform action: UPDATE
  #
  def logPerformDatabaseActionUpdate(self):
    # Log update
    self.dTLtmpl.log.write('TABLE %s UPDATED ID %s' % (self.dTLtmpl.mainTable, self.dTLtmpl.cgiFieldStorage['ID']), printLevel = 1)

  def performDatabaseActionUpdate(self):

    # Check rights of user
    if self.dTLtmpl.whoID > 0:
      if not self.can(table = self.dTLtmpl.mainTable, type = 'Aendern'):
        return ''

    # Initial statement for database update
    cmd = "self.dTLtmpl.SQLAL.act(action = 'U', table = self.dTLtmpl.mainTable"

    # Process keys
    for FormKey in self.dTLtmpl.cgiFieldStorage.keys():
      # Try to update database; keys from form = value of keys from form
      cmd = "%s, %s = self.dTLtmpl.SQLAL.check4ValidSQL(self.dTLtmpl.cgiFieldStorage['%s'])" % (cmd, FormKey, FormKey)

    # Close command
    cmd = '%s)' % cmd

    # Try to update database; keys from form = value of keys from form
    try:
        
      # Compile it into object code; Execute it
      py = compile(cmd, '<string>', 'exec')
      exec(py)
      
      # Log action
      self.logPerformDatabaseActionUpdate()

    # Any error
    except:
      # Log error and return -1
      self.dTLtmpl.log.write('%s(%s) UPDATING ENTRY IN DATABASE TABLE %s WITH COMMAND %s' % (sys.exc_info()[0], sys.exc_info()[1], self.dTLtmpl.mainTable, cmd), printLevel = 1)
      return -1

  #
  # Perform action: INSERT
  #
  def logPerformDatabaseActionInsert(self, ID = 0):
    # Log insert
    self.dTLtmpl.log.write('TABLE %s INSERTED ID %s' % (self.dTLtmpl.mainTable, ID), printLevel = 1)


  def performDatabaseActionInsert(self):

    # Check rights of user
    if self.dTLtmpl.whoID > 0:
      if not self.can(table = self.dTLtmpl.mainTable, type = 'Anlegen'):
        return ''

    # If no ID for new entry was given, fetch it
    if not self.dTLtmpl.cgiFieldStorage.has_key('ID'):
      # Get new ID for entry from database
      newID = self.dTLtmpl.SQLAL.getNewID(table = self.dTLtmpl.mainTable)
      # Set $var[InsertedID]$
      self.dTLtmpl.qVars['InsertedID'] = newID
      # Initital command for insert in database
      cmd = "self.dTLtmpl.SQLAL.act(action = 'I', table = self.dTLtmpl.mainTable, ID = newID"
    
    # else we have been given an ID...
    elif self.dTLtmpl.cgiFieldStorage.has_key('ID'):
      # Get it
      newID = self.dTLtmpl.cgiFieldStorage['ID']
      # Set $var[InsertedID]$
      self.dTLtmpl.qVars['InsertedID'] = self.dTLtmpl.cgiFieldStorage['ID']
      # Initital command for insert in database
      cmd = "self.dTLtmpl.SQLAL.act(action = 'I', table = self.dTLtmpl.mainTable"
    
    # Process keys
    for FormKey in self.dTLtmpl.cgiFieldStorage.keys():
      # Try to insert in database; keys from form = value of keys from form
      cmd = "%s, %s = self.dTLtmpl.SQLAL.check4ValidSQL(self.dTLtmpl.cgiFieldStorage['%s'])" % (cmd, FormKey, FormKey)

    # Close command
    cmd = '%s)' % cmd
    
    # Execute SQL query    
    try:
      # Compile it into object code; Execute it
      py = compile(cmd, '<string>', 'exec')
      exec(py)
      # Log action
      self.logPerformDatabaseActionInsert(ID = newID)

    # Any error
    except:
      # Log and return error
      self.dTLtmpl.log.write('%s(%s) CREATING ENTRY IN TABLE %s WITH COMMAND %s' % (self.dTLtmpl.mainTable, sys.exc_info()[0], sys.exc_info()[1], cmd), printLevel = 1)
      return -1

  #
  # Use a trigger
  #
  def trigger(self, which = ''):
  
    # Check rights
    if not self.permsTrigger.can(table = self.dTLtmpl.mainTable):
      # Trigger has no access
      return '4111'

    # Init args, it is optional
    args = ''
    
    try:
      # Split qTrigger by '.'
      package, module, method = string.split(self.dTLtmpl.qVars[which], '.')
      
      # Try to find () in method
      if string.find(method, '(') >= 0:
      
        # Split method([<args>])
        method, args = string.split(method, '(')
        
        # Add '(' (it was removed by string.split)
        args = '(%s' % args

      # Call callPyModule()
      return self.dTLtmpl.callPyModule(package = package, module = module, method = method, args = args)
  
    # Any error
    except:
      # Log and return error
      self.dTLtmpl.log.write('%s(%s) TRIGGERING FUNCTION: %s' % (sys.exc_info()[0], sys.exc_info()[1], self.dTLtmpl.qVars[which]), printLevel = 1)
      return sys.exc_info()[0]

  #
  # Execute a trigger
  #
  def triggerExec(self, which = ''):
  
    # Init flag
    crv = 0

    # Yes, call trigger() and checkRetVal()
    crv = self.checkRetVal(self.trigger(which))
        
    # On successful trigger execution delete 'qTrigger'
    if crv:
      # Log
      self.dTLtmpl.log.write('SUCCESSFUL EXECUTION OF TRIGGER %s' % self.dTLtmpl.qVars[which], printLevel = 1)
      # Delete variable
      del self.dTLtmpl.qVars[which]

    else:
      # Log
      self.dTLtmpl.log.write('UNSUCCESSFUL EXECUTION OF TRIGGER %s' % self.dTLtmpl.qVars[which], printLevel = 1)

      if self.dTLtmpl.qVars.has_key('qErrorTemplate'):
        # Open error template qErrorTemplate
        self.dTLtmpl.qVars['qTemplate'] = self.dTLtmpl.qVars['qErrorTemplate']

      else:
        # Open general error template
        self.dTLtmpl.qVars['qTemplate'] = 'error'
    
  #
  # Perform action on database (after pressing 'qButton')
  #
  def performDatabaseActions(self):
  
    # Delete
    if self.dTLtmpl.cgiFieldStorage.has_key('ID') and self.dTLtmpl.qVars.has_key('qDelete') and not self.dTLtmpl.qVars.has_key('qInsert'):

      # Call performActionDelete()
      if self.performDatabaseActionDelete() == -1:
        # Error, return
        return '1227'
    
    # Update
    if self.dTLtmpl.cgiFieldStorage.has_key('ID') and not self.dTLtmpl.qVars.has_key('qInsert') and not self.dTLtmpl.qVars.has_key('qDelete'):
      # Compare fields from CGI with database fields
      self.cmpGivenFields()

      # Call performActionUpdate()
      if self.performDatabaseActionUpdate() == -1:
        # Error, return
        return '1226'

    # Insert
    if self.dTLtmpl.qVars.has_key('qInsert'):
      # Call performActionInsert()
      if self.performDatabaseActionInsert() == -1:
        # Error, return
        return '1225'

    # Return true
    return 1

  #
  # Send a password
  #
  def sendPasswd(self):
  
    # Use sendPasswd from module login
    import login
    l = login.login()
    
    # Save actual qTemplate, qSuperSearch
    qTemplate = self.dTLtmpl.qVars['qTemplate']
    qSuperSearch = ''
    if self.dTLtmpl.qVars.has_key('qSuperSearch'):
      qSuperSearch = self.dTLtmpl.qVars['qSuperSearch']
    
    # Send password
    l.sendPasswd(qLoginField = 'Login', qLogin = self.dTLtmpl.cgiFieldStorage['Login'])

    # Restore actual qTemplate, qSuperSearch
    self.dTLtmpl.qVars['qTemplate'] = qTemplate
    if qSuperSearch:
      self.dTLtmpl.qVars['qSuperSearch'] = qSuperSearch
    
  #
  # Try to open template
  #
  def openTmpl(self):

    try:
      # Get TFN
      TFN, TFN_error = self.dTLtmpl.filename()
      # Open template for reading
      self.tmplfd = open(TFN, 'r')

    # Error opening template file for reading
    except IOError:
      # Log failed attempt to open template
      self.dTLtmpl.log.write('FAILED TO OPEN TEMPLATE %s' % TFN, printLevel = 1)

      try:
        # Log opening of template
        ###self.dTLtmpl.log.write('OPENING ERROR TEMPLATE %s' % TFN_error, printLevel = 1)
        # Try to open error template
        self.tmplfd = open(TFN_error, 'r')

      # Any error
      except:
        # Log failed attempt to open template and exit
        self.dTLtmpl.log.write('FAILED TO OPEN ERROR TEMPLATE %s' % TFN_error, printLevel = 1)
        sys.exit()

  #
  # Log entry for different sam stages
  #
  def logSamStage(self, stage):
  
    # Init log string for protected templates
    logstr = 'TEMPLATE %s PROTECTED BY SAM' % self.dTLtmpl.qVars['qTemplate']

    # 1
    if stage == 1:
      self.dTLtmpl.log.write('%s, SESSION MUST NOT EXIST' % logstr, printLevel = 1)

    # 2
    elif stage == 2:
      self.dTLtmpl.log.write('%s, SESSION MUST EXIST' % logstr, printLevel = 1)
      
    # 3
    elif stage == 3:
      self.dTLtmpl.log.write('%s, SESSION MUST EXIST AND BE AUTHORIZED' % logstr, printLevel = 1)

    # 4
    elif stage == 4:
      self.dTLtmpl.log.write('%s, SESSION MUST EXIST AND BE AUTHORIZED, CAN ONLY S/I/U/D OWN DATA' % logstr, printLevel = 1)

    # 5
    elif stage == 5:
      self.dTLtmpl.log.write('%s, CAN ONLY BE USED BY JOBS' % logstr, printLevel = 1)

  #
  # Process sam stage if found
  # Set variables and log
  #
  def processSamStage(self, line):

    # If no was discovered before and we are called from the web
    if self.dTLtmpl.samStage == None:

      # Check for SAM stage ... self.regexp.subst(line, regExp.dTL['samStage'], '')
      self.dTLtmpl.samStage, line = self.dTLtmpl.scanSamStage(line)

      # If sam stage was discovered and we are called from the web
      if self.WEB:

        # Log, if sam stage found
        if self.dTLtmpl.samStage > 0:
          self.logSamStage(self.dTLtmpl.samStage)
    
        # Check session depending on value of samstage
        # 1
        if self.dTLtmpl.samStage == 1:
          self.dTLtmpl.checkSession(SessionMustExist = 0, SessionMustBeAuthorized = 0)

        # 2
        elif self.dTLtmpl.samStage == 2:
          self.dTLtmpl.checkSession(SessionMustExist = 1, SessionMustBeAuthorized = 0)

        # 3 or 4
        elif self.dTLtmpl.samStage == 3 or self.dTLtmpl.samStage == 4:
         self.dTLtmpl.checkSession(SessionMustExist = 1, SessionMustBeAuthorized = 1)

        # 5
        elif self.dTLtmpl.samStage == 5:
        
          # If we were not called as a script
          if not self.SCRIPT:
            # Log and raise error
            self.dTLtmpl.log.write('ACCESS VIOLATION!', printLevel = 1)
            raise '1327'

    # Return (unmodified) line
    return line

  #
  # Check retVal
  #
  def checkRetVal(self, retVal):

    # String
    if type(retVal) is types.StringType:
      # Log
      self.dTLtmpl.log.write('GOT ERROR NO. %s' % retVal, printLevel = 1)
      # Set error number in qVars
      self.dTLtmpl.qVars['qErrNo'] = retVal
      # Return false
      return 0
      
    # Integer
    if type(retVal) is types.IntType:
      return 1

  #
  # Perform several actions
  #
  def performActions(self):

    # Init flags
    retVal = '' # Message from function; use an error/another template?
    crv = 1     # Set 'checkReturnValue' to true

    # If 'qButton' is set
    if self.dTLtmpl.qVars.has_key('qButton'):
      # Log
      self.dTLtmpl.log.write('PERFORMING ACTIONS', printLevel = 1)

      # Perform database actions and checkRetVal()
      crv = self.checkRetVal(self.performDatabaseActions())

    # No error open template
    if crv:
      # Send password feature?
      # (must be excuted after performDatabaseActions() because a new user must be inserted into
      # the database first to use the send password feature)
      if self.dTLtmpl.qVars.has_key('qSendPasswd'):
        # Call sendPasswd()
        self.sendPasswd()
    
    # Error open error template
    else:
      if self.dTLtmpl.qVars.has_key('qErrorTemplate'):
        # Open error template qErrorTemplate
        self.dTLtmpl.qVars['qTemplate'] = self.dTLtmpl.qVars['qErrorTemplate']

      else:
        # Open general error template
        self.dTLtmpl.qVars['qTemplate'] = 'error'
    
  #
  # Process template
  #
  def processTemplate(self):

    # Get ignored cgi fields
    self.dTLtmpl.AppendIgnoreCgiFields()
  
    # Init variables
    self.dTLtmpl.samStage = None  # sam stage
    parseBuf = []                 # Buffer to parse (for dTLtmpl.parse())

    # Check for template file descriptor
    if not self.tmplfd:
      self.dTLtmpl.log.write('SORRY: NO TEMPLATE FILE DESCRIPTOR')
      sys.exit()

    # Read file
    fileBuf = self.tmplfd.readlines()

    # Set line counter
    self.dTLtmpl.lineCount = len(fileBuf)
    
    # Log
    ###self.dTLtmpl.log.write('READ %i LINES FROM TEMPLATE' % self.dTLtmpl.lineCount, printLevel = 1)

    # Process every line
    for line in fileBuf:

      try:
        # Scan for sam stage
        line = self.processSamStage(line)

      # Any error
      except:
        # Return
        return
      
      try:
        # Called from the web:
        if self.WEB:
          # Only feed non-empty lines
          if len(line) > 1:
            parseBuf.append(self.dTLtmpl.feed(line))

        # Called as script
        else:
          # Feed line to dTL
          parseBuf.append(self.dTLtmpl.feed(line))

      # Any error
      except SyntaxError:
        # Log
        self.dTLtmpl.log.write('%s(%s) FEEDING LINE TO dTL' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)

    try:

      # Process and output parseBuf
      self.dTLtmpl.parse(parseBuf)

    # IO error
    except IOError, msg:
      # Log
      self.dTLtmpl.log.write('SCRIPT EXECUTION ABORTED: %s' % msg, printLevel = 1)

    # Any error
    except SyntaxError:
      # Log
      self.dTLtmpl.log.write('%s(%s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)

    # Close output file descriptor
    self.dTLtmpl.fdout.close()

    # Close template file descriptor
    self.tmplfd.close()

    # Delete buffers
    del parseBuf
    del fileBuf

  #
  # Things to do if called from the web
  #
  def web(self):

    # Set flag
    self.WEB = 1
    
    # If qVars doesn't have the key qTrigger
    if not self.dTLtmpl.qVars.has_key('qTrigger'):
      # Process form etc.
      self.performActions()

  #
  # Things to do if called as script
  #
  def script(self):
    # Open file descriptor for output
    self.fdoutName = tempfile.mktemp()
    self.dTLtmpl.fdout = open(self.fdoutName, 'w')

  #
  # Go on! Process the template
  #
  def go(self, web = 0, script = 0):

    # Key 'qTemplate' exists?
    if not self.dTLtmpl.qVars.has_key('qTemplate'):
      # Print error message and exit
      print '[dTP: NO TEMPLATE]'
      sys.exit()

    # If mainTable is not set
    if not self.dTLtmpl.mainTable:
      # Set table
      if self.dTLtmpl.qVars.has_key('qTN'):
        # qTN = TableName
        self.setMainTable(self.dTLtmpl.qVars['qTN'])

      else:
        # Name of database table = name of template directory
        self.setMainTable(string.split(self.dTLtmpl.qVars['qTemplate'], ',')[0])

    # Execute trigger
    if self.dTLtmpl.qVars.has_key('qTrigger'):
      self.triggerExec('qTrigger')
      
    # Decide what to do
    if web:
      self.web()
    elif script:
      self.script()

    # Execute after-trigger
    if self.dTLtmpl.qVars.has_key('qTriggerAfter'):
      self.triggerExec('qTriggerAfter')
      
    # Open template
    self.openTmpl()
    
    # Process template
    self.processTemplate()
    
#####################################################################
#
# M A I N
#
#####################################################################
def main():

  # Print HTML header
  print 'Content-type: text/html'
  print

  # Timestamp BEGIN
  t_BEGIN = time.time()

  # Make instance of 'dmerce Template Processor'
  dtp = dTP()
  
  # CALLED FROM THE WEB
  if dtp.misc.env('REMOTE_ADDR'):
    dtp.go(web = 1)
    
  # CALLED AS SCRIPT
  else:
    dtp.go(script = 1)
  
  # Timestamp END
  t_END = time.time()
  
  # Calc differece between BEGIN and END
  t_DIFF = t_END - t_BEGIN

  # Log
  dtp.dTLtmpl.log.write('RUNTIME=%.2f SECONDS' % t_DIFF, printLevel = 1)

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
