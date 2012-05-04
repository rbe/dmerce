#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.11 $'
#
##

import sys
import os
import stat
import string
import Core.Error
import Core.Log
import DMS.Object

class Template:

    """
    represent information about template
    and make it accessible
    """

    def __init__(self, sqlSys, prjcfg):
        self.__sqlSys = sqlSys
        self.__templateTable = 'Templates'
        if self.__sqlSys.GetType() != 'POSTGRESQL':
            self.__templateTable = 'dmerce_sys.' + self.__templateTable
        self.__fqhn = None
        self.__fqtn = None
        self.__templateRoot = None
        self.__fd = None
        self.__log = Core.Log.File(debug = prjcfg['debug'], module = '1[Template]')

    def Init(self):
        oc = DMS.Object.Convert()
        """ get informations about template from system database """
        stmt = "SELECT SAM, SAMFailUrl, GivenFields, IgnoreEmptyFields," \
               "       EmailFrom, EmailSubject" \
               "  FROM %s" \
               " WHERE FQHN = '%s'" \
               "   AND FQTN = '%s'" % (self.__templateTable, self.__fqhn, self.__fqtn)
        rc, r = self.__sqlSys[stmt]
        if rc:
            self.__info = oc.StripWhitespaces(r[0])
        else:
            self.__info = None

    def __getitem__(self, item):
        """ return an information about template """
        if self.__info:
            return self.__info[item]
        else:
            return None

    def SetFQHN(self, fqhn):
        """ set fully qualified host name """
        self.__fqhn = fqhn

    def GetFQHN(self):
        """ get fully qualified host name """
        return self.__fqhn

    def SetFQTN(self, fqtn):
        """ set fully qualified template name """
        self.__fqtn = fqtn

    def GetFQTN(self):
        """ get fully qualified template name """
        return self.__fqtn

    def SetTemplateRoot(self, tr):
        """ set template root in which template resides """
        self.__templateRoot = tr

    def __GenFilename(self):
        """ construct name of template and check access """
        try:
            mode = os.stat(self.__templateRoot)[stat.ST_MODE]
            qTemplateIdent = string.split(self.GetFQTN(), ',')
            tmplFile = qTemplateIdent[-1:][0]
            tfn = '%s%s/%s' % (self.__templateRoot, string.join(qTemplateIdent[:-1], '/'), tmplFile)
            return '%s.html' % tfn
        except:
            self.__log.Write(submodule = '__GenFilename', msgType = 'ERROR', \
                             msg = 'CANNOT CREATE TEMPLATE FILENAME IN ROOT %s: %s %s' \
                             % (self.__templateRoot, sys.exc_info()[0], sys.exc_info()[1]))
            return None

    def OpenTemplate(self):
        """ try to open template """
        self.__tfn = self.__GenFilename()
        try:
            self.__fd = open(self.__tfn, 'r')
        except:
            self.__log.Write(submodule = '__OpenTemplate', msgType = 'ERROR', \
                             msg = 'FAILED TO OPEN TEMPLATE %s' % self.__tfn)
            raise Core.Error.NoValidTemplateGivenError(1, self.__tfn)

    def ReadTemplate(self):
        """ read lines from template into list """
        self.__lines = self.__fd.readlines()

    def GetLines(self):
        """ return list of read lines """
        return self.__lines

class SAM:

    """ SAM for a template """
    pass
