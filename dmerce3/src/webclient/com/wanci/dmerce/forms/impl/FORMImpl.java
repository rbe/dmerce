//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.1-05/30/2003 05:06 AM(java_re)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2004.03.09 at 04:45:04 CET 
//


package com.wanci.dmerce.forms.impl;

public class FORMImpl implements com.wanci.dmerce.forms.FORM, com.sun.xml.bind.JAXBObject, com.wanci.dmerce.forms.impl.runtime.UnmarshallableObject, com.wanci.dmerce.forms.impl.runtime.XMLSerializable, com.wanci.dmerce.forms.impl.runtime.ValidatableObject
{

    protected java.lang.String _Description;
    protected java.lang.String _Formversion;
    protected com.sun.xml.bind.util.ListImpl _Field = new com.sun.xml.bind.util.ListImpl(new java.util.ArrayList());
    protected java.lang.String _Name;
    protected java.lang.String _Id;
    public final static java.lang.Class version = (com.wanci.dmerce.forms.impl.JAXBVersion.class);
    private static com.sun.msv.grammar.Grammar schemaFragment;

    private final static java.lang.Class PRIMARY_INTERFACE_CLASS() {
        return (com.wanci.dmerce.forms.FORM.class);
    }

    public java.lang.String getDescription() {
        return _Description;
    }

    public void setDescription(java.lang.String value) {
        _Description = value;
    }

    public java.lang.String getFormversion() {
        return _Formversion;
    }

    public void setFormversion(java.lang.String value) {
        _Formversion = value;
    }

    public java.util.List getField() {
        return _Field;
    }

    public java.lang.String getName() {
        return _Name;
    }

    public void setName(java.lang.String value) {
        _Name = value;
    }

    public java.lang.String getId() {
        return _Id;
    }

    public void setId(java.lang.String value) {
        _Id = value;
    }

    public com.wanci.dmerce.forms.impl.runtime.UnmarshallingEventHandler createUnmarshaller(com.wanci.dmerce.forms.impl.runtime.UnmarshallingContext context) {
        return new com.wanci.dmerce.forms.impl.FORMImpl.Unmarshaller(context);
    }

    public void serializeElementBody(com.wanci.dmerce.forms.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        int idx3 = 0;
        final int len3 = _Field.size();
        context.startElement("http://www.1ci.de", "name");
        context.endNamespaceDecls();
        context.endAttributes();
        try {
            context.text(((java.lang.String) _Name));
        } catch (java.lang.Exception e) {
            com.wanci.dmerce.forms.impl.runtime.Util.handlePrintConversionException(this, e, context);
        }
        context.endElement();
        context.startElement("http://www.1ci.de", "description");
        context.endNamespaceDecls();
        context.endAttributes();
        try {
            context.text(((java.lang.String) _Description));
        } catch (java.lang.Exception e) {
            com.wanci.dmerce.forms.impl.runtime.Util.handlePrintConversionException(this, e, context);
        }
        context.endElement();
        if (_Formversion!= null) {
            context.startElement("http://www.1ci.de", "formversion");
            context.endNamespaceDecls();
            context.endAttributes();
            try {
                context.text(((java.lang.String) _Formversion));
            } catch (java.lang.Exception e) {
                com.wanci.dmerce.forms.impl.runtime.Util.handlePrintConversionException(this, e, context);
            }
            context.endElement();
        }
        while (idx3 != len3) {
            context.startElement("http://www.1ci.de", "field");
            int idx_6 = idx3;
            context.childAsURIs(((com.sun.xml.bind.JAXBObject) _Field.get(idx_6 ++)));
            context.endNamespaceDecls();
            int idx_7 = idx3;
            context.childAsAttributes(((com.sun.xml.bind.JAXBObject) _Field.get(idx_7 ++)));
            context.endAttributes();
            context.childAsElementBody(((com.sun.xml.bind.JAXBObject) _Field.get(idx3 ++)));
            context.endElement();
        }
    }

