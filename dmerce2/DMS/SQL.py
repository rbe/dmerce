#!/export/home/r/rb/dmerce2/ralf/python/bin/python2.2
#/usr/local/bin/python
##
#
# $Author: rb $
revision = "$Revision: 2.92 $"
#
# Revision 1.1  2000-10-03 15:01:10+02  rb
# Initial revision
#
##

#import sys
#sys.path.append('/export/home/r/rb/dmerce2')
#sys.path.append('/export/home/r/rb/dmerce2/ralf/python/lib/python2.2/site-packages')
import vars
vars.vars()
import time
import types
import string
import Core.Log
import Core.Error
import DMS.Statement

class Layer1:

    """
    base class for connections and dealing with database
    under Python DBAPI
    """

    def __init__(self, connectStr = ''):
        self.__dbType = None
        self.__dbHost = None
        self.__dbName = None
        self.__dbUser = None
        self.__dbPwd = None
        self.__connectStr = connectStr
        self.__log = Core.Log.File(debug = 1, module = '1[SQL].Layer1')

    def Init(self):
        """
        initialize:
        - analys connection string
        - load handler
        """
        self.__dbType, self.__dbUser, self.__dbPwd, self.__dbHost, self.__dbName = self.AnalyseConnectStr(self.__connectStr)
        self.__LoadHandler()

    def __LoadHandler(self):
        """ load database handler """
        #a = time.time()
        if string.upper(self.__dbType) == 'MYSQL':
            import DMS.SQLHdlMySQL
            self.dbHandlerModule = DMS.SQLHdlMySQL
            self.dbHandler = DMS.SQLHdlMySQL.Handler(self.__dbHost, self.__dbName,
                                                     self.__dbUser, self.__dbPwd)
        elif string.upper(self.__dbType) == 'ORACLE':
            import DMS.SQLHdlOracle
            self.dbHandlerModule = DMS.SQLHdlOracle
            self.dbHandler = DMS.SQLHdlOracle.Handler(self.__dbHost, self.__dbName,
                                                     self.__dbUser, self.__dbPwd)
        elif string.upper(self.__dbType) == 'POSTGRESQL':
            import DMS.SQLHdlPostgres
            self.dbHandlerModule = DMS.SQLHdlPostgres
            self.dbHandler = DMS.SQLHdlPostgres.Handler(self.__dbHost, self.__dbName,
                                                        self.__dbUser, self.__dbPwd)
        #b = time.time()
        #print 'loadhandler took', b - a
        self.dbHandler.Connect()
        #c = time.time()
        #print 'connect took', c - b

    def AnalyseConnectStr(self, cstr):
        """ analyse connect string (database type:user:password@host:database name) """
        try:
            a, b = string.split(cstr, '@')
            dbType, dbUser, dbPwd = string.split(a, ':')
            dbHost, dbName = string.split(b, ':')
        except:
            raise Core.Error.DatabaseConnectStringError(1, 'ERROR WITH CONNECT STRING: ' + str(cstr))
        return (dbType, dbUser, dbPwd, dbHost, dbName)

    def Connect(self):
        """ open connection to database """
        self.dbHandler.Connect()

    def Close(self):
        """ close connection to database """
        self.dbHandler.Close()

    def GetQuery(self):
        """ returns an instance of a query object """
        return self.dbHandlerModule.Query(self.dbHandler)

    def GetConnectionString(self):
        return self.__connectStr

