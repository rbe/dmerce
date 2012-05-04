/*
 * Created on Nov 18, 2003
 */
package com.wanci.dmerce.gui.tree;

import java.util.HashMap;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.wanci.dmerce.workflow.xmlbridge.PAGE;
import com.wanci.dmerce.workflow.xmlbridge.WORKFLOW;
import com.wanci.dmerce.workflow.xmlbridge.Workflows;

/**
 * Klasse um ein TreeModel von der workflows.xml in dem JTree anzeigen zu
 * können.
 * 
 * @author mm
 * @version $Id: WorkflowTreeModel.java,v 1.1 2004/01/14 14:11:59 mm Exp $
 */
public class WorkflowTreeModel implements TreeModel {

	/**
	 * Workflows Variable, die das Root Element beinhaltet
	 */
	private Workflows wfRoot = null;

	private HashMap mapWrapper;

	/**
	 * Konstruktor
	 * 
	 * @param root
	 *            Element von workflows.xml
	 */
	public WorkflowTreeModel(Workflows root) {
		this.wfRoot = root;
	}

	/**
	 * return Object, root des Workflow Tree
	 */
	public Object getRoot() {
		mapWrapper = new HashMap();
		return getWrapperForObject(wfRoot);
	}

	/**
	 * @return int, die Anzahl der Kind-Elemente des Eltern-Elements
	 */
	public int getChildCount(Object parent) {

		JAXBWrapper wrapper = (JAXBWrapper) parent;

		if (wrapper.getWrappedObject() instanceof Workflows) {
			Workflows wfs = (Workflows) wrapper.getWrappedObject();
			List wfsList = wfs.getWorkflow();
			return wfsList.size();
		}

		else if (wrapper.getWrappedObject() instanceof WORKFLOW) {
			WORKFLOW wf = (WORKFLOW) wrapper.getWrappedObject();
			List wfList = wf.getPage();
			return wfList.size();
		}

		else if (wrapper.getWrappedObject() instanceof PAGE) {
			PAGE page = (PAGE) wrapper.getWrappedObject();
			List pageList = page.getTransition();
		}

		return 0;
	}

	/**
	 * Methode, die prüft, ob ein Knoten der unterste ist um ihn dann als Blatt
	 * ohne Unterknoten darzustellen.
	 */
	public boolean isLeaf(Object node) {

		JAXBWrapper wrapper = (JAXBWrapper) node;

		if (wrapper.getWrappedObject() instanceof PAGE)
			return true;
		else
			return false;
	}

	public void addTreeModelListener(TreeModelListener l) {

	}

	public void removeTreeModelListener(TreeModelListener l) {

	}

	/**
	 * @return Object, entweder ein WORKFLOW oder ein TRANSITION Object
	 */
	public Object getChild(Object parent, int index) {

		JAXBWrapper wrapper = (JAXBWrapper) parent;

		if (wrapper.getWrappedObject() instanceof Workflows) {
			Workflows wfs = (Workflows) wrapper.getWrappedObject();
			return getWrapperForObject(wfs.getWorkflow().get(index));
		}

		else if (wrapper.getWrappedObject() instanceof WORKFLOW) {
			WORKFLOW wf = (WORKFLOW) wrapper.getWrappedObject();
			return getWrapperForObject(wf.getPage().get(index));
		}

		else if (wrapper.getWrappedObject() instanceof PAGE) {
			PAGE page = (PAGE) wrapper.getWrappedObject();
			return getWrapperForObject(page.getTransition().get(index));
		}

		return null;
	}

	/**
	 *@return int, index des Kind-Elements
	 */
	public int getIndexOfChild(Object parent, Object child) {

		JAXBWrapper wrapper = (JAXBWrapper) parent;

		if (wrapper.getWrappedObject() instanceof Workflows) {
			Workflows wfs = (Workflows) wrapper.getWrappedObject();
			List wfsList = wfs.getWorkflow();
			return wfsList.indexOf(child);
		}

		else if (wrapper.getWrappedObject() instanceof WORKFLOW) {
			WORKFLOW wf = (WORKFLOW) wrapper.getWrappedObject();
			List wfList = wf.getPage();
			return wfList.indexOf(child);
		}

		else if (wrapper.getWrappedObject() instanceof PAGE) {
			PAGE page = (PAGE) wrapper.getWrappedObject();
			List pageList = page.getTransition();
			return pageList.indexOf(child);
		}

		return 0;
	}

	/**
	 * Falls sich der Pfad zu einem value geändert hat
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		System.out.println(
			"*** valueForPathChanged : " + path + " --> " + newValue);
	}

	/**
	 * Ein Cache für gewrappte Objekte zur Effizienzsteigerung.
	 * 
	 * @param o
	 *            Objekt, für das ein Wrapper angefordert wird.
	 * @return Gibt einen JAXBWrapper zurück, in dem das Objekt o enthalten
	 *         ist.
	 */
	public Object getWrapperForObject(Object o) {
		if (mapWrapper.get(o) == null) {
			mapWrapper.put(o, new JAXBWrapper(o));
		}
		return mapWrapper.get(o);
	}

}