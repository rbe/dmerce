#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.5 $'
#
# Revision 1.1  2000-07-10 18:43:43+02  rb
# Initial revision
#
##

import sys
import time
import string
import Core.Log
import Core.Error
import Core.ErrorMessages
import DMS.SQL
import DMS.MBase
import DMS.Object

def ReformatFloat(f, prec):
    """ reformat floating point number """
    f_s = '%s' % f
    return float(f_s[:string.index(f_s, '.') + prec + 1])

class Stone_m2(DMS.MBase.Class):

    """
    m2-Problem with stones
    Check it
    Generate <select>-option tags
    """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = 'Auction.Stone_m2')

    def Options(self, id, pcsString, m2String, selected = 0):
        """
        generate <option>-fields for m2-problem
        - set m2x = m2 * x
        - generate <option>-string
        - check if value should be selected
        """
        options = []
        stmt = "SELECT m2, m2MinStueck, m2Stueck FROM AuctionBid WHERE ID = %i" % id
        rc, r = self._sql[stmt]
        self.__log.Write(msg = 'm2*-RESULT=%s: %s' % (stmt, str(r)))
        for x in range(r[0]['m2MinStueck'], r[0]['m2Stueck'] + 1):
            option = ''
            m2x = r[0]['m2'] * x
            option = '<option value="%i"' % x
            if selected:
                if r[i]['m2'] == selected:
                    option = '%s selected>' % option
            option = '%s>%i %s (%.4f %s)</option>' % (option, x, pcsString, m2x, m2String)
            self.__log.Write(msg = 'ADDING OPTION=%s' % option)
            options.append(option)
        if not options:
            options.append('ERROR - YOU SUBMITTED WRONG PARAMETERS!')
        self.__log.Write(msg = 'OPTIONS ARE=%s' % str(options))
        return options

class BidBase(DMS.MBase.Class):

    """ base class for a bid """

    def __init__(self, auctionId, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = 'Auction.BidBase')
        self.__auctionId = auctionId
        self.auction = None
        self.__GetInfo()

    def __GetInfo(self):
        """
        get relevant informations from database
        get data from database for auction 'AuktionsID'
        """
        stmt = "SELECT CustomerID, Auktionstyp, Startdatum, Enddatum," + \
               " naechstesGebot, Maximalpreis, Mindesterhoehung, Mindestabnahme," + \
               " AngeboteneMenge, MengederBezuege, m2Stueck FROM AuctionBid" + \
               " WHERE ID = %s" % self.__auctionId
        rc, r = self._sql[stmt]
        self.__log.Write(msg = 'GETTING INFO ABOUT AUCTION: STMT=%s, RC=%s' % (str(stmt), str(rc)))
        if not rc:
            raise Core.Error.AuctionError(1221)
        else:
            self.auction = r[0]

    def CheckTimestamps(self):
        """ check values of timestamps """
        startNowDiff = self.timeNow - self.auction['Startdatum']
        #self.__log.Write(msg = 'TIME: DIFFERENCE IN SECONDS: %s' % startNowDiff)
        if not (self.timeNow <= self.auction['Enddatum'] and startNowDiff >= -5):
            raise Core.Error.AuctionError(1111)

