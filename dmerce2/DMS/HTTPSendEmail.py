#!/usr/bin/env python

import os
import os.path
import types
import string
import cgi
import Core.Log
import Core.OS
import DMS.Cache

class Env:

    def __init__(self):
        self.__env['req'] = None
        self.__env['DOCUMENT_ROOT'] = ''
        self.__env['REMOTE_ADDR'] = ''
        self.__env['SERVER_ADDR'] = ''
        self.__env['SERVER_PORT'] = 0
	self.__env['SERVER_NAME'] = 'localhost'
        try:
            self.__env['DMERCE_LANG'] = self.__osEnv['DMERCE_LANG']
        except:
            self.__env['DMERCE_LANG'] = 'GERMAN'
        self.__httpEnv['DOCUMENT_ROOT'] = ''
        self.__httpEnv['REFERERER'] = ''

    def GetRefererTemplate(self):
        return ''

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

class CGI:

    """ access to cgi and web server environment """

    def __init__(self, debug, req = None, cgiForm = None, qvars = None):
        self.__req = req
        self.__log = Core.Log.File(debug = debug, module = '1[SENDMAIL].CGI')
        if cgiForm is None:
            cgiForm = self.__GetForm()
        if qvars is not None:
            self.__qVars = qvars
            self.__qVars.SetQ(cgiForm)
            self.__qVars.SetEnvVars()
        self.__fieldStorage = FieldStorage(cgiForm)
        self.__fieldStorage.Parse()

    def __GetForm(self):
        """
        process form values
        - if we find a list, generate comma separated values
        - check for file uploads
        """
        qs = {}
	form = util.FieldStorage(self.__req)
        #self.__log.Write(msg = 'ANALYZING FOLLOWING FIELDS: %s' % form.list)
        for field in form.keys():
            #self.__log.Write(msg = 'ANALYZING %s' % field)
            if field:
                if type(form[field]) is types.ListType:
                    self.__log.Write(msg = 'TYPE=LIST')
                    qs[field] = []
                    for item in form[field]:
                        qs[field].append(item)
                elif isinstance(form[field], util.Field):
                    qs['UploadName_%s' % field] = form[field].filename
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
