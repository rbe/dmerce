<%@ include file="/init.jsp" %>

<html>
<head>
<link rel="stylesheet" href="<q:res path="/css/vw.css"/>" type="text/css">
</head>
<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<qsql:prepare id="linkscat">SELECT name, entries FROM v_linkcategories WHERE id = <q:var name="linkcategoryid"/>
</qsql:prepare>

<qsql:prepare id="links">SELECT createddatetime, realname, email, name, description, url, hits FROM v_links WHERE linkcategoriesid = <q:var name="linkcategoryid"/>
</qsql:prepare>

<table width="760" border="0" cellspacing="0" cellpadding="0">

<qsql:execute id="linkscat">
<qsql:row>

  <tr bgcolor="#FFFFFF">
    <td width="6" height="21"><img src="images/head_ecke_w.gif"></td>
    <td class="bodytext_bold_ital12" colspan="3"><qsql:field name="name"/> (<qsql:field name="entries" precision="0"/>)</td>
  </tr>

</qsql:row>
</qsql:execute>

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
    <td colspan="2" class="bodytext_bold"><div align="center"><a href="workflow.do?qWorkflow=1cs_links&linkcategoryid=<q:var name="linkcategoryid"/>&restart">Neuen Link eintragen</a></div></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="12" colspan="3"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>
  <tr class="bodytext_bold_w">
    <td height="21" bgcolor="#CCCCCC" background="<q:res path="/images/bg_hbl.gif"/>"><img src="<q:res path="/images/dummy.gif"/>"></td>
    <td width="250" bgcolor="#CCCCCC" background="<q:res path="/images/bg_hbl.gif"/>">Link/Website</td>
    <td width="510" bgcolor="#CCCCCC" background="<q:res path="/images/bg_hbl.gif"/>">Beschreibung</td>
  </tr>

  <qsql:execute id="links">
  <qsql:row>

  <tr bgcolor="#FFFFFF">
    <td colspan="3" height="6"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td><img src="images/dummy.gif"></td>
    <td class="bodytext_bold" align="left" valign="top" width="250"><qsql:field name="name"/><br><a target="_new" href="http://<qsql:field name="url"/>"><qsql:field name="url"/></a></td>
    <td class="bodytext" align="left" valign="top" width="510"> <qsql:field name="createddatetime" format="dd.MM.yyyy"/><qsql:fieldnotempty name="realname"> von <qsql:field name="realname"/></qsql:fieldnotempty> <qsql:fieldnotempty name="email">(<a href="mailto"><qsql:field name="email"/></a>)</qsql:fieldnotempty><br><qsql:field name="description"/></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="3" height="12"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td colspan="3" height="1"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>

  </qsql:row>
  </qsql:execute>

  <tr bgcolor="#FFFFFF">
    <td height="6" colspan="3"><img src="<q:res path="/images/dummy.gif"/>"></td>
  </tr>

  <tr bgcolor="#FFFFFF">
    <td height="1"><img src="<q:res path="/images/dummy.gif"/>"></td>
    <td colspan="2" class="bodytext_bold"><div align="center"><a href="workflow.do?qWorkflow=1cs_links&linkcategoryid=<q:var name="linkcategoryid"/>&restart">Neuen Link eintragen</a></div></td>
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
