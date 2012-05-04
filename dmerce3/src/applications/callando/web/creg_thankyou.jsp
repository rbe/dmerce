<%@ include file="/init.jsp" %>
	
<html>
<head>
<title></title>
</head>
<body bgcolor="#FFFFFF" callpadding="0" cellspacing="0">

<qsql:prepare id="q1">
	SELECT text1 FROM t_reseller WHERE id = <q:contextvar name="resellerid"/>
</qsql:prepare>

<table width="97%" border="1" bordercolor="#DEDFDE" cellspacing="0" cellpadding="0" align="center">

       <tr>
       <td valign="top">

<table width="100%" border="0" cellspacing="2" cellpadding="0" style="font-family:Verdana,Arial,Helvetica,Helv,sans-serif; font-size: 8pt;">

       <tr>
       <td bgcolor="#909090" colspan="2">&nbsp;<b>Anmeldebest&auml;tigung:</b></td>
       </tr>

       <tr>
       <td colspan="2"><img src="<q:res path="/pic/dummy.gif"/>" width="1" height="1" border="0"></td>
       </tr>

<qsql:execute id="q1">
<qsql:row>

       <tr>
       <td colspan="2" bgcolor="#CFCFCF"><qsql:field name="text1"/></td>
       </tr>

</qsql:row>
</qsql:execute>

</table>
</td>
</tr>

</table>

</body>

</html>
