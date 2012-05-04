#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 2.36 $
#
# Revision 1.1  2000-10-03 15:03:08+02  rb
# Initial revision
#
##

import sys
import string
import types
import cPickle
import MySQLdb
import Core.Log
import Core.OS
import DMS.Statement

class Connection:

    """ handle a connction to a mysql database server """

    def __init__(self, dbHost, dbName, dbUser, dbPwd):
        self.__dbType = 'MYSQL'
        self.__dbHost = dbHost
        self.__dbName = dbName
        self.__dbUser = dbUser
        self.__dbPwd = dbPwd
        self.__log = Core.Log.File(module = '1[MySQL].Connection')

    def Connect(self):
        """ connect to database """
        try:
            self.connection = MySQLdb.connect(host = self.__dbHost, db = self.__dbName,
                                              user = self.__dbUser, passwd = self.__dbPwd)
        except MySQLdb.OperationalError, msg:
            self.Exception(msg)

    def Commit(self):
        self.connection.commit()

    def Close(self):
        """ close connectio to database """
        self.connection.close()

    def GetCursor(self):
        self.__cursor = self.connection.cursor()
        return self.__cursor

    def SetType(self, t):
        """ set type of database """
        self.__dbType = t

    def GetType(self):
        """ get type of database """
        return self.__dbType

    def SetUser(self, u):
        """ set user for database connection """
        self.__dbUser = u

    def GetUser(self):
        """ get user for database connection """
        return self.__dbUser

    def SetHost(self, h):
        """ set database host """
        self.__dbHost = h

    def GetHost(self):
        """ get database host """
        return self.__dbHost

    def SetName(self, n):
        """ set database base name """
        self.__dbName = n

    def GetName(self):
        """ get database base name """
        return self.__dbName

    def Exception(self, msg):
        self.__log.Write(submodule = 'Connect', msgType = 'ERROR',
                         msg = '%s %s' % (msg[0], msg[1]))
        if msg[0] == 1045:
            # 1045, Access denied
            raise Core.Error.UserAccessDeniedError(msg[0], msg[1])
        if msg[0] == 1049:
            # 1049, Unknown database
            raise Core.Error.UnknownDatabaseError(msg[0], msg[1])
        elif msg[0] == 1109:
            # 1109, Table not found in WHERE clause
            raise Core.Error.UnknownTableError(msg[0], msg[1])
        elif msg[0] == 2005:
            # 2005, Unknown server
            raise Core.Error.UnknownServerError(msg[0], msg[1])
        else:
            raise Core.Error.OperationalError(msg[0], msg[1])

