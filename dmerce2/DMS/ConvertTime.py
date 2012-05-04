#!/usr/bin/env python

import types
import time
import string
import vars
import DMS.SQL
import Guardian.Config
import sys
import os

class DBDataMySQL:
    """ constructor """
    def __init__(self, query):
        self.__query = query

    def GetAllTables(self):
        """ now we get all tables from actual database """
        rc, r = self.__query["SHOW TABLES"]
        return r
    
    def DescribeTable(self, table):
        """ we check table for recent columns """
        columnList = []
        rc, r = self.__query["DESCRIBE %s" % table]
        for i in r:
            if i['Type'] == 'double(16,6)':
                columnList.append(i['Field'])
        if 'CreatedDateTime' in columnList and 'CreatedDateTime' in columnList:
            return 1
        else:
            return None
    
    def CheckFieldType(self, field, type):
        rc, r = self.__query[""]
    
    def GetTimeFields(self, table):
        """ get ID, CreatedDateTime and ChangedDateTime data """
        rc, r = self.__query["SELECT ID, CreatedDateTime, ChangedDateTime " \
                             "FROM %s" % table]
        return r

    def ChangeTimeFields(self, table):
        """ changes field type of CreatedDateTime and ChangedDateTime """
        rc, r = self.__query["ALTER TABLE %s MODIFY CreatedDateTime varchar(20)" % table]
        rc, r = self.__query["ALTER TABLE %s MODIFY ChangedDateTime varchar(20)" % table]

    def DeleteData(self, table):
        """ delete all data in CreatedDateTime and ChangedDateTime """
        rc, r = self.__query["UPDATE %s SET CreatedDateTime = 0, ChangedDateTime = 0" % table]

    def ReInsertData(self, data, table):
        c = Convert()
        """ inserts saved data """
        rc, r = self.__query["UPDATE %s SET CreatedDateTime  = '%s', " % (table, c.MakeIso(data["CreatedDateTime"])) + \
                             "ChangedDateTime = '%s' " % c.MakeIso(data["ChangedDateTime"]) + \
                             "WHERE ID = '%s'" % data["ID"]]


class DBDataOracle:
    """ constructor """
    def __init__(self, query):
        """ for this class set global cursor instance """
        self.__query = query

    def GetAllTables(self):
        """ now we get all tables from actual database """
        rc, r = self.__query["SELECT table_name FROM user_tables"]
        return r

    def DescribeTable(self, table):
        ok = 0
        """ we check table for recent columns """
        columnList = []
        rc, r = self.__query["SELECT Column_Name FROM USER_TAB_COLUMNS " + \
                             "WHERE Table_Name = '%s' AND DATA_TYPE != 'DATA'" % table]
        for i in r:
            columnList.append(i['COLUMN_NAME'])
        if 'CREATEDDATETIME' in columnList and 'CHANGEDDATETIME' in columnList:
            return 1
        else:
            return None

    def GetTimeFields(self, table):
        """ get ID, CreatedDateTime and ChangedDateTime data """
        rc, r = self.__query["SELECT ID, CreatedDateTime, ChangedDateTime " \
                             "FROM %s" % table]
        return r

    def ChangeTimeFields(self, table):
        """ changes field type of CreatedDateTime and ChangedDateTime """
        rc, r = self.__query["ALTER TABLE %s MODIFY CreatedDateTime DATE" % table]
        rc, r = self.__query["ALTER TABLE %s MODIFY ChangedDateTime DATE" % table]

    def DeleteData(self, table):
        """ delete all data in CreatedDateTime and ChangedDateTime """
        rc, r = self.__query["UPDATE %s SET CreatedDateTime = NULL, ChangedDateTime = NULL" % table]

    def ReInsertData(self, data, table):
        c = Convert()
        """ inserts saved data """
        id = data["ID"]
        if type(id) is types.LongType:
            id = str(id)[:-1]
        rc, r = self.__query["UPDATE %s SET " % table + \
                             "CreatedDateTime = TO_DATE('%s', 'YYYY-MM-DD HH24:MI:SS'), " % c.MakeIso(data["CreatedDateTime"]) + \
                             "ChangedDateTime = TO_DATE('%s', 'YYYY-MM-DD HH24:MI:SS') " % c.MakeIso(data["ChangedDateTime"]) + \
                             "WHERE ID = %s" % id]



class Convert:
    """ we have to convert a timestamp to a iso format """
    def MakeIso(self, value):
        t = time.localtime(value)
        f = self.CreateIsoDate(t)
        return f
        
    def CreateIsoDate(self, value):
        """ create a valid iso time string """
        iso = ''
        iso = iso + str(value[0]) + '-'
        if int(value[1]) >= 10:
            """ Check month for leading zero """
            iso = iso + str(value[1]) + '-'
        else:
            iso = iso + '0' + str(value[1]) + '-'
        if int(value[2]) >= 10:
            """ Check day for leading zero """
            iso = iso + str(value[2]) + ' '
        else:
            iso = iso + '0' + str(value[2]) + ' '
        if int(value[3]) >= 10:
            """ Check hour for leading zero """
            iso = iso + str(value[3]) + ':'
        else:
            iso = iso + '0' + str(value[3]) + ':'
        if int(value[4]) >= 10:
            """ Check minute for leading zero """
            iso = iso + str(value[4]) + ':'
        else:
            iso = iso + '0' + str(value[4]) + ':'
        if int(value[5]) >= 10:
            """ Check second for leading zero """
            iso = iso + str(value[5])
        else:
            iso = iso + '0' + str(value[5])

        return iso

    def __str__(self):
        return self.__timeValue

