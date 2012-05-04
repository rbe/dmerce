#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.22 $'
#
##
import sys
import time
import string
import StringIO
import smtplib
import rfc822
import base64
import MimeWriter
import Core.Log
import Core.OS
import DMS.Cache
import DMS.MBase

class EmailHeader:

    """ a header for an email """

    def __init__(self):
        self.__header = DMS.Cache.Cache()

    def GenerateDate(self):
        self.__header['Date'] = time.strftime("%d %b %Y %H:%M:%S %Z", time.localtime(time.time()))

    def SetFrom(self, f):
        self.__header['From'] = f

    def GetFrom(self):
        return self.__header['From']

    def SetTo(self, t):
        self.__header['To'] = t

    def GetTo(self):
        return self.__header['To']

    def SetCc(self, t):
        """ list of cc addresses """
        self.__header['Cc'] = t

    def AddCc(self, t):
        """ add cc address """
        self.__header['Cc'].append(t)

    def GetCc(self):
        return self.__header['Cc']

    def SetBcc(self, t):
        """ list of cc addresses """
        self.__header['Bcc'] = t

    def AddBcc(self, t):
        """ add cc address """
        self.__header['Bcc'].append(t)

    def GetBcc(self):
        return self.__header['Bcc']

    def SetSubject(self, s):
        self.__header['Subject'] = s

    def GetSubject(self):
        return self.__header['Subject']

    def SetReplyTo(self, s):
        self.__header['Reply-To'] = s

    def GetReplyTo(self, s):
        return self.__header['Reply-To']

    def SetHeader(self, h, v):
        """ set additional header """
        self.__header[string.capitalize(h)] = v

    def GetHeader(self, h):
        """ get additional header """
        return self.__header[string.capitalize(h)]

    def SetXHeader(self, h, v):
        """ set additional X-header """
        self.__header['X-' + string.capitalize(h)] = v

    def GetXHeader(self, h):
        """ get additional X-header """
        return self.__header['X-' + string.capitalize(h)]

    def GetAllHeadersAsString(self):
        allHeaders = ''
        for item in self.__header.keys():
            allHeaders = allHeaders + item + '=' + self.__header[item] + ' '
        return allHeaders

    def CreateAllRcpt(self):
        self.__allRcpt = []
        for i in string.split(self.__header['To'], ','):
            if i != '':
                self.__allRcpt.append(i)
        for i in string.split(self.__header['Cc'], ','):
            if i != '':
                self.__allRcpt.append(i)
        for i in string.split(self.__header['Bcc'], ','):
            if i != '':
                self.__allRcpt.append(i)

    def GetAllRcpt(self):
        return self.__allRcpt

    def Check(self):
        """ check if all neccessary attributes are set """
        ok = 1
        if not self.__header.has_key('From'):
            ok = 0
        if not self.__header.has_key('To'):
            ok = 0
        if not self.__header.has_key('Subject'):
            ok = 0
        if not self.__header.has_key('Date'):
            ok = 0
        return ok

    def Get(self, w = 1):
        """ return header as string with newlines """
        s = ''
        for h in self.__header.keys():
            if not w and h == 'To' and h == 'From':
                continue
            s = s + h + ': ' + string.strip(self.__header[h]) + '\n'
        return s

    def __str__(self):
        return self.Get()

class EmailAttachments:

    """ all attachments for an email """

    def __init__(self):
        self.__output = StringIO.StringIO()
        self.__m = MimeWriter.MimeWriter(self.__output)
        self.__m.addheader('MIME-Version', '1.0')
        self.__m.flushheaders()
        self.__m.startmultipartbody('mixed')
        text = m.nextpart()
        t = text.startbody('text/plain')
        text.flushheaders()

    def AddAttachment(self):
        attach = self.__m.nextpart()
        attach.addheader('Content-Transfer-Encoding', 'base64')
        attach.addheader('Content-Disposition', 'attachment; filename="%s"' % filename)
        f = attach.startbody('application/octet-stream; name="%s"' % filename)
        attach.flushheaders()
        base64.encode(open(path + filename, 'r'), f)

    def Last(self):
        self.__m.lastpart()

class EmailSend:

    """ functions for sending mails """

    def __init__(self, header = None, body = None):
        self.__log = Core.Log.File(module = 'Messaging.SendMail')
        self.__header = header
        self.__body = body

    def SetHeader(self, header):
        self.__header = header

    def GetHeader(self):
        return self.__header

    def SetBody(self, body):
        self.__body = body

    def GetBody(self):
        return self.__body

    def LogSuccess(self):
        self.__log.Write(submodule = 'EmailSend', msgType = 'INFO', msg = 'SENDING MESSAGE: HEADERS: %s'
                         % self.__header.GetAllHeadersAsString())

    def Send(self):
        """
        send email per smtp
        fromaddr = string, toaddr = list of strings, subject = string, message = string
        """
        #try:
        server = smtplib.SMTP('localhost')
        server.set_debuglevel(0)
        server.sendmail(self.__header.GetFrom(), self.__header.GetAllRcpt(), self.Get())
        server.quit()
        self.LogSuccess()
        return 1
        #except:
        #    self.__log.Write(submodule = 'SendMail', msgType = 'ERROR', msg = 'ERROR SENDING MAIL: %s %s' \
        #                     % (sys.exc_info()[0], sys.exc_info()[1]))

    def Get(self):
        """ build and return message """
        msg = str(self.__header)
        msg = msg + '\n'
        for b in self.__body:
            msg = msg + b #+ '\n'
        return msg

    def __str__(self):
        return self.Get()

