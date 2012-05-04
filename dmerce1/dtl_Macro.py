#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 1.2.7.1 $
#
# Revision 1.1  2000-07-05 20:48:32+02  rb
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
 
  import misc
  import regExp
except:
  print '[dtl_Macro: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()

#####################################################################
#
# C L A S S E S
#
#####################################################################
class Macro:

  # $repeat$ ... $endrepeat$
  repeatDepth = 0       # Depth of repeat
  repeatBlock = {}      # Block between $repeat$ and $endrepeat$
  repeatBlockTable = {} # Which table is processed in which repeat block?
  ##repeatTimes = {}      # How many times to repeat block
  ##repeatBreakCount = {} # Counter when to break repeat and write repeatBreakCode
  ##repeatBreakCode = {}  # Text to output when repeatBreakCount is reached
  repeatVars = {}       # Text to output when repeatBreakCount is reached
  repeatSQLrow = {}     # Line of result set repeat block is working on
  repeatBlockIndex = 0  # Actual line number of actual repeat block (being processed)

  # $if$ ... $endif$
  ifDepth = 0           # Depth of if
  ifBlock = {}          # Block between $if$ and $endif$
  elseBlock = {}        # Block between $else$ and $endif$
  ifIsTrue = {}         # Flag, if <expr> is true

  #
  # Constructor
  #
  def __init__(self):

    # Make instance of misc.regexp
    self.regexp = misc.regexp()
    
  #
  # Look for macro: $var[x]$ in line
  #
  def var(self, line):
    return self.regexp.findall(line, regExp.dTL['var'])

  #
  # Look for macro: $form[x]$ in line
  #
  def form(self, line):
    return self.regexp.findall(line, regExp.dTL['form'])

  #
  # Look for macro: $who[<x>]$ in line
  #
  def who(self, line):
    return self.regexp.findall(line, regExp.dTL['who'])

  #
  # Look for macro: $qSuperSearch[<x>]$ or $qSuperSearch2[<x>]$ in line
  #
  def scanqSuperSearch(self, line):
  
    # Scan for qSuperSearch
    var = self.regexp.findall(line, regExp.dTL['qSuperSearch'])
    
    # Found?
    if var:
      # Return
      return 1, var

    # Not found
    elif not var:
      # Scan for qSuperSearch2
      var = self.regexp.findall(line, regExp.dTL['qSuperSearch2'])

      # If found      
      if var:
        # Return
        return 2, var
    
    # Return false
    return 0, 0

  #
  # Look for macro: $<table>.<field>[[,<precision]|[[<position-from],<position-to>]$
  #
  def tableField(self, line):

    # Look for macro in line
    var = self.regexp.findall(line, regExp.dTL['tf'])
    if not var:
      # Return false
      return 0

    else:
      # Return true
      return var
    
  #
  # Look for macro $<table>.<field>;FIELD=VALUE[[,<precision]|[[<position-from],<position-to>]$ in line
  #
  def tableField_xref(self, line):
    return self.regexp.findall(line, regExp.dTL['tf_xref'])

  #
  # Look for macro %table.field%
  #
  def tableField_combined(self, line):
    return self.regexp.findall(line, regExp.dTL['tf_combined'])

  #
  # $[<package>],<module>,[<class>]:<method>([<args>])$
  #
  def cmd(self, line):

    # Look for macro in line
    var = self.regexp.findall(line, regExp.dTL['cmd'])
    
    # Not found
    if not var:
      # Return false
      return 0

    # Return true            
    return var

  #
  # Check occurance of value in variable
  # not caring about if it is a list or tuple or not
  #
  def occur(self, val1, val2):
  
    # Log
    ###self.log.write('occur(): GOT val1, val2:', 1, val1, val2)
  
    # If val2 is a list or a tuple  
    if type(val2) is types.ListType or type(val2) is types.TupleType:

      # Convert type of val1 to StringType, if list element has StringType
      if type(val1) is not types.StringType and type(val2[0]) is types.StringType:
        val1 = '%s' % val1

    # cond = '3', ['1', '3']
    if type(val2) is types.ListType or type(val2) is types.TupleType:
      return val1 in val2

    # cond = 3, 3
    else:
      return val1 == val2

  #
  # $if <expr>$
  #
  def ifCond(self, line):
  
    # Look for macro in line
    ifCond = self.regexp.findall(line, regExp.dTL['ifCond'])
    
    # Did we find something?
    if not ifCond:
      # Return false
      return 0

    # Set depth and flags
    self.ifDepth = self.ifDepth + 1
    self.ifBlock[self.ifDepth] = []
    self.ifIsTrue[self.ifDepth] = 0
    
    # TODO: NO EXEC!!!
    # split catched if cond. in 3 vars: var1 op var2 (NESTED -> AND OR ... !!!)
    # then try to evaluate expr manually 
    # Evaluate expression: construct command, compile it into code object and execute it as python code
    cmd = "if %s: self.ifIsTrue[self.ifDepth] = 1" % ifCond[0]
    py = compile(cmd, '<string>', 'exec')
    
    try:
      exec(py)

    # Any error
    except:
      # Log and raise errpr
      self.log.write('$if$: %s(%s) EVALUATING EXPRESSION %s' % (sys.exc_info()[0], sys.exc_info()[1], cmd), printLevel = 1)
      raise sys.exc_info()[0], cmd
    
    # Prepare log string
    ###logIfExpr = '$if$ STAGE %i: if %s' % (self.ifDepth, ifCond[0])

    # Expression true
    ###if self.ifIsTrue[self.ifDepth]:

      # Log
      ###self.log.write('%s TRUE' % logIfExpr, printLevel = 1)

    # Expression is false
    ###else:
      
      # Log
      ###self.log.write('%s FALSE' % logIfExpr, printLevel = 1)

    # Return true
    return 1

  #
  # Look for macro $else$ in line
  #
  def elseCond(self, line):
    return self.regexp.findall(line, regExp.dTL['elseCond'])

  #
  # Look for macro $endif$ in line
  #
  def endifCond(self, line):
    return self.regexp.findall(line, regExp.dTL['endifCond'])

  #
  # Act with block between $if$ and $endif$
  #
  def scanIfBlock(self, line):

    try:

      # $if$
      ok = self.ifCond(line)
      if ok:
        line = self.regexp.subst(line, regExp.dTL['ifCond'], '')

      # $endif$
      ok = self.endifCond(line)
      if ok:
        line = self.regexp.subst(line, regExp.dTL['endifCond'], '')

        # ifDepth?           
        if self.ifDepth > 0:

          # If expression is true
          if self.ifIsTrue[self.ifDepth]:
            # Return true
            depth = self.ifDepth

          else:
            # Return false
            depth = 0
              
          # Reduce ifDepth
          self.ifDepth = self.ifDepth - 1
        
          # Return
          return depth, ''

    # Any error
    except:

      # Log error
      self.log.write('$if$: %s(%s) WITH LINE %s' % (sys.exc_info()[0], sys.exc_info()[1], line), printLevel = 1)
 
      # Return false
      return 0, ''

    # If we are in an if statement
    if self.ifDepth and line:
    
      # If expression is true
      if self.ifIsTrue.has_key(self.ifDepth):
      
        # Append to list 'ifBlock'
        self.ifBlock[self.ifDepth].append(line)
      
      # Empty line
      line = ''

    # Return line
    return 0, line

  #
  # Look for macro: $repeat[<x>]$ in line
  #
  def scanRepeatVars(self, line):
    return self.regexp.findall(line, regExp.dTL['repeatVars'])

  #
  # $repeat[,x times[, break x code]]$
  #
  def repeat(self, line):

    # Look for macro in line (1)
    var = self.regexp.findall(line, regExp.dTL['repeat'])

    # Did we find something?
    if not var:

      # Return false
      return 0

    # Set depth
    self.repeatDepth = self.repeatDepth + 1
    
    # Create list for block
    self.repeatBlock[self.repeatDepth] = []

    # Create list for SQL row
    self.repeatSQLrow[self.repeatDepth] = 0

    # Log
    ###self.log.write('REPEAT STAGE=%i' % self.repeatDepth, 1, var)

    # table,qSuperSearch-condition
    if var[0][1]:
      
      # Split var[1] into table and condition
      table, cond = string.split(string.strip(var[0][1]), ',')

      # If repeat depth is greater than 1,
      # process macro $table.field$ first
      if self.repeatDepth > 1:

        # Decrease repeatDepth
        self.repeatDepth = self.repeatDepth - 1

        # Scan condition for $table.field$ and substitute with value from
        # actual row of higher repeat block
        cond = self.processTableField(cond)

        # Inrease repeatDepth (to actual value)
        self.repeatDepth = self.repeatDepth + 1
          
      # Set variables for SQL
      self.repeatBlockTable[self.repeatDepth] = table
      self.SQLcond[table] = cond

    # times
    if var[0][2]:
      self.repeatTimes[self.repeatDepth] = self.regexp.findall(var[0][1], regExp.repeat['find_times'])[0]

    # no times
    ###else:
      ###self.repeatTimes[self.repeatDepth] = 0

    # break
    ###if var[0][3]:
      ###self.repeatBreakCount[self.repeatDepth] = self.regexp.findall(var[0][3], regExp.repeat['find_breakCount'])[0]
      ###self.repeatBreakCode[self.repeatDepth] = self.regexp.findall(var[0][3], regExp.repeat['find_breakCode'])[0]

    # no break
    ###else:
      ###self.repeatBreakCount[self.repeatDepth] = 0
      ###self.repeatBreakCode[self.repeatDepth] = ''

    # Return true
    return 1

  #
  # Look for macro $endrepeat$ in line
  #
  def endrepeat(self, line):
    return self.regexp.findall(line, regExp.dTL['endrepeat'])

  #
  # Look for macro in line $SQL[<type>,<x>]$
  #
  def scanSQL(self, line):
    return self.regexp.findall(line, regExp.dTL['SQL'])
    
  #
  # Check for special tag: 
  #
  # Known format: <html [..] sam="<value := [digit]>">
  #
  def scanSamStage(self, line):
  
    # Find known format
    var = self.regexp.findall(line, regExp.dTL['samStage'])

    # Did we find something?
    if not var:
      # Return none
      return None, line

    else:

      # Substitute sam="x" with '' in string 'line'
      # Convert found stage to integer variable
      # Return stage and substituted line
      return int(var[0]), self.regexp.subst(line, regExp.dTL['samStage'], '')
