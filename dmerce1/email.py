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

#####################################################################
#
# email.py:      Send form as email
# author:        Copyright (C) 2000 by Informationssysteme Ralf Bensmann
# date:          2000-03-25
#
# <form action="http://cgi.bensmann.de/email.pyc" method="post">
#
#   <input type="hidden" name="sender" value="info@blablub.de">
#   <input type="hidden" name="recipient" value="info@blablud.de">
#   <input type="hidden" name="subject" value="Kontakt-Seite">
#   <input type="hidden" name="required" value="realname">
#   <input type="hidden" name="env_report" value="REMOTE_HOST, HTTP_USER_AGENT">
#   <input type="hidden" name="redirect_ok" value="http://www.blablub.de/contact_response.html">
#   <input type="hidden" name="redirect_error" value="http://www.blablub.de/contact_error.html">
#   <input type="hidden" name="redirect_missing_fields" value="http://www.blablub.de/contact_error.html">
#
# </form>

# Fields which begin with 'ignore_' in <input name=> will be ignored
#
#####################################################################

# Global namespace
GLOBAL = {}
GLOBAL['message'] = 'Values from email formular:\n\n'

# Proctected keys: don't use/show in email
ProtectedKeys = ['sender', 'recipient', 'subject', 'required', 'env_report', \
                 'redirect_ok', 'redirect_error', 'redirect_missing_fields', \
                 'message']

# Print CGI header
print 'Content-type: text/html'
print

#####################################################################
#
# F U N C T I O N S
#
#####################################################################

#
# Everything ok, send email
#
def sendmail():

  # Global namespace
  global GLOBAL

  # Try to send mail
  try:

    # Make instance of SMTP lib
    server = smtplib.SMTP('localhost')

    # No debug messages
    server.set_debuglevel(0)

    # Actual date and time
    date = time.strftime("%d %b %Y %H:%M:%S %Z", time.gmtime(time.time()))

    # Generate message
    msg = "Date: %s\nFrom: %s\nReply-To: %s\nTo: %s\n" \
          "Subject: %s\n\n%s" % (date, GLOBAL['sender'], GLOBAL['sender'], \
                                  GLOBAL['recipient'], GLOBAL['subject'], \
                                  GLOBAL['message'])

    # Do it
    server.sendmail(GLOBAL['sender'], GLOBAL['recipient'], msg)

    # Goodbye
    server.quit()

  # Error sending mail
  except:
    
    # Redirect to error URL
    return 0

  # OK
  return 1

#
# Check email form; values
# Generate message
#
def ProcessForm():

  # Global namespace
  global GLOBAL

  # Put all values from form into global variable
  for FormKey in form.keys():
    GLOBAL[FormKey] = form[FormKey][0]
  
  #
  # Check values from form
  #

  #
  # Set value(s) to standard values, if not given
  #

  # If we don't have a subject
  if not form.has_key('subject'):
    GLOBAL['subject'] = 'Contact from your website'

  # If we don't have redirect_ok
  if not form.has_key('redirect_ok'):
    GLOBAL['redirect_ok'] = m.env('HTTP_REFERER')

  # If we don't have redirect_error
  if not form.has_key('redirect_error'):
    GLOBAL['redirect_error'] = 'http://cgi.bensmann.de/email_error.html'

  # If we don't have redirect_missing_fields
  if not form.has_key('redirect_missing_fields'):
    GLOBAL['redirect_missing_fields'] = 'http://cgi.bensmann.de/email_missing_fields.html'

  # If we don't have a sender
  if not form.has_key('sender'):
    GLOBAL['sender'] = '"Contact formular service" <email@bensmann.de>'

  # If we don't have a recipient
  if not form.has_key('recipient'):
    GLOBAL['recipient'] = '"Contact formular service" <email@bensmann.de>'

  #
  # Check required values
  #
  if GLOBAL.has_key('required'):

    # Split fields to search
    Required = string.split(GLOBAL['required'], ',')

    # Go through
    for Test4Required in Required:

      # Is the required field there?
      if not GLOBAL.has_key(string.strip(Test4Required)):

        # Found, but without value, so goto missing_fields URL
        HTML.refresh(GLOBAL['redirect_missing_fields'])
        sys.exit()

  #
  # Check values to ignore
  # Add them to ProtectedKeys
  #
  for Test4Ignore in GLOBAL.keys():

    # Search for 'ignore_' in field name
    if string.find(Test4Ignore, 'ignore_') != -1:

      # Found, so add it to ProtectedKeys
      ProtectedKeys.append(Test4Ignore)

#
# Generate email
#
def GenerateEMail():
  
  # Sort global namespace
  Sorted_GLOBAL = []
  Sorted_GLOBAL = GLOBAL.keys()
  Sorted_GLOBAL.sort()

  #
  # Generate message with all fields from form, which are not in ProtectedKeys
  #
  for field in Sorted_GLOBAL:

    # Add key/value-pair to message if key is not in ProtectedKeys
    if field not in ProtectedKeys:
      GLOBAL['message'] = '%s\n%s: %s' % (GLOBAL['message'], string.ljust(string.strip(field), 40), GLOBAL[field])

  #
  # Attach enviroment report
  #
  if form.has_key('env_report'):

    # Add header
    GLOBAL['message'] = '\n\n%s\n\nEnviroment Report:\n' % GLOBAL['message']

    # Split
    EnvKeys = string.split(GLOBAL['env_report'], ',')

    # Show every key with its value
    for EK in EnvKeys:
      GLOBAL['message'] = '%s%s:\t\t\t%s\n' % (GLOBAL['message'], string.strip(EK), m.env(EK))

#####################################################################
#
# M A I N
#
#####################################################################

#
# I M P O R T  M O D U L E S
#
import sys
import os
import string
import time
import rfc822
import smtplib

import misc

#
# Make instances
#

# Misc
m = misc.MISC()

# CGI
CGI = misc.CGI()

# Get CGI form
form, e1, e2 = CGI.processForm()
if not form:
  print 'ERROR'
  sys.exit()

# HTML
HTML = misc.HTML()

# Process email; check given values
try:
  
  # Go through form and generate mail
  ProcessForm()
  GenerateEMail()

  # Try to send mail
  if sendmail():

    # OK
    HTML.refresh(GLOBAL['redirect_ok'])
    sys.exit()

  # Error, goto error URL
  else:
    HTML.refresh(GLOBAL['redirect_error'])
    sys.exit()

# NameError: form
except NameError, msg:

  # Print error message
  print 'NameError with %s!' % msg
