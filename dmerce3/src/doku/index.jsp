<%@ include file="/init.jsp" %>
<%@ page import="
			org.jdom.*,
			org.jdom.input.SAXBuilder,
			org.jdom.transform.JDOMSource,
			javax.xml.transform.*,
			javax.xml.transform.stream.*,
			org.jaxen.*,
			org.jaxen.jdom.JDOMXPath,
			org.saxpath.SAXPathException,
			java.io.IOException,
			com.wanci.dmerce.res.ClassLoaderDummy" %>
			
<q:usetemplate path="/design.jsp">
	<q:setbrick name="ueberschrift">dmerce&reg; Dokumentation</q:setbrick>
	<q:setbrick name="content">
	<ul>
<%
	if (ClassLoaderDummy.class.getResource("doc.xml") != null) {
%>
		<li><a href="<q:res path="/index_doc.jsp"/>">Benutzer-Dokumentation</a></li>
<%
	}
	if (ClassLoaderDummy.class.getResource("devdoc.xml") != null) {
%>
		<li><a href="<q:res path="/index_devdoc.jsp"/>">Entwickler-Dokumentation</a></li>
		<li><a href="<q:res path="/api/index.html"/>">Javadoc zur dmerce-API</a></li>
<%
	}
%>
	</ul>
	</q:setbrick>
	<q:setbrick name="toc">&nbsp;</q:setbrick>
	<q:setbrick name="subnavi">&nbsp;</q:setbrick>
</q:usetemplate>
