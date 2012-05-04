import string, copy, re, time

monthshort2num = {
    "Jan" : 1, "Feb" : 2,
    "Mar" : 3, "Apr" : 4,
    "May" : 5, "Jun" : 6,
    "Jul" : 7, "Aug" : 8,
    "Sep" : 9, "Oct" : 10,
    "Nov" : 11, "Dec" : 12
    }

monthnum2gername = {
    1 : "Januar", 2 : "Februar",
    3 : "Maerz", 4 : "April",
    5 : "Mai", 6 : "Juni",
    7 : "Juli", 8: "August",
    9 : "September", 10 : "Oktober",
    11 : "November", 12 : "Dezember"
    }

class ApacheLog:

    def __init__(self):
        self.__all = []

    def SetRaw(self, raw):
        self.__raw = raw
        
    def Digest(self):
        tmp = string.split(self.__raw, ' ')
        self.__client = tmp[0]
        self.__all.append(tmp[0])
        d = tmp[3] + ' ' + tmp[4]
        self.__gdate = d[1:len(d)-1]
        self.__all.append(d[1:len(d)-1])
        self.__request = tmp[5] + ' ' + tmp[6] + ' ' + tmp[7]
        self.__all.append(tmp[5] + ' ' + tmp[6] + ' ' + tmp[7])
        self.__respcode = int(tmp[8])
        self.__all.append(int(tmp[8]))
        if tmp[9] == '-':
            self.__respsize = 0
        else:
            self.__respsize = int(tmp[9])
        self.__all.append(self.__respsize)
        if tmp[10] == '"-"':
            self.__url = ''
        else:
            self.__url = tmp[10]
        self.__all.append(tmp[10])
        temp = tmp[11]
        for i in range(12, len(tmp)):
            temp = temp + ' ' + tmp[i]
        self.__useragent = temp
        self.__all.append(temp)
        # digest date
        tmp = string.split(self.__gdate, ' ')
        self.__timezone = tmp[1]
        self.__all.append(tmp[1])
        tmp = string.split(tmp[0], ':')
        date = tmp[0]
        self.__all.append(tmp[0])
        self.__hour = int(tmp[1])
        self.__all.append(int(tmp[1]))
        self.__minute = int(tmp[2])
        self.__all.append(int(tmp[2]))
        self.__second = int(tmp[3])
        self.__all.append(int(tmp[3]))
        date = string.split(date, '/')
        self.__day = int(date[0])
        self.__all.append(int(date[0]))
        self.__month = monthshort2num[date[1]]
        self.__all.append(monthshort2num[date[1]])
        self.__year = int(date[2])
        self.__all.append(int(date[2]))
        self.__timestamp = time.mktime(self.__year, self.__month,
                                       self.__day, self.__hour,
                                       self.__minute, self.__second, 0, 0,
                                       time.localtime(time.time())[8])

    def GetRaw(self):
        return self.__raw

    def GetClient(self):
        return self.__client

    def GetGdate(self):
        return self.__gdate

    def GetRequest(self):
        return self.__request

    def GetRespcode(self):
        return self.__respcode

    def GetRespsize(self):
        return self.__respsize

    def GetURL(self):
        return self.__url

    def GetUseragent(self):
        return self.__useragent
    
    def GetTimezone(self):
        return self.__timezone

    def GetHour(self):
        return self.__hour

    def GetMinute(self):
        return self.__minute

    def GetSecond(self):
        return self.__second

    def GetDay(self):
        return self.__day

    def GetMonth(self):
        return self.__month

    def GetYear(self):
        return self.__year
    
    def GetTimestamp(self):
        return self.__timestamp

    def GetAll(self):
        return self.__all

    def WriteSQL(self):
        s = "INSERT INTO SrvApacheLog VALUES ( '%s', '%s', %d, %d, '%s', '%s', '%s', %d )" %(self.__client, self.__request, self.__respcode, self.__respsize, self.__url, self.__useragent, self.__timezone, self.__timestamp)
        return s

