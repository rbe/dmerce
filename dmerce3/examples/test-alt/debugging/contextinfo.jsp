<%@ page import="java.util.*,
				com.wanci.dmerce.workflow.*,
				com.wanci.dmerce.workflow.webapp.*,
				com.wanci.dmerce.taglib.form.*" %>
<%@ taglib uri="/tags/dmerce" prefix="q" %>
<html>
<head>
  <title></title>
  <link rel="stylesheet" type="text/css" href="<q:res path="/css/style.css"/>">
  <meta http-equiv="expires" content="0">
  <meta http-equiv="pragma" content="no-cache">
</head>
<body>

<img src="<q:res path="/images/dmerce_logo.gif"/>" border="0">&nbsp;III alpha2
<hr>

<h1>Debugging-Info<h1>
<h2>Workflow-Kontext</h2>

<%
	WebappWorkflowEngine engine = (WebappWorkflowEngine) session.getAttribute("qWorkflowEngine");
	if (engine != null) {
		Iterator it = engine.getWorkflowIDs().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			WebappWorkflowContext ctx = engine.getWorkflowContext(engine.getWorkflow(key));
%>
<h3>Formularfelder im Workflow (ID: <a href="workflowinfo.jsp?workflow=<%=key%>"><%=key%></a>)</h3>
	<table border="1"><tr><th>Feld</th><th>Wert</th><tr>
<%
			Collection colFormFields = ctx.getFormFields();
			if (colFormFields != null) {
				Iterator itFormfields = colFormFields.iterator();
				while (itFormfields.hasNext()) {
					FormField field = (FormField) itFormfields.next();
					String outtext = "&nbsp;";
					if (field.getValue() == null)
						outtext = "null";
					else if (!field.getValue().equals(""))
						outtext = field.getValue();
					out.println("<tr><td>"+field.getPageId()+"."+field.getFormId()+"."+field.getName()+"</td><td>"+outtext+"</td></tr>");
				}
			}
			else {
				out.println("keine Formularfelder im Kontext gefunden.");
			}
			out.println("</table>");
		}
	}
	else {
%>
Zur Zeit befindet sich keine Workflow-Engine im Session-Kontext.
<%
	}
%>
	
</body>
</html>
