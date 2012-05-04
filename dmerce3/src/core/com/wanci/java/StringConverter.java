/*
 * Datei angelegt am 21.10.2003
 */
package com.wanci.java;

import java.util.Collection;
import java.util.Iterator;

/**
 * 
 * @author Masanori Fujita
 * @version $Id: StringConverter.java,v 1.1 2004/03/29 13:39:39 rb Exp $
 */
public class StringConverter {

	public static String toString(Collection c) {
		
		Iterator it = c.iterator();
		String result = "";
		
		while (it.hasNext()) {
			Object o = (Object) it.next();
			result += o.toString();
		}
		
		return result;
		
	} 

}
