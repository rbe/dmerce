<%@ include file="/init.jsp" %>

<html>
<head>
<!--<link rel="stylesheet" href="vw.css" type="text/css">-->
<link rel="stylesheet" href="<q:res path="/css/vw.css"/>" type="text/css">
</head>
<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<!-- Informationen ueber aktuelle Kateogorie -->
<qsql:prepare id="advertcatname">
      SELECT name, entries FROM v_advertcategories WHERE id = <q:contextvar name="advertcategoryid"/>
</qsql:prepare>

<table width="760" height="480" border="0" cellspacing="0" cellpadding="0">

  <qsql:execute id="advertcatname">
  <qsql:row>

  <tr bgcolor="#FFFFFF">
    <td width="6" height="21"><img src="images/head_ecke_w.gif"></td>
    <td class="bodytext_bold_ital12" colspan="2">Kleinanzeigen - Anzeige verfassen in <qsql:field name="name"/> (<qsql:field name="entries"/>)</td>
  </tr>

  </qsql:row>
  </qsql:execute>

  <tr bgcolor="#FFFFFF">
    <td height="12" colspan="2"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td height="1" colspan="2"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="6" colspan="2"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="12"><img src="images/dummy.gif"></td>
    <td height="12" class="bodytext">F&uuml;llt bitte alle mit
      einem * markierten Felder aus. Bei einer Fehlermeldung &uuml;berpr&uuml;ft
      bitte das/die angemerkte(n) Feld(er).</td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="12" colspan="2"><img src="images/dummy.gif"></td>
  </tr>
  <tr class="bodytext_bold_w" bgcolor="#CCCCCC">
    <td height="21" background="images/bg_hbl.gif"><img src="images/dummy.gif"></td>
    <td background="images/bg_hbl.gif">Eintrag</td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="6" colspan="3"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="100%"><img src="images/dummy.gif"></td>
    <td valign="top"><qform:form id="1cs_adverts">
    <qform:hidden name="advertcategoryid" contextvarname="advertcategoryid"/>
      <table width="754" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bodytext_bold" width="150" align="left" valign="top">Dein Name</td>
          <td width="604"><qform:text name="realname"/>&nbsp;<qform:errormessage name="realname"/></td>
        </tr>
        <tr>
          <td class="bodytext_bold" width="150" align="left" valign="top">Deine E-Mail Adresse</td>
          <td width="604"><qform:text name="email"/>&nbsp;<qform:errormessage name="email"/></td>
        </tr>
        <tr>
          <td class="bodytext_bold" width="150" align="left" valign="top">Deine Website</td>
          <td width="604"><qform:text name="url"/>&nbsp;<qform:errormessage name="url"/></td>
        </tr>
        <tr>
          <td class="bodytext_bold" width="150" align="left" valign="top">&Uuml;berschrift</td>
          <td width="604"><qform:text name="description"/>&nbsp;<qform:errormessage name="description"/></td>
        </tr>
        <tr>
          <td class="bodytext_bold" width="150" align="left" valign="top">Deine Anzeige</td>
          <td width="604"><qform:textarea name="text" cols="50" rows="5"/>&nbsp;<qform:errormessage name="text"/></td>
        </tr>
        <tr>
          <td class="bodytext_bold" width="150">Dein Bild</td>
          <td width="604"><qform:file name="foto"/>&nbsp;<qform:errormessage name="foto"/></td>
        </tr>
        <tr>
          <td class="bodytext_bold" colspan="2" height="12"><img src="images/dummy.gif"></td>
        </tr>
        <tr class="bodytext_bold">
          <td colspan="2"><qform:button type="submit" text="Anzeige aufgeben"/>&nbsp;<qform:button type="submit" text="Zurück"/></td>
        </tr>
      </table>
      </qform:form> </td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td height="1" colspan="2"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="6" colspan="2"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td width="6" height="21"><img src="images/dummy.gif"></td>
    <td class="fusszeile">powered by <a target="_new" href="http://www.1ci.com">1[wan]Ci dmerce&reg;</a></td>
  </tr>
</table>
</body>
</html>