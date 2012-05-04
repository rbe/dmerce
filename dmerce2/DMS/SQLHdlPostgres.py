#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 2.39 $
#
# Revision 1.1  2000-10-03 15:03:08+02  rb
# Initial revision
#
##

#import sys
#sys.path.append('/export/home/r/rb/dmerce2')
import types
import string
import psycopg
import Core.Log
import Core.OS
import DMS.Statement

class Connection:

    """ handle a connction to a oracle database server """

    def __init__(self, dbHost, dbName, dbUser, dbPwd):
        self.SetType('POSTGRESQL')
        self.__dbHost = dbHost
        self.__dbName = dbName
        self.__dbUser = dbUser
        self.__dbPwd = dbPwd
        self.__log = Core.Log.File(module = '1[PostgreSQL].Connection')

    def Connect(self):
        """ connect to database """
        try:
            dsn = 'host=%s dbname=%s user=%s password=%s' % (self.__dbHost, self.__dbName,
                                                             self.__dbUser, self.__dbPwd)
            self.__log.Write(msg = 'CONNECT TO POSTGRESQL WITH DSN=%s' % dsn)
            self.connection = psycopg.connect(dsn, serialize = 0)
        except psycopg.OperationalError, msg:
            self.Exception(msg)

    def Commit(self):
        self.connection.commit()

    def Close(self):
        """ close connection to database """
        self.__cursor.close()
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
        self.__log.Write(submodule = 'Connect', msgType = 'ERROR', msg = '%s' % msg)
        raise Core.Error.OperationalError(msg, '')

class UpperDict:

    """ a dictionary that converts all keys to upper case """

    def __init__(self):
        self.__d = {}

    def __setitem__(self, item, value):
        self.__d[string.upper(item)] = value

    def __getitem__(self, item):
        return self.__d[string.upper(item)]

    def __str__(self):
        return str(self.__d)

    def has_key(self, k):
        return self.__d.has_key(k)

    def keys(self):
        return self.__d.keys()

class Query:

    """ execute queries on a database table """

    def __init__(self, db):
        """
        takes a class instance (derived from instance of class Handler)
        and a table name as argument
        """
        self.__db = db
        self.__osBase = Core.OS.Base()
        self.__log = Core.Log.File(debug = 1, module = '1[PostgreSQL].Query')

    def Commit(self):
        self.__db.conn.Commit()

    def GetCursor(self):
        return self.__db.cur

    #def ExecStoredProc(self, name, *param):
    #    return apply(self.__db.cur.callproc, name, param)

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
            s = stmt.Get()
        else:
            s = stmt
        rowCount, result = self.__db[s]
        if result:
            return rowCount, self.AssignFields(stmt, result)
            #return rowCount, result
        else:
            return None, []

    def GetTableHandler(self, schema = '', table = ''):
        return TableHandler(self.__db, schema, table)

    def GetGrantHandler(self, schema = '', table = '', uxsrole = ''):
        return GrantHandler(self.__db, schema, table, uxsrole)

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

    def AssignFields(self, stmt, result):
        """ create a dictionary and assign values from result rows with field name """
        d = []
        for row in range(len(result)):
            # Append a dictionary for field:value-pairs of one row to list of rows
            ud = UpperDict()
            # Assign every field from row with it's field name
            for field in range(len(result[row])):
                curdesc = self.__db.cur.description[field]
                #print curdesc
                tf = '%s' % string.upper(curdesc[0])
                ty = curdesc[1]
                length = curdesc[2]
                prec = curdesc[3]
                content = result[row][field]
                #msg = 'CONVERTED VALUE: NAME=%s TYPE=%s LEN=%s PREC=%s CONTENT=%s' \
                #      % (str(tf), str(ty), str(length), str(prec), content)
                #self.__log.Write(msg = msg)
                #print msg, type(content)
                if ty == 25 or ty == 1043 or ty == 1082:
                    if not content:
                        content = ''
                if ty == 1700:
                    if content:
                        s = str(content)
                        i, r = s.split('.')
                        content = int(i)
                    else:
                        content = 0
                if ty == 1082:
                    if content:
                        if self.__osBase.Env('DMERCE_LANG') == 'german':
                            tconv = DMS.Time.Conversion({})
                            content = tconv.IsoToGerman(str(content))[:10]
                        else:
                            content = str(content)[:10]
                    else:
                        content = ''
                val = content
                ud[tf] = val
            d.append(ud)
        return d

    def CountOf(self, table, cond = '1'):
        """ get count of rows in a table (matching condition) """
        count = 0
        if string.find(table, '__') > 0:
            schema, table = table.split('__')
            #stmt = "SELECT COUNT(*) FROM %s.%s" % (schema, table)
        stmt = "SELECT COUNT(*) FROM %s" % table
        if cond:
            stmt = "%s WHERE %s" % (stmt, cond)
        rc, r = self.Get(stmt)
        # self.__log.Write(msg = 'COUNT OF STMT=%s RESULT=%s' % (stmt, str(r[0])))
        if r[0]['count']:
            count = r[0]['count']
        return count

    def GetMaxId(self, schema = None, table = '', field = 'ID'):
        """ get maximal ID from table """
        id = -1
        m = 'MAX(%s)' % field
        if schema:
            table = schema + '.' + table
        rc, r = self.__db["SELECT %s FROM %s" % (m, table)]
        try:
            if r[0][0] != None:
                id = int(r[0][0])
        except:
            pass
        return id

    def GetType(self):
        """ returns type of current database """
        return self.__db.conn.GetType()

    def GetHost(self):
        return self.__db.conn.GetHost()

    def GetName(self):
        """ returns name of current database """
        return self.__db.conn.GetName()

    def GetUser(self):
        """ returns user of current database connection """
        return self.__db.conn.GetUser()

