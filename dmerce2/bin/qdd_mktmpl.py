#!/usr/bin/env python
#

""" Import recent Modules """
try:
    import sys
    import os
    import string
    import MySQLdb
    import getopt
    import operator
except:
    print 'Errors occured while importing modules !'
    sys.exit()

class ConnectDB:
    """ Class to make an connection to database """
    def __init__(self,db = '', host = '', user = '', passwd = ''):
        """ Constructor """
        db = MySQLdb.connect(db = db, host = host, user = user, passwd = passwd) 
        self.c = db.cursor()

class DBData(ConnectDB):
    """ DB Handling : Get user and group data """
    def __init__(self, connectStr):
        """ Constructor """
        self.__connectStr = connectStr
        self.AnalyseConnectStr()
        ConnectDB.__init__(self, self.__db, self.__host, self.__user, self.__passwd)

    def AnalyseConnectStr(self):
        """ Analyse connect string """
        try:
            """ analyse connect string (database type:user:password@host:database name) """
            a, b = string.split(self.__connectStr, '@')
            self.__dbType, self.__user, self.__passwd = string.split(a, ':')
            self.__host, self.__db = string.split(b, ':')
        except:
            print 'Error in connection string: ' , self.__connectStr

    def Count(self, table, field, value):
        query = 'SELECT COUNT(*) FROM %s WHERE %s = "%s"' % (table, field, value)
        self.c.execute(query)
        result = self.c.fetchall()
        return int(result[0][0])
    
    def CheckExist(self, table, field, value):
        query = 'SELECT COUNT(*) FROM %s WHERE %s = "%s"' %(table, field, value)
        self.c.execute(query)
        result = self.c.fetchall()
        if int(result[0][0]) == 0:
            return None
        else:
            return 1

    def CheckTemplates(self, fqhn, fqtn):
        query = 'SELECT COUNT(*) FROM Templates WHERE FQHN = "%s" ' % fqhn + \
                'AND FQTN = "%s"' % fqtn
        self.c.execute(query)
        result = self.c.fetchall()
        if int(result[0][0]) == 0:
            """ if no occurences return true to insert """
            return 1
        else:
            return None
    
    def GetTables(self):
        query = 'SHOW TABLES'
        self.c.execute(query)
        return self.c.fetchall()
    
    def GetFields(self, table):
        query = 'DESCRIBE %s' % table
        self.c.execute(query)
        return self.c.fetchall()

    def GetMaxID(self, IDField, table):
        query = 'SELECT max(%s) FROM %s' % (IDField, table)
        self.c.execute(query)
        result = self.c.fetchall()
        return int(result[0][0]) + 1

    def Templates(self, fqhn, fqtn):
        maxID = self.GetMaxID('ID', 'Templates')
        if (string.split(fqhn, '.'))[0] == 'dmerce':
            """ web type """
            sam = 1
        if (string.split(fqhn, '.'))[0] == 'www':
            sam = 0
        query = 'INSERT INTO Templates (ID, FQHN, FQTN, SAM, SAMFailURL) ' + \
                'VALUES ("%s","%s","%s","%s","../index.html")' %(maxID, fqhn, fqtn, sam)
        self.c.execute(query)

    def UXSGroups(self, type, fqhn = '', right = '0'):
        list = [('Everybody', '0'), ('Teilnehmer', '1'), ('Agent','2')]
        if type == 'n':
            """ insert static data out of list above """
            for i in range(len(list)):
                maxID = self.GetMaxID('ID', 'UXSGroups')
                query = 'INSERT INTO UXSGroups (ID, GroupID, Name, FQHN) ' + \
                        'VALUES ("%s","%s","%s","%s")' % (maxID, list[i][1], list[i][0], fqhn)
                self.c.execute(query)
        if type == 'u':
            """ update rigths for fqhn """
            query = 'UPDATE UXSGroups SET R = "%s" WHERE FQHN = "%s"' % (right , fqhn)
            self.c.execute(query)


    def UXSRights(self, fqhn, object):
        if (string.split(fqhn, '.'))[0] == 'www':
            R = '224'
        if (string.split(fqhn, '.'))[0] == 'dmerce':
            R = '480'
        maxID = self.GetMaxID('ID', 'UXSRights')
        object = object + '.*'
        query1 = 'SELECT COUNT(*) FROM UXSRights where FQHN = "%s"' % fqhn
        self.c.execute(query1)
        rightID = self.c.fetchall()
        rightID = int(rightID[0][0]) + 1
        query2 = 'INSERT INTO UXSRights (ID, RightID, Object, R, FQHN) ' + \
                 'VALUES ("%s", "%s", "%s", "%s", "%s")' % (maxID, (int(rightID) + 1), object, R, fqhn)
        self.c.execute(query2)

