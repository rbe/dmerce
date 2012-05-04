<%@ include file="header.jsp" %>

<h1>Formular mit einer CheckBoxListe - Seite 1</h1>

<q:message/>

<qform:form id="listboxform">
	<table>
		<tr><td valign="top">Name</td><td><qform:text name="name"/><qform:errormessage name="name"/></td></tr>
		<tr><td valign="top">Vorname</td><td><qform:text name="vorname"/><qform:errormessage name="vorname"/></td></tr>
		<tr><td valign="top">Email</td><td><qform:text name="email"/><qform:errormessage name="email"/></td></tr>
		<tr><td valign="top">Strasse</td><td><qform:text name="strasse"/></td></tr>
		<tr><td valign="top">PLZ</td><td><qform:text name="plz"/></td></tr>
		<tr><td valign="top">Ort</td><td><qform:text name="ort"/></td></tr>
		<tr><td valign="top">Hobby</td><td><qform:list type="list" name="hobby" multiple="true"/></td></tr>
	</table>
<qform:button type="submit" text="Ok"/>  <qform:button type="submit" text="Cancel"/>
</qform:form>

<%@ include file="footer.jsp" %>

