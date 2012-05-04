<%@ include file="../init.jsp" %>

<html>
<head>
</head>

<body>

<h1>Login</h1>

<form action="j_security_check" method="post">

<table>
	
	<tr>
	<td>Username:</td>
	<td><input type="text" name="j_username" size="20" maxlength="20"/></td>
	</tr>
	
	<tr>
	<td>Password:</td>
	<td><input type="password" name="j_password" size="20" maxlength="20"/></td>
	</tr>
	
	<tr>
	<td colspan="2"><input type="submit" value="Login" style="width: 60px" /></td>
	</tr>
	
</table>

</form>

</body>
</html>
