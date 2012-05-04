#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.47 $'
#
# Revision 1.1  2000-06-07 17:57:45+02  rb
# Initial revision
#
##

import os
import os.path
import types
import string
import cgi
import Core.Log
import Core.OS
import DMS.Cache
#import DMS.Q
from mod_python import apache
from mod_python import util

class Env:

    """ represents HTTP server environment """

    def __init__(self, req):
        self.__req = req
        self.__env = DMS.Cache.Cache()
        self.__e = Core.OS.Base()

    def Init(self):
        self.InitOsEnv()
        self.InitModPyEnv()

    def InitOsEnv(self):
        self.__osEnv = {}
        for e in self.__e.EnvAll():
            self.__osEnv[e[0]] = e[1]

    def CreateDocRoot(self, value):
        newDoc = ''
        if string.count(value, '.') >= 1:
            self.__split = string.split(value, '.')
            for i in range(len(self.__split) -1, -1, -1):
                newDoc = newDoc + '/' + str(self.__split[i])
            return newDoc
        else:
            return value

    def InitModPyEnv(self):
	conn = self.__req.connection
	server = self.__req.server
        self.__env['req'] = self.__req
        self.__env['DOCUMENT_ROOT'] = server.path
        self.__env['REMOTE_ADDR'], remotePort = conn.remote_addr
        self.__env['SERVER_ADDR'], serverPort = conn.local_addr
        self.__env['SERVER_PORT'] = server.port
	self.__env['SERVER_NAME'] = server.server_hostname
        try:
            self.__env['DMERCE_LANG'] = self.__osEnv['DMERCE_LANG']
        except:
            self.__env['DMERCE_LANG'] = 'GERMAN'

    #def __setitem__(self, envvar, value):
    #    self.__env[envvar] = value

    def __getitem__(self, envvar):
        return self.__env[envvar]

    def keys(self):
        return self.__env.keys()

    def GetRefererTemplate(self):
        httpref = self.__env['HTTP_REFERER']
        if httpref:
            f1 = string.find(httpref, 'qTemplate=')
            httpref = httpref[f1 + 10:]
            f2 = string.find(httpref, '&')
            if f2 >= 0:
                httpref = httpref[:f2]
        return httpref

class ServerName:

    """ represents current ServerName """

    def __init__(self, e = None):
        if e is None:
            self.__e = Env()
            self.__e.Init()
        else:
            self.__e = e
        self.__sn = []
        self.__serverName = ''
        self.__lang = 'en'
        self.__domain = ''
        self.__tld = ''

    def Analyse(self):
        """ analyses URL """
        sn = self.__e['SERVER_NAME']
        self.__sn = string.split(sn, '.')
        self.SetServerName(sn[0])
        self.SetLanguage(self.__sn[1])
        self.SetDomain(string.join(self.__sn[2:-1], '.'))
        self.SetTLD(self.__sn[-1])

    def SetTLD(self, t):
        self.__tld = t

    def GetTLD(self):
        return self.__tld

    def SetServerName(self, s):
        self.__serverName = s

    def GetServerName(self):
        return self.__serverName

    def SetLanguage(self, l):
        self.__lang = l

    def GetLanguage(self):
        return self.__lang

    def SetDomain(self, d):
        self.__domain = d

    def GetDomain(self):
        return self.__domain

class URL:

    """ represents current URL """

    def __init__(self, e = None):
        if e is None:
            self.__e = Env()
        else:
            self.__e = e

    def Analyse(self):
        """ analyses URL """
        pass

    def SetProtocol(self, p):
        self.__protocol = p

    def GetProtocol(self):
        return self.__protocol

    def SetServerName(self, s):
        self.__serverName = s

    def GetServerName(self):
        return self.__serverName

