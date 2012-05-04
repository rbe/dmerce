/*
 * Datei angelegt am 04.11.2003
 */
package com.wanci.dmerce.workflow.webapp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Diese Klasse hält lediglich eine Liste von Formularfeldern.
 * Es werden keinerlei Prüfungen in dieser Klasse durchgeführt.
 * 
 * @author Masanori Fujita
 */
public class FieldList {
	
	/**
	 * Innere Klasse, um eine FormField-Zeile abzubilden.
	 * @author Masanori Fujita
	 */
	class FormField {
		
		private String pageid;
		private String formid;
		private String fieldname;
		
		/**
		 * @param formid
		 * @param fieldname
		 */
		FormField(String formid, String fieldname) {
			super();
			this.formid = formid;
			this.fieldname = fieldname;
		}
		
		String getPageId() {
			return pageid;
		}
		
		void setPageId(String pageid) {
			this.pageid = pageid;
		}
		
		String getFieldname() {
			return fieldname;
		}

		void setFieldname(String fieldname) {
			this.fieldname = fieldname;
		}

		String getFormid() {
			return formid;
		}

		void setFormid(String formid) {
			this.formid = formid;
		}
	}
	
	/*
	 * Liste von Formularfeldern
	 */
	private List fieldlist;
	
	/**
	 * Standard-Konstruktor
	 */
	public FieldList() {
		fieldlist = new ArrayList();
	}
	
	public int size() {
		return fieldlist.size();
	}
	
	public void addField(String formid, String fieldname, String pageId) {
		FormField formfield = new FormField(formid, fieldname); 
		if (pageId != null)
			formfield.setPageId(pageId);
		fieldlist.add(formfield);
	}
	
	public FormField get(int i) {
		return (FormField) fieldlist.get(i);
	}
	
	public Iterator iterator() {
		return fieldlist.iterator();
	}

}
