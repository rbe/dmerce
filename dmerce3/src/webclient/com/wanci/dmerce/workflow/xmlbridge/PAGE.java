//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.1-05/30/2003 05:06 AM(java_re)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2004.01.30 at 06:31:07 CET 
//


package com.wanci.dmerce.workflow.xmlbridge;


/**
 * Java content class for PAGE complex type.
 *  <p>The following schema fragment specifies the expected content contained within this java content object.
 * <p>
 * <pre>
 * &lt;complexType name="PAGE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="formmap" type="{http://www.1ci.de}FORMMAP" minOccurs="0"/>
 *         &lt;element name="transition" type="{http://www.1ci.de}TRANSITION" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="template" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="editable" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="formid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface PAGE {


    /**
     * 
     * @return possible object is
     * {@link java.lang.String}
     */
    java.lang.String getFormid();

    /**
     * 
     * @param value allowed object is
     * {@link java.lang.String}
     */
    void setFormid(java.lang.String value);

    /**
     * 
     * @return possible object is
     * {@link com.wanci.dmerce.workflow.xmlbridge.FORMMAP}
     */
    com.wanci.dmerce.workflow.xmlbridge.FORMMAP getFormmap();

    /**
     * 
     * @param value allowed object is
     * {@link com.wanci.dmerce.workflow.xmlbridge.FORMMAP}
     */
    void setFormmap(com.wanci.dmerce.workflow.xmlbridge.FORMMAP value);

    boolean isEditable();

    void setEditable(boolean value);

    /**
     * 
     * @return possible object is
     * {@link java.lang.String}
     */
    java.lang.String getTemplate();

    /**
     * 
     * @param value allowed object is
     * {@link java.lang.String}
     */
    void setTemplate(java.lang.String value);

    /**
     * 
     * @return possible object is
     * {@link java.lang.String}
     */
    java.lang.String getId();

    /**
     * 
     * @param value allowed object is
     * {@link java.lang.String}
     */
    void setId(java.lang.String value);

    /**
     * Gets the value of the Transition property.
     * 
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there's any setter method for the Transition property.
     * 
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransition().add(newItem);
     * </pre>
     * 
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link com.wanci.dmerce.workflow.xmlbridge.TRANSITION}
     * 
     */
    java.util.List getTransition();

}
