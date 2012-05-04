<%@ include file="/init.jsp" %>

<qsql:prepare id="guestbook">
      SELECT createddatetime, realname, email, url, text FROM t_guestbook ORDER BY createddatetime DESC
</qsql:prepare>

<html>
<head>
<link rel="stylesheet" href="<q:res path="/css/vw.css"/>" type="text/css">
</head>
<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#FFFFFF">
    <td width="6" height="21"><img src="images/head_ecke_w.gif"></td>
    <td class="bodytext_bold_ital12" colspan="3">G&auml;stebuch - Ihr m&ouml;chte uns und dem Rest der Welt etwas mitteilen?</td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="3" height="12"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td colspan="3" height="1"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="3" height="6"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="1"><img src="<q:res path="/images/dummy.gif"/>"></td>
    <td colspan="2" class="bodytext">Vorab: Dies ist ein G&auml;stebuch.
      F&uuml;r Fragen zum Club nutzt bitte das <a href="<q:res path="/vwcontact.jsp"/>">Kontaktformular</a>,
      f&uuml;r Inserate die <a href="<q:res path="/vwlistadvertcats.jsp"/>">Kleinanzeigen</a>,
      f&uuml;r Publikmachung Eurer Website die <a href="<q:res path="/vwlistlinkscats.jsp"/>">Linkliste</a>
      sowie f&uuml;r Veranstaltungen dem <a href="<q:res path="/vwlistappointments.jsp"/>">Terminkalender</a>.
      Wir freuen uns &uuml;ber jede Nachricht von Euch.</td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="3" height="12"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td colspan="3" height="1"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="3" height="6"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="1"><img src="<q:res path="/images/dummy.gif"/>"></td>
    <td colspan="2" class="bodytext_bold">
      <div align="center"><q:workflowlink id="1cs_guestbook" restart="true">Neue
        Nachricht hinterlassen</q:workflowlink></div>
    </td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="12" colspan="3"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>
  <tr class="bodytext_bold_w">
    <td height="21" bgcolor="#CCCCCC" background="<q:res path="/images/bg_hbl.gif"/>"><img src="<q:res path="/images/dummy.gif"/>"></td>
    <td width="250" bgcolor="#CCCCCC" background="<q:res path="/images/bg_hbl.gif"/>">Absender</td>
    <td width="510" bgcolor="#CCCCCC" background="<q:res path="/images/bg_hbl.gif"/>">Eintrag</td>
  </tr>
  <tr class="bodytext_bold_w">
    <td colspan="3" height="6" bgcolor="#FFFFFF"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>
  <qsql:execute id="guestbook">
  <qsql:row>
  <tr bgcolor="#FFFFFF">
    <td><img src="images/dummy.gif"></td>
    <td class="bodytext_bold" align="left" valign="top" width="250"><qsql:field name="realname"/> <qsql:fieldnotempty name="email">(<a href="mailto:<qsql:field name="email"/>"><qsql:field name="email"/></a>)</qsql:fieldnotempty><br>
      <qsql:fieldnotempty name="url"><a target="_new" href="http://<qsql:field name="url"/>"><qsql:field name="url"/></a>
      </qsql:fieldnotempty></td>
    <td class="bodytext" align="left" valign="top" width="510">
     <qsql:field name="createddatetime" format="dd.MM.yyyy"/><br>
     <qsql:field name="text"/></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="12" colspan="3"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td height="1" colspan="3"><img src="<q:res path="/images/bg_hbl.gif"/>"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="6" colspan="3"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>

  </qsql:row>
  </qsql:execute>

  <tr bgcolor="#FFFFFF">
    <td height="1"><img src="<q:res path="/images/dummy.gif"/>"></td>
    <td colspan="2" class="bodytext_bold"><div align="center"><q:workflowlink id="1cs_guestbook" restart="true">Neue Nachricht hinterlassen</q:workflowlink></div></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="12" colspan="3"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td height="1" colspan="3"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="6" colspan="3"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td width="6" height="21"><img src="<q:res path="/images/dummy.gif"/>"></td>
    <td colspan="2" class="fusszeile"><img src="images/dummy.gif">powered by <a target="_new" href="http://www.1ci.com">1[wan]Ci dmerce&reg;</a></td>
  </tr>
</table>
</body>
</html>