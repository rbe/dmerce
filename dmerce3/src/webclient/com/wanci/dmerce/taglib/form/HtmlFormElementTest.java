package com.wanci.dmerce.taglib.form;

import java.util.HashMap;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.forms.FIELD;
import com.wanci.dmerce.forms.FORM;
import com.wanci.dmerce.servlet.XmlDocumentCache;


/**
 * tests für HtmlFormElements
 * 
 * @author pg
 */
public class HtmlFormElementTest {

	public static void main(String[] args) {
		
		XmlDocumentCache xml;
		FormField f = null;
		
		try {
			xml = XmlDocumentCache.getInstance();
			FORM form = (FORM) xml.getForms().getForm().get(0);
			System.out.println("FORM " + form.getName());
			FIELD field = (FIELD) form.getField().get(0);
			System.out.println("FIELD " + field.getName());
			f = new FormField(field, "form1");
		} catch (DmerceException e) {
			e.printStackTrace();
		}


		HashMap attribs = new HashMap();
		attribs.put("style", "width:100");
		attribs.put("class", null);

		HtmlFormElement el;
		
		el = new HtmlTextElement(f);
		el.setAttributes(attribs);
		el.setValue("testtext");
		System.out.println(el.toHtml());

		el = new HtmlCheckboxElement(f);
		el.setAttributes(attribs);
		el.setValue("true");
		System.out.println(el.toHtml());

		el = new HtmlRadioElement(f);
		el.setAttributes(attribs);
		el.setValue("true");
		System.out.println(el.toHtml());

		el = new HtmlListElement(f);
		el.setAttributes(attribs);
		el.setValue("listtext");
		System.out.println(el.toHtml());

		el = new HtmlFileUploadElement(f);
		el.setAttributes(attribs);
		System.out.println(el.toHtml());

		el = new HtmlHiddenElement(f);
		el.setValue("hiddentext");
		System.out.println(el.toHtml());

		el = new HtmlButtonElement(f);
		el.setAttributes(attribs);
		el.setValue("Ok");
		System.out.println(el.toHtml());
		
		el = new HtmlTextareaElement(f);
		el.setAttributes(attribs);
		el.setValue("testtext");
		System.out.println(el.toHtml());
		


		
	}
}
