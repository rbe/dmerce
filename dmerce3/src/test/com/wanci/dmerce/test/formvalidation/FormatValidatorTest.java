/*
 * Created on Oct 14, 2003
 */
package com.wanci.dmerce.test.formvalidation;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import com.wanci.dmerce.forms.FIELD;
import com.wanci.dmerce.forms.ObjectFactory;
import com.wanci.dmerce.formvalidation.FormatValidator;
import com.wanci.dmerce.taglib.form.FormField;

/**
 * @author mm
 * @version $Id: FormatValidatorTest.java,v 1.2 2004/03/09 16:51:25 rb Exp $
 */
public class FormatValidatorTest extends TestCase {

	private FormatValidator fv;
	private FormField formfield;
	private FormField formfield2;
	private FormField formfield3;
	private FormField formfield4;
	
	/**
	 * Methode, um Sachen vor jedem Test neu zu initialisieren
	 * Erfüllt 
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Constructor for FormatValidatorTest.
	 * @param arg0
	 */
	public FormatValidatorTest(String arg0) {
		super(arg0);
		try {
			ObjectFactory of = new ObjectFactory();
			
			FIELD field = of.createFIELD();
			field.setName("Email");
			field.setDescription("Email-Adresse");
			field.setRequired(true);
			field.setType("format");
			formfield = new FormField(field, "Form1");
			formfield.getConstraints().put("format", ".+@..+\\...+");
			
			FIELD field2 = of.createFIELD();
			field2.setName("Email");
			field2.setDescription("Email-Adresse");
			field2.setRequired(true);
			field2.setType("string");
			formfield2 = new FormField(field2, "Form2");
			formfield2.getConstraints().put("format", ".+@..+\\...+");
			
			FIELD field3 = of.createFIELD();
			field3.setName("Email");
			field3.setDescription("Email-Adresse");
			field3.setRequired(false);
			field3.setType("string");
			formfield3 = new FormField(field3, "Form3");
			formfield3.getConstraints().put("format", ".+@..+\\...+");
			

			FIELD field4 = of.createFIELD();
			field4.setName("Email");
			field4.setDescription("Email-Adresse");
			field4.setRequired(true);
			field4.setType("");
			formfield4 = new FormField(field4, "Form4");
			
			fv = new FormatValidator();
		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public void testIsResponsibleFor() {
		
		boolean zustaendig = false;
		
		zustaendig = fv.isResponsibleFor(formfield);
		assertTrue(!zustaendig);
		
		zustaendig = fv.isResponsibleFor(formfield2);
		assertTrue(!zustaendig);
		
		zustaendig = fv.isResponsibleFor(formfield3);
		assertTrue(!zustaendig);
		
		formfield3.setValue("klasghiheag");
		zustaendig = fv.isResponsibleFor(formfield3);
		assertTrue(zustaendig);
		
		zustaendig = fv.isResponsibleFor(formfield4);
		assertTrue(!zustaendig);
	}

	public void testValidate() {
		
		boolean fehlerBekommen = false;
		
		formfield.setValue("");
		fehlerBekommen = fv.validate(formfield);
		assertTrue(!fehlerBekommen);
		
		formfield.setValue("lkahg oihign ieg");
		fehlerBekommen = fv.validate(formfield);
		assertTrue(!fehlerBekommen);
		
		formfield2.setValue("");
		fehlerBekommen = !fv.validate(formfield2);
		assertTrue(fehlerBekommen);
		
		formfield2.setValue("agkenbagivban hra");
		fehlerBekommen = !fv.validate(formfield2);
		assertTrue(fehlerBekommen);

		formfield3.setValue("");
		fehlerBekommen = !fv.validate(formfield2);
		assertTrue(fehlerBekommen);
		
		formfield3.setValue("agkenbagivban hra");
		fehlerBekommen = !fv.validate(formfield2);
		assertTrue(fehlerBekommen);

		formfield3.setValue("");
		fehlerBekommen = !fv.validate(formfield2);
		assertTrue(fehlerBekommen);
		
		formfield3.setValue("");
		fehlerBekommen = !fv.validate(formfield2);
		assertTrue(fehlerBekommen);
	}
}