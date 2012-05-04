/*
 * Created on Mar 18, 2004
 *
 */
package com.wanci.dmerce.generator;

import java.util.Vector;


/**
 * @author rb2
 * @version $Id: Form.java,v 1.1 2004/03/19 14:25:18 rb Exp $
 *
 */
public class Form {

	Vector fields=new Vector();
	
	public Form() {
		
	}
	
	public void addField(Field field) {
		fields.add(field);
	}
	
	public void deleteField(Field field) {
		fields.remove(field);
	}
	
}