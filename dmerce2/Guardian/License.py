#!/usr/bin/env python
##
#
# $Author: rb $
revision = "$Revision: 2.5 $"
#
##

import sys
import os
import Core.Log
import Core.Error
import Guardian.Config

class Key:

    """
    represents a license key:
    - hostname
    - check sum
    - licensed modules: basis, sam, merchant, auction
    """

    def __init__(self, key = None):
        self.__key = key
        self.__hostname = ''

    def SetHostname(self, hostname):
        self.__hostname = hostname

    def GetHostname(self):
        return self.__hostname

    def SetChecksum(self, checksum):
        self.__checksum = checksum

    def GetChecksum(self):
        return self.__checksum

    def Encode(self):
        pass

    def Decode(self):
        pass

class HostSerial:

    """ serial number of host """

    def __init__(self, prjcfg):
        self.__log = Core.Log.File(module = '1[Guardian].HostSerial')
        self.__prjcfg = prjcfg

    def Check(self):
        """ check for valid license """
        m = 'LICENSE NOT VALID: YOU ARE NOT AUTHORIZED TO USE DMERCE ON THIS SERVER'
        hs = 0
        hsmb = int(os.stat('/etc/hostname')[6] * 2009)
        if self.__prjcfg.has_key('HOSTSERIAL'):
            hs = int(self.__prjcfg['HOSTSERIAL'])
        else:
            hs = int(self.__prjcfg['HostSerial'])
        #self.__log.Write(msgType = 'INFO', msg = 'CHECKING HOST SERIAL %s != %s?' % (hsmb, hs))
        if hsmb != hs:
            raise Core.Error.HostSerialNotValidError(1, m)
