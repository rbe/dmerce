<%@ include file="/init.jsp" %>

<html>
<head>
<link rel="stylesheet" href="<q:res path="/css/vw.css"/>" type="text/css">
</head>
<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<qsql:prepare id="linkcats">
      SELECT id, name, description, entries, dateoflastlink, urloflastlink FROM v_linkcategories
</qsql:prepare>

<table width="760" height="480" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#FFFFFF">
    <td width="6" height="21"><img src="images/head_ecke_w.gif" width="6" height="21"></td>
    <td class="bodytext_bold_ital12" colspan="2">Links - wo es sonst noch was zu sehen gibt</td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="3" height="12"><img src="images/dummy12.gif" width="1" height="12"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td colspan="3" height="1"><img src="images/bg_dbl.gif" width="1" height="1"></td>
  </tr>

  <qsql:execute id="linkcats">
  <qsql:row>

  <tr>
    <td width="6" height="70"><img src="images/dummy6.gif" width="6"></td>
    <td width="377" class="bodytext"><a href="vwshowlinkcat.jsp?linkcategoryid=<qsql:field name="id"/>&restart"><qsql:field name="name"/> (<qsql:field name="entries" precision="0"/>)</a><br><qsql:field name="description"/><br><qsql:fieldnotempty name="dateoflastlink">Letzter Link vom <qsql:field name="dateoflastlink" format="dd.MM.yyyy HH:mm"/> Uhr <br><a target="_new" href="http://<qsql:field name="urloflastlink"/>">http://<qsql:field name="urloflastlink"/></a></qsql:fieldnotempty><qsql:fieldempty name="dateoflastlink">kein Link vorhanden</qsql:fieldempty></td>

    <qsql:nextrow/>

    <qsql:rownotempty>
    <td width="377" class="bodytext"><a href="vwshowlinkcat.jsp?linkcategoryid=<qsql:field name="id"/>"><qsql:field name="name"/> (<qsql:field name="entries" precision="0"/>)</a><br><qsql:field name="description"/><br><qsql:fieldnotempty name="dateoflastlink">Letzter Link vom <qsql:field name="dateoflastlink" format="dd.MM.yyyy HH:mm"/> Uhr <br><a target="_new" href="http://<qsql:field name="urloflastlink"/>">http://<qsql:field name="urloflastlink"/></a></qsql:fieldnotempty><qsql:fieldempty name="dateoflastlink">kein Link vorhanden</qsql:fieldempty></td>
    </qsql:rownotempty>
    <qsql:rowempty>
    <td width="377" class="bodytext">&nbsp;</td>
    </qsql:rowempty>
  </tr>

  </qsql:row>
  </qsql:execute>

  <tr bgcolor="#AAB5BE">
    <td colspan="3" height="1"><img src="images/bg_dbl.gif" width="1" height="1"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="3" height="6"><img src="images/dummy6.gif" width="6" height="6"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="100%" width="6"><img src="images/dummy6.gif" width="6"></td>
    <td colspan="2" valign="top" class="bodytext"></td>
  </tr>
  <tr>
    <td colspan="3" height="1" bgcolor="#AAB5BE"><img src="images/bg_dbl.gif" width="1" height="1"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="3" height="6"><img src="images/dummy6.gif" width="1" height="6"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="21" width="6"><img src="images/dummy6.gif" width="6" height="6"></td>
    <td colspan="2" class="fusszeile" height="21">powered by <a target="_new" href="http://www.1ci.com">1[wan]Ci dmerce&reg;</a></td>
  </tr>
</table>

</body>
</html>