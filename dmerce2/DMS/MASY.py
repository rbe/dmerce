#!/usr/local/bin/python
##
#
# $Author: rb $
revision = "$Revision: 2.32 $"
#
##

#import sys
#sys.path.append('/export/home/r/rb/dmerce2/')
import os
import types
import string
import Core.OS
import Core.Log
import DMS.MBase
import DMS.Time
import DMS.Graphic

class Deckung(DMS.MBase.Class):

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = 'MASY.Deckung')

    def Pruefen(self, schadendatum = 'NULL', fahrzeugId = 0):
        stmt = "SELECT fahrzeugid" \
               " FROM masy5.deckungsumfang" \
               " WHERE fahrzeugid = %s" \
               " AND gueltigab <= TO_DATE('%s', 'DD.MM.YYYY')" \
               " AND gueltigbis >= TO_DATE('%s', 'DD.MM.YYYY')" % (fahrzeugId, schadendatum, schadendatum)
        #self.__log.Write(msg = 'Deckungspruefung: STMT=' + stmt)
        rc, r = self._sql[stmt]
        if rc:
            return 1
        else:
            return 0

class Reporting(DMS.MBase.Class):

    """ create images for MASY/Reporting """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__osBase = Core.OS.Base()
        self.__log = Core.Log.File(debug = self._debug, module = 'MASY.Reporting')
        self.__sdv = DMS.Graphic.SDVImage(kw)
        self.__whoRechtegruppe = self._sam.GetWho('Rechtegruppe')
        self.__schadendatum = "schadendatum > TO_DATE('%s', 'YYYY-MM-DD-HH24-MI-SS')" \
                              " AND schadendatum < TO_DATE('%s', 'YYYY-MM-DD-HH24-MI-SS')"

    def GenerateDiagram(self, tmpdir = '/pic/tmp', title = 'Schäden nach Uhrzeit',
                        size = '800,600', firmaId = 0, datumVon = '', datumBis = '',
                        stmts = [], x = [], xname = '', yname = ''):
        y = []
        schadendatum = self.__schadendatum % (datumVon, datumBis)
        for stmt in stmts:
            try:
                rc, r = self._sql[stmt]
                if not rc:
                    y.append(0)
                else:
                    y.append(r[0]['c1'])
            except:
                y.append(-1)
        fn = self.__sdv.Create(tmpdir = tmpdir, title = title, size = '930,698', x = x, y = y,
                               xname = xname, yname = yname)
        self.__log.Write(msg = 'GENERATED SDV IMAGE: NAME=%s SIZE=%s X=%s Y=%s' % (fn, size, x, y))
        return fn
        
    def DiagrammSchaedenNachUhrzeit(self, tmpdir = '/pic/tmp', title = 'Schäden nach Uhrzeit',
                                    size = '800,600', firmaId = 0, datumVon = '', datumBis = ''):
        schadendatum = self.__schadendatum % (datumVon, datumBis)
        stmts = [
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschuhr0003" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschuhr0306" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschuhr0609" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschuhr0912" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschuhr1215" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschuhr1518" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschuhr1821" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschuhr2124" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            ]
        x = ['00-03', '03-06', '06-09', '09-12', '12-15', '15-18', '18-21', '21-00']
        return self.GenerateDiagram(tmpdir, title, size, firmaId, datumVon, datumBis,
                                    stmts, x, 'Uhrzeit', 'Schäden')

    def DiagrammSchaedenNachWochentag(self, tmpdir = '/pic/tmp', title = 'Schäden nach Wochentag',
                                      size = '800,600', firmaId = 0, datumVon = '', datumBis = ''):
        schadendatum = self.__schadendatum % (datumVon, datumBis)
        stmts = [
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschtagmon" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschtagdie" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschtagmit" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschtagdon" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschtagfre" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschtagsam" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschtagson" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            ]
        x = ['Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag', 'Sonntag']
        return self.GenerateDiagram(tmpdir, title, size, firmaId, datumVon, datumBis,
                                    stmts, x, 'Wochentag', 'Schäden')

    def DiagrammSchaedenNachDrittel(self, tmpdir = '/pic/tmp', title = 'Schäden nach Drittel',
                                    size = '800,600', firmaId = 0, datumVon = '', datumBis = ''):
        schadendatum = self.__schadendatum % (datumVon, datumBis)
        stmts = [
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschdri0110" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschdri1120" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            "SELECT COUNT(*) AS c1 FROM masy5.vrepschdri2131" + self.__whoRechtegruppe +
            " WHERE firmenid = %s AND %s" % (firmaId, schadendatum),
            ]
        x = ['01.-10.', '11.-20.', '21.-31.']
        return self.GenerateDiagram(tmpdir, title, size, firmaId, datumVon, datumBis,
                                    stmts, x, 'Tage', 'Schäden')

    def DiagrammSchaedenNachMonat(self, tmpdir = '/pic/tmp', title = 'Schäden nach Monat',
                                  size = '800,600', firmaId = 0, datumVon = '', datumBis = ''):
        schadendatum = self.__schadendatum % (datumVon, datumBis)
        stmts = []
        for i in range(1, 13):
            s = '%02i' % i
            stmts.append(
                "SELECT COUNT(*) AS c1 FROM masy5.vrepschmon" + s + self.__whoRechtegruppe +
                " WHERE firmenid = %s AND %s" % (firmaId, schadendatum)
                )
        x = ['Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun', 'Jul', 'Aug', 'Sep',
             'Okt', 'Nov', 'Dez']
        return self.GenerateDiagram(tmpdir, title, size, firmaId, datumVon, datumBis,
                                    stmts, x, 'Monat', 'Schäden')

    def DiagrammSchaedenNachSchadenort(self, tmpdir = '/pic/tmp', title = 'Schäden nach Schadenort',
                                       size = '800,600', firmaId = 0, datumVon = '', datumBis = ''):
        schadendatum = self.__schadendatum % (datumVon, datumBis)
        rc, r = self._sql["SELECT id, schadenort FROM masy5.schadenort WHERE id > 0"]
        stmts = []
        x = []
        for i in r:
            stmts.append(
                "SELECT COUNT(*) AS c1 FROM masy5.vrepschschort" + str(i['id'])[:-1] + self.__whoRechtegruppe +
                " WHERE firmenid = %s AND %s" % (firmaId, schadendatum)
                )
            x.append(i['schadenort'])
        return self.GenerateDiagram(tmpdir, title, size, firmaId, datumVon, datumBis,
                                    stmts, x, 'Monat', 'Schäden')

    def DiagrammMeldezeitraum(self, tmpdir = '/pic/tmp', title = 'Meldezeitraum',
                              size = '800,600', firmaId = 0, datumVon = '', datumBis = ''):
        schadendatum = self.__schadendatum % (datumVon, datumBis)
        stmt = "SELECT polkz, delta" \
               "  FROM masy5.vrepmeldezraum" \
               " WHERE firmenId = '%s'" \
               "   AND %s" \
               "   AND rownum < 21" \
               % (firmenId, schadendatum)
        rc, r = self._sql[stmt]
        x = []
        for row in r:
            x.append(r[polkz])
        fn = self.__sdv.Create(tmpdir = tmpdir, title = title, size = '930,698', x = x, y = y,
                               xname = 'Kennzeichen', yname = 'Delta')
        self.__log.Write(msg = 'GENERATED SDV IMAGE: NAME=%s SIZE=%s X=%s Y=%s' % (fn, size, x, y))
        return fn

    def SummeAufwandNachSchadenart(self, firmaId = 0, datumVon = '', datumBis = '', schadenartTextId = 0):
        schadendatum = self.__schadendatum % (datumVon, datumBis)
        stmt = "SELECT SUM(summe) AS c" \
               "  FROM masy5.vrepsumaufschart" \
               " WHERE firmenid = %s" \
               "   AND satid = %s AND %s" % (firmenId, schadenartTextId, schadendatum)
        rc, r = self._sql[stmt]
        return r[0]['c']

    def Schadensfrequenz(self, firmaId = 0, datumVon = '', datumBis = ''):
        """
        alle schaeden und fahrzeuge aus einem gewaehlten zeitraum
        anzahl schaeden innerhalb des zeitraums und anzahl fahrzeuge, die
        in diesem zeitraum bereits existierten
        """
        schadendatum = self.__schadendatum % (datumVon, datumBis)
        stmt = "SELECT COUNT(*) AS c FROM masy5.fahrzeuge WHERE kfzhalterfirmaid = %s" % firmaId
        rc, r = self._sql[stmt]
        fahrzeuge = r[0]['c']
        stmt = "SELECT COUNT(*) AS c FROM masy5.schaden WHERE firmenid = %s AND %s" \
               % (firmaId, schadendatum)
        rc, r = self._sql[stmt]
        schaeden = r[0]['c']
        if fahrzeuge > 0 and schaeden > 0 :
            r = '%.2f' % (schaeden * 100 / fahrzeuge)
        else:
            r = 0.0
        return r
