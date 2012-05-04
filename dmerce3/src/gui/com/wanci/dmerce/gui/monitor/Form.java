package com.wanci.dmerce.gui.monitor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

/**
 * Utility Klasse für die vereinfachte Benutzung
 * des GridBagLayouts für ein zweispaltiges Formular. <br>
 * Um das Formular kann ein Rahmen mit Titel angeordnet werden.
 * <p>
 * <b>Beispiele:</b><br>
 * <b>1. Formular ohne Ausdehnung</b>
 * <pre>JTextField txName = new JTextField(15);
 *  JTextField txVorname = new JTextField(10);
 *	JTextField txPlz = new JTextField(5);
 *	Behaelter b = new Behaelter();
 *	b.setzeInhaltErsteSpalte(new Object[] { "Name", "Vorname", "Plz" } );
 *	b.setzeInhaltZweiteSpalte(new Object[] { txName, txVorname, txPlz } );
 * </pre>
 * <table bgcolor=red cellspacing=2><tr><td bgcolor=white>Name</td><td bgcolor=white>XXXXXXXXXXXXXXX</td></tr>
 * <tr><td bgcolor=white>Vorname</td><td bgcolor=white>XXXXXXXXXX</td></tr>
 * <tr><td bgcolor=white>Plz</td><td bgcolor=white>XXXXX</td></tr></table>
 * 
 * <b>2. Formular mit Ausdehnung</b>
 * <pre>JTextField txName = new JTextField(15);
 *	JTextField txVorname = new JTextField(10);
 *	JTextField txPlz = new JTextField(5);
 *	Behaelter b = new Behaelter();
 *	b.setzeInhaltErsteSpalte(new Object[] { "Name", "Vorname", "Plz" } );
 *	b.setzeInhaltZweiteSpalte(new Object[] { txName, txVorname, txPlz }, new boolean[] { true, true, false} );
 *	</pre>
 *	<table bgcolor=red cellspacing=2><tr><td bgcolor=white>Name</td><td bgcolor=white>XXXXXXXXXXXXXXXXXX</td></tr>
 * <tr><td bgcolor=white>Vorname</td><td bgcolor=white>XXXXXXXXXXXXXXXXXX</td>
 * </tr><tr><td bgcolor=white>Plz</td><td bgcolor=white>XXXXX</td></tr></table>
 *
 *  <b>3. Einfache Verschachtelung von 2 Behaeltern</b>
 *  <pre>JTextField txName = new JTextField(15);
 *	JTextField txVorname = new JTextField(10);
 *	JTextField txStrasse = new JTextField(15);
 *	JTextField txPlz = new JTextField(5);
 *
 *	Behaelter bLinks = new Behaelter();
 *	b.setzeInhaltErsteSpalte(new Object[] { "Name", "Vorname" } );
 *	b.setzeInhaltZweiteSpalte(new Object[] { txName, txVorname } );
 *
 *	Behaelter bRechts = new Behaelter();
 *	b.setzeInhaltErsteSpalte(new Object[] { "Strasse", "Plz" } );
 *	b.setzeInhaltZweiteSpalte(new Object[] { txStrasse, txPlz } );
 *
 *	Behaelter bAlles = new Behaelter();
 *	bAlles.setzeInhaltErsteSpalte(new Object[] { bLinks } );
 *	bAlles.setzeInhaltZweiteSpalte(new Object[] { bRechts } );
 * </pre>
 * <table bgcolor=blue cellspacing=2>
 * <tr><td><table bgcolor=red cellspacing=3>
 * <tr><td bgcolor=white>Name</td><td bgcolor=white>XXXXXXXXXXXXXXX</td></tr>
 * <tr><td bgcolor=white>Vorname</td><td bgcolor=white>XXXXXXXXXX</td></tr></table>
 * </td><td><table bgcolor=red cellspacing=3><tr><td bgcolor=white>Strasse</td>
 * <td bgcolor=white>XXXXXXXXXXXXXXX</td></tr><tr><td bgcolor=white>Plz</td>
 * <td bgcolor=white>XXXXX</td></tr></table>
 * </td></tr></table>
 *
 * <b>4. Ausrichtung von untereinander stehenden Formularen</b>
 * <pre>JTextField txName = new JTextField(15);
 * JTextField txVorname = new JTextField(10);
 * JTextField txBewirtungskosten = new JTextField(6);
 * JTextField txMiete = new JTextField(6);
 * Behaelter bOben = new Behaelter();
 * <font color=red>bOben.setzeBreiteErsteSpalte(170);</font>
 * bOben.setzeInhaltErsteSpalte(new Object[] { "Name", "Vorname" } );
 * bOben.setzeInhaltZweiteSpalte(new Object[] { txName, txVorname } );
 * Behaelter bUnten = new Behaelter();
 * <font color=red>bUnten.setzeBreiteErsteSpalte(170);</font>
 * bUnten.setzeInhaltErsteSpalte(new Object[] { "Bewirtungskosten", "Miete" } );
 *	bUnten.setzeInhaltZweiteSpalte(new Object[] { txBewirtungskosten, txBewirtungskosten } );
 *
 *	setzeLayout( BOXLAYOUT_VERTIKAL );
 *	hinzufuegen( bOben );
 *	hinzufuegen( bUnten );
 *	</pre>
 *	<table border=0 cellspacing=6><tr>
 *  <td><b>OHNE "setzeBreiteErsteSpalte"</b></td>
 *	<td><b>MIT "setzeBreiteErsteSpalte"</b></td>
 *	</tr><tr>
 *  <td><table bgcolor=blue cellspacing=3>
 *	<tr><td><table bgcolor=red cellspacing=3>
 *	<tr><td bgcolor=white>Name</td><td bgcolor=white>XXXXXXXXXXXXXXX</td>
 *	</tr><tr><td bgcolor=white>Vorname</td>
 *	<td bgcolor=white>XXXXXXXXXX</td></tr>
 *  </table></td>
 *	<tr><td>
 *  <table bgcolor=red cellspacing=3>
 *		<tr>
 * 			<td bgcolor=white>Bewirtungskosten</td>
 * 			<td bgcolor=white>XXXXXX</td>
 * 		</tr>
 * 		<tr>
 * 			<td bgcolor=white>Miete</td>
 * 			<td bgcolor=white>XXXXXX</td>
 * 		</tr>
 * </table>
 * 	</td>
 * 	</tr>
 * 	</table>	
 * 	</td>
 * 	<td>
 *  <table bgcolor=blue cellspacing=3>
 * 	<tr><td><table bgcolor=red cellspacing=3>
 * 	<tr>
 *  	<td bgcolor=white width=130>Name</td>
 * 		<td bgcolor=white>XXXXXXXXXXXXXXX</td>
 * 	</tr>
 *  <tr>
 *  <td bgcolor=white width=130>Vorname</td>
 *  <td bgcolor=white>XXXXXXXXXX</td>
 * 	</tr></table>		
 * </td><tr>				
 * <td>
 * <table bgcolor=red cellspacing=3>
 * <tr><td bgcolor=white width=130>Bewirtungskosten</td>
 * 	<td bgcolor=white>XXXXXX</td></tr>
 * <tr><td bgcolor=white width=130>Miete</td>
 * 		<td bgcolor=white>XXXXXX</td>
 * 	</tr></table></td></tr></table>	
 * </td></tr></table>
 * 
 * @author Ron Kastner
 */
