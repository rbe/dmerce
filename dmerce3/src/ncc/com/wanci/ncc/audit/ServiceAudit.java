/*
 * ServiceAudit.java
 *
 * Created on January 6, 2003, 7:48 PM
 */

package com.wanci.ncc.audit;

/** Definiert einen Audit/Datensatz für Monitoring von Diensten
 *
 * @author  rb
 * @version $Id: ServiceAudit.java,v 1.1 2004/02/02 09:41:53 rb Exp $
 */
public interface ServiceAudit {

	/** Ist das ServiceResource auf schwarz gesetzt?
	 */
    boolean isServiceBlack();
    
	/** Ist das ServiceResource auf grün gesetzt?
	 */
    boolean isServiceGreen();
    
	/** Ist das ServiceResource auf rot gesetzt?
	 */
    boolean isServiceRed();
    
	/** Ist das ServiceResource auf orange gesetzt?
	 */
    boolean isServiceOrange();
    
	/** Ist das ServiceResource auf gelb gesetzt?
	 */
    boolean isServiceYellow();
    
	/** Setze das ServiceResource auf schwarz
	 */
    void setServiceBlack();
    
	/** Setze das ServiceResource auf grün
	 */
    void setServiceGreen();
    
	/** Setze das ServiceResource auf rot
	 */
    void setServiceRed();
    
	/** Setze das ServiceResource auf orange
	 */
    void setServiceOrange();
    
	/** Setze das ServiceResource auf gelb
	 */
    void setServiceYellow();
    
    /** Gibt ein SQL-UPDATE-Statement zurück, mit dem die Datenbank
     * die Informationen aufnehmen kann
     */
    String toSqlUpdate();
    
}