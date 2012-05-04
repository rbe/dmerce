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
  import os
  import time
  import cgi
  import string
  import re
  import operator
  import types
except:
  print '[misc: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()

#####################################################################
#
# C L A S S E S
#
#####################################################################

#
# Miscellanous functions
#
class MISC:

  #
  # Return operating system enviroment variables
  #
  def env(self, key):

    # Get a key from the environment
    # or set result to ''
    try:
      return os.environ[key]
    
    # KeyError: enviroment variable does not exist
    except KeyError:
      return ''

#
# HTML functions
#
class HTML:

  #
  # Print meta information for changing to another html site
  #
  def refresh(self, destination = '/', duration = 0):

    # Print HTML meta key
    print '<meta http-equiv="refresh" content="%i; url=%s">' % (duration, destination)

  #
  # Return to referer page by http redirect
  #
  def return2referer(self):

    # Make instance of MISC
    misc = MISC()

    # Return to caller/referer
    print '<html><head><link rel="stylesheet" type="text/css" href="style.css"><meta http-equiv="refresh" ' \
          'content="0; url=%s"></head></html>' % misc.env('HTTP_REFERER')

  #
  # Substitute html syntax to show in a html document
  #
  def substEntities(self, line = ''):

    # Check arguments  
    if not line:
    
      # Return empty line
      return ''
  
    # Dictionary: substitute chars with html entities
    entities = {'<' : '&lt;', \
                '>' : '&gt;'}

    # Go through dictionary and substitute
    for e in entities.keys():
      line = string.replace(line, e, entities[e])

    # Return formatted line
    return line

  #
  # Print line with or without '<br>' depending
  # on set enviroment variable 'REMOTE_ADDR'
  # assuming it's called from a browser
  #
  def printLine(self, line = '', withoutNL = 0):

    # Check arguments  
    if not line:
    
      # Return empty line
      return ''
    
    # Make instance of MISC
    misc = MISC()
  
    # HTTP, no newline
    if misc.env('REMOTE_ADDR') and not withoutNL:
      print '%s<br>\n' % self.substEntities(line)

    # HTTP, newline
    elif misc.env('REMOTE_ADDR') and withoutNL:
      print '%s' % self.substEntities(line)

    # Terminal, no newline
    elif not misc.env('REMOTE_ADDR') and not withoutNL:
      print line

    # Terminal, newline
    elif not misc.env('REMOTE_ADDR') and withoutNL:
      print line,

  #
  # Return nothing or 'checked'
  #
  def isChecked(self, var = 0):
  
    # Check arguments
    if not var:
      # Return nothing
      return ''

    # Check value of 'var'
    if var == 1 or var == 'on':
      # Yes it is!
      return ' checked'

    else:
      # Return nothing
      return ''

#
# CGI functions
#
class CGI:

  #
  # Process QUERY_STRING
  #
  def processForm(self):

    try:
      # Make instance of MISC
      m = MISC()

      # Check REQUEST_METHOD: GET
      if m.env('REQUEST_METHOD') == 'GET':
        # Parse QUERY_STRING
        qs = cgi.parse_qs(m.env('QUERY_STRING'))

      # Check REQUEST_METHOD: POST
      elif m.env('REQUEST_METHOD') == 'POST':
        # Parse sys.stdin
        qs = cgi.parse()

      # If no error occured
      return qs, '', ''

    # Any error
    except:
      return {}, sys.exc_info()[0], sys.exc_info()[1]
    
#
# EMail functions
#
class email:

  #
  # Send email
  #
  def sendmail(self, fromaddr = '', toaddr = [], subject = '', message = ''):
  
    # Check arguments
    if not fromaddr or not toaddr:
      # Return false
      return 0

    # Open smtp log file
    fd = open('/tmp/dmerce_SMTP.log', 'w')
      
    # Import modules
    try:
      import smtplib
      import rfc822

    # Any error
    except:
    
      # Print error message
      print '[email: ERROR LOADING MODULES: (%s, %s)' % (sys.exc_info()[0], sys.exc_info()[1])
      
      # Return false
      return 0
  
    # Try to send mail
    try:

      # Establish connection to mailer on localhost
      server = smtplib.SMTP('localhost')

      # No debugging
      server.set_debuglevel(0)

      # Generate time string
      date = time.strftime("%d %b %Y %H:%M:%S %Z", time.localtime(time.time()))

      # Generate message headers
      msg = 'Date: %s\nFrom: %s\nReply-To: %s\nTo: %s' % (date, fromaddr, fromaddr, string.join(toaddr, ','))
      
      # Do we have a subject?
      msg = '%s\nSubject: %s' % (msg, subject)
      
      # Add message body
      msg = '%s\n\n%s' % (msg, message)

      # Send mail
      server.sendmail(fromaddr, toaddr, msg)
      
      # Quit server connection
      server.quit()
      
      # Write message into log file
      fd.write('SENDING MESSAGE:\n%s\n' % msg)
      
      # Close file
      fd.close()

      # Return success
      return 1
      
    # Any error
    except:
    
      # Generate error message
      err_msg = '[email: ERROR SENDING MAIL: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
      
      # Write error message to smtp log
      fd.write(err_msg + '\n')

  #
  # Send file content as email
  #
  def sendFileAsMail(self, fromaddr = '', toaddr = [], subject = '', file = ''):
  
    # Open file for reading
    fileContent = open(file, 'r').readlines()
    
    # Construct message as string form list fileContent
    msg = '' # Init msg
    for line in fileContent:
    
      # Do we have a subject?
      if not subject:
      
        # We found it on the first position of the line (important!)
        if string.find(line, 'Subject:') == 0:
          subject = line[9:]

      # Subject already found, add line to message body
      else:
        msg = '%s%s' % (msg, line)
    
    # Send read lines as mail
    self.sendmail(fromaddr = fromaddr, toaddr = toaddr, subject = subject, message = msg)

