#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.9 $'
#
##

import string
import types
import time
import Core.Log
import Core.Error
import DMS.SuperSearch
import DMS.Statement
import DMS.SQL
import DMS.MBase
import DMS.Object

class TableFields(DMS.MBase.Class):

    """
    get all fields like '*_*' from cgi field storage
    and create a dictionary: key = table, value = list of fields
    """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = 'TableFields')
        self.__tables = self.ScanTableField()

    def __SplitName(self, item):
        """
        split name of item with _
        take care of field with names having _ in it
        (how to separate table and field name with _ in it?)
        """
        t = None
        f = None
        l = string.split(item, '_')
        if len(l) > 2:
            t = l[0]
            f = string.join(l[1:], '_')
        else:
            t, f = l
        return t, f
    
    def ScanTableField(self):
        """
        scan for cgi form fields that represent table.field syntax
        """
        d = {}
        #self.__log.Write(msg = 'FIELDS=%s' % self._cgi.GetFieldKeys())
        for item in self._cgi.GetFieldKeys():
            #self.__log.Write(msg = 'CHECKING TABLE.FIELD=%s VALUE=%s' % (item, str(self._cgi.GetField(item))))
            if string.find(item, '_') > 0:
                table, field = self.__SplitName(item)
                #self.__log.Write(msg = 'FOUND FIELD %s FOR TABLE %s' % (f, t))
                if not d.has_key(table):
                    d[table] = {}
                d[table][field] = self._cgi.GetField(item)
        return d

    def __getitem__(self, item):
        try:
            return self.__tables[item]
        except:
            raise Core.Error.PDBATableNotFoundError(1, 'REQUESTED TABLE %s NOT FOUND' % str(item))

    def has_key(self, key):
        return self.__tables.has_key(key)

class PDBA(DMS.MBase.Class):

    """ base class for performing database manipulations """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = 'PDBA')
        self._t = kw['t']
        self._stf = TableFields(kw)

    def CompareGivenFields(self, table):
        """ compare given fields by form with fields in template """
        tmf = []
        gf = []
        cgif = self._cgi.GetFieldKeys()
        if not self._t['GivenFields']:
            return None
        #try:
        for g in string.split(self._t['GivenFields'], ','):
            t, f = string.split(g, '.', 1)
            if t == table:
                gf.append(g)
        for f in gf:
            if f not in cgif:
                tmf.append(f)
        return tmf
        #except:
        #    raise Core.Error.CompareGivenFieldsError()


class Insert(PDBA):

    """ perform insert on database """

    def __init__(self, kw):
        PDBA.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = 'PDBA.Insert')
        self.__insertedIds = {}
        self.__tables = []
        self.__stmts = []
        for table in kw['tables']:
            table = string.replace(table, ' ', '')
            self.__tables.append(table)

    def Generate(self):
        for table in self.__tables:
            self.__log.Write(msg = 'GENERATING INSERT-STATEMENT FOR TABLE %s' % table)
            stmt = DMS.Statement.Statement('INSERT')
            stmt.SetTable(table)
            fields = self._stf[table]
            for field in fields.keys():
                stmt.AddField(field)
                stmt.AddValue(stmt.EscapeSQLValues(fields[field]))
            if not fields.has_key('ID'):
                dbOid = DMS.SQL.DBOID(self._sqlSys, self._sql)
                newId = self.__insertedIds[table] = dbOid[table]
                stmt.AddField('ID')
                stmt.AddValue(newId)
            stmt.AddField('CreatedBy')
            stmt.AddValue(self._sam.GetWho('ID'))
            stmt.AddField('CreatedDateTime')
            stmt.AddValue(time.time())
            self.__stmts.append(stmt)

    def GetInsertedId(self, table):
        """ return inserted ID for table """
        try:
            return self.__insertedIds[table]
        except:
            return None

    def __str__(self):
        return self.__stmts

