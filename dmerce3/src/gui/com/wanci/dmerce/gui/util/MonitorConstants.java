package com.wanci.dmerce.gui.util;

import java.awt.Color;

/**
 * Enthält alle Konstanten die in der Monitor-Anwendung
 * verwendet werden.
 * @author	Ron Kastner
 */
public interface MonitorConstants {

	 public final static String TAG_ENTRY    = "ENTRY";
	 public final static String TAG_DATE     = "DATE";
	 public final static String TAG_CLIENT   = "CLIENT";
	 public final static String TAG_PROXY    = "PROXY";
	 public final static String TAG_TEMPLATE = "TEMPLATE";
	 public final static String TAG_MODULE   = "MODULE";
	 public final static String TAG_MESSAGE  = "MESSAGE";
	 public final static String TAG_MSGTYPE  = "MSGTYPE";
	 
	 public final static int COL_DATE     = 0;
	 public final static int COL_CLIENT   = 1;
	 public final static int COL_PROXY    = 2;
	 public final static int COL_TEMPLATE = 3;
	 public final static int COL_MODULE   = 4;
	 public final static int COL_MESSAGE  = 5;
	 public final static int COL_MSGTYPE  = 6;
	 
	 public final static int LOG_ROW_SIZE = 7;

	 public final static String XML_ROW_ADDED = "XML_ROW_ADDED";
	 
/*
	 public final static Color COLOR_MSG_ERROR = new Color(251,167,169); // rot
	 public final static Color COLOR_MSG_WARNING = new Color(251,197,197);
	 public final static Color COLOR_MSG_INFO = Color.white;
	 public final static Color COLOR_MSG_DEBUG = new Color(255,255,160);
	 public final static Color COLOR_MSG_ACCOUNT = new Color(220,220,220);
*/
	 public final static Color COLOR_MSG_ERROR = new Color(155,155,155); 
	 public final static Color COLOR_MSG_WARNING = new Color(190,190,190);
	 public final static Color COLOR_MSG_INFO = Color.white;
	 public final static Color COLOR_MSG_DEBUG = new Color(210,210,210);
	 public final static Color COLOR_MSG_ACCOUNT = new Color(240,240,240);

	 public final static String MSGTYPE_ERROR = "ERROR";
	 public final static String MSGTYPE_WARNING = "WARNING";
	 public final static String MSGTYPE_INFO = "INFO";
	 public final static String MSGTYPE_DEBUG = "DEBUG";
	 public final static String MSGTYPE_ACCOUNT= "ACCOUNT";
	 
		 
	 
}

