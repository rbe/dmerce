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
    		<qsql:fieldempty name="strasse" inverted="true"><qsql:field name="strasse"/><br/></qsql:fieldempty>
    		<qsql:fieldempty name="plz" inverted="true"><qsql:field name="plz"/> </qsql:fieldempty><qsql:fieldempty name="ort" inverted="true"><qsql:field name="ort"/><br/></qsql:fieldempty>
    		<qsql:delete table="vkunde" key="kundennr" template="columnlist2.jsp"> [ del ] </qsql:delete>
    		<qsql:edit workflow="register" key="kundennr"> [ edit ] </qsql:edit>
    	</td>
    	
    	<qsql:nextrow/>
    	<td style="font-size: 10px; width: 200px">
    	  <qsql:rownotempty>
    		<qsql:number/>.
    		<strong>
    		<a href="mailto:<qsql:field name="email"/>">
    			<qsql:field name="vorname"/> <qsql:field name="name"/>
    		</a>
    		</strong>
    		(Kd-Nr. <qsql:field name="kundennr"/>)<br/>
    		<qsql:fieldempty name="strasse" inverted="true"><qsql:field name="strasse"/><br/></qsql:fieldempty>
    		<qsql:fieldempty name="plz" inverted="true"><qsql:field name="plz"/> </qsql:fieldempty><qsql:fieldempty name="ort" inverted="true"><qsql:field name="ort"/><br/></qsql:fieldempty>
    		<qsql:delete table="vkunde" key="kundennr" template="columnlist2.jsp"> [ del ] </qsql:delete>
    		<qsql:edit workflow="register" key="kundennr"> [ edit ] </qsql:edit>
    	  </qsql:rownotempty>
    	  &nbsp;
    	</td>
    </tr>
    </qsql:row>
</qsql:execute>
</table>
<br><br>
<q:workflowlink id="register" restart="true">Neuer Eintrag</q:workflowlink>
<%@ include file="footer.jsp" %>