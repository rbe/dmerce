#!/usr/bin/env python
import DMS.SQL
import DMS.SysTbl
import Guardian.Config
import sys
import os
import os.path
import time
import string
import getopt
import DNSArpa
import DNSNS
from DNSZone import ZoneData
import DMS.InitDmerce
import vars

class Serial:
    def EraseSubSerial(self, sub):
        sub = int(sub)
        sub = sub + 1
        if sub <= 9:
            return "0" + str(sub)
        else:
            return str(sub)

    def ToValidString(self, value):
        if int(value) <= 9:
            return "0" + str(value)
        else:
            return str(value)

    def Check(self, serial, subSerial):
        now = time.localtime(time.time())
        todaySerial = str(now[0]) + self.ToValidString(now[1]) + self.ToValidString(now[2])
        if str(todaySerial) != str(serial):
            return todaySerial, "01"
        else:
            return todaySerial, self.EraseSubSerial(subSerial)

