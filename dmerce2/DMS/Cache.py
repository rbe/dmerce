#!/usr/bin/env python
##
#
# $Author: rb $
revision =  '$Revision: 2.4 $'
#
##

import string

class Cache:

    """
    a cache
    - keys can be case insensitive (create instance as
      follows: c = Cache(ci = 1))
    """

    def __init__(self, ci = 0):
        self.__cache = {}
        self.__ci = ci

    def __setitem__(self, item, value):
        """ set item to value """
        if self.__ci:
            self.__cache[string.upper(item)] = value
        else:
            self.__cache[item] = value

    def __getitem__(self, item):
        """
        retrieve item, return None when it is
        no existent in cache
        """
        r = None
        if self.__cache.has_key(item):
            if self.__ci:
                r = self.__cache[string.upper(item)]
            else:
                r = self.__cache[item]
        return r

    def __str__(self):
        """ show me """
        return str(self.__cache)

    def __delitem__(self, item):
        if self.__ci:
            del self.__cache[string.upper(item)]
        else:
            del self.__cache[item]

    def Get(self):
        """ return cache dictionary """
        return self.__cache

    def Set(self, cache):
        """ set cache """
        self.__cache = cache

    def has_key(self, item):
        """ did we cache item? """
        return self.__cache.has_key(item)

    def keys(self):
        """ return all keys """
        return self.__cache.keys()

class Module(Cache):

    """ cache modules """

    def __init__(self):
        Cache.__init__(self)

    def Add(self, fqfc, instance):
        Cache.__setitem__(self, fqfc, instance)

    def Get(self, fqfc):
        return Cache.__getitem__(self, fqfc)

    def GetCache(self):
        return Cache.Get(self)

#t = test()
#t.Add('test',1)
#print t.Get('test')
#print t.GetCache()
#del t['test']
#print t.GetCache()
