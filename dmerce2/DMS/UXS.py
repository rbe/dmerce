#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 1.93 $'
#
##
import types
import string
import Core.Log
import DMS.MBase

class Role(DMS.MBase.Class):

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__uxsRolePrefix = None
        self.__uxsRoleSuffix = None

    def __GetDataFromTemplate(self):
        self.__uxsRolePrefix = self._cgi.GetqVar('qUXSCreateRole_Prefix', None)
        self.__uxsRoleSuffix = self._cgi.GetqVar('qUXSCreateRole_Suffix', None)
        self.__uxsRole = self._cgi.GetqVar('qUXSCreateRole_Role', None)
        self.__schema = self._cgi.GetqVar('qUXSCreateRole_Schema', None)

    def __CreateOracleRole(self, schema, role):
        l1 = DMS.SQL.Layer1(self._prjcfg['DMERCESYSCONNECTSTRING'])
        l1.Init()
        q = l1.GetQuery()
        q["CREATE ROLE %s" % role]
        q["INSERT INTO uxsgroups (id, active, uxsgroup, schema)" \
          "     VALUES (s_uxsgroups.NextVal, 1, '%s', '%s')"
          % (role, schema)]
        c = q.GetCursor()
        c.callproc('dmerce_sys.p_all_colpermcounts', schema, role)
        c.close()
        q.Commit()
        l1.Close()

    def Create(self):
        self.__GetDataFromTemplate()
        if not self.__uxsRole:
            role = self.__uxsRolePrefix + self.__uxsRoleSuffix
        else:
            role = self.__uxsRole
        role = string.upper(role)
        schema = string.upper(self.__schema)
        self._log.Write(msg = 'CREATING ROLE=%s' % role)
        self.__CreateOracleRole(schema, role)
        self._log.Write(msg = 'ROLE %s CREATED' % role)
        return 1

