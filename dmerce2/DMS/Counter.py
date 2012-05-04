#!/usr/bin/env python
##
#
# $Author: rb $
revision =  '$Revision: 2.2 $'
#
##

class Counter:

    """
    class that counts different things
    """

    def __init__(self):
        self.__counter = {}

    def Count(self, thing, n = 1):
        if self.__counter.has_key(thing):
            self.__counter[thing] = self.__counter[thing] + n
        else:
            self.__counter[thing] = n

    def __getitem__(self, thing):
        if self.__counter.has_key(thing):
            return self.__counter[thing]
        else:
            return -1

    def keys(self):
        return self.__counter.keys()
                
