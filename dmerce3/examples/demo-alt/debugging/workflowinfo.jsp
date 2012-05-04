<%@ page import="java.util.*,
				com.wanci.dmerce.workflow.*,
				com.wanci.dmerce.workflow.webapp.*,
				com.wanci.dmerce.taglib.form.*" %>
<%@ taglib uri="/tags/dmerce" prefix="q" %>
<html>
<head>
  <title></title>
  <link rel="stylesheet" type="text/css" href="../css/style.css">
  <meta http-equiv="expires" content="0">
  <meta http-equiv="pragma" content="no-cache">
</head>
<body>

<img src="<q:res path="/debugging/images/dmerce_logo.gif"/>" border="0">&nbsp;III alpha2
<hr>
<h1>Dubugging-Info</h1>
<h2>Workflow-Struktur</h2>

<%
	WebappWorkflowEngine engine = (WebappWorkflowEngine) session.getAttribute("qWorkflowEngine");
	if (engine != null) {
		// assert request.getParameter("workflow") != null : "Eine Workflow-ID muss als Request-Parameter übergeben werden.";
		Workflow wf = (Workflow) engine.getWorkflow(request.getParameter("workflow"));
		out.println("<font size=\"1\">");
		out.println(wf.toHtml());
		out.println("</font>");
	}
	else {
%>
Zur Zeit befindet sich keine Workflow-Engine im Session-Kontext.
<%
	}
%>
	
</body>
</html>
