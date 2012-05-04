#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 2.62 $
#
# Revision 1.1  2000-10-03 15:03:08+02  rb
# Initial revision
#
##

#import sys
#sys.path.append('/export/home/r/rb/dmerce2')
import types
import string
import DCOracle2
import Core.Log
import DMS.Statement

class Connection:

    """ handle a connction to a oracle database server """

    def __init__(self, dbHost, dbName, dbUser, dbPwd):
        self.SetType('ORACLE')
        self.__dbHost = dbHost
        self.__dbName = dbName
        self.__dbUser = dbUser
        self.__dbPwd = dbPwd
        self.__log = Core.Log.File(module = '1[ORACLE].Connection')

    def Connect(self):
        """ connect to database """
        try:
            dbService = '%s/%s@%s' % (self.__dbUser, self.__dbPwd, self.__dbName)
            #self.__log.Write(msg = 'CONNECTING TO ORACLE AS %s' % dbService)
            self.connection = DCOracle2.connect(dbService)
            #self.__log.Write(msg = 'CONNECT TO ORACLE AS %s=%s' % (dbService, str(self.connection)))
        except DCOracle2.OperationalError, msg:
            self.Exception(msg)
        except DCOracle2.DatabaseError, msg:
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
                         msg = 'ORACLE CONNECT ERROR: %s %s' % (msg[0], msg[1]))
        raise Core.Error.OperationalError(msg[0], msg[1])

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
        self.__log = Core.Log.File(debug = 1, module = '1[ORACLE].Query')

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
            rowCount, result = self.__db[stmt.Get()]
        else:
            rowCount, result = self.__db[stmt]
        if result:
            return rowCount, self.AssignFields(stmt, result)
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
                tf = '%s' % string.upper(curdesc[0])
                ty = curdesc[1]
                length = curdesc[2]
                prec = curdesc[4]
                scale = curdesc[5]
                content = result[row][field]
                # msg = 'CONVERTED VALUE: NAME=%s TYPE=%s LEN=%s PREC=%s SCALE=%s CONTENT=%f' \
                #      % (str(tf), str(ty), str(length), str(prec), str(scale), content)
                # print msg, type(content)
                # self.__log.Write(msg = msg)
                if ty == 'NUMBER':
                    if scale == 0 and prec == 0:
                        if content:
                            val = long(content)
                        else:
                            val = 0
                        #s = str(content)
                        #e = string.find('e+', s)
                        #num, ep = s[:e], s[e + 2:]
                        #for i in range(int(ep)):
                        #    num = num + 
                    elif scale == 0:
                        if content:
                            if prec <= 10:
                                val = int(content)
                            else:
                                val = long(content)
                        else:
                            val = 0
                    elif scale:
                        if content:
                            val = float(content)
                        else:
                            val = 0.0
                else:
                     s = str(content)
                     if s != 'None':
                         val = s
                     else:
                         val = ''
                ud[tf] = val
            d.append(ud)
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
            stmt = "%s WHERE %s" % (stmt, cond)
        rc, r = self.Get(stmt)
        #self.__log.Write(msg = 'COUNT OF STMT=%s RESULT=%s' % (stmt, str(r[0])))
        if r[0]['COUNT(*)']:
            count = r[0]['COUNT(*)']
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

    """ mysql database specific handler """

    def __init__(self, dbHost, dbName, dbUser, dbPwd):
        self.__dbHost = dbHost
        self.__dbName = dbName
        self.__dbUser = dbUser
        self.__dbPwd = dbPwd
        self.__log = Core.Log.File(debug = 1, module = '1[ORACLE].Handler/' + dbName)

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
            rc = 0
            self.__log.Write(msg = 'SQLHdlOracle: EXEC STMT=%s' % stmt)
            if self.cur.execute(stmt) > 0:
                try:
                    result = self.cur.fetchall()
                except:
                    result = []
                rc = len(result)
            self.Commit()
            return rc, result
        except DCOracle2.DatabaseError, msg:
            self.ExceptionDatabase(msg, stmt)
        except DCOracle2.OperationalError, msg:
            self.ExceptionOperational(msg, stmt)
        except DCOracle2.IntegrityError, msg:
            self.ExceptionIntegrity(msg, stmt)
        except DCOracle2.DataError, msg:
            raise Core.Error.DataError(msg[0], msg[1] + ' STMT=' + stmt)
        except DCOracle2.Warning, msg:
            raise Core.Error.DBWarning(1, msg[0] + ' STMT=' + stmt)

    def ExceptionDatabase(self, msg, stmt):
        raise Core.Error.OperationalError(msg[0], msg[1] + ' ' + stmt)

    def ExceptionOperational(self, msg, stmt):
        raise Core.Error.OperationalError(msg[0], msg[1] + ' ' + stmt)

    def ExceptionIntegrity(self, msg, stmt):
        raise Core.Error.IntegrityError(msg[0], msg[1] + ' ' + stmt)

