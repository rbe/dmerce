//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.1-05/30/2003 05:06 AM(java_re)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2004.03.09 at 04:45:04 CET 
//

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.wanci.dmerce.forms.impl.runtime;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.helpers.PrintConversionEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;

import org.xml.sax.SAXException;

import com.sun.xml.bind.ProxyGroup;

/**
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Util {
    /**
     * Report a print conversion error while marshalling.
     */
    public static void handlePrintConversionException(
        Object caller, Exception e, XMLSerializer serializer ) throws SAXException {
        
        if( e instanceof SAXException )
            // assume this exception is not from application.
            // (e.g., when a marshaller aborts the processing, this exception
            //        will be thrown) 
            throw (SAXException)e;
        
        ValidationEvent ve = new PrintConversionEventImpl(
            ValidationEvent.ERROR, e.getMessage(),
            new ValidationEventLocatorImpl(caller) );
        serializer.reportError(ve);
    }
    
    
    private static final Class[] xmlSerializableSatellite = new Class[] {
        XMLSerializable.class,
        XMLSerializer.class,
        NamespaceContext2.class
    };
    
    public static XMLSerializable toXMLSerializable(Object o) {
        return (XMLSerializable)ProxyGroup.blindWrap( o, XMLSerializable.class, xmlSerializableSatellite );
    }
    
    
    private static final Class[] validatableSatellite = new Class[] {
        XMLSerializable.class,
        XMLSerializer.class,
        ValidatableObject.class,
        NamespaceContext2.class
    };
    
    public static ValidatableObject toValidatableObject(Object o) {
        return (ValidatableObject)ProxyGroup.blindWrap( o, ValidatableObject.class, validatableSatellite );
    }
}
