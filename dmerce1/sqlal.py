#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 1.2.7.1 $'
#
# Revision 1.1  2000-05-11 15:04:06+02  rb
# Initial revision
#
##

#
# I M P O R T  M O D U L E S
#
try:
  import sys
  import string
  import types

  import myexcept
  import log
except:
  print '[sqlal: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()

#####################################################################
#
# C L A S S E S
#
#####################################################################

#
# SQL Abstraction Layer
#
class SQLAL:

  # Debug?
  debug = 0

  #
  # Constructor
  #
  # Parameters for connection
  # (SQLALcd  = SQL Abstraction Layer connection dictionary)
  #
  def __init__(self, **SQLALcd):

    # Make instances
    self.sqllog = log.LOGGER(fn = '/tmp/dmerce_SQLAL.log')

    # Check for values
    if not SQLALcd.has_key('dbtype') \
       or not SQLALcd.has_key('host') \
       or not SQLALcd.has_key('db') \
       or not SQLALcd.has_key('user') \
       or not SQLALcd.has_key('passwd'):

      # Print error message and exit
      self.sqllog.write('MISSING ARGUMENTS FOR SQL CONNECTION', 1, SQLALcd.keys())
      sys.exit()

    # Assign variable dbtype
    self.dbtype = SQLALcd['dbtype']
    self.dbtype = 'mysql'
    
    # MySQL
    if self.dbtype is 'mysql':

      # Import module MySQLdb
      try:
        import MySQLdb

      # ImportError
      except ImportError:
        self.sqllog.write('CAN\'T LOCATE MODULE \'MySQLdb\'', printLevel = 1)
        sys.exit()

      try:

        # Connection
        self.connection = MySQLdb.connect(host = SQLALcd['host'], \
          db = SQLALcd['db'], \
          user = SQLALcd['user'], \
          passwd = SQLALcd['passwd'])

      # MySQL Error
      except MySQLdb.Error, msg:
      
        # Log and print error
        self.sqllog.write('ERROR CONNECTING DATABASE: \'%s\'' % myexcept.EDatabaseConnection, printLevel = 1)

      try:

        # Cursor
        self.cursor = self.connection.cursor()

      # MySQL Error
      except MySQLdb.Error, msg:

        # Log and print error
        self.sqllog.write('ERROR USING DATABASE: \'%s\'' % myexcept.EDatabaseCursor, printLevel = 1)

      # Log connection
      ###self.sqllog.write('ESTABLISHED DATABASE CONNECTION', printLevel = 1)

    # Log ID of object
    ###self.sqllog.write('SQLAL.__init__: %i' % id(self), printLevel = 1)
  
  #
  # Check string an escape characters which can be dangerous
  # Check string 's' and return SQL secure string
  #
  # We don't escape ' because all strings in our sql statements
  # are enclosed by "
  #
  def check4ValidSQL(self, s):

    # List of characters to escape in SQL syntax
    SQLChars = ('"',)

    # Escape them
    for ToEscape in SQLChars:
  
      # in strings only...
      if type(s) is types.StringType:
        s = string.replace(s, ToEscape, '\\%s' % ToEscape)

    return s

  #
  # Send a query to database and return result set
  #
  def query(self, stmt):

    # Debug?
    self.sqllog.write('STATEMENT: %s' % stmt, printLevel = 1)

    # MySQL
    if self.dbtype is 'mysql':

      # Import module MySQLdb
      try:
        import MySQLdb

      # ImportError
      except ImportError:
        self.sqllog.write('CAN\'T LOCATE MODULE \'MySQLdb\'', printLevel = 1)
        sys.exit()

      try:

        # SQL-Query
        self.cursor.execute(stmt)
        
        # Return row count
        return self.cursor.rowcount

      # Error querying database
      except MySQLdb.Error, msg:

        # Debug?
        if self.debug:
          self.sqllog.write('ERROR QUERYING DATABASE: \'%s\'' % msg, printLevel = 1)

        # Raise exception
        raise myexcept.EDatabaseQuery, msg

      # Warning!
      except MySQLdb.Warning, msg:

        # Debug?
        if self.debug:
          print '[%s' % msg

      # Any other error
      except:

        # Debug?
        if self.debug:
          self.sqllog.write('UNKNOWN ERROR WITH DATABASE: (%s, %s)' % (sys.exc_info()[0], sys.exc_info()[1]))
          sys.exit()

  #
  # Return result
  #
  def fetchall(self):

    # MySQL
    if self.dbtype is 'mysql':

      # Import module MySQLdb
      try:
        import MySQLdb

      # ImportError
      except ImportError:
        self.sqllog.write('CAN\'T LOCATE MODULE \'MySQLdb\'', printLevel = 1)
        return

      # Try to fetch all rows
      try:
        result = self.cursor.fetchall()
        return result

      # Error with fetchall
      except MySQLdb.Error, msg:

        # Debug?
        if self.debug:
          self.sqllog.write('ERROR FETCHING ROWS FROM DATABASE', printLevel = 1)

        # Raise exception
        raise myexcept.EDatabaseQuery, msg

  #
  # Create dictionary of the result 
  # SELECT a, b, c FROM ... ==> a = result[row][0]; b = result[row][1], c = result[row][2]
  # and return dictionary
  #
  def assignRS_FL(self, resultSet = [], fieldList = ''):
  
    # Check arguments
    if not resultSet or not fieldList:
      return '[error]'

    # Split fields from select statement by ,
    splitFields = string.split(string.replace(fieldList, ' ', ''), ',')

    # Init list
    returnResult = []

    # In every row...
    for rowCount in range(0, len(resultSet)):

      fieldValueDict = {}

      # Process every field from database
      for fieldCount in range(0, len(splitFields)):
          
        # Generate list[dictionary]
        fieldValueDict[splitFields[fieldCount]] = resultSet[rowCount][fieldCount]

      # Add FieldValue to tuple returnResult
      returnResult.append(fieldValueDict)

    # Return count and result
    return returnResult

  #
  # Search for
  # fields should contain all database fields you want to retrieve
  # orderby is optional
  # Return result and count
  #
  def search(self, table = 'NOT SET', fields = 'NOT SET', orderby = 'ID', groupby = '', **data):

    # Try to select tuple from database
    try:

      # Initial statement
      stmt = 'SELECT %s FROM %s' % (fields, table)

      try:

        # Process keys in data
        if data:
        
          # Init sql statement
          stmt = '%s WHERE' % stmt

          # All keys of data
          # Count, init i = 0
          i = 0
          for DataKey in data.keys():

            # Add AND when it is not the first key
            if i > 0:
              stmt = '%s AND %s LIKE "%s"' % (stmt, DataKey, data[DataKey])
            else:
              stmt = '%s %s LIKE "%s"' % (stmt, DataKey, data[DataKey])

            # Increase counter
            i = i + 1

      # No keys/dictionary; data given
      except NameError:
      
        # Log error
        self.sqllog.write('sqlal.search(): NameError WITH/MISSING ARGUMENT \'data\']', printLevel = 1)
        
        # Return
        return
      
      # Any other error
      except:
      
        # Log and print error
        self.sqllog.write('sqlal.search(): UNKNOWN ERROR: (%s, %s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)

        # Return
        return 0

      # If groupby is set
      if groupby:
        stmt = '%s GROUP BY %s' % (stmt, groupby)

      # If orderby is set
      if orderby:
        stmt = '%s ORDER BY %s' % (stmt, orderby)

      # Query database
      rowCount = self.query(stmt)

      # Get result as array
      result = self.fetchall()
      
      # Only try to make dictionary if fields is not '*'
      if fields != '*':
        return rowCount, self.assignRS_FL(result, fields)

      # fields = '*'
      else:
        return rowCount, result

    # If an database error occurs
    except myexcept.EDatabaseQuery, msg:

      # Debug?
      if self.debug:
        self.sqllog.write('ERROR WITH SQL STATEMENT \'%s\': \'%s\'' % (stmt, msg), printLevel = 1)

      # Raise exception
      raise myexcept.EDatabaseQuery, msg

  #
  # Get field from database for record with id
  #
  def getfield(self, table = '', field = '', **searchstring):

    # All necessary parameters given?
    if not table or not field or not searchstring:

      # Log error      
      self.sqllog.write('[sqlal.getfield(): MISSING ARGUMENTS: REQUIRE \'table\', \'field\' AND DICTIONARY \'searchstring\'', printLevel = 1)

      # Return
      return 0, '[error]'

    # Get record from database
    try:

      # Get count and result from table.field for searchstring
      cmd = "count, result = self.search(table = '%s', fields = field, " % table

      # Add all key/value-pairs from searchstring
      for SSKey in searchstring.keys():
        cmd = '%s %s = "%s"' % (cmd, SSKey, searchstring[SSKey])

      # Close command; Compile it into object code; Execute it
      cmd = '%s)' % cmd
      py = compile(cmd, '<string>', 'exec')
      exec(py)

      # Successful, return count and result
      return count, result

    # Any error
    except:
    
      # Log error      
      self.sqllog.write('[sqlal.getfield(): UNKNOWN ERROR: (%s, %s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)

      # Return 0 row count and error message
      return 0, '[error]'

  #
  # Act on database
  #
  def act(self, table, **data):
  
    # Protected keys
    ProtectedKeys = ['action']

    # SQL statement
    stmt = ''
    
    # Check for minimal keys
    if data.has_key('action') and data.has_key('ID'):

      #
      # Add to database
      #
      if data['action'] is 'I':
        stmt = 'INSERT INTO %s (' % table
        
        # Build sql statement field = "value"; fields
        for DataKey in data.keys():
          if DataKey not in ProtectedKeys:
            stmt = '%s %s, ' % (stmt, DataKey)

        # Add fields which are mandatory fields
        #stmt = '%s CreatedDateTime, CreatedBy, ChangedDateTime, ChangedBy) VALUES (' % stmt
        stmt = '%s CreatedDateTime, ChangedDateTime) VALUES (' % stmt

        # Build sql statement field = "value"; values
        for DataKey in data.keys():
          if DataKey not in ProtectedKeys:
          
            if type(data[DataKey]) is types.StringType:
              stmt = '%s "%s",' % (stmt, data[DataKey])
            else:
              stmt = '%s %s,' % (stmt, data[DataKey])

        # Add values for mandatory fields
        #stmt = '%s NOW(), %i, NOW(), %i)' % (stmt, data['AgentID'], data['AgentID'])
        stmt = '%s NOW(), NOW())' % stmt

      #
      # Delete Agent from database
      #
      if data['action'] is 'D':
        stmt = 'DELETE FROM %s WHERE ID = "%s"' % (table, data['ID'])

      #
      # Change data of Agent in database
      #
      if data['action'] is 'U':
        stmt = 'UPDATE %s SET ' % table

        # Build sql statement field = "value"; fields
        for DataKey in data.keys():
          if DataKey not in ProtectedKeys:
            stmt = '%s %s = "%s", ' % (stmt, DataKey, data[DataKey])

        # Add values for mandatory fields
        stmt = '%s ChangedDateTime = NOW() WHERE ID = "%s"' % (stmt, data['ID'])

      #
      # Execute query
      #
      if stmt:
      
        try:

          # Query database
          self.query(stmt)
          
        # Error dealing with database
        except myexcept.EDatabaseQuery, msg:
          raise myexcept.EDatabaseQuery, msg

  #
  # Copy
  #
  def Copy(self, table, qNo, qNewNo):

    # Check for given arguments
    if not SID or not qNo or not qNewNo:
      return '[Fatal: Can\´t copy entry: Not all arguments given]'

    try:

      # Get entry from database
      count, result = self.search(table = table, \
        fields = self.form.keys(),
        ID = qNo)

      # Insert new entry into database with qArtNr

    # Any error
    except:
      return 0

    # Return success
    return 1

  #
  # Get maximal ID + 1 from table
  #
  def getNewID(self, table = ''):

    # Check argument
    if not table:
    
      # Log
      self.sqllog.write('[sqlal.getNewID(): CAN\'T GET NEW ID FOR TABLE ?: MISSING TABLE ARGUMENT', printLevel = 2)
      
      # Return error
      return '[MISSING ARGUMENT]'

    # Init newID
    newID = 0

    try:

      # Select MAX(ID) from database
      count, result = self.search(table = table, fields = 'MAX(ID)', orderby = '')
    
      # Increase ID with 1
      # If we have an integer as result
      if result[0]['MAX(ID)'] != None:
        newID = int(result[0]['MAX(ID)']) + 1
        
      # If we have 'None' set to/init with 1
      else:
        newID = 1

    # Any error
    except:
    
      # Log
      self.sqllog.write('[sqlal.getNewID(): ERROR (%s, %s) GETTING NEW ID FOR TABLE=%s' % (sys.exc_info()[0], sys.exc_info()[1], table), printLevel = 1)
      
      # Return
      return '[error]'

    # Return new ID for table
    return newID

  #
  # Get count from table
  #
  def countOf(self, table = '', cond = ''):

    # Check argument
    if not table:
    
      # Log
      self.sqllog.write('[sqlal.countOf(): CAN\'T GET NEW ID FOR TABLE ?: MISSING TABLE ARGUMENT', printLevel = 1)
      
      # Return error
      return '[MISSING ARGUMENT]'

    # Init count
    count = 0

    try:

      # Select COUNT from table
      stmt = 'SELECT COUNT(*) FROM %s' % table
      
      # WHERE condition?
      if cond:
        stmt = '%s WHERE %s' % (stmt, cond)
      
      # Send query
      rowCount = self.query(stmt)
      
      # Fetch result
      result = self.fetchall()
    
      # If we have a result
      if result[0][0] != None:
        return int(result[0][0])
        
      # If we have 'None' set to 0
      else:
        return 0

    # Any error
    except:
    
      # Log error
      self.sqllog.write('sqlal: ERROR (%s, %s) GETTING COUNT FOR TABLE=%s CONDITION=%s' % (sys.exc_info()[0], sys.exc_info()[1], table, cond), printLevel = 1)
      
      # Return error
      return '[error]'

    # Return count
    return count

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
