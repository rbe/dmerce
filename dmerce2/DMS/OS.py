#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.1 $'

import sys
import os
import os.path
import string
import tempfile
import Core.Log
import DMS.Cache
import DMS.HTTP
import DMS.MBase

class File(DMS.MBase.Class):

    """ file system functions """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = 'Modules.OS')
        self.__cache = DMS.Cache.Cache()

    def Exists(self, fn):
        """ does file DOCUMENT_ROOT/fn exist? """
        fn = '%s/%s' % (os.environ['DOCUMENT_ROOT'], fn)
        try:
            if not self.__cache[fn] is None:
                self.__log.Write(submodule = 'Exists', msgType = 'INFO',
                                 msg = 'EXISTANCE OF FILE %s WAS CACHED: %s' % (fn, str(self.__cache[fn])))
            else:
                os.stat(fn)
                self.__cache[fn] = 1
        except OSError:
            self.__cache[fn] = 0
            self.__log.Write(submodule = 'Exists', msgType = 'WARNING',
                             msg = 'FILE %s DOES NOT EXIST' % fn)
        return self.__cache[fn]

    def __UploadCheckSaveAt(self):
        """ upload: check cgi environment for information """
        saveAt = self._cgi.GetqVar('qUploadSaveAt')
        if saveAt is None:
            saveAt = self._prjcfg['FileUploadDir']
        if not os.access(saveAt, 7):
            self.__log.Write(submobule = 'Upload', msg = 'DIRECTORY %s NOT WRITEABLE FOR ME. SAVING FILE IN /tmp'
                             % saveAt)
            saveAt = '/tmp'
        return saveAt

    def __UploadAnalyseRequests(self):
        """ analyse upload requests """
        filesToUpload = self._cgi.RetrieveField('UploadName_')
        if not filesToUpload:
            self.__log.Write(msgType = 'INFO', msg = 'NOTHING TO UPLOAD!')
            return None
        uploadReq = DMS.HTTP.Upload()
        for fileKey in filesToUpload.keys():
            file = filesToUpload[fileKey]
            ident = string.split(fileKey, '_', 1)[1]
            exts = string.split(os.path.basename(self._cgi.GetField('UploadName_%s' % ident)), '.')
            ext = exts[len(exts) - 1]
            saveAt = self.__UploadCheckSaveAt()
            tmp = self._sam.Check() + string.replace(string.split(tempfile.mktemp(), '/')[3], '@', '')
            newFilename = '%s/%s.%s' % (saveAt, tmp, ext)
            uploadReq.AddRequest(ident,
                                 newFilename,
                                 self._cgi.GetField('UploadContent_%s' % ident),
                                 saveAt)
        return uploadReq

    def Upload(self):
        """ upload a file to webserver """
        requests = self.__UploadAnalyseRequests()
        if not requests:
            return 1
        #for r in requests.GetRequests():
        #    self.__log.Write(msg = 'UPLOAD REQUEST FOR %s' % r.GetFilename())
        try:
            for request in requests.GetRequests():
                self.__log.Write(submodule = 'Upload', msgType = 'INFO', msg = 'SAVING FILE %s'
                                 % request.GetFilename())
                request.Save()
                self._cgi.RemoveField('UploadContent_%s' % request.GetIdent())
                self._cgi.RenameField('UploadName_%s' % request.GetIdent(), request.GetIdent())
                self._cgi.SetField(request.GetIdent(), os.path.basename(request.GetFilename()))
            return 1
        except:
            import traceback
            return traceback.print_exc(file = sys.stdout) # RAISE!!!