class Function:
    """ a collection of needed functions """
    def CleanFileData(self, data):
        """ clean the read file data and return it """
        fresh = []
        for i in data:
            if string.strip(string.replace(i, '\012', '')) != '':
                fresh.append(string.strip(string.replace(i, '\012', '')))
        return fresh

    def CleanMySQL(self, tables, dbname):
        """ cleans the returned value of all tables in a MySQL DB """
        dictKey = 'Tables_in_%s' % dbname
        fresh = []
        for i in tables:
            fresh.append(i[dictKey])
        return fresh

    def CleanORACLE(self, tables):
        """ cleans the returned value of all tables in a Oracle DB"""
        dictKey = 'TABLE_NAME'
        fresh = []
        for i in tables:
            fresh.append(i[dictKey])
        return fresh

if __name__=='__main__':
    """ initialize needed dmerce vars """
    vars.vars()
    """ make an instance of funcion class we need """
    f = Function()
    table = ''
    """ beginn with input form """
    dbtype = string.upper(string.strip(raw_input('Enter dbtype (oracle, mysql): ')))
    database = string.strip(raw_input('Enter name of database: '))
    user = string.strip(raw_input('Enter database user: '))
    passwd = string.strip(raw_input('Enter the password for given db user: '))
    all = string.upper(string.strip(raw_input('Change tables [a = all | l = seperated list | f = file]: ')))
    if all == 'F':
        inputFile = (raw_input('Enter complete path to file: '))
    """ look if a valid dbtype is given, if not exit program """
    if dbtype != 'ORACLE' and dbtype != 'MYSQL':
        print '\nSorry, your given dbtype', dbtype ,'! is not valid.\n\n Please try again !!!\n'
        sys.exit()
    """ if test is ok then build connection string """
    connStr = dbtype + ':' + user + ':' + passwd + '@localhost:' + database
    """ make an instance of db connection, get db cursor """
    al = DMS.SQL.Layer1(connStr)
    al.Init()
    query = al.GetQuery()
    """ choose rigth DBData instance """
    if string.upper(dbtype) == 'MYSQL':
        db = DBDataMySQL(query)
    if string.upper(dbtype) == 'ORACLE':
        db = DBDataOracle(query)
    """ now we begin to check what the user want to do """
    if all == 'L':
        """ this is the section if the user has given a comma seperated table list """
        tables = []
        table = string.strip(raw_input('Enter tablenames,: '))
        if table == '':
            print '\nYou have to specify a table'
            print 'Pleas try again !!!\n'
            sys.exit()
        for i in string.split(table, ','):
            if i != '':
                tables.append(i)
        for i in tables:
            if db.DescribeTable(i):
                a = db.GetTimeFields(i)
                db.DeleteData(i)
                db.ChangeTimeFields(i)
                for j in a:
                    db.ReInsertData(j, i)
                    print 'In table !',i,'! updated data -> ', j
            else:
                print 'No changes made in table ',i
    if all == 'A' and string.upper(dbtype) == 'MYSQL':
        """ this is the section if the user wants to change all tables in a MySQL DB """
        tables = f.CleanMySQL(db.GetAllTables(), database)
        for i in tables:
            if db.DescribeTable(i):
                a = db.GetTimeFields(i)
                db.DeleteData(i)
                db.ChangeTimeFields(i)
                for j in a:
                    db.ReInsertData(j, i)
                    print 'In table !',i,'! updated data -> ', j
            else:
                print 'No changes made in table ', i
    if all == 'A' and string.upper(dbtype) == 'ORACLE':
        """ this section handels an oracle db with all tables """
        tables = f.CleanORACLE(db.GetAllTables())
        for i in tables:
            if db.DescribeTable(i):
                a = db.GetTimeFields(i)
                db.DeleteData(i)
                db.ChangeTimeFields(i)
                for j in a:
                    db.ReInsertData(j, i)
                    print 'In table !',i,'! updated data -> ', j
            else:
                print 'No changes in table ', i
    if all == 'F':
        """ here we changes tables defined in a file """
        fi = open(inputFile, 'r')
        tables = fi.readlines()
        fi.close()
        tables = f.CleanFileData(tables)
        for i in tables:
            if db.DescribeTable(string.upper(i)):
                a = db.GetTimeFields(i)
                db.DeleteData(i)
                db.ChangeTimeFields(i)
                for j in a:
                    db.ReInsertData(j, i)
                    print 'In table !',i,'! updated data -> ', j
            else:
                print 'No chanes made in table ',i
