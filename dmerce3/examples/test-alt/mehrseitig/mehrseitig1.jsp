<%@ include file="header.jsp" %>

<h1>Seite1</h1>

<q:message/>

<qform:form id="form1">
<table>
<tr><td>Name</td><td><qform:text name="name"/></td></tr>
<tr><td>Vorname</td><td><qform:text name="vorname"/></td></tr>
<tr><td>Email</td><td><qform:textarea name="email" rows="5" cols="28"/></td></tr>
</table>
<qform:button type="submit" text="Ok"/>  <qform:button type="submit" text="Cancel"/>
</qform:form>

<%@ include file="footer.jsp" %>

