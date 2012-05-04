//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.1-05/30/2003 05:06 AM(java_re)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2004.01.30 at 06:31:07 CET 
//


package com.wanci.dmerce.workflow.xmlbridge;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.wanci.dmerce.workflow.xmlbridge package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
public class ObjectFactory
    extends com.wanci.dmerce.workflow.xmlbridge.impl.runtime.DefaultJAXBContextImpl
{

    private static java.util.HashMap defaultImplementations = new java.util.HashMap();
    public final static java.lang.Class version = (com.wanci.dmerce.workflow.xmlbridge.impl.JAXBVersion.class);

    static {
        defaultImplementations.put("com.wanci.dmerce.workflow.xmlbridge.FIELDMAP", "com.wanci.dmerce.workflow.xmlbridge.impl.FIELDMAPImpl");
        defaultImplementations.put("com.wanci.dmerce.workflow.xmlbridge.WorkflowsType", "com.wanci.dmerce.workflow.xmlbridge.impl.WorkflowsTypeImpl");
        defaultImplementations.put("com.wanci.dmerce.workflow.xmlbridge.ACTION", "com.wanci.dmerce.workflow.xmlbridge.impl.ACTIONImpl");
        defaultImplementations.put("com.wanci.dmerce.workflow.xmlbridge.PAGE", "com.wanci.dmerce.workflow.xmlbridge.impl.PAGEImpl");
        defaultImplementations.put("com.wanci.dmerce.workflow.xmlbridge.CONDITION", "com.wanci.dmerce.workflow.xmlbridge.impl.CONDITIONImpl");
        defaultImplementations.put("com.wanci.dmerce.workflow.xmlbridge.FORMMAP", "com.wanci.dmerce.workflow.xmlbridge.impl.FORMMAPImpl");
        defaultImplementations.put("com.wanci.dmerce.workflow.xmlbridge.PARAMETERS", "com.wanci.dmerce.workflow.xmlbridge.impl.PARAMETERSImpl");
        defaultImplementations.put("com.wanci.dmerce.workflow.xmlbridge.TRANSITION", "com.wanci.dmerce.workflow.xmlbridge.impl.TRANSITIONImpl");
        defaultImplementations.put("com.wanci.dmerce.workflow.xmlbridge.WORKFLOW", "com.wanci.dmerce.workflow.xmlbridge.impl.WORKFLOWImpl");
        defaultImplementations.put("com.wanci.dmerce.workflow.xmlbridge.PARAMETER", "com.wanci.dmerce.workflow.xmlbridge.impl.PARAMETERImpl");
        defaultImplementations.put("com.wanci.dmerce.workflow.xmlbridge.Workflows", "com.wanci.dmerce.workflow.xmlbridge.impl.WorkflowsImpl");
        defaultImplementations.put("com.wanci.dmerce.workflow.xmlbridge.FORMFIELD", "com.wanci.dmerce.workflow.xmlbridge.impl.FORMFIELDImpl");
    }

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.wanci.dmerce.workflow.xmlbridge
     * 
     */
    public ObjectFactory() {
        super(new com.wanci.dmerce.workflow.xmlbridge.ObjectFactory.GrammarInfoImpl());
    }

    /**
     * Create an instance of the specified Java content interface.
     * 
     * @param javaContentInterface the Class object of the javacontent interface to instantiate
     * @return a new instance
     * @throws JAXBException if an error occurs
     */
    public java.lang.Object newInstance(java.lang.Class javaContentInterface)
        throws javax.xml.bind.JAXBException
    {
        return super.newInstance(javaContentInterface);
    }

    /**
     * Get the specified property. This method can only be
     * used to get provider specific properties.
     * Attempting to get an undefined property will result
     * in a PropertyException being thrown.
     * 
     * @param name the name of the property to retrieve
     * @return the value of the requested property
     * @throws PropertyException when there is an error retrieving the given property or value
     */
    public java.lang.Object getProperty(java.lang.String name)
        throws javax.xml.bind.PropertyException
    {
        return super.getProperty(name);
    }

    /**
     * Set the specified property. This method can only be
     * used to set provider specific properties.
     * Attempting to set an undefined property will result
     * in a PropertyException being thrown.
     * 
     * @param name the name of the property to retrieve
     * @param value the value of the property to be set
     * @throws PropertyException when there is an error processing the given property or value
     */
    public void setProperty(java.lang.String name, java.lang.Object value)
        throws javax.xml.bind.PropertyException
    {
        super.setProperty(name, value);
    }

    /**
     * Create an instance of FIELDMAP
     * 
     * @throws JAXBException if an error occurs
     */
    public com.wanci.dmerce.workflow.xmlbridge.FIELDMAP createFIELDMAP()
        throws javax.xml.bind.JAXBException
    {
        return new com.wanci.dmerce.workflow.xmlbridge.impl.FIELDMAPImpl();
    }

    /**
     * Create an instance of WorkflowsType
     * 
     * @throws JAXBException if an error occurs
     */
    public com.wanci.dmerce.workflow.xmlbridge.WorkflowsType createWorkflowsType()
        throws javax.xml.bind.JAXBException
    {
        return new com.wanci.dmerce.workflow.xmlbridge.impl.WorkflowsTypeImpl();
    }

    /**
     * Create an instance of ACTION
     * 
     * @throws JAXBException if an error occurs
     */
    public com.wanci.dmerce.workflow.xmlbridge.ACTION createACTION()
        throws javax.xml.bind.JAXBException
    {
        return new com.wanci.dmerce.workflow.xmlbridge.impl.ACTIONImpl();
    }

    /**
     * Create an instance of PAGE
     * 
     * @throws JAXBException if an error occurs
     */
    public com.wanci.dmerce.workflow.xmlbridge.PAGE createPAGE()
        throws javax.xml.bind.JAXBException
    {
        return new com.wanci.dmerce.workflow.xmlbridge.impl.PAGEImpl();
    }

    /**
     * Create an instance of CONDITION
     * 
     * @throws JAXBException if an error occurs
     */
    public com.wanci.dmerce.workflow.xmlbridge.CONDITION createCONDITION()
        throws javax.xml.bind.JAXBException
    {
        return new com.wanci.dmerce.workflow.xmlbridge.impl.CONDITIONImpl();
    }

    /**
     * Create an instance of FORMMAP
     * 
     * @throws JAXBException if an error occurs
     */
    public com.wanci.dmerce.workflow.xmlbridge.FORMMAP createFORMMAP()
        throws javax.xml.bind.JAXBException
    {
        return new com.wanci.dmerce.workflow.xmlbridge.impl.FORMMAPImpl();
    }

    /**
     * Create an instance of PARAMETERS
     * 
     * @throws JAXBException if an error occurs
     */
    public com.wanci.dmerce.workflow.xmlbridge.PARAMETERS createPARAMETERS()
        throws javax.xml.bind.JAXBException
    {
        return new com.wanci.dmerce.workflow.xmlbridge.impl.PARAMETERSImpl();
    }

    /**
     * Create an instance of TRANSITION
     * 
     * @throws JAXBException if an error occurs
     */
    public com.wanci.dmerce.workflow.xmlbridge.TRANSITION createTRANSITION()
        throws javax.xml.bind.JAXBException
    {
        return new com.wanci.dmerce.workflow.xmlbridge.impl.TRANSITIONImpl();
    }

    /**
     * Create an instance of WORKFLOW
     * 
     * @throws JAXBException if an error occurs
     */
    public com.wanci.dmerce.workflow.xmlbridge.WORKFLOW createWORKFLOW()
        throws javax.xml.bind.JAXBException
    {
        return new com.wanci.dmerce.workflow.xmlbridge.impl.WORKFLOWImpl();
    }

    /**
     * Create an instance of PARAMETER
     * 
     * @throws JAXBException if an error occurs
     */
    public com.wanci.dmerce.workflow.xmlbridge.PARAMETER createPARAMETER()
        throws javax.xml.bind.JAXBException
    {
        return new com.wanci.dmerce.workflow.xmlbridge.impl.PARAMETERImpl();
    }

    /**
     * Create an instance of Workflows
     * 
     * @throws JAXBException if an error occurs
     */
    public com.wanci.dmerce.workflow.xmlbridge.Workflows createWorkflows()
        throws javax.xml.bind.JAXBException
    {
        return new com.wanci.dmerce.workflow.xmlbridge.impl.WorkflowsImpl();
    }

    /**
     * Create an instance of FORMFIELD
     * 
     * @throws JAXBException if an error occurs
     */
    public com.wanci.dmerce.workflow.xmlbridge.FORMFIELD createFORMFIELD()
        throws javax.xml.bind.JAXBException
    {
        return new com.wanci.dmerce.workflow.xmlbridge.impl.FORMFIELDImpl();
    }

    private static class GrammarInfoImpl
        extends com.wanci.dmerce.workflow.xmlbridge.impl.runtime.AbstractGrammarInfoImpl
    {


        public java.lang.Class getDefaultImplementation(java.lang.Class javaContentInterface) {
            java.lang.Class c = null;
            try {
                c = java.lang.Class.forName(((java.lang.String) defaultImplementations.get(javaContentInterface.getName())));
            } catch (java.lang.Exception _x) {
                c = null;
            }
            return c;
        }

        public com.wanci.dmerce.workflow.xmlbridge.impl.runtime.UnmarshallingEventHandler createUnmarshaller(java.lang.String uri, java.lang.String local, com.wanci.dmerce.workflow.xmlbridge.impl.runtime.UnmarshallingContext context) {
            if (("workflows" == local)&&("http://www.1ci.de" == uri)) {
                return new com.wanci.dmerce.workflow.xmlbridge.impl.WorkflowsImpl().createUnmarshaller(context);
            }
            return null;
        }

        public java.lang.Class getRootElement(java.lang.String uri, java.lang.String local) {
            if (("workflows" == local)&&("http://www.1ci.de" == uri)) {
                return (com.wanci.dmerce.workflow.xmlbridge.impl.WorkflowsImpl.class);
            }
            return null;
        }

        public boolean recognize(java.lang.String uri, java.lang.String local) {
            if (("workflows" == local)&&("http://www.1ci.de" == uri)) {
                return true;
            }
            return false;
        }

        public java.lang.String[] getProbePoints() {
            return new java.lang.String[] {"http://www.1ci.de", "workflows"};
        }

    }

}