class DBOID:

    """ handle database object IDs """

    def __init__(self, sqlSys, sql):
        self.__sqlSys = sqlSys
        self.__sql = sql
        self.__log = Core.Log.File(debug = 1, module = '1[SQL].DBOID')

    def GetNextId(self, schema, table):
        stmt = "SELECT ID FROM dmerce_sys.DBOID WHERE D = '%s' AND S = '%s' AND T = '%s'" \
               % (self.__sql.GetName(), schema, table)
        rc, r = self.__sqlSys[stmt]
        #self.__log.Write(msg = 'MySQL/GetNextId: STMT=%s RC=%s R=%s' % (stmt, rc, r))
        if rc:
            return r[0]['ID'] + 1
        else:
            return 0

    def CareForIds(self, schema, table):
        """
        take care of IDs
        the MAX(ID) of a table must be less than the DBOID we want to use
        if MAX(ID) is greater than DBOID, DBOID should be MAX(ID) + 1
        """
        try:
            maxId = self.__sql.GetMaxId(schema = schema, table = table)
        except:
            maxId = -1
        nextId = self.GetNextId(schema, table)
        #self.__log.Write(msg = 'DBOID.CareForIds: TABLE=%s MAXID=%s NEXTID=%s'
        #                 % (table, maxId, nextId))
        if maxId != -1:
            if nextId <= maxId:
                nextId = maxId + 1
        return nextId

    def OracleDboidSequence(self, schema, seq):
        #stmt = "SELECT dmerce_sys.s_dboid_%s.NextVal FROM dual" % seq
        stmt = "SELECT %s.s_%s.NextVal FROM dual" % (schema, seq)
        rc, r = self.__sql[stmt]
        nextval = r[0]['NEXTVAL']
        return nextval

    def CareForOracleSequence(self, schema, table):
        #name = schema + '__' + table
        #name = name[:22]
        name = table
        name = name[:28]
        try:
            stmt = "SELECT MAX(ID) AS c FROM %s.%s" % (schema, table)
            rc, r = self.__sql[stmt]
            maxid = r[0]['c']
            # self.__log.Write(msg = 'STMT=%s, MAXID=%s' % (stmt, maxid))
        except:
            maxid = None
        nextval = self.OracleDboidSequence(schema, name)
        # self.__log.Write(msg = 'STMT=%s, NEXTVAL=%s' % (stmt, nextval))
        if maxid is not None:
            if nextval < maxid:
                self.CreateOracleSequence(schema, table, maxid + 1)
                return nextval
        return nextval

    def CreateOracleSequence(self, schema, table, startWith = 0):
        if not startWith:
            maxid = self.__sql.GetMaxId(schema, table)
            if maxid == -1:
                maxid = 0
            startWith = maxid + 1
        name = table
        stmt = "DROP SEQUENCE %s.s_%s" % (schema, name[:28])
        try:
            rc, r = self.__sql[stmt]
        except:
            pass
        stmt = "CREATE SEQUENCE %s.s_%s" \
               " START WITH %s" \
               " INCREMENT BY 1" \
               " NOCYCLE NOMAXVALUE NOCACHE ORDER" \
               % (schema, name[:28], startWith)
        rc, r = self.__sql[stmt]
        return self.OracleDboidSequence(schema, name[:28])
        
    def PostgreSQLDboidSequence(self, schema, seq):
        #stmt = "SELECT dmerce_sys.s_dboid_%s.NextVal FROM dual" % seq
        stmt = "SELECT nextval('s_%s')" % (seq)
        rc, r = self.__sql[stmt]
        # self.__log.Write(msg = 'STMT=%s R=%s' % (stmt, r))
        nextval = r[0]['nextval']
        return nextval

    def CareForPostgreSQLSequence(self, schema, table):
        #name = schema + '__' + table
        #name = name[:22]
        name = table
        name = name[:28]
        try:
            stmt = "SELECT MAX(ID) AS c FROM %s" % (table)
            rc, r = self.__sql[stmt]
            maxid = r[0]['c']
            self.__log.Write(msg = 'STMT=%s, MAXID=%s' % (stmt, maxid))
        except:
            maxid = None
        nextval = self.PostgreSQLDboidSequence(schema, name)
        # self.__log.Write(msg = 'STMT=%s, NEXTVAL=%s' % (stmt, nextval))
        if maxid is not None:
            if nextval < maxid:
                self.CreatePostgreSQLSequence(schema, table, maxid + 1)
                return nextval
        return nextval

    def CreatePostgreSQLSequence(self, schema, table, startWith = 0):
        if not startWith:
            maxid = self.__sql.GetMaxId(schema, table)
            if maxid == -1:
                maxid = 0
            startWith = maxid + 1
        name = table[:28]
        stmt = "DROP SEQUENCE s_%s" % (name[:28])
        try:
            rc, r = self.__sql[stmt]
        except:
            pass
        stmt = "CREATE SEQUENCE s_%s" \
               " START %s" \
               " INCREMENT 1" \
               % (name[:28], startWith)
        rc, r = self.__sql[stmt]
        # self.__log.Write(msg = 'STMT=%s R=%s' % (stmt, r))
        return self.PostgreSQLDboidSequence(schema, name[:28])
        
    def __getitem__(self, table):
        """
        returns and sets a new OID
        increase DBOID for a table
            - or -
        when (exception, failed UPDATE) no DBOID exists for a table
        we create one by INSERT
        """
        if string.find(table, '__') > 0:
            schema, table = string.split(table, '__')
        else:
            schema = ''
        if self.__sql.GetType() == 'ORACLE':
            try:
                return self.CareForOracleSequence(schema, table)
            except:
                return self.CreateOracleSequence(schema, table)
        elif self.__sql.GetType() == 'POSTGRESQL':
            try:
                return self.CareForPostgreSQLSequence(schema, table)
            except:
                return self.CreatePostgreSQLSequence(schema, table)
        elif self.__sql.GetType() == 'MYSQL':
            nextId = self.CareForIds(schema, table)
            if nextId > 0:
                upd = "UPDATE dmerce_sys.DBOID SET ID = %i WHERE D = '%s' AND S = '%s' AND T = '%s'" \
                      % (nextId, self.__sql.GetName(), schema, table)
                rc, r = self.__sqlSys[upd]
                self.__sqlSys["COMMIT"]
                self.__log.Write(msg = 'UPDATE: %s' % upd)
            else:
                if nextId == 0:
                    nextId = 1
                ins = "INSERT INTO dmerce_sys.DBOID (D, S, T, ID) VALUES ('%s', '%s', '%s', %i)" \
                      % (self.__sql.GetName(), schema, table, nextId)
                rc, r = self.__sqlSys[ins]
                self.__sqlSys["COMMIT"]
                self.__log.Write(msg = 'INSERT: %s' % ins)
                nextId = 1
            return nextId

