#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 1.2.7.1 $'
#
# Revision 1.1  2000-07-03 13:11:38+02  rb
# Initial revision
#
##

#
# I M P O R T  M O D U L E S
#
try:
  import sys
  import imp
except:
  print '[qError: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()
      
#####################################################################
#
# C L A S S E S
#
#####################################################################
class qError:

  #
  # Return message of error number
  #
  def getMsg(self, qErrNo = '', lang = 'de'):
  
    # Check arguments
    if not qErrNo:
      return 'NO ERROR'

    # Check arguments
    if qErrNo == '[error]':
      return 'UNKNOWN ERROR CODE - REFER TO LOG FILES'

    # I M P O R T  M O D U L E S
    try:
      
      # Find the module
      modName = 'myexcept_%s' % lang
      find_mod = imp.find_module(modName)
        
      # Load it
      mod = imp.load_module(modName, find_mod[0], find_mod[1], find_mod[2])
      
    except:
      print '[qError: %s(%s) LOADING LANGUAGE SPECIFIC ERRORS]' % (sys.exc_info()[0], sys.exc_info()[1])
      return 'UNKNOWN ERROR'

    # Return error message
    return mod.ERR[qErrNo]

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

    # Print an error message referring to an error number
    elif sys.argv[1]:
      q = qError()
      print q.getMsg(qErrNo = sys.argv[1])
      sys.exit()
