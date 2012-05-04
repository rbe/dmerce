package com.wanci.dmerce.gui.util;

import java.awt.Container;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.wanci.dmerce.gui.ApplHelper;
import com.wanci.dmerce.gui.monitor.FilterTableModel;
import com.wanci.dmerce.gui.monitor.Monitor;


/**
 * Zentrale Klasse, die die "BusinessLogik" der gesamten
 * Anwendung kapselt.
 * <p>
 * Der LogHandler kennt eine Instanz des XMLHandlers und delegiert alle
 * XML Anforderungen an diesen.
 * <p>
 * LogHandler benutzt das Singelton-Pattern.<p>
 *
 * @author Ron Kastner 
 */
public class ProcessModel implements MonitorConstants {


	/** Singelton Instanz */
	private static ProcessModel singleInstance;

	/** Sollen die empfangenen Daten angezeigt werden */
	private boolean listening = false;

	/** Delegat für XML-Arbeit */
	private XmlHandler xmlHandler = new XmlHandler();
	
	/** Singelton Konstruktor */
	private ProcessModel() { }

	/** Merker, ob weiterhin vom Socket gelesen werden soll */
	boolean processReadFromSocket = false;
	
	/**
	 * Liefere zurück, ob die Informationen vom Server
	 * ausgewertet werden sollen.<p>
	 * @return boolean
	 */
	public boolean isListening() {
		return listening;
	}	
	
	/**
	 * Lege fest, ob die Informationen vom Server
	 * ausgewertet werden sollen.<p>
	 * @return boolean
	 */
	public void setListening(boolean arg) {
		listening = arg;
		
		if (arg) {
			ApplHelper.getAction("startListeningFromSocketAction").setEnabled(false);
			ApplHelper.getAction("stopListeningFromSocketAction").setEnabled(true);
		}
		else {
			ApplHelper.getAction("startListeningFromSocketAction").setEnabled(true);
			ApplHelper.getAction("stopListeningFromSocketAction").setEnabled(false);
		}
	}	
	
	/**
	 * Liefere eine Singelton-Instanz dieser Klasse zurück.
	 * @return LogHandler
	 */
	public static ProcessModel getInstance() {
		if (singleInstance == null)
			singleInstance = new ProcessModel();
			
		return singleInstance;
	}
	
   /** Echo Socket */
   private Socket echoSocket = null;
   private PrintWriter out = null;
   private BufferedReader in = null;	
			 
	 	 	 	 
	/** Referenz auf das Tabellenmodell für die Darstellung der XML-Daten */
	protected static TableModel tablemodel;
		
	/** 
	 * Setze das Tabellenmodell
	 * @param	tb		javax.swing.table.DefaultTableModel
	 */	
	public static void setTableModel(TableModel tb) {
		tablemodel = tb;
	}
	
	
	/**
	 * Liefere Liste mit Tab-separierten Einträgen zurück.
	 * @return	java.util.ArrayList
	 */
	public ArrayList getTabSeparatedTableEntries(JTable table) {
		
		ArrayList l = new ArrayList();		
		
		for (int i=0; i < table.getRowCount(); i++) {
			StringBuffer b = new StringBuffer();
			for (int j = 0; j < table.getColumnCount(); j++) {
				b.append(table.getValueAt(i,j)).append("\t");
			}
			
			l.add(b.toString());
		}
		
		return l;
	}
	
	/**
	 * Schreibe Tabelleninhalt als XML-Datei
	 * @param	f	java.io.File
	 * @param	table	javax.swing.table
	 */
	public void writeXMLFile(File f,JTable table) {
	
		ArrayList l = XmlHandler.getXMLDocument(table);
		writeFile(f,l.iterator());		
	}
	
	
	public void writeTxtFile(File f,JTable table) {
	
		ArrayList l = getTabSeparatedTableEntries(table);
		writeFile(f,l.iterator());
		
	}



	protected void writeFile(File f,Iterator iter) {
		
		try {
			
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			
			while (iter.hasNext()) {
				out.write((String)iter.next());
				out.newLine();
			}
			
			out.flush();
			out.close();
		}
		catch (IOException io) {
			if (ApplHelper.DEBUG) io.printStackTrace();
		}
		
	}
	
    /**
     * Auslesen des XMLs aus einer Datei
     * @param	src	InputSource
     * @param	val	boolean
     */
    public void parseFile(String filename) throws SAXException, IOException {

    	
	 	// eventuell offene Verbindungen schliessen & Tabelle ablöschen
	 	resetViewConnections();

		try {
		    ApplHelper.getAction("startListeningFromSocketAction").setEnabled(false);
		    ApplHelper.getAction("stopListeningFromSocketAction").setEnabled(false);
		}
		catch (Exception e) {
			if (ApplHelper.DEBUG) e.printStackTrace();
		}

	    // Delegation an den XMLHandler (Validation = true)
		updateTableModel( xmlHandler.parseFile( filename,true ) );	   
		Monitor.getSingleInstance().setConnectionStatus( filename ); 	   	    	    
     
    }
    
