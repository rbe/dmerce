<%@ include file="header.jsp" %>

<h1>SQL-Test</h1>

<q:message/>

<qsql:prepare id="query1">
   SELECT * FROM vdate ORDER BY id
</qsql:prepare>

Request-Variable test="<q:var name="test"/>"<p/>

<table cellpadding=2 cellspacing=3 border=1>
<qsql:execute id="query1">
    <tr><td colspan="10"><qsql:rowcount/> Einträge gefunden</td></tr>
    <tr>
    	<th>&nbsp;</th>
        <th>ID</th>
        <th>Spalte 1</th>
        <th>Spalte 2</th>
        <th>Spalte 3</th>
        <th>Spalte 4</th>
        <th>&nbsp;</th>
    </tr>
    <qsql:row>
    <tr class="tdbg<qsql:alternate/>">
    	<td><qsql:number/></td>
		<td><qsql:field name="id"/></td>
    	<td><qsql:datefield name="datetime"/></td>
		<td><qsql:datefield name="date" format="dd.MM.yyyy"/></td>
		<td><qsql:datefield name="time" format="HH:mm:ss' Uhr'"/></td>
		<td><qsql:datefield name="timestamp" format="'Timestamp: 'yyyyMMddHHmmss"/></td>
		<td><qsql:delete table="vdate" key="id" template="list.jsp"> [ del ] </qsql:delete></td>
    </tr>
    </qsql:row>
</qsql:execute>
</table>
<br><br>
<a href="workflow?qWorkflow=register">[ Neuer Eintrag ]</a>

<%@ include file="footer.jsp" %>