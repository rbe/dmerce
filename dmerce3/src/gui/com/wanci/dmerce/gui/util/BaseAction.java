package com.wanci.dmerce.gui.util;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
/**
 * Abstrakte Oberklasse für alle Actionen der Applikation
 * sorgt dafür das Actions in einem seperaten Thread ausgeführt werden,
 * damit die Reaktionsfähigkeit der Oberfläche erhalten bleibt.
 * 
 * @author	Ron Kastner
 * @version $Id: BaseAction.java,v 1.1 2003/11/10 18:47:39 rb Exp $
 * 
 */
public abstract class BaseAction extends AbstractAction implements Runnable {
	
	/** Komponente die den Event ausgelöst hat */
	//protected JComponent source;
	
	/** eigener Enabled-State */
	private boolean customEnabledState = true;
	
	protected JComponent comp = null; 
	
	/** ActionCommand */
	protected String command;
	
	public void actionPerformed(ActionEvent ae) {
		//source = (JComponent)ae.getSource();
		command = ae.getActionCommand();			
		//SwingUtilities.invokeLater(this);
		handleEvent();
	}
	
	/** Implementierung des Runnable-Interfaces */
	public void run() {
		handleEvent();
	}
	
	/** 
	 * Sperren oder Freigeben der Aktion 
	 * @param arg	boolean 
	 */
	public void setEnabled(boolean arg) {
		super.setEnabled(arg);		
		
		customEnabledState = arg;
		
		if (comp != null)
			comp.setEnabled(customEnabledState);
	}
	
	public void setComponent(JComponent comp) {
		this.comp = comp;
		comp.setEnabled(customEnabledState);
	}
	
	/**
	 * Behandle den Event. Diese Methode ist in Subklassen zu
	 * überschreiben
	 */
	public abstract void handleEvent();
};