class Function:
    """ class with basis functions """
    def CleanFields(self, fields):
        cleaning = ('ID','ChangedDateTime','CreatedDateTime','CreatedBy','ChangedBy')
        result = []
        for i in range(len(fields)):
            if fields[i][0] not in cleaning:
                result.append(fields[i][0])
        return result

    def BuildFQTN(self, dir, template):
        return '%s,%s' % (dir, template)

    def CalcRight(self, R):
        acl = Acl()
        r = int(R)
        rights = (2, r)
        for i in range(rights[0], rights[1] + 1):
            acl.GiveSingleRight(i)
        z = acl.Get()
        return str(z)[:-1]

class Acl:
    def __init__(self, _startvalue=0):
        self.__rights = _startvalue

    def Bitval(self, _id, _flag):
        _id = _id >> _flag
        if _id % 2 == 0:
            return 0
        else:
            return 1

    def Length(self, _input):
        n=0
        while _input > 2**n:
            n = n+1
        return n


    def GiveSingleRight(self, _right): # _right ist Nr. eines Bitregisters (also >=0)
        _result = 0
        if self.Bitval(self.__rights, _right) == 0:
            self.__rights = self.__rights + 2L**_right

    def GiveRights(self, _rights):
        _result = 0
        for i in range (0, self.Length(self.__rights) + self.Length(_rights)+1):
            if self.Bitval(self.__rights, i) == 1 or self.Bitval(_rights, i) == 1:
                _result = _result + 2**i
        self.__rights = _result
       
    def HasRight(self, _digit):
        return self.Bitval(self.__rights, _digit)

    def Get(self):
        return self.__rights
        
    def KillRight(self, _right): # _right ist Nr. eines Bitregisters (also >= 0)
        if self.HasRight(_right):
            self.__rights = self.__rights - 2**_right

    def KillRights(self, _rights):
        for i in range(0, self.Length(self.__rights)):
            if self.HasRight(i) and self.Bitval(_rights, i):
                self.__rights = self.__rights - 2**i

    def ShowRights(self):
        r = self.__rights
        l = []
        while r:
            if r % 2 == 0:
                l.append(0)
            else:
                l.append(1)
            r = r >> 1
        return l

class Maintain:
    """ class to hold maintain data """
    def SetMaintain(self, project, table, fieldlist):
        self.__maintain = '<html>\n\n' + \
                          '<head>\n\n' + \
                          '\t<meta name="AUTHOR" CONTENT="1Ci GmbH - Münster (info@1Ci.de)">\n' + \
                          '\t<meta name="COPYRIGHT" CONTENT="1Ci GmbH Münster" (info@1Ci.de)">\n' + \
                          '\t<meta name="LANGUAGE" CONTENT="de">\n' + \
                          '\t<meta name="REVISIT-AFTER" CONTENT="1 month">\n' +  \
                          '\t<meta name="REPLY-TO" CONTENT="webmaster@1Ci.de">\n' + \
                          '\t<title>%s - %s - Maintain</title>' %(project, table) + \
                          '</head>\n\n' + \
                          '<body topmargin="0" leftmargin="0"  marginwidth="0" marginheight="0">\n\n' + \
                          '<form method="post" action="/1Ci/dmerce">\n\n' + \
                          '<input type="hidden" name="qTemplate" value="%s,maintain">\n' % table + \
                          '<input type="hidden" name="qUpdate" value="%s">\n' % table + \
                          '<input type="hidden" name="%s_ID" value="(form ID)">\n\n' % table + \
                          '(repeat "%s.ID*(form ID)")\n\n' % table

        for i in range(len(fieldlist)):
            self.__maintain = self.__maintain + '<table width="100%" border="0" cellpadding="0" cellspacing="0">\n\n' + \
                              '\t<tr>\n' + \
                              '<td><a href="/1Ci/dmerce?qTemplate=%s,insert">hinzuf&uuml;gen</a></td>\n' % table + \
                              '<td><a href="/1Ci/dmerce?qTemplate=%s,' % table + \
                              'list&ID=(form ID)&qDelete=%s&%s_ID=' % (table, table) + \
                              '(sql %s.ID)&qButton=1">Eintrag l&ouml;schen</a></td>\n' % table + \
                              '\t</tr>\n' + \
                              '</table>\n\n' + \
                              '<table width="100%" border="0" cellpadding="0" cellspacing="0">\n\n' + \
                              '\t<tr>\n' + \
                              '\t<td>%s</td>\n' % fieldlist[i] + \
                              '\t<td><input type="text" size="20" name="%s_%s"' % (table, fieldlist[i]) + \
                              'value="(sql %s.%s)"></td>\n' % (table, fieldlist[i]) + \
                              '\t</tr>\n\n' + \
                              '</table>\n\n'
            
        self.__maintain = self.__maintain + '(endrepeat)\n' + \
                          '</table>\n\n' + \
                          '</form>\n\n' + \
                          '</body>\n\n' + \
                          '</html>'

    def __str__(self):
        if self.__maintain:
            return str(self.__maintain)

