<%@ include file="/init.jsp" %>

<html>
<head>
<!--<link rel="stylesheet" href="vw.css" type="text/css">-->
<link rel="stylesheet" href="<q:res path="/css/vw.css"/>" type="text/css">
</head>
<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<table width="760" height="480" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#FFFFFF">
    <td width="6" height="21"><img src="images/head_ecke_w.gif" width="6" height="21"></td>
    <td class="bodytext_bold_ital12" colspan="2">Terminkalender - Eintrag verfassen</td>
  </tr>
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
    <td class="bodytext">F&uuml;llt bitte alle mit einem * markierten Felder aus. Bei einer Fehlermeldung &uuml;berpr&uuml;ft das/die angemerkte(n) Feld(er).</td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="12" colspan="2"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#CCCCCC" class="bodytext_bold_w">
    <td height="21" background="images/bg_hbl.gif"><img src="images/dummy.gif"></td>
    <td background="images/bg_hbl.gif">Eintrag</td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="3" height="6"><img src="images/dummy.gif"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="100%"><img src="images/dummy.gif"></td>
    <td valign="top"><qform:form id="1cs_appointments">
      <table width="754" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bodytext_bold" width="150" align="left" valign="top">Dein 
            Name</td>
          <td width="604"><qform:text name="realname"/>&nbsp;<qform:errormessage name="realname"/></td>
        </tr>
        <tr>
          <td class="bodytext_bold" width="150" align="left" valign="top">Deine 
            E-Mail Adresse</td>
          <td width="604"><qform:text name="email"/>&nbsp;<qform:errormessage name="email"/></td>
        </tr>
        <tr>
          <td class="bodytext_bold" width="150" align="left" valign="top">Deine 
            Website</td>
          <td width="604"><qform:text name="url"/>&nbsp;<qform:errormessage name="url"/></td>
        </tr>
        <tr>
          <td class="bodytext_bold" width="150" align="left" valign="top">Deine 
            Telefonnummer</td>
          <td width="604"><qform:text name="phone"/>&nbsp;<qform:errormessage name="phone"/></td>
        </tr>
        <tr>
          <td class="bodytext_bold" width="150" align="left" valign="top">Dein 
            Termin (TT.MM.JJJJ)</td>
          <td width="604"><qform:text name="dateofappointment"/>&nbsp;<qform:errormessage name="dateofappointment"/></td>
        </tr>
        <tr>
          <td class="bodytext_bold" width="150" align="left" valign="top">Dein Eintrag</td>
          <td width="604"><qform:textarea name="description" cols="50" rows="5"/>&nbsp;<qform:errormessage name="description"/></td>
        </tr>
        <tr>
          <td class="bodytext_bold" colspan="2" height="12"><img src="images/dummy.gif"></td>
        </tr>
        <tr>
          <td colspan="2"><qform:button type="submit" text="Termin eintragen"/>&nbsp;<qform:button type="submit" text="Zurück"/></td>
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
    <td height="21"><img src="images/dummy.gif"></td>
    <td class="fusszeile">powered by <a target="_new" href="http://www.1ci.com">1[wan]Ci dmerce&reg;</a></td>
  </tr>
</table>

</body>
</html>