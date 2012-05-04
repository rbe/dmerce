<%@ include file="/init.jsp" %>

<html>
<head>
<link rel="stylesheet" href="<q:res path="/css/vw.css"/>" type="text/css">
</head>
<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<qsql:prepare id="advertcats">
      SELECT id, name, description, entries, dateoflastentry FROM v_advertcategories
</qsql:prepare>

<table width="760" height="480" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#FFFFFF">
    <td width="6" height="21"><img src="images/head_ecke_w.gif" width="6" height="21"></td>
    <td class="bodytext_bold_ital12" colspan="2">Kleinanzeigen - die Welt au&szlig;erhalb
      von ebay</td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="3" height="12"><img src="images/dummy12.gif" width="1" height="12"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td colspan="3" height="1"><img src="images/bg_dbl.gif" width="1" height="1"></td>
  </tr>
  <qsql:execute id="advertcats">
  <qsql:row>
  <tr bgcolor="#FFFFFF">
    <td width="6" height="70"><img src="images/dummy6.gif" width="6"></td>
    <td width="377" height="70" class="bodytext"><a href="vwshowadvertcat.jsp?advertcategoryid=<qsql:field name="id"/>"/><qsql:field name="name"/> (<qsql:field name="entries" precision="0"/>)</a><br><qsql:field name="description"/><br><qsql:fieldnotempty name="dateoflastentry">Letzter Eintrag vom: <qsql:field name="dateoflastentry" format="dd.MM.yyyy HH:mm"/> Uhr</qsql:fieldnotempty><qsql:fieldempty name="dateoflastentry">kein Eintrag vorhanden</qsql:fieldempty></td>

    <qsql:nextrow/>

    <qsql:rownotempty>
    <td width="377" height="70" class="bodytext"><a href="vwshowadvertcat.jsp?advertcategoryid=<qsql:field name="id"/>"/><qsql:field name="name"/> (<qsql:field name="entries" precision="0"/>)</a><br><qsql:field name="description"/><br><qsql:fieldnotempty name="dateoflastentry">Letzter Eintrag vom: <qsql:field name="dateoflastentry" format="dd.MM.yyyy HH:mm"/> Uhr</qsql:fieldnotempty><qsql:fieldempty name="dateoflastentry">kein Eintrag vorhanden</qsql:fieldempty></td>
    </qsql:rownotempty>
    <qsql:rowempty>
    <td width="377" height="70" class="bodytext">&nbsp;</td>
    </qsql:rowempty>
  </tr>

  </qsql:row>
  </qsql:execute>

  <tr bgcolor="#AAB5BE">
    <td colspan="3" height="1"><img src="images/bg_dbl.gif" width="1" height="1"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="3" height="6"><img src="images/dummy6.gif" width="1" height="6"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="100%" width="6"><img src="images/dummy6.gif" width="6"></td>
    <td colspan="2" valign="top" class="bodytext" height="100%">
      <span class="bodytext_bold">Hinweis:</span><br>
        Jeder Besucher unserer Seite kann hier in den Kleinanzeigen Artikel zum
        Verkauf anbieten. Dieses ist kostenlos und auf vier Wochen befristet.
        Nach Ablauf dieses Zeitraums wird der jeweilige Artikel automatisch vom
        System gel&ouml;scht. Ein erneutes Einstellen ist nat&uuml;rlich m&ouml;glich.
        <br>
        <br>
        Wir bieten lediglich die M&ouml;glichkeit einen Kontakt zwischen Verk&auml;ufer
        und K&auml;ufer herzustellen. Daher sind wir auch nicht haftbar f&uuml;r
        Nichteinhaltung oder Nichterf&uuml;llung von Kaufabwicklungen zu machen.
        Eine Kontrolle der eingestellten Artikel findet nur bedingt statt. Wir
        behalten uns aber das Recht vor Artikel zu entfernen, wenn Sie gegen geltendes
        Recht versto&szlig;en.
      </td>
  </tr>
  <tr>
    <td colspan="3" height="1" bgcolor="#AAB5BE"><img src="images/bg_dbl.gif" width="1" height="1"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="3" height="6"><img src="images/dummy6.gif" width="1" height="6"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td width="6" height="21"><img src="images/dummy6.gif" width="6" height="6"></td>
    <td colspan="2" class="fusszeile">powered by <a target="_new" href="http://www.1ci.com">1[wan]Ci dmerce&reg;</a></td>
  </tr>
</table>

</body>
</html>