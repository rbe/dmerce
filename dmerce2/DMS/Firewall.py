import sys
#sys.path.append('/home/rb/dmerce2')
import types
import operator
import string
import DMS.IP

class FirewallRule:

    """ a single rule """
    
    def __init__(self):
        self.__rule = ''
        self.__programmCall = ''
        self.__ruleType = ''
        self.__availableRuleTypes = (
            'ipfw',
            'iptables',
            'ipchains'
            )
        self.__ruleNo = 0
        self.__probability = 0
        self.__log = 0
        self.__logAmount = 0
        self.__protocol = 'ip'
        self.__availableProtocols = (
            'ip', 'tcp', 'udp', 'icmp', 'ipv6', 'ipv6-route', 'ipv6-frag',
            'ipv6-icmp', 'ipv6-nonxt', 'ipv6-opts', 'ipip', 'ipx-in-ip',
            'igmp',
            'ggp',
            'ipencap',
            'st2',
            'cbt',
            'egp',
            'igp',
            'bbn-rcc',
            'nvp',
            'pup',
            'argus',
            'emcom',
            'xnet',
            'chaos',
            'mux',
            'dcn',
            'hmp',
            'prm',
            'xns-idp',
            'trunk-1',
            'trunk-2',
            'leaf-1',
            'leaf-2',
            'rdp',
            'irtp',
            'iso-tp4',
            'netblt',
            'mfe-nsp',
            'merit-isp',
            'sep',
            '3pc',
            'idpr',
            'xtp',
            'ddp',
            'idpr-cmtp',
            'tp++',
            'il',
            'sdrp',
            'rsvp',
            'gre',
            'mhrp',
            'bna',
            'esp',
            'ah',
            'i-nlsp',
            'swipe',
            'narp',
            'mobile',
            'tlsp',
            'skip',
            'cftp',
            'sat-expak',
            'kryptolan',
            'rvd',
            'ippc',
            'sat-mon',
            'visa',
            'ipcv',
            'cpmux',
            'xphb',
            'wsn',
            'pvp',
            'br-sat-mon',
            'sun-nd',
            'wb-mon',
            'wb-expak',
            'isp-ip',
            'vmtp',
            'secure-vmtp',
            'vines',
            'ttp',
            'nsfnet-igp',
            'dgp',
            'tcf',
            'eigrp',
            'ospf',
            'sprite-rpc',
            'larp',
            'mtp',
            'ax.25',
            'micp',
            'scc-sp',
            'etherip',
            'encap',
            'gmtp',
            'ifmp',
            'pnni',
            'pim',
            'aris',
            'scps',
            'qnx',
            'a/n',
            'ipcomp',
            'snp',
            'compaq-peer',
            'vrrp',
            'pgm',
            'l2tp',
            'iatp',
            'divert'
            )
        self.__directionIn = 0
        self.__directionOut = 0
        self.__directionAny = 0
        self.__interfaceIn = ''
        self.__interfaceOut = ''
        self.__programaction = ''
        self.__availableProgramActions = (
            'add',
            'delete'
            )
        self.__ruleaction = 'allow'
        self.__availableRuleActions = (
            'allow',
            'deny',
            'reset',
            'count',
            'check-state',
            'divert',
            'tee',
            'fwd',
            'pipe',
            'queue',
            'skipto'
            )
        self.__srcNot = 0
        self.__srcIp = DMS.IP.Ip4()
        self.__srcMask = DMS.IP.Ip4Mask()
        self.__srcPorts = ''
        self.__destNot = 0
        self.__destIp = DMS.IP.Ip4()
        self.__destMask = DMS.IP.Ip4Mask()
        self.__destPorts = ''
        self.__optKeepState = 0
        self.__optBridged = 0
        self.__optFragment = 0
        self.__optEstablished = 0
        self.__optSetup = 0

    def SetProgrammCall(self, w):
        self.__programmCall = w

    def GetProgrammCall(self):
        return self.__programmCall
    
    def SetRuleType(self, t):
        if t in self.__availableRuleTypes:
            self.__ruleType = t
            return 1
        else:
            return 0

    def GetRuleType(self):
        return self.__ruleType

    def SetRuleNo(self, ruleNo):
        self.__ruleNo = ruleNo

    def GetRuleNo(self):
        return self.__ruleNo

    def SetProbability(self, p):
        self.__probability = p

    def GetProbability(self):
        return self.__probability

    def SetLog(self, l):
        if l:
            self.__log = 1
        else:
            self.__log = 0

    def SetLogAmount(self, la):
        self.__logAmount = la

    def GetLogAmount(self):
        return self.__logAmount

    def SetProtocol(self, p):
        if p in self.__availableProtocols:
            self.__protocol = p
            return 1
        else:
            return 0

    def GetProtocol(self):
        return self.__protocol

    def SetDirection(self, d):
        if d == 1:
            self.__directionIn = 1
        elif d == 2:
            self.__directionOut = 1
        elif d == 3:
            self.__directionAny = 1

    def GetDirection(self):
        if self.__directionIn:
            return 1
        elif self.__directionOut:
            return 2
        elif self.__directionAny:
            return 3

    def SetInterfaceIn(self, i):
        self.__interfaceIn = i

    def GetInterfaceIN(self):
        return self.__interfaceIn

    def SetInterfaceOut(self, i):
        self.__interfaceOut = i

    def GetInterfaceOut(self):
        return self.__interfaceOut

    def SetProgramAction(self, a):
        if a in self.__availableProgramActions:
            self.__programaction =a
            return 1
        else:
            return 0

    def GetProgramAction(self):
        return self.__programaction
    
    def SetRuleAction(self, a):
        if a in self.__availableRuleActions:
            self.__ruleaction = a
            return 1
        else:
            return 0

    def GetRuleAction(self):
        return self.__ruleaction

    def SetSrcNot(self):
        self.__srcNot = 1

    def UnsetSrcNot(self):
        self.__srcNot = 0

    def GetSrcNot(self):
        if self.__srcNot:
            return '!'
        else:
            return ''

    def SetSrcIp(self, ip):
        self.__srcIp = ip

    def GetSrcIp(self):
        return self.__srcIp

    def SetSrcMask(self, srcMask):
        self.__srcMask.Set(srcMask)

    def GetSrcMask(self, cidr = 0):
        if not cidr:
            return self.__srcMask.GetNetmask()
        else:
            return self.__srcMask.GetCidr()

    def SetSrcPorts(self, srcPorts):
        self.__srcPorts = DMS.IP.Ports(srcPorts)

    def GetSrcPorts(self):
        return self.__srcPorts

    def SetDestNot(self):
        self.__destNot = 1

    def UnsetDestNot(self):
        self.__destNot = 0

    def GetDestNot(self):
        if self.__destNot:
            return '!'
        else:
            return ''

    def SetDestIp(self, destIp):
        self.__destIp = destIp

    def GetDestIp(self):
        return self.__destIp

    def SetDestMask(self, destMask):
        self.__destMask.Set(destMask)

    def GetDestMask(self, cidr = 0):
        if not cidr:
            return self.__destMask.GetNetmask()
        else:
            return self.__destMask.GetCidr()

    def SetDestPorts(self, destPorts):
        self.__destPorts = DMS.IP.Ports(destPorts)

    def GetDestPorts(self):
        return self.__destPorts

    def SetOptKeepState(self, k):
        self.__optKeepState = k

    def GetOptKeepState(self):
        return self.__optKeepState

    def SetOptBridged(self, b):
        self.__optBridged = b

    def GetOptBriged(self):
        return self.__optBridged

    def SetOptFragment(self, f):
        self.__optFragment = f

    def GetOptFragment(self):
        return self.__optFragment

    def SetOptEstablished(self, e):
        self.__optEstablished = e

    def GetOptEstablished(self):
        return self.__optEstablished

    def SetOptSetup(self, s):
        self.__optSetup = s

    def GetOptSetup(self):
        return self.__optSetup

    def AppendRule(self, s, space = 1):
        if space:
            self.__rule = self.__rule + ' '
        self.__rule = self.__rule + str(s)

    def CreateIpchainsCall(self):
        return '/sbin/ipchains -A input -p tcp -s %s -d 0/0 80 -j DENY' % string.strip(line)

    def CreateIptablesCall(self):
        self.AppendRule(self.GetProgrammCall(), 0)
        self.AppendRule('-j ' + self.GetRuleAction())
        self.AppendRule('-p ' + self.GetProtocol())
        self.AppendRule('-s')
        self.AppendRule(self.GetSrcNot())
        self.AppendRule(self.GetSrcIp(), 0)
        self.AppendRule('/', 0)
        self.AppendRule(self.GetSrcMask(), 0)
        self.AppendRule('--sport %s' % self.GetSrcPorts())
        self.AppendRule('-d')
        self.AppendRule(self.GetDestNot())
        self.AppendRule(self.GetDestIp(), 0)
        self.AppendRule('/', 0)
        self.AppendRule(self.GetDestMask(), 0)
        self.AppendRule('--dport ' + self.GetDestPorts())
        #return '/sbin/iptables -t filter -A INPUT -p tcp -s %s -d 0/0 --dport 80 -j REJECT' % string.strip(line)

    def CreateIpfwCall(self):
        self.AppendRule(self.GetProgrammCall(), 0)
        self.AppendRule(self.GetProgramAction())
        self.AppendRule(self.GetRuleNo())
        self.AppendRule(self.GetRuleAction())
        self.AppendRule(self.GetProtocol())
        self.AppendRule('from')
        self.AppendRule(self.GetSrcNot())
        self.AppendRule(self.GetSrcIp(), 0)
        self.AppendRule('/', 0)
        self.AppendRule(self.GetSrcMask(cidr = 1), 0)
        self.AppendRule(self.GetSrcPorts())
        self.AppendRule('to')
        self.AppendRule(self.GetDestNot())
        self.AppendRule(self.GetDestIp(), 0)
        self.AppendRule('/', 0)
        self.AppendRule(self.GetDestMask(cidr = 1), 0)
        self.AppendRule(self.GetDestPorts())

    def Create(self):
        if self.__ruleType == 'ipfw':
            self.CreateIpfwCall()
        elif self.__ruleType == 'iptables':
            self.CreateIptablesCall()
        elif self.__ruleType == 'ipchains':
            self.CreateIpchainsCall()

    def __str__(self):
        return self.__rule

