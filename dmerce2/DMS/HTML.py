#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.20 $'
#
# Revision 1.1  2000-05-11 15:04:06+02  rb
# Initial revision
#
##

#import sys
#sys.path.append('/export/home/r/rb/dmerce2')
#import string
import Core.Log
import DMS.MBase
#import Core.OS

class Head:

    def __init__(self, req):
        self.req = req

    def Refresh(self, destination = '/', duration = 0):
        """ print meta information for changing to another html site """
        self.req.write('Content-type: text/html\n\n'
                       '<html>\n'
                       '<head>'
                       '<meta http-equiv="refresh" content="%i; url=%s">'
                       '</head>\n'
                       '<body>\n'
                       '<a href="%s">Go here.</a>'
                       '</body>\n'
                       '</html>\n'% (duration, destination, destination))

    #def ReturnToReferer(self):
    #    """ return to referer page by http redirect """
    #    m = DMS.OS.Misc()
    #    print '<html><head><link rel="stylesheet" type="text/css" href="style.css"><meta http-equiv="refresh" ' \
    #          'content="0; url=%s"></head></html>' % m.Env('HTTP_REFERER')

class EntitySubst:

    """ substitute plain text to get HTML """

    def __init__(self):
        self.__entities = {
            #'<' : '&lt;',
            #'>' : '&gt;',
            '\r\n' : '<br>'
            }        

    def Forward(self, buf):
        """ return item after convert all entities """
        for e in self.__entities.keys():
            buf = buf.replace(e, self.__entities[e])
        return buf

    def Backward(self, buf):
        """ return item after convert all entities """
        for e in self.__entities.keys():
            buf = buf.replace(self.__entities[e], e)
        return buf

class PlainToHTML(DMS.MBase.Class):
    
    """ convert plain text to html """
    
    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__es = EntitySubst()
        self.__log = Core.Log.File(debug = self._debug, module = '1[HTML].PlainToHTML')

    def Format(self, buf):
        """ convert plain text in buf to HTML """
        return self.__es.Forward(buf)

    def FormatFields(self):
        """ convert plain text from given cgi fields to HTML """
        fields = self._cgi.GetqVar('qFieldsConvertPlainToHTML', '').split(',')
        for field in fields:
            field = field.replace(' ', '')
            fieldContent = self._cgi.GetField(field)
            if fieldContent:
                newContent = self.__es.Forward(fieldContent)
            else:
                newContent = ''
            self._cgi.SetField(field, newContent)
            self.__log.Write(msg = 'CONVERTED PLAIN TO HTML IN FIELD %s FROM=%s TO=%s'
                             % (field, fieldContent, newContent))
        return 1

    def PrintLine(self, line, withoutNL = 0):
        """
        print line with or without '<br>' depending
        on set enviroment variable 'REMOTE_ADDR'
        assuming it's called from a browser
        """
        m = Core.OS.Base()
        if m.Env('REMOTE_ADDR') and not withoutNL:
            print '%s<br>\n' % self.substEntities(line)
        elif m.Env('REMOTE_ADDR') and withoutNL:
            print '%s' % self.substEntities(line)
        elif not m.Env('REMOTE_ADDR') and not withoutNL:
            print line
        elif not m.Env('REMOTE_ADDR') and withoutNL:
            print line,

class HTMLToPlain(DMS.MBase.Class):

    """ convert html to plain text """
    
    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__es = EntitySubst()
        self.__log = Core.Log.File(debug = self._debug, module = '1[HTML].HTMLToPlain')

    def Format(self, buf):
        """ convert plain text in buf to HTML """
        buf2 = self.__es.Backward(buf)
        self.__log.Write(msg = 'CONVERTED "%s" TO "%s"' % (buf, buf2))
        return buf2
        #buf3 = self.__es.Backward(buf2)
        #self.__log.Write(msg = 'CONVERTED "%s" TO "%s"' % (buf, buf3))
        #return buf3

#e = EntitySubst()
#print e.Backward("asdasdasdad<br><br>asdadasdasd")
