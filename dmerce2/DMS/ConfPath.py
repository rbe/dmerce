#!/usr/bin/env python
import vars
import DMS.SnmpMac
from DMS import Dhcp
import sys
import string
import DMS.Lock
import os
import os.path

class Path:
    """ Constructor currently does nothing """
    def __init__(self, path):
        self.__path = self.CheckPath(path)

    def CheckPath(self, path):
        """ makedirs only work with an path without an / at the end """
        if path[-1] == '/':
            return path[:-1]
        else:
            return path

    def Exists(self):
        """ checks if path exists """
        if os.path.isdir(self.__path):
            return 1
        else:
            return None

    def Create(self):
        """ creates a directory """
        if not self.Exists():
            os.makedirs(self.__path)
