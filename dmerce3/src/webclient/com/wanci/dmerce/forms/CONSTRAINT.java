//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.1-05/30/2003 05:06 AM(java_re)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2004.03.09 at 04:45:04 CET 
//


package com.wanci.dmerce.forms;


/**
 * Java content class for CONSTRAINT complex type.
 *  <p>The following schema fragment specifies the expected content contained within this java content object.
 * <p>
 * <pre>
 * &lt;complexType name="CONSTRAINT">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="minlength"/>
 *             &lt;enumeration value="maxlength"/>
 *             &lt;enumeration value="mininclusive"/>
 *             &lt;enumeration value="maxinclusive"/>
 *             &lt;enumeration value="precision"/>
 *             &lt;enumeration value="scale"/>
 *             &lt;enumeration value="startdate"/>
 *             &lt;enumeration value="enddate"/>
 *             &lt;enumeration value="format"/>
 *             &lt;enumeration value="minselected"/>
 *             &lt;enumeration value="maxselected"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface CONSTRAINT {


    /**
     * 
     * @return possible object is
     * {@link java.lang.String}
     */
    java.lang.String getType();

    /**
     * 
     * @param value allowed object is
     * {@link java.lang.String}
     */
    void setType(java.lang.String value);

    /**
     * 
     * @return possible object is
     * {@link java.lang.String}
     */
    java.lang.String getValue();

    /**
     * 
     * @param value allowed object is
     * {@link java.lang.String}
     */
    void setValue(java.lang.String value);

    /**
     * 
     * @return possible object is
     * {@link java.lang.String}
     */
    java.lang.String getDescription();

    /**
     * 
     * @param value allowed object is
     * {@link java.lang.String}
     */
    void setDescription(java.lang.String value);

}
