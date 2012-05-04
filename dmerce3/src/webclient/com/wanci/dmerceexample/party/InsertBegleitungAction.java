/*
 * Datei angelegt am 17.11.2003
 */
package com.wanci.dmerceexample.party;

import java.util.HashMap;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.exceptions.FieldNotFoundException;
import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.taglib.form.FormField;
import com.wanci.dmerce.webservice.SQLService;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.webapp.WebappAction;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowContext;

/**
 * @author Masanori Fujita
 */
public class InsertBegleitungAction extends WebappAction {

	private SQLService service ;
	
	/**
	 * @param parent
	 * @throws WorkflowConfigurationException
	 */
	public InsertBegleitungAction(Transition parent) throws WorkflowConfigurationException {
		super(parent);
		try {
			service = new SQLService();
		} catch (DmerceException e) {
			throw new WorkflowConfigurationException(e.getMessage());
		}
	}

	public void execute(WebappWorkflowContext context) throws WorkflowConfigurationException {
		try {
			// Datensatz erzeugen und abspeichern
			HashMap data = new HashMap();
			// Name der Begleitung mit Zusatz-Info versehen
			FormField formfield = context.getFormField("begleitunginfo","einladung","name");
			formfield.setValue(formfield.getValue()+" (Begleitung von "+context.getFormField("personinfo","einladung","name").getValue()+")");
			data.put("Name", context.getFormField("begleitunginfo","einladung","name").getValue());
			data.put("Trinken", context.getFormField("begleitunginfo","einladung","trinken2").getValue());
			data.put("Bier", new Boolean(context.getFormField("begleitunginfo","einladung","bier").getValue()));
			data.put("Wein", new Boolean(context.getFormField("begleitunginfo","einladung","wein").getValue()));
			data.put("Hartes", new Boolean(context.getFormField("begleitunginfo","einladung","hartes").getValue()));
			data.put("Soft", new Boolean(context.getFormField("begleitunginfo","einladung","soft").getValue()));
			service.insertData("partyinfo","ID", data);
		} catch (FieldNotFoundException e) {
			WorkflowConfigurationException wce = new WorkflowConfigurationException(e.getMessage());
			wce.setStackTrace(e.getStackTrace());
			throw wce;
		}
	}

}
