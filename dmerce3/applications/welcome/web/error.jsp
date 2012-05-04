<%@ include file="/init.jsp" %>
<html>
<head>
  <title></title>
  <link rel="stylesheet" type="text/css" href="<q:res path="/css/style.css"/>">
  <meta http-equiv="expires" content="0">
  <meta http-equiv="pragma" content="no-cache">
</head>
	<body>
<h1>Fehler</h1>

<%

String str = pageContext.getRequest().getParameter("qMessage");
if (str == null) {
   str = (String) pageContext.getSession().getAttribute("qMessage");
   if (str != null)
	pageContext.getSession().removeAttribute("qMessage");
}

if (str == null) {
	str = "ein unbekannter Fehler ist aufgetreten.<br>";
	str = str + "Bitte kontaktieren sie den Administrator dieser Webseite";
}

out.print(str);

%>

<hr/>
</body>
</html>