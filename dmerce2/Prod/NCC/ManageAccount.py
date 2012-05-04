#!/usr/local/env python
#

"""
Import recent Modules
"""
import sys
import string
import os

class ManageAccount:
    """
    Class to get system information
    """
    def GetSystem(self):
        """ Get system Information Solaris = SunOS """
        OS = os.uname()
        return OS[0]

    def AddUserToSystem(self, user, group):
        """ USES useradd for adding user """
        if self.GetSystem() == 'SunOS':
            if group:
                os.system('useradd -u %s -u %s' %(user, group))

    def DelUserFromSystem(self, user):
        """ Uses deluser for removing user """
        if self.GetSystem() == 'SunOs':
            os.system('deluser %s' % user)
