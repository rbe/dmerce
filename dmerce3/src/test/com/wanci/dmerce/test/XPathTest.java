/*
 * Created on Aug 25, 2003
 */
package com.wanci.dmerce.test;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;


/**
 * @author mm
 * @version $Id: XPathTest.java,v 1.1 2004/01/30 17:00:57 rb Exp $
 */
public class XPathTest {

	private Document document;

	private SAXBuilder builder;

	public boolean readXml() {

		builder = new SAXBuilder();

		try {
			document = builder.build("test/forms.xml");
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void searchForm() {
		
		if (!readXml())
			System.out.println("Fehler beim Lesen des XML-Dokumentes aufgetreten.");
		
		String id = "formular";
		
		String xpath = "//form[@id='" + id + "']";
		
		Element node = null;

		try {
			node = (Element) XPath.newInstance(xpath).selectSingleNode(document.getRootElement());

			System.out.println(node.getAttributeValue("id"));
			
			/*
			 List nodes = XPath.newInstance(xpath).selectNodes(document.getRootElement()); 
			  
			 for (int i = 0; i < nodes.size(); i++) {
				// Get element
				Element elem = (Element) nodes.get(i);
				elem.getChild("description");

				if (elem.getChild("description") == null) {
					System.out.println("keine Beschreibung!");
				}
				else {
					System.out.println(elem.getChild("description").getText());	
				}
			}*/
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
	
		XPathTest test = new XPathTest();
		test.searchForm();
	}
}
