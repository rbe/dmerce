#!/usr/local/bin/python
##
#
# $Author: rb $
revision = '$Revision: 1.21 $'
#
##

#import sys
#sys.path.append('/export/home/r/rb/dmerce2')
import Core.Log
import DMS.MBase

class Rotating(DMS.MBase.Class):

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__log = Core.Log.File(debug = 1, module = '1[Ads].Rotating')

    def Show(self, BannerSize = 0, PictureURLBase = '/'):
        if self._sql.GetType() == 'POSTGRESQL':
            stmt = "SELECT ID, URL, BannerPicture, ShownCount" \
                   "  FROM Advertisement" \
                   " WHERE BannerSize = %s" \
                   "   AND BannerFrom <= current_date" \
                   "   AND BannerUntil >= current_date" \
                   " ORDER BY Shown, ShownCount" % BannerSize
        elif self._sql.GetType() == 'MYSQL':
            stmt = "SELECT ID, URL, BannerPicture, ShownCount" \
                   "  FROM Advertisement" \
                   " WHERE BannerSize = %s" \
                   "   AND BannerFrom <= NOW()" \
                   "   AND BannerUntil >= NOW()" \
                   " ORDER BY Shown, ShownCount" % BannerSize
        elif self._sql.GetType() == 'ORACLE':
            stmt = "SELECT ID, URL, BannerPicture, ShownCount" \
                   "  FROM Advertisement" \
                   " WHERE BannerSize = %s" \
                   "   AND BannerFrom <= SYSDATE" \
                   "   AND BannerUntil >= SYSDATE" \
                   " ORDER BY Shown, ShownCount" % BannerSize
        self.__log.Write(msg = stmt)
        rc, r = self._sql[stmt]
        # zweiten eintrag aus der liste nehmen
        ad = r[0]
        # showncount+=1 setzen
        stmt = "UPDATE Advertisement" \
               "   SET Shown = 1, ShownCount = %s" \
               " WHERE ID = %s" % (ad['ID'], ad['ShownCount'] + 1)
        self._sql[stmt]
        self.__log.Write(msg = stmt)
        stmt = "UPDATE Advertisement" \
               "   SET Shown = 0" \
               " WHERE ID != %s" % ad['ID']
        self._sql[stmt]
        self.__log.Write(msg = stmt)
        # anzeigen
        return '<a href="http://%s" target="_new"><img border="0" src="%s/%s"></a>' \
               % (ad['URL'], PictureURLBase, ad['BannerPicture'])
