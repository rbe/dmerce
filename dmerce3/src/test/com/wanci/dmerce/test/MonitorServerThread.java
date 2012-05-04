package com.wanci.dmerce.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class MonitorServerThread extends Thread {
    private Socket socket = null;
    
    
	 public final static String TAG_LOG      = "LOG";
	 public final static String TAG_ENTRY    = "ENTRY";
	 public final static String TAG_DATE     = "DATE";
	 public final static String TAG_CLIENT   = "CLIENT";
	 public final static String TAG_PROXY    = "PROXY";
	 public final static String TAG_TEMPLATE = "TEMPLATE";
	 public final static String TAG_MODULE   = "MODULE";
	 public final static String TAG_MESSAGE  = "MESSAGE";
	 public final static String TAG_MSGTYPE	 = "MSGTYPE";
    
    private static Random r = new Random();
    
    public String[] val_date = new String[] {
    	"1004098003.978141",
    	"1005098005.978141",
    	"1006098011.978141",
    	"1007098012.978141",
    	"1008098031.978141",
    	"1010098030.978141",
    	"1006098022.978141",
    };
    
    
    
    public String[] val_client = new String[] {
    	"10.48.30.160",
    	"12.14.22.310",
    	"30.48.30.160",
    	"22.00.33.189",
    	"11.38.55.111",
    	"53.12.33.123",
		"10.17.65.430"
    };
    
    public String[] val_proxy = new String[] {
    	"12.48.35.1",
    	"11.48.35.3",
    	"14.32.35.4",
    	"10.61.35.6",
    	"12.48.35.1",
    	"41.48.35.5",
    	"66.12.05.7"
     };

    public String[] val_template = new String[] {
    	"None",
    	"Default",
    	"e-com",
    	"dmerce",
    	"shop"
     };

    public String[] val_module = new String[] {
    	"Core.ShowError ERROR",
    	"DMS.SuperSearch",
    	"DMERCE.ErrorHandler",
    	"Core.LogMode",
    	"Core.Dump"
     };

    public String[] val_messages = new String[] {
    	"Please inform System Administrator",
    	"Unknown Table Error /DBMS STOP",
    	"TYPE OF UXSI=type 'string'",
    	"Memory Overflow"    	
     };

    public String[] val_msgtype = new String[] {
    	"ERROR",
    	"INFO",
    	"INFO",
    	"INFO",    	
    	"WARNING",
    	"INFO",
    	"DEBUG",
       	"INFO",
       	"INFO",  	
    	"ACCOUNT",
    	"INFO",
    	"INFO"    	
     };


    public MonitorServerThread(Socket socket) {
	super("KKMultiServerThread");
	this.socket = socket;
    }



	public static String chooseElement(String[] arg) {
		return arg[r.nextInt(arg.length)];
	}

	public static String addElement(String tag,String value) {
		StringBuffer buf = new StringBuffer();
		buf.append(addStartTag(tag)).append(value).append(addEndTag(tag));
		return buf.toString();
	}

	public static String addStartTag(String tag) {
		StringBuffer buf = new StringBuffer();
		buf.append("<"+tag+">");
		return buf.toString();
	}
	
	public static String addEndTag(String tag) {
		StringBuffer buf = new StringBuffer();
		buf.append("</"+tag+">");
		return buf.toString();
	}

	/**
	 * Hauptmethode
	 */
    public void run() {

		System.out.println("Starting Child process...");
		// Server schreibt nur Log-Meldungen
		// Hört nicht auf Meldungen vom Client
		try {
		    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 //String nothing = in.readLine();
			 
	
			 PrintWriter out;
			 Random r = new Random();
		    
		    out = new PrintWriter(socket.getOutputStream(), true);		    	
		    
		    boolean flag=true;
		    while (flag) {
		    	
				 
		    	 // XML HEADER Information
		     	 //out.print("<?xml version=\"1.0\"?>\n<!DOCTYPE LOG SYSTEM \"log.dtd\">\n");
		    	 out.print("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>\n");
		    	 
			    out.print(addStartTag(TAG_LOG)+"\n");
			    
			    int max = r.nextInt(6)+1;
			    
			    for (int i=0; i < max ; i++) {
				    out.print(addStartTag(TAG_ENTRY)+"\n");				    
				    
				    out.print(addElement(TAG_DATE,chooseElement(val_date))+"\n");				    
				    out.print(addElement(TAG_CLIENT,chooseElement(val_client))+"\n");				    
				    out.print(addElement(TAG_PROXY,chooseElement(val_proxy))+"\n");				    
				    out.print(addElement(TAG_TEMPLATE,chooseElement(val_template))+"\n");				    				    
				    out.print(addElement(TAG_MODULE,chooseElement(val_module))+"\n");				    
					out.print(addElement(TAG_MESSAGE,chooseElement(val_messages))+"\n");				    
					out.print(addElement(TAG_MSGTYPE,chooseElement(val_msgtype))+"\n");				    

				    out.print(addEndTag(TAG_ENTRY)+"\n");
				  }
			    out.print(addEndTag(TAG_LOG)+"\n");
			    out.flush();
			    
			    try {
			    	Thread.sleep( (r.nextInt(4)+2) * 1000);
			    }
			    catch (InterruptedException e) {
			    	e.printStackTrace();
			    }
			    
			 }
	
		    out.close();
		    in.close();
		    socket.close();
	
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }
}
