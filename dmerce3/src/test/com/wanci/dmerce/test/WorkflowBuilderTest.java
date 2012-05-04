/*
 * Datei angelegt am 20.10.2003
 */
package com.wanci.dmerce.test;

import javax.xml.bind.JAXBException;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowBuilder;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowEngine;

/**
 * @author Masanori Fujita
 */
public class WorkflowBuilderTest {
	
	public static void main(String[] args) throws JAXBException, DmerceException, WorkflowConfigurationException {
		WebappWorkflowBuilder wb = new WebappWorkflowBuilder();
		WebappWorkflowEngine engine = new WebappWorkflowEngine();
		wb.readForms(engine);
		/*
		Iterator it = engine.getFields().iterator();
		while (it.hasNext()) {
			FormField field = (FormField) it.next();
			System.out.println(field.getFormId()+"."+field.getName());
		}
		*/
		wb.readWorkflows(engine);
		System.out.println(engine.getWorkflow("einladung").toString()+"\n");
		// System.out.println(engine.getWorkflow("register").toString()+"\n");
		// System.out.println(engine.getWorkflow("verzweigung").toString()+"\n");
		
	}

}
