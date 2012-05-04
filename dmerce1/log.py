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
  
  import misc
except:
  print '[log: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])

#####################################################################
#
# C L A S S E S
#
#####################################################################

#
# Logging
#
class LOGGER:

  # Flags
  logToFile = 1         # Should we log to a file?
  logToDatabase = 0     # Should we log into a database?

  #
  # Constructor
  #
  def __init__(self, fn = '/tmp/dmerce.log'):

    # Make instances
    self.misc = misc.MISC()
    self.TIME = misc.TIME()

    # Set filename to log to
    self.fn = fn

  #
  # Writes string 'str' with timestamp
  #
  def write(self, str = '', printLevel = 0, *opt):

    # Try to open file for appending
    try:
      self.fd = open(self.fn, 'a+')

      # Get hostname
      if self.misc.env('HTTP_HOST'):
        host = self.misc.env('HTTP_HOST')
      else:
        host = 'localhost'

      # Get client hostname
      if self.misc.env('REMOTE_ADDR'):
        client = self.misc.env('REMOTE_ADDR')
      else:
        client = 'localhost'

      # Generate logstr
      logstr = '%s %s <- %s: %s' % (self.TIME.actual(), host, client, str)

      if opt:
        logstr = '%s OPTLIST=%s' % (logstr, opt)

      logstr = '%s\n' % logstr

      # Log to file
      if printLevel == 1 or printLevel == 2:
        self.fd.write(logstr)
      
      # Print to stdout?
      if printLevel == 0 or printLevel == 2:
        print logstr
    
      # Flush log file
      self.fd.flush()

    # IOError
    except IOError, msg:
    
      # Print error message
      print '[log: CAN\'T OPEN LOGFILE \'%s\' FOR WRITING: %s]' % (self.fn, msg)
      
      # Exit
      sys.exit()

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
