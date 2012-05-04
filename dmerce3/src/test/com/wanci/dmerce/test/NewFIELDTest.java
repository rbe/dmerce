/*
 * Datei angelegt am 14.10.2003
 */
package com.wanci.dmerce.test;

import javax.xml.bind.JAXBException;

import com.wanci.dmerce.forms.FIELD;
import com.wanci.dmerce.forms.ObjectFactory;
import com.wanci.dmerce.formvalidation.EmailValidator;
import com.wanci.dmerce.taglib.form.FormField;

/**
 * @author Masanori Fujita
 */
public class NewFIELDTest {
	
	public static void main (String[] args) {
		try {
			ObjectFactory of = new ObjectFactory();
			FIELD field = of.createFIELD();
			field.setName("Email");
			field.setDescription("Email-Adresse");
			field.setRequired(true);
			field.setType("string");
			FormField formfield = new FormField(field, "Form1");
			formfield.setValue("mm@1ci.de");
			EmailValidator ev = new EmailValidator();
			System.out.println(ev.validate(formfield));
		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