class Insert:
    """ class to hold insert data """
    def SetInsert(self, project, table, fieldlist):
        self.__insert = '<html>\n\n' + \
                        '<head>\n\n' + \
                        '<meta name="AUTHOR" CONTENT="1Ci GmbH - Münster (info@1Ci.de)">\n' + \
                        '<meta name="COPYRIGHT" CONTENT="1Ci GmbH Münster" (info@1Ci.de)">\n' + \
                        '<meta name="LANGUAGE" CONTENT="de">\n' + \
                        '<meta name="REVISIT-AFTER" CONTENT="1 month">\n' + \
                        '<meta name="REPLY-TO" CONTENT="webmaster@1Ci.de">\n' + \
                        '<title>%s - %s - Insert</title>\n' % (project, table) + \
                        '</head>\n\n' + \
                        '<body topmargin="0" leftmargin="0"  marginwidth="0" marginheight="0">\n\n' + \
                        '<form method="post" action="/1Ci/dmerce">\n' + \
                        '<input type="hidden" name="qTemplate" value="%s,insert">\n' % table + \
                        '<input type="hidden" name="qInsert" value="%s">\n' % table + \
                        '<input type="hidden" name="%s_ID" value="(sql dboid %s)">\n' % (table, table)


        for i in range(len(fieldlist)):
            self.__insert = self.__insert + '<table width="100%" border="0" cellpadding="0" cellspacing="0">\n' + \
                            '\t<tr>\n' + \
                            '\t<td>%s</td>\n' % fieldlist[i] + \
                            '\t<td><input type="text" size="20" name="%s_%s"</td>\n' %(table, fieldlist[i]) + \
                            '\t</tr>\n\n' + \
                            '</table>\n\n' + \
                            '<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">\n\n' + \
                            '\t<tr>' + \
                            '\t<td><input type="submit" name="qButton" value="Speichern"></td>\n' + \
                            '\t</tr>\n' + \
                            '</table>\n'
        self.__insert = self.__insert + '</form>\n\n' + \
                        '</body>\n\n' + \
                        '</html>'

    def __str__(self):
        if self.__insert:
            return str(self.__insert)

