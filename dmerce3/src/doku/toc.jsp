<%@ include file="init.jsp" %>
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
<%
		SAXBuilder builder = new SAXBuilder();
		
		try {
			Document document = builder.build(ClassLoaderDummy.class.getResourceAsStream("doc.xml"));
			Element root = document.getRootElement();
			XPath xpath = new JDOMXPath("/document");
			Element node = (Element) xpath.selectSingleNode(root);
			
			try {
				Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(ClassLoaderDummy.class.getResourceAsStream("doc2toc.xsl")));
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
		} catch (JaxenException e) {
			e.printStackTrace();
		}

%>