class BidAuction(DMS.MBase.Class, BidBase):

    """ bid in an auction """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__oc = DMS.Object.Convert()
        self.__GetAuctionId()
        BidBase.__init__(self, self.__auctionId, kw)
        self.__GetValues()
        self.__log = Core.Log.File(debug = 1, module = 'Auction.BidAuction')

    def __GetAuctionId(self):
        """ retrieve auction id from cgi """
        self.__auctionId = int(self._cgi.GetField('AuctionJoin_AuktionsID'))

    def __GetValues(self):
        """ retrieve relevant value from cgi form and database """
        self.__stueckpreis = self.__oc.ChangeType(self._cgi.GetField('AuctionJoin_Stueckpreis'), 'float')
        self.__menge = self.__oc.ChangeType(self._cgi.GetField('AuctionJoin_Menge'), 'float')
        self.__angeboteneMenge = float(self.auction['AngeboteneMenge'])
        self.__naechstesGebot = float(self.auction['naechstesGebot'])
        self.__mindestabnahme = float(self.auction['Mindestabnahme'])
        self.__mindesterhoehung = float(self.auction['Mindesterhoehung'])

    def Check(self):
        """
        1. Stueckpreis >= naechstesGebot or raise error
        2. Menge must be between Mindestmenge and AngeboteneMenge or raise error
        3. AuctionBid: set Gebot to Stueckpreis
        4. naechstesGebot to Gebot + Mindesterhoehung
        5. AuctionJoin: calculate Gesamtpreis = Stueckpreis * Menge
        6. Auction/tender: Set status 4 (best bid at this time), any other set to 2
        """
        stmts = []
        if not self.__stueckpreis >= self.__naechstesGebot:
            raise Core.Error.AuctionError(3511, '')
        if not (self.__menge >= self.__mindestabnahme and self.__menge <= self.__angeboteneMenge):
            self.__log.Write(msg = 'MENGE=%s MINDESTABNAHME=%s ANGEBOTENE MENGE=%s'
                             % (str(self.__menge), str(self.__mindestabnahme), str(self.__angeboteneMenge)))
            raise Core.Error.AuctionError(3512, '')
        stmts.append("UPDATE AuctionBid SET Gebot = %f, naechstesGebot = %f WHERE ID = %i"
                     % (self.__stueckpreis, self.__stueckpreis + self.__mindesterhoehung,
                        self.__auctionId))
        self._cgi.SetField('AuctionJoin_Gesamtpreis', self.__stueckpreis * self.__menge)
        self._cgi.SetField('AuctionJoin_status', 4)
        stmts.append("UPDATE AuctionJoin SET status = 2 WHERE AuktionsID = %i" % self.__auctionId)
        return stmts

class BidTender(DMS.MBase.Class, BidBase):

    """ bid in a tender """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__objConv = DMS.Object.Convert()
        self.__GetCgiValues()
        BidBase.__init__(self, self.__auctionId, kw)
        self.__GetAuctionValues()

    def __GetCgiValues(self):
        """ retrieve relevant values from cgi form """
        self.__auctionId = self.__objConv.ChangeType(self._cgi.GetField('AuctionJoin_AuktionsID'), 'int')
        self.__stueckpreis = self.__objConv.ChangeType(self._cgi.GetField('AuctionJoin_Stueckpreis'), 'float')

    def __GetAuctionValues(self):
        """ retrieve relevant values from database """
        self.__naechstesGebot = self.__objConv.ChangeType(self.auction['naechstesGebot'], 'float')
        self.__mindestabnahme = self.__objConv.ChangeType(self.auction['Mindestabnahme'], 'float')

    def Check(self):
        """
        1. Stueckpreis <= naechstesGebot or raise error
        2. AuctionBid: set Gebot to Stueckpreis, naechstesGebot to Stueckpreis - 0.1
        3. AuctionJoin: set Gesamtpreis = Stueckpreis * Mindestabnahme
        4. Menge must be == AngeboteneMenge
        5. Auction/tender: set status 4 (best bid at this time) any other set to 2
        """
        stmts = []
        # BACK AUCTION:
        #if not self.__stueckpreis <= self.__naechstesGebot:
        #    raise '3513'
        #stmt.append('UPDATE AuctionBid SET Gebot = %f, naechstesGebot = %f WHERE ID = %i'
        #            % (self.__stueckpreis, self.__stueckpreis - 0.1, self.__auctionId))
        #self._cgi.SetField('AuctionJoin_Gesamtpreis', self.__stueckpreis * self.__mindestabnahme)
        #self._cgi.SetField('AuctionJoin_Menge', self.__mindestabnahme)
        self._cgi.SetField('AuctionJoin_status', 4)
        stmts.append("UPDATE AuctionJoin SET status = 2 WHERE AuktionsID = %i" % self.__auctionId)
        return stmts

