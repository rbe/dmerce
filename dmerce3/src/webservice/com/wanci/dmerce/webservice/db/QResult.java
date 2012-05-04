/*
 * Created on Aug 20, 2003
 */
package com.wanci.dmerce.webservice.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Repr�sentiert die Ergebnismenge einer Datenbankabfrage. Das QResult-Objekt
 * enth�lt mehrere {@link QRow QRow}-Objekte.
 * @author Masanori Fujita
 * @version $Id: QResult.java,v 1.1 2004/03/29 13:39:37 rb Exp $
 */
public class QResult implements Serializable {
	
	private Collection rows;
	private String sqlStatement;
        private QTypeInfo typeinfo;
        private String error = null;
        
	/**
	 * Standard-Konstruktor
	 */

        /**
         * Standard-Konstruktor, der ein leeres Resultset erzeugt.
         * Die Rows m�ssen von einem XML-Java-Bridge gef�llt werden und sollten
         * auf keinen Fall manuell modifiziert werden.
         */
	public  QResult(String sqlStatement, QTypeInfo typeinfo) {
            this.rows = new ArrayList();
            this.typeinfo = typeinfo;
            this.sqlStatement = sqlStatement;
	}
        
        /**
         * Hilfsmethode f�r die XML-Java-Bridge zum Hinzuf�gen von QRows in das
         * Resultset. Diese Methode wird nur zum Auff�llen des QResult verwendet
         * und ist daher nur f�r Klassen dieser Package zug�nglich.
         */
        public void addRow(QRow row) throws QTypeMissmatchException {
            if (row.getTypeInfo().equals(this.typeinfo))
                this.rows.add(row);
            else
                throw new QTypeMissmatchException(this.typeinfo, row.getTypeInfo());
        }
        
	/**
	 * Gibt eine Liste von Ergebniszeilen zur�ck
	 * @return Liste von Rows
	 */
	public Collection getRows() {
		return rows;
	}
        
	/**
	 * Ermittelt die Anzahl der Datens�tze in der Ergebnismenge
	 * @return Anzahl der Datens�tze
	 */
	public int getRowCount() {
            return rows.size();
	}
	
	/**
	 * Gibt das SQL-Statement zur�ck, das diese Ergebnismenge geliefert hat.
	 * @return SQL-Statement der Ergebnismenge
	 */
	public String getSQLStatement() {
            return sqlStatement;
	}
        
        /**
         * Gibt das QTypeInfo-Objekt zu diesem QResult zur�ck.
         */
        public QTypeInfo getTypeInfo() {
            return this.typeinfo;
        }
        
        public String toString() {
            String result = "";
            Iterator it = getRows().iterator();
            while (it.hasNext()) {
                result += it.next().toString() + "\n";
            }
            return result;
        }
        
        /**
         * @return true, falls Abfrage erfolgreich, false sonst
         */
        public boolean success() {
            return error == null;
        }
        
        /**
         * Gibt bei Fehlschlagen der Abfrage eine aussagekr�ftige Fehlermeldung
         * zur�ck.
         * @return Fehlermeldung bei Fehlschlag, null sonst
         */
        public String getErrorMessage() {
            return error;
        }
        
        public void setError(String message) {
            if ( (message != null) && message.equals("") )
                this.error = null;
            else
                this.error = message;
        }
	
}
