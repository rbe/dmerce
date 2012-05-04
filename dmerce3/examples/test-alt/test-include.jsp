<%@ include file="header.jsp" %>

<h1>Test Include</h1>

<%
String str = pageContext.getRequest().getParameter("message");
if (str == null) {
   str = (String) pageContext.getSession().getAttribute("message");
   if (str != null)   
	pageContext.getSession().removeAttribute("message");
}

if (str == null) {
	str = "ein unbekannter Fehler ist aufgetreten.<br>";
	str = str + "Bitte kontaktieren sie den Administrator dieser Webseite";	
}

out.print(str);
%>

<%@ include file="footer.jsp" %>
