<%@ include file="/init.jsp" %>
<h2>Getr�nkeplanung f�r die Begleitung</h2>
Nun bitte noch die Infos zu deiner Begleitung angeben.
<q:message/>

<qform:form id="einladung">
<table style="padding:10px;">
<tr>
	<td valign="top" width="60px">Name:</td>
	<td width="400px"><qform:text name="name"/><qform:errormessage name="name"/></td>
</tr>
<tr>
	<td valign="top">Fahren oder nicht fahren:</td>
	<td><qform:list type="radio" name="trinken2"/><qform:errormessage name="trinken2"/></td>
</tr>
<tr>
	<td valign="top">Die bevorzugten Getr�nke deiner Begleitung:</td>
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
