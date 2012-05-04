/*
 * Created on 26.11.2003
 *  
 */
package com.wanci.dmercedoc;

import java.util.StringTokenizer;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;

/**
 * @author Masanori Fujita
 */
public class Helper {

    /**
     * Erzeugt aus einer mit Punkten abgetrennten Kapitelbezeichnung wie
     * "1.2.4" einen XPath-Ausdruck passend zum doc.xml
     * 
     * @param chapterString
     *            Kapitelstring
     * @return XPath-Ausdruck
     */
    public static String getXPathFromChapter(String chapterString) {

        StringTokenizer st = new StringTokenizer(chapterString, ".");
        String xPathString = "/document";
        int i = 0;

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            xPathString += "/section[" + token + "]";
        }

        return xPathString;

    }

    /**
     * Erzeugt aus einer mit Punkten abgetrennten Kapitelbezeichnung wie
     * "1.2.4" einen XPath-Ausdruck passend zum doc.xml, der niemals die Tiefe
     * 3 überschreitet.
     * 
     * @param chapterString
     *            Kapitelstring
     * @return XPath-Ausdruck
     */
    public static String getSubnaviChapterString(String chapterString) {

        StringTokenizer st = new StringTokenizer(chapterString, ".");
        String result = st.nextToken();
        int i = 1;

        while (st.hasMoreTokens() && i++ < 2) {
            String token = st.nextToken();
            result += "." + token;
        }

        return result;

    }

    /**
     * Führt eine XPath-Suche in einem Knoten durch
     * 
     * @param xPathString
     *            XPath-Ausdruck
     * @param nodeToSearchIn
     *            Knoten, in dem gesucht werden soll
     * @return Der gefundene Knoten
     * @throws JaxenException
     */
    public static Element getElement(String xPathString, Element nodeToSearchIn)
            throws JaxenException {

        XPath xpath;
        xpath = new JDOMXPath(xPathString);

        return (Element) xpath.selectSingleNode(nodeToSearchIn);

    }

}