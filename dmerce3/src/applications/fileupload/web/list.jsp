<%@ include file="header.jsp" %>

<h1>Einfache SQL-Abfrage</h1>

<qsql:prepare id="query1">
   SELECT * FROM vkunde ORDER BY kundennr
</qsql:prepare>

<table cellpadding=2 cellspacing=3 border=1>
<qsql:execute id="query1">
    <tr><td colspan="9"><qsql:rowcount/> Einträge gefunden</td></tr>
    <tr>
    	<th>&nbsp;</th>
        <th>Kunden-Nr.</th>
        <th>Name</th>
        <th>Vorname</th>
        <th>Email</th>
        <th colspan="2">Foto</th>
        <th>&nbsp;</th>
        <th>&nbsp;</th>
    </tr>
    <qsql:row>
	    <tr>
	    	<td><qsql:number/></td>
			<td><qsql:field name="kundennr"/></td>
	    	<td><qsql:field name="name"/></td>
			<td><qsql:field name="vorname"/></td>
			<td><qsql:field name="email"/></td>
			<td>
				<qsql:fieldnotempty name="filename_foto" file="true">
					<a href="<q:uploadres name="filename_foto"/>">
						<qsql:field name="filename_foto" file="true"/>
					</a>
				</qsql:fieldnotempty>
				<qsql:fieldempty name="filename_foto" file="true">
					-
				</qsql:fieldempty>
			</td>
			<td>
				<img src="<q:uploadres name="filename_foto" emptypath="images/noimg.jpg"/>" style="width:50px; height: 30px"/>
			</td>
			<td><qsql:delete table="vkunde" key="kundennr" template="list.jsp"> [ del ] </qsql:delete></td>
			<td><qsql:edit workflow="register" key="kundennr"> [ edit ] </qsql:edit></td> 
	    </tr>
    </qsql:row>
</qsql:execute>
</table>
<br><br>
<a href="workflow.do?qWorkflow=register">[ Neuer Eintrag ]</a>

<%@ include file="footer.jsp" %>