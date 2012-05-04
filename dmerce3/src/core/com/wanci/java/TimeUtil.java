/*
 * Created on Jun 27, 2003
 *
 */
package com.wanci.java;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author rb
 * @version $Id: TimeUtil.java,v 1.3 2004/02/10 17:55:57 rb Exp $
 *
 * Hilfsmethoden fuer das Behandeln, Umwandlen und Ausgeben von
 * Datum und Zeit.
 *
 */
public class TimeUtil {
    
    /**
     * Wandelt die Beschreibung von Zeit:
     *
     * 4h = 4 Stunden
     * 2d = 2 Tage
     * 7y = 7 Jahre
     *
     * in Sekunden um und gibt diese zurueck
     *
     * @param description
     * @return
     */
    public static long descriptionToSeconds(String description) {
        
        StringBuffer sb = new StringBuffer();
        char type = ' ';
        long value = -1;
        
        for (int i = 0; i < description.length(); i++) {
            
            char a = description.charAt(i);
            if (Character.isDigit(a))
                sb.append(a);
            else if (Character.isLetter(a))
                type = a;
            
            switch (type) {
                
                case 'h' :
                    value = new Integer(sb.toString()).intValue() * 3600;
                    break;
                case 'd' :
                    value = new Integer(sb.toString()).intValue() * 3600 * 24;
                    break;
                case 'y' :
                    value =
                    new Integer(sb.toString()).intValue() * 3600 * 24 * 12;
                    break;
                    
            }
            
        }
        
        return value;
    }
    
	/**
	 * Formatiert ein Datum
	 * 
	 * @param datum
	 * @return
	 */
	public static String formatDate(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}
    
	/**
	 * Liefert einen ISO-String des aktuellen Datum/der akutellen Zeit
	 * @return
	 */
	public static String getIsoTimeString() {
		return new SimpleDateFormat("yyyyMMddHms").format(new Date());
	}
	
}