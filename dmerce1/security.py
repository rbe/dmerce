#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 1.2.7.1 $
#
# Revision 1.1  2000-07-04 16:27:01+02  rb
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
  import random
except:
  print '[security: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit

#
# Generate random, secure passwords
#
class password:

  #
  # Contructor
  #
  def __init__(self):
    
    # Allowed values for ASCII chars:

    # 33-47: !"#$%&'()*+,-.
    self.RangeSpecial = (33, 47)
    
    # 48-57: 0..9
    self.RangeDigits = (48, 57)
    
    # 65..89: A..Z
    self.RangeUCChars = (65, 89)
    
    # 97..122: a..z
    self.RangeLCChars = (97, 122)

  #
  # Return random special character (space!"#$%&'()*+,-.)
  #
  def special(self):
    return random.randint(self.RangeSpecial[0], self.RangeSpecial[1])

  #
  # Return random digit (0 .. 9)
  #
  def digit(self):
    return random.randint(self.RangeDigits[0], self.RangeDigits[1])

  #
  # Return random upper case character (A .. Z)
  #
  def ucchar(self):
    return random.randint(self.RangeUCChars[0], self.RangeUCChars[1])

  #
  # Return random lower case character (A .. Z)
  #
  def lcchar(self):
    return random.randint(self.RangeLCChars[0], self.RangeLCChars[1])

  #
  # Generate secure password
  #
  def generate(self, **opt):
  
    # Password
    pwd = ''

    # Length of passwords to generate
    # Default to 8    
    self.PWD_LEN = 8

    # Has opt a key named 'len'?
    if opt.has_key('len'):
      if type(opt['len']) is types.IntType:
        self.PWD_LEN = opt['len']

    # Type of password to generate
    # L = low
    # M = medium
    # H = high
    # P = phonetic

    # Default to 'H'
    self.PWD_TYPE = 'H'

    # Has opt a key named 'type'?
    if opt.has_key('type'):
      if type(opt['type']) is types.StringType:
        self.PWD_TYPE = opt['type']

    # Generate password with length PWD_LEN
    for i in range(self.PWD_LEN):

      # HIGH, digits, lower case chars, upper case chars, special chars
      if string.upper(self.PWD_TYPE) is 'H':
        which = random.randint(1,4)
      
      # MEDIUM, digits, lower case chars, upper case chars
      elif string.upper(self.PWD_TYPE) is 'M':
        which = random.randint(1,3)
        
      # LOW, only lower and upper case chars
      elif string.upper(self.PWD_TYPE) is 'L':
        which = random.randint(2,3)

      # 1: get a random digit
      if which == 1:
        R = self.digit()
        
      # 2: get a random upper case character
      elif which == 2:
        R = self.ucchar()

      # 3: get a random lower case character
      elif which == 3:
        R = self.lcchar()
      
      # 4: get a random special character
      elif which == 4:
        R = self.special()
      
      # Add random digit/character to password
      pwd = '%s%s' % (pwd, chr(R))
      
    # Return generated password
    return pwd

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

  p = password()
  print 'TYPE=H', p.generate(type='H')
  print 'TYPE=M', p.generate(type='M')
  print 'TYPE=L', p.generate(type='L')
