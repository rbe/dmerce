<%@ include file="/init.jsp" %>
	
<html>
<head>
<title></title>
</head>
<body bgcolor="#FFFFFF" callpadding="0" cellspacing="0">

<qsql:prepare id="q1">
	SELECT productid, resellerid, name, installationcost, price FROM v_products WHERE resellerid = <q:var name="resellerid"/>
</qsql:prepare>

<table width="97%" border="1" bordercolor="#DEDFDE" cellspacing="0" cellpadding="0" align="center">

       <tr>

       <td valign="top">

<table width="60%" border="0" align="center" cellspacing="5" cellpadding="5"
	style="font-family:Verdana,Arial,Helvetica,Helv,sans-serif; font-size: 9pt;">

       <tr>
       <td colspan="2"><img src="<q:res path="/pic/dummy.gif"/>" width="1" height="50" border="0"></td>
       </tr>
       
       <tr>
       <td bgcolor="#909090" colspan="2">&nbsp;<b><font size="+1">Tarifwahl:</font></b></td>
       </tr>

       <tr>
       <td bgcolor="#CFCFCF" colspan="2">&nbsp;Bitte w&auml;hlen Sie hier Ihren Wunschtarif.</td>
       </tr>

       <tr>
       <td colspan="2"><img src="<q:res path="/pic/dummy.gif"/>" width="1" height="5" border="0"></td>
       </tr>

<qsql:execute id="q1">
<qsql:row>
	   <tr>
       <td bgcolor="#CFCFCF" colspan="2"><a href="workflow.do?qWorkflow=creg&resellerid=<q:var name="resellerid"/>&productid=<qsql:field name="productid"/>&restart=true"><qsql:field name="name"/> - <qsql:field name="price"/> &#8364; (zzgl. einmalig <qsql:field name="installationcost"/> &#8364; Einrichtungsgeb&uuml;hr)</a></td>
       </tr>
</qsql:row>
</qsql:execute>

       <tr>
       <td colspan="2"><img src="<q:res path="/pic/dummy.gif"/>" width="1" height="5" border="0"></td>
       </tr>

       <tr>
       <td colspan="2" bgcolor="#EFEFEF">Auf der folgenden Seite geben Sie uns bitte Ihre pers&ouml;nlichen Daten bekannt</td>
       </tr>


       <tr>
       <td colspan="2"><img src="<q:res path="/pic/dummy.gif"/>" width="1" height="35" border="0"></td>
       </tr>
	
</table>

	   </td>
       </tr>
       
	
</table>
</body>

</html>
