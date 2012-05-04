#!/usr/local/bin/python

import sys
import DMS.MBase

class Dues(DMS.MBase.Class):

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)

    def Create(self):
        import customer.igga.banking
        var = self._cgi.GetField('Test', 1)
        if customer.igga.banking.main(var):
            return 1
        else:
            return None
    
    def CreateVSV(self):
        import customer.vsv.banking
        var = self._cgi.GetField('Test', 1)
        if customer.vsv.banking.main(var):
            return 1
        else:
            return None