    public void serializeAttributes(com.wanci.dmerce.forms.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        int idx3 = 0;
        final int len3 = _Field.size();
        if (_Id!= null) {
            context.startAttribute("", "id");
            try {
                context.text(((java.lang.String) _Id));
            } catch (java.lang.Exception e) {
                com.wanci.dmerce.forms.impl.runtime.Util.handlePrintConversionException(this, e, context);
            }
            context.endAttribute();
        }
    }

    public void serializeAttributeBody(com.wanci.dmerce.forms.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        int idx3 = 0;
        final int len3 = _Field.size();
        context.startElement("http://www.1ci.de", "name");
        context.endNamespaceDecls();
        context.endAttributes();
        try {
            context.text(((java.lang.String) _Name));
        } catch (java.lang.Exception e) {
            com.wanci.dmerce.forms.impl.runtime.Util.handlePrintConversionException(this, e, context);
        }
        context.endElement();
        context.startElement("http://www.1ci.de", "description");
        context.endNamespaceDecls();
        context.endAttributes();
        try {
            context.text(((java.lang.String) _Description));
        } catch (java.lang.Exception e) {
            com.wanci.dmerce.forms.impl.runtime.Util.handlePrintConversionException(this, e, context);
        }
        context.endElement();
        if (_Formversion!= null) {
            context.startElement("http://www.1ci.de", "formversion");
            context.endNamespaceDecls();
            context.endAttributes();
            try {
                context.text(((java.lang.String) _Formversion));
            } catch (java.lang.Exception e) {
                com.wanci.dmerce.forms.impl.runtime.Util.handlePrintConversionException(this, e, context);
            }
            context.endElement();
        }
        while (idx3 != len3) {
            context.startElement("http://www.1ci.de", "field");
            int idx_6 = idx3;
            context.childAsURIs(((com.sun.xml.bind.JAXBObject) _Field.get(idx_6 ++)));
            context.endNamespaceDecls();
            int idx_7 = idx3;
            context.childAsAttributes(((com.sun.xml.bind.JAXBObject) _Field.get(idx_7 ++)));
            context.endAttributes();
            context.childAsElementBody(((com.sun.xml.bind.JAXBObject) _Field.get(idx3 ++)));
            context.endElement();
        }
    }

    public void serializeURIs(com.wanci.dmerce.forms.impl.runtime.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        int idx3 = 0;
        final int len3 = _Field.size();
    }

    public java.lang.Class getPrimaryInterface() {
        return (com.wanci.dmerce.forms.FORM.class);
    }