class List:
    """ class to hold view data """
    def SetList(self, project, table, fieldlist):
        self.__list = '<html>\n\n' + \
                      '<head>\n\n' + \
                      '<meta name="AUTHOR" CONTENT="1Ci GmbH - Münster (info@1Ci.de)">\n' + \
                      '<meta name="COPYRIGHT" CONTENT="1Ci GmbH Münster" (info@1Ci.de)">\n' + \
                      '<meta name="LANGUAGE" CONTENT="de">\n' + \
                      '<meta name="REVISIT-AFTER" CONTENT="1 month">\n' + \
                      '<meta name="REPLY-TO" CONTENT="webmaster@1Ci.de">\n' + \
                      '<title>%s - %s - List</title>\n' % (project, table) + \
                      '</head>\n\n' + \
                      '<body topmargin="0" leftmargin="0"  marginwidth="0" marginheight="0">\n\n' + \
                      '<table width="100%" border="0" cellpadding="0" cellspacing="0">\n ' + \
                      '(repeat "%s.ID](form ID)")\n\n' % table


        for i in range(len(fieldlist)):
            self.__list = self.__list + '\t<tr>\n' + \
                          '\t<td>%s</td>\n' % fieldlist[i] + \
                          '\t<td><a href="/1Ci/dmerce?qTemplate=%s,' % table + \
                          'view&ID=(sql %s.ID)">(sql %s.%s)</a></td>\n' %(table, table, fieldlist[i]) + \
                          '\t<td><a href="/1Ci/dmerce?qTemplate=%s,maintain&ID=(sql %s.ID)">&auml;ndern</a>\n' %(table, table) + \
                          '\t</tr>\n\n'

        self.__list =  self.__list + '(endrepeat)\n\n' + \
                      '</table>\n\n' + \
                      '</body>\n\n' + \
                      '</html>'

    def __str__(self):
        if self.__list:
            return str(self.__list)



class View:
    """ class to hold list data """
    def SetView(self, project, table, fieldlist):
        self.__view = '<html>\n\n' + \
                      '<head>\n\n' + \
                      '<meta name="AUTHOR" CONTENT="1Ci GmbH - Münster (info@1Ci.de)">\n' + \
                      '<meta name="COPYRIGHT" CONTENT="1Ci GmbH Münster" (info@1Ci.de)">\n' + \
                      '<meta name="LANGUAGE" CONTENT="de">\n' + \
                      '<meta name="REVISIT-AFTER" CONTENT="1 month">\n' + \
                      '<meta name="REPLY-TO" CONTENT="webmaster@1Ci.de">\n' + \
                      '<title>%s - %s - View</title>\n' % (project, table) + \
                      '</head>\n' + \
                      '<body topmargin="0" leftmargin="0"  marginwidth="0" marginheight="0">\n\n' + \
                      '<table width="100%" border="0" cellpadding="0" cellspacing="0">\n\n' + \
                      '\t<tr>\n' + \
                      '\t<td><a href="/1Ci/dmerce?qTemplate=%s,' % table + \
                      'maintain&ID=(sql %s.ID)">Eintrag &auml;ndern</a></td>\n' % table + \
                      '\t<td><a href="/1Ci/dmerce?qTemplate=%s,' % table + \
                      'list&ID=(form ID)&qDelete=%s&%s_ID=' % (table, table) + \
                      '(sql %s.ID)&qButton=1">Eintrag l&ouml;schen</a></td>\n' % table + \
                      '\t</tr>\n' + \
                      '</table>\n' + \
                      '<table width="100%" border="0" cellpadding="0" cellspacing="0">\n\n' + \
                      '(repeat "ID]0")\n\n'

        for i in range(len(fieldlist)):
            self.__view = self.__view + '\t<tr>\n' + \
                          '\t<td>%s</td>\n' %fieldlist[i] + \
                          '\t<td>(sql %s.%s)</td>\n' %(table, fieldlist[i])+ \
                          '\t</tr>\n\n'

        self.__view = self.__view + '(endrepeat)\n\n' + \
                      '</table>\n\n' + \
                      '</body>\n\n' + \
                      '</html>'
        
    def __str__(self):
        if self.__view:
            return str(self.__view)

class File:
    def CheckPath(self, path):
        if not os.path.exists(path):
            os.makedirs(path, 0770)
        os.system('chmod -R u=rwx %s ' % path)
        os.system('chmod -R g=rwxs %s' % path)

    def CheckGivenDir(self, dir):
        if dir[-1] != '/':
            dir = dir + '/'
        if dir[0] != '/':
            dir = '/' + dir
        return dir
        
    def Write(self, path, tabledir, filename, value):
        """dir -> directory of webserver project structure
           tabledir -> directorynames out of table structure
           filename -> maintain.html, insert.html, view.html, ....
        """
        self.__path = path + 'templates/' + tabledir
        self.__file = filename + '.html'
        self.CheckPath(self.__path)
        f = open((self.__path + '/' + self.__file), 'w', 0770)
        f.write(value)
        f.close()
        """set permission to 'rw' for user and group"""
        os.system('chmod ug=rw %s' %(self.__path + '/*'))
        os.system('chmod o= %s' %(self.__path + '/*'))