class FileContentAsMail(EmailSend):

    """ send file content as body of mail """

    def __init__(self, header = None, body = None):
        self.__log = Core.Log.File(module = 'Messaging.SendFileContentAsMail')

    def Send(self):
        """
        send file content as email
        fromaddr = string, toaddr = list of strings, subject = string, file = string
        """
        # Open file for reading
        fileContent = open(file, 'r').readlines()
        # Construct message as string form list fileContent
        msg = '' # Init msg
        for line in fileContent:
            # Do we have a subject?
            if not subject:
                # We found it on the first position of the line (important!)
                if string.find(line, 'Subject:') == 0:
                    subject = line[9:]
            else:
                # Subject already found, add line to message body
                msg = '%s%s' % (msg, line)
        # Send read lines as mail
        #self.SendMail(fromaddr = fromaddr, toaddr = toaddr, subject = subject, message = msg)

class Memo:

    """ working with memos """

    def __init__(self, **kw):
        self.__sql = kw['sql']
        self.__log = Core.Log.File(module = 'DMS.Messaging.Memo')

    def CountOf(self, Tabelle, TabelleID):
        """ does table Memo has entries for table 'Tabelle'? """
        try:
            rc, r = self.__sql["SELECT COUNT(*) FROM Memo WHERE Tabelle = '%s' AND TabelleID = %i" \
                               % (Tabelle, TabelleID)]
            return r[0]['COUNT(*)']
        except:
            self.__log.write(msgType = 'ERROR', msg = '')
            return '[error]'

class Email(DMS.MBase.Class):

    """ dmerce (exec) function for sending email messages """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)

    def __GetValuesFromForm(self):
        """ return some values from form variables """
        newBccAddr = ''
        template = self._cgi.GetqVar('qEmailSendTemplate', '')
        toAddr = self._cgi.GetqVar('qEmailSendToAddr', '')
        ccAddr = self._cgi.GetqVar('qEmailSendCcAddr', '')
        bccAddr = self._cgi.GetqVar('qEmailSendBccAddr', '')
        fromAddr = self._cgi.GetqVar('qEmailSendFromAddr', '')
        subject = self._cgi.GetqVar('qEmailSendSubject', '[sorry, forgot subject]')
        fieldsForQuery = string.split(self._cgi.GetqVar('qEmailSendFieldsForQuery', []), ',')
        tmp = string.split(bccAddr)
        for i in tmp:
            if i != ' ' or i != '':
                newBccAddr = newBccAddr + '%s' % i
        if len(newBccAddr) > 0:
            if newBccAddr[-1] == ',':
                newBccAddr = newBccAddr[:-1] 
        return template, toAddr, ccAddr, newBccAddr, fromAddr, subject, fieldsForQuery

    def __GenerateQuery(self, opt):
        query = ''
        optKeys = opt.keys()
        optKeysLen = len(optKeys)
        if optKeysLen > 0:
            query = query + '&'
            for i in range(optKeysLen):
                query = query + str(optKeys[i]) + '=' + str(opt[optKeys[i]])
                if i < optKeysLen - 1:
                    query = query + '&'
        return query

    def Send(self, template = None, toAddr = '', ccAddr = '', bccAddr = '', fromAddr = '',
             subject = '[sorry, forgot subject]', **opt):
        """
        check values and insert request into dmerce_sys.SendEmail
        Cronjobs.SendEmail.py will care for it
        """
        #osBase = Core.OS.Base()
        if not template and not toAddr and not fromAddr:
            template, toAddr, ccAddr, bccAddr, fromAddr, subject, fieldsForQuery = self.__GetValuesFromForm()
            if fieldsForQuery:
                #self._log.Write(msg = 'FIELDS FOR QUERY=%s' % str(fieldsForQuery))
                opt = {}
                for item in fieldsForQuery:
                    opt[item] = self._cgi.GetField(item)
        query = 'toAddr=' + toAddr + '&ccAddr=' + ccAddr + '&bccAddr=' + bccAddr \
                + '&fromAddr=' + fromAddr + '&subject=' + subject
        query = query + self.__GenerateQuery(opt)
        if template and toAddr and fromAddr:
            if opt.has_key('htmlFormatted'):
                if opt['htmlFormatted'] == 1:
                    htmlFormatted = 1
                else:
                    htmlFormatted = 0
            else:
                htmlFormatted = 0
            if self._sqlSys.GetType() == 'POSTGRESQL':
                table = 'SendEmail'
            else:
                table = 'dmerce_sys.SendEmail'
            os = Core.OS.Base()
            # docRoot = os.Env('DOCUMENT_ROOT')
            docRoot = self._httpEnv['DOCUMENT_ROOT']
            stmt = "INSERT INTO %s" % table + \
                   " (CreatedDateTime, FQHN, PrjDocRoot, Template, Query, HTMLFormatted)" + \
                   " VALUES (%.6f, '%s', '%s', '%s', '%s', %i)" \
                   % (time.time(), self._fqhn, docRoot, template, query, htmlFormatted)
            #self._log.Write(msgType = 'INFO', msg = stmt)
            self._sqlSys[stmt]
            self._log.Write(msgType = 'INFO', msg = 'SCHEDULING EMAIL REQUEST: TEMPLATE=%s FROMADDR=%s TOADDR=%s'
                            'CCADDR=%s BCCADDR=%s SUBJECT=%s QUERY=%s: STMT=%s'
                            % (template, fromAddr, toAddr, ccAddr, bccAddr, subject, query, stmt))
        else:
            self._log.Write(msgType = 'ERROR', msg = 'ARGUMENTS NOT CORRECT: I TRIED TO SCHEDULE EMAIL REQUEST'
                            ' TEMPLATE=%s FROMADDR=%s TOADDR=%s CCADDR=%s BCCADDR=%s SUBJECT=%s QUERY=%s'
                            % (template, fromAddr, toAddr, ccAddr, bccAddr, subject, query))
        return 1
