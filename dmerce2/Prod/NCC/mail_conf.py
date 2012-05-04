#! /usr/local/env python
#


""" Import recent Modules"""
import sys
try:
    import DMS.SQL
    import os
    import crypt
    import string
    import MySQLdb
    import getopt
except:
    print 'Errors occured while importing modules !'
    sys.exit()

class System:
    """ get sytem information and return some specific data """
    def __init__(self):
        """ Constructor """
        self.__cfDir = ''

    def GetOS(self):
        """ Get system Information """
        OS = os.uname()
        return OS[0]

    def SetcfDir(self):
        """ look for system and return right dir for sendmail.cf """
        if self.GetOS() == 'Linux':
            return '/usr/share/sendmail/sendmail.cf/'
        if self.GetOS() == 'FreeBSD':
            return '/usr/share/sendmail/sendmail/'

class DBData:
    def GetMacros(self, query, macroName):
        """ get data out if mail server database"""
        rc, r = query['SELECT Macro AS MacroName, ' + \
                      'Value AS MacroValue ' + \
                      'FROM SrvMailSrv ' + \
                      'WHERE Macro="%s"' % macroName]
        return r
    
class BuildConfFile:
    """create line from db data and return it"""
    def SetMacro(self, value):
        self.__macro = value

    def SetValue(self, value):
        self.__value = value

    def CheckMacro(self, value):
        pass

    def CheckValue(self, value):
        pass
    
    def __str__(self):
        return '%s(%s)\n' % (self.__macro, self.__value)

class SetConfFile:
    """ class to AddLine to configuration file and write it"""
    def __init__(self):
        """constructor"""
        self.__file = ''
    
    def AddLine(self, line):
        self.__file = self.__file + line

    def Write(self, outFile):
        self.__f = open('%s' % outFile, 'w')
        self.__f.write('%s' % self.__file)
        
    def __str__(self):
        return '%s' % self.__file

    def ConvertConfFile(self, dir, mcFile, cfFile):
        os.system('m4 %sm4/cf.m4 %s > %s' % (dir, mcFile, cfFile))
    

if __name__ == '__main__':
    """ getopt section"""
    try:
        optlist, args = getopt.getopt(sys.argv[1:],
                                      'f:k:u:p:k:',['mcFile=', 'cfFile='])
        optdict = {}
        for i in optlist:
            optdict[i[0]] = i[1]
            if not optdict.has_key('--mcFile'):
                optdict['--mcFile'] = None
            if not optdict.has_key('--cfFile'):
                optdict['--cfFile'] = None
    except getopt.error, msg:
        print 'Error parsing arguments: %s' %msg
        sys.exit()
    mcFile = optdict['--mcFile']
    cfFile = optdict['--cfFile']
    """this version contains static db connection"""
    connectStr = 'MySQL:ncc:lk(/$8@localhost:dmerce_ncc'
    dbapi = DMS.SQL.DBAPI(connectStr)
    query = DMS.SQL.Query(dbapi)
    """
    put all macros into list to define the right order
    which sendmail understand
    """
    macroList = ['divert', 'VERSIONID', 'OSTYPE',
                 'FEATURE', 'MAILER', 'define',
                 'MASQUERADE_AS', 'DAEMON_OPTIONS']
    db = DBData()
    mac = SetConfFile()
    for h in range(len(macroList)):
        macros = db.GetMacros(query, macroList[h])
        if macros:
            for i in range(len(macros)):
                mc = BuildConfFile()
                mc.SetMacro(macros[i]['MacroName'])
                mc.SetValue(macros[i]['MacroValue'])
                mac.AddLine(str(mc))
    mac.Write(mcFile)
    sys = System()
    cfDir = sys.SetcfDir()
    print cfDir
    mac.ConvertConfFile(cfDir, mcFile, cfFile)