class BidFixedPrice(DMS.MBase.Class, BidBase):

    """ bid in fixed price auction """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__oc = DMS.Object.Convert()
        self.__GetCGI()
        BidBase.__init__(self, self.__auctionId, kw)
        self.__log = Core.Log.File(debug = self._debug, module = 'Auction.BidFixedPrice')

    def __GetCGI(self):
        """ retrieve relevant value from cgi form """
        self.__auctionId = int(self._cgi.GetField('AuctionJoin_AuktionsID'))
        self.__menge = self.__oc.ChangeType(self._cgi.GetField('AuctionJoin_Menge'), 'float')
        self.__mindestabnahme = self.__oc.ChangeType(self._cgi.GetField('AuctionJoin_Mindestabnahme'), 'float')
        self.__angeboteneMenge = self.__oc.ChangeType(self._cgi.GetField('AuctionJoin_AngeboteneMenge'), 'float')

    def Check(self):
        """
        1. Menge must be between Mindestabnahme and AngeboteneMenge
        2. Calculate new value for AngeboteneMenge
        3. AuctionBid: set AngeboteneMenge to AngeboteneMenge - Menge
        4. AuctionJoin: set Stueckpreis to Maximalpreis
        5. AuctionJoin: set Gesamtpreis to Maximalpreis * Menge
        6. AuctionJoin: set status to 3
        7. AuctionJoin: disable fixed price bid, if AngeboteneMenge == 0
        8. If AngeboteneMenge == 0
        8.1. Set aktiv to 0
        8.2. Disable fixed price bid
        """
        stmts = []
        if not (self.__menge >= self.auction['Mindestabnahme'] and self.__menge <= self.auction['AngeboteneMenge']):
            e = Core.ErrorMessages.ErrMsg()
            self.__log.Write(msgType = 'INFO', msg = '%s: %s < %s < %s'
                             % (e.Get(3512), self.auction['Mindestabnahme'],
                                self.__menge, self.auction['AngeboteneMenge']))
            raise Core.Error.AuctionError(3512)
        angeboteneMengeNew = self.auction['AngeboteneMenge'] - self.__menge
        stmts.append("UPDATE AuctionBid SET AngeboteneMenge = %f WHERE ID = %i"
                     % (angeboteneMengeNew, self.__auctionId))
        if self._cgi.GetField('m2Stueck'):
            m2StueckNew = self.auction['m2Stueck'] - int(self._cgi.GetField('m2Stueck'))
            self._sql["UPDATE AuctionBid SET m2Stueck = %i WHERE ID = %s" % (m2StueckNew, self.__auctionId)]
        self._cgi.SetField('AuctionJoin_Stueckpreis', self.auction['Maximalpreis'])
        self._cgi.SetField('AuctionJoin_Gesamtpreis', self.auction['Maximalpreis'] * self.__menge)
        self._cgi.SetField('AuctionJoin_status', 3)
        if angeboteneMengeNew == 0:
            self._cgi.SetField('active', 0)
            stmts.append("UPDATE AuctionBid SET active = 0, beendet = 1 WHERE ID = %i" % (self.__auctionId))
            self.__log.Write(submodule = '', msgType = 'INFO',
                             msg = 'AuctionBid ID %i: EVERYTHING SOLD, DISABLING' % self.__auctionId)
        return stmts

