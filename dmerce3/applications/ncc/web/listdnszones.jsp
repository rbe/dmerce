<%@ include file="/init.jsp" %>

<q:usetemplate path="layout.jsp">
<q:setbrick name="menu" path="menu.jsp"/>
<q:setbrick name="content">

<qsql:prepare id="zones">
      SELECT * FROM t_dnszones
</qsql:prepare>

<qsql:execute id="zones">

<p>
<qsql:rowcount/> entries found
</p>

<table>
<tr>
<td>ID</td>
<td>Name</td>
<td>Action</td>
</tr>

<qsql:row>

<tr>
<td><qsql:field name="id"/></td>
<td><qsql:field name="name"/></td>
<td><qsql:edit workflow="1ncc_create_dnszone" key="id">edit</qsql:edit> <qsql:delete table="t_customer" key="ID" template="listdnszones.jsp">delete</qsql:delete></td>
</tr>

</qsql:row>

</table>

</qsql:execute>

</q:setbrick>

</q:usetemplate>