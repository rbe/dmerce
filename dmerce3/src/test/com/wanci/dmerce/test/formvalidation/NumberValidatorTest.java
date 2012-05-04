/*
 * Created on Oct 14, 2003
 */
package com.wanci.dmerce.test.formvalidation;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import com.wanci.dmerce.forms.FIELD;
import com.wanci.dmerce.forms.ObjectFactory;
import com.wanci.dmerce.formvalidation.NumberValidator;
import com.wanci.dmerce.taglib.form.FormField;

/**
 * @author mm
 * @version $Id: NumberValidatorTest.java,v 1.2 2004/03/09 16:51:25 rb Exp $
 */
public class NumberValidatorTest extends TestCase {

	private NumberValidator nv;
	private FormField formfield;
	private FormField formfield2;

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Constructor for NumberValidatorTest.
	 * @param arg0
	 */
	public NumberValidatorTest(String arg0) {
		super(arg0);
		try {
			ObjectFactory of = new ObjectFactory();
			FIELD field = of.createFIELD();
			field.setName("Nummerierung");
			field.setDescription("Durch nummerieren der Zeilen");
			field.setRequired(true);
			field.setType("number");
			formfield = new FormField(field, "Form1");
			
			FIELD field2 = of.createFIELD();
			field2.setName("Nummerierung");
			field2.setDescription("Durch nummerieren der Zeilen");
			field2.setRequired(true);
			field2.setType("string");
			formfield2 = new FormField(field2, "Form2");
			
			nv = new NumberValidator();
		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public void testIsResponsibleFor() {
		
		boolean verantwortlich = false;
		
		verantwortlich = nv.isResponsibleFor(formfield);
		assertTrue(verantwortlich);

		verantwortlich = nv.isResponsibleFor(formfield2);
		assertTrue(!verantwortlich);
	}

	public void testValidate() {
		
		boolean fehlerBekommen = false;
		
		formfield.setValue("3");
		fehlerBekommen = !nv.validate(formfield);
		assertTrue(!fehlerBekommen);
				
		formfield.setValue("");
		fehlerBekommen = !nv.validate(formfield);
		assertTrue(!fehlerBekommen);
		
		formfield.setValue("hdg");
		fehlerBekommen = !nv.validate(formfield);
		assertTrue(fehlerBekommen);
		
		formfield.setValue("3szrtz");
		fehlerBekommen = !nv.validate(formfield);
		assertTrue(fehlerBekommen);
	}
}