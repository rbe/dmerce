<%@ include file="/init.jsp" %>


<html>
<head>
<title>test</title>
<meta name="author" content="hobbes">
<meta name="generator" content="Ulli Meybohms HTML EDITOR">
</head>
<body text="#000000" bgcolor="#FFFFFF" link="#FF0000" alink="#FF0000" vlink="#FF0000">

<table>

<qauth:not-logged-in>

	<form method="post" action="<q:res path="/login.do"/>">

         <tr>
         <td>Email<input type="text" name="qUsername" size="10"></td>
         </tr>

         <tr>
	<td>Password<input type="password" name="qPassword" size="10"></td>
         </tr>

         <tr>
	<td><input type="submit" name="Login" value="OK"></td>
         </tr>

</qauth:not-logged-in>

<qauth:logged-in realm="user">

	<tr>
         <td>&nbsp;</td>
         </tr>

	<tr>
         <td><a href="<q:res path="/logout.do"/>">Logout</a></td>
         </tr>

</qauth:logged-in>


</table>


</body>
</html>