class FieldStorage(DMS.Cache.Cache):

    """
    holds cgi fields
    """

    def __init__(self, q):
        DMS.Cache.Cache.__init__(self)
        self.__q = q

    def Parse(self):
        """ parse variables that do not begin with the letter q """
        for k in self.__q.keys():
            if k[0] != 'q':
                DMS.Cache.Cache.__setitem__(self, k, self.__q[k])

    def Retrieve(self, wc):
        """
        retrieve fields from cache beginning with string 'wc'
        (like a wildcard: eg. 'wc' = 'qSearch', we look for 'qSearch'*)
        """
        r = {}
        for field in DMS.Cache.Cache.keys(self):
            if string.find(field, wc) == 0:
                r[field] = DMS.Cache.Cache.__getitem__(self, field)
        return r

class qSearchBase:

    """
    takes a qVars instance and looks for
    qSearch* fields, saves them and makes
    them available for others
    """

    def __init__(self, qvars):
        self.__qvars = qvars
        self.__qsearch = {}
        self.__qsearchName = {}
        self.__qsearchOp = {}
        self.__qsearchFields = {}
        self.__qsuperSearch = ''

    def Parse(self):
        qsearchvars = self.__qvars.Retrieve('qSearch')
        for field in qsearchvars.keys():
            f, v = string.split(field, '_')[1], qsearchvars[field]
            if string.find(field, 'qSearch_') == 0:
                self.__qsearch[f] = v
            elif string.find(field, 'qSearchName_') == 0:
                self.__qsearchName[f] = v
            elif string.find(field, 'qSearchOp_') == 0:
                self.__qsearchOp[f] = v
            elif string.find(field, 'qSearchFields_') == 0:
                self.__qsearchFields[f] = v

    def GetOp(self, f):
        """
        return operator from qsearchOp
        or standard ~ if we found none
        """
        if not self.__qsearchOp.has_key(f):
            return '~'
        else:
            return self.__qsearchOp[f]

    def GetqSearchKeys(self):
        return self.__qsearch.keys()

    def GetqSearch(self, f):
        return self.__qsearch[f]

    def GetqSearchNameKeys(self):
        return self.__qsearchName.keys()

    def GetqSearchName(self, f):
        return self.__qsearchName[f]

    def GetqSearchFieldsKeys(self):
        return self.__qsearchFields.keys()

    def GetqSearchFields(self, f):
        return self.__qsearchFields[f]

class qSearchX:

    """
    process qSearchX-fields and generates qSuperSearch
    we delete qSearchX-fields from cgi environment which
    is returned!

    process special form variable qSearch_FIELD=VALUE
    append data from qSearch_FIELD=VALUE to and in format of qSuperSearch
    - standard operator (if none was given) is qSuperSearch ~ (LIKE)
    - append qSuperSearch : (AND) if we have more than one qSearch
    """

    def __init__(self, qsb):
        """ takes a qSearchBase instance as argument """
        self.__qsb = qsb

    def Process(self):
        qss = '('
        i = 0
        for a in self.__qsb.GetqSearchKeys():
            op = self.__qsb.GetOp(a)
            if op == '~':
                qss = '%s%s~\'%%%s%%\'' % (qss, a, self.__qsb.GetqSearch(a))
            else:
                qss = '%s%s%s%s' % (qss, a, op, self.__qsb.GetqSearch(a))
            i = i + 1
            if i < len(self.__qsb.GetqSearchKeys()):
                qss = '%s:' % qss
        qss = '%s)' % qss
        if qss == '()':
            return ''
        else:
            return qss

class qSearchNameX:

    """
    process qSearchNameX-fields and generates qSuperSearch
    we delete qSearchX-fields from cgi environment which
    is returned!

    process special form variable qSearchName_FIELD=VALUE
    append data from qSearch_FIELD=VALUE to and in format of qSuperSearch
    - append bracket (
    - add 'FIELD OP VALUE' for every field
    - append qSuperSearch | (OR) when we have more fields than 
    - append bracket )
    """

    def __init__(self, qsb):
        """ takes a qSearchBase instance as argument """
        self.__qsb = qsb

    def Process(self):
        qss = '('
        i = 0
        for a in self.__qsb.GetqSearchNameKeys():
            i = i + 1
            op = self.__qsb.GetOp(a)
            fields = string.split(self.__qsb.GetqSearchFields(a), ',')
            j = 0
            for f in fields:
                j = j + 1
                if op == '~':
                    qss = '%s%s%s\'%%%s%%\'' % (qss, f, op, self.__qsb.GetqSearchName(a))
                else:
                    qss = '%s%s%s%s' % (qss, f, op, self.__qsb.GetqSearchName(a))
                if j < len(fields):
                    qss = '%s|' % qss
            if i < len(self.__qsb.GetqSearchNameKeys()):
                qss = '%s:' % qss
        qss = '%s)' % qss
        if qss == '()':
            return ''
        else:
            return qss