class SendmailLog:

    def __init__(self):
        self.__all = []
        
    def SetRaw(self, raw):
        self.__raw = raw
            
    def Digest(self):
        # header
        tmp = string.split(self.__raw, ': ')
        header = string.split(tmp[0])
        self.__month = monthshort2num[header[0]]
        self.__all.append(monthshort2num[header[0]])
        self.__day = int(header[1])
        self.__all.append(int(header[1]))
        tim = string.split(header[2],':')
        self.__hour = int(tim[0])
        self.__all.append(int(tim[0]))
        self.__minute = int(tim[1])
        self.__all.append(int(tim[1]))
        self.__second = int(tim[2])
        self.__timestamp = time.mktime(time.localtime(time.time())[0],
                                       self.__month, self.__day, self.__hour,
                                       self.__minute, self.__second, 0, 0,
                                       time.localtime(time.time())[8])

        self.__all.append(int(tim[2]))
        self.__timezone = time.timezone/36
        self.__machine = header[3]
        self.__all.append(header[3])
        process = string.split(header[4], '[')
        self.__process = process[0]
        self.__all.append(process[0])
        self.__pid = int(process[1][0:len(process[1])-1])
        self.__all.append(int(process[1][0:len(process[1])-1]))
        # name=value pairs (if any)
        temp = string.split(tmp[1], ' ')
        if len(temp) > 1 and temp[1] == 'failed':
            self.__failed = 1
            self.__all.append(1)
            self.__action = temp[0]
            self.__all.append(temp[0])
            self.__shmsgid = ''
            self.__all.append('')
            self.__from = ''
            self.__all.append('')
            self.__size = 0
            self.__all.append(0)
            self.__class = 0
            self.__all.append(0)
            self.__pri = 0
            self.__all.append(0)
            self.__nrcpts = 0
            self.__all.append(0)
            self.__msgid = ''
            self.__all.append('')
            self.__relay = ''
            self.__all.append('')
            self.__daemon = ''
            self.__all.append('')
            self.__direction = ''
            self.__all.append('')
            self.__to = ''
            self.__all.append('')
            self.__ctladdr = ''
            self.__all.append('')
            self.__delay = 0
            self.__all.append(0)
            self.__xdelay = 0
            self.__all.append(0)
            self.__mailer = ''
            self.__all.append('')
            self.__proto = ''
            self.__all.append('')
            self.__dsn = ''
            self.__all.append('')
            self.__stat = ''
            self.__all.append('')
        else:
            self.__action = ''
            self.__all.append('')
            self.__failed = 0
            self.__all.append(0)
            self.__shmsgid = tmp[1]
            self.__all.append(tmp[1])
            vals = string.split(tmp[2], ', ')
            self.__from = ''
            self.__size = 0
            self.__class = 0
            self.__pri = 0
            self.__nrcpts = 0
            self.__msgid = ''
            self.__proto = ''
            self.__daemon = ''
            self.__relay = ''
            self.__to = ''
            self.__ctladdr = ''
            self.__delay = 0
            self.__xdelay = 0
            self.__mailer = ''
            self.__stat = ''
            self.__dsn = ''


            for i in range(len(vals)):
                if string.split(vals[i], '=')[0] == "from":
                    self.__from = string.split(vals[i], '=')[1]
                    self.__all.append(string.split(vals[i], '=')[1])
                    self.__direction = 'from'
                    self.__all.append('from')
                if string.split(vals[i], '=')[0] == "to":
                    self.__to = string.split(vals[i], '=')[1]
                    self.__all.append(string.split(vals[i], '=')[1])
                    self.__direction = 'to'
                    self.__all.append('to')
                if (string.split(vals[i], '=')[0] == "from"
                    or string.split(vals[i], '=')[0] == "to"):
                    ta = string.split(vals[i], '=')[1]
                    ta = string.split(ta, ',')
                    t = []
                    for j in ta:
                        t.append(string.strip(j))
                    self.__addresses = []
                    na = re.compile('[\w-,]*("[\w- ]+")[\w-,]*')
                    ad = re.compile('[.]*([\w-\.]+@[\w-\.]+)[.]*')
                    ad1 = re.compile('^([\w-]+)$')
                    ad2 = re.compile('^([\w-]+),')
                    ad3 = re.compile('([\w-]+)$')
                    ad4 = re.compile(',([\w-]+),')
                    for j in t:
                        if na.findall(j):
                            u = string.strip(na.findall(j)[0]) + ' '
                        else:
                            u = ''
                        aa = ad.findall(j) + ad1.findall(j) + ad2.findall(j) + ad3.findall(j) + ad4.findall(j)
                        self.__addresses.append(u + '<' + aa[0] + '>')
                    self.__all.append(self.__addresses)
                if string.split(vals[i], '=')[0] == "size":
                    self.__size = int(string.split(vals[i], '=')[1])
                    self.__all.append(int(string.split(vals[i], '=')[1]))
                if string.split(vals[i], '=')[0] == "class":
                    self.__class = int(string.split(vals[i], '=')[1])
                    self.__all.append(int(string.split(vals[i], '=')[1]))
                if string.split(vals[i], '=')[0] == "pri":
                    self.__pri = int(string.split(vals[i], '=')[1])
                    self.__all.append(int(string.split(vals[i], '=')[1]))
                if string.split(vals[i], '=')[0] == "nrcpts":
                    self.__nrcpts = int(string.split(vals[i], '=')[1])
                    self.__all.append(int(string.split(vals[i], '=')[1]))
                if string.split(vals[i], '=')[0] == "msgid":
                    self.__msgid = string.split(vals[i], '=')[1]
                    self.__all.append(string.split(vals[i], '=')[1])
                if string.split(vals[i], '=')[0] == "proto":
                    self.__proto = string.split(vals[i], '=')[1]
                    self.__all.append(string.split(vals[i], '=')[1])
                if string.split(vals[i], '=')[0] == "relay":
                    self.__relay = string.split(vals[i], '=')[1]
                    self.__all.append(string.split(vals[i], '=')[1])
                
                if string.split(vals[i], '=')[0] == "daemon":
                    self.__daemon = string.split(vals[i], '=')[1]
                    self.__all.append(string.split(vals[i], '=')[1])
                if string.split(vals[i], '=')[0] == "ctladdr":
                    self.__ctladdr = string.split(vals[i], '=')[1]
                    self.__all.append(string.split(vals[i], '=')[1])
                if string.split(vals[i], '=')[0] == "delay":
                    delay = string.split(string.split(vals[i], '=')[1], ':')
                    self.__delay = 3600 * int(delay[0]) + 60 * int(delay[1]) + int(delay[2])
                    self.__all.append(3600 * int(delay[0]) + 60 * int(delay[1]) + int(delay[2]))
                if string.split(vals[i], '=')[0] == "xdelay":
                    xdelay = string.split(string.split(vals[i], '=')[1], ':')
                    self.__xdelay = 3600 * int(xdelay[0]) + 60 * int(xdelay[1]) + int(xdelay[2])
                    self.__all.append(3600 * int(xdelay[0]) + 60 * int(xdelay[1]) + int(xdelay[2]))
                if string.split(vals[i], '=')[0] == "mailer":
                    self.__mailer = string.split(vals[i], '=')[1]
                    self.__all.append(string.split(vals[i], '=')[1])
                if string.split(vals[i], '=')[0] == "stat":
                    self.__stat = string.join(string.split(vals[i], '=')[1:], '=')
                    self.__all.append(string.join(string.split(vals[i], '=')[1:], '='))
                if string.split(vals[i], '=')[0] == "dsn":
                    self.__dsn = string.split(vals[i], '=')[1]
                    self.__all.append(string.split(vals[i], '=')[1])
                

    def GetRaw(self):
        return self.__raw

    def GetMonth(self):
        return self.__month

    def GetDay(self):
        return self.__day

    def GetHour(self):
        return self.__hour

    def GetMinute(self):
        return self.__minute

    def GetSecond(self):
        return self.__second

    def GetTimestamp(self):
        return self.__timestamp

    def GetMachine(self):
        return self.__machine

    def GetProcess(self):
        return self.__process

    def GetPid(self):
        return self.__pid

    def GetAction(self):
        return self.__action

    def GetFailed(self):
        return self.__failed

    def GetShmsgid(self):
        return self.__shmsgid

    def GetFrom(self):
        return self.__from

    def GetAddresses(self):
        return self.__addresses

    def GetSize(self):
        return self.__size

    def GetClass(self):
        return self.__class

    def GetPri(self):
        return self.__pri

    def GetNrcpts(self):
        return self.__nrcpts

    def GetMsgid(self):
        return self.__msgid

    def GetRelay(self):
        return self.__relay

    def GetDirection(self):
        return self.__direction

    def GetDaemon(self):
        return self.__daemon

    def GetTo(self):
        return self.__to

    def GetCtladdr(self):
        return self.__ctladdr

    def GetDelay(self):
        return self.__delay

    def GetXdelay(self):
        return self.__xdelay

    def GetMailer(self):
        return self.__mailer

    def GetProto(self):
        return self.__proto

    def GetDsn(self):
        return self.__dsn

    def GetStat(self):
        return self.__stat

    def GetAll(self):
        return self.__all

    def WriteSQL(self):
        s = "INSERT INTO SrvSendmailLog VALUES ( '%s', %d, '%s', %d, '%s', '%s', '%s', '%s', %d, %d, %d, %d, '%s', '%s', '%s', '%s', %d, %d, '%s', '%s', '%s', '%s', %d, %f )" %(self.__process, self.__pid, self.__action, self.__failed, self.__shmsgid, self.__from, self.__to, self.__direction, self.__size, self.__class, self.__pri, self.__nrcpts, self.__msgid, self.__relay, self.__daemon, self.__ctladdr, self.__delay, self.__xdelay, self.__mailer, self.__dsn, self.__proto, self.__stat, self.__timezone, self.__timestamp)
        return s

