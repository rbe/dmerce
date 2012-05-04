/*
 * Created on Oct 27, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.wanci.dmerce.dbmgr;

/**
 * @author tw
 * 
 * util class for some operations
 */
public class Util {

	/**
	 * debugging function
	 * @param d
	 * @param s
	 */
	public static void debug(boolean d, String s) {
		if (d)
			System.out.println(s);
	}

	/**
	 * usage example and test
	 * @param args
	 */
	public static void main(String[] args) {
		Util.debug(true, "hello");
	}
}