#
# Regex functions
#
class regexp:

  #
  # Find all regex 'regex' in string 'line'
  # Compile: must we compile a regex before, or is it compiled?
  #
  def findall(self, line, regex, compile = 0):

    # Check arguments
    if not line or not regex:
      # Return false
      return 0

    # Compile regex
    if compile:
      macro = re.compile(regex)

    # Findall
    mm = regex.findall(line)
    
    # List of found strings
    if mm:
      return mm
    
    # No...
    else:
      # Return false
      return 0

  #
  # Find regex 'regex' in string 'line'
  # Compile: must we compile a regex before, or is it compiled?
  #
  def match(self, line, regex, compile = 0):
  
    # Check arguments
    if not line or not regex:
      # Return false
      return 0

    # Compile regex
    if compile:
      macro = re.compile(regex)

    # Match
    mm = regex.match(line)
    
    # Regex match in line?
    if mm != None:
      # Return found groups
      return mm.groups()
      
    # No...
    else:
      # Return false
      return 0

  #
  # Substitute regex 'regex' in string 'line' with string 's'
  #
  def subst(self, line, regex, s, compile = 0):

    # Check arguments
    if not line or not regex:
      # Return line
      return ''

    # Should we compile 'regex'?
    if compile:
      # Yes, compile regex
      ms = re.compile(regex)

    # Regex is pre-compiled
    else:
      ms = regex

    # Substitute macro with string 's'
    line = ms.sub(s, line, count = 1)

    # Return modified line
    return line

#
# Base OS functions
#
class OS:

  #
  # Does file 'fn' exist?
  #
  def fileExists(self, fn = ''):
    # Check arguments
    if not fn:
      # Return false
      return 0
    
    try:
      # Add DOCUMENT_ROOT TO fn
      fn = '%s/%s' % (os.environ['DOCUMENT_ROOT'], fn)

      # Try to stat
      if os.stat(fn):
        # Return true
        return 1

      else:
        # Return false
        return 0

    # Any error    
    except:
      # Return false
      return 0

  #
  # Upload a file to webserver
  #
  def FileUpload(self, qFilename = '', qSaveAt = '', qSaveAs = ''):
    # Check variables
    if not qSaveAt:
      qSaveAt = '/tmp'

    # Save file
    if qFilename.file:
      fd = open('%s/%s' % (qSaveAt, qSaveAs), 'w')
      fd.write(qFilename.file.read())
      fd.close()

#
# Working with memos
#
class memo:

  #
  # Do table 'Memo' has entries for 'Tabelle'?
  #
  def countOf(self, Tabelle = '', TabelleID = 0):

    if not Tabelle or not TabelleID:
      return '[No argument given: can\'t get count]'

    # Import SI
    import si
    
    # Make instance
    si = si.SI(UseSAM = 0)
    
    # Query database
    si.SQLAL.query('SELECT COUNT(*) FROM Memo WHERE Tabelle = "%s" AND TabelleID = %i' % (Tabelle, TabelleID))
    result = si.SQLAL.fetchall()
    return result[0][0]

