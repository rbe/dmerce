/*
 * Created on Nov 25, 2003
 */
package com.wanci.dmerce.gui;

import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * @author mm
 * @version $Id: ClearDetailPanel.java,v 1.2 2004/01/14 14:11:59 mm Exp $
 */
public class ClearDetailPanel extends JPanel {

	/**
	 * neues leeres Panel erzeugen.
	 */
	public ClearDetailPanel() {
		super(new GridBagLayout());		
	}

	/**
	 * @param heading
	 *            The heading to set. Die Beschriftung des JPanels.
	 */
	public ClearDetailPanel(String heading) {
		super(new GridBagLayout());
		
		this.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(heading),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
	}

}
