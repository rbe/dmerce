<%@ include file="/init.jsp" %>
<h2>Getr�nkeplanung</h2>
Hinweis: Wenn du mit Begleitung kommst, werden die Informationen f�r die Person auf der n�chsten Seite abgefragt.
<q:message/>

<qform:form id="einladung">
<table style="padding:10px;">
<tr>
	<td valign="top" width="60px">Fahren oder nicht fahren:</td>
	<td width="*"><qform:list type="radio" name="trinken"/><qform:errormessage name="trinken"/></td>
</tr>
<tr>
	<td valign="top">Meine bevorzugten Getr�nke:</td>
	<td>
		<qform:checkbox name="bier"/>Bier<br/>
		<qform:checkbox name="wein"/>Wein<br/>
		<qform:checkbox name="hartes"/>Hartes Ges�ff<br/>
		<qform:checkbox name="soft"/>Weichei-Getr�nke
	</td>
</tr>
</table>
<qform:button type="submit" text="Ok"/>  <qform:button type="submit" text="Cancel"/>
</qform:form>
