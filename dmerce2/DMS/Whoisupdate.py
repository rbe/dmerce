#!/usr/local/bin/python

import DMS.MBase
import Prod.NCC.whoisupdate

class Update(DMS.MBase.Class):

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)

    def Do(self):
        if Prod.NCC.whoisupdate.main([]):
            return 1
        else:
            return None

    def DoOne(self):
        data = {}
        put = []
        id = self._cgi.GetField('Zone', 1)
        name = self._cgi.GetField('ZName', '')
        data['ID'] = id
        data['Name'] = name
        put.append(data)
        if Prod.NCC.whoisupdate.main(put):
            return 1
        else:
            return None
