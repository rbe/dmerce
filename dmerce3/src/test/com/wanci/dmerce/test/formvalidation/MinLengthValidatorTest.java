/*
 * Created on Oct 14, 2003
 */
package com.wanci.dmerce.test.formvalidation;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import com.wanci.dmerce.forms.FIELD;
import com.wanci.dmerce.forms.ObjectFactory;
import com.wanci.dmerce.formvalidation.MinLengthValidator;
import com.wanci.dmerce.taglib.form.FormField;

/**
 * @author mm
 * @version $Id: MinLengthValidatorTest.java,v 1.2 2004/03/09 16:51:25 rb Exp $
 */
public class MinLengthValidatorTest extends TestCase {

	private FormField formfield;
	private FormField formfield2;
	
	private MinLengthValidator mlv;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Constructor for MinLengthValidatorTest.
	 * @param arg0
	 */
	public MinLengthValidatorTest(String arg0) {
		super(arg0);
		try {
			ObjectFactory of = new ObjectFactory();
			
			FIELD field = of.createFIELD();
			field.setName("Email");
			field.setDescription("Email-Adresse");
			field.setRequired(true);
			field.setType("string");
			formfield = new FormField(field, "Form1");
			formfield.getConstraints().put("minlength", "6");
			
			FIELD field2 = of.createFIELD();
			field2.setName("Email");
			field2.setDescription("Email-Adresse");
			field2.setRequired(true);
			field2.setType("string");
			formfield2 = new FormField(field2, "Form2");
			formfield2.getConstraints().put("mininclusive", "2");
			
			mlv = new MinLengthValidator();
		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public void testIsResponsibleFor() {
		
		boolean verantwortlich = false;
		
		verantwortlich = mlv.isResponsibleFor(formfield);
		assertTrue(verantwortlich);
		
		verantwortlich = mlv.isResponsibleFor(formfield2);
		assertTrue(!verantwortlich);
	}

	public void testValidate() {
		
		boolean fehlerBekommen = false;
		
		formfield.setValue("kngnoi");
		fehlerBekommen = !mlv.validate(formfield);
		assertTrue(!fehlerBekommen);
		
		formfield.setValue("kngnoidhae");
		fehlerBekommen = !mlv.validate(formfield);
		assertTrue(!fehlerBekommen);

		formfield.setValue("kngn");
		fehlerBekommen = !mlv.validate(formfield);
		assertTrue(fehlerBekommen);

		formfield.setValue("");
		fehlerBekommen = !mlv.validate(formfield);
		assertTrue(fehlerBekommen);
	}
}