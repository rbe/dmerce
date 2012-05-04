#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 1.10 $'
#
##

import sys
import types
import os
import Queue
import socket
import select
import threading
import time
import string

class Operator(threading.Thread):

    """ an operator """

    def __init__(self):
        # the protocol dictionary
        self.dict = {
            "SetProtocolQueue" : self.SetProtocolQueue,
            "LoadProtocol" : self.LoadProtocol,
            "WriteProtocol" : self.WriteProtocol,
            "ReadProtocol" : self.ReadProtocol,
            "NewOperator" : self.NewOperator,
            "run" : self.run,
            "NewQueue" : self.NewQueue,
            "ReadQueue" : self.ReadQueue,
            "WriteQueue" : self.WriteQueue,
            "NewFifo" : self.NewFifo,
            "SetFifoPath" : self.SetFifoPath,
            "ReadFifo" : self.ReadFifo,
            "WriteFifo" : self.WriteFifo,
            "NewSocket" : self.NewSocket,
            "NewSocketServer" : self.NewSocketServer,
            "NewSocketClient" : self.NewSocketClient,
            "AssignSocketToServer" : self.AssignSocketToServer,
            "AssignSocketToClient" : self.AssignSocketToClient,
            "AssignSubProtocol" : self.AssignSubProtocol,
            "AssignInQueueToServer" : self.AssignInQueueToServer,
            "AssignOutQueueToServer" : self.AssignOutQueueToServer,
            "AssignInQueueToClient" : self.AssignInQueueToClient,
            "AssignOutQueueToClient" : self.AssignOutQueueToClient,
            "StartServer" : self.StartServer,
            "StartClient" : self.StartClient
            }
        self.operators = []
        self.locks = []
        self.rlocks = []
        self.semaphores = []
        self.queues = []
        self.fifos = []
        self.sockets = []
        self.servers = []
        self.clients = []

    def BuildProtocol(self, list):
        """ Enqueue the protocol-strings """
        q = Protocol()
        q.SetProtocolList(list)
        q.Enqueue
        self.protocol = q

    def WriteProtocol(self, item, b = 1):
        """ Add an action to the protocol """
        return self.protocol.Write(item, b)

    def ReadProtocol(self, b = 1):
        """ Read from the protocol queue """
        return self.protocol.Read(b)
    
    def NewOperator(self):
        """ Build a new operator and store it """
        o = Operator()
        self.operators.append(o)

    def StartOperator(self, n):
        """ start a stored operator """
        self.operators[n].run()

    def Do(self, str):
        """ perform the command of a protocol string """
        x = string.split(str, ' ')
        z = string.strip(x[0])
        x = x[1:]
        # the diagonal case of the action Do
        if z == "Do":
            x = string.join(x)
            self.Do(x)
        else:
            for i in range(len(x)):
                x[i] = string.strip(x[i])
                try:
                    x[i] = int(x[i])
                except:
                    pass
            x = tuple(x)
            apply(self.dict[z], x)

    def run(self):
        """ run the operator by 'Do'ing all protocol commands """
        while 1:
            x = self.ReadProtocol()
            self.Do(x)
            
    def NewQueue(self, n = 0):
        """ Build a new queue and store it """
        q = Queue.Queue(n)
        self.queues.append(q)
        
    def ReadQueue(self, m, b):
        """ read from a stored queue """
        return self.queues[m].get(b)

    def WriteQueue(self, item, m, b):
        """ put an item into a stored queue """
        self.queues[m].put(item, b)
        
    def NewFifo(self):
        """ Build a new fifo and store it """
        f = os.mkfifo(self.fifopath + str(self.fifocounter))
        self.fifos.append(f)

    def SetFifoPath(self, path):
        """ Set the Path for all fifos """
        self.fifopath = path

    def ReadFifo(self, m):
        """ Read from a stored fifo """
        openend = os.open(self.fifos[m], os.O_RDONLY)
        os.read(openend)

    def WriteFifo(self, stuff, m):
        """ Write into a stored fifo """
        openend = os.open(self.fifos[m], os.O_WRONLY)
        os.write(openend, stuff)
        
    def NewSocket(self, family = socket.AF_INET, type = socket.SOCK_STREAM, proto = 0, blocking = 1):
        """ build a new socket and store it """
        s = Socket(family, type, proto)
        s.SetSocketOptions(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        s.SetBlocking(blocking)
        self.sockets.append(s)
        return s

    def NewSocketServer(self, host = '', port = 3091, backlog = 1):
        """ build a new socket server and store it """
        s = SelectSocketServer(host, port, backlog)
        self.servers.append(s)

    def AssignSubProtocol(self, kind, n, proto):
        """ assign some protocol-list to a server, client or operator """
        dic = {
            "server" : self.servers,
            "client" : self.clients,
            "operator" : self.operators
            }
        dic[kind][n].BuildProtocol(proto)

    def AssignSocketToServer(self, n, m):
        """ Assign a stored socket to a stored server """
        self.servers[n].SetSocket(self.sockets[m])

    def AssignInQueueToServer(self, n, m):
        """ Assign a stored queue to a stored server as in-queue """
        self.servers[n].SetInQueue(self.queues[m])

    def AssignOutQueueToServer(self, n, m):
        """ Assign a stored queue to a stored server as out-queue """
        self.servers[n].SetOutQueue(self.queues[m])

    def StartServer(self, n):
        """ run a stored server """
        self.servers[n].start()
        
    def NewSocketClient(self, host, port):
        """ Build a socket client and store it """
        sc = SocketClient(host, port)
        self.clients.append(sc)

    def AssignSocketToClient(self, n, m):
        """ Assign a stored socket to a stored client """
        self.clients[n].SetSocket(self.sockets[m])

    def AssignInQueueToClient(self, n, m):
        """ Assign a stored queue to a stored client as in-queue """
        self.clients[n].SetInQueue(self.queues[m])

    def AssignOutQueueToClient(self, n, m):
        """ Assign a stored queue to a stored client as out-queue """
        self.clients[n].SetOutQueue(self.queues[m])
    
    def StartClient(self, n):
        """ run a stored client """
        self.clients[n].start()
        
class SelectSocketServer(Operator):

    """ select-based multiplexing socket server """

    def __init__(self, host, port, backlog = 1):
        Operator.__init__(self)
        self.host = host
        self.port = port
        self.backlog = backlog
        self.mainsocks = []
        self.readsocks = []
        self.writesocks = []

    def SetSocket(self, socket):
        self.socket = socket
        
    def SetHost(self, host):
        self.host = host

    def SetPort(self, port):
        self.port = port

    def SetBacklog(self, backlog):
        self.backlog = backlog

    def SetInQueue(self, queue):
        self.inqueue = queue

    def SetOutQueue(self, queue):
        self.outqueue = queue

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
                        self.readsocks.append(newsock)
                    else:
                        data = sockobj.recv(1024)
                        if not data:
                            sockobj.close()
                            self.readsocks.remove(sockobj)
                        else:
                            self.ReadClient(sockobj, data)
                            self.SendClient(sockobj)
            except:
                print '%s: closing connection' % time.ctime(time.time())
                sockobj.close()
                self.readsocks.remove(sockobj)

class SocketClient(Operator):

    """ a socket client """
    
    def __init__(self, host, port):
        Operator.__init__(self)
        self.host = host
        self.port = port
        self.dict.update({
            "Connect" : Connect,
            "Send" : Send,
            "Recieve" : Recieve,
            "Close" : Close
            })

    def SetSocket(self, socket):
        self.socket = socket

    def SetHost(self, host):
        self.host = host

    def SetPort(self, port):
        self.port = port

    def SetInQueue(self, queue):
        self.inqueue = queue

    def SetOutQueue(self,queue):
        self.outqueue = queue

    def Fetch(self):
        self.outqueue.put(self.Recieve())

    def Request(self):
        self.Send(self.inqueue.get())

    def Connect(self):
        self.socket.connect(self.host, self.port)

    def Send(self, msg):
        self.socket.send(msg)

    def Recieve(self, bufsize = 1024):
        return self.socket.recv(bufsize)

    def Close(self):
        self.socket.close()
        
class Protocol:

    """ a protocol object """

    def SetProtocolStr(self, str):
        self.protocol = string.split(str, '\n')

    def SetProtocolList(self, list):
        self.protocol = list
    
    def Enqueue(self):
        self.queue = Queue.Queue(0)
        for i in self.protocol:
            self.queue.put(i)

    def Read(self, b = 1):
        return self.queue.get(b)

    def Write(self, item, b = 1):
        self.queue.put(item, b)
    

class Socket:

    def __init__(self, family = socket.AF_INET, type = socket.SOCK_STREAM, proto = 0, blocking = 1):
        self.socket = socket.socket(family, type, proto)
        self.family = family
        self.type = type
        self.proto = proto
        self.blocking = blocking
        self.server = -1
        self.client = -1
        self.free = 1
        
    def SetSocketOptions(self, level = socket.SOL_SOCKET, option = socket.SO_REUSEADDR, buflen = 1):
        self.level = level
        self.option = option
        self.buflen = buflen
        self.socket.setsockopt(level, option, buflen)

    def SetBlocking(self, blocking):
        self.socket.setblocking(blocking)

    def SetServer(self, no):
        self.server = n
        self.free = 0

    def SetClient(self, no):
        self.client = n
        self.free = 0

    def GetFamily(self):
        return self.family

    def GetType(self):
        return self.type

    def GetProto(self):
        return self.proto

    def GetBlocking(self):
        return self.blocking

    def GetServer(self):
        return self.server

    def GetClient(self):
        return self.client

    def IsFree(self):
        return self.free
    
    def GetLevel(self):
        return self.level

    def GetOption(self):
        return self.option

    def GetBuflen(self):
        return self.buflen
    
################################################################
### Beispielprotokolle befinden sich in dmerce2/DMS/test.py
################################################################



