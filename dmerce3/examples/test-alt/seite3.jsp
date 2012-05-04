<%@ include file="header.jsp" %>

<h1>Seite 3 (register-Workflow)</h1>

<q:message/>

<qsql:prepare id="query1">
   SELECT * FROM vkunde ORDER BY kundennr
</qsql:prepare>

<table cellpadding=2 cellspacing=3 border=1>
<qsql:execute id="query1">
    <tr><td colspan="10"><qsql:rowcount/> Einträge gefunden</td></tr>
    <tr>
    	<th>&nbsp;</th>
        <th>Kunden-Nr.</th>
        <th>Name</th>
        <th>Vorname</th>
        <th>Email</th>
        <th>Straße</th>
        <th>PLZ</th>
        <th>Ort</th>
        <th>&nbsp;</th>
        <th>&nbsp;</th>
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
		<td><qsql:delete table="vkunde" key="kundennr" template="seite3.jsp"> [ del ] </qsql:delete></td>
		<td><qsql:delete table="vkunde" key="kundennr"> [ del ] </qsql:delete></td>
		<td><qsql:edit workflow="register2" key="kundennr"> [ edit (register2) ] </qsql:edit></td> 
		<td><qsql:edit workflow="register" key="kundennr"> [ edit (register) ] </qsql:edit></td> 
		<td><qsql:edit workflow="register-auto" key="kundennr"> [ edit (reg-auto) ] </qsql:edit></td> 
    </tr>
    </qsql:row>
</qsql:execute>
</table>
<br><br>
<q:workflowlink id="register">[ Neuer Eintrag ]</q:workflowlink>

<%@ include file="footer.jsp" %>