class Handler:

    """ postgresql database specific handler """

    def __init__(self, dbHost, dbName, dbUser, dbPwd):
        self.__dbHost = dbHost
        self.__dbName = dbName
        self.__dbUser = dbUser
        self.__dbPwd = dbPwd
        self.__log = Core.Log.File(debug = 1, module = '1[PostgreSQL].Handler/' + dbName)

    def Connect(self):
        """ connect to database """
        self.conn = Connection(self.__dbHost, self.__dbName, self.__dbUser, self.__dbPwd)
        self.conn.Connect()
        self.cur = self.conn.GetCursor()
        self.cur.autocommit()

    def Commit(self):
        self.conn.Commit()

    def Close(self):
        """ close connection to database """
        self.conn.Close()

    def __getitem__(self, stmt):
        """ send a query to database; catch exceptions; return result """
        result = []
        rc = 0
        stmt = stmt.strip()
        try:
            w = self.cur.execute(stmt)
            #self.Commit()
            if stmt[:6] == 'SELECT':
                #result = self.cur.dictfetchall()
                result = self.cur.fetchall()
            else:
                result = []
            rc = len(result)
            #self.__log.Write(msg = 'EXECUTING STMT=%s, RC=%s, R=%s' % (stmt, rc, result))
            return rc, result
        except psycopg.DatabaseError, msg:
            self.ExceptionDatabase(msg, ' STMT=' + stmt)
        except psycopg.OperationalError, msg:
            self.ExceptionOperational(msg, ' STMT=' + stmt)
        except psycopg.IntegrityError, msg:
            self.ExceptionIntegrity(msg, ' STMT=' + stmt)
        except psycopg.DataError, msg:
            raise Core.Error.DataError(msg, ' STMT=' + stmt)
        except psycopg.Warning, msg:
            raise Core.Error.DBWarning(1, msg + ' STMT=' + stmt)

    def ExceptionDatabase(self, msg, stmt):
        raise Core.Error.OperationalError(msg, '')

    def ExceptionOperational(self, msg, stmt):
        raise Core.Error.OperationalError(msg, '')

    def ExceptionIntegrity(self, msg, stmt):
        raise Core.Error.IntegrityError(msg, '')

