<%@ include file="/init.jsp" %>
<h2>Getränkeplanung</h2>
Hinweis: Wenn du mit Begleitung kommst, werden die Informationen für die Person auf der nächsten Seite abgefragt.
<q:message/>

<qform:form id="einladung">
<table style="padding:10px;">
<tr>
	<td valign="top" width="60px">Fahren oder nicht fahren:</td>
	<td width="*"><qform:list type="radio" name="trinken"/><qform:errormessage name="trinken"/></td>
</tr>
<tr>
	<td valign="top">Meine bevorzugten Getränke:</td>
	<td>
		<qform:checkbox name="bier"/>Bier<br/>
		<qform:checkbox name="wein"/>Wein<br/>
		<qform:checkbox name="hartes"/>Hartes Gesöff<br/>
		<qform:checkbox name="soft"/>Weichei-Getränke
	</td>
</tr>
</table>
<qform:button type="submit" text="Ok"/>  <qform:button type="submit" text="Cancel"/>
</qform:form>
