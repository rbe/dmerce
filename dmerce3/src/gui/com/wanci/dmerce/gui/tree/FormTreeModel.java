/*
 * Created on Nov 18, 2003
 */
package com.wanci.dmerce.gui.tree;

import java.util.HashMap;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.wanci.dmerce.forms.FIELD;
import com.wanci.dmerce.forms.FORM;
import com.wanci.dmerce.forms.Forms;

/**
 * Klasse um ein TreeModel von der forms.xml in dem JTree
 * anzeigen zu können.
 * 
 * @author mm
 * @version $Id: FormTreeModel.java,v 1.1 2004/01/14 14:11:59 mm Exp $
 */
public class FormTreeModel implements TreeModel {

	/**
	 * Forms Objekt Variable 
	 */
	private Forms formsRoot = null;
	
	private HashMap mapWrapper;

	/**
	 * Konstruktor
	 * 
	 * @param root Element von forms.xml
	 */
	public FormTreeModel(Forms root) {
		this.formsRoot = root;
	}

	/**
	 * return Object, root des Form Tree
	 */
	public Object getRoot() {
		mapWrapper = new HashMap();
		return getWrapperForObject(formsRoot);
	}

	/**
	 * return int, die Anzahl der Kind-Elemente des Eltern-Elements
	 */
	public int getChildCount(Object parent) {
		
		JAXBWrapper wrapper = (JAXBWrapper) parent;

		if (wrapper.getWrappedObject() instanceof Forms) {
			Forms forms = (Forms) wrapper.getWrappedObject();
			List formList = forms.getForm();
			return formList.size();
		}

		else if (wrapper.getWrappedObject() instanceof FORM) {
			FORM newForm = (FORM) wrapper.getWrappedObject();
			List fieldList = newForm.getField();
			return fieldList.size();
		}

		return 0;
	}

	/**
	 * Methode, die prüft, ob ein Knoten der unterste ist um ihn
	 * dann als Blatt ohne Unterknoten darzustellen.
	 */
	public boolean isLeaf(Object node) {
		
		JAXBWrapper wrapper = (JAXBWrapper) node;
		
		if (wrapper.getWrappedObject() instanceof FIELD)
			return true;
		else
			return false;
	}

	public void addTreeModelListener(TreeModelListener l) {

	}

	public void removeTreeModelListener(TreeModelListener l) {

	}

	/**
	 * @return Returns object, entweder ein FORM oder ein FIELD Object
	 */
	public Object getChild(Object parent, int index) {

		JAXBWrapper wrapper = (JAXBWrapper) parent;		

		if (wrapper.getWrappedObject() instanceof Forms) {
			Forms forms = (Forms) wrapper.getWrappedObject();
			return getWrapperForObject(forms.getForm().get(index));
		}

		else if (wrapper.getWrappedObject() instanceof FORM) {
			FORM newForm = (FORM) wrapper.getWrappedObject();
			return getWrapperForObject(newForm.getField().get(index));
		}
		return null;
	}

	/**
	 * @return int, index des Kind-Elements
	 */
	public int getIndexOfChild(Object parent, Object child) {

		JAXBWrapper wrapper = (JAXBWrapper) parent;

		if (wrapper.getWrappedObject() instanceof Forms) {
			Forms forms = (Forms) wrapper.getWrappedObject();
			List formList = forms.getForm();
			return formList.indexOf(child);
		}

		if (wrapper.getWrappedObject() instanceof FORM) {
			FORM newForm = (FORM) wrapper.getWrappedObject();
			List fieldList = newForm.getField();
			return fieldList.indexOf(child);
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
	 * @param o Objekt, für das ein Wrapper angefordert wird.
	 * @return Gibt einen JAXBWrapper zurück, in dem das Objekt o enthalten ist.
	 */
	public Object getWrapperForObject(Object o) {
		if (mapWrapper.get(o) == null) {
			mapWrapper.put(o, new JAXBWrapper(o));
		}
		return mapWrapper.get(o);
	}
	
}