class Transaction:

    """ handle a transaction """

    def __init__(self, db, transaction = []):
        """ argument is a list of instances of class Statement or strings """
        self.__stmts = []
        self.__transaction = transaction
        self.__db = db

    def Init(self):
        for s in self.__transaction:
            self.Append(self.CheckStatement(s))

    def CheckStatement(self, stmt):
        """ is statement stmt an instance of Statement? """
        if isinstance(stmt, Statement):
            return stmt.Get()
        else:
            return stmt
  
    def Append(self, stmt):
        """ append a statement """
        self.__stmts.append(stmt)

    def Execute(self):
        """ execute every statement """
        for s in self.__stmts:
            self.__db[s]

#a = time.time()
#s = Layer1('PostgreSQL:dmerce_web:dmerce_web@localhost:dmerce_sys')
#b = time.time()
#print 'layer1 took', b - a
#s.Init()
#c = time.time()
#print 'layer1-init took', c - b
#q = s.GetQuery()
#d = time.time()
#print 'layer1-getquery took', d - c
#rc, r = q["SELECT * FROM Configuration"]
#print rc, str(r[0])
#e = time.time()
#print 'query took', e - d
#print 'we took', e - a
#t = q.GetTableHandler('MASY5', 'KONTOVERBINDUNG')
#t.Describe()
#print t.GetFieldType('KONTONR')
