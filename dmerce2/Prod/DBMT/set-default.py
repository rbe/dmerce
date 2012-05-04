#!/usr/bin/env python
#

""" Import recent Modules """
try:
    import sys
    import os
    import string
    import MySQLdb
    import getopt
except:
    print 'Errors occured while importing modules !'


class ConnectDB:
    """ Class to make an connection to database """
    def __init__(self,db = '', host = '', user = '', passwd = ''):
        """ Constructor """
        db = MySQLdb.connect(db = db, host = host, user = user, passwd = passwd) 
        self.c = db.cursor()

class DBData(ConnectDB):
    """ DB Handling : Get user and group data """
    def __init__(self, connectStr):
        """ Constructor """
        self.__connectStr = connectStr
        self.AnalyseConnectStr()
        ConnectDB.__init__(self, self.__db, self.__host, self.__user, self.__passwd)

    def AnalyseConnectStr(self):
        """ Analyse connect string """
        try:
            """ analyse connect string (database type:user:password@host:database name) """
            a, b = string.split(self.__connectStr, '@')
            self.__dbType, self.__user, self.__passwd = string.split(a, ':')
            self.__host, self.__db = string.split(b, ':')
        except:
            print 'Error in connection string: ' , self.__connectStr

    def GetTables(self):
        query = 'SHOW TABLES'
        self.c.execute(query)
        return self.c.fetchall()

    def DescribeTable(self, table):
        query = 'DESCRIBE %s' % table
        self.c.execute(query)
        return self.c.fetchall()

    def UpdateTable(self, table, field, default, set_to, type):
        query = 'UPDATE %s ' % table + \
                'SET %s = "%s"' % (field, set_to)
        self.c.execute(query)
        query2 = 'ALTER TABLE %s ' % table + \
                 'CHANGE %s %s ' % (field, field) + \
                 '%s DEFAULT %s NOT NULL' % (type, default)
        self.c.execute(query2)
        
    def AddField(self, table, field, default, type):
        query = 'ALTER TABLE %s ' % table + \
                'ADD %s ' % field + \
                '%s DEFAULT %s NOT NULL' % (type, default)
        self.c.execute(query)
        
class Function:
    def CheckFor(self, value, list, count):
        res = None
        for i in range(len(list)):
            if list[i][count] == value:
                res = 1
        if res:
            return 1

if __name__=='__main__':
    """ Main procedure """
    try:
        optlist, args = getopt.getopt(sys.argv[1:],
                                      'f:k:u:p:k:',['db=',
                                                    'field=',
                                                    'set=',
                                                    'type=',
                                                    'default='])
        optdict = {}
        for i in optlist:
            optdict[i[0]] = i[1]
            if not optdict.has_key('--db'):
                optdict['--db'] = ''
            if not optdict.has_key('--field'):
                optdict['--field'] = ''
            if not optdict.has_key('--set'):
                optdict['--set'] = ''
            if not optdict.has_key('--type'):
                optdict['--type'] = ''
            if not optdict.has_key('--default'):
                optdict['--default'] = ''
    except getopt.error, msg:
        # Print error message
        print 'Error parsing arguments: %s' %msg  
        # Exit
        sys.exit()
    dbase = optdict['--db']
    field = optdict['--field']
    default = optdict['--default']
    set_to = optdict['--set']
    type = optdict['--type']
    if dbase == '' or default == '' or field == '' or set_to == '' or type == '':
        print 'Sorry, you have to give all options'
        print 'Please use it in this form:'
        print 'set-default.py\n\n --db="<database name>" --field="<fieldname>"\n' + \
              ' --type="<type of field>" --default="<default parameter>"\n\n'
        print 'Please try again !'
        sys.exit()
    db = DBData('MySQL:root:X1/yP7@localhost:' + dbase)
    tables = db.GetTables()
    func = Function()
    for i in range(len(tables)):
        """ go through table list an describe them """
        value = db.DescribeTable(tables[i][0])
        if func.CheckFor(field, value, 0):
            """ if field exists set to new value """
            db.UpdateTable(tables[i], field, default, set_to, type)
        else:
            db.AddField(tables[i][0], field, default, type)
