#!/usr/bin/env python
#
import string
from DNSSerial import Serial

class ReverseMap:
    """ Class to build reverse map file (in-addr.arpa) """
    def __init__(self, reverseList = []):
        """ Constructor """
        self.__reverseList = reverseList
        self.__ptr = ''

    def InvertIP(self, ip):
        """ Check for class c net """
        self.__ip = ip
        convert = ''
        if ip[-1] != '.':
            self.__ip = self.__ip + '.'
        each = string.split(self.__ip, '.')
        for i in range(len(each)-1, -1, -1):
            convert = convert + each[i]
            if i != len(each)-1:
                convert = convert + '.'
        return convert

    def LastIP(self, ip):
        """ Get last IP """
        self.__ip = ip
        if self.__ip[-1] == '.':
            self.__ip = self.__ip[:-1]
        lastIP = string.split(self.__ip, '.')
        return lastIP[len(lastIP)-1]

    def FirstIP(self, ip):
        """ Get last IP """
        tmp = string.split(ip, '.')
        if ip[0] != '.':
            return tmp[0]
        else:
            return tmp[1]

    def CreateRecord(self, value1, value2):
        """ Crate PTR record """
        self.__value1 = value1
        self.__value2 = value2
        if self.__value1[-1] != '.':
            self.__value1 = self.__value1 + '.'
        if self.__value2[-1] != '.':
            self.__value2 = self.__value2 + '.'
        return self.__value1 + self.__value2

    def FilterIP(self, arpa):
        ip = ''
        elements = string.split(arpa, '.')
        for i in range(len(elements) - 1, -1, -1):
            try:
                ip = ip + str(int(elements[i])) + '.'
            except:
                pass
        return ip[:-1]
        

    def SetHeader(self, arpa, priNS, id, zoneC, nsList, serial, subserial, db, query):
        ser = Serial()
        if zoneC[-1] != '.':
            zoneC = zoneC + '.'
        if priNS[-1] != '.':
            priNS = priNS + '.'
        if arpa[-1] != '.':
            arpa = arpa + '.'
        newSerial, newSubserial = ser.Check(serial, subserial) 
            
        """ Return SOA record in a formatted form """
        if len(arpa) > 0 and len(priNS) > 0 and len(zoneC) > 0:
            self.__arpa = '$TTL 10800\n%s\tIN\tSOA\t%s\t%s (\n\t\t%s\t; serial ' \
                          '\n\t\t604800\t\t; refresh '\
                          '\n\t\t86400\t\t; retry '\
                          '\n\t\t2419200\t; expire '\
                          '\n\t\t604800 )\t\t; Negative caching TTL\n\n' \
                          %(arpa , priNS, zoneC, newSerial + newSubserial)
            for i in nsList:
                if i['NS_Name'][-1] != '.':
                    self.__arpa = self.__arpa + '\tIN\tNS\t%s.\n' % i['NS_Name']
                else:
                    self.__arpa = self.__arpa + '\tIN\tNS\t%s\n' % i['NS_Name']
            db.UpdateReverse(query, newSerial, newSubserial, id)
        else:
            self.__arpa = None

    def AddPTR(self, arpa, zonePart1, zonePart2):
        if zonePart1[-1] != '.':
            zonePart1 = zonePart1 + '.'
        if zonePart2[-1] != '.':
            zonePart2 = zonePart2 + '.'
        zone = zonePart1 + zonePart2

        self.__ptr = self.__ptr + self.FirstIP(arpa) + '\t10800\tIN\tPTR\t' + zone + '\n' 

    def __str__(self):
        return self.__arpa + self.__ptr