#class Bid(DMS.MBase.Class, BidBase):
class Bid(BidBase):

    """ bid in/join an auction """

    def __init__(self, kw):
        #try:
            self.__kw = kw
            DMS.MBase.Class.__init__(self, kw)
            self.__log = Core.Log.File(debug = self._debug, module = 'Auction.Bid')
            self.__objConv = DMS.Object.Convert()
            self.__timeNow = time.time()
            self.__GetCGI()
            BidBase.__init__(self, self.__auctionId, kw)
        #except:
        #    self.__log.Write(msg = '%s' % (str(sys.exc_info())))

    def __GetCGI(self):
        """ retrieve relevant value from cgi form """
        self.__auctionId = self.__objConv.ChangeType(self._cgi.GetField('AuctionJoin_AuktionsID'), 'int')
        # fetch from self.auction:
        #self.__enddatum = self.__objConv.ChangeType(self._cgi.GetField('AuctionJoin_Enddatum'), 'float')
        #self.__mengederbezuege = self.__objConv.ChangeType(
        #    self._cgi.GetField('AuctionJoin_MengederBezuege'), 'float')

    def __DeleteOldBids(self):
        """
        in auction and tender
        delete old bids of user where status = 2
        """
        stmt = "DELETE FROM AuctionJoin WHERE CustomerID = %s AND AuktionsID = %s AND status = 2" \
               % (str(self._sam.GetWho('ID')), self.__auctionId)
        self.__log.Write(submodule = '__DeleteOldBids', msgType = 'INFO',
                         msg = 'DELETING OLD BIDS OF CUSTOMER ID %s: STMT=%s'
                         % (str(self._sam.GetWho('ID')), stmt))
        self._sql[stmt]
    
    def __Check_m2Problem(self):
        """
        check m2-problem when joining an auction
        - we got 'm2Stueck'
        - calculate 'Menge'
        """
        m2Stueck = self._cgi.GetField('AuctionJoin_m2Stueck')
        if m2Stueck:
            try:
                rc, r = self._sql["SELECT m2 FROM AuctionBid WHERE ID = %i" % self.__auctionId]
            except:
                self.__log.Write(submodule = 'Check_m2Problem', msgType = 'ERROR',
                                 msg = '%s %s' % (sys.exc_info()[0], sys.exc_info()[1]))
                raise Core.Error.AuctionError(1221, '')
            self._cgi.SetField('AuctionJoin_Menge', ReformatFloat(float(int(m2Stueck) * r[0]['m2']), 4))

    def __Auction(self):
        """ auction """
        a = BidAuction(self.__kw)
        return a.Check()

    def __Tendering(self):
        """ tendering """
        a = BidTender(self.__kw)
        return a.Check()

    def __FixedPrice(self):
        """ fixed price bid """
        a = BidFixedPrice(self.__kw)
        return a.Check()

    def New(self):
        """
        1. 'now' <= 'Enddatum' && 'now' >= 'Startdatum'
        2. Check for m2-problem
        3. AuctionBid: Increase 'MengederBezuege'
        4. AuctionJoin: Set OriginatorID to id of user originating auction/tender/fixed price bid
        5. AuctionJoin: Set CustomerID to id of logged in user
        6. AuctionJoin: Set 'Abgabezeitpunkt' to 'now'
        """
        ###if not (self.timeNow <= self.result[0]['Enddatum'] and self.timeNow >= self.result[0]['Startdatum']):
        self.__Check_m2Problem()
        if self.auction['Auktionstyp'] == 1:
            stmts = self.__Auction()
        elif self.auction['Auktionstyp'] == 2:
            stmts = self.__Tendering()
        elif self.auction['Auktionstyp'] == 3:
            stmts = self.__FixedPrice()
        for stmt in stmts:
            self.__log.Write(msg = 'EXECUTING SQL STMT=%s' % str(stmt))
            self._sql[stmt]
        if self.auction['Auktionstyp'] == 1 or self.auction['Auktionstyp'] == 2:
            self.__DeleteOldBids()
        mengederbezuege = int(self.auction['MengederBezuege']) + 1
        self._sql["UPDATE AuctionBid SET MengederBezuege = %i WHERE ID = %i"
                   % (mengederbezuege, self.__auctionId)]
        self.__log.Write(submodule = 'New', msgType = 'INFO',
                         msg = 'AuctionBid ID %i INCREASED MengederBezuege BY 1, NOW %i'
                         % (self.__auctionId, int(self.auction['MengederBezuege']) + 1))
        self._cgi.SetField('AuctionJoin_OriginatorID', self.auction['CustomerID'])
        self._cgi.SetField('AuctionJoin_CustomerID', self._sam.GetWho('ID'))
        self._cgi.SetField('AuctionJoin_Abgabezeitpunkt', self.__timeNow)
        return 1

