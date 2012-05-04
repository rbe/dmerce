/*
 * Datei angelegt am 21.10.2003
 */
package com.wanci.dmerce.test;

import java.io.InputStream;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.wanci.dmerce.workflow.xmlbridge.CONDITION;
import com.wanci.dmerce.workflow.xmlbridge.PAGE;
import com.wanci.dmerce.workflow.xmlbridge.TRANSITION;
import com.wanci.dmerce.workflow.xmlbridge.WORKFLOW;
import com.wanci.dmerce.workflow.xmlbridge.Workflows;

/**
 * @author Masanori Fujita
 */
public class WorkflowsXmlTest {

	public static void main(String[] args) throws JAXBException {
		WorkflowsXmlTest o = new WorkflowsXmlTest();
		
		JAXBContext jc = JAXBContext.newInstance("com.wanci.dmerce.workflow.xmlbridge");
		// Unmarshaller erzeugen
		Unmarshaller u = jc.createUnmarshaller();
		// Unmarshalling
		InputStream inputstream =
			o.getClass().getResourceAsStream("../res/workflows.xml");
		if (inputstream == null) {
			System.out.println("DEBUG-Info: inputstream darf nicht null sein.");
			throw new JAXBException("XmlDocumentCache: workflows.xml konnte nicht gefunden werden.");
		}
		Object unm = u.unmarshal(inputstream);
		System.out.println(unm.getClass().getName());
		Workflows w = (Workflows) unm;
		WORKFLOW wf = (WORKFLOW) w.getWorkflow().get(2);
		PAGE page = (PAGE) wf.getPage().get(0);
		TRANSITION tr =  (TRANSITION) page.getTransition().get(0);
		CONDITION cond = tr.getCondition();
		System.out.println(cond.getJavaclass());
		System.out.println(cond.getFormfield().size());
		Iterator it = cond.getFormfield().iterator();
		while (it.hasNext()) {
			Object element = (Object) it.next();
			System.out.println(element.getClass().getName());
		}
	}

}