class FixRights(DMS.MBase.Class):

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__tablenames = []
        self.__uxsRolePrefix = {}
        self.__uxsRoleSuffix = {}
        self.__uxsTotalTableGrant = {}
        self.__uxsFieldGrants = {}

    def __GetRealNames(self, tn):
        realTableName = string.lower(string.replace(tn, '__', '.'))
        role = string.lower(self.__uxsRolePrefix[tn] + self.__uxsRoleSuffix[tn])
        return realTableName, role

    def __GetDataFromTemplate(self):
        tablenames = self._cgi.GetqVar('qUXSFixRights_Tablenames', None)
        if not tablenames:
            return 0
        if type(tablenames) is types.StringType:
            tablenames = [tablenames]
        self.__tablenames = tablenames

    def __GetRole(self, tn):
        self.__uxsRolePrefix[tn] = self._cgi.GetqVar('qUXSFixRights_RolePrefix_%s' % tn, None)
        self.__uxsRoleSuffix[tn] = self._cgi.GetqVar('qUXSFixRights_RoleSuffix_%s' % tn, None)

    def __GetTotalTableGrant(self, tn):
        tg = self._cgi.GetqVar('qUXSFixRights_TotalTableGrant_%s' % tn, None)
        if type(tg) is types.StringType:
            tg = [tg]
        self.__uxsTotalTableGrant[tn] = tg

    def __StripFieldGrantName(self, fg, tn):
        fg = string.replace(fg, 'qUXSFixRights_FieldGrant_', '')
        fg = string.replace(fg, tn + '_', '')
        return fg

    def __GetFieldGrants(self, tn):
        fg = self._cgi.RetrieveqVar('qUXSFixRights_FieldGrant_%s' % tn)
        d = {}
        for k in fg.keys():
            v = fg[k]
            k = self.__StripFieldGrantName(k, tn)
            if type(v) is types.StringType:
                v = [v]
            d[k] = v
            self.__uxsFieldGrants[tn] = d

    def __DMLType(self, s):
        dmltype = None
        if s[0] == 'S':
            dmltype = "SELECT"
        elif s[0] == 'I':
            dmltype = "INSERT"
        elif s[0] == 'U':
            dmltype = "UPDATE"
        elif s[0] == 'D':
            dmltype = "DELETE"
        return dmltype

    def __TotalTableGrant(self, tn):
        stmts = []
        realTableName, role = self.__GetRealNames(tn)
        ttg = self.__uxsTotalTableGrant[tn]
        if ttg:
            for grant in ttg:
                s = "GRANT %s ON %s TO %s" % (self.__DMLType(grant[0]), realTableName, role)
                stmts.append(s)
        return stmts

    def __FieldGrants(self, tn):
        stmts = []
        realTableName, role = self.__GetRealNames(tn)
        if self.__uxsFieldGrants.has_key(tn):
            d = self.__uxsFieldGrants[tn]
            for field in d.keys():
                for grant in d[field]:
                    dmltype = self.__DMLType(grant[0])
                    if dmltype in ['INSERT', 'UPDATE', 'DELETE']:
                        s = "GRANT %s(%s) ON %s TO %s" % (dmltype, string.lower(field),
                                                          realTableName, role)
                        stmts.append(s)
        return stmts

    def __RevokeAll(self, tn):
        stmts = []
        realTableName, role = self.__GetRealNames(tn)
        stmts.append("REVOKE ALL ON %s FROM %s" % (realTableName, role))
        return stmts

    def __OracleViewDep(self, tn):
        realTableName, role = self.__GetRealNames(tn)
        schema, tableName = string.split(tn, '__')
        schema = string.upper(schema)
        tableName = string.upper(tableName)
        self.__qOwner["DELETE FROM viewdep"
                      " WHERE referenced_name = '%s'" % tableName]
        self.__qOwner["INSERT INTO viewdep"
                      " SELECT name, referenced_type, referenced_owner, referenced_name"
                      "   FROM user_dependencies"
                      "  WHERE type = 'VIEW'"
                      "    AND (referenced_type = 'TABLE'"
                      "         OR referenced_type = 'VIEW')"
                      "    AND referenced_name = '%s'" % tableName]
        self.__qOwner.Commit()

    def __OracleDmerceSysPerms(self, tn):
        realTableName, role = self.__GetRealNames(tn)
        role = string.upper(role)
        schema, tableName = string.split(tn, '__')
        schema = string.upper(schema)
        self.__qDmerceSys["DELETE FROM tabperms"
                          " WHERE table_schema = '%s'"
                          "   AND table_name = '%s'"
                          "   AND uxsrole = '%s'" % (schema, tableName, role)]
        self.__qDmerceSys["INSERT INTO tabperms"
                          " SELECT table_schema, table_name, privilege, grantee"
                          "   FROM all_tab_privs"
                          "  WHERE table_schema = '%s'"
                          "    AND table_name = '%s'"
                          "    AND grantee = '%s'" % (schema, tableName, role)]
        self.__qDmerceSys["DELETE FROM colperms"
                          " WHERE table_schema = '%s'"
                          "   AND table_name = '%s'"
                          "   AND uxsrole = '%s'" % (schema, tableName, role)]
        self.__qDmerceSys["INSERT INTO colperms"
                          " SELECT table_schema, table_name, column_name, privilege, grantee"
                          "   FROM all_col_privs"
                          "  WHERE table_schema = '%s'"
                          "    AND table_name = '%s'"
                          "    AND grantee = '%s'" % (schema, tableName, role)]
        c = self.__qDmerceSys.GetCursor()
        c.callproc('dmerce_sys.p_colpermcounts', schema, tableName, role)
        c.close()
        self.__qDmerceSys.Commit()

    def __OracleGrant(self, tn, stmts):
        realTableName, role = self.__GetRealNames(tn)
        role = string.upper(role)
        for stmt in stmts:
            self.__qOwner[stmt]
        self.__qOwner.Commit()

    def Oracle(self):
        stmts = []
        self.__GetDataFromTemplate()
        self.__l1Owner = DMS.SQL.Layer1(self._prjcfg['OWNERCONNECTSTRING'])
        self.__l1Owner.Init()
        self.__qOwner = self.__l1Owner.GetQuery()
        self.__l1dmerceSys = DMS.SQL.Layer1(self._prjcfg['DMERCESYSCONNECTSTRING'])
        self.__l1dmerceSys.Init()
        self.__qDmerceSys = self.__l1dmerceSys.GetQuery()
        for tn in self.__tablenames:
            self.__GetRole(tn)
            self.__GetTotalTableGrant(tn)
            self.__GetFieldGrants(tn)
            revokeAllStmts = self.__RevokeAll(tn)
            totalTableGrantStmts = self.__TotalTableGrant(tn)
            fieldGrantStmts = self.__FieldGrants(tn)
            self._log.Write(msg = 'GRANTING PRIVILEGES: REVOKE=%s TOTAL TABLE=%s FIELD GRANT=%s'
                            % (revokeAllStmts, totalTableGrantStmts, fieldGrantStmts))
            self.__OracleGrant(tn, revokeAllStmts + totalTableGrantStmts + fieldGrantStmts)
            self._log.Write(msg = 'PRIVILEGES GRANTED')
            self.__OracleDmerceSysPerms(tn)
            self._log.Write(msg = 'DMERCE_SYS PERMISSIONS UPDATED')
            self.__OracleViewDep(tn)
            self._log.Write(msg = 'DMERCE_SYS VIEW DEPENDENCIES UPDATED')
        self.__l1Owner.Close()
        self.__l1dmerceSys.Close()
        return 1

