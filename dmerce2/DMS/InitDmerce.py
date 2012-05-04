#!/usr/bin/env python
##
#
# $Author: ka $

import DMS.SysTbl
import Guardian.Config
import sys
import os
import vars


class NCC:

    """ First initialisation of NCC getting variables and database connection"""

    def __init__(self):
        vars.vars()
        try:
            self.__conf = Guardian.Config.RFC822(os.environ['DMERCE_HOME'])
        except ImportError:
            print 'You always have to set DMERCE_HOME in your environment!'
            print 'Example in bash:$ export DMERCE_HOME=/path/to/dmerce'
            print 'This is the right error message: ', val
            sys.exit()

    def InitNCC(self):
        self.sysTbl = DMS.SysTbl.Retrieve()
        self.sysTbl.SetConfig(self.__conf)
        try:
            fqhn = self.__conf.cp.get('ncc', 'FQHN')
        except:
            print 'There is no Field FQHN under [ncc] in '+os.environ['DMERCE_HOME']+'/dmerce.cfg'
            print 'Add the FQHN and try again!'
            sys.exit()
        self.sysTbl.SetFQHN(fqhn)
        self.sysTbl.Init()
        
    def InitDBConnection(self):
        return self.sysTbl.GetSQLSys()
        
    def GetVar(self, var):
        return self.__conf.cp.get('ncc', var)
    
#if __name__ == '__main__':

#    a = NCC()
#    a.InitNCC()
#    sqlData = a.InitDBConnection()
#    print a.GetVar('Homeprefix')
#    print a.GetVar('fqhn')
