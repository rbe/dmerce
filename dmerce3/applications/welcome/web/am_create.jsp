<%@ include file="/init.jsp" %>

<q:usetemplate path="/layout.jsp">
<q:setbrick name="menu" path="bricks/menu.jsp"/>
<q:setbrick name="content">
<h1>Adress-Manager - Anlegen/Pflege eines Datensatzes</h1>

<q:message/>

<qform:form id="form1">

<table>

   <tr>
   <td class="text" valign="top">Vorname</td>
   <td class="text"><qform:text name="vorname"/><qform:errormessage name="vorname"/></td>
   </tr>

   <tr>
   <td class="text" valign="top">Nachname</td>
   <td class="text"><qform:text name="nachname"/><qform:errormessage name="nachname"/></td>
   </tr>

   <tr>
   <td class="text" valign="top">Email</td>
   <td class="text"><qform:text name="email"/><qform:errormessage name="email"/></td>
   </tr>

   <tr>
   <td class="text" valign="top">Strasse</td>
   <td class="text"><qform:text name="strasse"/><qform:errormessage name="strasse"/></td>
   </tr>

   <tr>
   <td class="text" valign="top">PLZ</td>
   <td class="text"><qform:text name="plz"/><qform:errormessage name="plz"/></td>
   </tr>

   <tr>
   <td class="text" valign="top">Ort</td>
   <td class="text"><qform:text name="ort"/><qform:errormessage name="ort"/></td>
   </tr>

   <tr>
   <td class="text" valign="top">Geburtsdatum</td>
   <td class="text"><qform:text name="geburtsdatum"/><qform:errormessage name="geburtsdatum"/></td>
   </tr>

   <tr>
   <td class="text" valign="top">Foto</td>
   <td class="text"><qform:file name="foto"/><qform:fieldnotempty name="foto"><br/><a href="<q:uploadres name="foto"/>"><q:contextvar name="foto" index="1"/></a>&nbsp;<qform:clearfield name="foto">[ del ]</qform:clearfield></qform:fieldnotempty></td>
   </tr>

</table>

<qform:button type="submit" text="Ok"/> <qform:button type="submit" text="Cancel"/>

</qform:form>

<p></p>

<%@ include file="am_footer.jsp" %>
</q:setbrick>
</q:usetemplate>