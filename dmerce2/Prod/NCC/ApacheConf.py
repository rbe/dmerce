#!/usr/local/env python
#

""" Import recent Modules """
import sys
import os
import string
import getopt
import time


class HttpdFile:
    def BaseConf(self, basedir):
        """ open httpd.conf file from recent apache installation """
        f = open(basedir + '/conf/httpd.conf', 'r')
        self.__httpd = f.readlines()
        f.close()

    def AddConf(self, value):
        """ add your own configuration """
        self.__httpd = self.__httpd + '\n' + value

    def Write(self, dir, filename, value):
        """ Write file new httpd.conf """
        f = open(dir + filename, 'w')
        f.write(value)
        f.close()

    def __str__(self):
        return '%s' % self.__httpd
