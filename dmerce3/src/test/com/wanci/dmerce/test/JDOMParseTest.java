/*
 * Created on Sep 3, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.wanci.dmerce.test;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jaxen.JaxenException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMSource;
import org.jdom.xpath.XPath;

import com.wanci.dmercedoc.Helper;

/**
 * @author pg
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class JDOMParseTest {
	
	public JDOMParseTest() {
	}
	
	public void test1() {
		String requestId="1";
		
		SAXBuilder builder = new SAXBuilder();
				
		try {
			Document document = builder.build(getClass().getResourceAsStream("doc.xml"));
			Element root = document.getRootElement();
			
			StringTokenizer st = new StringTokenizer(requestId, ".");
			String ebene1 = st.nextToken();
			String ebene2;
			XPath xpath;
			if (st.hasMoreTokens()) {
				ebene2 = st.nextToken();
				xpath = XPath.newInstance("/document/section["+ebene1+"]/section["+ebene2+"]/content");
			}
			else {
				xpath = XPath.newInstance("/document/section["+ebene1+"]/content");
			}
			Element node = (Element) xpath.selectSingleNode(root);
			// ?! System.out.println(node.getParent().getChild("title").getText());
			// System.out.println(node.getText());
			
			try {
				Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(getClass().getResourceAsStream("doc2html.xsl")));
				transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
				transformer.setOutputProperty(OutputKeys.INDENT, "YES");
				transformer.transform(new JDOMSource(node), new StreamResult(System.out));
				
			} catch (TransformerConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformerFactoryConfigurationError e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		} catch (JDOMException e) {
			System.out.println("JDOM-Fehler");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Datei nicht gefunden.");
			e.printStackTrace();
		}
		 
	}
	
	public void test2() {
		try {
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build(getClass().getResourceAsStream("doc.xml"));
			Element root = document.getRootElement();
			String strKapitel = "2.1";
			String xpathString = Helper.getXPathFromChapter(strKapitel);
			Element rootnode = Helper.getElement(xpathString, document.getRootElement());
			
			PrintStream out = System.out;
			
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JaxenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		JDOMParseTest instance =  new JDOMParseTest();
		instance.test2();
	}
}
