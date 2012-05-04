<%@ include file="/init.jsp" %>
<h2>Rï¿½ckmeldung zur Party</h2>
<qform:form id="einladung">
<table style="padding:10px">
<tr><td valign="top">Name:</td><td><qform:text name="name"/><qform:errormessage name="name"/></td></tr>
<tr><td valign="top">Telefon:</td><td><qform:text name="telefon"/></td></tr>
<tr><td valign="top">Rückmeldung:</td><td><qform:list type="list" size="1" name="teilnahme"/></td></tr>
</table>
<qform:button type="submit" text="Ok"/>  <qform:button type="submit" text="Cancel"/>
</qform:form>
