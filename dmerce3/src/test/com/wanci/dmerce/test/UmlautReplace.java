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

		String test = "� ein satz mit � umlauten �";

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < test.length(); i++) {

			char c = test.charAt(i);
			if (c == '�')
				sb.append("ae");
			else if (c == '�')
				sb.append("oe");
			else if (c == '�')
				sb.append("ue");
			else if (c == '�')
				sb.append("AE");
			else if (c == '�')
				sb.append("OE");
			else if (c == '�')
				sb.append("UE");
			else
				sb.append(c);

		}
		System.out.println(sb.toString());

	}

}