<%@ include file="header.jsp" %>

<q:headline name="Fehler"/>

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
