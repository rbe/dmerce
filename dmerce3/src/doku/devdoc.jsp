<%@ include file="/init.jsp" %>

<%@ page import="org.jdom.*,
			org.jdom.input.*,
			org.jdom.xpath.XPath,
			java.io.IOException,
			com.wanci.dmerce.res.ClassLoaderDummy,
			java.util.StringTokenizer,
			com.wanci.dmercedoc.Helper" %>

<%

	String strKapitel = (String) session.getAttribute("currentid");

	if (request.getParameter("id")!= null) {

		session.setAttribute("currentid", request.getParameter("id"));
		strKapitel = request.getParameter("id");

	}
	else {

		session.setAttribute("currentid", "1");
		strKapitel = "1";

	}

	SAXBuilder builder = new SAXBuilder();
	String strUeberschrift = "";	

	try {
			
		Document document = builder.build(ClassLoaderDummy.class.getResourceAsStream("devdoc.xml"));
		Element root = document.getRootElement();
		String xpathString = Helper.getXPathFromChapter(strKapitel);
		Element node = Helper.getElement(xpathString, root);
		strUeberschrift = node.getChild("title").getText();

	} catch (JDOMException e) {

		System.out.println("Error with JDOM: " + e.getMessage());
		e.printStackTrace();

	} catch (IOException e) {

		System.out.println("File not found: " + e.getMessage());
		e.printStackTrace();

	}

%>

	<q:usetemplate path="/design.jsp">
	<q:setbrick name="ueberschrift"><%= strUeberschrift %></q:setbrick>
	<q:setbrick name="content" path="devcontent.jsp"/>
	<q:setbrick name="toc" path="devtoc.jsp"/>
	<q:setbrick name="subnavi" path="devsubnavi.jsp"/>

</q:usetemplate>