class TableHandler:

    """ handle a table """

    def __init__(self, db, schema = '', table = ''):
        self.__db = db
        self.__schema = schema
        self.__table = table
        self.__fieldProperties = {}
        self.__log = Core.Log.File(debug = 1, module = '1[ORACLE].TableHandler')

    def Describe(self):
        """ get and save table properties """
        if string.find(self.__table, '__') > 0:
            schema, table = string.split(self.__table, '__') # do not overwrite orig var
            table = string.upper(table)
        else:
            schema = self.__schema
            table = self.__table
        stmt = "SELECT column_name, data_type, data_length, data_precision, data_scale" \
               " FROM all_tab_columns" \
               " WHERE "
        if schema:
            stmt = stmt + " owner = '%s' AND" % string.upper(schema)
        stmt = stmt + " table_name = '%s'" % string.upper(table)
        rc, r = self.__db[stmt]
        for row in r:
            colName = row[0]
            colType = row[1]
            colLength = row[2]
            colPrec = row[3]
            colScale = row[4]
            #msg = 'DESCRIBING %s: FIELD=%s TYPE=%s Prec=%s SCALE=%s' \
            #      % (self.__table, colName, colType, colPrec, colScale)
            #self.__log.Write(msg = msg)
            #print msg
            self.__fieldProperties[colName] = {'Type' : colType,
                                               'Length' : colLength,
                                               'Prec' : colPrec,
                                               'Scale' : colScale}

    def GetProperties(self):
        """ get properties of all fields """
        return self.__fieldProperties

    def GetFieldProperties(self, field):
        """ get properties of a single field """
        return self.__fieldProperties[string.upper(field)]

    def GetFieldType(self, field):
        """ returns type of field using python's types.* """
        field = string.upper(field)
        ty = self.__fieldProperties[field]['Type']
        length = self.__fieldProperties[field]['Length']
        prec = self.__fieldProperties[field]['Prec']
        scale = self.__fieldProperties[field]['Scale']
        if ty == 'NUMBER':
            if scale == 0 and prec == 0:
                return types.LongType
            elif not scale:
                if prec <= 10:
                    r = types.IntType
                elif prec > 10:
                    r = types.LongType
            else:
                r = types.FloatType
        else:
            r = types.StringType
        return r

    def GetRealFieldType(self, field):
        field = string.upper(field)
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
        self.__log = Core.Log.File(debug = 1, module = '1[ORACLE].GrantHandler')

    def CreateTab(self):
        stmt = "SELECT COUNT(*) FROM all_tab_privs WHERE"
        if self.__schema:
            stmt = stmt + "schema = '" + self.__schema + "'"
        stmt = stmt + " table_name = '" + self.__table + "'"
        rc, r = self.__query[stmt]
        if rc:
            for priv in ['SELECT', 'INSERT', 'UPDATE', 'DELETE']:
                stmt = "INSERT dmerce_sys.tabperms" \
                       " SELECT table_schema, table_name, privilege" \
                       "   FROM all_tab_privs" \
                       "  WHERE table_schema = '%s'" \
                       "    AND table_name = '%s'" \
                       "    AND privilege = '%s'" % (self.__schema, self.__table, priv)
                rc, r = self.__query[stmt]

    def CreateCol(self):
        stmt = "SELECT COUNT(*) FROM all_col_privs WHERE"
        if self.__schema:
            stmt = stmt + "schema = '" + self.__schema + "'"
        stmt = stmt + " table_name = '" + self.__table + "'"
        rc, r = self.__query[stmt]
        if rc:
            for priv in ['SELECT', 'INSERT', 'UPDATE', 'DELETE']:
                stmt = "INSERT INTO dmerce_sys.tabperms" \
                       " SELECT table_schema, table_name, privilege" \
                       "   FROM all_tab_privs" \
                       "  WHERE table_schema = '%s'" \
                       "    AND table_name = '%s'" \
                       "    AND privilege = '%s'" % (self.__schema, self.__table, priv)
                rc, r = self.__query[stmt]

    def DiscoverTab(self):
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

    def DiscoverCol(self):
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

    def Discover(self):
        """ discover grants to user of connection """
        rt = self.DiscoverTab()
        rc = self.DiscoverCol()

    def Get(self):
        return self.__allTabPrivs, self.__allColPrivs

    def hasPrivilegeOnTable(self, priv):
        """ has user privilege priv on table (and/or column)? """
        r = None
        priv = string.upper(priv)
        if self.__allTabPrivs.has_key(priv):
            r = 1
        self.__log.Write(msg = 'CHECKING PRIVILEGE=%s ON TABLE=%s.%s R=%s'
                         % (priv, self.__schema, self.__table, r))
        return r

    def hasPrivilegeOnColumn(self, priv, col):
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