class Firewall:

    """ a set of rules """

    def __init__(self, rules = []):
        self.__rules = rules

    def AddRule(self, rule):
        self.__rules.append(rule)

    def DeleteRulePos(self, pos):
        try:
            del self.__rules[pos]
        except:
            pass

    def FindRuleNo(self, ruleNo):
        """
        find rule with no ruleNo in list
        and return list position
        """
        i = 0
        for r in range(len(self.__rules)):
            if self.__rules[r].GetRuleNo() == ruleNo:
                return r

    def SetRuleNo(self, ruleNo, rule):
        """ delete rule with no ruleNo """
        self.__rules[self.FindRuleNo(ruleNo)] = rule

    def DeleteRuleNo(self, ruleNo):
        """ set rule with no ruleNo """
        del self.__rules[self.FindRuleNo(ruleNo)]

    def Get(self):
        """ returns rules as string with newlines """
        s = ''
        for r in self.__rules:
            r.Create()
            s = s + str(r) + '\n'
        return s

    def __str__(self):
        return self.Get()

#r = FirewallRule()
#r.SetProgrammCall('/sbin/ipfw')
#r.SetRuleType('ipfw')
#r.SetAction('add')
#r.SetProtocol('tcp')
#r.SetSrcIp('217.29.11.2')
#r.SetSrcMask('255.255.255.0')
#r.SetSrcPorts([(20,21), (25,), (80,), (443,)])
#r.Create()
#print r
