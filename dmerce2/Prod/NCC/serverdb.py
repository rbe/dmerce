#!/usr/bin/env python

import DMS.ServerDb

if __name__ == '__main__':
    
    """ Getting Disks Data """
    n = DMS.ServerDb.NCCServerDsk()
    servercount, servers = n.GetServer()          #Get Servers from SQL Database    
    for i in range(servercount):
        n.SnmpConnect(servers[i]['IPv4'],'public')
        n.SnmpGetDataIndex('.1.3.6.1.4.1.2021.9.1.1')
        n.SnmpGetData('.1.3.6.1.4.1.2021.9.1')
        n.MakeEntry(servers[i]['Name'],servers[i]['ID'])
    n.AddEntries()

    """ Getting Process Data """
    o = DMS.ServerDb.NCCServerProc()
    servercount, servers = o.GetServer()
    for i in range(servercount):
        o.SnmpConnect(servers[i]['IPv4'],'public')
        o.SnmpGetDataIndex('.1.3.6.1.4.1.2021.2.1.1')
        o.SnmpGetData('.1.3.6.1.4.1.2021.2.1')
        o.MakeEntry(servers[i]['ID'])
    o.AddEntries()

    """ Getting Interfaces Data """
    p = DMS.ServerDb.NCCServerInterfaces()
    servercount, servers = p.GetServer()
    for i in range(servercount):
        p.SnmpConnect(servers[i]['IPv4'],'public')
        p.SnmpGetDataIndex('.1.3.6.1.2.1.2.2.1.1')
        p.SnmpGetData('.1.3.6.1.2.1.2.2.1')
        p.MakeEntry(servers[i]['Name'],servers[i]['ID'])
    p.AddEntries()
