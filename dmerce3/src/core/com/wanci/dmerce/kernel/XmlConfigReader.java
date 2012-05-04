/*
 * Created on Jun 18, 2003
 */
package com.wanci.dmerce.kernel;

//import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * @author mm
 * @author pg
 * @version $Id: XmlConfigReader.java,v 1.6 2004/03/11 22:06:55 rb Exp $
 * 
 * reader for scanner resource
 * 
 * in the file or db there are different kinds of scan objects, networks and
 * hosts this class provides a translation from xml to Vectors
 * 
 * the Vector for Hosts: host1 (Hashmap) (inetaddress host) host2 ... ...
 * 
 * the Vector for Networks: network1 (Hashmap) (string range) network2 ... ...
 * 
 * contains the business logic to ask for networks to scan, for hosts and ports
 *  
 */
public class XmlConfigReader implements ExtendedXmlConfigReaderInterface {

    protected boolean DEBUG = false;

    protected boolean DEBUG2 = false;

    /**
     * resource string for xml config string
     */
    private InputStream streamXmlResource;

    private String stringXmlResource;

    private boolean boolStreamModus;

    /**
     * parser
     */
    protected SAXBuilder parser;

    /**
     * xml document
     */
    protected Document document;

    /**
     * getter for xml-document
     * 
     * @return Document object
     */
    public Document getDocument() {
        return document;
    }

    /**
     * read the xmlresource
     * 
     * @return true on success
     * @see com.wanci.dmerce.dmf.XmlConfigReaderInterface#read()
     */
    public boolean read() {

        boolean r = false;

        //System.out.println("Reading and parsing xml properties file: " +
        // xmlResource);

        if ((streamXmlResource == null)
                & (stringXmlResource == null || stringXmlResource.equals("")))
            return false;

        parser = new SAXBuilder();

        try {

            if (isSetAsStream()) {
                //System.out.println("SAXBuilder: Parse as Stream.");
                document = parser.build(streamXmlResource);
            }
            else {
                //System.out.println("SAXBuilder: Parse as String.");
                document = parser.build(stringXmlResource);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    /**
     * get the element list as vector of a special kind of elements
     * 
     * @param parent
     * @param string
     * @return vector with list
     */
    public Vector getElementList(Element parent, String string) {

        Vector v = new Vector();

        try {
            v.add(XPath.newInstance(string).selectNodes(parent));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return v;

    }

    /**
     * process and inspect element and print out structure
     * 
     * @param element
     */
    public void process(Element element) {

        inspect(element);
        List content = element.getContent();
        Iterator iterator = content.iterator();

        while (iterator.hasNext()) {
            Object o = iterator.next();

            if (o instanceof Element) {
                Element child = (Element) o;
                process(child);
            }
        }
    }

    /**
     * inspect an element and print out structure
     * 
     * @param element
     */
    public void inspect(Element element) {

        if (!element.isRootElement()) {
            System.out.println();
        }

        String qualifiedName = element.getQualifiedName();
        System.out.println(qualifiedName + ":");

        List attributes = element.getAttributes();

        if (!attributes.isEmpty()) {
            Iterator iterator = attributes.iterator();

            while (iterator.hasNext()) {
                Attribute attribute = (Attribute) iterator.next();
                String name = attribute.getName();
                String value = attribute.getValue();
                Namespace attributeNamespace = attribute.getNamespace();

                if (attributeNamespace == Namespace.NO_NAMESPACE) {
                    System.out.println("  " + name + "=\"" + value + "\"");

                }
                else {
                    String prefix = attributeNamespace.getPrefix();
                    System.out.println("  " + prefix + ":" + name + "=\""
                            + value + "\"");
                }
            }
        }
    }

    /**
     * set XML Resource File
     * 
     * @param xml
     *            resource file
     * @return true on success
     */
    public void setXmlResource(InputStream stream) {
        streamXmlResource = stream;
        boolStreamModus = true;
    }

    /**
     * get root element of document
     * 
     * @return rootelement
     */
    public Element getRootElement() {
        return document.getRootElement();
    }

    /**
     * get the name of an element (the string after the opentag " <"
     * 
     * @param element
     * @return
     */
    public String getElementName(Element element) {
        return element.getQualifiedName();
    }

    /**
     * get a special attribute of an element
     * 
     * @param element
     * @param attributeName
     * @return attributevalue as string
     */
    public String getAttributeValue(Element element, String attributeName) {

        List attributes = element.getAttributes();

        if (!attributes.isEmpty()) {
            Iterator iterator = attributes.iterator();

            while (iterator.hasNext()) {
                Attribute attribute = (Attribute) iterator.next();
                String name = attribute.getName();
                if (name.equals(attributeName))
                    return attribute.getValue();
            }
        }
        return "";

    }

    /**
     * method for testing the scanner reader
     * 
     * @param args
     */
    public static void main(String[] args) throws IOException, JDOMException {

        Boot.printCopyright("XML CONFIG READER");

        //XmlConfigReader xml = new PortScannerXmlConfigReader("etc/ncc.xml");

        XmlConfigReader xml = new XmlConfigReader();

        //xml.process(xml.getRootElement());
        xml.read();
    }

    /**
     */
    public void setXmlResource(String string) {
        stringXmlResource = string;
        boolStreamModus = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.wanci.dmerce.kernel.XmlConfigReaderInterface#isSetAsStream()
     */
    public boolean isSetAsStream() {
        // TODO Auto-generated method stub
        return boolStreamModus;
    }

}