<%@ include file="header.jsp" %>

<h1>SQL-Test</h1>

<qsql:prepare id="query1" sql-query-id="query1">
   <parameter varname="id"/>
   <parameter type="name"/>
</qsql:prepare>

<qsql:prepare id="query1" query="select * from addresses where id=? and hello=?">
   <parameter varname="id"></parameter>
   <parameter type="string"><q:var name="id"/></parameter>
</qsql:prepare>

<qsql:prepare id="query1">
   select * from addresses_view where id=<q:var name="id"/> and hello='<q:var name="hello"/>' and valid=1
</qsql:prepare>

<table>
<qsql:execute id="query1" pagesize=30>
    <tr><td><qsql:rowcount/> Einträge gefunden</td></tr>
    <qsql:row>
       <tr>
         <td><qsql:field name="name"/></td>
	 <td><qsql:field name="city"/></td>
       </tr>
    </qsql:row>
</qsql:execute>
</table>

<qsql:prev id="query1"/> | <qsql:next id="query1"/>

<%@ include file="footer.jsp" %>
