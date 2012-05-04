/*
 * Datei angelegt am 19.11.2003
 */
package com.wanci.dmerce.gui.tree;

import com.wanci.dmerce.forms.FIELD;
import com.wanci.dmerce.forms.FORM;
import com.wanci.dmerce.forms.Forms;
import com.wanci.dmerce.workflow.xmlbridge.PAGE;
import com.wanci.dmerce.workflow.xmlbridge.TRANSITION;
import com.wanci.dmerce.workflow.xmlbridge.WORKFLOW;
import com.wanci.dmerce.workflow.xmlbridge.Workflows;

/**
 * Eine Wrapper-Klasse, um in einem JTree-Model JAXB-Objekte vewenden
 * zu können.
 * @author Masanori Fujita
 */
public class JAXBWrapper {
	
	Object node;
	
	public JAXBWrapper(Object node) {
		this.node = node;
	}
	
	public Object getWrappedObject() {
		return node;
	}
	
	public String toString() {
		
		if (this.node instanceof Forms)
			return "forms.xml";
		else if (this.node instanceof FORM)
			return ((FORM) node).getId();
		else if (this.node instanceof FIELD)
			return ((FIELD) node).getName();
		
		else if (this.node instanceof Workflows)
			return "workflows.xml";
		else if (this.node instanceof WORKFLOW)
			return ((WORKFLOW) node).getId();
		else if (this.node instanceof PAGE) {
			PAGE page = ((PAGE) node);
			return page.getId()+" ("+page.getTemplate()+")";
		}
		else if (this.node instanceof TRANSITION)
			return ((TRANSITION) node).getName();

		else return "unbekanntes Objekt vom Typ "+node.getClass().getName();
	}

}
