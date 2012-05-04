#!/usr/bin/env python
##
#
# $Author: rb $
revision =  '$Revision: 1.2.7.1 $'
#
# Revision 1.1  2000-05-11 15:04:06+02  rb
# Initial revision
#
##

#####################################################################
#
# dmerce Template Language
#
#####################################################################

#
# I M P O R T  M O D U L E S
#
try:
  import sys
  import os
  import stat
  import string
  import types
  import imp

  import qError
  import misc
  import security
  import regExp
  import aDataStruct
  import dtl_Macro
except:
  print '[dtl: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()

#####################################################################
#
# C L A S S E S
#
#####################################################################
class Template(dtl_Macro.Macro, aDataStruct.aDS):

  # Some flags
  fdout = sys.stdout # File descriptor to write output to, standard is sys.stdout
  lineCount = 0      # Line counter

  # Loaded modules
  __loadedModules = {}    # Mark loaded modules: {module : 1}
  
  # Main database table for template
  mainTable = ''

  #
  # Constructor
  #
  def __init__(self, lang = ''):
  
    # Call constructor of Macro
    dtl_Macro.Macro.__init__(self)

    # Call constructor of aDS
    aDataStruct.aDS.__init__(self, CGI = 1)
  
    # Import and instanciate standard dmerce modules
    self.myqError = qError.qError()
    self.mymisc = misc.MISC()
    self.myOS = misc.OS()
    self.mypassword = security.password()
    self.myhtml = misc.HTML()
    self.mymemo = misc.memo()
    self.mytime = misc.TIME()
    
    # Set TEMPLATE_ROOT
    #   1. <DOCUMENT_ROOT>/templates/qTemplate
    if self.misc.env('DOCUMENT_ROOT'):
      self.TEMPLATE_ROOT = '%s/templates/' % self.misc.env('DOCUMENT_ROOT')
    
    #   2. Use value config.file
    else:
    
      # Check for language
      if lang:
        self.TEMPLATE_ROOT = '%s/%s/templates/' % (self.cp.get('q', 'DOCUMENT_ROOT'), lang)
      else:
        self.TEMPLATE_ROOT = '%s/templates/' % self.cp.get('q', 'TEMPLATE_ROOT')

  #
  # Construct name of template to open
  # TFN = TemplateFileName
  #
  def filename(self, lang = ''):
  
    # Init flag
    tmplDirFound = 0
    
    try:
      # Try to stat dir
      mode = os.stat(self.TEMPLATE_ROOT)[stat.ST_MODE]
      # Successful: set tmplDirFound
      tmplDirFound = 1

    # Any error
    except:
      pass
    
    # Templates directory found?
    if not tmplDirFound:
      # Log
      self.log.write('dTL: TEMPLATE DIRECTORY NOT FOUND: "%s"' % self.TEMPLATE_ROOT, printLevel = 1)
      # Return
      return '', ''

    # Split qTemplate by '_'
    qTemplateIdent = string.split(self.qVars['qTemplate'], ',')
    
    # Last is template filename
    tmplFile = qTemplateIdent[-1:][0]

    # Init TFN; add 'templates/qTemplate'
    TFN = '%s%s/%s' % (self.TEMPLATE_ROOT, string.join(qTemplateIdent[:-1], '/'), tmplFile)

    # Init TFN_error; add 'templates/qTemplate'
    TFN_error = '%s%s/error.html' % (self.TEMPLATE_ROOT, string.join(qTemplateIdent[:-1], '/'))

    # Add extension .html
    TFN = '%s.html' % TFN

    # Return
    return TFN, TFN_error

  #
  # Returns a formatted value depending on dmerce-own-format-args.
  #
  # Strings:
  # ========
  # Known format: $[<value>],[[position_from-]position_to]$
  #
  # Numbers:
  # ========
  # Known format: $[<value>],[<precision>]$
  #
  def formatValue(self, value = '', formatList = []):

    # Check arguments
    if not value:
      return ''
    
    if not formatList:
      return value
    
    # Float: if format[0] is set print given format
    if type(value) is types.FloatType and formatList[0]:
      formatString = '%.' + formatList[0] + 'f'
      return formatString % value

    # Float: print as %f
    elif type(value) is types.FloatType:
      
      # Convert to string to get rid of trailing zeros
      valueStr = '%s' % value
      
      # Preserve at least 2 floating points
      if len(valueStr[string.index(valueStr, '.') + 1:]) == 1:
        return '%.2f' % float(valueStr)

      # Return
      else:
        return valueStr

    # Integer: print as %i
    elif type(value) is types.IntType:
      return '%i' % value

    # Long: print as %l
    elif type(value) is types.LongType:
      return '%l' % value

    # String
    elif type(value) is types.StringType:

      # [position_from]-[position_to]
      if formatList[0] and formatList[1]:
        return '%s' % value[int(formatList[0]):int(formatList[1])]

      # [position_to]
      elif formatList[0]:
        return '%s' % value[:int(formatList[0])]

      # [position_from]-
      elif formatList[1]:
        return '%s' % value[int(formatList[1]):]

      # Without formatting
      else:
        return '%s' % value

  #
  # Reset loaded modules
  #
  def resetLoadedModules(self):
    self.__loadedModules = {}

  #
  # $[<package>/]<module>:<method>([<args>])$
  #
  def callPyModule(self, package = '', module = '', method = '', args = ''):

    # Check arguments
    if not module or not method:
      return '[error]'
    
    # Init args
    if not args:
      args = ''
    
    # Check for package and if it is already instanciated
    if package:
    
      # Build package.module
      pack_mod = '%s.%s' % (package, module)

      # Check if instance exists
      if not self.__loadedModules.has_key(pack_mod):

        try:

          # Find the module
          find_mod = imp.find_module(package)
        
          # Load it
          mod = imp.load_module(package, find_mod[0], find_mod[1], find_mod[2])
      
          # Make instance
          cmd = 'self.my%s = mod.%s()' % (module, module)
          py = compile(cmd, '<string>', 'exec')
          exec(py)
        
          # Mark as loaded
          self.__loadedModules[pack_mod] = 1

        # ImportError
        except ImportError, msg:

          # Log error and return
          self.log.write('dTL: PACKAGE %s NOT FOUND (%s)' % (package, msg), printLevel = 1)

        # Any other error
        except:

          # Log error
          self.log.write('IMPORTING FUNCTION: %s.%s%s: %s(%s)' % (module, method, args, sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
      
          # Return
          return sys.exc_info()[0]

    # Call method
    try:

      # Call method
      cmd = 'return_value = self.my%s.%s' % (module, method)

      # Arguments?
      if args:
        cmd = '%s%s' % (cmd, args)
          
      # No arguments
      else:
          
        # Close command
        cmd = '%s()' % cmd

      # Compile into object code
      py = compile(cmd, '<string>', 'exec')
      exec(py)
      
      # Return value
      return return_value

    # Any error
    except SyntaxError:

      # Log error
      self.log.write('EXECUTING FUNCTION: %s:%s%s: %s(%s)' % (module, method, args, sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
      
      # Return
      return sys.exc_info()[0]

  #
  # Process macro $var[<x>]$
  #
  def processMacroVar(self, line):

    # Scan for macro
    var = self.var(line)

    # Not found
    if not var:
      # Return unmodified line
      return line

    # Process every found macro    
    for element in range(0, len(var)):
      line = self.regexp.subst(line, regExp.dTL['var'], '%s' % self.getqVar(var[element]))

    # Return unmodified line
    return line

  #
  # Process macro $form[<x>]$
  #
  def processMacroForm(self, line):

    # Scan for macro
    var = self.form(line)
    
    # Not found
    if not var:
    
      # Return unmodified line
      return line

    # Process every found macro
    for element in range(0, len(var)):
      line = self.regexp.subst(line, regExp.dTL['form'], '%s' % self.getCgiFieldStorage(var[element]))

    # Return unmodified line
    return line

  #
  # Process macro $cmd$
  #
  def processMacroCmd(self, line):

    # $qSuperSearch[<x>]$ or $qSuperSearch2[<x>]$
    line = self.processMacroqSuperSearch(line)

    # Scan for macro
    var = self.cmd(line)
    
    # Not found
    if not var:
      # Return unmodified line
      return line

    # Process every found macro
    for element in range(0, len(var)):
    
      # Check return value
      retVal = self.callPyModule(package = string.replace(var[element][0], '/', ''), module = var[element][1], method = var[element][2], args = var[element][3])
      
      # List?
      if type(retVal) is types.ListType:
        subst = '' # Init string

        # Go through list
        for i in retVal:
          
          # Concat elements of list to strings with newline
          subst = '%s%s\n' % (subst, i)

      # No list...
      else:
        subst = retVal
    
      # Substitute
      line = self.regexp.subst(line, regExp.dTL['cmd'], '%s' % subst)

    # Return modified line
    return line

  #
  # Process macro $who[<x>]$
  #
  def processMacroWho(self, line):

    # Scan for macro
    var = self.who(line)
    
    # Not found
    if not var:
    
      # Return unmodified line
      return line
    
    # Process every found macro
    for element in range(0, len(var)):
    
      # Special: whoType
      if var[element] == 'Type':
        subst = '%s' % self.whoType
      
      # Get data (from dictionary self.whoData)
      else:
        subst = '%s' % self.getWho(var[element])
    
      # Substitute
      line = self.regexp.subst(line, regExp.dTL['who'], subst)

    # Return modified line
    return line

  #
  # Process macro $qSuperSearch[<x>]$
  #
  def processMacroqSuperSearch(self, line):

    # Scan for macro
    type, var = self.scanqSuperSearch(line)
    
    # Not found
    if not var:
      # Return unmodified line
      return line

    # Process every found macro
    for element in range(len(var)):
      # Get qVars['qSuperSearch'] or qVars['qSuperSearch2']
      try:
        if type == 1:
          qSuperSearch = self.qVars['qSuperSearch']

        elif type == 2:
          qSuperSearch = self.qVars['qSuperSearch2']
      
      # KeyError -> qSuperSearch / qSuperSearch2
      except KeyError, msg:
        # Log and return error
        self.log.write('$qSuperSearch/qSuperSearch2[]$: NO SUCH VARIABLE: %s' % msg, printLevel = 1)
        return self.regexp.subst(line, regExp.dTL['qSuperSearch2'], '[error]')
    
      # Any error
      except:
        # Log and return error
        self.log.write('$qSuperSearch/qSuperSearch2[]$: (%s, %s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
        return self.regexp.subst(line, regExp.dTL['qSuperSearch2'], '[error]')
    
      # Delete brackets
      for r in ['(', ')']:
        qSuperSearch = string.replace(qSuperSearch, r, '')

      # Convert
      for r in ['*', '~', '[', ']', '!']:
        qSuperSearch = string.replace(qSuperSearch, r, '=')

      # Convert
      for r in [':', '|']:
        qSuperSearch = string.replace(qSuperSearch, r, ',')

      # Find exceptions in strings: ORDER BY, GROUP BY, LIMIT
      exceptions = ['{', '}', '@']
      pos = []  # List of positions in string
      todo = [] # List of extracted directives

      # Find position of chars in string
      for e in exceptions:
        pos.append(string.find(qSuperSearch, e))

      # Sort positions
      pos.sort()

      # Delete from first position a directive occured
      for p in range(len(pos)):
        # If an position was found (> -1)
        if pos[p] > -1:
          # Delete string
          qSuperSearch = qSuperSearch[:pos[p]]
          break

      # Split by ','      
      var_elements = string.split(qSuperSearch, ',')

      # Generate dictionary
      qss_dict = {} # Init
      
      try:
        # Go through list of elements
        for fv in var_elements:
          # Split FIELD=VALUE
          f, v = string.split(fv, '=')

          # Key present?        
          if qss_dict.has_key(f):
            # Make list of key
            qss_dict[f] = [qss_dict[f], v]

          else:
            # Save in dictionary
            qss_dict[f] = v
      
        # Substitute with value from dictionary
        subst = '%s' % qss_dict[var[element]]
        subst = string.replace(subst, '\'', '')
      
        # Substitute
        if type == 1:
          line = self.regexp.subst(line, regExp.dTL['qSuperSearch'], subst)
        elif type == 2:
          line = self.regexp.subst(line, regExp.dTL['qSuperSearch2'], subst)

      # Any error
      except:
        # Log and subsitute with error
        self.log.write('$qSuperSearch[]$: %s(%s)' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
        subst = '[error]'

    # Return modified line
    return line

  #
  # Process macro $table.field$
  #
  def processTableField(self, line):
  
    # Scan for macro
    var = self.tableField(line)
    
    # Not found
    if not var:
      # Return unmodified line
      return line
    
    # Process every found macro
    for element in range(len(var)):
      # Init subst
      subst = ''

      try:
        # Database field has a value
        if self.SQL[var[element][0]][self.repeatSQLrow[self.repeatDepth]][var[element][1]]:
          # Substitute with value from database
          subst = '%s' % self.formatValue(self.SQL[var[element][0]][self.repeatSQLrow[self.repeatDepth]][var[element][1]], [var[element][2], var[element][3]])

        # Field is 'empty' but an integer
        elif type(self.SQL[var[element][0]][self.repeatSQLrow[self.repeatDepth]][var[element][1]]) is types.IntType:
          # Substitute with 0
          subst = '0'

        # Field is 'empty' but a float
        elif type(self.SQL[var[element][0]][self.repeatSQLrow[self.repeatDepth]][var[element][1]]) is types.FloatType:
          # Substitute with 0
          subst = '0.0'

      # KeyError: Field not found; try to query field directly
      except KeyError, field:
        # Get info about field that failed
        table, field, format1, format2 = var[0][0], var[0][1], var[0][2], var[0][3]
        # Build SQL statement and query database
        stmt = 'SELECT %s FROM %s WHERE %s' % (field, table, self.convertqSuperSearch(self.SQLcond[table]))
        self.SQLcount['tmp'] = self.SQLAL.query(stmt)
        # Fetch result and build result dictionary
        self.SQL['tmp'] = self.SQLAL.assignRS_FL(resultSet = self.SQLAL.fetchall(), fieldList = field)
        subst = '%s' % self.formatValue(self.SQL['tmp'][0][field], [format1, format2])

      # Any other error
      except:
        # Log error and return error message
        self.log.write('ERROR GETTING TABLE.FIELD: $%s.%s$: %s(%s) IN SQL ROW %i' % (var[element][0], var[element][1], sys.exc_info()[0], \
                                                                                     sys.exc_info()[1], self.repeatSQLrow[self.repeatDepth]), printLevel = 1)
        subst = '[error: $%s.%s$]' % (var[element][0], var[element][1])
      
      # Substitute
      line = self.regexp.subst(line, regExp.dTL['tf'], subst)

    # Return modified line
    return line

  #
  # Process macro $table.field;FIELD=VALUE$
  #
  def processTableField_xref(self, line = ''):
  
    # Scan for macro
    var = self.tableField_xref(line)
    
    # Not found
    if not var:
      # Return unmodified line
      return line

    # Process every found macro   
    for element in range(len(var)):
      # Init SQL statement
      stmt = ''

      try:
        # Query database
        stmt = 'SELECT %s FROM %s WHERE' % (var[element][1], var[element][0])
        
      # Any error
      except:
        # Log and return error
        self.log.write('TABLE.FIELD_XREF: %s(%s): CAN\'T INIT SQL STATEMENT' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
        return '[XREF error]'

      # Go through FIELD=VALUE pairs
      i = 0 # Counter
      for condElement in range(2, len(var[element]), 2):
        # Increase counter
        i = i + 1
        # Add WHERE cond. to statement
        if i == 1:
          stmt = '%s %s = "%s"' % (stmt, var[element][condElement], var[element][condElement + 1])
        else:
          stmt = '%s AND %s = "%s"' % (stmt, var[element][condElement], var[element][condElement + 1])

      try:
        # Execute query
        rowCount = self.SQLAL.query(stmt)
        if rowCount:      
          result = self.SQLAL.fetchall()
          # Substitute with value from database
          # subst = '%s' % result[0][0]
          if type(result[0][0]) is types.StringType:
            subst = '%s' % result[0][0]
          else:
            subst = '%s' % self.formatValue(result[0][0], [0, 2])
      
        else:
          # Substitute with empty value
          subst = ''
      
      # Any error
      except:
        # Log and return
        self.log.write('TABLE.FIELD_XREF: %s(%s) WITH SQL-QUERY %s' % (sys.exc_info()[0], sys.exc_info()[1], stmt), printLevel = 1)
        subst = '[XREF error]'

      # Substitute
      line = self.regexp.subst(line, regExp.dTL['tf_xref'], subst)
      
      # Scan for more xrefs
      if self.tableField_xref(line):
        line = self.processTableField_xref(line)

    # Return modified line
    return line

  #
  # Process macro %table.field%
  #
  def processTableField_combined(self, line = ''):
  
    # Scan for macro
    var = self.tableField_combined(line)
    
    # Not found
    if not var:
      # Return unmodified line
      return line

    # Process every found macro   
    for element in range(len(var)):
      # Init SQL statement
      stmt = ''

      try:
        # Query database
        stmt = 'SELECT %s FROM %s WHERE %s' % (var[element][1], var[element][0], self.convertqSuperSearch(self.SQLcond[var[element][0]]))
        
      # Any error
      except:
        # Log and return error
        self.log.write('TABLE.FIELD_COMBINED: %s(%s): CAN\'T INIT SQL STATEMENT' % (sys.exc_info()[0], sys.exc_info()[1]), printLevel = 1)
        return '[COMBINED error]'

      try:
        # Execute query
        rowCount = self.SQLAL.query(stmt)
        if rowCount:      
          result = self.SQLAL.fetchall()
          # Substitute with value from database
          subst = '%s' % result[self.repeatSQLrow[self.repeatDepth]][0]
      
        else:
          # Substitute with empty value
          subst = ''
      
      # Any error
      except:
        # Log and return
        self.log.write('TABLE.FIELD_COMBINED: %s(%s) WITH SQL-QUERY %s' % (sys.exc_info()[0], sys.exc_info()[1], stmt), printLevel = 1)
        subst = '[COMBINED error]'

      # Substitute
      line = self.regexp.subst(line, regExp.dTL['tf_combined'], subst)
      
      # Scan for more xrefs
      if self.tableField_combined(line):
        line = self.processTableField_combined(line)

    # Return modified line
    return line
  #
  # Process block $repeat$ ... $end$
  #
  def processMacroRepeatVars(self, line):
  
    # Scan for macro
    var = self.scanRepeatVars(line)
    
    # Not found
    if not var:
      # Return unmodified line
      return line

    # Process every found macro
    for element in range(0, len(var)):
      # Substitute macro with value
      line = self.regexp.subst(line, regExp.dTL['repeatVars'], '%s' % self.repeatVars[var[element]])
    
    # Return modified line
    return line

  #
  # Process line of repeat block
  #
  def processRepeatBlockLine(self, line):
  
    # $table.field$
    line = self.processTableField(line)

    # Check for tableField_xref
    line = self.processTableField_xref(line)

    # Check for tableField_combined
    line = self.processTableField_combined(line)

    # $repeat[<x>]$
    line = self.processMacroRepeatVars(line)

    # $cmd$
    if not self.ifDepth:
      line = self.processMacroCmd(line)
    if self.ifDepth:
      if self.ifIsTrue[self.ifDepth]:
        line = self.processMacroCmd(line)

    # $qSuperSearch[<x>]$
    line = self.processMacroqSuperSearch(line)

    # $SQL[<type>,<table>,<cond>]$
    line = self.processMacroSQL(line)

    # $if$ and $endif$
    procIf, line = self.scanIfBlock(line)

    # ifBlock?
    ifBlock = []
    if procIf:
      # Process all ifBlock lines as repeat block line
      ifBlock = self.processIfBlock(procIf)

    # Print/copy ifBlock
    if ifBlock:
      # Process list
      for line in ifBlock:
        # repeatDepth > 1?
        if self.repeatDepth > 1:
          self.repeatBlock[self.repeatDepth - 1].append(line)
        else:
          self.fdout.write(line)

    # Print line
    elif line:
      # repeatDepth > 1?
      if self.repeatDepth > 1:
        self.repeatBlock[self.repeatDepth - 1].append(line)
      else:
        self.fdout.write(line)

  #
  # Process block $repeat$ ... $endrepeat$
  #
  def processRepeatBlock(self, depth = 0):
  
    # Init
    stmt = ''
    ###Test4repeatBreakCount = 0

    # Check arguments
    if not depth:
      return

    # Set table to work with
    # Repeat block uses own table
    if self.repeatBlockTable.has_key(depth):
      # Get table from dictionary self.repeatBlockTable
      table = self.repeatBlockTable[depth]

      # Get field list of table
      tf = self.getSQLFields(table)

      # If we have fields build SQL statement, query database and fetch result; build result dict.
      if tf:
        stmt = 'SELECT %s FROM %s WHERE %s' % (tf, table, self.convertqSuperSearch(self.SQLcond[table]))
        self.SQLcount[table] = self.SQLAL.query(stmt)
        self.SQL[table] = self.SQLAL.assignRS_FL(resultSet = self.SQLAL.fetchall(), fieldList = tf)
      else:
        # Log and return
        self.log.write('WARNING: NO FIELDS FOUND FOR TABLE %s' % table, printLevel = 1)
        return ''

    # Use main table (given through first element of qTemplate or qTN)
    else:
      table = self.mainTable
    
    # Log
    ###self.log.write('PROCESSING REPEAT BLOCK STAGE %i WITH TABLE %s' % (depth, table), printLevel = 1)

    try:
      # For every result from database, process $repeat$...$endrepeat$
      for self.repeatSQLrow[self.repeatDepth] in range(self.SQLcount[table]):
        # Set $repeat[Index]$
        self.repeatVars['Index'] = self.repeatSQLrow[self.repeatDepth]
    
        # Process list repeatBlock
        for self.repeatBlockIndex in range(len(self.repeatBlock[depth])):
          # Line not empty?
          if self.repeatBlock[depth][self.repeatBlockIndex]:
            # Process line
            self.processRepeatBlockLine(self.repeatBlock[depth][self.repeatBlockIndex])
          else:
            # Process next line
            continue

      ## Do we have 'repeatTimes'?
      #if self.repeatTimes[depth] > 0:
        ## Check repeatTimes and return if == repeatingSQLrow
        #if self.repeatTimes[depth] == repeatingSQLrow:
          #return

      ## Check, if repeatBreakCount is reached
      #if self.repeatBreakCount.has_key(depth):
        ## Counter = repeatBreakCount
        #if Test4repeatBreakCount == int(self.repeatBreakCount[depth]):
          ## Print repeatBreakCode
          #Test4repeatBreakCount = -1
          #self.fdout.write(self.repeatBreakCode[depth])
              
        ## Increase counter
        #Test4repeatBreakCount = Test4repeatBreakCount + 1

    # Any error
    except:
      # Log and return error/nothing
      self.log.write('%s(%s) WITH QUERY %s ON TABLE %s' % (sys.exc_info()[0], sys.exc_info()[1], stmt, table), printLevel = 1)
      return ''

  #
  # Process block $if$ ... $endif$
  #
  def processIfBlock(self, depth = 0):
  
    # Check arguments and block's expression must be true
    if not depth:
      return ''

    # Init b
    b = []

    # Process ifBlock
    for line in self.ifBlock[depth]:
      # Process macro $cmd$
      # (when not processed by self.processRepeatBlockLine before)
      if not self.repeatDepth:
        line = self.processMacroCmd(line)

      # When line
      if line:
        # ifDepth > 1?
        if depth > 1:
          self.ifBlock[(depth - 1)].append(line)
        
        # repeatDepth > 1?
        elif self.repeatDepth > 0:
          # Append line to block
          b.append(line)

        # Print line to stdout
        else:
          self.fdout.write(line)

    # Block 'b' generated?
    if b:
      # Return
      return b

  #
  # Append table and field to dictionary SQL for preload of tables and fields
  #
  def cacheSQLTableField(self, table = '', field = ''):

    # Check arguments
    if not table or not field:
      return
  
    # Check if table exists in SQL dict.
    if not self.SQL.has_key(table):
      # No then create key
      self.SQL[table] = {}

    # Check if table exists in SQLcount dict.
    if not self.SQLcount.has_key(table):
      # No then create key
      self.SQLcount[table] = 0

    # Check if table exists in SQLfields dict.
    if not self.SQLfields.has_key(table):
      # No then create key
      self.SQLfields[table] = []

    # Check, if we have the field already in our list
    if not field in self.SQLfields[table]:
      # Append to list SQL['table']
      self.SQLfields[table].append(field)
      # Log
      ###self.log.write('CACHING SQL TABLE.FIELD: %s.%s' % (table, field), printLevel = 1)

  #
  # $SQL[<type>,<table>,<cond>]$
  #
  def processMacroSQL(self, line):

    # SQL[<type>,<table>,<cond>]
    var = self.scanSQL(line)

    # Not found    
    if not var:
      # Return unmodified line
      return line

    # Log
    # self.log.write('FOUND SQL macro:', 1, var)

    # Process every found macro    
    for element in range(0, len(var)):
      # Init subst
      subst = ''
      
      # newID
      if var[element][0] == 'newID':
        # New ID of table
        subst = '%i' % self.SQLAL.getNewID(table = var[element][1])

      # count
      elif var[element][0] == 'rowcount':
        # Show count of loaded table
        if not var[element][1]:
          subst = '%i' % self.SQLcount[self.mainTable]

        # Show count of another table
        elif self.SQLcount.has_key(var[element][1]):
          subst = '%i' % self.SQLcount[var[element][1]]

      # Get count of another table
      elif var[element][0] == 'count':
        # Strip beginning , in var[element][2]
        # Call countOf
        subst = self.SQLAL.countOf(table = var[element][1], cond = self.convertqSuperSearch(var[element][2][1:]))
        
      # fields
      elif var[element][0] == 'fields':
        # Show selected field of table
        subst = '%s' % self.SQLfields[var[element][1]]

      # Substitute
      line = self.regexp.subst(line, regExp.dTL['SQL'], '%s' % subst)

    # Return modified line
    return line

  #
  # Test/process line for macros
  #
  def feed(self, line = ''):

    # Check arguments
    if not line:
      # Return
      return ''

    # var[<x>]
    line = self.processMacroVar(line)

    # form[<x>]
    line = self.processMacroForm(line)
    
    # who[<x>]
    line = self.processMacroWho(line)

    # Check for macro: $table.field$
    var = self.tableField(line)
    # If found
    if var:
      # Process every found element
      for element in range(len(var)):
        # Cache
        self.cacheSQLTableField(table = var[element][0], field = var[element][1])

    # Return (modified) line
    return line

  #
  # Test/process line for macros
  # and generate buffer for output
  #
  def parse(self, buf = []):
  
    # Check arguments
    if not buf:
      # Return
      return ''
    
    # Log
    ###self.log.write('GOT BUFFER OF %i LINES' % len(buf), printLevel = 1)
    
    # FOR DOWNWARD COMPAT.:
    self.qSearchString()

    # Should we use qSuperSearch?
    if self.qVars.has_key('qSuperSearch'):
      # Call qSuperSearch()
      self.qSuperSearch(table = self.mainTable)

    # Should we use a predefined query?
    elif self.qVars.has_key('qQuery'):
      # Call qQuery()
      self.qQuery(table = self.mainTable)
      
    # Go through list of lines
    for line in buf:
      # Because '$repeat$...$endrepeat$' does this by its own
      if not self.repeatDepth:
      
        # $SQL[<type>,<table>,<cond>]$
        line = self.processMacroSQL(line)

        # $cmd$
        line = self.processMacroCmd(line)

        # $qSuperSearch[<x>]$ or $qSuperSearch2[<x>]$
        line = self.processMacroqSuperSearch(line)

        # Check for tableField_xref
        line = self.processTableField_xref(line)

        # $if$ and $endif$
        ifDepth, line = self.scanIfBlock(line)
        if ifDepth:
          # Process if block
          self.processIfBlock(ifDepth)

      # $repeat$
      if self.repeat(line):
        # Substitute
        line = self.regexp.subst(line, regExp.dTL['repeat'], '')
        # Init list for repeat block
        self.repeatBlock[self.repeatDepth] = []

      # $endrepeat$
      if self.endrepeat(line):
        # Substitute
        line = self.regexp.subst(line, regExp.dTL['endrepeat'], '')
        self.processRepeatBlock(depth = self.repeatDepth)
        # Decrease repeat depth
        self.repeatDepth = self.repeatDepth - 1

      # Append to list, which holds the body of $repeat$...$endrepeat$
      if self.repeatDepth and line:
        self.repeatBlock[self.repeatDepth].append(line)
        line = ''

      # Print line
      if line:
        self.fdout.write(line)

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
