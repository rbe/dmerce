#!/usr/bin/env python

import DMS.SnmpMac

if __name__ == '__main__':
    
    r=DMS.SnmpMac.NCCSnmpMac()
    a = r.GetMacAddresses()     # Get all macaddresses from our server
    b = r.GetIfNameOfMac()      # Get the Names of our MACs
    knownid = None
    new = []                 #initialize a list with new 

    for i in range(len(a)):
        server = r.GetServer(a[i]) #Is there a MAC in SrvServerInterfaces with a ServerID?
        if server['ServerID']: 
            if knownid is None:        #If there is no knownid
                knownid = server['ServerID']
            else:
                if knownid != server['ServerID']:   #If the knownid differs from ServerID
                    print 'There are 2 ServerIDs for one Physical Server!'
        else:              #Is there an entry in SrvServer, but no known MAC in Interfaces
            server = r.GetSrvServer()
            if server['ServerID']:
                if knownid is None:        #If there is no knownid
                    knownid = server['ServerID']
                else:
                    if knownid != server['ServerID']:   #If the knownid differs from ServerID
                       print 'There are 2 ServerIDs for one Physical Server!' 
            new.append((a[i],b[i]))    #Append a new MAC, because there is no entry in Interfaces

    if knownid is None:
        knownid = r.AddNewServer()

    for i in new:
        r.AddMacAddress(knownid,i)


