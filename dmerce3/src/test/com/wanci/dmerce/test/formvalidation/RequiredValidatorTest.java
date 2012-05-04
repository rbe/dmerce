/*
 * Created on Oct 14, 2003
 */
package com.wanci.dmerce.test.formvalidation;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import com.wanci.dmerce.forms.FIELD;
import com.wanci.dmerce.forms.ObjectFactory;
import com.wanci.dmerce.formvalidation.RequiredValidator;
import com.wanci.dmerce.taglib.form.FormField;

/**
 * @author mm
 * @version $Id: RequiredValidatorTest.java,v 1.2 2004/03/09 16:51:25 rb Exp $
 */
public class RequiredValidatorTest extends TestCase {

	private FormField formfield;
	private FormField formfield2;
	private FormField formfield3;
	
	private RequiredValidator rv;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Constructor for RequiredValidatorTest.
	 * @param arg0
	 */
	public RequiredValidatorTest(String arg0) {
		super(arg0);
		try {
			ObjectFactory of = new ObjectFactory();

			FIELD field = of.createFIELD();
			field.setName("Email");
			field.setDescription("Email-Adresse");
			field.setRequired(true);
			field.setType("string");
			formfield = new FormField(field, "Form1");
			formfield.getConstraints().put("format", ".+@..+\\...+");
			
			FIELD field2 = of.createFIELD();
			field2.setName("Email");
			field2.setDescription("Email-Adresse");
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
			
			rv = new RequiredValidator();
		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public void testIsResponsibleFor() {
		boolean benoetigt = false;

		benoetigt = rv.isResponsibleFor(formfield);
		assertTrue(benoetigt);
		
		benoetigt = rv.isResponsibleFor(formfield2);
		assertTrue(!benoetigt);
		
		benoetigt = rv.isResponsibleFor(formfield3);
		assertTrue(!benoetigt);
	}

	public void testValidate() {
		boolean fehlerBekommen = false;

		fehlerBekommen = rv.validate(formfield);
		assertTrue(!fehlerBekommen);

		formfield.setValue("");
		fehlerBekommen = !rv.validate(formfield);
		assertTrue(fehlerBekommen);	
	}
}