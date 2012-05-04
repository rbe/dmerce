<%@ include file="/init.jsp" %>

<html>
<head>
<link rel="stylesheet" href="<q:res path="/css/vw.css"/>" type="text/css">
</head>
<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<!-- Alle Kategorien fuer Links zu anderen -->
<qsql:prepare id="advertcats">
      SELECT id, name, entries FROM v_advertcategories
</qsql:prepare>

<!-- Informationen ueber aktuelle Kateogorie -->
<qsql:prepare id="advertcatname">
      SELECT name, entries FROM v_advertcategories WHERE id = <q:var name="advertcategoryid"/>
</qsql:prepare>

<!-- Eintraege in aktueller Kategorie -->
<qsql:prepare id="adverts">
      SELECT createddatetime, realname, email, url, description, text, foto_original, foto_server FROM v_adverts WHERE advertcategoriesid = <q:var name="advertcategoryid"/>
</qsql:prepare>

<table width="760" height="480" border="0" cellspacing="0" cellpadding="0">

  <qsql:execute id="advertcatname">
  <qsql:row>

  <tr bgcolor="#FFFFFF">
    <td width="6" height="21"><img src="images/head_ecke_w.gif" width="6" height="21"></td>
    <td class="bodytext_bold_ital12" colspan="4" bgcolor="#FFFFFF">Kleinanzeigen: <qsql:field name="name"/> (<qsql:field name="entries" precision="0"/>)</td>
  </tr>

  </qsql:row>
  </qsql:execute>

  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="12"><img src="images/dummy12.gif" width="1" height="12"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td colspan="4" height="1"><img src="images/bg_dbl.gif" width="1" height="1"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="6"><img src="images/dummy6.gif" width="1" height="6"></td>
  </tr>
  <tr>
    <td width="6" height="12" bgcolor="#FFFFFF"><img src="images/dummy.gif" width="1" height="1"></td>
    <td colspan="3" class="bodytext" height="12" bgcolor="#FFFFFF">Wechseln zu:
      <qsql:execute id="advertcats"> <qsql:row> <a href="vwshowadvertcat.jsp?advertcategoryid=<qsql:field name="id"/>"><qsql:field name="name"/>
      (<qsql:field name="entries" precision="0"/>)</a> / </qsql:row> </qsql:execute> </td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="6"><img src="images/dummy6.gif" width="6" height="6"></td>
  </tr>
  <tr bgcolor="#AAB5BE">
    <td colspan="4" height="1"><img src="images/bg_dbl.gif" width="1" height="1"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="6"><img src="images/dummy6.gif" width="1" height="6"></td>
  </tr>
  <tr>
    <td height="12" width="6" bgcolor="#FFFFFF"><img src="images/dummy.gif" width="1" height="1"></td>
    <td colspan="3" class="bodytext_bold" bgcolor="#FFFFFF">
      <div align="center"><a href="workflow.do?qWorkflow=1cs_advert&advertcategoryid=<q:var name="advertcategoryid"/>&restart">Neuen
        Artikel einstellen</a></div>
    </td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="12"><img src="images/dummy.gif" width="1" height="1"></td>
  </tr>

  <qsql:execute id="adverts">
  <qsql:row>

  <tr class="bodytext_bold_white">
    <td height="21" width="6" bgcolor="#CCCCCC" background="images/bg_hbl.gif"><img src="images/dummy.gif"></td>
    <td colspan="3" class="bodytext_bold_w" bgcolor="#CCCCCC" background="images/bg_hbl.gif" height="21"><qsql:field name="description"/> vom <qsql:field name="createddatetime" format="dd.MM.yyyy HH:mm"/> Uhr</td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="6"><img src="images/dummy.gif"></td>
  </tr>
  <tr>
    <td width="6" height="100%" bgcolor="#FFFFFF"><img src="images/dummy.gif"></td>
    <td width="300" class="bodytext_bold" align="center" bgcolor="#CCCCCC" background="images/bg_hbl.gif"><qsql:fieldnotempty name="foto" file="true"><img width="300" src="<q:uploadres name="foto"/>"></qsql:fieldnotempty><qsql:fieldempty name="foto" file="true">kein Bild</qsql:fieldempty></td>
    <td width="6" bgcolor="#FFFFFF"><img src="images/dummy.gif"></td>
    <td width="448" valign="top" class="bodytext" bgcolor="#FFFFFF">
      <p><qsql:field name="text"/></p>
    <p class="bodytext_bold_ital"><qsql:field name="realname"/><br><a target="_new" href="http://<qsql:field name="url"/>"><qsql:field name="url"/></a><br><a href="mailto:<qsql:field name="email"/>"><qsql:field name="email"/></a></p>
    </td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="12"><img src="images/dummy.gif"></td>
  </tr>

  </qsql:row>
  </qsql:execute>

  <!-- RB: platzhalter -->
  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="100%">&nbsp;</td>
  </tr>

  <tr>
    <td colspan="4" height="1" bgcolor="#AAB5BE"><img src="images/bg_dbl.gif" width="1" height="1"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td colspan="4" height="6"><img src="images/dummy6.gif" width="1" height="6"></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td width="6" height="21"><img src="images/dummy6.gif" width="6" height="6"></td>
    <td colspan="3" class="fusszeile">powered by <a target="_new" href="http://www.1ci.com">1[wan]Ci dmerce&reg;</a></td>
  </tr>
</table>

</body>
</html>