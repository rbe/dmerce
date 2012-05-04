<%@ include file="header.jsp" %>

<h1>Fehler</h1>

<%
out.println("<p/>");
String str = pageContext.getRequest().getParameter("qMessage");
if (str == null) {
	str = (String) pageContext.getSession().getAttribute("qMessage");
	if (str != null)   
		pageContext.getSession().removeAttribute("qMessage");
}
// Immernoch keine Message?
if (str == null) {
	str = "Ein unbekannter Fehler ist aufgetreten.<br>";
	str = str + "Bitte kontaktieren sie den Administrator dieser Webseite.";	
}
out.print(str);

// Exception ausgeben
	Throwable t = (Throwable) session.getAttribute("qException");
	if (t != null) {
		out.println("<p/>Stack-Trace:<br/>");
		StackTraceElement[] st = t.getStackTrace();
		for (int i = 0; i < st.length; i++) {
			out.println(st[i].toString()+"<br/>");
		}
	}
	
// Root-Cause ausgeben
	Throwable rc = (Throwable) session.getAttribute("qRootCause");
	if (rc != null) {
		out.println("<p/>Root Cause:<br/>");
		StackTraceElement[] st = rc.getStackTrace();
		for (int i = 0; i < st.length; i++) {
			out.println(st[i].toString()+"<br/>");
		}
	}

%>

<%@ include file="footer.jsp" %>