class SellBase(DMS.MBase.Class):

    """ base class for sell """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__objConv = DMS.Object.Convert()
        self.__log = Core.Log.File(debug = self._debug, module = 'Auction.Sell')
        self.__timeNow = time.time()

    def GetInfo(self, auctionId):
        """
        get relevant informations from database
        get data from database for auction 'AuktionsID'
        """
        rc, r = self._sql["SELECT CustomerID, Auktionstyp, Startdatum, Enddatum," +
                          " naechstesGebot, Maximalpreis, Mindesterhoehung, Mindestabnahme," +
                          " AngeboteneMenge, MengederBezuege, m2Stueck FROM AuctionBid" +
                          " WHERE ID = %i" % auctionId]
        if not rc:
            raise Core.Error.AuctionError(1221, '')
        else:
            self.auction = r[0]

    def Check(self):
        """ check values from cgi form """
        self.__startTime = self.CheckStartTime(self.__objConv.ChangeType(
            self._cgi.GetField('AuctionBid_Startdatum', time.time()), 'float'))
        self.__endTime = self.__objConv.ChangeType(
            self.CheckStartTime(self._cgi.GetField('AuctionBid_Enddatum', time.time())), 'float')
        self.__runTime = self.__objConv.ChangeType(self._cgi.GetField('AuctionBid_Laufzeit'), 'float')
        self._cgi.SetField('AuctionBid_active', self.__CheckActive())
        if self.__startTime and self.__runTime:
            self._cgi.SetField('AuctionBid_Enddatum', self.CalcEndTime())
        elif self.__startTime and self.__endTime:
            self._cgi.SetField('AuctionBid_Laufzeit', self.__CalcRunTime())
        elif self.__endTime and self.__runTime:
            pass #self._cgi.SetField()
        self._cgi.SetField('AuctionBid_CustomerID', self._sam.GetWho('ID'))
        if self._cgi.GetField('AuctionBid_m2Stueck'):
            self.Check_m2Problem()

    def CheckStartTime(self, start):
        """
        check start timestamp
        calculate difference between now and given start timestamp
        if difference is in our tolerance set 'Startdatum' to 'now'
        tolerance is 900 seconds
        """
        #self.__log.Write(msg = 'comparing %s %s - %s %s <= 900'
        #                 % (str(self.__timeNow), type(self.__timeNow), str(start), type(start)))
        diff = self.__timeNow - start
        if diff <= 3600:
            return self.__timeNow
        else:
            self.__log.Write(msgType = 'ERROR', msg = 'TIMENOW %f - STARTTIME %f = %s (>= 3600 SECONDS)!'
                             % (self.__timeNow, start, str(diff)))
            raise Core.Error.AuctionError(1111, '')

    def CalcEndTime(self):
        """ calculate end timestamp """
        return self.__startTime + self.__runTime

    def __CalcRunTime(self):
        """ calculate runtime """
        return self.__endTime - self.__startTime

    def __CheckActive(self):
        """ check if we should be active now or later """
        if self.__startTime > self.__timeNow:
            return 0
        else:
            return 1

    def Check_m2Problem(self):
        """
        check/calculate value for m2-problem
        check existence of 'Breite' and 'Hoehe'
        calculate 'AuctionBid.m2', 'AuctionBid.Mindestabnahme', 'AuctionBid.AngeboteneMenge'
        convert to exactly %.4f
        """
        if not self._cgi.GetField('AuctionBid_Breite') \
           or not self._cgi.GetField('AuctionBid_Hoehe') \
           or not self._cgi.GetField('AuctionBid_m2MinStueck'):
            raise Core.Error.AuctionError(3521, '')
        else:
            self._cgi.SetField('AuctionBid_m2', ReformatFloat(
                float(self._cgi.GetField('AuctionBid_Breite')) / 100 *
                float(self._cgi.GetField('AuctionBid_Hoehe')) / 100, 4))
            self._cgi.SetField('AuctionBid_Mindestabnahme', ReformatFloat(
                int(self._cgi.GetField('AuctionBid_m2MinStueck')) * self._cgi.GetField('AuctionBid_m2'), 4))
            self._cgi.SetField('AuctionBid_AngeboteneMenge', ReformatFloat(
                int(self._cgi.GetField('AuctionBid_m2Stueck')) * self._cgi.GetField('AuctionBid_m2'), 4))
            return 1

