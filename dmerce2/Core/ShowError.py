#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.13 $'
#
##

import sys
import time
import os
import os.path
import tempfile
import string
import cgi
import Log
import ErrorMessages

class Handler:

    """ write an error page to file """

    """ HTML """
    page_header = [
        '<html>', \
        '<head>', \
        '<title>1Ci - dmerce - Error</title>', \
        '<style>', \
        '  body, td, th {', \
        '    background: #000000;', \
        '    color: #ffffff;', \
        '    font-family: Arial, Helvetica, Helv;', \
        '    font-size: 9pt;', \
        '  }', \
        '  th {', \
        '    background: #0000ff;', \
        '  }', \
        '  th.l {', \
        '    text-align: left;', \
        '  }', \
        '  th.r {', \
        '    text-align: right;', \
        '  }', \
        '  td {', \
        '    background: #0055ff;', \
        '  }', \
        '  td.black {', \
        '    background: #000000;', \
        '  }', \
        '  td.bold {', \
        '    font-weight: bold;', \
        '  }', \
        '</style>', \
        '</head>', \
        '<body>', \
        '<table border="0" cellspacing="0" cellpadding="3">', \
        '  <tr>', \
        '    <td class="black" rowspan="7">', \
        '      <a target="_new" href="http://www.1ci.de"><img border="0" width="179" height="52" src="http://www.1ci.de/images/1ci_logo_s_200.gif"></a>', \
        '    </td>', \
        '  </tr>', \
        '  <tr>', \
        '    <th colspan="2">An unhandled exception occured with your application</th>', \
        '  </tr>', \
        '  <tr>', \
        '    <td valign="top">Error message:</td>'
        ]

    page_error = '    <td class="bold">%s %s: %s</td>\n'

    page_error2 = [
        '  </tr>', \
        '  <tr>', \
        '    <th colspan="2">Detailed information</th>', \
        '  </tr>', \
        '  <tr>', \
        '    <td>Raised:</td>\n' \
        '    <td>%s</td>\n' \
        '  </tr>', \
        '  <tr>', \
        '  <td valign="top">Error occured in:</td>', \
        '    <td><pre>'
        ]

    page_footer = [
        '</pre></td>', \
        '</tr>', \
        '</table>', \
        '</p>', \
        '</body>', \
        '</html>'
        ]

    def __init__(self, tb = None, fn = None, httpEnv = None):
        #self.__debug = debug
        self.__httpEnv = httpEnv
        self.__fn = fn
        self.__lines = []
        if tb is not None:
            self.__trace = tb
            self.__etrace = tb.extracted
            self.__umsg = tb.userMessage
        if fn is not None:
            self.OpenFile(fn)
        self.__err = ErrorMessages.ErrMsg()
        self.__log = Log.File(debug = 1, module = '1[Core].ShowError')

    def SetId(self):
        #self.__id = str(time.time()) + string.replace(os.path.basename(tempfile.mktemp()), '@', '')
        self.__id = str(time.time())

    def GetId(self):
        return self.__id

    def OpenFile(self, fn = None):
        """
        open a file
        if argument fn is not given, create temporary file and open it for writing
        else open the given file for reading
        """
        dr = self.__httpEnv['DOCUMENT_ROOT']
        if fn is None:
            self.SetId()
            self.__fn = '%s/templates/error/%s.html' % (dr, self.GetId())
            self.fd = open(self.__fn, 'w')
        else:
            self.__fn = '%s/templates/error/%s.html' % (dr, fn)
            self.fd = open(self.__fn, 'r')

    def GetFilename(self):
        return self.__fn

    def CloseFile(self):
        self.fd.close()

    def DeleteFile(self):
        os.unlink(self.__fn)

    def WriteHeader(self):
        for line in self.page_header:
            self.__lines.append(line)

    def WriteFooter(self):
        for line in self.page_footer:
            self.__lines.append(line)

    def WriteMessage(self, msg = None):
        """ print (detailed) error information in a HTML page """
        self.__lines.append(self.page_error % (self.__umsg.GetNumber(),
                                               self.__umsg.GetMessage(),
                                               self.__umsg.GetMessageOpt()))

    def WriteDetails(self):
        for line in range(len(self.page_error2)):
            if line == 5:
                e = str(self.__trace.GetRaisedError())
                self.__lines.append(self.page_error2[line] % e.replace('Core.Error.', ''))
            else:
                self.__lines.append(self.page_error2[line])
        self.__etrace.Summarize()
        order, sum = self.__etrace.GetSum()
        for item in order:
            self.__lines.append('%sc %s %i %s\n' % (item[0], sum[item][0], sum[item][1],
                                                    sum[item][2]))

    def WriteFile(self):
        """ write error message to file """
        for line in self.__lines:
            self.fd.write(line + '\n')
            
    def Log(self):
        """ log error to dmerce log """
        msg = '%s:%s@%s %s:%s' % (self.__trace.GetRaisedError(),
                                  self.__etrace.GetModuleName(),
                                  self.__etrace.GetLineNo(),
                                  self.__umsg.GetNumber(),
                                  self.__umsg.GetMessage())
        mo = self.__umsg.GetMessageOpt()
        if mo:
            msg = msg + ': ' + mo
        self.__log.Write(submodule = '', msgType = 'ERROR', msg = msg)

    def ReadPage(self):
        """ read error page """
        self.__lines = self.fd.readlines()

    def ShowPage(self):
        """ show an error page """
        #sys.stdout.write('Content-type: text/html\n\n')
        try:
            for line in self.__lines:
                sys.stdout.write(line)
            return 1
        except:
            sys.stdout.write('Error page %s cannot be viewed.' % self.__fn)
            return 0

# M A I N
if __name__ == '__main__':
    c = cgi.FieldStorage()
    se = Handler(fn = c['p'].value)
    se.ReadPage()
    if se.ShowPage():
        #se.DeleteFile()
        pass
    se.CloseFile()
