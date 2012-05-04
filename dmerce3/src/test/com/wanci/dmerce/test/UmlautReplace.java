/*
 * Created on Feb 15, 2004
 *  
 */
package com.wanci.dmerce.test;

/**
 * @author rb
 * @version $Id: UmlautReplace.java,v 1.1 2004/02/15 19:32:24 rb Exp $
 *  
 */
public class UmlautReplace {

	public static void main(String[] args) {

		String test = "ä ein satz mit ö umlauten ü";

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < test.length(); i++) {

			char c = test.charAt(i);
			if (c == 'ä')
				sb.append("ae");
			else if (c == 'ö')
				sb.append("oe");
			else if (c == 'ü')
				sb.append("ue");
			else if (c == 'Ä')
				sb.append("AE");
			else if (c == 'Ö')
				sb.append("OE");
			else if (c == 'Ü')
				sb.append("UE");
			else
				sb.append(c);

		}
		System.out.println(sb.toString());

	}

}