<%@ include file="header.jsp" %>

<h1>Einfache SQL-Abfrage</h1>

<qsql:prepare id="query1">
   SELECT * FROM vkunde ORDER BY kundennr
</qsql:prepare>

<table cellpadding=2 cellspacing=3 border=1>
<qsql:execute id="query1">
    <tr><td colspan="8"><qsql:rowcount/> Eintr‰ge gefunden</td></tr>
    <tr>
    	<th>&nbsp;</th>
        <th>Kunden-Nr.</th>
        <th>Name</th>
        <th>Vorname</th>
        <th>Email</th>
        <th>Straﬂe</th>
        <th>PLZ</th>
        <th>Ort</th>
    </tr>
    <qsql:row>
    <tr class="tdbg<qsql:alternate/>">
    	<td><qsql:number/></td>
		<td><qsql:field name="kundennr"/></td>
    	<td><qsql:field name="name"/></td>
		<td><qsql:field name="vorname"/></td>
		<td><qsql:field name="email"/></td>
		<td><qsql:field name="strasse"/></td>
		<td><qsql:field name="plz"/></td>
		<td><qsql:field name="ort"/></td>
    </tr>
    </qsql:row>
</qsql:execute>
</table>
<br><br>
<%@ include file="footer.jsp" %>