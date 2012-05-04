/*
 * Created on Oct 22, 2003
 *
 */
package com.wanci.dmerce.test;

import java.util.Enumeration;
import java.util.Properties;

/**
 * @author tw2
 *
 * @version $id$
 */
public class SystemPropertiesTest {

	public static void main(String[] args) {
		
		Properties sysprops = System.getProperties();
		Enumeration propnames = sysprops.propertyNames();
		while (propnames.hasMoreElements()) {
			String propname = (String)propnames.nextElement();
			System.out.println(
				propname + "=" + System.getProperty(propname)
			);
		}
		
	}
}