class Query:

    """ execute queries on a database table """

    def __init__(self, db):
        """
        takes a class instance (derived from instance of class Handler)
        and a table name as argument
        """
        self.__db = db
        self.__osBase = Core.OS.Base()
        self.__log = Core.Log.File(debug = 1, module = '1[MySQL].Query')

    def Commit(self):
        self.__db.conn.Commit()

    def __getitem__(self, stmt):
        return self.Get(stmt)

    def Get(self, stmt):
        """
        query database
        - takes a class instance (derived from Statement) as argument,
          if no instance from Statement is given we make one
        - return rowcount and list of rows with dictionary of fields
        """
        if isinstance(stmt, DMS.Statement.Statement):
            rowCount, result = self.__db[stmt.Get()]
        else:
            rowCount, result = self.__db[stmt]
        if result:
            return rowCount, self.AssignFields(stmt, result)
        else:
            return None, []

    def GetTableHandler(self, schema = '', table = ''):
        return TableHandler(self.__db, schema, table)

    def GetGrantHandler(self, schema = '', table = '', uxsrole = None):
        return GrantHandler(self.__db, schema, table)

    def AssignFields(self, stmt, result):
        """ create a dictionary and assign values from result rows with field name """
        d = []
        for row in range(len(result)):
            # Append a dictionary for field:value-pairs of one row to list of rows
            d.append({})
            # Assign every field from row with it's field name
            for field in range(len(result[row])):
                tf = '%s' % self.__db.cur.description[field][0]
                ty = self.__db.cur.description[field][1]
                rf = result[row][field]
                # self.__log.Write(msg = 'VALUE=(%s %s %s)' % (tf, str(ty), rf))
                if ty == 3:
                    if rf is None:
                        d[row][tf] = 0
                    else:
                        d[row][tf] = int(rf)
                elif ty == 0 or ty == 4 or ty == 5:
                    if rf is None:
                        d[row][tf] = 0.0
                    else:
                        d[row][tf] = float(rf)
                elif ty == 10:
                    if rf:
                        if self.__osBase.Env('DMERCE_LANG') == 'german':
                            tconv = DMS.Time.Conversion({})
                            d[row][tf] = tconv.IsoToGerman(str(rf))[:10]
                        else:
                            d[row][tf] = str(rf)[:10]
                    else:
                        d[row][tf] = ''
                elif ty == 12:
                    if rf:
                        if self.__osBase.Env('DMERCE_LANG') == 'german':
                            tconv = DMS.Time.Conversion({})
                            d[row][tf] = tconv.IsoToGerman(str(rf))
                        else:
                            d[row][tf] = str(rf)
                    else:
                        d[row][tf] = ''
                elif ty == 252 or ty == 253 or ty == 254:
                    s = str(rf)
                    if s != 'None':
                        d[row][tf] = s
                    else:
                        d[row][tf] = ''
        return d

    def CountOf(self, table, cond = '1'):
        """ get count of rows in a table (matching condition) """
        count = 0
        if string.find(table, '__') > 0:
            schema, table = string.split(table, '__')
            stmt = "SELECT COUNT(*) FROM %s.%s" % (schema, table)
        else:
            stmt = "SELECT COUNT(*) FROM %s" % table
        if cond:
            stmt = '%s WHERE %s' % (stmt, cond)
        rc, r = self.__db[stmt]
        #self.__log.Write(msg = 'COUNT OF STMT=%s RESULT=%s' % (stmt, r))
        if r[0][0]:
            count = r[0][0]
        return count

    def GetMaxId(self, schema = None, table = '', field = 'ID'):
        """ get maximal ID from table """
        id = -1
        m = 'MAX(%s)' % field
        if schema:
            table = schema + '.' + table
        rc, r = self.__db["SELECT %s FROM %s" % (m, table)]
        if r[0][0] != None:
            id = int(r[0][0])
        return id

    def GetName(self):
        """ returns name of current database """
        return self.__db.conn.GetName()

    def GetType(self):
        return self.__db.conn.GetType()