class IgnoredFields(DMS.Cache.Cache):

    """
    hold ignored cgi fields
    - scans cgi fields for ignore_* saves, and removes
      fields from field storage and returns it
    """

    def __init__(self, q):
        DMS.Cache.Cache.__init__(self)
        self.__q = q

    def Save(self):
        ignf = self.__q.GetKeys()
        if ignf:
            for field in ignf:
                if string.find(field, 'ignore_') == 0:
                    f = string.split(field, '_', 1)[1]
                    if type(self.__q[field]) is types.StringType:
                        v = string.replace(self.__q[field], ' ', '')
                    else:
                        v = self.__q[field]
                    DMS.Cache.Cache.__setitem__(self, f, v)
                    del self.__q[field]

class CGI:

    """ access to cgi and web server environment """

    def __init__(self, debug, req = None, cgiForm = None, qvars = None):
        self.__req = req
        self.__log = Core.Log.File(debug = debug, module = '1[HTTP].CGI')
        if cgiForm is None:
            cgiForm = self.__GetForm()
        if qvars is not None:
            self.__qVars = qvars
            self.__qVars.SetQ(cgiForm)
            #self.__qVars.Parse()
            self.__qVars.SetEnvVars()
        self.__fieldStorage = FieldStorage(cgiForm)
        self.__fieldStorage.Parse()
        ### self.__ignoredFields = IgnoredFields(self.__qVars)
        ### self.__ignoredFields.Save()

    def Init(self):
        qsb = qSearchBase(self.__qVars)
        qsb.Parse()
        qs = qSearchX(qsb)
        qsp = qs.Process()
        if self.GetqVar('qSuperSearch') and qsp:
            self.AppendqVar('qSuperSearch', ':%s' % qsp)
        elif qsp:
            self.SetqVar('qSuperSearch', qsp)
        qsn = qSearchNameX(qsb)
        qsnp = qsn.Process()
        if self.GetqVar('qSuperSearch') and qsnp:
            self.AppendqVar('qSuperSearch', ':%s' % qsnp)
        elif qsnp:
            self.SetqVar('qSuperSearch', qsnp)
        #for i in self.__qVars.keys():
        #    self.__log.Write(msg = 'qvar=%s,value=%s' % (i, self.__qVars[i]))
        #for i in self.__fieldStorage.keys():
        #    self.__log.Write(msg = 'cgifield=%s,value=%s' % (i, self.__fieldStorage[i]))

    def __GetForm(self):
        """
        process form values
        - if we find a list, generate comma separated values
        - check for file uploads
        """
        qs = {}
	form = util.FieldStorage(self.__req)
        #form = cgi.FieldStorage()
        #self.__log.Write(msg = 'ANALYZING FOLLOWING FIELDS: %s' % form.list)
        for field in form.keys():
            #self.__log.Write(msg = 'ANALYZING %s' % field)
            if field:
                if type(form[field]) is types.ListType:
                    #self.__log.Write(msg = 'TYPE=LIST')
                    #i = 0
                    qs[field] = []
                    for item in form[field]:
                        #qs[field].append(item.value)
                        qs[field].append(item)
                        #if i > 0:
                        #    qs[field] = qs[field] + ', ' + item.value
                        #else:
                        #    qs[field] = item.value
                        #i = i + 1
                #elif form[field].filename:
                #    qs['UploadName_%s' % field] = form[field].filename
                #    qs['UploadContent_%s' % field] = form[field].value
                elif isinstance(form[field], util.Field):
                    #self.__log.Write(msg = 'FORM FIELD IS A FILE: FILENAME=%s CONTENT-LENGHT=%s'
                    #                 % (form[field].filename, len(form[field].value)))
                    qs['UploadName_%s' % field] = form[field].filename
                    #qs['UploadContent_%s' % field] = form[field].file
                    qs['UploadContent_%s' % field] = form[field].value
                else:
                    #self.__log.Write(msg = 'TYPE=NORMAL')
                    qs[field] = form[field] #.value
        #self.__log.Write(msg = 'DONE')
        return qs

    def AppendIgnoredFields(self):
        """ append ignored cgi fields to self.cgiFieldStorage """
        for field in self.__ignoredFields.keys():
            self.SetField(field, self.__ignoredFields[field])

    def GetqVar(self, var, returnType = None):
        return self.__qVars.Get(var, returnType)

    def RetrieveqVar(self, r):
        """ retrieve qvars beginning with r """
        return self.__qVars.Retrieve(r)

    def SetqVar(self, var, value):
        self.__qVars.Set(var, value)

    def RemoveqVar(self, var):
        self.__qVars.Remove(var)

    def AppendqVar(self, var, value):
        self.__qVars.Append(var, value)

    def CopyqVar(self, var, varNew):
        """ copy var to varNew """
        self.__qVars.Copy(var, varNew)

    def RenameqVar(self, var, name):
        """ sets name of var to name and removes old field """
        self.__qVars.Rename(var, name)

    def GetqVarKeys(self):
        return self.__qVars.GetKeys()

    def GetField(self, var, returnType = None):
        """ return value of cgi field """
        if self.__fieldStorage.has_key(var):
            return self.__fieldStorage[var]
        else:
            return returnType

    def RetrieveField(self, r):
        """ retrieve cgi field beginning with r """
        return self.__fieldStorage.Retrieve(r)

    def SetField(self, var, value):
        self.__fieldStorage[var] = value

    def CopyField(self, var, varNew):
        """ sets name of var to name """
        self.__fieldStorage[varNew] = self.__fieldStorage[var]

    def RenameField(self, var, name):
        """ sets name of var to name and removes old field """
        self.CopyField(var, name)
        del self.__fieldStorage[var]

    def RemoveField(self, var):
        if self.__fieldStorage.has_key(var):
            del self.__fieldStorage[var]
        #self.__log.Write(msg = 'REMOVED FIELD %s. FIELDS LEFT=%s' % (var, self.__fieldStorage.keys()))

    def GetFieldKeys(self):
        return self.__fieldStorage.keys()

