#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 1.2.7.1 $'
#
# Revision 1.1  2000-06-07 17:57:45+02  rb
# Initial revision
#
##

#####################################################################
#
# Access to data strcutures
#
#####################################################################

#
# I M P O R T  M O D U L E S
#
try:
  import sys
  import types
  import string
  import re
  
  import misc
  import si
except:
  print '[aDataStruct: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()

#####################################################################
#
# C L A S S E S
#
#####################################################################
class aDS(si.SI):

  # Init dictionaries for CGI field storages
  cgiFieldStorage = {} # Main storage for variables from CGI form
  IgnoreCgiFields = {} # Fields ignored by database processnig, but further needed
                       # in templates
  qVars = {}           # Special storage for our 'q' variables
            
  # Program specific parameters
  qVars['VERSION'] = '1.2.6'

  #
  # SQL namespace
  # SQL[table] = [row[field]]
  #
  SQL = {}        # Dictionary of tables with list of rows with dictionary of field/value
  SQLcount = {}   # Count of rows of table in result set
  SQLfields = {}  # List of fields of tables used in template
  SQLcond = {}    # List of conditions (format like qSuperSearch) for table queries

  # Build legend for SQL statements
  SQLrep = {
            ':' : ' AND ', \
            '|' : ' OR ', \
            '~' : ' LIKE ', \
            '*' : '=', \
            '!' : ' NOT ', \
            '[' : '<', \
            ']' : '>', \
            '@' : ' LIMIT ', \
            '{' : ' ORDER BY ', \
            '}' : ' GROUP BY '
           }

  # Template-Security
  samStage = 0

  # Init dictionary for variations of qSearch
  appqSearch = {}
  appqSearchName = {}
  appqSearchFields = {}
  appqSearchOp = {}
      
  #
  # Constructor
  #
  def __init__(self, CGI = 0):

    # Call constructor of class SI
    # UseSAM = 0, to only get access to data structures
    si.SI.__init__(self, UseSAM = 0)

    # Process cgi formular if REMOTE_ADDR is set and CGI = 1
    if self.misc.env('REMOTE_ADDR') and CGI:
      self.processCgiForm()
      self.SetEnvVars()

  #
  # Set environment variables; getting information about the server
  #
  def SetEnvVars(self):
    # Set qVars['SSL']
    if self.misc.env('SERVER_PORT') == '443':
      self.qVars['SSL'] = 1
    elif self.misc.env('SERVER_PORT') == '80':
      self.qVars['SSL'] = 0
    else:
      self.qVars['SSL'] = -1

    # Set some environment vars...
    for e in ['SERVER_PROTOCOL', 'SERVER_PORT',]:
      self.qVars[e] = self.misc.env(e)
    
  #
  # Process special form variable qSearch_FIELD=VALUE
  # Append data from qSearch_FIELD=VALUE to and in format of qSuperSearch
  #
  def processqSearch(self):
    # Init qVars['qSuperSearch'] if not present
    if not self.qVars.has_key('qSuperSearch'):
      self.qVars['qSuperSearch'] = ''
    
    # Else add AND
    else:
      self.qVars['qSuperSearch'] = '%s:' % self.qVars['qSuperSearch']
    
    # Prepend left bracket
    self.qVars['qSuperSearch'] = '%s(' % self.qVars['qSuperSearch']

    i = 0
    # Go through appqSearch
    for a in self.appqSearch.keys():
      # Check operator
      if not self.appqSearchOp.has_key(a):
        # Standard operator is LIKE
        op = '~'
      
      else:
        # Operator given in appqSearchOp
        op = self.appqSearchOp[a]

##       # Length of list
##       j = len(self.appqSearch[a])
##       # If we have more than one value
##       if j > 1:
##         # Open bracket
##         self.qVars['qSuperSearch'] = '%s(' % self.qVars['qSuperSearch']

      # Go through list and add 'FIELD OP VALUE'
