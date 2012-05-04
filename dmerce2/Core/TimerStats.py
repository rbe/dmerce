#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.2 $'
#
##

import time
import Core.Timer

class Statistics:

    """
    collect timing statistics - a stop watch
    you can start and stop this timer serveral times
    deltas can be retrieved about several periods
    a sum can be returned
    """

    def __init__(self):
        self.__timer = []

    def ActTimer(self):
        return len(self.__timer) - 1

    def AddTimer(self):
        self.__timer.append(Core.Timer.Timer())

    def GetTimer(self):
        return self.__timer

    def Start(self, n = None):
        """ set start time of actual timer """
        if n is None:
            n = self.ActTimer()
        self.__timer[n].Start()

    def GetStart(self, n = None):
        """ get start time of actual timer """
        if n is None:
            n = self.ActTimer()
        return self.__timer[n].GetStart()

    def Stop(self, n = None):
        """ set stop time of actual timer """
        if n is None:
            n = self.ActTimer()
        self.__timer[n].Stop()

    def GetStop(self, n = None):
        """ get stop time of actual timer """
        if n is None:
            n = self.ActTimer()
        return self.__timer[n].GetStop()

    def Delta(self, n = None):
        """ return delta between stop and start of a timer """
        if n is None:
            n = self.ActTimer()
        return self.__timer[n].Delta()

    def AllDelta(self):
        sum = 0
        for t in range(len(self.__timer)):
            sum = sum + self.Delta(t)
        return sum

#
# Test
#
#s = Statistics()
#
#s.AddTimer()
#s.AddTimer()
#
#s.Start(0)
#time.sleep(1)
#s.Stop(0)
#
#s.Start(1)
#time.sleep(2)
#s.Stop(1)
#
#print s.GetTimer()
#print s.GetStart(0), s.GetStop(0), s.Delta(0)
#print s.GetStart(1), s.GetStop(1), s.Delta(1)