class UploadFile:

    """ a file to upload """

    def __init__(self, ident = '', filename = '', content = '', dir = ''):
        self.__ident = ident
        self.__filename = filename
        self.__content = content
        self.__dir = dir

    def SetIdent(self, ident):
        """ the ident """
        self.__ident = ident

    def GetIdent(self):
        return self.__ident

    def SetFilename(self, filename):
        """ the filename """
        self.__filename = filename

    def GetFilename(self):
        return self.__filename

    def SetContent(self, content):
        """ the file descriptor / content of file """
        self.__content = content

    def GetContent(self):
        return self.__content
    
    def SetDir(self, dir):
        """ directory to put uploaded file """
        self.__dir = dir

    def GetDir(self):
        return self.__dir

    def Save(self):
        """ save a file """
        fd = open(self.__filename, 'w')
        fd.write(self.__content)
        fd.close()

class Upload:

    """ upload a file per http """

    def __init__(self):
        self.__requests = []

    def AddRequest(self, ident, filename, content, dir):
        """ add an upload request """
        self.__requests.append(UploadFile(ident, filename, content, dir))

    def GetRequests(self):
        return self.__requests

    def __str__(self):
        return str(self.__requests)

    def Upload(self):
        """ process all upload requests """
        for request in self.__requests:
            request.Save()
