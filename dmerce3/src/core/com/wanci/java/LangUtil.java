/*
 * JavaUtil.java
 * 
 * Created on January 6, 2003, 11:23 PM
 */

package com.wanci.java;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.mail.internet.InternetAddress;

/**
 * @author rb
 * 
 * Hilfen fuer java.lang.* bzw. generelle Probleme
 */
public class LangUtil {

	/** You cannot create an instance */
	private LangUtil() {
	}

	/**
	 * Retrieve characters from char array and return as string
	 * 
	 * @param charArray
	 * @param offset
	 * @param length
	 * @return
	 */
	public static String charArrayToString(char[] charArray, int offset,
			int length) {

		StringBuffer sb = new StringBuffer();
		for (int i = offset; i < offset + length; i++)
			sb.append(charArray[i]);
		return sb.toString();

	}

	public static void consoleDebug(boolean debug, String s) {

		if (debug)
			System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
					.format(new Date())
					+ " " + s);
	}

	/**
	 * Debug with date and class name
	 * 
	 * @param debug
	 * @param c
	 * @param s
	 */
	public static void consoleDebug(boolean debug, Class c, String s) {
		consoleDebug(debug, c.getClass() + " " + s);
	}

	/**
	 * Debug with date and class name
	 * 
	 * @param debug
	 * @param c
	 * @param s
	 */
	public static void consoleDebug(boolean debug, Object o, String s) {
		consoleDebug(debug, o + " " + s);
	}

	/**
	 * @param v
	 */
	public static void dumpVector(Vector v) {

		Iterator i = v.iterator();
		while (i.hasNext()) {

			Object o = i.next();
			System.out.println(o);

		}

	}

	/**
	 * Ensures that a string ends with a dot
	 * 
	 * @return String
	 */
	public static String ensureStringWithDotAtEnd(String s) {
		if (!s.endsWith("."))
			return s + ".";
		else
			return s;
	}

	/**
	 * Ist das Zeichen an der Position i in String s eine Zahl?
	 * 
	 * @param s
	 *            String
	 * @param i
	 *            Index in String s der geprueft werden soll
	 * @return boolean
	 * @author rb
	 */
	public static boolean isCharAtNumber(String s, int i) {
		int c = s.charAt(i);
		if (c >= 48 && c <= 57)
			return true;
		else
			return false;
	}

	/**
	 * Ist das Zeichen an der Position i in String s ein Buchstabe?
	 * 
	 * @param s
	 *            String
	 * @param i
	 *            Index in String s der geprueft werden soll
	 * @return boolean
	 * @author rb
	 */
	public static boolean isCharAtLetter(String s, int i) {
		int c = s.charAt(i);
		if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122))
			return true;
		else
			return false;
	}

	/**
	 * Convert umlauts to ASCII
	 * 
	 * @param s
	 * @return
	 */
	public static String normalizeUmlauts(String s) {

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {

			char c = s.charAt(i);
			if (c == 'ä')
				sb.append("ae");
			else if (c == 'ö')
				sb.append("oe");
			else if (c == 'ü')
				sb.append("ue");
			else if (c == 'Ä')
				sb.append("AE");
			else if (c == 'Ö')
				sb.append("OE");
			else if (c == 'Ü')
				sb.append("UE");
			else if (c == 'é')
				sb.append("e");
			else if (c == 'É')
				sb.append("E");
			else if (c == '\'')
				sb.append("");
			else if (c == ';')
				sb.append(",");
			else
				sb.append(c);

		}

		return sb.toString();

	}

	/**
	 * Liefert einen String-Array: - den Dateinamen - die Erweiterung eines
	 * Dateinamens
	 * 
	 * @param st
	 * @return
	 */
	public static String[] splitFilenameExtension(String fileName) {

		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(fileName, ".");

		int i = st.countTokens();
		for (int j = 0; j < i - 1; j++) {
			sb.append(st.nextToken());
		}

		return new String[] { sb.toString(), st.nextToken() };

	}

	/**
	 * Ueberfuehrt einen StringTokenizer in einen Vector
	 * 
	 * @param st
	 *            StringTokenizer
	 * @return Vector mit den Ergebnissen der Zerstueckelung
	 * @author rb
	 */
	public static Vector stringTokensToVector(StringTokenizer st) {

		Vector v = new Vector();
		while (st.hasMoreTokens()) {

			v.add(st.nextToken());

		}

		return v;

	}

	/**
	 * Fuehrt ein Kommando aus und gibt das Output zeilenweise zurueck
	 * 
	 * @param cmd
	 *            Kommando zur Ausfuehrung
	 * @return Iterator Iterator ueber einen Vector mit den Ergebnissen (das
	 *         Output zeilenweise)
	 * @author ka
	 */
	public static Iterator systemCommand(String cmd) {

		int i;
		StringBuffer sb = new StringBuffer();
		BufferedInputStream bis;
		Vector v = new Vector();

		Process p;
		Runtime r = Runtime.getRuntime();

		try {

			p = r.exec(cmd);
			bis = new BufferedInputStream(p.getInputStream());

			while ((i = bis.read()) != -1) {

				if (i == 10) {
					v.add(sb.toString());
					sb.delete(0, sb.length());
				} else
					sb.append((char) i);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return v.iterator();

	}

	/**
	 * Convert a vector an object array
	 * 
	 * @param v
	 * @return
	 */
	public static Array vectorToArray(Vector v, Class c) {

		//Object[] o = new Object[v.size()];
		Object arr = Array.newInstance(c, v.size());
		Iterator i = v.iterator();
		int j = 0;
		while (i.hasNext())
			//o[j++] = (c.) i.next();
			Array.set(arr, j++, i.next());

		return (Array) arr;

	}

	/**
	 * Convert a vector an InternetAddress[]
	 * 
	 * @param v
	 * @return
	 */
	public static InternetAddress[] vectorToInternetAddressArray(Vector v) {

		InternetAddress[] o = new InternetAddress[v.size()];
		Iterator i = v.iterator();
		int j = 0;
		while (i.hasNext())
			o[j++] = (InternetAddress) i.next();

		return o;

	}

	public static void main(String[] args) {

		consoleDebug(true, "test");

		String[] a = splitFilenameExtension("abcd.jpg");
		System.out.println(a[0] + " " + a[1]);

		/*
		 * Iterator i = LangUtil.systemCommand("ls -l"); while (i.hasNext()) {
		 * String s = (String) i.next(); System.out.println(s); }
		 * 
		 * System.out.println(LangUtil.isCharAtNumber("1", 0));
		 * System.out.println(LangUtil.isCharAtNumber("2", 0));
		 * System.out.println(LangUtil.isCharAtNumber("3", 0));
		 * System.out.println(LangUtil.isCharAtNumber("4", 0));
		 * System.out.println(LangUtil.isCharAtNumber("5", 0));
		 * System.out.println(LangUtil.isCharAtNumber("6", 0));
		 * System.out.println(LangUtil.isCharAtNumber("7", 0));
		 * System.out.println(LangUtil.isCharAtNumber("8", 0));
		 * System.out.println(LangUtil.isCharAtNumber("9", 0));
		 * System.out.println(LangUtil.isCharAtNumber("0", 0));
		 * 
		 * System.out.println(LangUtil.isCharAtLetter("a", 0));
		 * System.out.println(LangUtil.isCharAtLetter("z", 0));
		 * 
		 * System.out.println(LangUtil.isCharAtLetter("A", 0));
		 * System.out.println(LangUtil.isCharAtLetter("Z", 0));
		 */

	}

}