#!/usr/bin/env python
#
# Import modules
import time
import string
import os
import DMS.SQL
import DMS.SysTbl
import Guardian.Config
from BuildAFCStatement import Statement

class TimeConversion:
    def ToStamp(self, iso_date, type = ''):
        if iso_date == '' or iso_date == ' ':
            iso_date = '00-00-00 00:00'
        """ Convert iso date to unix time stamp """
        if iso_date[0:2] == '00':
            day = 0
        else:
            day = int(iso_date[0:2])
        if iso_date[3:5] == '00':
            month = 0
        else:
            month = int(iso_date[3:5])
        if iso_date[6:8] == '00':
            year = 2000
        elif int(iso_date[6:8]) > 50:
            year = 1900 + int(iso_date[6:8])
        else:
            year = 2000 + int(iso_date[6:8])
        if iso_date[9:11] == '00':
            hour = 0
        else:
            hour = int(iso_date[9:11])
        if iso_date[12:14] == '00':
            min = 0
        else:
            min = int(iso_date[12:14])
        sec = 0
        if type == 'stamp':
            return time.mktime(year, month, day, hour, min, sec, 0, 0, -1)
        else:
            return ('%s-%s-%s %s:%s:%s') %(year,month,day,hour,min,sec)


class DBData:
    def EmptyTable(self, query, tablename):
        """ Method to empty a table """
        rc, r = query["DELETE FROM %s" % tablename]

    def InsertData(self, query, statement):
        """ Method to insert data into table """
        rc, r = query[statement]

    def UpdateSchaden(self, query):
        rc, r = query["UPDATE Schaden SET AFC_SchadenNr = ID"]

class File:
    def __init__(self, path, filename):
        """ Constructor """
        self.__path = path
        self.__filename = filename
        
    def Read(self):
        """ Method to read data file """
        f = open(self.__path + self.__filename , 'r')
        file = f.readlines()
        f.close()
        return file
    
class Function:
    def CleanData(self, data):
        """ Method to remove 015 and 012"""
        withoutSpace = []
        withoutQuotes = []
        splitted = []
        for i in range(1,len(data)):
            """
            Clean data and do not return first line
            because it is the field name list and has
            not to be imported
            """
            withoutSpace.append(string.strip(string.replace(data[i],'"','')))
        for j in range(len(withoutSpace)):
            splitted.append(string.split(withoutSpace[j], ';'))
        return splitted

    def ConvertIsoDate(self, data):
        """ Convert the iso Date to Unix time stamp """
        convert = TimeConversion()
        for i in range(len(data)):
            if len(data[i]) > 5:
                data[i][3] = convert.ToStamp(data[i][3], 'stamp')
                """ Convert Data to time stamp """
                data[i][4] = convert.ToStamp(data[i][4])
                """ Make an english time format out of a german one """
        return data
        

def main(table, data):
    db = DBData()
    stmt = Statement()
    sysTbl = DMS.SysTbl.Retrieve()
    sysTbl.SetConfig(Guardian.Config.RFC822('/usr/local/1Ci/dmerce/conf'))	# Set config instance
    sysTbl.SetFQHN('import.afc.data')	        # Set fqhn
    sysTbl.InitSQLSysConn()		        # Init connection to sql sys db
    sysTbl.SetSysConfig()		       	# retrieve and set system config
    sysTbl.InitSQLDataConn()			# Init connection to sql data db
    sqlSys = sysTbl.GetSQLSys()
    query = sysTbl.GetSQLData()
    """ Made database connection """
    if len(data) > 0:
        db.EmptyTable(query, table)
    statement = None
    for i in range(len(data)):
        statement = stmt.Build(table, data[i])
        if statement:
            db.InsertData(query, statement)
    if table == 'Schaden':
        db.UpdateSchaden(query)

if __name__ == '__main__':
    notImported = []
    imported = []
    """ Main procedure """
    csvDataDir = '/tmp/afc-data/'
    function = Function()
    """ made instances """
    tableList = ['Fahrzeuge', 'Firma', 'Kostenstellen', 'Personen', 'Schaden', \
                 'Schadenart', 'Schadenkosten', 'Schadenort', 'Schadentyp']
    """ Table wich have to be updated """
    files = os.listdir(csvDataDir)
    for i in range(len(tableList)):
        if (tableList[i] + '.txt') in files:
            imported.append(tableList[i])
            file = File(csvDataDir, tableList[i] + '.txt')
            data = file.Read()
            data = function.CleanData(data)
            if tableList[i] == 'Schaden':
                data = function.ConvertIsoDate(data)
            main(tableList[i], data)
        else:
            notImported.append(tableList[i])
    print 'Imported Files are -> ', imported
    if len(notImported) > 0:
        print 'List of not imported files -> ', notImported
    """ read and cleaned data received out of file """
