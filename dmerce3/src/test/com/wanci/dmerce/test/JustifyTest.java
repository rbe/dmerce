/*
 * Created on 14.02.2004
 *  
 */
package com.wanci.dmerce.test;

import com.wanci.java.IOUtil;

/**
 * @author rb
 * @version $Id: JustifyTest.java,v 1.1 2004/02/15 18:20:44 rb Exp $
 *  
 */
public class JustifyTest {

	public static void main(String[] args) {

		String test = "Ahlers, Michael";
		int len = 27;
		String j = IOUtil.justify(test.toUpperCase(), IOUtil.LEFT, len, " ");

		System.out.println(j + " Length: " + j.length());

	}

}