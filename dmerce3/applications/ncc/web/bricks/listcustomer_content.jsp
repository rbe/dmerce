<%@ include file="/init.jsp" %>

<qsql:prepare id="customer">
      SELECT * FROM t_customer
</qsql:prepare>

<table>
<tr>
<td>ID</td>
<td>Lastname</td>
<td>Phone number</td>
<td>Action</td>
</tr>
<qsql:execute id="customer">
<qsql:row>
<tr>
<td><qsql:field name="id"/></td>
<td><qsql:field name="lastname"/></td>
<td><qsql:field name="PHONE"/></td>
<td><qsql:edit workflow="1ncc_create_customer" key="id">edit</qsql:edit> <qsql:delete table="t_customer" key="ID" template="ncclistcustomer.jsp">delete</qsql:delete></td>
</tr>
</qsql:row>
</qsql:execute>
</table>

<q:workflowlink id="1ncc_create_customer" restart="true">Neuen Kunden anlegen</q:workflowlink>