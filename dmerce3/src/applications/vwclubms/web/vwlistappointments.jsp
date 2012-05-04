<%@ include file="/init.jsp" %>

<html>
<head>
<link rel="stylesheet" href="<q:res path="/css/vw.css"/>" type="text/css">
</head>

<qsql:prepare id="appointments">
      SELECT dateofappointment, description, url, realname, email, phone FROM t_appointments WHERE dateofappointment >= SYSDATE - 1 ORDER BY dateofappointment
</qsql:prepare>

<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#FFFFFF">
    <td width="6" height="21"><img src="images/head_ecke_w.gif"></td>
    <td colspan="3"class="bodytext_bold_ital12">Termine - wann, was, wo?</td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="12"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td colspan="4" height="1"><img src="images/bg_dbl.gif"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="6"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="1"><img src="images/dummy.gif"></td>
    <td colspan="3" class="bodytext_bold">
      <div align="left" class="bodytext">Hier habt Ihr die M&ouml;glichkeit Eure eigenen oder Euch bekannte
		Termin zu veröffentlichen. Tragt bitte nur zum Thema passende Veranstaltungen ein und überprüft vorher,
		ob nicht schon jemand den gleichen Eintrag gemacht hat. Wir &uuml;bernehmen keine Garantie f&uuml;r die
		Richtigkeit der angegebenen Termine und Veranstaltungen. Bitte richtet Fragen und Anmerkungen zu den
		jeweiligen Veranstaltungen nicht an uns, sondern an die angegebene Kontaktperson/-adresse.</div>
    </td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="12" colspan="4"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td height="1" colspan="4"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="6"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="12"><img src="images/dummy.gif"></td>
    <td colspan="3" class="bodytext">
      <div align="center"><q:workflowlink id="1cs_appointments" restart="true">Neuen Termin eintragen</q:workflowlink></div>
    </td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="12" colspan="4"><img src="images/dummy.gif"></td>
  </tr>
  <tr class="bodytext_bold_white">
    <td height="21" width="6" background="images/bg_hbl.gif"><img src="images/dummy.gif"></td>
    <td width="100" class="bodytext_bold_w" background="images/bg_hbl.gif">Datum</td>
    <td width="404" class="bodytext_bold_w" background="images/bg_hbl.gif">Veranstaltung</td>
    <td width="250" class="bodytext_bold_w" background="images/bg_hbl.gif">Kontakt
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="6"><img src="images/dummy.gif"></td>
  </tr>

  <qsql:execute id="appointments">
  <qsql:row>

  <tr valign="top" bgcolor="#FFFFFF">
    <td width="6"><img src="images/dummy.gif"></td>
    <td width="100" class="bodytext_bold"><qsql:field name="dateofappointment" format="dd.MM.yyyy"/></td>
    <td width="404" class="bodytext"><qsql:field name="description"/></td>
    <td width="250" class="bodytext"><qsql:field name="realname"/><br>
      <a href="mailto:<qsql:field name="email"/>"><qsql:field name="email"/></a><br><a target="_new" href="http://<qsql:field name="url"/>"><qsql:field name="url"/></a><br><qsql:field name="phone"/></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="12" colspan="4"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td height="1" colspan="4"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="6"><img src="images/dummy.gif"></td>
  </tr>
  </qsql:row>
  </qsql:execute>
  <tr bgcolor="#FFFFFF">
    <td height="12"><img src="images/dummy.gif"></td>
    <td colspan="3" class="bodytext">
      <div align="center"><q:workflowlink id="1cs_appointments">Neuen Termin eintragen</q:workflowlink></div>
    </td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="12"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td colspan="4" height="1"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="6"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td width="6" height="21"><img src="images/dummy.gif"></td>
    <td colspan="3" class="fusszeile">powered by <a target="_new" href="http://www.1ci.com">1[wan]Ci dmerce&reg;</a></td>
  </tr>
</table>

</body>
</html>