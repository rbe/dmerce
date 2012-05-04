<%@ include file="header.jsp" %>

<h1>Seite1</h1>

<q:message/>

<qform:form id="form1">
<table>
<tr><td valign="top">Anrede</td><td><qform:list name="gender" type="combo"/></td></tr>
<tr><td valign="top">Name</td><td><qform:text name="name"/><qform:errormessage name="name"/></td></tr>
<tr><td valign="top">Vorname</td><td><qform:text name="vorname"/><qform:errormessage name="vorname"/></td></tr>
<tr><td valign="top">Email</td><td><qform:text name="email"/><qform:errormessage name="email"/></td></tr>
<tr><td valign="top">Strasse</td><td><qform:text name="strasse"/></td></tr>
<tr><td valign="top">PLZ</td><td><qform:text name="plz"/></td></tr>
<tr><td valign="top">Ort</td><td><qform:text name="ort"/></td></tr>
<tr><td valign="top">Telefon</td><td><qform:text name="telefon"/></td></tr>
<tr><td valign="top">AGB</td><td><qform:checkbox name="agb"/></td></tr>
<tr><td valign="top">Hobbies (max. 2)</td><td><qform:list type="checkbox" name="hobby"/><qform:errormessage name="hobby"/></td></tr>
<tr><td valign="top">Bild</td><td><qform:file name="picture"/><qform:errormessage name="picture"/></td></tr>

<!--
<tr><td valign="top">Hobbies (max. 2)</td><td><qform:list type="radio" name="hobby"/></td></tr>
<tr><td valign="top">Hobbies (max. 2)</td><td><qform:list type="list" size="3" name="hobby"/></td></tr>
<tr><td valign="top">Hobbies (max. 2)</td><td><qform:list type="list" size="3" multiple="multiple" name="hobby"/></td></tr>
<tr><td valign="top">Hobbies (max. 2)</td><td><qform:list type="checkbox" name="hobby"/></td></tr>
<tr><td valign="top">Hobbies (max. 2)</td><td><qform:list type="radio" multiple="multiple" name="hobby"/></td></tr>
<tr><td valign="top">Hobbies (sql)</td><td><qform:list type="list" size="3" name="hobby"/></td></tr>
<tr><td valign="top">Land</td><td><qform:list name="country"/></td></tr>
-->

<tr><td valign="top">Bemerkung</td><td><qform:textarea name="comment" rows="5" cols="28"/></td></tr>
</table>
<qform:button type="submit" text="Ok"/>  <qform:button type="submit" text="Cancel"/>
</qform:form>

<%@ include file="footer.jsp" %>

