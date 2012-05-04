/*
 * Created on 25.06.2003
 */
package com.wanci.java;

import java.beans.XMLEncoder;

/**
 * @author pg
 * @version $Id: Util.java,v 1.1 2004/03/29 13:39:39 rb Exp $
 *
 * Helper Class for debugging features
 *
 */
public class Util {
	
	public static void dump(Object o) {
		XMLEncoder enc = new XMLEncoder(System.out);
		enc.writeObject(o);
		enc.close();
	}
	
	public static void debug(String str) {
		System.out.println(str);
	}

}