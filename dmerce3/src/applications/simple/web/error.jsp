<%@ include file="init.jsp" %>
<%@ include file="header.jsp" %>

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

<%@ include file="footer.jsp" %>
