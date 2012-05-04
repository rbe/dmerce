#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.28 $'
#
# Revision 1.1  2000-07-04 22:30:41+02  rb
# Initial revision
#
##

import re

SuperSearch = "a-zA-Z%s%s%s%s%s%s%s0-9'! /*+_\-~|:.,()\[\]@\{\}%%=\#&" \
              % (chr(196), chr(214), chr(220), chr(223), chr(228), chr(246), chr(252))
qSuperSearch = '"([%s]*)"' % SuperSearch
xref = '"([%s]*)"{0,1}' % SuperSearch
q = '"([%s]*)"' % SuperSearch

macros = {
    'form'              : re.compile('\(form[ ]+(\w+)\)'),
    'var'               : re.compile('\(var[ ]+(\w+)\)'),
    'var-rav'           : re.compile('\(var[ ]+(\w+)[ ]+rav\)'),
    'set-myvar'         : re.compile('\(set-myvar[ ]+(\w+)[ ]+(.*?)\)'),
    'get-myvar'         : re.compile('\(get-myvar[ ]+(\w+)\)'),
    'append-myvar'      : re.compile('\(append-myvar[ ]+(\w+)\)'),
    'include'           : re.compile('\(include[ ]+(\S+)\)'),
    'q'                 : re.compile('\(q[ ]+%s[ ]+([a-zA-Z0-9.]*)\)' % q),
    'who'               : re.compile('\(who[ ]+(\w+)\)'),
    'sam-gen-new-sess'  : re.compile('\(sam gen-new-sess\)'),
    'sql-count'         : re.compile('\(sql[ ]+count[ ]+(\w+)[ ]+%s\)' % qSuperSearch),
    'sql-rowcount'      : re.compile('\(sql[ ]+rowcount[ ]*([0-9]*)\)'),
    'sql-index'         : re.compile('\(sql[ ]+(index)[ ]*(\d+){0,1}\)'),
    'sql-index1'        : re.compile('\(sql[ ]+(index1)\)'),
    'sql-get'           : re.compile('\(sql[ ]+(\w+__){0,1}(\w+)\.(\w+)[ ]*([0-9]+){0,1}-{0,1}([0-9]+){0,1}\)'),
    'sql-getf'          : re.compile('\(sqlf[ ]+(\w+__){0,1}(\w+)\.(\w+)[ ]*([0-9]+){0,1}-{0,1}([0-9]+){0,1}\)'),
    'sql-get-xref'      : re.compile('\(sql[ ]+(\w+__){0,1}(\w+)\.(\w+)[ ]*%s[ ]*([0-9]+){0,1}-{0,1}([0-9]+){0,1}\)'
                                     % xref),
    'sql-getf-xref'     : re.compile('\(sqlf[ ]+(\w+__){0,1}(\w+)\.(\w+)[ ]*%s[ ]*([0-9]+){0,1}-{0,1}([0-9]+){0,1}\)'
                                     % xref),
    'sql-dboid'         : re.compile('\(sql[ ]+dboid[ ]+([a-zA-Z0-9_]+)\)'),
    'sql-inserteddboid' : re.compile('\(sql-inserted-dboid[ ]+(\w+)\)'),
    'sql-select'        : re.compile('\(sql[ ]+(SELECT[ ]+.*)[ ]+lqs\)'),
    'exec'              : re.compile('\(exec[ ]+(\w+)\.(\w+)\.(\w+)([ ]*.*?)[ ]+cexe\)'),    
    'if'                : re.compile('\(if[ ]+(.*)\)'),
    'else'              : re.compile('\(else\)'),
    'endif'             : re.compile('\(endif\)'),
    'repeat'            : re.compile('\(repeat[ ]+"(.*)"\)'),
    'endrepeat'         : re.compile('\(endrepeat\)'),
    'set-skin'          : re.compile('\(set-skin[ ]+(\S+)\)'),
    'skin-img'          : re.compile('\(skin-img[ ]+(\S+)\)'),
    'skin-imgtag'       : re.compile('\(skin-imgtag[ ]+(\S+)\)'),
    'skin-stylesheet'   : re.compile('\(skin-stylesheet\)')
    }

# planned macros: inc, dec, bool, true, false
#s = '(sql masy_4__t.f "PolKz*MS-EX 66")'
#l = macros['sql-get-xref'].match(s)
#print l.groups()
