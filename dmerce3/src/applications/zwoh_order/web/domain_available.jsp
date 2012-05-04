<%

String domain = request.getParameter("d");
/*
String tld = request.getParameter("tld");
if (tld != null)
	domain += "." + tld;
*/

%>

<html>
<head>
<title>:: Branchenl&ouml;sungen :: Die web visitenkarte! :: compoint 2000</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<!-- Fireworks MX Dreamweaver MX target.  Created Mon Dec 01 10:57:31 GMT+0100 (Westeuropäische Normalzeit) 2003-->
<style type="text/css">
<!--


BODY {
scrollbar-track-color: #bab9b0;
scrollbar-arrow-color: #000000;
scrollbar-face-color: #dcd9c7;
scrollbar-highlight-color: #dcd9c7;
scrollbar-darkshadow-color: #dcd9c7;
scrollbar-shadow-color:#bab9b0;
scrollbar-3dlight-color: #bab9b0;
}

.text {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 15px;
	color: #333333;
	text-align: justify;
	line-height: 20px;
}
.bild {
	border: 1px solid #333333;
}

.scrollen { width: 450; height: 455; overflow: AUTO; padding: 4; border: 0px}

li {
	list-style-image:  url(../images/list.gif);
}
h1 {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
	font-weight: bold;
	color: #333333;
}
a:link {
	font-family: Arial, Helvetica, sans-serif;
	font-weight: bold;
	color: #990000;
	text-decoration: underline;
}
a:visited {
	font-family: Arial, Helvetica, sans-serif;
	font-weight: bold;
	color: #333333;
}
a:hover {
	font-family: Arial, Helvetica, sans-serif;
	font-weight: bold;
	color: #000000;
}
a:active {
	font-weight: bold;
	color: #666666;
}
input {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 11px;
	color: #333333;
	background-color: #CCCCCC;
	border: 1px solid #333333;
	height: 20px;
	width: 180px;

}
textarea {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 11px;
	color: #333333;
	background-color: #CCCCCC;
	border: 1px solid #333333;
	height: 100px;
	width: 150px;

}
.text2 {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
	color: #333333;
}
-->
</style>
<style type="text/css">
<!--
.smaller {
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 10px;
	color: #333333;
}
-->
</style>
</head>
<body scroll="auto" bgcolor="#bdb897">
<table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
  <!-- fwtable fwsrc="PAGELIGHT.png" fwbase="PAGELIGHT.jpg" fwstyle="Dreamweaver" fwdocid = "742308039" fwnested="0" -->
  <tr>
    <td><img src="http://www.webstart.zwoh.de/order/images/spacer.gif" width="80" height="1" border="0" alt=""></td>
    <td><img src="http://www.webstart.zwoh.de/order/images/spacer.gif" width="606" height="1" border="0" alt=""></td>
    <td><img src="http://www.webstart.zwoh.de/order/images/spacer.gif" width="5" height="1" border="0" alt=""></td>
    <td><img src="http://www.webstart.zwoh.de/order/images/spacer.gif" width="109" height="1" border="0" alt=""></td>
    <td><img src="http://www.webstart.zwoh.de/order/images/spacer.gif" width="1" height="1" border="0" alt=""></td>
  </tr>
  <tr>
    <td colspan="4"><img name="PAGELIGHT_r1_c1" src="http://www.webstart.zwoh.de/images/PAGELIGHT_r1_c1.jpg" width="800" height="89" border="0" alt=""></td>
    <td><img src="http://www.webstart.zwoh.de/order/images/spacer.gif" width="1" height="89" border="0" alt=""></td>
  </tr>
  <tr>
    <td colspan="3"><img name="PAGELIGHT_r2_c1" src="http://www.webstart.zwoh.de/images/PAGELIGHT_r2_c1.jpg" width="691" height="4" border="0" alt=""></td>
    <td rowspan="4"><img name="PAGELIGHT_r2_c4" src="http://www.webstart.zwoh.de/images/PAGELIGHT_r2_c4.jpg" width="109" height="42" border="0" alt=""></td>
    <td><img src="http://www.webstart.zwoh.de/order/images/spacer.gif" width="1" height="4" border="0" alt=""></td>
  </tr>
  <tr>
    <td rowspan="6"><img name="PAGELIGHT_r3_c1" src="http://www.webstart.zwoh.de/images/PAGELIGHT_r3_c1.jpg" width="80" height="507" border="0" alt=""></td>
    <td colspan="2"><img src="http://www.webstart.zwoh.de/order/images/PAGELIGHT_r3_c2.jpg" alt="" name="PAGELIGHT_r3_c2" width="611" height="22" border="0" usemap="#PAGELIGHT_r3_c2Map"></td>
    <td><img src="http://www.webstart.zwoh.de/order/images/spacer.gif" width="1" height="22" border="0" alt=""></td>
  </tr>
  <tr>
    <td colspan="2"><img name="PAGELIGHT_r4_c2" src="http://www.webstart.zwoh.de/images/PAGELIGHT_r4_c2.jpg" width="611" height="10" border="0" alt=""></td>
    <td><img src="http://www.webstart.zwoh.de/order/images/spacer.gif" width="1" height="10" border="0" alt=""></td>
  </tr>
  <tr>
    <td rowspan="3" align="center" valign="top" background="../images/PAGELIGHT_r5_c2.jpg"><table width="100%" border="0" cellpadding="5" cellspacing="0" class="text">
        <tr>
          <td width="80%" align="center" valign="middle"> <p><strong><img src="http://www.webstart.zwoh.de/order/images/logo_smaller.jpg" width="100" height="44" hspace="10" align="left"></strong></p>
            <p>&nbsp;</p>
            <p align="left">Herzlichen Gl&uuml;ckwunsch,</p>
            <form action="http://www.webstart.zwoh.de/order/bestellen.php" method="get" enctype="application/x-www-form-urlencoded" name="form1">
              <p>Die gew&uuml;nschte Domain www.<%= domain %> ist verf&uuml;gbar</p>
              <p>&nbsp; </p>
              <p>
                <input type="submit" name="Submit" value="Bestellen">
                <input name="url" type="hidden" value="<%= domain %>">
              </p>
            </form>
            <p align="left">&nbsp;</p>
            <p><strong><img src="http://www.webstart.zwoh.de/order/images/logo_smaller.jpg" width="100" height="44" hspace="10" align="right"></strong></p>
            </td>
          <td width="20%" align="center" valign="top"> <p><img src="http://www.webstart.zwoh.de/order/images/price.gif" width="100" height="380" class="bild">
              <br>
              <span class="smaller">*zzgl. einmalig <br>
              29,- Euro Einrichtungsgeb&uuml;hr</span></p></td>
        </tr>
      </table></td>
    <td rowspan="4"><img name="PAGELIGHT_r5_c3" src="http://www.webstart.zwoh.de/images/PAGELIGHT_r5_c3.jpg" width="5" height="475" border="0" alt=""></td>
    <td><img src="http://www.webstart.zwoh.de/order/images/spacer.gif" width="1" height="6" border="0" alt=""></td>
  </tr>
  <tr>
    <td><img name="PAGELIGHT_r6_c4" src="http://www.webstart.zwoh.de/images/PAGELIGHT_r6_c4.jpg" width="109" height="330" border="0" alt=""></td>
    <td><img src="http://www.webstart.zwoh.de/order/images/spacer.gif" width="1" height="330" border="0" alt=""></td>
  </tr>
  <tr>
    <td rowspan="2"><img name="PAGELIGHT_r7_c4" src="http://www.webstart.zwoh.de/images/PAGELIGHT_r7_c4.jpg" width="109" height="139" border="0" alt=""></td>
    <td><img src="http://www.webstart.zwoh.de/order/images/spacer.gif" width="1" height="130" border="0" alt=""></td>
  </tr>
  <tr>
    <td><img name="PAGELIGHT_r8_c2" src="http://www.webstart.zwoh.de/images/PAGELIGHT_r8_c2.jpg" width="606" height="9" border="0" alt=""></td>
    <td><img src="http://www.webstart.zwoh.de/order/images/spacer.gif" width="1" height="9" border="0" alt=""></td>
  </tr>
</table>
<map name="PAGELIGHT_r3_c2Map">
  <area shape="rect" coords="1,2,78,24" href="http://www.webstart.zwoh.de/index.htm">
  <area shape="rect" coords="94,2,137,20" href="http://www.webstart.zwoh.de/profil.htm">
  <area shape="rect" coords="155,2,229,21" href="http://www.webstart.zwoh.de/funktionen.htm">
  <area shape="rect" coords="251,2,302,21" href="http://www.webstart.zwoh.de/preis.htm">
  <area shape="rect" coords="313,3,376,20" href="bestellen.php">
  <area shape="rect" coords="398,2,439,21" href="http://www.webstart.zwoh.de/agb.htm">
  <area shape="rect" coords="456,1,514,19" href="http://www.webstart.zwoh.de/zukunft.htm">
  <area shape="rect" coords="528,2,608,21" href="http://www.webstart.zwoh.de/impressum.htm">
</map>
</body>
</html>