#
# Time operations
#
class TIME:

  # Define values for time entities in seconds
  inSecs = {}
  inSecs['min'] = 60
  inSecs['hour'] = inSecs['min'] * 60
  inSecs['day'] = inSecs['hour'] * 24
  inSecs['week'] = inSecs['day'] * 7
  inSecs['month30'] = inSecs['day'] * 30
  inSecs['month31'] = inSecs['day'] * 31
      
  #
  # Return actual date and time
  #
  def actual(self, type = 'ISO', format = '%Y-%m-%d %H:%M:%S'):

    # Return date/time string
    if type == 'ISO':
      return time.strftime(format, time.localtime(time.time()))
    elif type == 'timestamp':
      return time.time()

  #
  # Calculate a date/time string
  #
  def calc(self, format = '%Y-%m-%d %H:%M:%S', timestamp = 0, **opt):
  
    # Return seconds of time entity
    if opt.has_key('inSecs'):
      
      # Multiply?
      if opt.has_key('mult'):
        return self.inSecs[opt['inSecs']] * opt['mult']
      else:
        return self.inSecs[opt['inSecs']]
  
    # Get time tuple of 'now'
    # Convert tuple to dictionary
    timeDict = self.timeTuple2timeDict(time.localtime(time.time()))

    # Calculate new
    for k in opt.keys():
    
      # Add time
      if k != 'week':
        timeDict[k] = timeDict[k] + opt[k]
      
      # Week is a special thing...
      # Add week * 7 to julianDay
      else:
        timeDict['day'] = timeDict['day'] + (opt[k] * 7)

    # Convert dictionary to tuple
    timeTuple = self.timeDict2timeTuple(timeDict)
    
    # Return new formatted date/time string
    if not timestamp:
      return time.strftime(format, time.localtime(time.mktime(timeTuple)))
    else:
      return time.mktime(timeTuple)

  #
  # Calculate difference between two dates
  #
  def diff(self, d1, d2):
    # Check d1 and d2 for type; if string (no timestamp) convert
    if not d1 or not d2:
      return 'N/A'
    if type(d1) is types.StringType:
      if d1[0] == '0':
        return 'N/A'
      if self.isISODate(d1):
        d1 = self.isoToTimestamp(d1)
    if type(d2) is types.StringType:
      if d2[0] == '0':
        return 'N/A'
      if self.isISODate(d2):
        d2 = self.isoToTimestamp(d2)
    # Calculate difference in seconds
    delta = d1 - d2
    if delta < 0:
      delta = operator.neg(delta)
    # Calculate days
    days = int(round(delta / 60 / 60 / 24))
    if days == 0:
      days = 1
    return days

  #
  # Convert german date format to timestamp
  # Automatically recognizes what is given
  #
  def germanToTimestamp(self, date = ''):

    # Init
    year = 0
    month = 0
    day = 0
    hour = 0
    min = 0
    sec = 0

    # Get slices from iso; DD-MM-YYYY
    day = int(date[0:2])
    month = int(date[3:5])
    year = int(date[6:10])
    
    # Time also given?
    if len(date) == 19:

      # Get slices from iso; HH:MM:SS
      hour = int(date[11:13])
      min = int(date[14:16])
      sec = int(date[17:19])

    # Return timestamp
    return time.mktime(year, month, day, hour, min, sec, 0, 0, -1)

  #
  # Check if we have an ISO date format
  #
  def isISODate(self, date = ''):
    if string.find(date, '-') == 4:
      return 1
    else:
      return 0

  #
  # Check if we have an german date format
  #
  def isGermanDate(self, date = ''):
    if string.find(date, '.') == 2:
      return 1
    else:
      return 0

  #
  # Convert ISO date format to timestamp
  # Automatically recognizes what is given
  #
  def isoToTimestamp(self, date = ''):
    
    # Init
    year = 0
    month = 0
    day = 0
    hour = 0
    min = 0
    sec = 0

    # Get slices from iso; YYYY-MM-DD
    year = int(date[0:4])
    month = int(date[5:7])
    day = int(date[8:10])
    
    # Time also given?
    if len(date) == 19:

      # Get slices from iso; HH:MM:SS
      hour = int(date[11:13])
      min = int(date[14:16])
      sec = int(date[17:19])

    # Return timestamp
    return time.mktime(year, month, day, hour, min, sec, 0, 0, -1)

  #
  # Generate time dictionary
  #
  def timeTuple2timeDict(self, timeTuple = ()):
  
    # Init dictionary
    dict = {}

    # Assign values from tuple
    dict['year'] = timeTuple[0]
    dict['month'] = timeTuple[1]
    dict['day'] = timeTuple[2]
    dict['hour'] = timeTuple[3]
    dict['min'] = timeTuple[4]
    dict['secs'] = timeTuple[5]
    dict['weekDay'] = timeTuple[6]
    dict['julianDay'] = timeTuple[7]
    dict['DST'] = timeTuple[8]
    
    # Return dictionary
    return dict

  #
  # Generate time tuple
  #
  def timeDict2timeTuple(self, timeDict = {}):
  
    # Assign values from dictionary
    return (timeDict['year'], timeDict['month'], timeDict['day'], timeDict['hour'], timeDict['min'], timeDict['secs'], timeDict['weekDay'], timeDict['julianDay'], timeDict['DST'])
    
  #
  # Return human readable date and time format
  # from UNIX time
  #
  def unixToLocal(self, secs = 0.0, format = '%Y-%m-%d %H:%M:%S'):

    # If UNIX time is given
    if secs > 0:
    
      # Return formatted string
      return time.strftime(format, time.localtime(secs))
    
    else:

      # Return empty
      return '[Never]'

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
