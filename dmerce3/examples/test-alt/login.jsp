<%@ include file="header.jsp" %>

<q:headline name="Login"/>

<qform:form id="login">
<q:contextvar name="fehler"/>
<table>
<tr><td>Name</td><td><qform:text name="name"/></td></tr>
<tr><td>Passwort</td><td><qform:text name="passwort"/></td></tr>
</table>
<qform:button text="ok"/><qform:button text="cancel"/>
</qform:form>

<%@ include file="footer.jsp" %>

