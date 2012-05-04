/*
 * Created on Sep 1, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.wanci.dmerce.test;

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.JAXBException;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.webservice.db.JAXBTypeMap;

/**
 * @author pg
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class JAXBBindingTest {

	public static void main(String[] args) throws JAXBException {
            
            
            JAXBTypeMap typemap = new JAXBTypeMap();
            
            Calendar now = Calendar.getInstance();
            Date nowDate = now.getTime();
            long nowLong = nowDate.getTime();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(nowLong);
            
            String nowString = typemap.toString(timestamp);
            System.out.println("#A: "+nowString);
            
            try {
                Calendar nowParsed = (Calendar) typemap.toQResultObject(nowString, java.util.Calendar.class);
                // Calendar nowParsed = (Calendar) typemap.toQResultObject("", java.util.Calendar.class);
                System.out.println("#B: "+nowParsed);
                System.out.println(typemap.toJDBCObject(nowParsed).toString());
                
            }
            catch (DmerceException e) {
                e.printStackTrace();
            }
            
		
		// JAXBContext.newInstance("com.wanci.dmerce.webservice.db.xmlbridge" );
		
                /*
                DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, java.util.Locale.GERMANY);
                String ausgabe = formatter.format(Calendar.getInstance(java.util.Locale.GERMANY).getTime());
                
		System.out.println(ausgabe);
                
                try {
                    Calendar parsedDate = Calendar.getInstance(java.util.Locale.GERMANY);
                    parsedDate.setTime(formatter.parse(ausgabe));

                    ausgabe = formatter.format(parsedDate.getTime());
                    System.out.println(ausgabe);
                }
                catch (Exception e) {
                    System.out.println("Fehler!");
                }
                 */
		
		// System.out.println(DatatypeConverter.parseDate(codiert).toString());
		
		// codiert = DatatypeConverter.printDouble(98.7654);
		// System.out.println(codiert);
		
	}
}