class Update(PDBA):

    """ perform update on database """

    def __init__(self, kw):
        PDBA.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = 'PDBA.Update')
        self.__tables = []
        self.__stmts = []
        for table in kw['tables']:
            table = string.replace(table, ' ', '')
            self.__tables.append(table)

    def __AddFields(self, fields, stmt):
        """ add fields and their values """
        for field in fields.keys():
            if not field == 'ID':
                #self.__log.Write(msg = 'ADDING FIELD %s=%s FOR UPDATE OF TABLE %s'
                #                 % (field, fields[field], self.__actTable))
                stmt.AddField('%s.%s' % (self.__actTable, field))
                t = self.__tableHandler.GetFieldType(field)
                s = stmt.EscapeSQLValues(fields[field])
                if t is types.IntType:
                    stmt.AddValue(int(s))
                elif t is types.FloatType:
                    stmt.AddValue(float(s))
                elif t is types.StringType:
                    stmt.AddValue(str(s))
        return stmt

    def __AddMissingFields(self, stmt):
        """ add missing fields (from cgi post) """
        mf = self.CompareGivenFields(self.__actTable)
        if mf:
            for f in mf:
                table, field = string.split(f, '.')
                stmt.AddField(f)
                t = self.__tableHandler.GetFieldType(field)
                if t is types.StringType:
                    stmt.AddValue('')
                elif t is types.IntType or t is types.LongType:
                    stmt.AddValue(0)
                elif t is types.FloatType:
                    stmt.AddValue(0.0)
        return stmt

    def __AddStats(self, stmt):
        """
        add statistical informations
        - created/changed datetime/by
        """
        try:
            w = self._sam.GetWho('ID')
            t = time.time()
            #self.__log.Write(msg = 'ADDING FIELD ChangedBy=%s FOR UPDATE OF TABLE %s'
            #                 % (w, self.__actTable))
            stmt.AddField('%s.ChangedBy' % self.__actTable)             ### POSSIBLE ERROR WHEN NOT LOGGED IN
            stmt.AddValue(w)
            #self.__log.Write(msg = 'ADDING FIELD ChangedDateTime=%s FOR UPDATE OF TABLE %s'
            #                 % (t, self.__actTable))
            stmt.AddField('%s.ChangedDateTime' % self.__actTable)
            stmt.AddValue(t)
            return stmt
        except KeyError:
            raise Core.Error.PDBAUpdateNoWhoIDError(1, 'CANNOT GET WHO-ID FOR UPDATE')

    def __GetUpdateCondition(self, sf):
        """
        update condition is either <table>.ID or if that field is not
        present, the condition (qSuperSearch) given in qUpdateWhere is used
        """
        cond = None
        quw = self._cgi.GetqVar('qUpdateWhere')
        quwf = self._cgi.GetqVar('qUpdateWithFields')
        if quw is not None:
            qss = DMS.SuperSearch.SuperSearch(quw)
            cond = qss.ConvertToSQLWhere()
            #self.__log.Write(msg = 'qUpdateWhere: COND=%s' % str(cond))
        elif quwf is not None:
            quwf = string.replace(quw, ' ', '')
            quwf = string.split(quw, ',')
            i = 0
            for f in sf.keys():
                if f in quwf:
                    cond = '%s = "%s"' % (f, sf[f])
                    if i < len(sf):
                        cond = cond + ' AND'
                    i = i + 1
            #self.__log.Write(msg = 'qUpdateWithFields: COND=%s' % str(cond))
        elif sf.has_key('ID'):
            cond = '%s.ID = %s' % (self.__actTable, sf['ID']) ### POSSIBLE ERROR WHEN NO TABLE_ID WAS GIVEN!
            #self.__log.Write(msg = 'qUpdate: COND=%s' % str(cond))
        return cond

    def Generate(self):
        """ generate update statements for all tables that we should update """
        for self.__actTable in self.__tables:
            self.__tableHandler = DMS.SQL.HandleTable(self._sql, self.__actTable)
            self.__tableHandler.Describe()
            self.__log.Write(msg = 'GENERATING UPDATE-STATEMENT FOR TABLE %s' % self.__actTable)
            stmt = DMS.Statement.Statement('UPDATE')
            stmt.SetTable(self.__actTable)
            sf = self._stf[self.__actTable]
            stmt = self.__AddFields(sf, stmt)
            stmt = self.__AddMissingFields(stmt)
            stmt = self.__AddStats(stmt)
            cond = self.__GetUpdateCondition(sf)
            if cond is None:
                self.__log.Write(msg = 'UPDATE-STATEMENT HAS NO WHERE-CONDITION, I WILL NOT EXECUTE IT: STMT=%s'
                                 % str(stmt))
            else:
                stmt.SetCondition(cond)
                self.__stmts.append(stmt)

    def __str__(self):
        return self.__stmts

class Delete(PDBA):

    """ perform delete on database """

    def __init__(self, kw):
        PDBA.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = 'PDBA.Delete')
        self.__tables = []
        self.__stmts = []
        for table in kw['tables']:
            table = string.replace(table, ' ', '')
            self.__tables.append(table)

    def Generate(self):
        for table in self.__tables:
            self.__log.Write(msg = 'GENERATING DELETE-STATEMENT FOR TABLE %s' % table)
            stmt = DMS.Statement.Statement('DELETE')
            stmt.SetTable(table)
            sf = self._stf[table]
            cond = ''
            i = 0
            for f in sf.keys():
                cond = '%s = "%s"' % (f, sf[f])
                if i < len(sf):
                    cond = cond + ' '
                i = i + 1
            stmt.SetCondition(cond)
            self.__stmts.append(stmt)

    def __str__(self):
        return self.__stmts
