<%@ include file="../header.jsp" %>

<h1>Seite1</h1>

<form action="j_security_check" method="post">
<table>
	<tr>
		<td>Benutzerkennung:</td>
		<td><input type="text" name="j_username" size="20" maxlength="255"/></td>
	</tr>
	<tr>
		<td>Passwort:</td>
		<td><input type="password" name="j_password" size="20" maxlength="255"/></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" value="Login" style="width: 60px" /></td>
	</tr>
</table>
</form>

<%@ include file="../footer.jsp" %>

