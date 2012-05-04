#!/usr/local/env python
#

""" Import recent Modules """
import sys
import DMS.SysTbl
import Guardian.Config
import DMS.SQL
import os
import string
import vars

class Webalizer:
    def __init__(self, fqhn, dir, logdir):
        self.__fqhn = fqhn
        #self.__dir = self.CheckDir(dir)
        self.__dir = dir
        self.__logdir = self.CheckLogDir(logdir)
        self.__dmercehome = os.environ['DMERCE_HOME']

    def CheckLogDir(self, dir):
        if dir[-1:] == '/':
            dir = dir[:-1]
            return dir
        else:
            return dir

    def CheckDir(self, dir):
        if dir[-1:] == '/':
            dir = dir[:-1]
        if os.path.isdir(dir + '/webalizer'):
            return dir
        else:
            os.makedirs(dir + '/webalizer', 0770)
            return dir
    
    def CreateConf(self):
        self.__conf = "LogFile  %s/%s-access_log\n" %(self.__logdir, self.__fqhn) + \
                      "OutputDir      %s/webalizer\n" % self.__dir + \
                      "HistoryName    webalizer.hist\n" \
                      "IncrementalName webalizer.current\n" \
                      "ReportTitle    Usage Statistics for\n"\
                      "HostName       %s\n" % self.__fqhn + \
                      "DNSChildren     5\n" \
                      "DNSCache        dns_cache.db\n" \
                      "PageType         htm*\n" \
                      "PageType        cgi\n" \
                      "HideURL         *.gif\n" \
                      "HideURL         *.GIF\n" \
                      "HideURL         *.jpg\n" \
                      "HideURL         *.JPG\n" \
                      "HideURL         *.png\n" \
                      "HideURL         *.PNG\n" \
                      "HideURL         *.ra\n" \
                      "SearchEngine    yahoo.com       p=\n" \
                      "SearchEngine    altavista.com   q=\n" \
                      "SearchEngine    google.com      q=\n" \
                      "SearchEngine    eureka.com      q=\n" \
                      "SearchEngine    lycos.com       query=\n" \
                      "SearchEngine    hotbot.com      MT=\n" \
                      "SearchEngine    msn.com         MT=\n" \
                      "SearchEngine    infoseek.com    qt=\n" \
                      "SearchEngine    webcrawler      searchText=\n" \
                      "SearchEngine    excite          search=\n" \
                      "SearchEngine    netscape.com    search=\n" \
                      "SearchEngine    mamma.com       query=\n" \
                      "SearchEngine    alltheweb.com   query=\n" \
                      "SearchEngine    northernlight.com  qr=\n" \

    def WriteConf(self):
        if not os.path.isdir(self.__dmercehome + '/webalizer/'):
            os.makedirs(self.__dmercehome + '/webalizer/', 0770)
        f = open(self.__dmercehome + '/webalizer/webalizer.%s' % self.__fqhn, 'w')
        f.write(self.__conf)
        f.close()

    def GetConf(self):
        return self.__conf
