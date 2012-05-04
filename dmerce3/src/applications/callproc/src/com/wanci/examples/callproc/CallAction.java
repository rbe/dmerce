/*
 * Datei angelegt am 13.01.2004
 */
//package com.wanci.examples.callproc;

import java.util.ArrayList;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.exceptions.FieldNotFoundException;
import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.WorkflowRuntimeException;
import com.wanci.dmerce.webservice.SQLService;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.webapp.WebappAction;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowContext;

/**
 * CallAction
 *
 * @author Masanori Fujita
 */
public class CallAction extends WebappAction {

	/**
	 * @param parent
	 * @throws WorkflowConfigurationException
	 */
	public CallAction(Transition parent) throws WorkflowConfigurationException {
		super(parent);
		System.out.println("CallAction wird gerade erzeugt. Parent ist "+getParentTransition().getId()+", übergeben wurde "+parent.getId());
	}

	public void execute(WebappWorkflowContext context)
		throws WorkflowRuntimeException {
		
		try {
			SQLService service = new SQLService();
			ArrayList params = new ArrayList();
			params.add(context.getFormField("registerpage","form1","name").getValue());
			params.add(context.getFormField("registerpage","form1","vorname").getValue());
			params.add(context.getFormField("registerpage","form1","email").getValue());
			service.callProc("new_kunde",params);
		} catch (DmerceException e) {
			e.printStackTrace();
			throw new WorkflowRuntimeException(e.getMessage());
		} catch (FieldNotFoundException e) {
			e.printStackTrace();
			throw new WorkflowRuntimeException(e.getMessage());
		}
	}

}
