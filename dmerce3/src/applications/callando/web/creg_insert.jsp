<%@ include file="/init.jsp" %>

<html>
<head>
<title></title>
</head>
<body bgcolor="#FFFFFF" callpadding="0" cellspacing="0">

<qsql:prepare id="q1">
	SELECT name, installationcost, price FROM v_products WHERE resellerid = <q:contextvar name="resellerid"/> and productid = <q:contextvar name="productid"/>
</qsql:prepare>

<table width="97%" border="1" bordercolor="#DEDFDE" cellspacing="0" cellpadding="0" align="center">

       <tr>

       <td valign="top">

<table width="100%" border="0" cellspacing="2" cellpadding="0"
	style="font-family:Verdana,Arial,Helvetica,Helv,sans-serif; font-size: 8pt;">

       <tr>
       <td bgcolor="#909090" colspan="2">&nbsp;<b><font size="+1">Anmeldeformular:</font></b></td>
       </tr>

       <tr>
       <td bgcolor="#CFCFCF" colspan="2">&nbsp;Bitte beachten Sie, da&szlig; Sie die mit einem * gekennzeicheten Felder ausf&uuml;llen m&uuml;ssen</td>
       </tr>

       <tr>
       <td colspan="2"><img src="<q:res path="/pic/dummy.gif"/>" width="1" height="5" border="0"></td>
       </tr>

       <tr>
       <td colspan="2" bgcolor="#EFEFEF">Ich bestelle den unten ausgew&auml;hlten DSL-Flatrate-Tarif zu den
       	Konditionen, welche auf der Produkt&uuml;bersichtsseite aufgef&uuml;hrt sind.<br>
        Die Leistungsbeschreibungen werden Bestandteil des Vertrages, ebenso die aktuellen AGB.<br>
        <br>Gleichzeitig erm&auml;chtige ich die callando Internet GmbH oder ein beauftragtes Inkassob&uuml;ro
        die f&auml;lligen Zugangsgeb&uuml;hren<br>von meinem unten angebenen Konto im Lastschriftverfahren
        abzubuchen. F&uuml;r ausreichende Deckung des Kontos werde ich immer sorgen.<br>
        Vom Widerrufsrecht habe ich Kenntnis genommen.</td>
       </tr>


       <tr>
       <td colspan="2"><img src="<q:res path="/pic/dummy.gif"/>" width="1" height="5" border="0"></td>
       </tr>

       <tr>
       <td colspan="2" bgcolor="#CFCFCF">&nbsp;<b>gew&uuml;nschter Tarif:</b></td>
       </tr>

<qsql:execute id="q1">
<qsql:row>

       <tr>
       <td bgcolor="#CFCFCF" id="1">&nbsp;</td>
       <td bgcolor="#EFEFEF">&nbsp;<b><qsql:field name="name"/> - <qsql:field name="price" precision="2"/> &#8364; (zzgl. einmalig <qsql:field name="installationcost" precision="2"/> &#8364; Einrichtungsgeb&uuml;hr)</b></td>
       </tr>

</qsql:row>
</qsql:execute>

       <tr>
       <td bgcolor="#CFCFCF">&nbsp;</td>
       <td bgcolor="#EFEFEF">&nbsp;Bitte klicken Sie auf einen Tarif, um mehr Informationen zu diesem Tarif zu erhalten.<br>&nbsp;<a href="creg_listproducts.jsp?resellerid=<q:contextvar name="resellerid"/>">Tarif wechseln!</a></br></td>
       </tr>

       <tr>
       <td colspan="2" bgcolor="#CFCFCF">&nbsp;<b>Anmeldedaten:</b></td>
       </tr>


