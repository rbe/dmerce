//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.1-05/30/2003 05:06 AM(java_re)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2003.09.05 at 03:14:44 CEST 
//


package com.wanci.dmerce.webservice.db.xmlbridge;


/**
 * Java content class for META complex type.
 *  <p>The following schema fragment specifies the expected content contained within this java content object.
 * <p>
 * <pre>
 * &lt;complexType name="META">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sql-statement" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="type-info" type="{http://www.wanci.com}TYPEINFO"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface META {


    /**
     * 
     * @return possible object is
     * {@link com.wanci.dmerce.webservice.db.xmlbridge.TYPEINFO}
     */
    com.wanci.dmerce.webservice.db.xmlbridge.TYPEINFO getTypeInfo();

    /**
     * 
     * @param value allowed object is
     * {@link com.wanci.dmerce.webservice.db.xmlbridge.TYPEINFO}
     */
    void setTypeInfo(com.wanci.dmerce.webservice.db.xmlbridge.TYPEINFO value);

    /**
     * 
     * @return possible object is
     * {@link java.lang.String}
     */
    java.lang.String getSqlStatement();

    /**
     * 
     * @param value allowed object is
     * {@link java.lang.String}
     */
    void setSqlStatement(java.lang.String value);

}
