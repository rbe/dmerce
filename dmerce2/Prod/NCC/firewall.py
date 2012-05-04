#!/usr/bin/env python

""" imports from python """
import sys
import os
import time
import string
import commands

""" imports from NCC """
import vars
vars.vars()
import DMS.SysTbl
import Guardian.Config
import DMS.SQL
import DMS.Firewall
import DMS.IP


class NCCHellfire:

    """ 1Ci - NCC - Hellfire """

    def __init__(self):
        self.__time = time.time()
        self.__fw = DMS.Firewall.Firewall()
        self.__netMask = DMS.IP.Ip4Mask()
        self.__data = []
        sysTbl = DMS.SysTbl.Retrieve()
        sysTbl.SetConfig (Guardian.Config.RFC822('/usr/local/1Ci/dmerce/conf'))
        sysTbl.SetFQHN('www.ncc.1ci.de')
        sysTbl.Init()
        self.__sqlSys = sysTbl.GetSQLSys()
        self.__sqlData = sysTbl.GetSQLData()
        self.__dboid = DMS.SQL.DBOID(self.__sqlSys,self.__sqlData)
        
    def GetRules(self):
        """ fetches rules todo from database """
        rulescount, rules = self.__sqlData["SELECT r.ID,t.Name AS FwType,t.ProgramCall,r.active,"+
                                           " r.RuleNo,a.Name AS ActName,r.ActionOpt," +
                                           " r.Probability,p.Name AS ProName,r.InterfaceIn," +
                                           " r.InterfaceOut,d.Name AS DirName,r.SrcNot," +
                                           " r.SrcIp,r.SrcCidr,r.SrcMask,r.SrcPorts,r.DestNot," +
                                           " r.DestIp,r.DestCidr,r.DestMask,r.DestPorts," +
                                           " r.OptKeepstate,r.OptBridged,r.OptFragment," +
                                           " r.OptEstablished,r.OptSetup,r.Log,r.LogAmount" +
                                           " FROM SrvFirewall AS s,SrvFirewallRules AS r," +
                                           " SrvFirewallTy AS t, SrvFirewallAct AS a," +
                                           " SrvFirewallProto AS p,SrvFirewallDirection AS d" +
                                           " WHERE r.active = 1 AND r.ToDo = 1" +
                                           " AND r.FwID = s.ID AND t.ID = s.TypeID " +
                                           " AND a.ID = r.Action AND p.ID = r.Protocol" +
                                           " AND d.ID = r.Direction"]
        return rulescount, rules
        
    def __CheckMask(self, rule, direction):
        """ check cidr/netmask, direction: 0=source, 1=destination """
        if direction==0:
            __cidr = rule['SrcCidr']
            __netmask = rule['SrcMask']
            __prefix = 'src_'
        elif direction==1:
            __cidr = rule['DestCidr']
            __netmask = rule['DestMask']
            __prefix = 'dest_'
        self.__netMask.SetNetmask(__netmask)
        __netmask2cidr = self.__netMask.GetCidr()
        if __netmask2cidr != __cidr:
            print 'Data ('+__prefix+'cidr,',__prefix+'mask) from SrvFirewallRules ID =\''+\
                  str(rule['ID'])+'\'are different!'
            print __prefix+'cidr',__cidr,'is not',__prefix+'mask',__netmask,' --->',\
                  __netmask2cidr
            print __prefix+'cidr',__cidr,' will be used!'
        """If cidr differs from netmask, cidr will be returned!"""
        return __cidr 
        
    def __SetSrc(self, rule, l):
        """ Set Source Definitions """
        if rule['SrcNot'] == 1:
            l.SetSrcNot()
        l.SetSrcIp(rule['SrcIp'])
        """ First look if cidr and netmask are set,
            after that if cidr is set and at last if a netmask ist set
        """
        if rule['SrcCidr'] and rule['SrcMask']:
            l.SetSrcMask(self.__CheckMask(rule,0))
        elif rule['SrcCidr']:
            l.SetSrcMask(rule['SrcCidr'])
        elif rule['SrcMask']:
            l.SetSrcMask(rule['SrcMask'])
        return l

    def __SetDest(self, rule, l):
        """ Set Destination Definitions """
        if rule['DestNot'] == 1:
            l.SetDestNot()
        l.SetDestIp(rule['DestNot'])
        """ First look if cidr and netmask are set,
            after that if cidr is set and at last if a netmask ist set
        """
        if rule['DestCidr'] and rule['DestMask']:
            l.SetDestMask(self.__CheckMask(rule,1))
        elif rule['DestCidr']:
            l.SetDestMask(rule['DestCidr'])
        elif rule['DestMask']:
            l.SetDestMask(rule['DestMask'])
        return l

    def AnalysePorts(self,rule):
        """ Analyse Port-strings """
        srclist = []
        destlist = []
        srcstring=rule['SrcPorts']
        deststring=rule['DestPorts']
        
        """ Lege eine sourceliste an, die n*m Komponenten hat """
        """ n = Anzahl der Src Eintraege, m = Anzahl der Dest Eintraege """
        for i in range(len(string.split(srcstring,','))):
            t = string.split(srcstring,',')[i]
            for k in range(len(string.split(deststring,','))):
		srclist.append(string.split(t,'-'))

        """ Lege eine destinationliste an, die m Eintraege hat """
        for i in range(len(string.split(deststring,','))):
            t = string.split(deststring,',')[i]
            destlist.append(string.split(t,'-'))
                               
        count = 0
        """ Zaehle ueber n und gebe somit jeder Src Komponente alle Dest Komponenten """
        for i in range(len(string.split(srcstring,','))):
            for j in range(len(string.split(deststring,','))):
                srclist[count] = srclist[count], destlist[j]
		count = count + 1
        return srclist

    def MakeRules(self,rulescount,rules):
	print "RC:", rulescount
        for i in range(rulescount):
            rule = rules[i]    
            ports = self.AnalysePorts(rule)
            for j in range(len(ports)):    
                r = Firewall.FirewallRule()
                r.SetRuleType(rule['FwType'])
                r.SetProgrammCall(rule['ProgramCall'])
                if rule['active'] == 1:
                    r.SetProgramAction('add')
                r.SetRuleNo((rule['RuleNo']+j))
                r.SetRuleAction(rule['ActName'])
                r.SetProbability(rule['Probability'])
                r.SetProtocol(rule['ProName'])
                r.SetInterfaceIn(rule['InterfaceIn'])
                r.SetInterfaceOut(rule['InterfaceOut'])
                r.SetDirection(rule['DirName'])
                r = self.__SetSrc(rule, r)
                r = self.__SetDest(rule, r)
                r.SetOptKeepState(rule['OptKeepstate'])
                r.SetOptBridged(rule['OptBridged'])
                r.SetOptFragment(rule['OptFragment'])
                r.SetOptEstablished(rule['OptEstablished'])
                r.SetOptSetup(rule['OptSetup'])
                r.SetLog(rule['Log'])
                r.SetLogAmount(rule['LogAmount'])
                r.SetSrcPorts([ports[j][0]])
                r.SetDestPorts([ports[j][1]])
                self.__fw.AddRule(r)
        self.__data = self.__fw.Get()
        
    def SetTodo(self,rulescount,rules):
        """ For adding Rules not again, set ToDo to zero """
        for i in range(rulescount):
            rule = rules[i] 
            self.__sqlData["UPDATE SrvFirewallRules SET ToDo = 0 WHERE ID = '%s'" \
                                  % str(rule['ID'])]
    
    def Execute(self):
        data = self.__data
        for i in string.split(data,'\n'):
            #commands.getstatusoutput(str(i)) #Raus, weil noch kein FreeBSD Rechner da zum testen 
            print i

    def WriteLog(self):
        data = string.replace(self.__data,'/sbin/ipfw ','')
        file = open('/home/sb/ipfw.log','a')
        file.write(data)
        file.close()

if __name__ == '__main__':
    n = NCCHellfire()
    rc,r = n.GetRules()
    n.MakeRules(rc,r)
    #n.SetTodo()
    n.Execute()
    n.WriteLog()




