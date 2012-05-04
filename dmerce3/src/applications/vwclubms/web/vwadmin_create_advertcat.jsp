<%@ include file="/init.jsp" %>

<html>
<body>

<table>

<tr>
<td>Neuer Eintrag</td>
</tr>

<qform:form id="vwadmin_advertcats">
<tr>
<td>Name</td>
<td><qform:text name="name"/><qform:errormessage name="name"/></td>
</tr>
<tr>
<td>Beschreibung</td>
<td><qform:text name="description"/><qform:errormessage name="description"/></td>
</tr>
<tr>
<td><qform:button type="submit" text="OK"/><qform:button type="submit" text="Cancel"/></td>
</tr>
</qform:form>

</table>

<p>
<a href="vwadmin_list_advertcats.jsp">Liste der Kategorien f&uuml;r Kleinanzeigen</a>
</p>

</body>
</html>