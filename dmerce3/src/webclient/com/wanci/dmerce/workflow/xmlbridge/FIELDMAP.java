//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.1-05/30/2003 05:06 AM(java_re)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2004.01.30 at 06:31:07 CET 
//


package com.wanci.dmerce.workflow.xmlbridge;


/**
 * Java content class for FIELDMAP complex type.
 *  <p>The following schema fragment specifies the expected content contained within this java content object.
 * <p>
 * <pre>
 * &lt;complexType name="FIELDMAP">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="formfield" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dbfield" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface FIELDMAP {


    /**
     * 
     * @return possible object is
     * {@link java.lang.String}
     */
    java.lang.String getDbfield();

    /**
     * 
     * @param value allowed object is
     * {@link java.lang.String}
     */
    void setDbfield(java.lang.String value);

    /**
     * 
     * @return possible object is
     * {@link java.lang.String}
     */
    java.lang.String getDefault();

    /**
     * 
     * @param value allowed object is
     * {@link java.lang.String}
     */
    void setDefault(java.lang.String value);

    /**
     * 
     * @return possible object is
     * {@link java.lang.String}
     */
    java.lang.String getFormfield();

    /**
     * 
     * @param value allowed object is
     * {@link java.lang.String}
     */
    void setFormfield(java.lang.String value);

    boolean isFile();

    void setFile(boolean value);

}