##       for i in range(j):
      # LIKE operator
      if op == '~':
        self.qVars['qSuperSearch'] = '%s%s%s\'%%%s%%\'' % (self.qVars['qSuperSearch'], a, op, self.appqSearch[a])
      else:
        self.qVars['qSuperSearch'] = '%s%s%s%s' % (self.qVars['qSuperSearch'], a, op, self.appqSearch[a])
##         # Add OR when we have more than one value
##         if i < j:
##           self.qVars['qSuperSearch'] = '%s|' % self.qVars['qSuperSearch']

##       # Close bracket
##       self.qVars['qSuperSearch'] = '%s)' % self.qVars['qSuperSearch']

      # Append AND only if we have more fields; and it's not the last
      i = i + 1
      if i < len(self.appqSearch):
        self.qVars['qSuperSearch'] = '%s:' % self.qVars['qSuperSearch']

    # Append right bracket
    self.qVars['qSuperSearch'] = '%s)' % self.qVars['qSuperSearch']

  #
  # Process special form variable qSearchName_FIELD=VALUE
  # Append data from qSearch_FIELD=VALUE to and in format of qSuperSearch
  #
  def processqSearchName(self):

    # Init qVars['qSuperSearch'] if not present
    if not self.qVars.has_key('qSuperSearch'):
      self.qVars['qSuperSearch'] = ''
    
    # Else add AND
    else:
      self.qVars['qSuperSearch'] = '%s:' % self.qVars['qSuperSearch']

    # Prepend left bracket
    self.qVars['qSuperSearch'] = '%s(' % self.qVars['qSuperSearch']

    # Go through appqSearch
    i = 0 # Counter
    for a in self.appqSearchName.keys():
      # Increase counter
      i = i + 1

      # Check operator
      if not self.appqSearchOp.has_key(a):
        # Standard operator is LIKE
        op = '~'
      
      else:
        # Operator given in appqSearchOp
        op = self.appqSearchOp[a]
      
      # Split by ',' given fields into list
      fields = string.split(self.appqSearchFields[a], ',')

      # Add 'FIELD OP VALUE' for every field
      j = 0 # Counter
      for f in fields:
        # Increase counter
        j = j + 1
        # Add converted fields to qSuperSearch
        if op == '~':
          self.qVars['qSuperSearch'] = '%s(%s%s\'%%%s%%\')' % (self.qVars['qSuperSearch'], f, op, self.appqSearchName[a])

        else:
          self.qVars['qSuperSearch'] = '%s(%s%s%s)' % (self.qVars['qSuperSearch'], f, op, self.appqSearchName[a])
      
        # Append OR only if we have no more fields
        if j < len(fields):
          self.qVars['qSuperSearch'] = '%s|' % self.qVars['qSuperSearch']

      # Append AND only if we have more fields
      if i < len(self.appqSearchName):
        self.qVars['qSuperSearch'] = '%s:' % self.qVars['qSuperSearch']

    # Append right bracket
    self.qVars['qSuperSearch'] = '%s)' % self.qVars['qSuperSearch']

  #
  # Methods to collect
  #
  def collect_qSearch(self, f):
    # Get field and value
    field, value = string.split(f, '_')[1], self.qs[f][0]
    # Save it in dictionary to append it to qSuperSearch later
    self.appqSearch[field] = value
    # Delete it from form
    del self.qs[f]

  def collect_qSearchName(self, f):
    # Get field and value
    field, value = string.split(f, '_', 1)[1], self.qs[f][0]
    # Save it in dictionary to append it to qSuperSearch later
    self.appqSearchName[field] = value
    # Delete it from form
    del self.qs[f]

  def collect_qSearchOp(self, f):
    # Get field and value
    field, value = string.split(f, '_', 1)[1], self.qs[f][0]
    # Save it in dictionary to append it to qSuperSearch later
    self.appqSearchOp[field] = value
    # Delete it from form
    del self.qs[f]

  def collect_qSearchFields(self, f):
    # Get field and value
    field, value = string.split(f, '_', 1)[1], string.replace(self.qs[f][0], ' ', '')
    # Save it in dictionary to append it to qSuperSearch later
    self.appqSearchFields[field] = value
    # Delete it from form
    del self.qs[f]

  def collect_ignore(self, f):
    # Get field and value
    field, value = string.split(f, '_', 1)[1], string.replace(self.qs[f][0], ' ', '')
    # Collect field
    self.CollectIgnoreCgiField(field, value)
    # Delete it form form
    del self.qs[f]

  #
  # Process formular got by CGI
  #
  def processCgiForm(self):
  
    # Set dictionaries for form values
    # Call processForm
    CGI = misc.CGI()
    self.qs, err1, err2 = CGI.processForm()
    # Have we got a form?
    if not self.qs:
      return
    # Catch errors
    if err1:
      # Print and log error message
      print '[ERROR PROCESSING FORM]'
      self.log.write('ERROR PROCESSING FORM: %s, %s' % (err1, err2), printLevel = 1)

    # Go through all form keys
    for f in self.qs.keys():
      
      # Test, if key f begins with 'q' put it into a special dictionary qVars
      if f[0] == 'q':
        # qSearch_FIELD=VALUE
        if string.find(f, 'qSearch_') >= 0:
          self.collect_qSearch(f)

        # qSearchName_xxx
        elif string.find(f, 'qSearchName_') >= 0:
          self.collect_qSearchName(f)
          
        # qSearchOp_xxx
        elif string.find(f, 'qSearchOp_') >= 0:
          self.collect_qSearchOp(f)

        # qSearchFields_xxx
        elif string.find(f, 'qSearchFields_') >= 0:
          self.collect_qSearchFields(f)

        # Value from checkbox: On?
        elif self.qs[f][0] == 'on':
          self.qVars[f] = '1'

        # Store it in qVars
        else:
          self.qVars[f] = self.qs[f][0]

      # Field names does not begin with 'q': ignore_xxx
      elif string.find(f, 'ignore_') >= 0:
        self.collect_ignore(f)

      # No, "normal" variable
      else:
        # Put them into dictionary cgiFieldStorage[key]
        # Check value from checkbox: 'on' or 'off'?
        if self.qs[f][0] == 'on':
          self.cgiFieldStorage[f] = '1'
        else:
          self.cgiFieldStorage[f] = self.qs[f][0]

    # qSearch_FIELD=VALUE
    if len(self.appqSearch):
      self.processqSearch()

    # qSearchName_FIELD=VALUE
    if len(self.appqSearchName):
      self.processqSearchName()

    # Delete instances and data structures
    del self.qs
    del CGI
    
  #
  # $var[x]$ -> qVars[x]
  #
  def getqVar(self, var = ''):

    # Check arguments
    if not var:
      # Log error and return
      self.log.write('$var[]$: MISSING ARGUMENT', printLevel = 1)
      return '[error]'

    try:
      # Get variable form dictionary qVars (from class SI)
      return self.qVars[var]

    # KeyError
    except KeyError:
      # Log error and return
      self.log.write('$var[]$: NO SUCH VARIABLE: %s' % var, printLevel = 1)
      return '[error]'

    # Any other error
    except:
      # Log error and return
      self.log.write('$var[]$: %s(%s) WITH %s' % (sys.exc_info()[0], sys.exc_info()[1], var), printLevel = 1)
      return '[error]'

  #
  # Collect fields with name beginning with 'ignore_'
  #
  def CollectIgnoreCgiField(self, field, value):
    self.IgnoreCgiFields[field] = value

  #
  # Append ignored cgi fields to self.cgiFieldStorage
  #
  def AppendIgnoreCgiFields(self):
    for field in self.IgnoreCgiFields.keys():
      self.cgiFieldStorage[field] = self.IgnoreCgiFields[field]

  #
  # $form[x]$ -> cgiFieldStorage[x]
  #
  def getCgiFieldStorage(self, var = ''):
  
    # Check arguments
    if not var:
      # Log error and return
      self.log.write('$form[]$: MISSING ARGUMENT', printLevel = 1)
      return '[MISSING ARGUMENT]'

    try:
      # Get value for variable
      return self.cgiFieldStorage[var]

    # No such key
    except KeyError:
      # Log error and return
      self.log.write('$form[]$: NO SUCH FORM VALUE: %s' % var, printLevel = 1)
      return ''

    # Any other error
    except:
      # Log and return
      self.log.write('$form[]$: %s(%s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
      return '[ERROR]'

  #
  # $who[x]$
  #
  def getWho(self, var = ''):

    # Check arguments
    if not var:
      # Log error and return
      self.log.write('$who[]$: MISSING ARGUMENT', printLevel = 1)
      return '[error]'

    try:
      # Get variable from class SI
      if var == 'ID':
        return self.whoID
      elif var == 'Type':
        return self.whoTyp
      else:
        return self.whoData[var]

    # KeyError
    except KeyError:
      # Log error and return
      self.log.write('$who[]$: NO SUCH VARIABLE: %s' % var, printLevel = 1)
      return '[error]'

    # Any other error
    except:
      # Log error and return
      self.log.write('$who[]$: %s(%s) WITH %s: ' % (sys.exc_info()[0], sys.exc_info()[1], var), printLevel = 1)
      return '[error]'

  #
  # Get query from queries.sql
  #
  # e.g.:
  # =====
  #
  # [show_basket]
  # fields = ID, SID, ArtNr, Anzahl
  # begin = SELECT
  # from = Warenkorb
  # where = WHERE ...
  # stmt_UseSAM = 1
  #
  def qQuery(self):
  
    # Init variables
    where_cond = 0          # WHERE condition found?

    # Parse queries.sql
    queries = self.parseConfig('queries.sql')
      
    # Build query
    try:
      stmt = '%s %s FROM %s' % (queries.get(self.qVars['qQuery'], 'begin'), \
        queries.get(self.qVars['qQuery'], 'fields'), \
        queries.get(self.qVars['qQuery'], 'from'))

    # Missing option
    except ConfigParser.NoOptionError, msg:
      # Log and return
      self.log.write('MISSING REQUIRED OPTION \'%s\' IN SECTION \'%s\' IN FILE queries.sql' % (msg, ads.qVars['qQuery']), printLevel = 1)
      return 0, {}

    # Missing section
    except ConfigParser.NoSectionError, msg:
      # Log and return
      self.log.write('MISSING REQUIRED SECTION \'%s\' IN FILE queries.sql' % msg, printLevel = 1)
      return 0, {}
      
    # WHERE
    # Do we have a where condition?
    try:
      stmt = '%s WHERE %s' % (stmt, queries.get(self.qVars['qQuery'], 'where'))
      where_cond = 1
    except ConfigParser.NoOptionError, msg:
      pass
    
    # UseSAM
    try:
      if queries.get(self.qVars['qQuery'], 'UseSAM') == '1' and where_cond:
        stmt = '%s AND SID = "%s"' % (stmt, self.SID)
      elif queries.get(self.qVars['qQuery'], 'UseSAM') == '1':
        stmt = '%s WHERE SID = "%s"' % (stmt, self.SID)
    except:
      pass
    
    # Perform query on database
    rowCount = self.SQLAL.query(stmt)
    result = self.SQLAL.fetchall()

    # Return count (numrows) and dictionary of results
    return rowCount, self.SQLAL.assignRS_FL(result, queries.get(self.qVars['qQuery'], 'fields'))

  #
  # Get fields for table from self.SQLfields[table]
  # ready for use with SQL SELECT statement
  #
  def getSQLFields(self, table):
  
    # Init fields
    fields = ''
  
    # Process fields of table
    i = 0 # Counter

    try:

      for field in self.SQLfields[table]:
        # Increase counter
        i = i + 1
      
        # If counter < len(SQL['fields']) then use a comma
        if i < len(self.SQLfields[table]):
          fields = '%s %s,' % (fields, field)
        elif i == len(self.SQLfields[table]):
          fields = '%s %s' % (fields, field)
    
      # Return field-list string
      return fields

    # Any error
    except:
      # Log and return error
      self.log.write('%s(%s) CAN\'T GET FIELDS FOR TABLE %s' % (sys.exc_info()[0], sys.exc_info()[1], table), printLevel = 1)
      return ''

  #
  # Add restrictions for active flag
  #
  def restrActive(self, stmt, table):
  
    # Init
    active = 0
    
    try:
      # Get flag: yes or no?
      active = self.cp.get(table, 'active')
      if active:
        pos_where = string.find(stmt, 'WHERE')
        pos_where = pos_where + 5
        s1 = stmt[:pos_where]
        s2 = stmt[pos_where:len(stmt)]
        return '%s active = 1 AND %s' % (s1, s2)

    # On error (i.e. nothing found in configuration file) do nothing
    except:
      pass

    # Return original statement
    return stmt

  #
  # Add restrictions for B2B/B2C
  #
  def restrB2BB2C(self, stmt, table):
  
    # Init
    B2BB2C = 0

    try:
      # Get ID field from config.file for check
      B2BB2C = self.cp.get(table, 'B2BB2C')

    # Not found...
    except:
      # Return unmodified statement
      return stmt

    if B2BB2C:
      pos_where = string.find(stmt, 'WHERE')
      pos_where = pos_where + 5
      s1 = stmt[:pos_where]
      s2 = stmt[pos_where:len(stmt)]

      # Add restriction for logged in customers
      if self.whoType is 'Customer':
        # Show B2B
        return '%s B2B = 1 AND %s' % (s1, s2)

      # Any other user, but for agents there are no restrictions
      elif self.whoType is 'NOBODY':
        # Show B2C
        return '%s B2C = 1 AND %s' % (s1, s2)
    
  #
  # Add restrictions for user
  #
  def restrUser(self, stmt, table):
  
    # Init
    IDField = ''

    try:
      # Get ID field from config.file for check
      IDField = self.cp.get(table, 'CustomerIDField')

    # Not found...
    except:
      # Return unmodified statement
      return stmt

    if IDField:
      pos_where = string.find(stmt, 'WHERE')
      pos_where = pos_where + 5
      s1 = stmt[:pos_where]
      s2 = stmt[pos_where:len(stmt)]
      # Add 'AND' to SQL statement
      return '%s %s = %i AND %s' % (s1, IDField, self.whoID, s2)

    # Return modified statement
    return stmt

  #
  # Add ORDER BY and/or GROUP BY and/or LIMIT condition
  #
  def orderGroupByLimit(self, stmt):

    # GROUP BY
    if self.qVars.has_key('qGroupby'):
      stmt = '%s GROUP BY %s' % (stmt, self.qVars['qGroupby'])

    # ORDER BY
    if self.qVars.has_key('qOrderby'):
      stmt = '%s ORDER BY %s' % (stmt, self.qVars['qOrderby'])

    # LIMIT BY
    if self.qVars.has_key('qLimit'):
      stmt = '%s LIMIT %s' % (stmt, self.qVars['qLimit'])

    # Return modified statement
    return stmt

  #
  # Append qVars['qSearchString'] to qVars['qSuperSearch']
  # -> AND (...)
  #
  def qSearchString(self):

    # Check arguments
    if not self.qVars.has_key('qSearchString'):
      return 0

    # Init qSuperSearch, if not present    
    if not self.qVars.has_key('qSuperSearch'):
      self.qVars['qSuperSearch'] = ''

    # Add AND if there is something in qSuperSearch    
    if len(self.qVars['qSuperSearch']):
      self.qVars['qSuperSearch'] = '%s:' % self.qVars['qSuperSearch']

    # Append to qSuperSearch
    self.qVars['qSuperSearch'] = '%s(%s)' % (self.qVars['qSuperSearch'], string.replace(self.qVars['qSearchString'], ',', '*'))
    
    # Log
    self.log.write('CONVERTED qSearchString %s' % self.qVars['qSearchString'], printLevel = 1)

  #
  # Convert qSuperSearch-String into SQL WHERE condition
  #
  def convertqSuperSearch(self, q):
    # Convert
    for r in self.SQLrep.keys():
      q = string.replace(q, r, self.SQLrep[r])
    
    # Return real SQL statement
    return q

  #
  # Unsharp search
  #
  def UnsharpSearch(self):
    SQLrep2 = [
                ':',
                '|',
                '~',
                '!',
                '@',
                '{',
                '}',
             ]

    s1 = 'a[*100,-10'

    # Look for characters wanting an unsharp search
    # build regex: number/float with following comma!
    if string.find(s1, ',') >= 0:
      # search for position of [, ] or * in statement and mark it
      pos1 = string.find(s1, '*')

      if pos1 == -1:
        pos1 = string.find(s1, '[')

      if pos1 == -1:
        pos1 = string.find(s1, ']')

      # search for position of , in statement (starting at position of pos1)
      # and mark it
      pos2 = string.find(s1[pos1:], ',')
      pos2 = pos2 + len(s1[:pos1])

      # get the next characters after position of ? until a character from
      # self.SQLrep2 or end
      for r in SQLrep2:
        pos3 = string.find(s1[pos2:], r)
        if pos3 >= 0:
          print 'found:', r, pos3
          break

      # No seperator found: end of line?
      if pos3 == -1:
        pos3 = len(s1[pos2:])

      print 'pos1-pos2:', s1[pos1 + 1:pos2]
      print 'pos2-pos3:', s1[pos2 + 1:pos3 + pos2]

      # analyse string (operators + and - and the value itself)

      # get value between {, ] or * and ?

      # calculate value against values from ?-statement

  #
  # Process qVars['qSuperSearch']
  # Convert qSuperSearch to SQL SELECT statement
  #
  def qSuperSearch(self, table = ''):

    # Check arguments
    if not table:
      # Return empty result
      return 0, {}
    
    # Init statement
    stmt = 'SELECT'
    
    # Get fields
    fl = self.getSQLFields(table)
    
    # Split fl into list
    self.SQLfields[table] = string.split(string.strip(fl), ',')
    
    # Add fields to statement
    stmt = '%s%s' % (stmt, fl)
    
    # FROM condition
    stmt = '%s FROM %s WHERE' % (stmt, table)
    
    # Add WHERE (convert qSuperSearch-String into SQL WHERE condition)
    self.SQLcond[table] = self.qVars['qSuperSearch'] # Save query on table
    stmt = '%s %s' % (stmt, self.convertqSuperSearch(self.qVars['qSuperSearch']))
    
    # qSuperSearch2?
    if self.qVars.has_key('qSuperSearch2'):
      # If qSuperSearch exists add it with AND to WHERE condition
      if self.qVars.has_key('qSuperSearch'):
        stmt = '%s AND %s' % (stmt, self.convertqSuperSearch(self.qVars['qSuperSearch2']))

      # If not add it to WHERE condition
      else:
        stmt = '%s %s' % (stmt, self.convertqSuperSearch(self.qVars['qSuperSearch2']))
        
    # Add B2B/B2C restrictions
    if self.whoType != 'Agent':
      stmt = self.restrB2BB2C(stmt, table)

    # Add agent/customer restrictions
    if self.samStage == 4:
      stmt = self.restrUser(stmt, table)

    # active flag?
    if self.samStage != 4:
      stmt = self.restrActive(stmt, table)

    # Add ORDER BY/GROUP BY/LIMIT condition
    stmt = self.orderGroupByLimit(stmt)

    try:
      # Submit search
      rowCount = self.SQLAL.query(stmt)
      # Fetch result
      result = self.SQLAL.fetchall()
    
      # Log
      ###self.log.write('qSuperSearch: %s ->ROWCOUNT=%i' % (stmt, rowCount), printLevel = 1)
    
      # Set count and table
      self.SQLcount[table], self.SQL[table] = rowCount, self.SQLAL.assignRS_FL(resultSet = result, fieldList = fl)

      # Return true
      return 1

    # Any error
    except:
      # Log and return error
      self.log.write('qSuperSearch: %s(%s) WITH SQL QUERY=%s' % (sys.exc_info()[0], sys.exc_info()[1], stmt), printLevel = 1)
      return 0

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
