package com.wanci.dmerce.gui.util;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JTable;

/**
 * SingeltionServiceKlasse, die das Drucken übernimmt.
 * @author	Ron Kastner
 */
public class PrintManager {


	/** Singelton Instanz */
	protected static PrintManager singleInstance = null;


	/** Referenz auf die zu druckende Tabelle */ 
	protected JTable printTable;
	
	/** Flag gibt an ob das Drucken beendet ist */
	protected boolean printingFinished = false;
	
	/** gerade gedruckte Seite */
	protected int currentPage = 0;		


	/** aktuelle Zeile des Tabellenmodells */
	protected int rowCounter = 0;
	




	/** Konstruktor für Öffentlichkeit sperren */
	private PrintManager() { super(); }
	
	/**
	 * Zugriff auf Singelton Instanz
	 * @return PrintManager
	 */
	public static PrintManager getInstance() {
		
		if (singleInstance == null)
			singleInstance = new PrintManager();
			
		return singleInstance;
	}
	
	/**
	 * Gib den Inhalt der Tabelle auf dem lokalen
	 * Drucker aus.<p>
	 * @param	table	javax.swing.JTable
	 */
	public void printTable(JTable table) {
		
		
		// Referenz auf den Table merken
		printingFinished = false;
		rowCounter=0;
		printTable = table;
		
		PrinterJob pj = PrinterJob.getPrinterJob();
		pj.setPrintable(new PrinterObject());		
		pj.defaultPage().setOrientation(PageFormat.PORTRAIT);
		if (pj.printDialog()) {
			try {
				pj.print();
			}
			catch (PrinterException e) {
				//if (ApplHelper.DEBUG)
					e.printStackTrace();
			}
		}
	}
	
	
	class PrinterObject implements Printable {
		
	/**
	 * Implementierung des Interface "Printable"
	 * @param	g	Graphics
	 * @param	pf	PageFormat
	 * @param	pageIndex	int
	 */
	public int print(Graphics g,PageFormat pf,int pageIndex) {


		if (pageIndex != 0) return NO_SUCH_PAGE;
		Graphics2D g2 = (Graphics2D)g;
		g2.setPaint(Color.black);
		g2.setFont(new Font("Serif",Font.PLAIN,11));
		
		// test
		int start_h=100;
		int start_w=70;
		//int max_h = 700;//Integer.parseInt(""+pf.getImageableHeight());		
		int max_entries = printTable.getRowCount();
		int h = 8;


		for (int i=0; i< max_entries; i++) {
			
			
			Object[] value = new Object[printTable.getColumnCount()];
			for (int j=0; j < printTable.getColumnCount(); j++) {
				value[j] = printTable.getValueAt(i,j);
			}
			
			for (int z=0; z < value.length; z++) {
				System.out.println("Value: "+value[z]);
				g2.drawString( (value[z]==null) ? "" : value[z].toString() ,start_w+110*z,start_h+h);
			}
				
			rowCounter++;
			h+=14;			
		}
		
		return PAGE_EXISTS;
		
		/*
		System.out.println(">>> Seite: "+pageIndex);
		
		//if (printingFinished) return NO_SUCH_PAGE;
		if (pageIndex != 0) return NO_SUCH_PAGE;
		
		//System.out.println(">>> 1");
		int max_entries = printTable.getRowCount();
		//System.out.println(">>> 2");
		Graphics2D g2 = (Graphics2D)g;
		g2.setPaint(Color.black);
		//System.out.println(">>> 3");
		g2.setFont(new Font("SansSerif",Font.PLAIN,h));
		//System.out.println(">>> 4");
		for (int i=rowCounter; i< max_entries; i++) {
			
			
			Object[] value = new Object[printTable.getColumnCount()];
			//System.out.println(">>> 5");
			for (int j=0; j < printTable.getColumnCount(); j++) {
				value[j] = printTable.getValueAt(i,j);
				System.out.println(""+value[j]);
			}
			//System.out.println(">>> 6");	
			//for (int z=0; z < value.length; z++)
			for (int z=0; z < 1; z++)
				g2.drawString( (value[z]==null) ? "" : value[0].toString() ,150*z,start_h+h);
				
			rowCounter++;
			h+=12;			
			
			if (start_h+h > max_h) {
				break;
			}
		}


		printingFinished = true;
		System.out.println(">>>Fertig mit Seite: "+pageIndex);		
		return PAGE_EXISTS;
		*/
	}		
	}
}


