<%@ include file="header.jsp" %>

<h1>Test-Form2</h1>

<qform:form id="formular">
<table>
<tr><td>Name</td><td><qform:text name="name"/></td></tr>
<tr><td>Adresse</td><td><qform:text name="address"/></td></tr>
<tr><td>Newsletter</td><td><qform:textarea name="Vorschläge" cols="28" rows="5"/></td></tr>
</table>
<qform:button text="OK"/> <qform:button text="Übernehmen"/> <qform:button text="Abbrechen"/>
</qform:form>

<%@ include file="footer.jsp" %>

