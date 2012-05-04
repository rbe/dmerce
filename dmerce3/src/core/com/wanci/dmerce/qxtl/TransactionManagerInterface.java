/*
 * Created on Jul 9, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.qxtl;

import org.jdom.Element;


/**
 * @author mm
 * $Id: TransactionManagerInterface.java,v 1.1 2003/08/26 13:07:54 mm Exp $
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface TransactionManagerInterface {

	/** 
	 * @return
	 */
	Element executeTransaction(Element transaction);

}
