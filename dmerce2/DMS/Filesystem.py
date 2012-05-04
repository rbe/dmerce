#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 1.16 $'
#
##

#import sys
#sys.path.append('/export/home/r/rb/dmerce2')
import string
import time
import os.path
#import Core.OS
import Core.Log
import DMS.MBase
import DMS.Cache

class GlobDirectoryWalker:

    """ a forward iterator that traverses a directory tree """

    def __init__(self, directory, pattern="*"):
        self.stack = [directory]
        self.pattern = pattern
        self.files = []
        self.index = 0

    def __getitem__(self, index):
        """ walk through a directory """
        while 1:
            try:
                file = self.files[self.index]
                self.index = self.index + 1
            except IndexError:
                # pop next directory from stack
                self.directory = self.stack.pop()
                try:
                    self.files = os.listdir(self.directory)
                except:
                    pass
                self.index = 0
            else:
                # got a filename
                try:
                    fullname = os.path.join(self.directory, file)
                    if os.path.isdir(fullname) and not os.path.islink(fullname):
                        self.stack.append(fullname)
                    if fnmatch.fnmatch(file, self.pattern):
                        return fullname
                except:
                    pass

class DirectoryWalk:

    """ walk over directories and perform several activities """

    def __init__(self):
        self.__walk = {}

    def Add(self, dir, func, args):
        t = time.time()
        os.path.walk(dir, func, args)
        self.__walk[t] = args[1]
        return t

    def Get(self, t):
        return self.__walk[t]

    def List(self, arg, dirname, fileList):
        (pat, modifiedList) = arg
        for file in fileList:
            for f in [os.path.join(dirname, file)]:
                try:
                    if pat:
                        for p in pat:
                            if string.find(file, p) >= 0:
                                modifiedList.append(f)
                    else:
                        modifiedList.append(f)
                except:
                    pass

    def mtimeChecker(self, arg, dirname, fileList):
        (mtime, modifiedList) = arg
        for file in fileList:
            for f in [os.path.join(dirname, file)]:
                try:
                    if os.path.getmtime(f) > mtime:
                        modifiedList.append(f)
                except:
                    pass

    def SearchFiletype(self, arg, dirname, files):
        (ext, modifiedList) = arg
        for file in files:
            for f in [os.path.join(dirname, file)]:
                try:
                    for item in ext:
                        if string.find(file, item) >= 0:
                            modifiedList.append(f)
                except:
                    pass

class Directory(DMS.MBase.Class):

    """ functions to deal with directories """

    def __init__(self, kw = {}):
        if kw:
            DMS.MBase.Class.__init__(self, kw)
        self.__w = DirectoryWalk()
        self.__dirCache = DMS.Cache.Cache()
        self.__log = Core.Log.File(module = '1[Filesystem].Directory')

    def SearchFiletype(self, dir, ext = []):
        t = self.__w.Add(dir, self.__w.SearchFiletype, [ext, []])
        self.__dirCache[dir] = self.__w.Get(t)
        return self.__dirCache[dir]

    def ListToHTML(self, dir, pattern = '', htmlBefore = '', htmlAfter = '',
                   fnAsOptionValue = 0, baseFnAsOptionValue = 0,
                   link = '', img = 0, name = 0, breakBeforeName = 0, basename = 0):
        """ (exec): generate listing of directory with jpg/gif-files """
        s = ''
        #osBase = Core.OS.Base()
        #documentRoot = osBase.Env('DOCUMENT_ROOT')
        documentRoot = self._httpEnv['DOCUMENT_ROOT']
        #documentRoot = '/'
        dir = documentRoot + dir
        self.__log.Write(msg = 'SCANNING DIRECTORY=%s' % dir)
        if not self.__dirCache.has_key(dir):
            t = self.__w.Add(dir, self.__w.List, [pattern.split(','), []])
            self.__dirCache[dir] = self.__w.Get(t)
        files = self.__dirCache[dir]
        files.sort()
        for file in files:
            s = s + '\n\n'
            fn = file.replace(documentRoot, '')
            baseFn = os.path.basename(fn)
            if htmlBefore:
                s = s + htmlBefore
            if link:
                s = s + '<a href="%s&filename=%s">' % (link, fn)
            if img:
                s = s + '<img border="0" src="%s">' % fn
            elif fnAsOptionValue:
                s = s + '<option value="%s">' % fn
            elif baseFnAsOptionValue:
                s = s + '<option value="%s">' % baseFn
            if name:
                if not breakBeforeName:
                    s = s + '%s' % fn
                else:
                    s = s + '<p>%s</p>' % fn
            elif basename:
                s = s + '%s' % baseFn
            if fnAsOptionValue or baseFnAsOptionValue:
                s = s + '</option>'
            if link:
                s = s + '</a>'
            if htmlAfter:
                s = s + htmlAfter
            s = s + '\n'
        return s

#d = Directory()
#print d.ListToHTML(dir = '/export/home/r/rb/dmerce2', pattern = '.py,.txt', htmlBefore='<table width=\"650\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#FFFFFF\"><tr><td width=\"25\"><img src=\"/pic/dummy.gif\" width=\"25\" height=\"20\" border=\"0\" alt=\"\"></td><td width="625" class="ListEntry" align="center">',htmlAfter='</td></tr><tr><td width="25"><img src="/pic/dummy.gif" width="25" height="5" border="0" alt=""></td><td colspan="3"><img src="/pic/dot_orange_dark.gif" width="625" height="1" border="0"></td></tr></table>', name=0,img=1,fnAsOptionValue=0)
