#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 1.2.7.1 $
#
# Revision 1.1  2000-07-13 17:05:50+02  rb
# Initial revision
#
##

#
# I M P O R T  M O D U L E S
#
try:
  import sys
  
  import aDataStruct
except:
  print '[db2HTML: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()

#####################################################################
#
# C L A S S E S
#
#####################################################################

#
# Process <select>-tags
#
class select(aDataStruct.aDS):

  #
  # Constructor
  #
  def __init__(self):
  
    # Call constructor of aDS
    aDataStruct.aDS.__init__(self)
  
  #
  # Generate <option>-fields out of a database table
  #
  def mkOptions(self, table = '', qSuperSearch = '', optionValue = '', optionName = '',  selected = ''):
  
    # Build SQL statement
    stmt = 'SELECT %s, %s FROM %s WHERE %s' % (optionValue, optionName, table, self.convertqSuperSearch(qSuperSearch))
    
    # Query database
    rowCount = self.SQLAL.query(stmt)
    
    # Check rowCount
    if not rowCount:
    
      # Return false
      return 0
    
    # Fetch result
    result = self.SQLAL.fetchall()
    
    # Generate <option>-fields
    optionList = [] # Init list
    for i in range(0, len(result)):
    
      # Init <option>-tag
      appStr = '<option value="%s"' % result[i][0]
      
      # Check if value should be selected
      if selected:
        if result[i][0] == selected:
          appStr = '%s selected' % appStr

      appStr = '%s>%s</option>' % (appStr, result[i][1])

      # Append to list
      optionList.append(appStr)
      
    # Return list of strings
    return optionList

#
# Process boxes
#
class box(aDataStruct.aDS):

  #
  # Constructor
  #
  def __init__(self):
  
    # Call constructor of aDS
    aDataStruct.aDS.__init__(self)
  
  #
  # Return nothing or 'checked'
  #
  #def isChecked(self, var = 0):
  
    # Check value of 'var'
    #if var == 1 or var == 'on':
    
      # Yes it is!
      #return ' checked'

    #else:
    
      # Return nothing
      #return ''

  #
  # Return '' or 'checked'
  #
  def isChecked(self, expr = ''):
  
    # If true
    if expr:
    
      # Return string 'checked'
      return 'checked'
    
    # If false, return nothing
    else:
      return ''