class AccessRights(DMS.MBase.Class):

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__log = Core.Log.File(module = '1[UXS].AccessRights')

    def StmtOnWhat(self, Chart, MemberID, From):
        if From == 'Area':
            chart1 = 'Agent'
            idField = 'areaid'
        elif From == 'Attribute':
            chart1 = 'Member'
            idField = 'attributeid'
        return """
                SELECT e.ID
                  FROM %s e, %s a
                 WHERE (1 =
                           (SELECT COUNT(*)
                              FROM %sEverything
                             WHERE chart = '%s'
                               AND chartid = %i
                               AND %s = a.ID

                            INTERSECT

                            SELECT COUNT(*)
                              FROM %sEverything
                             WHERE chart = '%s'
                               AND chartid = e.ID
                               AND %s = a.ID))
                    OR (0 =
                           (SELECT COUNT(*)
                              FROM %sEverything
                             WHERE chart = '%s'
                               AND chartid = e.ID))
                 GROUP BY e.ID
                 """ % (Chart, From, From, chart1, MemberID, idField, From, Chart, idField, From, Chart)

    def CheckOneOnWhat(self, Chart, ChartID, MemberID, From):
        stmt = """
        SELECT COUNT(*) AS c
          FROM (%s) t
         WHERE t.id = %i;
         """ % (self.StmtOnWhat(Chart, MemberID, From), ChartID)
        #self.__log.Write(msg = stmt)
        rc, r = self._sql[stmt]
        #self.__log.Write(msg = 'RC=%s R=%s' % (rc, str(r)))
        ret = 0
        if rc:
            if r[0]['c']:
                ret = 1
        return ret

    def CheckOneArea(self, Chart, ChartID, MemberID):
        return self.CheckOneOnWhat(Chart = Chart, ChartID = ChartID, MemberID = MemberID,
                                   From = 'Area')

    def CheckOneAttribute(self, Chart, ChartID, MemberID):
        return self.CheckOneOnWhat(Chart = Chart, ChartID = ChartID, MemberID = MemberID,
                                   From = 'Attribute')

    def CheckListOnWhat(self, Chart, MemberID, From, ReturnName = None):
        stmt = self.StmtOnWhat(Chart, MemberID, From)
        self.__log.Write(msg = stmt)
        rc, r = self._sql[stmt]
        #self.__log.Write(msg = 'RC=%s R=%s' % (rc, str(r)))
        s = ''
        if rc:
            lr = len(r)
            if ReturnName:
                idField = ReturnName
            else:
                idField = Chart + '.id'
            for i in range(lr):
                s = s + idField + '*' + str(r[i]['id'])
                if i < lr - 1:
                    s = s + "|"
        else:
            s = '0*1'
        #self.__log.Write(msg = 'GENERATED SUPERSEARCH=%s' % s)
        return s

    def CheckListArea(self, Chart, MemberID, ReturnName = None):
        return self.CheckListOnWhat(Chart = Chart, MemberID = MemberID, From = 'Area', ReturnName = ReturnName)

    def CheckListAttribute(self, Chart, MemberID, ReturnName = None):
        return self.CheckListOnWhat(Chart = Chart, MemberID = MemberID, From = 'Attribute',
                                    ReturnName = ReturnName)

    def StmtForWho(self, Chart, DID, From):
        if From == 'Area':
            chart1 = 'Agent'
            idField = 'areaid'
        elif From == 'Attribute':
            chart1 = 'Member'
            idField = 'attributeid'
        return """
                SELECT e.ID
                  FROM %s e, %s a
                 WHERE (1 =
                           (SELECT COUNT(*)
                              FROM %sEverything
                             WHERE chart = '%s'
                               AND chartid = e.ID
                               AND %s = a.ID

                            INTERSECT

                            SELECT COUNT(*)
                              FROM %sEverything
                             WHERE chart = '%s'
                               AND chartid = %i
                               AND %s = a.ID))
                    OR (0 =
                           (SELECT COUNT(*)
                              FROM %sEverything
                             WHERE chart = '%s'
                               AND chartid = %i))
                 GROUP BY e.ID
                 """ % (chart1, From, From, chart1, idField, From, Chart, DID, idField, From, Chart, DID)

    def CheckOneForWho(self, Chart, DID, From, ReturnName):
        stmt = """
        SELECT COUNT(*) AS c
          FROM (%s) t
         WHERE t.id = %i;
         """ % (self.StmtForWho(Chart, DID, From), ChartID)
        #self.__log.Write(msg = stmt)
        rc, r = self._sql[stmt]
        #self.__log.Write(msg = 'RC=%s R=%s' % (rc, str(r)))
        ret = 0
        if rc:
            if r[0]['c']:
                ret = 1
        return ret

    def CheckOneMember(self, Chart, ChartID, DID):
        return self.CheckOneForWho(Chart = Chart, ChartID = ChartID, DID = DID,
                                   From = 'Attribute')

    def CheckOneAgent(self, Chart, ChartID, DID):
        return self.CheckOneForWho(Chart = Chart, ChartID = ChartID, DID = DID,
                                   From = 'Area')

    def CheckListForWho(self, Chart, DID, From, ReturnName):
        stmt = self.StmtForWho(Chart, DID, From)
        self.__log.Write(msg = stmt)
        rc, r = self._sql[stmt]
        #self.__log.Write(msg = 'RC=%s R=%s' % (rc, str(r)))
        s = ''
        if rc:
            lr = len(r)
            if ReturnName:
                idField = ReturnName
            else:
                idField = Chart + '.id'
            for i in range(lr):
                s = s + idField + '*' + str(r[i]['id'])
                if i < lr - 1:
                    s = s + "|"
        else:
            s = '0*1'
        #self.__log.Write(msg = 'GENERATED SUPERSEARCH=%s' % s)
        return s

    def CheckListMember(self, Chart, DID, ReturnName = None):
        return self.CheckListForWho(Chart = Chart, DID = DID, From = 'Attribute',
                                    ReturnName = ReturnName)

    def CheckListAgent(self, Chart, DID, ReturnName = None):
        return self.CheckListForWho(Chart = Chart, DID = DID, From = 'Area',
                                    ReturnName = ReturnName)
