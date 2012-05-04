<%@ include file="/init.jsp" %>

<q:usetemplate path="/layout.jsp">
<q:setbrick name="menu" path="bricks/menu.jsp"/>
<q:setbrick name="content">
<h1>Adress-Manager - Liste aller Eintr&auml;ge</h1>

<qsql:prepare id="query1">
   SELECT * FROM t_am_kunde ORDER BY nachname
</qsql:prepare>

<table cellpadding="2" cellspacing="3" border="1">

<qsql:execute id="query1">

    <tr><td class="text" colspan="5"><qsql:rowcount/> Eintr&auml;ge gefunden</td></tr>
    <tr>
    <td class="text">lfd. Nr.</th>
    <td class="text">ID</th>
    <td class="text" width="200">Daten</th>
    <td class="text">Foto</th>
    <td class="text">&nbsp;</th>
    </tr>

<qsql:row>

    <tr>
    <td class="text" valign="top"><qsql:number/></td>
    <td class="text" valign="top"><qsql:field name="id" precision="0"/></td>
    <td class="text" valign="top"><qsql:field name="vorname"/> <qsql:field name="nachname"/><br><qsql:field name="strasse"/><br><qsql:field name="plz"/> <qsql:field name="ort"/><br><qsql:fieldnotempty name="geburtsdatum">geb. am. <qsql:datefield name="geburtsdatum" format="dd.MM.yyyy"/><br></qsql:fieldnotempty><qsql:field name="email"/></td>
    <td class="text" valign="top"><qsql:fieldnotempty name="foto" file="true"><a href="<q:uploadres name="foto"/>"><qsql:field name="foto" file="true"/></a></qsql:fieldnotempty><qsql:fieldempty name="foto" file="true">&nbsp;</qsql:fieldempty><br><img src="<q:uploadres name="foto" emptypath="images/noimg.jpg"/>" style="width:50px; height: 30px"/></td>
    <td class="text" valign="top"><nobr><qsql:delete table="t_am_kunde" key="id" template="am_list.jsp">[ del ]</qsql:delete></nobr> <nobr><qsql:edit workflow="am_register" key="id">[ edit ]</qsql:edit></nobr></td>
    </tr>

</qsql:row>
</qsql:execute>

</table>

<%@ include file="am_footer.jsp" %>
</q:setbrick>
</q:usetemplate>