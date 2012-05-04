/*
 * Datei angelegt am 10.10.2003
 */
package com.wanci.dmerce.test;

import java.util.Iterator;

import javax.xml.bind.JAXBException;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.forms.FORM;
import com.wanci.dmerce.forms.Forms;
import com.wanci.dmerce.servlet.XmlDocumentCache;

/**
 * @author Masanori Fujita
 */
public class XmlDocumentCacheTest {

	public static void main(String[] args) throws JAXBException, DmerceException {
		Forms f = XmlDocumentCache.getInstance().getForms();
		Iterator it = f.getForm().iterator();
		while (it.hasNext()) {
			FORM form = (FORM) it.next();
			System.out.println(form.getName());			
		}
		System.out.println("ok");
	}
	
}
