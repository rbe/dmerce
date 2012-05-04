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

/*
 * @(#)$Id: UnmarshallingContext.java,v 1.11 2004/03/09 15:34:00 mf Exp $
 */
package com.wanci.dmerce.forms.impl.runtime;

import javax.xml.bind.ValidationEvent;
import javax.xml.namespace.NamespaceContext;

import org.relaxng.datatype.ValidationContext;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.sun.xml.bind.unmarshaller.Tracer;

/**
 * Methods exposed by the unmarshalling coordinator object
 * to the generated code.
 * 
 * This interface will be implemented by the coordinator, which
 * converts whatever events (e.g., SAX) into unmarshalling events.
 *
 * <p>
 * Errors detected by the AbstractUnmarshallingEventHandlerImpl-derived classes should
 * be either thrown as {@link UnrepotedException} or reported through
 * the handleEvent method of this interface.
 *
 * @author
 *  <a href="mailto:kohsuke.kawaguchi@sun.com>Kohsuke KAWAGUCHI</a>
 */
public interface UnmarshallingContext extends
    ValidationContext, NamespaceContext
{
    /** Obtains a reference to the current grammar info. */
    GrammarInfo getGrammarInfo();
    
    
    
    /**
     * Pushes the current content handler into the stack
     * and registers the newly specified content handler so
     * that it can receive SAX events.
     * 
     * @param memento
     *      When this newly specified handler will be removed from the stack,
     *      the leaveChild event will be fired to the parent handler
     *      with this memento.
     */
    void pushContentHandler( UnmarshallingEventHandler handler, int memento );
    
    /**
     * Pops a content handler from the stack and registers
     * it as the current content handler.
     * 
     * <p>
     * This method will also fire the leaveChild event with the
     * associated memento.
     * 
     * @return
     *      The new content handler. This method DOES NOT return
     *      the previous content handler.
     */
    void popContentHandler() throws SAXException;
    
    /**
     * Gets the current handler.
     */
    UnmarshallingEventHandler getCurrentHandler();

// those two methods are defined in SAX ContentHandler
    /**
     * Adds a new namespace declaration.
     * This method should be called by the generated code.
     */
    void startPrefixMapping( String prefix, String namespaceUri );
    
    /**
     * Removes a namespace declaration.
     * This method should be called by the generated code.
     */
    void endPrefixMapping( String prefix );
    
    
    
    /**
     * Stores a new attribute set.
     * This method should be called by the generated code
     * when it "eats" an enterElement event.
     * 
     * @param collectText
     *      false if the context doesn't need to fire text events
     *      for texts inside this element. True otherwise. 
     */
    void pushAttributes( Attributes atts, boolean collectText );
    
    /**
     * Discards the previously stored attribute set.
     * This method should be called by the generated code
     * when it "eats" a leaveElement event.
     */
    void popAttributes();
    
    /**
     * Gets the index of the attribute with the specified name.
     * This is usually faster when you only need to test with
     * a simple name.
     * 
     * @return
     *      -1 if not found.
     */
    int getAttribute( String uri, String name );
    
    /**
     * Gets all the unconsumed attributes.
     * If you need to find attributes based on more complex filter,
     * you need to use this method.
     */
    Attributes getUnconsumedAttributes();
    
    /**
     * Fires an attribute event for the specified attribute,
     * and marks the attribute as "used".
     */
    void consumeAttribute( int idx ) throws SAXException;
    
    /**
     * Marks the attribute as "used" and return the value of the attribute.
     */
    String eatAttribute( int idx ) throws SAXException;
    
    /**
     * Adds a job that will be executed at the last of the unmarshalling.
     * This method is used to support ID/IDREF feature, but it can be used
     * for other purposes as well.
     * 
     * @param   job
     *      The run method of this object is called.
     */
    void addPatcher( Runnable job );
    // TODO: shall we use a modified version of Runnable so that
    // the patcher can throw JAXBException?
    
    /**
     * Adds the object which is currently being unmarshalled
     * to the ID table.
     * 
     * @return
     *      Returns the value passed as the parameter.
     *      This is a hack, but this makes it easier for ID
     *      transducer to do its job.
     */
    String addToIdTable( String id );
    // TODO: what shall we do if the ID is already declared?
    //
    // throwing an exception is one way. Overwriting the previous one
    // is another way. The latter allows us to process invalid documents,
    // while the former makes it impossible to handle them.
    //
    // I prefer to be flexible in terms of invalid document handling,
    // so chose not to throw an exception.
    //
    // I believe this is an implementation choice, not the spec issue.
    // -kk
    
    /**
     * Looks up the ID table and gets associated object.
     * 
     * @return
     *      If there is no object associated with the given id,
     *      this method returns null.
     */
    Object getObjectFromId( String id );
    // TODO: maybe we should throw UnmarshallingException
    // if we don't find ID.
    
    
    /**
     * Gets the current source location information.
     */
    Locator getLocator();
    
    /**
     * Reports an error to the user, and asks if s/he wants
     * to recover. If the canRecover flag is false, regardless
     * of the client instruction, an exception will be thrown.
     * 
     * Only if the flag is true and the user wants to recover from an error,
     * the method returns normally.
     * 
     * The thrown exception will be catched by the unmarshaller.
     */
    void handleEvent( ValidationEvent event, boolean canRecover ) throws SAXException;
    
    
// DBG
    /**
     * Gets a tracer object.
     * 
     * Tracer can be used to trace the unmarshalling behavior.
     * Note that to debug the unmarshalling process,
     * you have to configure XJC so that it will emit trace codes
     * in the unmarshaller.
     */
    Tracer getTracer();
}