#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 1.2.7.1 $
#
# Revision 1.1  2000-07-04 22:30:41+02  rb
# Initial revision
#
##

#
# I M P O R T  M O D U L E S
#
try:
  import sys
  import re
except:
  print '[regExp: ERROR LOADING MODULES: (%s, %s)]' % (sys.exc_info()[0], sys.exc_info()[1])
  sys.exit()

#####################################################################
#
# Regular expressions used by dmerce template language
#
#####################################################################

# 'sam="x"' in <html>-tag
# $who[x]$
# $var[x]$
# $qSearchString[x]$
# $form[<x>]$
# $SQL[<type>,<x>]$
# $<table>.<field>[[,<precision]|[[<position-from],<position-to>]$
# $<table>.<field>;FIELD=VALUE[[,<precision]|[[<position-from],<position-to>]$;...
# $[<package>/][<module>]:<method>([<args>])$
# $if <expr>$
# $endif$
# $repeat$
# $endrepeat$
# $repeat[<x>]$

# formatList: ,{0,}(\d+){0,}-{0,}(\d+){0,}

dTL = {
       'samStage'          : re.compile(' sam="(\d)"'), \
       'who'               : re.compile('\$who\[(\w+)\]\$'), \
       'var'               : re.compile('\$var\[(\w+)\]\$'), \
       'qSuperSearch'      : re.compile('\$qSuperSearch\[(\w+)\]\$'), \
       'qSuperSearch2'     : re.compile('\$qSuperSearch2\[(\w+)\]\$'), \
       'form'              : re.compile('\$form\[(\w+)\]\$'), \
       'SQL'               : re.compile('\$SQL\[(\w+),(\w+)(,.+){0,}\]\$'), \
       'tf'                : re.compile('\$(\w+)\.(\w+),{0,}(\d+){0,}-{0,}(\d+){0,}\$'), \
       'tf_combined'       : re.compile('\%(\w+)\.(\w+),{0,}(\d+){0,}-{0,}(\d+){0,}\%'), \
       'tf_xref'           : re.compile('\$(\w+)\.(\w+);(\w+)=(\w+)\$'), \
       'cmd'               : re.compile('\$(\w+\/){0,}(\w+):(\w+)(\(.*\))\$'), \
       'ifCond'            : re.compile('\$if (.*)\$'), \
       'endifCond'         : re.compile('\$(endif)\$'), \
       'elseCond'          : re.compile('\$(else)\$'), \
       'repeat'            : re.compile('\$(repeat)( \w+,\S+){0,1},{0,}(\d+ times){0,},{0,}(break \d+ .*){0,}\$'), \
       'repeat_times'      : re.compile('(\d+) times'), \
       'repeat_breakCount' : re.compile('break (\d+)'), \
       'repeat_breakCode'  : re.compile('break \d+ (.*)'), \
       'endrepeat'         : re.compile('\$(endrepeat)\$'), \
       'repeatVars'        : re.compile('\$repeat\[(\w+)\]\$')
      }
