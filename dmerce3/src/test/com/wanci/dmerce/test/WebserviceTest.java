/*
 * WebserviceTest.java
 *
 * Created on August 27, 2003, 11:28 AM
 */

package com.wanci.dmerce.test;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.wanci.dmerce.impl.webservice.db.SQLWebserviceImpl;
import com.wanci.dmerce.webservice.SQLService;
import com.wanci.dmerce.webservice.db.QResult;

/**
 *
 * @author  pg
 */
public class WebserviceTest {
    
    /** Creates a new instance of WebserviceTest */
    public WebserviceTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WebserviceTest o = new WebserviceTest();
        //o.localUpdate();
        //o.remoteDelete();
        //o.localInsert();
        //o.remoteInsert();
        //o.remoteUpdate();
        o.remoteQuery();
        //o.localQuery();
    }
    
    private void remoteQuery() {
        try {
            SQLService service = new SQLService();
            QResult result = service.executeQuery("SELECT * FROM VKUNDE");
            if (result.success()) {
                System.out.println(result.toString());
                System.out.println(result.getTypeInfo());
                System.out.println(result.toString());
            }
            else {
                System.out.println("QResult enthält Fehlermeldungen:");
                System.out.println(result.getErrorMessage());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void localQuery() {
        try {
            SQLWebserviceImpl service = new SQLWebserviceImpl();
            String resultstring = service.executeQuery("SELECT * FROM VKUNDE");
            System.out.println(resultstring);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void localInsert() {
        try {
            SQLWebserviceImpl service = new SQLWebserviceImpl();
            Map fields = new HashMap();
            fields.put("NAME", "Nachname88");
            fields.put("VORNAME", "Vorname88");
            fields.put("EMAIL", "abc@def.de");
            Calendar cal = Calendar.getInstance();
            cal.set(2001, 9, 3, 0, 0, 0);
            fields.put("GEBURTSTAG", cal);
            int resultint = service.insertData("VKUNDE", "KUNDENNR", fields);
            System.out.println(resultint);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void localUpdate() {
        try {
            SQLWebserviceImpl service = new SQLWebserviceImpl();
            Map fields = new HashMap();
            fields.put("KUNDENNR", new Integer(86));
            fields.put("VORNAME", "abcdefg");
            fields.put("NAME", "edited1023");
            fields.put("EMAIL", "ab@cdef");
            fields.put("STRASSE", "Heisstr. 51");
            fields.put("TELEFON", "0251-123456789");
            Calendar cal = Calendar.getInstance();
            cal.set(1952, 4, 29, 0, 0, 0);
            fields.put("GEBURTSTAG", cal);
            int resultint = service.updateData("VKUNDE", "KUNDENNR", fields);
            System.out.println(resultint);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void remoteInsert() {
        try {
            SQLService service = new SQLService();
            Map fields = new HashMap();
            fields.put("VORNAME", "abcdefghij");
            fields.put("NAME", "-edited1023");
            fields.put("EMAIL", "ab@cdef");
            fields.put("STRASSE", "Heisstr. 51");
            fields.put("TELEFON", "0251-123456789");
            Calendar cal = Calendar.getInstance();
            cal.set(1952, 4, 29, 0, 0, 0);
            fields.put("GEBURTSTAG", cal);
            int resultint = service.insertData("VKUNDE", "KUNDENNR", fields);
            System.out.println("Datensatz eingefügt. Primary Key ist: "+resultint);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void remoteUpdate() {
        try {
            SQLService service = new SQLService();
            Map fields = new HashMap();
            fields.put("KUNDENNR", new Integer(86));
            /*
            fields.put("VORNAME", "hallo456");
            fields.put("NAME", "hallo123");
            fields.put("EMAIL", "ab@cdef");
            fields.put("STRASSE", "Heisstr. 51");
            fields.put("TELEFON", "0251-789");
             */
            Calendar cal = Calendar.getInstance();
            cal.set(1985, 1, 11, 10, 10, 30);
            fields.put("GEBURTSTAG", cal);
            int resultint = service.updateData("VKUNDE", "KUNDENNR", fields);
            System.out.println("Datensatz verändert. Primary Key ist: "+resultint);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void localDelete() {
        try {
            SQLWebserviceImpl service = new SQLWebserviceImpl();
            int resultint = service.deleteData("VKUNDE", "KUNDENNR", 17);
            System.out.println("Betroffene Datensätze: "+resultint);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void remoteDelete() {
        try {
            SQLService service = new SQLService();
            int resultint = service.deleteData("VKUNDE", "KUNDENNR", 16);
            System.out.println("Betroffene Datensätze: "+resultint);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
