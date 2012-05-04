//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.1-05/30/2003 05:06 AM(java_re)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2004.01.30 at 06:31:07 CET 
//

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)$Id: AbstractGrammarInfoImpl.java,v 1.4 2004/02/02 16:44:30 mf Exp $
 */
package com.wanci.dmerce.workflow.xmlbridge.impl.runtime;

import java.io.InputStream;
import java.io.ObjectInputStream;

import javax.xml.bind.JAXBException;

import com.sun.msv.grammar.Grammar;
import com.sun.xml.bind.GrammarImpl;
import com.sun.xml.bind.Messages;

/**
 * Keeps the information about the grammar as a whole.
 * 
 * Implementation of this interface is provided by the generated code.
 *
 * @author
 *  <a href="mailto:kohsuke.kawaguchi@sun.com>Kohsuke KAWAGUCHI</a>
 */
public abstract class AbstractGrammarInfoImpl implements GrammarInfo
{
    /**
     * Creates an unmarshaller that can unmarshal a given element.
     * 
     * @param namespaceUri
     *      The string needs to be interned by the caller
     *      for a performance reason.
     * @param localName
     *      The string needs to be interned by the caller
     *      for a performance reason.
     * 
     * @return
     *      null if the given name pair is not recognized.
     */
    public abstract UnmarshallingEventHandler createUnmarshaller(
        String namespaceUri, String localName, UnmarshallingContext context );
    
    /**
     * Return the probe points for this GrammarInfo, which are used to detect 
     * {namespaceURI,localName} collisions across the GrammarInfo's on the
     * schemaPath.  This is a slightly more complex implementation than a simple
     * hashmap, but it is more flexible in supporting additional schema langs.
     */
    public abstract String[] getProbePoints();
    
    /**
     * Returns true if the invocation of the createUnmarshaller method
     * will return a non-null value for the given name pair.
     * 
     * @param namespaceUri
     *      The string needs to be interned by the caller
     *      for a performance reason.
     * @param localName
     *      The string needs to be interned by the caller
     *      for a performance reason.
     */
    public abstract boolean recognize( String nsUri, String localName );
    
    /**
     * Gets the default implementation for the given public content
     * interface. 
     *
     * @param javaContentInterface
     *      the Class object of the public interface.
     * 
     * @return null
     *      If the interface is not found.
     */
    public abstract Class getDefaultImplementation( Class javaContentInterface );
    
    /**
     * Gets the MSV AGM which can be used to validate XML during
     * marshalling/unmarshalling.
     */
    public Grammar getGrammar() throws JAXBException {
        try {
            InputStream is = this.getClass().getResourceAsStream("bgm.ser");
            
            if( is==null )
                // unable to find bgm.ser
                throw new JAXBException(
                    Messages.format( Messages.NO_BGM,
                                     this.getClass().getName().replace('.','/') ) );
                    
            
            // deserialize the bgm
            ObjectInputStream ois = new ObjectInputStream( is );
            GrammarImpl g = (GrammarImpl)ois.readObject();
            ois.close();
            
            g.connect(new Grammar[]{g});    // connect to itself
            
            return g;
        } catch( Exception e ) {
            throw new JAXBException( 
                Messages.format( Messages.UNABLE_TO_READ_BGM ), 
                e );
        }
    }
}