class Handler:

    """ mysql database specific handler """

    def __init__(self, dbHost, dbName, dbUser, dbPwd):
        self.__dbHost = dbHost
        self.__dbName = dbName
        self.__dbUser = dbUser
        self.__dbPwd = dbPwd
        self.__log = Core.Log.File(debug = 1, module = '1[MySQL].Handler/' + dbName)

    def Connect(self):
        """ connect to database """
        self.conn = Connection(self.__dbHost, self.__dbName, self.__dbUser, self.__dbPwd)
        self.conn.Connect()
        self.cur = self.conn.GetCursor()

    def Commit(self):
        self.conn.Commit()

    def Close(self):
        """ close connection to database """
        self.conn.Close()

    def __getitem__(self, stmt):
        """ send a query to database; catch exceptions; return result """
        result = []
        try:
            self.cur.execute(stmt)
            if self.cur.rowcount > 0:
                result = self.cur.fetchall()
            return self.cur.rowcount, result
        except MySQLdb.OperationalError, msg:
            self.ExceptionOperational(msg, stmt)
        except MySQLdb.IntegrityError, msg:
            self.ExceptionIntegrity(msg, stmt)
        except MySQLdb.DataError, msg:
            raise Core.Error.DataError(msg[0], msg[1] + ' STMT=' + stmt)
        except MySQLdb.ProgrammingError, msg:
            raise Core.Error.ProgrammingError(1, msg[1] + ' STMT=' + stmt)
        except MySQLdb.Warning, msg:
            raise Core.Error.DBWarning(1, msg[0] + ' STMT=' + stmt)

    def PickleResult(self, result, name):
        """ pickle a result of a query """
        output = open(name, 'w')
        cPickle.dump(result, output)
        output.close()

    def UnpickleResult(self, result, name):
        """ unpickle result of query and return it """
        input = open(name, 'r')
        up = cPickle.load(input)
        input.close()
        return up

    def ExceptionOperational(self, msg, stmt):
        if msg[0] == 1045:
            # 1045, Access denied
            raise Core.Error.UserAccessDeniedError(msg[0], msg[1] + ' ' + stmt)
        if msg[0] == 1046:
            # 1046, No database selected
            raise Core.Error.NoDatabaseSelectedError(msg[0], msg[1] + ' ' + stmt)
        elif msg[0] == 1050:
            # 1050, Table already exists
            raise Core.Error.TableExistsError(msg[0], msg[1] + ' ' + stmt)
        elif msg[0] == 1051:
            # 1051, Unknown table
            raise Core.Error.UnknownTableError(msg[0], msg[1] + ' ' + stmt)
        elif msg[0] == 1054:
            # 1054, Unknown column xy in where clause
            raise Core.Error.UnknownColumnInWhereClauseError(msg[0], msg[1] + ' ' + stmt)
        elif msg[0] == 1064:
            # 1064, Syntax error
            raise Core.Error.SQLSyntaxError(msg[0], msg[1] + ' ' + stmt)
        elif msg[0] == 1109:
            # 1109, Table not found in WHERE clause
            raise Core.Error.UnknownTableError(msg[0], msg[1] + ' ' + stmt)
        elif msg[0] == 1146:
            # 1146, Table not found
            raise Core.Error.UnknownTableError(msg[0], msg[1] + ' ' + stmt)
        else:
            raise Core.Error.OperationalError(msg[0], msg[1] + ' ' + stmt)

    def ExceptionIntegrity(self, msg, stmt):
        # 1062, Duplicate entry
        if msg[0] == 1062:
            raise Core.Error.DuplicateEntryError(msg[0], msg[1] + ' ' + stmt)
        else:
            raise Core.Error.IntegrityError(msg[0], msg[1] + ' ' + stmt)

class TableHandler:

    """ handle a table """

    def __init__(self, db, schema = '', table = ''):
        self.__db = db
        self.__schema = schema
        self.__table = table
        self.__fieldProperties = {}
        self.__log = Core.Log.File(debug = 1, module = '1[MySQL].TableHandler')

    def Describe(self):
        """ get and save table properties """
        stmt = 'DESCRIBE '
        if string.find(self.__table, '__') > 0:
            self.__schema, self.__table = string.split(self.__table, '__')
        if self.__schema:
            stmt = stmt + self.__schema + '.'
        stmt = stmt + self.__table
        rc, r = self.__db[stmt]
        for row in r:
            self.__fieldProperties[row[0]] = {'Type' : row[1]}

    def GetProperties(self):
        """ get properties of all fields """
        return self.__fieldProperties

    def GetFieldProperties(self, field):
        """ get properties of a single field """
        return self.__fieldProperties[field]

    def GetFieldType(self, field):
        """ returns type of field using python's types.* """
        f = self.__fieldProperties[field]['Type']
        if string.find(f, 'int') == 0:
            r =  types.IntType
        elif string.find(f, 'float') == 0:
            r =  types.FloatType
        elif string.find(f, 'double') == 0:
            r = types.FloatType
        else:
            r = types.StringType
        #self.__log.Write(msg = 'TYPE OF FIELD %s=%s' % (field, str(f)))
        return r

    def GetRealFieldType(self, field):
        return self.__fieldProperties[field]['Type']

    def Duplicate(self, tableName):
        """ copy structure/data to a new table tableName """
        pass

    def CreateTable(self):
        """ returns create table statement for table """
        pass

class GrantHandler:

    """
    discover and return informations about permissions
    of the current user of connection
    """

    def __init__(self, query, schema = '', table = ''):
        #self.__query = query
        #self.__schema = string.upper(schema)
        #self.__table = string.upper(table)
        #self.__allTabPrivs = {}
        #self.__allColPrivs = {}
        pass

    def Discover(self):
        return 1

    def Get(self):
        return self.__allTabPrivs, self.__allColPrivs

    def hasPrivilegeOnTable(self, priv):
        """ has user privilege priv on table (and/or column)? """
        return 1

    def hasPrivilegeOnColumn(self, priv, col):
        return 1