    public com.sun.msv.verifier.DocumentDeclaration createRawValidator() {
        if (schemaFragment == null) {
            schemaFragment = com.sun.xml.bind.validator.SchemaDeserializer.deserialize((
 "\u00ac\u00ed\u0000\u0005sr\u0000\u001fcom.sun.msv.grammar.SequenceExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\u001dcom.su"
+"n.msv.grammar.BinaryExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\u0004exp1t\u0000 Lcom/sun/msv/gra"
+"mmar/Expression;L\u0000\u0004exp2q\u0000~\u0000\u0002xr\u0000\u001ecom.sun.msv.grammar.Expressi"
+"on\u00f8\u0018\u0082\u00e8N5~O\u0002\u0000\u0003I\u0000\u000ecachedHashCodeL\u0000\u0013epsilonReducibilityt\u0000\u0013Ljava"
+"/lang/Boolean;L\u0000\u000bexpandedExpq\u0000~\u0000\u0002xp\u0005\u0005\u0012Eppsq\u0000~\u0000\u0000\u0003\u0082F\u00fbppsq\u0000~\u0000\u0000\u0001"
+"\u00d6\u0013Bppsq\u0000~\u0000\u0000\u00019b#ppsr\u0000\'com.sun.msv.grammar.trex.ElementPattern"
+"\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001L\u0000\tnameClasst\u0000\u001fLcom/sun/msv/grammar/NameClass;xr\u0000"
+"\u001ecom.sun.msv.grammar.ElementExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002Z\u0000\u001aignoreUndeclare"
+"dAttributesL\u0000\fcontentModelq\u0000~\u0000\u0002xq\u0000~\u0000\u0003\u0000\u009c\u00b1\u000fpp\u0000sr\u0000\u001bcom.sun.msv."
+"grammar.DataExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0003L\u0000\u0002dtt\u0000\u001fLorg/relaxng/datatype/Data"
+"type;L\u0000\u0006exceptq\u0000~\u0000\u0002L\u0000\u0004namet\u0000\u001dLcom/sun/msv/util/StringPair;xq"
+"\u0000~\u0000\u0003\u0000\u009c\u00b1\u0004ppsr\u0000#com.sun.msv.datatype.xsd.StringType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001"
+"Z\u0000\risAlwaysValidxr\u0000*com.sun.msv.datatype.xsd.BuiltinAtomicTy"
+"pe\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000%com.sun.msv.datatype.xsd.ConcreteType\u0000\u0000\u0000\u0000\u0000\u0000"
+"\u0000\u0001\u0002\u0000\u0000xr\u0000\'com.sun.msv.datatype.xsd.XSDatatypeImpl\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0003L"
+"\u0000\fnamespaceUrit\u0000\u0012Ljava/lang/String;L\u0000\btypeNameq\u0000~\u0000\u0015L\u0000\nwhiteS"
+"pacet\u0000.Lcom/sun/msv/datatype/xsd/WhiteSpaceProcessor;xpt\u0000 ht"
+"tp://www.w3.org/2001/XMLSchemat\u0000\u0006stringsr\u00005com.sun.msv.datat"
+"ype.xsd.WhiteSpaceProcessor$Preserve\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000,com.sun.m"
+"sv.datatype.xsd.WhiteSpaceProcessor\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xp\u0001sr\u00000com.sun"
+".msv.grammar.Expression$NullSetExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0003\u0000\u0000"
+"\u0000\nppsr\u0000\u001bcom.sun.msv.util.StringPair\u00d0t\u001ejB\u008f\u008d\u00a0\u0002\u0000\u0002L\u0000\tlocalNameq\u0000"
+"~\u0000\u0015L\u0000\fnamespaceURIq\u0000~\u0000\u0015xpq\u0000~\u0000\u0019q\u0000~\u0000\u0018sr\u0000#com.sun.msv.grammar.S"
+"impleNameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\tlocalNameq\u0000~\u0000\u0015L\u0000\fnamespaceURIq\u0000~"
+"\u0000\u0015xr\u0000\u001dcom.sun.msv.grammar.NameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xpt\u0000\u0004namet\u0000\u0011ht"
+"tp://www.1ci.desq\u0000~\u0000\t\u0000\u009c\u00b1\u000fpp\u0000q\u0000~\u0000\u0010sq\u0000~\u0000!t\u0000\u000bdescriptionq\u0000~\u0000%sr"
+"\u0000\u001dcom.sun.msv.grammar.ChoiceExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0001\u0000\u009c\u00b1\u001appsq\u0000~\u0000\t"
+"\u0000\u009c\u00b1\u000fsr\u0000\u0011java.lang.Boolean\u00cd r\u0080\u00d5\u009c\u00fa\u00ee\u0002\u0000\u0001Z\u0000\u0005valuexp\u0000p\u0000q\u0000~\u0000\u0010sq\u0000~\u0000!"
+"t\u0000\u000bformversionq\u0000~\u0000%sr\u00000com.sun.msv.grammar.Expression$Epsilo"
+"nExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0003\u0000\u0000\u0000\tsq\u0000~\u0000,\u0001psq\u0000~\u0000)\u0001\u00ac3\u00b4ppsr\u0000 com."
+"sun.msv.grammar.OneOrMoreExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\u001ccom.sun.msv.gramm"
+"ar.UnaryExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001L\u0000\u0003expq\u0000~\u0000\u0002xq\u0000~\u0000\u0003\u0001\u00ac3\u00a9q\u0000~\u0000-psq\u0000~\u0000\t\u0001\u00ac3\u00a6q"
+"\u0000~\u0000-p\u0000sq\u0000~\u0000\t\u0001\u00ac3\u009bpp\u0000sq\u0000~\u0000)\u0001\u00ac3\u0090ppsq\u0000~\u00004\u0001\u00ac3\u0085q\u0000~\u0000-psr\u0000 com.sun.m"
+"sv.grammar.AttributeExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\u0003expq\u0000~\u0000\u0002L\u0000\tnameClassq\u0000~"
+"\u0000\nxq\u0000~\u0000\u0003\u0001\u00ac3\u0082q\u0000~\u0000-psr\u00002com.sun.msv.grammar.Expression$AnyStri"
+"ngExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0003\u0000\u0000\u0000\bq\u0000~\u00002psr\u0000 com.sun.msv.gramm"
+"ar.AnyNameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\"q\u0000~\u00001sq\u0000~\u0000!t\u0000\u001ccom.wanci.dmer"
+"ce.forms.FIELDt\u0000+http://java.sun.com/jaxb/xjc/dummy-elements"
+"sq\u0000~\u0000!t\u0000\u0005fieldq\u0000~\u0000%q\u0000~\u00001sq\u0000~\u0000)\u0001\u0082\u00cbEppsq\u0000~\u0000;\u0001\u0082\u00cb:q\u0000~\u0000-pq\u0000~\u0000\u0010sq\u0000"
+"~\u0000!t\u0000\u0002idt\u0000\u0000q\u0000~\u00001sr\u0000\"com.sun.msv.grammar.ExpressionPool\u0000\u0000\u0000\u0000\u0000\u0000"
+"\u0000\u0001\u0002\u0000\u0001L\u0000\bexpTablet\u0000/Lcom/sun/msv/grammar/ExpressionPool$Close"
+"dHash;xpsr\u0000-com.sun.msv.grammar.ExpressionPool$ClosedHash\u00d7j\u00d0"
+"N\u00ef\u00e8\u00ed\u001c\u0002\u0000\u0004I\u0000\u0005countI\u0000\tthresholdL\u0000\u0006parentq\u0000~\u0000L[\u0000\u0005tablet\u0000![Lcom/s"
+"un/msv/grammar/Expression;xp\u0000\u0000\u0000\n\u0000\u0000\u00009pur\u0000![Lcom.sun.msv.gramm"
+"ar.Expression;\u00d68D\u00c3]\u00ad\u00a7\n\u0002\u0000\u0000xp\u0000\u0000\u0000\u00bfppppppppppppppppppppppppppppp"
+"pq\u0000~\u0000*ppppppppppppq\u0000~\u0000\bq\u0000~\u0000\u0005pppppppq\u0000~\u0000\u0006pppppppppppppppppppp"
+"pppppq\u0000~\u0000\u0007ppppppppppppppppppppppppppppppppppq\u0000~\u0000:ppppppppppq"
+"\u0000~\u00009ppppppppppppppppppppppppq\u0000~\u00006ppppppppppq\u0000~\u00003pppppppppppp"
+"ppppq\u0000~\u0000Fppppppppppppp"));
        }
        return new com.sun.msv.verifier.regexp.REDocumentDeclaration(schemaFragment);
    }

