#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 1.5 $'
#
##

import time
import socket
import select
import DMS.NwOp

xmlMsg = """
<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>
<LOG>
<ENTRY>
<DATE>1004098003.978141</DATE>
<CLIENT>10.48.30.160</CLIENT>
<PROXY>10.48.35.1</PROXY>
<TEMPLATE>None</TEMPLATE>
<MODULE>DMS.SuperSearch DEBUG</MODULE>
<MESSAGE>CONVERTED</MESSAGE>
</ENTRY>
<ENTRY>
<DATE>1004098004.067834</DATE>
<CLIENT>10.48.30.160</CLIENT>
<PROXY>10.48.35.1</PROXY>
<TEMPLATE>None</TEMPLATE>
<MODULE>Core.ShowError ERROR</MODULE>
<MESSAGE>Core.Error.UnknownTableError:DMS/SQLHandler2_MySQL:83 (1109) Unknown table 'AccGroupUser' in where clause SELECT</MESSAGE>
</ENTRY>
<ENTRY>
<DATE>1004098026.459890 </DATE>
<CLIENT>10.48.30.160</CLIENT>
<PROXY>10.48.35.1</PROXY>
<TEMPLATE>None</TEMPLATE>
<MODULE>SAM.Handler DEBUG</MODULE>
<MESSAGE>TYPE OF UXSI=type 'string'</MESSAGE>
</ENTRY>
</LOG>
"""

class my(DMS.NwOp.SelectSocketServer):

    def __init__(self, host, port, backlog = 5):
        DMS.NwOp.SelectSocketServer.__init__(self, host, port, backlog)
        self.__sentBytes = 0

    def ReadClient(self, sock, data):
        pass #self.resp = str(long(data) * 2)
    
    def SendClient(self, sock):
        try:
            t1 = time.time()
            s = xmlMsg + '\n'
            sl = len(s)
            self.__sentBytes = self.__sentBytes + sl
            sock.send(s)
            time.sleep(2)
            t2 = time.time()
            d = t2 - t1
            print '%s: %s bytes sent. query took %.6f secs' % (time.ctime(time.time()), sl, d)
        except:
            raise ImportError

    def run(self):
        self.socket.bind(self.host, self.port)
        self.socket.listen(self.backlog)
        print '%s: successfully bound socket' % time.ctime(time.time())
        self.mainsocks.append(self.socket)
        self.readsocks.append(self.socket)
        while 1:
            try:
                readables, writeables, exc = select.select(self.readsocks, self.writesocks, [])
                for sockobj in readables:
                    if sockobj in self.mainsocks:
                        newsock, address = sockobj.accept()
                        print '%s: accepted: %s - %s' % (time.ctime(time.time()), str(newsock), address)
                        print '%s: sending data' % time.ctime(time.time())
                        self.readsocks.append(newsock)
                    else:
                        self.SendClient(sockobj)
            except:
                print '%s: closing connection. sent %s bytes' % (time.ctime(time.time()), self.__sentBytes)
                sockobj.close()
                self.readsocks.remove(sockobj)

m = my(host = '10.48.35.4', port = 3091)
o = DMS.NwOp.Operator()
o.NewSocketServer(m)
o.NewSocket(blocking = 1)
o.AssignSocketToServer(0, 0)
o.StartServer(0)
