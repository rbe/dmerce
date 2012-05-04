/*
 * Datei angelegt am 14.10.2003
 */
package com.wanci.dmerce.test.formvalidation;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import com.wanci.dmerce.forms.FIELD;
import com.wanci.dmerce.forms.ObjectFactory;
import com.wanci.dmerce.formvalidation.EmailValidator;
import com.wanci.dmerce.taglib.form.FormField;

/**
 * @author Manuel Murawski
 */
public class EmailValidatorTest extends TestCase {

	private FormField formfield;
	private FormField formfield2;

	private EmailValidator ev;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Constructor for EmailValidatorTest.
	 * @param arg0
	 */
	public EmailValidatorTest(String arg0) {
		super(arg0);
		try {
			ObjectFactory of = new ObjectFactory();
			FIELD field = of.createFIELD();
			field.setName("email");
			field.setDescription("Email-Adresse");
			field.setRequired(true);
			field.setType("email");
			formfield = new FormField(field, "Form1");
			
			FIELD field2 = of.createFIELD();
			field2.setName("email");
			field2.setDescription("Email-Adresse");
			field2.setRequired(true);
			field2.setType("string");
			formfield2 = new FormField(field2, "Form2");
			
			ev = new EmailValidator();
		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public void testIsResponsibleFor() {

		boolean fehlerBekommen = false;

		fehlerBekommen = ev.isResponsibleFor(formfield);
		assertTrue(fehlerBekommen);
		
		fehlerBekommen = ev.isResponsibleFor(formfield2);
		assertTrue(!fehlerBekommen);
	}

	public void testValidate() {

		boolean fehlerBekommen = false;

		formfield.setValue("m.murawski@1ci.de");
		fehlerBekommen = !ev.validate(formfield);
		assertTrue(!fehlerBekommen);

		formfield.setValue("m.murawski@1.de");
		fehlerBekommen = !ev.validate(formfield);
		assertTrue(fehlerBekommen);
		
		formfield.setValue("m.murawski@1c.de");
		fehlerBekommen = !ev.validate(formfield);
		assertTrue(!fehlerBekommen);
		
		formfield.setValue("m.murawski@1c.");
		fehlerBekommen = !ev.validate(formfield);
		assertTrue(fehlerBekommen);
		
		formfield.setValue("@1c.de");
		fehlerBekommen = !ev.validate(formfield);
		assertTrue(fehlerBekommen);
		
		formfield.setValue("m@1c.de");
		fehlerBekommen = !ev.validate(formfield);
		assertTrue(!fehlerBekommen);
		
		formfield.setValue("m@1cde");
		fehlerBekommen = !ev.validate(formfield);
		assertTrue(fehlerBekommen);
		
		formfield.setValue("m@.de");
		fehlerBekommen = !ev.validate(formfield);
		assertTrue(fehlerBekommen);
		
		formfield.setValue("");
		fehlerBekommen = !ev.validate(formfield);
		assertTrue(fehlerBekommen);

		formfield.setValue("@");
		fehlerBekommen = !ev.validate(formfield);
		assertTrue(fehlerBekommen);

		formfield.setValue("mAT1c.de");
		fehlerBekommen = !ev.validate(formfield);
		assertTrue(fehlerBekommen);
	}
}