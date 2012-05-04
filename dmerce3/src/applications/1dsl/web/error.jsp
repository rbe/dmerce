<%@ include file="/init.jsp" %>

<html>
<head>
  <meta http-equiv="expires" content="0">
  <meta http-equiv="pragma" content="no-cache">
  <title>dmerce&reg; Error Page</title>
  <link rel="stylesheet" type="text/css" href="<q:res path="/errorstyle.css"/>">
</head>

<body>
<h1>dmerce&reg; Error Page</h1>
<hr>

<%

	String str = pageContext.getRequest().getParameter("qMessage");
	if (str == null) {
		str = (String) pageContext.getSession().getAttribute("qMessage");
		if (str != null)   
			pageContext.getSession().removeAttribute("qMessage");
	}

	if (str == null) {
		str = "An unknown error occured.<br>";
		str += "Please contact the system administration of this website.";	
	}

	out.print(str);

%>

<hr>
</body>
</html>
