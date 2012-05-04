<%@ include file="/init.jsp" %>

<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<link rel="stylesheet" href="../style.css" type="text/css">
<title>1[NCC] - Network Coordination Center - 1[wan]Ci - www.1ci.com</title>
</head>

<body onLoad="document.myForm.qLogin.focus()" bgcolor="#FFFFFF" topmargin="0" leftmargin="0" >

<center>

<form method="post" action="/1Ci/dmerce" name="myForm">
<input type="hidden" name="qTrigger" value="Login.User.Check">
<input type="hidden" name="qLoginTable" value="Agent">
<input type="hidden" name="qs" value="(exec SAM.Session.CreateNew cexe)">
<input type="hidden" name="qTemplate" value="Navigation,redirect">

<br>

<tr align="left">
   <td><img border="0" src="<q:res path="/pic/1ci_ncc.gif"/>"></td>
</tr>

<br>
<br>
<br>

<table cellspacing="0" cellpadding="3" border="0">
<tr>
   <td><img border="0" src="<q:res path="/pic/but_user.gif"/>" alt="LOGIN" width="78" height="17"></td>
   <td align="right"><input type="text" name="qLogin"></td>
</tr>

<tr>
   <td><img border="0" src="<q:res path="/pic/but_password.gif"/>" alt="PASSWORD" width="78" height="17"></td>
   <td align="right"><input type="password" name="qPasswd"></td>
</tr>


<tr>
     <!--
     <td>
     (if (form SSL) == 0)
     <a href="https://(var SERVER_NAME)/1Ci/dmerce?qs=(var qs)&qTemplate=(var qTemplate)&SSL=1"><img border="0" src="../pic/ssl_off.gif"></a>
     (else)
     <a href="http://(var SERVER_NAME)/1Ci/dmerce?qs=(var qs)&qTemplate=(var qTemplate)&SSL=0"><img border="0" src="../pic/ssl_on.gif"></a>
     (endif)
     </td>
     -->
     <td align="right"><input class="submit" type="submit" name="qButton" value="Go"></td>
</tr>


</table>

</form>

</center>

</body>
</html>