class LogSet:

    def __init__(self):
        self.__list = []
        
    def SetList(self, list):
        self.__list = list

    def ApacheSignin(self):
        if not self.__type or self.__type != 'apache':
            pass
        else:
            client = open("client", "a+")
            request = open("request", "a+")
            url = open("url", "a+")
            useragent = open("useragent", "a+")

            client.close()
            request.close()
            url.close()
            useragent.close()
            
            for i in self.__list:

                client = open("client", "r")
                m = string.split(client.read(), '\n')
                client.close()
                client = open("client", "a")
                if i.GetClient() and not string.strip(i.GetClient()) in m:
                    client.write(string.strip(i.GetClient()) + '\n')
                client.close()

                request = open("request", "r")
                m = string.split(request.read(), '\n')
                request.close()
                request = open("request", "a")
                if i.GetRequest() and not string.strip(i.GetRequest()) in m:
                    request.write(string.strip(i.GetRequest()) + '\n')
                request.close()

                url = open("url", "r")
                m = string.split(url.read(), '\n')
                url.close()
                url = open("url", "a")
                if i.GetURL() and not string.strip(i.GetURL()) in m and not string.strip(i.GetURL()) == '"-"':
                    url.write(string.strip(i.GetURL()) + '\n')
                url.close()

                useragent = open("useragent", "r")
                m = string.split(useragent.read(), '\n')
                useragent.close()
                useragent = open("useragent", "a")
                if i.GetUseragent() and not string.strip(i.GetUseragent()) in m:
                    useragent.write(string.strip(i.GetUseragent()) + '\n')
                useragent.close()
            

    def SendmailSignin(self):
        if not self.__type or self.__type != 'sendmail':
            pass
        else:
            machine = open("machine", "a+")
            f = open("from", "a+")
            daemon = open("daemon", "a+")
            relay = open("relay", "a+")
            t = open("to", "a+")
            ctladdr = open("ctladdr", "a+")
            mailer = open("mailer", "a+")
            dsn = open("dsn", "a+")
            stat = open("stat", "a+")
            
            machine.close()
            f.close()
            daemon.close()
            relay.close()
            t.close()
            ctladdr.close()
            mailer.close()
            dsn.close()
            stat.close()
            
            for i in self.__list:
                
                machine = open("machine", "r")
                m = string.split(machine.read(), '\n')
                machine.close()
                machine = open("machine", "a")
                if i.GetMachine() and not string.strip(i.GetMachine()) in m:
                    machine.write(string.strip(i.GetMachine()) + '\n')
                machine.close()

                f = open("from", "r")
                ff = string.split(f.read(), '\n')
                f.close()
                f = open("from", "a")
                if i.GetFrom() and not string.strip(i.GetFrom()) in ff:
                    f.write(string.strip(i.GetFrom()) + '\n')
                f.close()

                daemon = open("daemon", "r")
                d = string.split(daemon.read(), '\n')
                daemon.close()
                daemon = open("daemon", "a")
                if i.GetDaemon() and not string.strip(i.GetDaemon()) in d:
                    daemon.write(string.strip(i.GetDaemon()) + '\n')
                daemon.close()

                relay = open("relay", "r")
                r = string.split(relay.read(), '\n')
                relay.close()
                relay = open("relay", "a")
                if i.GetRelay() and not string.strip(i.GetRelay()) in r:
                    relay.write(string.strip(i.GetRelay()) + '\n')
                relay.close()

                ctladdr = open("ctladdr", "r")
                c = string.split(ctladdr.read(), '\n')
                ctladdr.close()
                ctladdr = open("ctladdr", "a")
                if i.GetCtladdr() and not string.strip(i.GetCtladdr()) in c:
                    ctladdr.write(string.strip(i.GetCtladdr()) + '\n')
                ctladdr.close()

                t = open("to", "r")
                tt = string.split(t.read(), '\n')
                t.close()
                t = open("to", "a")
                if i.GetTo() and not string.strip(i.GetTo()) in tt:
                    t.write(string.strip(i.GetTo()) + '\n')
                t.close()

                mailer = open("mailer", "r")
                m = string.split(mailer.read(), '\n')
                mailer.close()
                mailer = open("mailer", "a")
                if i.GetMailer() and not string.strip(i.GetMailer()) in m:
                    mailer.write(string.strip(i.GetMailer()) + '\n')
                mailer.close()

                dsn = open("dsn", "r")
                d = string.split(dsn.read(), '\n')
                dsn.close()
                dsn = open("dsn", "a")
                if i.GetDsn() and not string.strip(i.GetDsn()) in d:
                    dsn.write(string.strip(i.GetDsn()) + '\n')
                dsn.close()

                stat = open("stat", "r")
                s = string.split(stat.read(), '\n')
                stat.close()
                stat = open("stat", "a")
                if i.GetStat() and not string.strip(i.GetStat()) in s:
                    stat.write(string.strip(i.GetStat()) + '\n')
                stat.close()
            
            
    def ApacheRead(self, file):
        r = []
        f = open(file)
        l = f.readline()
        while l:
            o = ApacheLog()
            o.SetRaw(l)
            o.Digest()
            r.append(o)
            l = f.readline()
        f.close
        self.__type = 'apache'
        self.__list = r

    def SendmailRead(self, file):
        r = []
        f = open(file)
        l = f.readline()
        while l:
            o = SendmailLog()
            o.SetRaw(l)
            o.Digest()
            r.append(o)
            l = f.readline()
        f.close
        self.__type = 'sendmail'
        self.__list = r

    def ApacheSQLSignIn(self):
        l = []
        for i in self.__list:
            l.append(i.WriteSQL())
        return l

    def SendmailSQLSignIn(self):
        l = []
        for i in self.__list:
            l.append(i.WriteSQL())
        return l


    def GetByRaw(raw):
        list = []
        for i in self.__list:
            if i.GetRaw() == raw:
                list.append(i)
        self.__list = list
        
    def GetByClient(self, client):
        list = []
        if self.__type == 'sendmail':
            pass
        else:
            for i in self.__list:
                if i.GetClient() == client:
                    list.append(i)
        self.__list = list
        
    def GetByGdate(self, gdate):
        list = []
        if self.__type == 'sendmail':
            pass
        else:
            for i in self.__list:
                if i.GetGdate() == gdate:
                    list.append(i)
        self.__list = list
        
    def GetByRequest(self, request):
        list = []
        if self.__type == 'sendmail':
            pass
        else:
            for i in self.__list:
                if i.GetRequest() == request:
                    list.append(i)
        self.__list = list
        
    def GetByResponse(self, response):
        list = []
        if self.__type == 'sendmail':
            pass
        else:
            for i in self.__list:
                if i.GetResponse() == response:
                    list.append(i)
        self.__list = list
        
    def GetByRespcode(self, respcode):
        list = []
        if self.__type == 'sendmail':
            pass
        else:
            for i in self.__list:
                if i.GetRespcode() == respcode:
                    list.append(i)
        self.__list = list
        
    def GetByRespsize(self, respsize):
        list = []
        if self.__type == 'sendmail':
            pass
        else:
            for i in self.__list:
                if i.GetRespsize() == respsize:
                    list.append(i)
        self.__list = list
        
    def GetByURL(self, url):
        list = []
        if self.__type == 'sendmail':
            pass
        else:
            for i in self.__list:
                if i.GetURL() == url:
                    list.append(i)
        self.__list = list
    
    def GetByUseragent(self, useragent):
        list = []
        if self.__type == 'sendmail':
            pass
        else:
            for i in self.__list:
                if i.GetUseragent() == useragent:
                    list.append(i)
        self.__list = list
        
    def GetByTimezone(self, timezone):
        list = []
        if self.__type == 'sendmail':
            pass
        else:
            for i in self.__list:
                if i.GetTimezone() == timezone:
                    list.append(i)
        self.__list = list
        
    def GetByHour(self, hour):
        list = []
        for i in self.__list:
            if i.GetHour() == hour:
                list.append(i)
        self.__list = list
        
    def GetByMinute(self, minute):
        list = []
        for i in self.__list:
            if i.GetMinute() == minute:
                list.append(i)
        self.__list = list
        
    def GetBySecond(self, second):
        list = []
        for i in self.__list:
            if i.GetSecond() == second:
                list.append(i)
        self.__list = list
        
    def GetByDay(self, day):
        list = []
        for i in self.__list:
            if i.GetDay() == day:
                list.append(i)
        self.__list = list
        
    def GetByMonth(self, month):
        list = []
        for i in self.__list:
            if i.GetMonth() == month:
                list.append(i)
        self.__list = list
        
    def GetByYear(self, year):
        list = []
        if self.__type == 'sendmail':
            pass
        else:
            for i in self.__list:
                if i.GetYear() == year:
                    list.append(i)
        self.__list = list

    def GetByTimestamp(self, timestamp):
        list = []
        for i in self.__list:
            if i.GetTimestamp() == timestamp:
                list.append(i)
        self.__list = list
        
    def GetByMachine(self, machine):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetMachine() == machine:
                    list.append(i)
        self.__list = list
        
    def GetByProcess(self, process):
        list = []        
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetProcess() == process:
                    list.append(i)
        self.__list = list
        
    def GetByPid(self, pid):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetPid() == pid:
                    list.append(i)
        self.__list = list
        
    def GetByFailed(self, failed):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetFailed() == failed:
                    list.append(i)
        self.__list = list
        
    def GetByShmsgid(self, shmsgid):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetShmsgid() == shmsgid:
                    list.append(i)
        self.__list = list
        
    def GetByFrom(self, f):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetFrom() == f:
                    list.append(i)
        self.__list = list
        
    def GetByAdresses(self, addr):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetAdresses() == addr:
                    list.append(i)
        self.__list = list

    def GetBySize(self, size):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetSize() == size:
                    list.append(i)
        self.__list = list
        
    def GetByClass(self, c):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetClass() == c:
                    list.append(i)
        self.__list = list
        
    def GetByPri(self, pri):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetPri() == pri:
                    list.append(i)
        self.__list = list
        
    def GetByNrcpts(self, nrcpts):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetNrcpts() == nrcpts:
                    list.append(i)
        self.__list = list
        
    def GetByMsgid(self, msgid):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetMsgid() == msgid:
                    list.append(i)
        self.__list = list
        
    def GetByRelay(self, relay):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetRelay() == relay:
                    list.append(i)
        self.__list = list
        
    def GetByDirection(self, direction):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetDirection() == direction:
                    list.append(i)
        self.__list = list
        
    def GetByTo(self, to):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetTo() == to:
                    list.append(i)
        self.__list = list
        
    def GetByCtladdr(self, ctladdr):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetCtladdr() == ctladdr:
                    list.append(i)
        self.__list = list
        
    def GetByDelay(self, delay):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetDelay() == delay:
                    list.append(i)
        self.__list = list
        
    def GetByXdelay(self, xdelay):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetXdelay() == xdelay:
                    list.append(i)
        self.__list = list
        
    def GetByMailer(self, mailer):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetMailer() == mailer:
                    list.append(i)
        self.__list = list
        
    def GetByStat(self, stat):
        list = []
        if self.__type == 'apache':
            pass
        else:
            for i in self.__list:
                if i.GetStat() == stat:
                    list.append(i)
        self.__list = list
        
    def GetList(self):
        return self.__list

    def GetType(self):
        return self.__type

    def Intersection(self, x, y):
        list = []
        for i in x.GetList():
            for j in y.GetList():
                if i.GetAll() == j.GetAll():
                    list.append(i)
        self.__list = list

    def Union(self, x, y):
        self.__list = x.GetList()[:] + y.GetList()[:]
        for i in self.__list:
            for j in self.__list:
                if i.GetAll() == j.GetAll() and i != j:
                    self.__list.remove(j)
    
    def Subtraction(self, x, y):
        self.__list = x.GetList()[:]
        for i in y.GetList():
            for j in self.__list:
                if i.GetAll() == j.GetAll():
                    self.__list.remove(j)
    
    def Xor(self, x, y):
        self.__list = x.GetList()[:] + y.GetList()[:]
        for i in self.__list:
            for j in self.__list:
                if i.GetAll() == j.GetAll() and i != j:
                    self.__list.remove(j)
        list = []
        for i in x.GetList():
            for j in y.GetList():
                if i.GetAll() == j.GetAll():
                    list.append(i)
        for j in list:
            for i in self.__list:
                if i.GetAll() == j.GetAll():
                    self.__list.remove(j)

    def Card(self):
        return len(self.__list)

