#!/usr/bin/env python
##
# Module to synchronize 2 Databases
##
import string
import MySQLdb
import sys


#
#Class to Connect to database 
#
class ConnectDB:

    def __init__(self, db_name='' , host='', user='', passwd=''):
        db = MySQLdb.connect(db = db_name , host = host, user = user, passwd = passwd)
        self.c = db.cursor()

#
#Class to get all Fields out of Database
#
class GetAllTableNames(ConnectDB):
  
    def __init__(self, db_name='', host='', user='', passwd=''):
        ConnectDB.__init__(self, db_name, host, user, passwd)

    def getIt(self):
        query = "SHOW TABLES"
        self.c.execute(query)
        return self.c.fetchall()


#
# Class to get the field types from the last query and
# put it into a list
#
class GetTableFieldType(ConnectDB):

    def __init__(self, db_name='', host='', user='', passwd=''):
        ConnectDB.__init__(self, db_name, host, user, passwd)

    def getIt(self,tableName):
        query = "SELECT * FROM %s" %(tableName)
        self.c.execute(query)
        return self.c.description

    def describeTable(self,tableName):
        query = "DESCRIBE %s" %(tableName)
        self.c.execute(query)
        return self.c.fetchall()

#
# Class to handle with databases an make changes
#
class HandleDB(ConnectDB):

    def __init__(self,db_name='',host='',user='',passwd=''):
        ConnectDB.__init__(self, db_name, host, user, passwd)

    def ifDBExists(self,db_name):
        pass 

    #
    # Method to create a database
    #
    def createDB(self,db_name):
        try:
            query = "CREATE DATABASE %s" %(db_name) 
            self.c.execute(query)
        except:
            error = 1
        return error  

    #
    # Method to create table
    #
    def createTable(self,tableName,createStmt):
        try:
            query = "CREATE TABLE %s (%s)" %(tableName,createStmt) #variables are given from function of syncTest.py
            self.c.execute(query)
        except:
            error = 1
        return error

    #
    # Method to alter a table
    #
    def alterTable(self,tableName,createStmt):
        try:
            # variables are given from function of syncTest.py
            query = "ALTER TABLE %s %s %s" %(tableName,tableName,createStmt)
            self.c.execute(query)
        except:
            error = 1
        return error

