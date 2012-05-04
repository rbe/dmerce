<%@ include file="init.jsp" %>
<%@ page import="org.jdom.*,
			javax.xml.transform.*,
			javax.xml.transform.stream.*,
			org.jdom.input.*,
			org.jdom.transform.JDOMSource,
			org.jdom.xpath.XPath,
			java.io.IOException,
			com.wanci.dmerce.res.ClassLoaderDummy,
			java.util.StringTokenizer,
			com.wanci.dmercedoc.Helper" %>
<%
		SAXBuilder builder = new SAXBuilder();
				
		try {
			
			Document document = builder.build(ClassLoaderDummy.class.getResourceAsStream("doc.xml"));
			String xpathString = Helper.getXPathFromChapter(((String) session.getAttribute("currentid")))+"/content";
			Element node = Helper.getElement(xpathString, document.getRootElement());
			try {
				Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(ClassLoaderDummy.class.getResourceAsStream("doc2html.xsl")));
				transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
				transformer.setOutputProperty(OutputKeys.INDENT, "YES");
				transformer.transform(new JDOMSource(node), new StreamResult(out));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (JDOMException e) {
			System.out.println("JDOM-Fehler");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Datei nicht gefunden.");
			e.printStackTrace();
		}

%>
