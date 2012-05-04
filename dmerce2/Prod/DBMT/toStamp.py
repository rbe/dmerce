#!/usr/bin/env python
##

import time
import string
import iso2Date2
import getopt
import sys

# Read an get options and arguements from command-line
try:
  optlist, args = getopt.getopt(sys.argv[1:], \
  'f:k:u:p:k:',['db_name=','host=', 'user=', 'userpass=' , 'field=' , 'IDField=' , 'tblName='])

  optdict = {}

  for i in optlist:
    optdict[i[0]] = i[1]

  if not optdict.has_key('--db_name'):
    optdict['--db_name'] = 'mysql'

  if not optdict.has_key('--host'):
    optdict['--host'] = 'localhost'

  if not optdict.has_key('--tblName'):
    optdict['--tblName'] = ''

  if not optdict.has_key('--field'):
    optdict['--field'] = ''

except getopt.error, msg:
  
  # Print error message
  print 'Error parsing arguments: %s' %msg  
  # Exit
  sys.exit()


# Put data out of dict into variables
dbName = optdict['--db_name']
host = optdict['--host']
user = optdict['--user']
passwd = optdict['--userpass']
fieldName = optdict['--field']
tblName = optdict['--tblName']
IDField = optdict['--IDField']

iso = iso2Date2.isoDate()

#
# Read the data
#
read = []
db = iso2Date2.queryDB(dbName , host , user , passwd)
tables = db.showTables()
new_tables = []
for h in range(len(tables)):
  if db.readData(fieldName, tables[h][0], IDField):
    read = read + db.readData(fieldName , tables[h][0], IDField)
    new_tables.append(tables[h][0])
    length = len(read)
    timestamp = []
    for i in range(length):
      sep = read[i:][0]
      stamp = iso.convert(sep)
      timestamp = timestamp + [stamp]
      i = i + 1
      db.update(tables[h][0],fieldName)
    for i in range(length):
      stamp = float(timestamp[i])
      ID = int(read[i][1])
      db.change(tables[k][0] , fieldName , ID , stamp)
      i = i + 1
    db.toDouble(tables[k][0], fieldName)  
      
print read
print 'New TableList begins here:   ' , new_tables

"""
#
# Convert data into timestamp 
#
i = 0 # Schleifenzaehler



for i in range(length):
  sep = read[i:][0]
  stamp = iso.convert(sep)
  timestamp = timestamp + [stamp]
  i = i + 1

#
# Change fieldtype
#
db.update(tblName,fieldName)




#
# Update DB with timestamp
#
i = 0  #Schleifenzaehler

for i in range(length):
  stamp = float(timestamp[i])
  ID = int(read[i][1])
  db.change(tblName , fieldName , ID , stamp)
  i = i + 1
  
db.toDouble(tblName,fieldName)
"""

