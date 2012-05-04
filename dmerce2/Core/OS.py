#!/usr/bin/env python
##
#
# $Author: rb $
revision = "$Revision: 2.4 $"
#
##

import os

class Base:

    """ basic miscellanous functions """

    def Env(self, key):
        """ return operating system enviroment variables """
        try:
            return os.environ[key]
        except KeyError:
            return ''

    def EnvAll(self):
        """
        return all environment variables
        as a list with containing tuples (key, value)
        """
        env = []
        for ek in os.environ.keys():
            env.append((ek, os.environ[ek]))
        return env