public class Form extends JPanel {

	static AbstractAction tabAction = new AbstractAction() {
		public void actionPerformed(ActionEvent ae) {
			javax.swing.FocusManager.getCurrentManager().focusNextComponent((Component)ae.getSource());
		}
	};
	static AbstractAction shiftTabAction = new AbstractAction() {
		public void actionPerformed(ActionEvent ae) {
			javax.swing.FocusManager.getCurrentManager().focusPreviousComponent((Component)ae.getSource());
		}
	};

	/** gibt an ob der Rahmen gemalt werden soll */
	private boolean paintBorder = true;
	
	/** Ueberschrift des Rahmens */
	private String title = new String();
	
	/** Anzahl maximale Pixelbreite fuer die erste Spalte */
	private int firstColumnWidth = -1;

	/** Ausdehnung der ersten Spalte */
	private boolean fillFirstColumn = false;

	/** Ausdehnung der ersten Spalte */
	private boolean fillSecondColumn = true;

	/**
	 * Setzt einen Abstand um diesen Behaelter
	 * @param	top		int
	 * @param	left	int
	 * @param	bottom	int
	 * @param	right	int
	 */
    public void setInsets(int top, int left, int bottom, int right) {

		Border b = BorderFactory.createEmptyBorder(top,left,bottom,right);

    	if (this.getBorder() == null) {
    		this.setBorder(b);
        }
        else {
        	// Wenn ein Titel gesetzt worden ist, dann mussen die beiden Rahmen
        	// verschachtelt werden.
        	CompoundBorder cb = BorderFactory.createCompoundBorder(getBorder(),b);
        	this.setBorder(cb);        	
        }
    }
		