class SellAuction(SellBase):

    """ auction """

    def __init__(self, kw):
        SellBase.__init__(self, kw)
        mindestgebot = self._cgi.GetField('AuctionBid_Mindestgebot')
        self._cgi.SetField('AuctionBid_Gebot', mindestgebot)
        self._cgi.SetField('AuctionBid_naechstesGebot', mindestgebot)

class SellBackAuction(SellBase):

    """ back-auction """

    def __init__(self, kw):
        SellBase.__init__(self, kw)
        maximalpreis = self._cgi.GetField('AuctionBid_Maximalpreis')
        self._cgi.SetField('AuctionBid_Gebot', maximalpreis)
        self._cgi.SetField('AuctionBid_naechstesGebot', maximalpreis)
                                              
class SellTender(SellBase):

    """ tender """

    def __init__(self, kw):
        SellBase.__init__(self, kw)

class SellFixedPrice(SellBase):

    """ fixed price """

    def __init__(self, kw):
        SellBase.__init__(self, kw)
        maximalpreis = self._cgi.GetField('AuctionBid_Maximalpreis')
        self._cgi.SetField('AuctionBid_Gebot', maximalpreis)
        self._cgi.SetField('AuctionBid_naechstesGebot', maximalpreis)

class SellChange(SellBase):

    """ change an auction """

    def __init__(self, kw, auktionsid):
        SellBase.__init__(self, kw)
        self.__auctionId = auktionsid
        SellBase.GetInfo(self, auktionsid)

    def CheckBids(self):
        """
        do we have bids?
        if yes, raise an AuctionError
        """
        rc, r = self._sql["SELECT COUNT(*) FROM AuctionJoin WHERE AuktionsID = %i" % self.__auctionId]
        if r[0]['COUNT(*)']:
            raise Core.Error.AuctionError(3515, '')

    def Update(self):
        """ check for update """
        if self._cgi.GetField('m2Stueck'):
            self.Check_m2Problem()
        #self.CheckStartTime(self.auction['Startdatum'])
        #self.CalcEndTime()
        return 1

class Sell(DMS.MBase.Class):

    """ sell """

    def __init__(self, kw):
        self.__kw = kw
        DMS.MBase.Class.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = 'Auction.Sell')
        self.__objConv = DMS.Object.Convert()

    def __NewCollectCGI(self):
        """ collect relevant cgi form values """
        try:
            dboid = DMS.SQL.DBOID(self._sqlSys, self._sql)
            self.__id = dboid['AuctionBid']
            self.__auktionstyp = self.__objConv.ChangeType(self._cgi.GetField('AuctionBid_Auktionstyp'), 'int')
            self.__mindestabnahme = self._cgi.GetField('AuctionBid_Mindestabnahme')
            self.__angeboteneMenge = self._cgi.GetField('AuctionBid_AngeboteneMenge')
        except:
            raise Core.Error.AuctionError(3510, '')
        if self.__mindestabnahme > self.__angeboteneMenge:
            raise Core.Error.AuctionError(3512, '')

    def __LogNew(self):
        """ log insert of auction into database """
        b = ('', 'AUCTION', 'TENDER', 'FIXED PRICE')
        self.__log.Write(submodule = 'NewEntry', msg = 'INSERTING NEW %s INTO DATABASE' %
                         b[self.__auktionstyp])

    def New(self):
        """
        make a new entry
        check 'Mindestabnahme' (if less than or equal to 0, set to 'AngeboteneMenge')
        """
        self.__NewCollectCGI()
        if self.__mindestabnahme <= 0:
            self._cgi.GetField('AuctionBid_Mindestabnahme', self.__angeboteneMenge)
        if self.__auktionstyp == 1:
            self.__sell = SellAuction(self.__kw)
        elif self.__auktionstyp == 2:
            self.__sell = SellTender(self.__kw)
        elif self.__auktionstyp == 3:
            self.__sell = SellFixedPrice(self.__kw)
        self.__sell.Check()
        self.__LogNew()
        return 1

    def Change(self):
        """ UPDATE/DELETE an entry (only if no bids exist) """
        self.__sell = SellChange(self.__kw, self.__objConv.ChangeType(self._cgi.GetField('AuctionBid_ID'), 'int'))
        self.__sell.CheckBids()
        self.__sell.Update()
        return 1
