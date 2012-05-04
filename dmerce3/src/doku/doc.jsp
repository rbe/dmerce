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
		SAXBuilder builder = new SAXBuilder();
		String strUeberschrift = "";	
		try {
			
			Document document = builder.build(ClassLoaderDummy.class.getResourceAsStream("doc.xml"));
			Element root = document.getRootElement();
			String xpathString = Helper.getXPathFromChapter(strKapitel);
			Element node = Helper.getElement(xpathString, root);
			strUeberschrift = node.getChild("title").getText();
		} catch (JDOMException e) {
			System.out.println("JDOM-Fehler");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Datei nicht gefunden.");
			e.printStackTrace();
		}

%>
	<q:usetemplate path="/design.jsp">
	<q:setbrick name="ueberschrift"><%= strUeberschrift %></q:setbrick>
	<q:setbrick name="content" path="content.jsp"/>
	<q:setbrick name="toc" path="toc.jsp"/>
	<q:setbrick name="subnavi" path="subnavi.jsp"/>
</q:usetemplate>