<qform:form id="customer">

       <tr>
       <td bgcolor="#CFCFCF" width="20%">&nbsp;Geschlecht</td>
       <td bgcolor="#EFEFEF" width="80%"><qform:list type="radio" name="gender"/>&nbsp;<qform:errormessage name="gender"/></td>
       </tr>

       <tr>
       <td bgcolor="#CFCFCF">&nbsp;Nachname</td>
       <td bgcolor="#EFEFEF">&nbsp;<qform:text name="lastname" size="50"/>&nbsp;<qform:errormessage name="lastname"/></td>
       </tr>

       <tr>
       <td bgcolor="#CFCFCF">&nbsp;Vorname</td>

       <td bgcolor="#EFEFEF">&nbsp;<qform:text name="firstname" size="50"/>&nbsp;<qform:errormessage name="firstname"/></td>
       </tr>

       <tr>
       <td bgcolor="#CFCFCF">&nbsp;Strasse</td>
       <td bgcolor="#EFEFEF">&nbsp;<qform:text name="street" size="50"/>&nbsp;<qform:errormessage name="street"/></td>
       </tr>

       <tr>

       <td bgcolor="#CFCFCF">&nbsp;PLZ - Ort</td>
       <td bgcolor="#EFEFEF">&nbsp;<qform:text name="zipcode" size="5"/>&nbsp;<qform:text name="city" size="42"/>&nbsp;<qform:errormessage name="zipcode"/>&nbsp;<qform:errormessage name="city"/></td>
       </tr>

       <tr>
       <td bgcolor="#CFCFCF">&nbsp;Telefon</td>
       <td bgcolor="#EFEFEF">&nbsp;<qform:text name="phone" size="50"/>&nbsp;<qform:errormessage name="phone"/></td>
       </tr>

       <tr>
       <td bgcolor="#CFCFCF">&nbsp;Geburtstag</td>
       <td bgcolor="#EFEFEF">&nbsp;<qform:text name="birthday" size="10"/> (Format TT.MM.JJJJ)&nbsp;<qform:errormessage name="birthday"/></td>
       </tr>

       <tr>
       <td bgcolor="#CFCFCF">&nbsp;Email</td>

       <td bgcolor="#EFEFEF">&nbsp;<qform:text name="email" size="50"/>&nbsp;<qform:errormessage name="email"/></td>
       </tr>

       <tr>
       <td bgcolor="#CFCFCF">&nbsp;KontoNr</td>
       <td bgcolor="#EFEFEF">&nbsp;<qform:text name="accountnumber" size="50"/>&nbsp;<qform:errormessage name="accountnumber"/></td>
       </tr>

       <tr>

       <td bgcolor="#CFCFCF">&nbsp;Kontoinhaber</td>
       <td bgcolor="#EFEFEF">&nbsp;<qform:text name="accountowner" size="50"/>&nbsp;<qform:errormessage name="accountowner"/></td>
       </tr>

       <tr>
       <td bgcolor="#CFCFCF">&nbsp;BLZ</td>
       <td bgcolor="#EFEFEF">&nbsp;<qform:text name="bankcode" size="8"/>&nbsp;<qform:errormessage name="bankcode"/></td>
       </tr>

       <tr>
       <td bgcolor="#CFCFCF">&nbsp;Bank</td>
       <td bgcolor="#EFEFEF">&nbsp;<qform:text name="bank" size="50"/>&nbsp;<qform:errormessage name="bank"/></td>
       </tr>

       <tr>
       <td bgcolor="#CFCFCF">&nbsp;AGB akzeptiert</td>
       <td bgcolor="#EFEFEF"><qform:checkbox name="tacread"/>&nbsp;<qform:errormessage name="tacread"/></td>

       </tr>

       <tr>
       <td bgcolor="#CFCFCF">&nbsp;Widerrufsrecht gelesen</td>
       <td bgcolor="#EFEFEF"><qform:checkbox name="rowread"/>&nbsp;<qform:errormessage name="rowread"/></td>
       </tr>

       <tr>
       <td bgcolor="#CFCFCF">&nbsp;Wunschdatum</td>
       <td bgcolor="#EFEFEF">&nbsp;<qform:text name="desiredstartdate" size="10"/> (Format TT.MM.JJJJ)&nbsp;<qform:errormessage name="desiredstartdate"/></td>

<!--
       <tr>
       <td bgcolor="#CFCFCF">&nbsp;Wunschdatum</td>

       <td bgcolor="#EFEFEF">&nbsp;<select name="StartDate_Day">
<option value="1">01</option>
<option value="2">02</option>
<option value="3" selected>03</option>
<option value="4">04</option>
<option value="5">05</option>
<option value="6">06</option>
<option value="7">07</option>
<option value="8">08</option>
<option value="9">09</option>
<option value="10">10</option>
<option value="11">11</option>
<option value="12">12</option>
<option value="13">13</option>
<option value="14">14</option>
<option value="15">15</option>
<option value="16">16</option>
<option value="17">17</option>
<option value="18">18</option>
<option value="19">19</option>
<option value="20">20</option>
<option value="21">21</option>
<option value="22">22</option>
<option value="23">23</option>
<option value="24">24</option>
<option value="25">25</option>
<option value="26">26</option>
<option value="27">27</option>
<option value="28">28</option>
<option value="29">29</option>
<option value="30">30</option>
<option value="31">31</option>
</select>
. <select name="StartDate_Month">
<option value="1">01</option>
<option value="2">02</option>

<option value="3" selected>03</option>
<option value="4">04</option>
<option value="5">05</option>
<option value="6">06</option>
<option value="7">07</option>
<option value="8">08</option>
<option value="9">09</option>
<option value="10">10</option>
<option value="11">11</option>
<option value="12">12</option>
</select>
. <select name="StartDate_Year">
<option value="2004" selected>2004</option>
<option value="2005">2005</option>
</select>
</td>
       </tr>
-->
       <tr>
       <td bgcolor="#EFEFEF" colspan="2">Bitte beachten Sie, da&szlig; die Einrichtung des Accounts ca. 1-2 Tage in
       	Anspruch nehmen kann.<br>Die Zugangsdaten werden per Mail an Ihre Adresse gesendet. Bitte achten Sie auf die
       	richtige Schreibweise Ihrer Mailadresse.</td>
       </tr>

       <tr>
       <td bgcolor="#CFCFCF" align="right" colspan="2"><qform:button text="Anmelden"/>&nbsp;&nbsp;&nbsp;</td>
       </tr>


</table>
</td>
</tr>

</table>

</qform:form>

</body>
</html>