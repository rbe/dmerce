<%@ include file="header.jsp" %>

<h1>Zweispaltige Ausgabe</h1>
<p>Hier erfolgt eine zweispaltige Ausgabe der Ergebnisse. Die Liste wurde
zeilenweise gefüllt.</p>

<qsql:prepare id="query1">
   SELECT * FROM vkunde ORDER BY kundennr
</qsql:prepare>

<qsql:execute id="query1">
<p><qsql:rowcount/> Einträge in der Datenbank</p>
<table cellpadding=2 cellspacing=3 border=1>
    <qsql:row>
    <tr class="tdbg<qsql:alternate/>">
    	<td style="font-size: 10px; width: 200px">
    		<qsql:number/>.
    		<strong>
    		<a href="mailto:<qsql:field name="email"/>">
    			<qsql:field name="vorname"/> <qsql:field name="name"/>
    		</a>
    		</strong>
    		(Kd-Nr. <qsql:field name="kundennr"/>)<br/>
    		<qsql:field name="strasse"/><br/>
    		<qsql:field name="plz"/> <qsql:field name="ort"/><br/>
    		<qsql:delete table="vkunde" key="kundennr" template="columnlist.jsp"> [ del ] </qsql:delete>
    		<qsql:edit workflow="register" key="kundennr"> [ edit ] </qsql:edit>
    	</td>
    	
    	<qsql:nextrow/>
    	
    	<td style="font-size: 10px; width: 200px">
    		<qsql:number/>.
    		<strong>
    		<a href="mailto:<qsql:field name="email"/>">
    			<qsql:field name="vorname"/> <qsql:field name="name"/>
    		</a>
    		</strong>
    		(Kd-Nr. <qsql:field name="kundennr"/>)<br/>
    		<qsql:field name="strasse"/><br/>
    		<qsql:field name="plz"/> <qsql:field name="ort"/><br/>
    		<qsql:delete table="vkunde" key="kundennr" template="columnlist.jsp"> [ del ] </qsql:delete>
    		<qsql:edit workflow="register" key="kundennr"> [ edit ] </qsql:edit>
    	</td>
    	
    </tr>
    </qsql:row>
</qsql:execute>
</table>
<br><br>
<a href="workflow.do?qWorkflow=register">[ Neuer Eintrag ]</a>

<%@ include file="footer.jsp" %>