class Data:

    def SetSet(self, set):
        self.__set = set
        
    def GetX(self):
        return self.__x

    def GetY(self):
        return self.__y
        
    def SortBySecond(self):
        self.__x = range(60)
        self.__y = []
        l = []
        for i in range(60):
            k = copy.copy(self.__set)
            l.append(k)
        for i in range(60):
            l[i].GetBySecond(i)
            self.__y.append(l[i].Card())

    def SortByMinute(self):
        self.__x = range(60)
        self.__y = []
        l = []
        for i in range(60):
            k = copy.copy(self.__set)
            l.append(k)
        for i in range(60):
            l[i].GetByMinute(i)
            self.__y.append(l[i].Card())

    def SortByHour(self):
        self.__x = range(24)
        self.__y = []
        l = []
        for i in range(24):
            k = copy.copy(self.__set)
            l.append(k)
        for i in range(24):
            l[i].GetByHour(i)
            self.__y.append(l[i].Card())

    def SortByDay(self):
        self.__x = range(1, 32)
        self.__y = []
        l = []
        for i in range(1, 32):
            k = copy.copy(self.__set)
            l.append(k)
        for i in range(1, 32):
            l[i-1].GetByDay(i)
            self.__y.append(l[i-1].Card())
            
    def SortByMonth(self):
        self.__x = [
            "Januar", "Februar", "Maerz", "April", "Mai", "Juni",
            "Juli", "August", "September", "Oktober", "November",
            "Dezember"
            ]
        self.__y = []
        l = []
        for i in range(12):
            k = copy.copy(self.__set)
            l.append(k)
        for i in range(1, 13):
            l[i-1].GetByMonth(i)
            self.__y.append(l[i-1].Card())

    def SortByYear(self, first, last):
        self.__x = range(first, last + 1)
        self.__y = []
        l = []
        for i in range(first, last + 1):
            k = copy.copy(self.__set)
            l.append(k)
        for i in range(first, last + 1):
            l[i-1].GetByYear(i)
            self.__y.append(l[i-1].Card())
    
    def SortByMachine(self):
        machine = open("machine", "r")
        self.__x = string.split(machine.read(), '\n')
        machine.close()
        self.__y = []
        l = []
        for i in self.__x:
            k = copy.copy(self.__set)
            l.append(k)
        for i in range(len(self.__x)):
            l[i].GetByMachine(self.__x[i])
            self.__y.append(l[i].Card())

    def SortByFailed(self):
        self.__x = [0, 1]
        self.__y = []
        l = []
        for i in range(2):
            k = copy.copy(self.__set)
            l.append(k)
        for i in range(2):
            l[i].GetByFailed(self.__x[i])
            self.__y.append(l[i].Card())


