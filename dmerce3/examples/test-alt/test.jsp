<%@ include file="header.jsp" %>

<h1>TEST</h1>
<%
		System.out.println("processRequest");
		String test = request.getServletPath() + "/../etc/forms.xml";

		System.out.println("request.getservletpath(): " + test);
		System.out.println("getservletcontext.getrealpath(): " + 
			getServletContext().getRealPath(test));
%>
		
<q:var name="message"/>

</body>
</html>