    protected void updateTableModel( Collection c ) {
    	
	    Iterator iter = c.iterator();
	    
	    while ( iter.hasNext() ) {
	    	Map m = (Map)iter.next();
		    if (tablemodel instanceof FilterTableModel) {
		    	((FilterTableModel)tablemodel).addRow(m);
		    }
	    }
    	
    	
    }	
	
	public String convertListToString(ArrayList l) {
		
		Iterator iter = l.iterator();
		
		StringBuffer val = new StringBuffer();
		
		while (iter.hasNext()) 
			val.append(iter.next()).append("\n");
			
		return val.toString();
	}
	

	public void resetViewConnections() {
		
		if (tablemodel instanceof com.wanci.dmerce.gui.monitor.TableSorter)
			((com.wanci.dmerce.gui.monitor.TableSorter)tablemodel).clear();		
			
		processReadFromSocket = false;			
	}

	 public void openSocketConnnection(Container con,String hname,int port) {
	 	
		try {
		    ApplHelper.getAction("startListeningFromSocketAction").setEnabled(false);
		    ApplHelper.getAction("stopListeningFromSocketAction").setEnabled(true);
		}
		catch (Exception e) {
			if (ApplHelper.DEBUG) e.printStackTrace();
		}
	 	
	 		 		 	
		resetViewConnections();

	 	final Container outer = con;
	 	final int oport = port;
	 	final String hostname = hname;

		Thread t = new Thread() {
			public void run() {				
				
				StringBuffer buf = null;								
			 	// Verbindung zum Server herstellen
			 	if (ApplHelper.DEBUG) System.out.println("Connecting to Server "+hostname+" "+oport+"...");
			 	
		      try {
		      	
		        echoSocket = new Socket(hostname,oport);
		        
				Monitor.getSingleInstance().setConnectionStatus( "Verbunden mit "+hostname +" auf "+ oport ); 	   	    	    		        
		         
				out = new PrintWriter(echoSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
					
				String val;
				out.print("Requestin Log File...\n");
				out.flush();
				
				//
				// als Default immer alle Informationen anzeigen
				//
				processReadFromSocket = true;
				listening = true;

				//
		        // Terminierung bei processRedFromSocket = false; 
		        //
		        while (processReadFromSocket) {			         
		     
		          	val =  in.readLine();
		          	
		          	//
					// nur parsen, wenn auch "gehorcht" werden soll
					//
					if(listening) {
			         			         
		          		if (buf != null) {
		          			buf.append(val);
		          		}
			          	
		          		if (val.indexOf("<?xml") != -1) {
		          			if (ApplHelper.DEBUG) System.out.println("*** NEUES DOKUMENT:");
		          			buf = new StringBuffer();
		          			buf.append(val.substring(val.indexOf("<?xml")));
		          		}
		          		
		          				          			          		
		          		if (val.indexOf("</LOG>") != -1) {
		          			
		          			if (ApplHelper.DEBUG) {
		          				System.out.println("*** ENDE DES DOKUMENTES:");	          			
		          				System.out.println(">>Parsing XML Data:\n"+buf.toString());
		          			}
		          			
		          			String arg = buf.toString().replace('\t',' ');
		          			CharArrayReader c = new CharArrayReader(arg.toCharArray());
		          			try {
		          				updateTableModel( xmlHandler.parseSocket(new InputSource(c),false) );
		          			} catch (Exception e) {
		          				if (ApplHelper.DEBUG) e.printStackTrace();
								Monitor.getSingleInstance().setConnectionStatus( "Fehler aufgetreten bei Verbindung mit "+hostname +"auf "+ oport ); 	   	    	    		        		          				
		          			}
		          			buf = null;
		          		}		          		
					}
			          		
		          }
		          
		        // closeConnection 
		        in.close();
			 	out.close();
				echoSocket.close();
		          
		      } catch (UnknownHostException e) {
		      	 JOptionPane.showMessageDialog(outer, ApplHelper.getLocString("error.socket.connect"),"", JOptionPane.ERROR_MESSAGE);
		          if (ApplHelper.DEBUG) System.err.println("Don't know about host"+hostname);
				  Monitor.getSingleInstance().setConnectionStatus( "Fehler aufgetreten bei Verbindung mit "+hostname +"auf "+ oport ); 	   	    	    		        		          				
		          
		      } catch (IOException e) {
					JOptionPane.showMessageDialog(outer, ApplHelper.getLocString("error.socket.read"),"", JOptionPane.ERROR_MESSAGE); 				    			      	
					if (ApplHelper.DEBUG) System.err.println("Couldn't get I/O for "+ "the connection to: "+hostname+" on Port "+oport);
					Monitor.getSingleInstance().setConnectionStatus( "Fehler aufgetreten bei Verbindung mit "+hostname +"auf "+ oport ); 	   	    	    		        		          									
		      }	 	
		   }
		 };
		 t.setDaemon(true);
		 t.start();
	 }
	











  



}
