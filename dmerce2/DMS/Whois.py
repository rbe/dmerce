import socket
import string
import sys

class Whois:      

    def __init__(self):

        self.__default = 'whois.internic.com'
        self.__whoismap={ 'com' : 'whois.internic.net' , \
                          'org' : 'whois.internic.net' , \
                          'net' : 'whois.internic.net' , \
                          'edu' : 'whois.networksolutions.com' , \
                          'de'  : 'whois.denic.de' , \
                          'gov' : 'whois.nic.gov' , \
                          # See http://www.nic.gov/cgi-bin/whois 
                          'mil' : 'whois.nic.mil' , \
                          # See http://www.nic.mil/cgi-bin/whois
                          'ca'  : 'whois.cdnnet.ca' , \
                          'uk'  : 'whois.nic.uk' , \
                          'au'  : 'whois.aunic.net' , \
                          'hu'  : 'whois.nic.hu' , \
                          
                          # All the following are unverified/checked. 
                          'be'  : 'whois.ripe.net',
                          'it'  : 'whois.ripe.net' , \
                          # also whois.nic.it
                          'at'  : 'whois.ripe.net' , \
                          # also www.nic.at, whois.aco.net
                          'dk'  : 'whois.ripe.net' , \
                          'fo'  : 'whois.ripe.net' , \
                          'lt'  : 'whois.ripe.net' , \
                          'no'  : 'whois.ripe.net' , \
                          'sj'  : 'whois.ripe.net' , \
                          'sk'  : 'whois.ripe.net' , \
                          'tr'  : 'whois.ripe.net' , \
                          # also whois.metu.edu.tr
                          'il'  : 'whois.ripe.net' , \
                          'bv'  : 'whois.ripe.net' , \
                          'se'  : 'whois.nic-se.se' , \
                          'br'  : 'whois.nic.br' , \
                          # a.k.a. whois.fapesp.br?
                          'fr'  : 'whois.nic.fr' , \
                          'sg'  : 'whois.nic.net.sg' , \
                          'hm'  : 'whois.registry.hm' , \
                          # see also whois.nic.hm
                          'nz'  : 'domainz.waikato.ac.nz' , \
                          'nl'  : 'whois.domain-registry.nl' , \
                          # RIPE also handles other countries
                          # See  http://www.ripe.net/info/ncc/rir-areas.html
                          'ru'  : 'whois.ripn.net' , \
                          'ch'  : 'whois.nic.ch' , \
                          # see http://www.nic.ch/whois_readme.html
                          'jp'  : 'whois.nic.ad.jp' , \
                          # (use DOM foo.jp/e for english; need to lookup !handles separately)
                          'to'  : 'whois.tonic.to' , \
                          'nu'  : 'whois.nic.nu' , \
                          'fm'  : 'www.dot.fm' , \
                          # http request http://www.dot.fm/search.html
                          'am'  : 'whois.nic.am' , \
                          'nu'  : 'www.nunames.nu' , \
                          # http request
                          # e.g. http://www.nunames.nu/cgi-bin/drill.cfm?domainname=nunames.nu
                          #'cx'  : 'whois.nic.cx' , \	# no response from this server
                          'af'  : 'whois.nic.af' , \
                          'as'  : 'whois.nic.as' , \
                          'li'  : 'whois.nic.li' , \
                          'lk'  : 'whois.nic.lk' , \
                          'mx'  : 'whois.nic.mx' , \
                          'pw'  : 'whois.nic.pw' , \
                          'sh'  : 'whois.nic.sh' , \
                          # consistently resets connection
                          'tj'  : 'whois.nic.tj' , \
                          'tm'  : 'whois.nic.tm' , \
                          'pt'  : 'whois.dns.pt' , \
                          'kr'  : 'whois.nic.or.kr' , \
                          # see also whois.krnic.net
                          'kz'  : 'whois.nic.or.kr' , \
                          # see also whois.krnic.net
                          'al'  : 'whois.ripe.net' , \
                          'az'  : 'whois.ripe.net' , \
                          'ba'  : 'whois.ripe.net' , \
                          'bg'  : 'whois.ripe.net' , \
                          'by'  : 'whois.ripe.net' , \
                          'cy'  : 'whois.ripe.net' , \
                          'cz'  : 'whois.ripe.net' , \
                          'dz'  : 'whois.ripe.net' , \
                          'ee'  : 'whois.ripe.net' , \
                          'eg'  : 'whois.ripe.net' , \
                          'es'  : 'whois.ripe.net' , \
                          'fi'  : 'whois.ripe.net' , \
                          'gr'  : 'whois.ripe.net' , \
                          'hr'  : 'whois.ripe.net' , \
                          'lu'  : 'whois.ripe.net' , \
                          'lv'  : 'whois.ripe.net' , \
                          'ma'  : 'whois.ripe.net' , \
                          'md'  : 'whois.ripe.net' , \
                          'mk'  : 'whois.ripe.net' , \
                          'mt'  : 'whois.ripe.net' , \
                          'pl'  : 'whois.ripe.net' , \
                          'ro'  : 'whois.ripe.net' , \
                          'si'  : 'whois.ripe.net' , \
                          'sm'  : 'whois.ripe.net' , \
                          'su'  : 'whois.ripe.net' , \
                          'tn'  : 'whois.ripe.net' , \
                          'ua'  : 'whois.ripe.net' , \
                          'va'  : 'whois.ripe.net' , \
                          'yu'  : 'whois.ripe.net' , \
                          # unchecked
                          'ac'  : 'whois.nic.ac' , \
                          'cc'  : 'whois.nic.cc' , \
                          #'cn'  : 'whois.cnnic.cn' , \	# connection refused
                          'gs'  : 'whois.adamsnames.tc' , \
                          'hk'  : 'whois.apnic.net' , \
                          #'ie'  : 'whois.ucd.ie' , \	# connection refused
                          #'is'  : 'whois.isnet.is' , \# connection refused
                          #'mm'  : 'whois.nic.mm' , \	# connection refused
                          'ms'  : 'whois.adamsnames.tc' , \
                          'my'  : 'whois.mynic.net' , \
                          #'pe'  : 'whois.rcp.net.pe' , \	# connection refused
                          'st'  : 'whois.nic.st' , \
                          'tc'  : 'whois.adamsnames.tc' , \
                          'tf'  : 'whois.adamsnames.tc' , \
                          'th'  : 'whois.thnic.net' , \
                          'tw'  : 'whois.twnic.net' , \
                          'us'  : 'whois.isi.edu' , \
                          'vg'  : 'whois.adamsnames.tc' , \
                          #'za'  : 'whois.co.za'	# connection refused
                          }

    def SetData(self, data):
        self.__line = self.__line + data + '\n'

    def GetWhoisServer(self, domain):
        dom = string.split(domain, '.')[1]
        return self.__whoismap[dom]
        
    def Get(self, host, query):
        new = 0
        linelist = []
        if not host:
            host = self.GetWhoisServer(query)
        elif host == '':
            host = self.GetWhoisServer(query)
        WHOIS_PORT = 43
        CRLF = "\015\012"
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect(host, WHOIS_PORT)
        f = s.makefile('rb')
        s.send(query + CRLF)
        rv = []
        while 1:
            line = f.readline()
            if not line:
                break
            if line[-2:] == CRLF:
                line = line[:-2]
            elif line[-1:] in CRLF:
                line = line[:-1]
                if string.count(line, 'Whois Server:'):
                    new = 1
                    newhost = string.split(string.replace(line, ' ' , ''), ':')[1] 
                rv.append(line)
        f.close()
        s.close()
        if new == 0:
            return string.join(rv, "\n") + "\n"
        else:
            return self.Get(newhost, query)

#if __name__=='__main__':
#    wh = Whois()
#    if len(sys.argv) == 2:
#        print wh.Get(None, sys.argv[1])
#    if len(sys.argv) == 3:
#        print wh.Get(sys.argv[1], sys.argv[2])
