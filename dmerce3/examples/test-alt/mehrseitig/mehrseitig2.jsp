<%@ include file="header.jsp" %>

<h1>Seite1</h1>

<q:message/>

<qform:form id="form1">
<table>
<tr><td>Strasse</td><td><qform:text name="strasse"/></td></tr>
<tr><td>PLZ</td><td><qform:text name="plz"/></td></tr>
<tr><td>Ort</td><td><qform:text name="ort"/></td></tr>
<tr><td>Telefon</td><td><qform:text name="telefon"/></td></tr>
</table>
<qform:button type="submit" text="Ok"/>  <qform:button type="submit" text="Cancel"/>
</qform:form>

<%@ include file="footer.jsp" %>

