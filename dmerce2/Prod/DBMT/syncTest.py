#!/usr/bin/env python
##
import sqlAl_td 
import string
import sys

# variables of source db
# will be replaced later by using 'getopt'
s_user = 'ka'
s_passwd = 'MyPython'
s_host = 'localhost'
sdb_name = 'ka'

## variables of destination db
## will be replaced later by using 'getopt'
d_user = 'ka'
d_passwd = 'MyPython'
d_host = 'localhost'
ddb_name = 'sync_test'

# Variables to test the functions of Module 'sqlAl_td'
typeList = []              # Variable to store type of fields

sourceTypeDict = {}        # Variable to store fieldNames with recent types
                           # of source database
destinationTypeDict = {}   # of destination database

# make an instance of Class sqlAq_td for both databases
source = sqlAl_td.GetAllTableNames(sdb_name,s_host,s_user,s_passwd)
destination = sqlAl_td.GetAllTableNames(ddb_name,d_host,d_user,d_passwd)

sdb_tables = source.getIt() # put all tablenames of source db into a list
ddb_tables = destination.getIt() # put all tablenames of destination db into a list


source_type = sqlAl_td.GetTableFieldType(sdb_name,s_host,s_user,s_passwd)
destination_type = sqlAl_td.GetTableFieldType(ddb_name,s_host,d_user,d_passwd)


# Create a dictionary of received tables,fieldnames (Source Database)
# Key is table name, value are fieldTypes
def getSourceType():
    for i in range(len(sdb_tables)):
        sourceTypeDict[sdb_tables[i][0]] = source_type.describeTable(sdb_tables[i][0]) 
    return sourceTypeDict

s_typeDict = getSourceType()
print 's_typeDict ---> ', s_typeDict
# Create a dictionary of received tables,fieldnames (Destination Database)
# Key is table name, value are fieldTypes    
def getDestinationType():
    for i in range(len(ddb_tables)):
        destinationTypeDict[ddb_tables[i][0]] = destination_type.describeTable(ddb_tables[i][0])
    return destinationTypeDict

d_typeDict = getDestinationType()

#
# function to Create destination database
#
def dbCreation(host,user,passwd,ddb_name):
    ddbCreate = sqlAl_td.handleDB('',host,user,passwd)
    createError = ddbCreate.createDB(ddb_name)

    if createError:
        print 'Sorry, the database you have chosen still exists'
    print 'Creation of destination database :  OK !'

create = sqlAl_td.CreateStatement()
copy = sqlAl_td.HandleTables()

#
# function to make a struct-copy of given table into another db
#
def copyTable(ddb_name,d_host,d_user,d_passwd,table,typeDict):
    create = sqlAl_td.HandleTables()
    stmt = create.formStatement(typeDict,table)

    # Then you can copy the tables with its fields
    copy = sqlAl_td.HandleTables()
    copy.copyStruct(ddb_name,d_host,d_user,d_passwd,table,stmt)


def checkForExisting(s_typeDict,d_typeDict):
    check = sqlAl_td.HandleTables()
    tableToCreateList , typeToCheckDict = check.checkForPresents(s_typeDict,d_typeDict)
    return tableToCreateList , typeToCheckDict

toCreate , toCheck = checkForExisting(s_typeDict,d_typeDict)

compare = sqlAl_td.HandleTables()
result = compare.compareFields(s_typeDict,toCheck)

for i in range(len(toCreate)):
    copyTable(ddb_name,d_host,d_user,d_passwd,toCreate[i],s_typeDict)



