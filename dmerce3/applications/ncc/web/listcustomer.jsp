<%@ include file="/init.jsp" %>

<q:usetemplate path="layout.jsp">
<q:setbrick name="menu" path="menu.jsp"/>
<q:setbrick name="content">

<qsql:prepare id="customer">
      SELECT * FROM t_customer
</qsql:prepare>

<qsql:execute id="customer">

<p>
<qsql:rowcount/> entries found
</p>

<table>
<tr>
<td>ID</td>
<td>Lastname</td>
<td>Phone number</td>
<td>Action</td>
</tr>

<qsql:row>

<tr>
<td><qsql:field name="id"/></td>
<td><qsql:field name="lastname"/></td>
<td><qsql:field name="PHONE"/></td>
<td><qsql:edit workflow="1ncc_create_customer" key="id">edit</qsql:edit> <qsql:delete table="t_customer" key="ID" template="ncclistcustomer.jsp">delete</qsql:delete></td>
</tr>

</qsql:row>

</table>

</qsql:execute>

</q:setbrick>

</q:usetemplate>