	/** 
	 * Konstruktor mit FormularTitel
	 */
	public Form(String arg) {
					
		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);
		
		if (arg != null)
			setTitle(arg);
		
	}	

	/**
	 * Default-Konstruktor
	 */	
	public Form() {
		this(null);
	}

	/**
	 * Setze die Breite (in Pixeln) der ersten Spalte.
	 * @param	neueBreite	int
	 */
	public void setFirstColumnWidth(int neueBreite) {
		firstColumnWidth = neueBreite;
		repaint();
	}
	
	/**
	 * Setze die Ausdehnung der ersten Spalte.
	 * @param	arg	boolean
	 */
	public void setFillFirstColumn(boolean arg) {
		fillFirstColumn = arg;
	}

	public void setFillSecondColumn(boolean arg) {
		fillSecondColumn = arg;
	}


	public int getFirstColumnWidth() { return firstColumnWidth; }

    /**
     * Setzt den Inhalt der geforderten Spalte in dem Behaelter.
     * Wird von beiden setzeInhalt-Methoden verwendet.
     */
	private void setContent( Object[] args, int spalte, int breite, boolean[] fill, boolean ausdehnung ) {

		Object[] inhalt = args;
		
		// Allgemeine Constraints-Einstellungen
		GridBagConstraints gbc = new GridBagConstraints();		
		
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = ausdehnung ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
		gbc.weightx = ausdehnung ? 1.0 : 0.0;
		gbc.weighty = 0.0;
		gbc.insets = new Insets(2,2,2,2);
		gbc.gridx = spalte;
		
		for (int i=0; i<inhalt.length; i++) {
			
			gbc.gridy = i;
			gbc.fill = fill[i] ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;

			JComponent comp = getComponentForLayout(inhalt[i]);

            if (comp instanceof JTextArea){
                JTextArea ta = (JTextArea)comp;
                ta.setLineWrap(true);
                // Focus Manager überlisten -> Tab, ShiftTab nächste/vorige Komponente
                ta.registerKeyboardAction(tabAction, KeyStroke.getKeyStroke(KeyEvent.VK_TAB , 0), JComponent.WHEN_FOCUSED );
                ta.registerKeyboardAction(shiftTabAction, KeyStroke.getKeyStroke(KeyEvent.VK_TAB , KeyEvent.SHIFT_MASK ), JComponent.WHEN_FOCUSED );
                gbc.fill = GridBagConstraints.BOTH;
                gbc.weighty = 1.0;
            	comp =  new JScrollPane(ta);
        	}
            else if (comp instanceof Form) {
                gbc.insets = new Insets(0,0,0,0);
            }
            else if ( breite != -1 && !ausdehnung ) {
				Dimension d = new Dimension( breite, comp.getPreferredSize().height);
				comp.setMaximumSize(d);
				comp.setMinimumSize(d);
				comp.setPreferredSize(d);
            }
			else {
	            comp.setMaximumSize(comp.getPreferredSize());
				comp.setMinimumSize(comp.getPreferredSize());
            }

			this.add(comp,gbc);
		}
        // fuege am Ende ein Dummy-Element hinzu, dass sich ausdehnt...
		gbc.weighty = 0.1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		this.add(Box.createVerticalStrut(1));
    }

	public void setContentFirstColumn(Object[] args) {
        setContentFirstColumn(args,new boolean[args.length]);
    }

	public void setContentFirstColumn(Object[] args, boolean[] fill) {
		setContent(args, 0, getFirstColumnWidth() , fill, fillFirstColumn );
	}

	private JComponent getComponentForLayout(Object obj) {
		
		if (obj instanceof JComponent) {
			return (JComponent)obj;
        }
		else 
			return new JLabel(obj.toString());
		
	}

	public void setContentSecondColumn(Object[] args) {
		setContentSecondColumn(args,new boolean[args.length]);
	}
	
	public void setContentSecondColumn(Object[] args,boolean[] fill) {
		setContent(args, 1, -1 , fill, fillSecondColumn );

        /*
		Object[] inhaltZweiteSpalte = args;
		
		// Allgemeine Constraints-Einstellungen
		GridBagConstraints gbc = new GridBagConstraints();		
		
		gbc.anchor = gbc.NORTHWEST;
		gbc.weightx = ausdehnungZweiteSpalte ? 1.0 : 0.0;
		gbc.weighty = 0.0;
		gbc.insets = new Insets(2,2,2,2);
		gbc.gridx = 1;

		for (int i=0; i<inhaltZweiteSpalte.length; i++) {
			
			gbc.gridy = i;
			gbc.fill = fill[i] ? gbc.BOTH : gbc.NONE;

            if (i == inhaltZweiteSpalte.length-1) {
				gbc.weighty = 1.0;
				gbc.anchor = gbc.NORTHWEST;
			}

			JComponent comp = gibKomponenteFuerLayout(inhaltZweiteSpalte[i]);

            if (comp instanceof JTextArea){
                JTextArea ta = (JTextArea)comp;
                ta.setLineWrap(true);
                gbc.fill = gbc.BOTH;
                gbc.weighty = 1.0;
            	comp =  new JScrollPane(ta);
        	}
			else {
	            comp.setMaximumSize(comp.getPreferredSize());
				comp.setMinimumSize(comp.getPreferredSize());
            }

			this.add(comp,gbc);
		}

        // fuege am Ende ein Dummy-Element hinzu, dass sich ausdehnt...
		gbc.weighty = 0.1;
		gbc.anchor = gbc.NORTHWEST;
		this.add(Box.createVerticalStrut(1));
		*/
	}

	
	/**
	 * Setze den Titel für den Rahmen des Formulars
	 * @param	arg	String
	 */
	public void setTitle(String arg) {
		title = arg;
		
		// wenn man einen Titel setzt, dann wird auch automatisch der Rahmen angezeigt
		setPaintBorder(true);
		
	}

	public String getTitle() { return title; }
	/** 
	 * Lege fest, ob ein Rahmen gemalt werden soll
	 * @param arg	boolean
	 */
	public void setPaintBorder(boolean arg) {
		paintBorder = arg;
		setBorder(paintBorder ? BorderFactory.createTitledBorder(getTitle()) : null);
	}

	protected void initialize() {
        System.out.println("Fenster meldet: Die initialisiere Methode ist nicht überschrieben worden!! wahrscheinlich sieht man jetzt nichts!");
    }

	/**
	 * Liefere zurück, ob ein Rahmen gemalt werden soll
	 * @return boolean
	 */
	public boolean isPaintBorder() { return paintBorder; }
}