#
# Class to compare fieldTypes (source and destination typeDict's are needed)
#
class HandleTables:
    #
    # Method to look for a table presents. If it exists, put it into a list
    # to check the differences
    #
    def checkForPresents(self,sDict,dDict):
        # initialize recent variables
        tableToCreateList = []
        typeCheckDict = {}

        # put keys (source table names) from source typeDict into a list to check,
        # if they are in destination typeDict
        keyList = sDict.keys()

        # loop to check keys (source table names) if they are in typeDict of
        # destination database
        for i in range(len(keyList)):
            if dDict.has_key(keyList[i]):
                typeCheckDict[keyList[i]] = dDict[keyList[i]]
            else:
                tableToCreateList.append(keyList[i])
            return tableToCreateList , typeCheckDict

    #
    # Method to compare field wich are still existing in destination db
    #
    def compareFields(self,sDict,typeCheckDict):
        dTableList = typeCheckDict.keys()   # Put all names of tables from dest. db into a list
        sTableList = sDict.keys()   # Put all names of tables from source db into a list
        
        toDelTableList = []          # A list will be returned, wich contains tables, wich could be deleted
        
        toDelFieldDict = {}          # A dict will be returned, wich contains fields, wich could be deleted
        # key is the name of table, value(s) is/are name of fields
        
        toCreateTableList = []       # A list, wich contains the tables, wich have to be created
        toCreateFieldDict = {}       # A dict, wich contains field(s), which have to be created
        # key is the name of table, value(s) is/are name of fields
        
        toChangeFieldDict = {}       # A dict, wich contains field(s), which type(s) have to be changed
        # key is the name of table, value(s) is/are name of fields
        
        # loop to check, which tables of destination db are still in source db
        # if not, you have to delete the recent key after saving it into a list
        # this list describes, which tables have to be removed from db
        for i in range(len(sTableList)):
            if sTableList[i] in dTableList:
                toCreateTableList.append(sTableList[i])
                
        same = 0
        # loop to initialize the dict, because every key values a list
        # not a string

        for i in range(len(dTableList)):
            sDict[dTableList[i]].sort()
            typeCheckDict[dTableList[i]].sort()
            toChangeFieldDict[dTableList[i]] = []
            
    #
    # Method to create the statement for 'CREATE TABLE'
    #
    def formStatement(self,typeDict,tabellenName):
        statementString = ''

        # Dictionary, which contains the keys, because you have to
        # have to define more than one key like this -> PRIMARY KEY(field1,field2,fieldx)
        keyDict = {}
        keyDict['PRI'] = []
        keyDict['UNI'] = []

        create = CreateStatement()
        #print 'TypeDict : ' , typeDict
        #print
        #print 'Tabellen Name' , tabellenName
    
        for x in range(len(typeDict[tabellenName])):
         
            tempDict = []
      
            for y in range(len(typeDict[tabellenName][x])):
                tempDict.append(typeDict[tabellenName][x][y])
    
            createdString = create.built(tempDict)
            keyTempDict = create.getKeys(tempDict)
      
            # ADD specified keys into the keyDict
            if keyTempDict.has_key('PRI'):
                for i in range(len(keyTempDict['PRI'])):
                    keyDict['PRI'].append(keyTempDict['PRI'][i])

            if keyTempDict.has_key('UNI'):
                for i in range(len(keyTempDict['UNI'])):
                    keyDict['UNI'].append(keyTempDict['UNI'][i])
          
            statementString = statementString + createdString
  
    
            if x < len(typeDict[tabellenName])-1:
                statementString = statementString + ','  
        
            # Check PRIMARY and UNIQUE KEY dictionaries
            # If they are present, add them to the creation-statement
            if len(keyDict['PRI']) > 0:
                keyString = ''
                for i in range(len(keyDict['PRI'])):
                    keyString = keyString + keyDict['PRI'][i]
                        
                    if i < len(keyDict['PRI'])-1:
                        keyString = keyString + ','
                        statementString = statementString + ',PRIMARY KEY(' + keyString + ')'

            if len(keyDict['UNI']) > 0:
                keyString = ''
                for i in range(len(keyDict['UNI'])):
                    keyString = keyString + keyDict['UNI'][i]
                        
                    if i < len(keyDict['UNI'])-1:
                        keyString = keyString + ','
                        statementString = statementString + ',UNIQUE KEY(' + keyString + ')'

      
        return statementString  

    #
    # Method to copy the struct into another database
    #
    def copyStruct(self,ddb_name,d_host,d_user,d_passwd,table,statement):
        error = 0
        handle = HandleDB(ddb_name,d_host,d_user,d_passwd)

        print 'Tabelle: ', table, ' ' , statement
        print
    
        try:
            handle.createTable(table,statement)
        except:
            error = sys.exc_info()
        print error

        if error == 0:
            print 'Well done !!! All structs are copied !!'
        else:
            print error

#
# Class to built the SQL type statement
#
class CreateStatement:
    #
    # Method to built statement for creating table with fields and it types
    #
    def built(self,typeDefinition):  ## param is the Dict with fieldTypes
        stmt = []
        stmt.append(typeDefinition[0])
        stmt.append(typeDefinition[1])

        # check for NOT NULL flag, '' = not null, 'YES' = null
        if typeDefinition[2] == '':
            stmt.append('NOT NULL')
    
        # check for default entry
        if typeDefinition[4] != '' and typeDefinition[4] is not None:
            stmt.append('DEFAULT \'')
            stmt.append(typeDefinition[4])
            stmt.append('\'')
            stmt.append('')

        if typeDefinition[5] != '':
            stmt.append(typeDefinition[5])

        stmt_string = string.join(stmt,' ') # transform tuple to string

        return stmt_string

    #
    # Method to get all key out of the table
    #
    def getKeys(self,typeDefinition):

        keyDict = {}
        
        # check for key_flag_type if exists
        if typeDefinition[3] == 'PRI':
            keyDict['PRI'] = []
            keyDict['PRI'].append(typeDefinition[0])
          
      
        elif typeDefinition[3] == 'UNI':
            keyDict['UNI'] = []
            keyDict['UNI'].append(typeDefinition[0])    

        return keyDict
