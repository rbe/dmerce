/*
 * Created on Jul 9, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.qxtl;

import java.util.Vector;

/**
 * @author pg
 * @author mm
 */
public interface TransactionBuilderInterface {

	/**
	 * 
	 */
	boolean addStep(String type, String command, Vector parameter);
	
	/**
	 * 
	 */
	void clearSteps(String type);
	
	/**
	 * 
	 */
	String toString();
	
}
