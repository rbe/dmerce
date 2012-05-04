<%@ include file="/init.jsp" %>

<html>
<body>

<qsql:prepare id="advertcats">
SELECT id, name, description FROM t_advertcategories
</qsql:prepare>

<qsql:execute id="advertcats">
<table>
<qsql:row>
<tr>
    <td><qsql:field name="id"/></td>
    <td><qsql:field name="name"/></td>
    <td><qsql:field name="description"/></td>
    <td><qsql:edit workflow="vwadmin_advertcats" key="id">edit</qsql:edit></td>
    <td><qsql:delete table="t_advertcategories" key="id" template="vwadmin_list_advertcats.jsp">löschen</qsql:delete></td>
</tr>
</qsql:row>
</table>
</qsql:execute>

<q:workflowlink id="vwadmin_advertcats" restart="true">Neuer Eintrag</q:workflowlink>

</body>
</html>