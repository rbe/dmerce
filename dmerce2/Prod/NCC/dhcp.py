#!/usr/bin/env python
import vars
import os
from DMS import Dhcp
import sys
import string
import DMS.Lock
import DMS.ConfPath
import time

def main(servername, a):
     dmerceHome = os.environ['DMERCE_HOME']
     dhcpConfPath = dmerceHome + '/' + 'NCC' + '/' + servername + '/' + 'dhcp'
     conf = DMS.ConfPath.Path(dhcpConfPath)
     conf.Create()
     glsettings = d.GetGlobalSettings(a[0])
     if glsettings == []:
          print 'There are no Global settings in your Database!'
          print 'Please add them first!'
          sys.exit()
          
     """ Get PATH where dhcpd.conf will be written to: dmerce.cfg->[ncc]->dhcp_out"""
     dmercecfg = DMS.InitDmerce.NCC()
     dmercecfg.InitNCC()

     """ Open File for writing dhcpd.conf"""
     file = open(dhcpConfPath + '/' + 'dhcpd.conf' , 'w')
     #Description
     text = "#\n#Global Settings\n#\n\n"
     # Global Settings
     if glsettings[0]['DefaultLeaseTime']:
          text = text + "default-lease-time " + str(glsettings[0]['DefaultLeaseTime']) + ";\n"
     if glsettings[0]['MaxLeaseTime']:
          text = text + "max-lease-time " + str(glsettings[0]['MaxLeaseTime']) + ";\n"
     text = text + "\n"
     text = text + "#\n#Subnet Settings\n#\n\n"
     for i in range(len(a)):
          print a[i]
          subsettings = d.GetSubnetSettings(a[i])
          # Add Subnet Settings
          if subsettings:
               shared = 0
               for i in range(len(subsettings)):
                    if subsettings[i]['SubnetIP'] and subsettings[i]['Netmask'] and shared == 0:
                         text = text + "shared-network " + subsettings[0]['IfName'] + " {\n"
                         shared = 1
                    if subsettings[i]['SubnetIP'] and subsettings[i]['Netmask']:
                         text = text + "\tsubnet " + subsettings[i]['SubnetIP'] + " netmask " + subsettings[i]['Netmask'] + " {\n"
                         text = text + "\t\toption subnet-mask " + subsettings[i]['Netmask'] + ";\n"
                         if  subsettings[i]['Broadcast']:
                              text = text + "\t\toption broadcast-address " + subsettings[i]['Broadcast'] + ";\n"
                         if  subsettings[i]['Router']:
                              text = text + "\t\toption routers " + subsettings[i]['Router'] + ";\n"
                         if  subsettings[i]['Nameserver']:
                              text = text + "\t\toption domain-name-servers " + subsettings[i]['Nameserver'] + ";\n"
                         if subsettings[i]['NetbiosNs']:
                              text = text + "\t\toption netbios-name-servers " + subsettings[i]['NetbiosNs'] + ";\n"
                              text = text + "\t\toption netbios-dd-server " + subsettings[i]['NetbiosNs'] + ";\n"
                              text = text + "\t\toption netbios-node-type 8;\n"
                         if subsettings[i]['Domain']:
                              text = text + '\t\toption domain-name "' + subsettings[i]['Domain'] + '";\n'
                         if subsettings[i]['RangelowIP'] and subsettings[i]['RangehighIP']:
                              text = text + "\t\trange " + subsettings[i]['RangelowIP'] + " " + subsettings[i]['RangehighIP'] + ";\n"
                              text = text + "\t\tallow unknown-clients;\n"
                         else:
                              text = text + "\t\tdeny unknown-clients;\n"  
                    text = text + "\t}\n"
               if i == len(subsettings)-1 and shared == 1:
                    text = text + "}\n\n"
     text = text + "\n"
     text = text + "#\n#Fixed Hosts\n#\n\n"

     for i in range(len(a)):      
          # Add Fixed Hosts
          hostsettings = d.GetHostSettings(a[i])
          if hostsettings:
               for i in range(len(hostsettings)):
                    if  hostsettings[i]['Hostname']:
                         text = text + "host " + string.replace(hostsettings[i]['Hostname'], '..', '.') + " {\n"
                    if hostsettings[i]['MacAddress']:
                         text = text + "\thardware ethernet " + hostsettings[i]['MacAddress'] + ";\n"
                    if  hostsettings[i]['ClientIP']:
                         text = text + "\tfixed-address " + hostsettings[i]['ClientIP'] + ";\n"
                         text = text + "}\n\n"
     text = text + "#\n# --- End of file: dhcpd.conf\n"
     file.write(text)
     file.close()

if __name__ == '__main__':
     sys.path.append('/export/home/k/ka/dmerce2/DMS')
     vars.vars()
     lock = DMS.Lock.Lock('dmerce.dhcp.lock')
     """ Create time string for log entry """
     logTime = time.localtime(time.time())
     print '[%s-%s-%s  %s:%s:%s] NCC - dhcp skript\n' % (logTime[0], logTime[1], logTime[2],
                                                         logTime[3], logTime[4], logTime[5])
     """ now we have to check, if this script is still running """
     if not lock.CheckLock():
          print 'dns script such running'
          sys.exit()
     """ lock does not exists, create one """
     lock.AquireLock()
     """ Get Global Settings identified by Mac Address """
     d = DMS.Dhcp.Dhcp()
     serverList = {}
     macs = d.GetDhcpServers()
     for i in macs:
          if serverList.has_key(i['Name']):
               pass
          else:
               serverList[i['Name']] = []
     for i in macs:
          serverList[i['Name']].append(i['mac'])
     servers = serverList.keys()
     for i in servers:
          main(i, serverList[i])
     """ end of script, remove lock file """
     lock.ReleaseLock()
