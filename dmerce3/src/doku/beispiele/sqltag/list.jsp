<%@ include file="/init.jsp" %>

<h1>SQL-Tag</h1>

<qsql:prepare id="query1">
   SELECT * FROM vkunde ORDER BY kundennr
</qsql:prepare>

<qsql:execute id="query1">
  <table style="border:1px solid black; padding: 4px;" cellspacing="0">
    <tr>
      <td colspan="3">
        <qsql:rowcount/> Einträge gefunden
      </td>
    </tr> 
    <qsql:row> 
      <tr class="tr<qsql:alternate/>"> 
        <td><qsql:field name="name"/></td>
        <td><qsql:field name="vorname"/></td>
        <td><qsql:field name="email"/></td>
      </tr> 
    </qsql:row>  
  </table>
</qsql:execute>
<b>Hinweis</b>:<br/>
Edit- und Delete-Tag wurden hier herausgenommen.