import aDataStruct
import misc

class ISO(aDataStruct.aDS):

    #
    # Constructor
    #
    def __init__(self):
        # Call constructor of aDS
        aDataStruct.aDS.__init__(self)
        self.mt = misc.TIME()

    def fromField(self):
        try:
            f = self.qVars['qFieldToIsoTimestamp']
            v = self.cgiFieldStorage[f]
            self.cgiFieldStorage[f] = self.mt.isoToTimestamp(v)
            return 1
        except:
            return 0