class TableHandler:

    """ handle a table """

    def __init__(self, db, schema = '', table = ''):
        self.__db = db
        self.__schema = schema
        self.__table = table
        self.__fieldProperties = {}
        self.__log = Core.Log.File(debug = 1, module = '1[PostgreSQL].TableHandler')

    def Describe(self):
        """ get and save table properties """
        if string.find(self.__table, '__') > 0:
            schema, table = string.split(self.__table, '__') # do not overwrite orig var
            table = string.upper(table)
        else:
            schema = self.__schema
            table = self.__table
        stmt = """SELECT a.attname AS field,
                       t.typname AS type,
                       a.attlen AS length,
                       a.atttypmod AS lengthvar,
                       a.attnotnull AS notnull
                  FROM pg_class c,
                       pg_attribute a,
                       pg_type t
                 WHERE c.relname = '%s'
                       and a.attnum > 0
                       and a.attrelid = c.oid
                       and a.atttypid = t.oid
                 ORDER BY a.attnum""" % table.lower()
        rc, r = self.__db[stmt]
        #self.__log.Write(msg = 'STMT=%s RC=%s' % (stmt, rc))
        for row in r:
            colName = row[0]
            colType = row[1]
            colLength = row[2]
            colLengthVar = row[3]
            msg = 'DESCRIBING %s: FIELD=%s TYPE=%s LENGTH=%s LENGTHVAR=%s' \
                  % (self.__table, colName, colType, colLength, colLengthVar)
            self.__log.Write(msg = msg)
            #print msg
            if colLength == -1:
                colLength = colLengthVar - 4
            self.__fieldProperties[colName] = {'Type' : colType,
                                               'Length' : colLength}

    def GetProperties(self):
        """ get properties of all fields """
        return self.__fieldProperties

    def GetFieldProperties(self, field):
        """ get properties of a single field """
        return self.__fieldProperties[field]

    def GetFieldType(self, field):
        """ returns type of field using python's types.* """
        field = string.lower(field)
        ty = self.__fieldProperties[field]['Type']
        length = self.__fieldProperties[field]['Length']
        # self.__log.Write(msg = '%s' % ty)
        if ty[:3] == 'int':
            return types.LongType
        else:
            r = types.StringType
        return r

    def GetRealFieldType(self, field):
        field = string.lower(field)
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

    def __init__(self, query, schema = '', table = '', uxsrole = None):
        self.__query = query
        self.__schema = string.upper(schema)
        self.__table = string.upper(table)
        self.__uxsrole = uxsrole
        self.__allTabPrivs = {}
        self.__allColPrivs = {}
        self.__log = Core.Log.File(debug = 1, module = '1[PostgreSQL].GrantHandler')

    def DiscoverTab(self):
        return 1

    """
        stmt = "SELECT table_name, privilege" \
               "  FROM dmerce_sys.tabperms" \
               " WHERE table_name = '%s'" % self.__table
        if self.__uxsrole:
            stmt = stmt + " AND uxsrole = '%s'" % self.__uxsrole
        self.__log.Write(msg = 'DISCOVERING TABLE PRIVILEGES STMT=%s' % stmt)
        rc, r = self.__query[stmt]
        if rc:
            for row in r:
                self.__allTabPrivs[row[1]] = 1
            return 1
        else:
            return 0
    """

    def DiscoverCol(self):
        return 1
    
    """
        stmt = "SELECT column_name, privilege" \
               "  FROM dmerce_sys.colperms" \
               " WHERE table_name = '%s'" % self.__table
        if self.__uxsrole:
            stmt = stmt + " AND uxsrole = '%s'" % self.__uxsrole
        self.__log.Write(msg = 'DISCOVERING TABLE-COLUMN PRIVILEGES STMT=%s' % stmt)
        rc, r = self.__query[stmt]
        if rc:
            for row in r:
                if not self.__allColPrivs.has_key(row[0]):
                    self.__allColPrivs[row[0]] = {}
                self.__allColPrivs[row[0]][row[1]] = 1
            return 1
        else:
            return 0
    """

    def Discover(self):
        """ discover grants to user of connection """
        rt = self.DiscoverTab()
        rc = self.DiscoverCol()

    def Get(self):
        return self.__allTabPrivs, self.__allColPrivs

    def hasPrivilegeOnTable(self, priv):
        """ has user privilege priv on table (and/or column)? """
        return 1
    """
        r = None
        priv = string.upper(priv)
        if self.__allTabPrivs.has_key(priv):
            r = 1
        self.__log.Write(msg = 'CHECKING PRIVILEGE=%s ON TABLE=%s.%s R=%s'
                         % (priv, self.__schema, self.__table, r))
        return r
    """

    def hasPrivilegeOnColumn(self, priv, col):
        return 1
    """
        r = None
        priv = string.upper(priv)
        col = string.upper(col)
        if self.__allColPrivs.has_key(col):
            c = self.__allColPrivs[col]
            if c.has_key(priv):
                r = 1
        self.__log.Write(msg = 'CHECKING PRIVILEGE=%s ON COLUMN=%s.%s.%s R=%s'
                         % (priv, self.__schema, self.__table, col, r))
        return r
    """
