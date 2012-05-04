<%@ include file="header.jsp" %>

<h1>Checkbox Test</h1>

<q:message/>

<qform:form id="form2">
<table>
<tr><td>Name</td><td><qform:text name="name"/></td></tr>
<tr><td>Vorname</td><td><qform:text name="vorname"/></td></tr>
<tr><td>Email</td><td><qform:text name="email"/></td></tr>
<tr><td>Strasse</td><td><qform:text name="strasse"/></td></tr>
<tr><td>PLZ</td><td><qform:text name="plz"/></td></tr>
<tr><td>Ort</td><td><qform:text name="ort"/></td></tr>
<tr><td>Telefon</td><td><qform:text name="telefon"/></td></tr>

<tr><td colspan=2>
	<qform:groupfield name="newsletter"/><br/>
	<qform:groupfield name="heiraten"/><br/>
	<qform:groupfield name="waschmaschine"/><br/>
</td>

</table>
<qform:button type="submit" text="Ok"/>  <qform:button type="reset" text="Cancel"/>
</qform:form>

<%@ include file="footer.jsp" %>