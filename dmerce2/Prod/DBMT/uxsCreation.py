#!/usr/local/env python
#
#
#
import sys
import string
import MySQLdb
import getopt


# Read an get options and arguements from command-line
try:
    optlist, args = getopt.getopt(sys.argv[1:],
                                  'f:k:u:p:k:',['db_name=',
                                                'host=',
                                                'user=',
                                                'passwd=' ,
                                                'serverID='])

    optdict = {}

    for i in optlist:
        optdict[i[0]] = i[1]

        if not optdict.has_key('--db_name'):
            optdict['--db_name'] = 'mysql'

        if not optdict.has_key('--host'):
            optdict['--host'] = 'localhost'

        if not optdict.has_key('--serverID'):
            optdict['--serverID'] = ''

        if not optdict.has_key('--passwd'):
            optdict['--passwd'] = ''

        if not optdict.has_key('--user'):
            optdict['--user'] = ''

except getopt.error, msg:
  
    # Print error message
    print 'Error parsing arguments: %s' %msg  
    # Exit
    sys.exit()

# Put data out of dict into variables
#dbName = optdict['--db_name']
#host = optdict['--host']
#user = optdict['--user']
#passwd = optdict['--passwd']

class HandleDB:

    #
    # Method which executes the query
    #
    def ExQuery(self, value):
        query = '%s' % value
        try:
            self.c.execute(query)
        except:
            print 'Error while executing query !'
            pass

    #
    # Method to get max ID from table
    #
    def GetMaxID(self, db, table, idField):
        query = "SELECT MAX(%s) FROM %s.%s" % (idField, db, table)
        try:
            self.c.execute(query)
        except:
            return 0
        maxID = self.c.fetchall()
        maxID = int(maxID)
        return maxID + 1
