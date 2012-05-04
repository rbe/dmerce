/**
 * Created on Jan 22, 2003
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package com.wanci.dmerce.test;

import java.util.Iterator;

/**
 * @author rb
 * 
 * Interface fuer einen Filter, der einen Vector von
 * @see LangUtil.systemCommand analysiert und Zugriffsmethoden zur
 * Verfuegung stellt
 */
public interface SystemCommandFilter {
	
	/**
     * @author rb
     * 
     * Analysiere das Ergebniss und ueberfuehre es (zeilenweise) in Objekte
     * 
	 */
	void analyse();
    	
	/**
     * Liefert einen Iterator ueber die analysierten Objekte zurueck
	 * @return Iterator ueber einen Vector
	 */
	Iterator iterator();
        
}