#!/usr/bin/env python
##
import sqlAl_td 
import string
import sys


# variables of source db
# will be replaced later by using 'getopt'
sdb_user = 'ka'
sdb_passwd = 'MyPython'
sdb_host = 'localhost'
sdb_name = 'ka'

# variables of destination db
# will be replaced later by using 'getopt'
ddb_user = 'ka'
ddb_passwd = 'MyPython'
ddb_host = 'localhost'
ddb_name = 'sync_test'

s_tablename = ['Beitrag']
d_tablename = ['Beitraege']
stmtList = []              # Initialize a variable to store a list of all Statements for each table
sourceTypeDict = {}        # Variable to store fieldNames with recent types
                           # of source database
destTypeDict = {}

source_type = sqlAl_td.GetTableFieldType(sdb_name,sdb_host,sdb_user,sdb_passwd)


def getSourceType():
    for i in range(len(s_tablename)):
        sourceTypeDict[s_tablename[i]] = source_type.describeTable(s_tablename[i]) 
    return sourceTypeDict

s_typeDict = getSourceType()

#if len(s_tablename) == len(d_tablename):
for i in range(len(s_tablename)):
    destTypeDict[d_tablename[i]] = sourceTypeDict[s_tablename[i]]
    #else:
    #    print ' Sorry, the count of source and destination tablenames are not equal !!'
    #    sys.exit()


createStmt = sqlAl_td.HandleTables()
createTable = sqlAl_td.HandleDB(ddb_name , ddb_host , ddb_user ,ddb_passwd)

def getStatements(s_tablename):
    for i in range(len(s_tablename)):
        stmtList.append(createStmt.formStatement(destTypeDict,d_tablename[i]))
    return stmtList

def createTables(d_tablename,stmtList):
    for i in range(len(d_tablename)):
        createTable.createTable(d_tablename[i],stmtList[i])


createTables(d_tablename , getStatements(s_tablename) )

print s_typeDict
print
print
