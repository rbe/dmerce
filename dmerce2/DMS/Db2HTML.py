#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.8 $'
#
# Revision 1.1  2000-07-13 17:05:50+02  rb
# Initial revision
#
##

import types
import string
import Core.Log
import DMS.Object
import DMS.SuperSearch

class Select:

    """ process <select>-tags """

    def __init__(self, kw):
        self.__log = Core.Log.File(debug = kw['debug'], module = '1[Db2HTML]')
        self.__sql = kw['sql']

    def Options(self, table, qSuperSearch, optionValue, optionName, length = None, selected = 0):
        """ generate <option>-fields out of a database table """
        objConv = DMS.Object.Convert()
        qss = DMS.SuperSearch.SuperSearch(qSuperSearch)
        optionName = string.split(optionName, ',')
        stmt = "SELECT %s, " % optionValue
        for item in range(len(optionName)):
            stmt = stmt + optionName[item]
            if item < len(optionName) - 1:
                stmt = stmt + ", "
        if string.find(table, '__') > 0:
            schema, schemaTable = string.split(table, '__') # do not overwrite orig var
            table = schema + '.' + schemaTable
        stmt = stmt + " FROM %s WHERE %s" % (table, qss.ConvertToSQLWhere())
        rc, r = self.__sql[stmt]
        if not rc:
            return 0
        optionList = []
        for i in range(rc):
            on = ''
            for item in optionName:
                on = on + ' ' + objConv.NumberToString(r[i][item])
            text = ''
            appStr = '<option value="%s"' % objConv.NumberToString(r[i][optionValue])
            if r[i][optionValue] == selected:
                appStr = '%s selected' % appStr
            if length:
                if len(on) > length:
                    text = on[:length - 3] + '...'
            if not text:
                text = on
            appStr = '%s>%s</option>' % (appStr, text)
            optionList.append(appStr)
        return optionList

class Checkbox:

    """ process checkboxes """

    def __init__(self, kw):
        self.__log = Core.Log.File(debug = kw['debug'], module = '1[Db2HTML].Checkbox')
  
    def isChecked(self, expr = None):
        """ return '' or 'checked' """
        if expr:
            return 'checked'
        else:
            return ''

class Radiobox:

    """ process radioboxes """

    def __init__(self, kw):
        self.__log = Core.Log.File(debug = kw['debug'], module = '1[Db2HTML].Radiobox')
  
    def isChecked(self, expr = None):
        """ return '' or 'checked' """
        if eval(str(expr)):
            return 'checked'
        else:
            return ''

