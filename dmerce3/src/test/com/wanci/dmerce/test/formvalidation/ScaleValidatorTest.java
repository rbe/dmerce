/*
 * Created on Oct 20, 2003
 */
package com.wanci.dmerce.test.formvalidation;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import com.wanci.dmerce.forms.FIELD;
import com.wanci.dmerce.forms.ObjectFactory;
import com.wanci.dmerce.formvalidation.ScaleValidator;
import com.wanci.dmerce.taglib.form.FormField;

/**
 * @author mm
 * @version $Id: ScaleValidatorTest.java,v 1.2 2004/03/09 16:51:25 rb Exp $
 */
public class ScaleValidatorTest extends TestCase {

	private FormField formfield;
	private FormField formfield2;
	
	private ScaleValidator sv;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Constructor for ScaleValidatorTest.
	 * @param arg0
	 */
	public ScaleValidatorTest(String arg0) {
		super(arg0);
		try {
			ObjectFactory of = new ObjectFactory();
			FIELD field = of.createFIELD();
			field.setName("Skalierung");
			field.setDescription("Skalierung der Nachkommastellen");
			field.setRequired(true);
			field.setType("string");
			formfield = new FormField(field, "Form1");
			formfield.getConstraints().put("scale", "5");
			
			FIELD field2 = of.createFIELD();
			field2.setName("Skalierung");
			field2.setDescription("Skalierung der Nachkommastellen");
			field2.setRequired(true);
			field2.setType("scale");
			formfield2 = new FormField(field2, "Form2");
			formfield.getConstraints().put("number", "6,12345");
			
			sv = new ScaleValidator();
		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public void testIsResponsibleFor() {
		
		boolean verantwortlich = false;
		
		verantwortlich = sv.isResponsibleFor(formfield);
		assertTrue(verantwortlich);
		
		verantwortlich = sv.isResponsibleFor(formfield2);
		assertTrue(!verantwortlich);		
	}

	public void testValidate() {
		
		boolean fehlerBekommen = false;
		
		formfield.setValue("6,12345");
		fehlerBekommen = !sv.validate(formfield);
		assertTrue(!fehlerBekommen);

		formfield.setValue("6,1234");
		fehlerBekommen = !sv.validate(formfield);
		assertTrue(!fehlerBekommen);

		formfield.setValue("6,123463");
		fehlerBekommen = !sv.validate(formfield);
		assertTrue(fehlerBekommen);
		
		formfield.setValue("6,12");
		fehlerBekommen = !sv.validate(formfield);
		assertTrue(!fehlerBekommen);

		formfield.setValue("6,123");
		fehlerBekommen = !sv.validate(formfield);
		assertTrue(!fehlerBekommen);

		formfield.setValue("6");
		fehlerBekommen = !sv.validate(formfield);
		assertTrue(!fehlerBekommen);

		formfield.setValue("");
		fehlerBekommen = sv.validate(formfield);
		assertTrue(fehlerBekommen);
		
		formfield.setValue("6.12345");
		fehlerBekommen = !sv.validate(formfield);
		assertTrue(!fehlerBekommen);

		formfield.setValue("12345");
		fehlerBekommen = !sv.validate(formfield);
		assertTrue(!fehlerBekommen);


	}
}