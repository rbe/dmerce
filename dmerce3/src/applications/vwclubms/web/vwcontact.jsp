<%@ include file="/init.jsp" %>

<html>
<head>
<link rel="stylesheet" href="<q:res path="/css/vw.css"/>" type="text/css">
</head>

<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="760" border="0" cellspacing="0" cellpadding="0" height="480">
  <tr>
    <td width="6" height="21"><img src="images/head_ecke_w.gif" width="6" height="21"></td>
    <td width="654" height="21" class="bodytext_bold_ital12" bgcolor="#FFFFFF">Kontakt
      <span class="bodytext">- Bitte f&uuml;llt </span><span class="bodytext_bold">alle</span><span class="bodytext">
      Felder dieses Kontaktformulars aus. Danke. </span></td>
  </tr>
  <tr>
    <td width="760" height="12" colspan="2" bgcolor="#FFFFFF"><img src="images/dummy12.gif" width="12" height="12"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td width="760" height="1" colspan="2"><img src="images/dummy.gif" width="1" height="1"></td>
  </tr>
  <tr>
    <td width="760" height="6" colspan="2" bgcolor="#FFFFFF"><img src="images/dummy6.gif" width="6" height="6"></td>
  </tr>
  <tr>
    <td width="6" bgcolor="#FFFFFF"><img src="images/dummy.gif" width="1" height="1"></td>
    <td bgcolor="#FFFFFF" width="754">
      <form action="http://www.services.1ci.de/email.pyc/do" method="POST">
        <p class="bodytext">
          <input type="hidden" name="recipient" value="info@vwclubms.de">
          <input type="hidden" name="subject" value="Nachricht von vwclubms.de">
          <input type="hidden" name="sequence" value="Name, Alter, Fahrzeug, email, Kommentar">
          <input type="hidden" name="format" value="1">
          <input type="hidden" name="required" value="Name, email">
          <input type="hidden" name="redirect_ok" value="http://www.vwclubms.de/mail_danke.html">
          <input type="hidden" name="redirect_missing_fields" value="http://www.vwclubms.de/mail_error.html">
        </p>
        <p class="bodytext_bold">Name:<br>
          <input type="text" name="Name" size="34">
          <br>
          <br>
          Alter:<br>
          <input type="text" name="Alter" size="6" maxlength="3">
          <img src="images/dummy.gif" width="1" height="1"> Jahre<br>
          <br>
          Mailadresse:<br>
          <input type="text" name="email" size="34">
          <br>
          <br>
          Fahrzeug:<br>
          <input type="text" name="Fahrzeug" size="34">
          <br>
          <br>
          Kommentar:<br>
          <textarea rows="7" name="Kommentar" cols="56"></textarea>
          <br>
          <br>
          <input type="submit" value="Abschicken" name="ignore_me_send" class="bodytext_bold">
          <input type="reset" value="Zur&uuml;cksetzen" name="B2" class="bodytext_bold">
      </form>
    </td>
  </tr>
  <tr>
    <td colspan="2" height="12" bgcolor="#FFFFFF"><img src="images/dummy.gif" width="1" height="1"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td colspan="2" height="1"><img src="images/dummy.gif" width="1" height="1"></td>
  </tr>
  <tr>
    <td colspan="2" height="6" bgcolor="#FFFFFF"><img src="images/dummy6.gif" width="6" height="6"></td>
  </tr>
  <tr>
    <td bgcolor="#FFFFFF" height="24"><img src="images/dummy.gif" width="1" height="1">
      <div align="center"></div>
    </td>
    <td bgcolor="#FFFFFF" class="fusszeile" height="24">
      <div align="center">Alle von Euch gemachten Angaben werden vertraulich behandelt
        und dienen nur zu Informationszwecken f&uuml;r und durch den 1. VW &amp;
        Audi Club M&uuml;nster e. V..<br>
        Sie werden weder an Dritte weitergegeben oder f&uuml;r deren Zwecke genutzt.</div>
    </td>
  </tr>
  <tr>
    <td colspan="2" bgcolor="#FFFFFF" height="12"><img src="images/dummy12.gif" width="12" height="12"></td>
  </tr>
</table>

</body>
</html>