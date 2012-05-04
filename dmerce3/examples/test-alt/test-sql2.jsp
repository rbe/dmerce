<%@ include file="header.jsp" %>

<h1>SQL-Test 2</h1>

<q:message/>

<qsql:prepare id="query1">
   select * from dmerce_kunde
</qsql:prepare>

<table cellpadding=3 cellspacing=5>
<qsql:execute id="query1">
    <tr><td colspan="5"><qsql:rowcount/> Einträge gefunden</td></tr>
    <qsql:row>
       <tr>
         <td><qsql:field name="dk_name"/></td>
	 <td><qsql:field name="dk_vorname"/></td>
	 <td><qsql:field name="dk_email"/></td>
       </tr>
    </qsql:row>
</qsql:execute>
</table>

<%@ include file="footer.jsp" %>
