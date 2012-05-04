/*
 * Created on Oct 22, 2003
 *
 */
package com.wanci.dmerce.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * @author tw2
 *
 * @version $id$
 */
public class PropertiesStoreLoadTest {

	public static void main(String[] args) {


		//Schreibt 2 definierte Werte in ein Property-Objekt, welches dann gespeichert wird
		Properties prop = new Properties();

		String value1 = "jdbc:oracle:thin:pg/pg@10.48.35.3:1521:wanci2";
		prop.put("Verbindung1", value1);

		String value2 = "jdbc:mysql:thin:tw/tw@10.48.30.12:1521:dmerce_test";
		prop.put("Verbindung2", value2);

		try {

			FileOutputStream out =
				new FileOutputStream("PropertiesStoreLoadTest.ini");
			prop.store(out, "---No Comment---");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		//Liest 2 Werte aus der Datei aus und schreibt diese in ein neues Properity-Objekt
		Properties prop2 = new Properties();

		try {

			FileInputStream in =
				new FileInputStream("PropertiesGetSetTest.ini");
			prop2.load(in);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Wert1 " +  prop2.getProperty("Verbindung1"));
		System.out.println("Wert2 " +  prop2.getProperty("Verbindung2"));
		
		System.out.println();
		
		//gibt alle Schlüssel des Propertie-Objektes wieder
		System.out.println("Schlüssel");
		
		int i = 1;
		
		Enumeration keys = prop2.propertyNames() ;
		
		while (keys.hasMoreElements()) {
			
			System.out.println(i + ": " + keys.nextElement());
			i++;
			
		}

		System.out.println();

		//Teilt ein Properties-Objekt auf
		
		StringTokenizer st = new StringTokenizer(value1, ":");
		while (st.hasMoreTokens()) {
		
			System.out.println(st.nextToken());
		
		}
		
		 		
	}

}
