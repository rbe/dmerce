package com.wanci.dmerce.gui.monitor;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.wanci.dmerce.gui.ApplHelper;

/**
 * Dialog der die Datei <b>readme.html</b> in einem Editorpane
 * anzeigt.
 * 
 * @author	Ron Kastner
 * @version $Id: HelpDialog.java,v 1.1 2003/11/10 18:47:39 rb Exp $
 * 
 */
public class HelpDialog extends JDialog implements Runnable, ActionListener {

	// DefaultButton
	//private JButton btOk;
	private JEditorPane p;

	public HelpDialog(Frame parent, boolean modal) {
		super(parent, modal);
		initView();
	}

	/**
	 * Eventbehandlung der Aktionen
	 */
	public void actionPerformed(ActionEvent ae) {
		this.dispose();
	}

	/**
	 * Initialisierung der View
	 */
	protected void initView() {

		p = new JEditorPane();
		setTitle(ApplHelper.getLocString("help.title"));

		p.setEditable(false);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(p), BorderLayout.CENTER);

		setSize(400, 500);
		setLocationRelativeTo(this.getParent());

		Thread t = new Thread() {
			public void run() {
				SwingUtilities.invokeLater(HelpDialog.this);
			}
		};
		t.start();

	}

	public void run() {

		try {

			String path = "";
			path =
				new File(
					ApplHelper.RESOURCE_PATH
						+ ApplHelper.getLocString("help.file"))
					.toURL()
					.toString();
			System.out.println("Path to HelpFile: " + path);
			p.setPage(new URL(path));

		}
		catch (Exception e) {
			p.setText(ApplHelper.getLocString("error.help.notfound"));
		}

	}

}