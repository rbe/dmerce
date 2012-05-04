<%@ include file="init.jsp" %>
<%@ page import="org.jdom.*,
			javax.xml.transform.*,
			javax.xml.transform.stream.*,
			org.jdom.input.*,
			org.jdom.xpath.XPath,
			java.io.IOException,
			com.wanci.dmerce.res.ClassLoaderDummy,
			java.util.*,
			com.wanci.dmercedoc.Helper" %>
<%
		SAXBuilder builder = new SAXBuilder();
		String strKapitel = Helper.getSubnaviChapterString((String) session.getAttribute("currentid"));
		try {
			Document document = builder.build(ClassLoaderDummy.class.getResourceAsStream("doc.xml"));
			String xpathString = Helper.getXPathFromChapter(strKapitel);
			Element rootnode = Helper.getElement(xpathString, document.getRootElement());
			List subsections = rootnode.getChildren("section"); 
			if (subsections.size()>0) {
				out.print("<ul>");
				Iterator it = subsections.iterator();
				while(it.hasNext()) {
					Element node = (Element) it.next();
					out.print("<li>");
					out.print("<a href=\"doc.jsp?id="+strKapitel+"."+(subsections.indexOf(node)+1)+"\">"+node.getChild("title").getText()+"</a>");
					out.print("</li>");
				}
				out.print("</ul>");
			}
		} catch (JDOMException e) {
			System.out.println("JDOM-Fehler");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Datei nicht gefunden.");
			e.printStackTrace();
		}

%>