    public class Unmarshaller
        extends com.wanci.dmerce.forms.impl.runtime.AbstractUnmarshallingEventHandlerImpl
    {


        public Unmarshaller(com.wanci.dmerce.forms.impl.runtime.UnmarshallingContext context) {
            super(context, "----------------");
        }

        protected Unmarshaller(com.wanci.dmerce.forms.impl.runtime.UnmarshallingContext context, int startState) {
            this(context);
            state = startState;
        }

        public java.lang.Object owner() {
            return com.wanci.dmerce.forms.impl.FORMImpl.this;
        }

        public void enterElement(java.lang.String ___uri, java.lang.String ___local, java.lang.String ___qname, org.xml.sax.Attributes __atts)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  13 :
                        attIdx = context.getAttribute("", "name");
                        if (attIdx >= 0) {
                            context.consumeAttribute(attIdx);
                            context.getCurrentHandler().enterElement(___uri, ___local, ___qname, __atts);
                            return ;
                        }
                        break;
                    case  15 :
                        if (("field" == ___local)&&("http://www.1ci.de" == ___uri)) {
                            context.pushAttributes(__atts, false);
                            state = 13;
                            return ;
                        }
                        revertToParentFromEnterElement(___uri, ___local, ___qname, __atts);
                        return ;
                    case  0 :
                        attIdx = context.getAttribute("", "id");
                        if (attIdx >= 0) {
                            final java.lang.String v = context.eatAttribute(attIdx);
                            eatText0(v);
                            state = 3;
                            continue outer;
                        }
                        state = 3;
                        continue outer;
                    case  6 :
                        if (("description" == ___local)&&("http://www.1ci.de" == ___uri)) {
                            context.pushAttributes(__atts, true);
                            state = 7;
                            return ;
                        }
                        break;
                    case  9 :
                        if (("formversion" == ___local)&&("http://www.1ci.de" == ___uri)) {
                            context.pushAttributes(__atts, true);
                            state = 10;
                            return ;
                        }
                        state = 12;
                        continue outer;
                    case  12 :
                        if (("field" == ___local)&&("http://www.1ci.de" == ___uri)) {
                            context.pushAttributes(__atts, false);
                            state = 13;
                            return ;
                        }
                        state = 15;
                        continue outer;
                    case  3 :
                        if (("name" == ___local)&&("http://www.1ci.de" == ___uri)) {
                            context.pushAttributes(__atts, true);
                            state = 4;
                            return ;
                        }
                        break;
                }
                super.enterElement(___uri, ___local, ___qname, __atts);
                break;
            }
        }

        private void eatText0(final java.lang.String value)
            throws org.xml.sax.SAXException
        {
            try {
                _Id = value;
            } catch (java.lang.Exception e) {
                handleParseConversionException(e);
            }
        }

        public void leaveElement(java.lang.String ___uri, java.lang.String ___local, java.lang.String ___qname)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  14 :
                        if (("field" == ___local)&&("http://www.1ci.de" == ___uri)) {
                            context.popAttributes();
                            state = 15;
                            return ;
                        }
                        break;
                    case  13 :
                        attIdx = context.getAttribute("", "name");
                        if (attIdx >= 0) {
                            context.consumeAttribute(attIdx);
                            context.getCurrentHandler().leaveElement(___uri, ___local, ___qname);
                            return ;
                        }
                        break;
                    case  15 :
                        revertToParentFromLeaveElement(___uri, ___local, ___qname);
                        return ;
                    case  0 :
                        attIdx = context.getAttribute("", "id");
                        if (attIdx >= 0) {
                            final java.lang.String v = context.eatAttribute(attIdx);
                            eatText0(v);
                            state = 3;
                            continue outer;
                        }
                        state = 3;
                        continue outer;
                    case  8 :
                        if (("description" == ___local)&&("http://www.1ci.de" == ___uri)) {
                            context.popAttributes();
                            state = 9;
                            return ;
                        }
                        break;
                    case  5 :
                        if (("name" == ___local)&&("http://www.1ci.de" == ___uri)) {
                            context.popAttributes();
                            state = 6;
                            return ;
                        }
                        break;
                    case  9 :
                        state = 12;
                        continue outer;
                    case  12 :
                        state = 15;
                        continue outer;
                    case  11 :
                        if (("formversion" == ___local)&&("http://www.1ci.de" == ___uri)) {
                            context.popAttributes();
                            state = 12;
                            return ;
                        }
                        break;
                }
                super.leaveElement(___uri, ___local, ___qname);
                break;
            }
        }

        public void enterAttribute(java.lang.String ___uri, java.lang.String ___local, java.lang.String ___qname)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  13 :
                        if (("name" == ___local)&&("" == ___uri)) {
                            _Field.add(((com.wanci.dmerce.forms.impl.FIELDImpl) spawnChildFromEnterAttribute((com.wanci.dmerce.forms.impl.FIELDImpl.class), 14, ___uri, ___local, ___qname)));
                            return ;
                        }
                        break;
                    case  15 :
                        revertToParentFromEnterAttribute(___uri, ___local, ___qname);
                        return ;
                    case  0 :
                        if (("id" == ___local)&&("" == ___uri)) {
                            state = 1;
                            return ;
                        }
                        state = 3;
                        continue outer;
                    case  9 :
                        state = 12;
                        continue outer;
                    case  12 :
                        state = 15;
                        continue outer;
                }
                super.enterAttribute(___uri, ___local, ___qname);
                break;
            }
        }

        public void leaveAttribute(java.lang.String ___uri, java.lang.String ___local, java.lang.String ___qname)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                switch (state) {
                    case  13 :
                        attIdx = context.getAttribute("", "name");
                        if (attIdx >= 0) {
                            context.consumeAttribute(attIdx);
                            context.getCurrentHandler().leaveAttribute(___uri, ___local, ___qname);
                            return ;
                        }
                        break;
                    case  15 :
                        revertToParentFromLeaveAttribute(___uri, ___local, ___qname);
                        return ;
                    case  0 :
                        attIdx = context.getAttribute("", "id");
                        if (attIdx >= 0) {
                            final java.lang.String v = context.eatAttribute(attIdx);
                            eatText0(v);
                            state = 3;
                            continue outer;
                        }
                        state = 3;
                        continue outer;
                    case  2 :
                        if (("id" == ___local)&&("" == ___uri)) {
                            state = 3;
                            return ;
                        }
                        break;
                    case  9 :
                        state = 12;
                        continue outer;
                    case  12 :
                        state = 15;
                        continue outer;
                }
                super.leaveAttribute(___uri, ___local, ___qname);
                break;
            }
        }

        public void handleText(final java.lang.String value)
            throws org.xml.sax.SAXException
        {
            int attIdx;
            outer:
            while (true) {
                try {
                    switch (state) {
                        case  10 :
                            eatText1(value);
                            state = 11;
                            return ;
                        case  13 :
                            attIdx = context.getAttribute("", "name");
                            if (attIdx >= 0) {
                                context.consumeAttribute(attIdx);
                                context.getCurrentHandler().text(value);
                                return ;
                            }
                            break;
                        case  15 :
                            revertToParentFromText(value);
                            return ;
                        case  4 :
                            eatText2(value);
                            state = 5;
                            return ;
                        case  0 :
                            attIdx = context.getAttribute("", "id");
                            if (attIdx >= 0) {
                                final java.lang.String v = context.eatAttribute(attIdx);
                                eatText0(v);
                                state = 3;
                                continue outer;
                            }
                            state = 3;
                            continue outer;
                        case  7 :
                            eatText3(value);
                            state = 8;
                            return ;
                        case  9 :
                            state = 12;
                            continue outer;
                        case  12 :
                            state = 15;
                            continue outer;
                        case  1 :
                            eatText0(value);
                            state = 2;
                            return ;
                    }
                } catch (java.lang.RuntimeException e) {
                    handleUnexpectedTextException(value, e);
                }
                break;
            }
        }

        private void eatText1(final java.lang.String value)
            throws org.xml.sax.SAXException
        {
            try {
                _Formversion = value;
            } catch (java.lang.Exception e) {
                handleParseConversionException(e);
            }
        }

        private void eatText2(final java.lang.String value)
            throws org.xml.sax.SAXException
        {
            try {
                _Name = value;
            } catch (java.lang.Exception e) {
                handleParseConversionException(e);
            }
        }

        private void eatText3(final java.lang.String value)
            throws org.xml.sax.SAXException
        {
            try {
                _Description = value;
            } catch (java.lang.Exception e) {
                handleParseConversionException(e);
            }
        }

    }

}