if __name__ == '__main__':
    """ Main procedure """
    try:
        optlist, args = getopt.getopt(sys.argv[1:],
                                  'f:k:u:p:k:',['db=',
                                                'dir=',
                                                'project='])
        optdict = {}
        for i in optlist:
            optdict[i[0]] = i[1]
            if not optdict.has_key('--db'):
                optdict['--db'] = ''
            if not optdict.has_key('--project'):
                optdict['--project'] = ''
            if not optdict.has_key('--dir'):
                optdict['--dir'] = ''
    except getopt.error, msg:
        # Print error message
        print 'Error parsing arguments: %s' %msg  
        # Exit
        sys.exit()
    """ Initialize recent variables """
    file = File()
    dbase = optdict['--db']
    project = optdict['--project']
    dir = optdict['--dir']
    web_fqhn = 'www.' + project
    dmerce_fqhn = 'dmerce.' + project
    """ Check, if getopt is used correctly"""
    if dbase == '' or dir == '' or project == '':
        print 'Sorry, you have to give all options'
        print 'Please use it in this form:'
        print 'build-templates.py --db="<database name>" --project="<fqhn without www. or dmerce.>"' + \
              ' --dir=<project-directory>\n\n'
        print 'Please try again !'
        sys.exit()
    dir = file.CheckGivenDir(dir)
    web_path = '/var/www/1ci.de/'
    """ build complete template - path """
    www_path = web_path + dir + 'www/'
    templateDB = DBData('MySQL:root:X1/yP7@localhost:' + str(dbase))
    dmerceSysDB = DBData('MySQL:root:X1/yP7@localhost:dmerce_sys')
    func = Function()
    """ Get and clean table list """
    tables = func.CleanFields(templateDB.GetTables())
    for i in range(len(tables)):
        fields = func.CleanFields(templateDB.GetFields(tables[i]))
        """ make instances """
        main = Maintain()
        view = View()
        insert = Insert()
        list = List()
        if dmerceSysDB.CheckTemplates(web_fqhn , func.BuildFQTN(tables[i], 'maintain')):
            """ instert new FQTN into table templates """
            dmerceSysDB.Templates(web_fqhn, func.BuildFQTN(tables[i], 'maintain'))
            dmerceSysDB.Templates(web_fqhn, func.BuildFQTN(tables[i], 'insert'))
            dmerceSysDB.Templates(web_fqhn, func.BuildFQTN(tables[i], 'view'))
            dmerceSysDB.Templates(web_fqhn, func.BuildFQTN(tables[i], 'list'))
            if not dmerceSysDB.CheckExist('UXSRights', 'FQHN', web_fqhn):
                """ insert data in rights-table """
                dmerceSysDB.UXSGroups('n', web_fqhn)
                dmerceSysDB.UXSGroups('n', dmerce_fqhn)
            dmerceSysDB.UXSRights(web_fqhn, tables[i])
            dmerceSysDB.UXSRights(dmerce_fqhn, tables[i])
            """ Set maintain file value """
            main.SetMaintain(web_fqhn, tables[i], fields)
            """ write maintain file """
            file.Write(www_path, tables[i], 'maintain', str(main))
            list.SetList('www.' + project, tables[i], fields)
            file.Write(www_path, tables[i], 'list', str(list))
            insert.SetInsert('www.' + project, tables[i], fields)
            file.Write(www_path, tables[i], 'insert', str(insert))
            view.SetView('www.' + project, tables[i], fields)
            file.Write(www_path, tables[i], 'view', str(view))
    """ Set rights for templates """
    count = dmerceSysDB.Count('Templates', 'FQHN', web_fqhn)
    dmerceSysDB.UXSGroups('u', web_fqhn, func.CalcRight(count + 1))
    dmerceSysDB.UXSGroups('u', dmerce_fqhn, func.CalcRight(count + 1))
