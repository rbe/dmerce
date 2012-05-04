/*
 * Created on Jul 7, 2003
 *
 */
package com.wanci.partner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * 
 * @author rb
 * @version $Id: ParseSunRefurbMail.java,v 1.1 2003/12/03 19:58:32 rb Exp $
 *
 * Ein Sun-Refurb-Article
 * 
 */
class SunRefurbArticle {

	/**
	 * Rabatt-Klasse bei Sun
	 */
	private char category;

	/**
	 * Beschreibung des Artikels
	 */
	private String description;

	/**
	 * Bezeichnung des Artikels
	 */
	private String nameOfItem;

	/**
	 * Preis des Artikels
	 */
	private float pricePerItem;

	/**
	 * Artikel-Nummer bei Sun Microsystems
	 */
	private String vendorArticleNumber;

	/**
	 * 
	 *
	 */
	SunRefurbArticle() {
	}

	/**
	 * Liefert die Artikel-Nummer des Herstellers
	 * @return
	 */
	public String getVendorArticleNumber() {
		return vendorArticleNumber;
	}

	/**
	 * Setzt die Rabatt-Klasse bei Sun
	 * @param category
	 */
	public void setCategory(char category) {
		this.category = category;
	}

	/**
	 * Setzt die Beschreibung des Artikels
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Setzt die Bezeichnung des Artikels
	 * @param nameOfItem
	 */
	public void setNameOfItem(String nameOfItem) {
		this.nameOfItem = nameOfItem;
	}

	/**
	 * Setzt den Preis des Artikels
	 * @param pricePerItem
	 */
	public void setPricePerItem(String pricePerItem) {
		this.pricePerItem = Float.parseFloat(pricePerItem);
	}

	/**
	 * Setzt den Preis des Artikels
	 * @param pricePerItem
	 */
	public void setPricePerItem(float pricePerItem) {
		this.pricePerItem = pricePerItem;
	}

	/**
	 * Setzt die Hersteller Artikel-Nummer
	 * @param articleNumber
	 */
	public void setVendorArticleNumber(String vendorArticleNumber) {
		this.vendorArticleNumber = vendorArticleNumber;
	}

}

/**
 * @author rb
 * @version $Id: ParseSunRefurbMail.java,v 1.1 2003/12/03 19:58:32 rb Exp $
 *
 * Liest eine Refurb Mail von Sun ein und stellt die Artikel-Daten
 * in eine Datenbank ein
 * 
 */
public class ParseSunRefurbMail {

	/**
	 * Lies eine Sun-Refurb-Mail aus einer Datei ein
	 */
	private BufferedReader r;

	/**
	 * Speichert alle Instanzen, die einen Sun Refurb Artikel
	 * representieren
	 */
	private Vector sunRefurbArticles = new Vector();

	/**
	 * 
	 *
	 */
	public ParseSunRefurbMail(BufferedReader r) {
		this.r = r;
	}

	/**
	 * 
	 * @param f
	 */
	public ParseSunRefurbMail(File f) throws FileNotFoundException {
		this.r = new BufferedReader(new FileReader(f));
	}

	/**
	 * Parst die Sun-Refurb-Mail und filtert alle Artikel
	 * heraus. Es werden einige Objekte vom Typ SunRefurbArticle
	 * angelegt 
	 *
	 */
	public void parseMail() throws IOException {

		String line;
		boolean parse = false;
		while ((line = r.readLine()) != null) {

			// Beginne das Parsen erst, nachdem die Zeile
			// mit Gleichheitszeichen am Anfang gefunden wurde
			if (!parse && line.charAt(0) == '=')
				parse = true;

			if (parse) {

				// Verarbeite nur nicht-leere Zeilen
                if (line.length() > 0) {

					StringTokenizer st = new StringTokenizer(line);
					String vendorArticleNumber = st.nextToken();
					String pricePerItem = st.nextToken();
					String nameOfItem = st.nextToken();
					String category = st.nextToken();

					// Wenn alle wichtigen Angaben wie Artikel-Nummer des
                    // Herstellers, Preis und Bezeichnung vorhanden sind,
                    // dann lege einen neuen Artikel an
                    if (vendorArticleNumber != null
						&& pricePerItem != null
						&& nameOfItem != null) {

						SunRefurbArticle s = new SunRefurbArticle();
						s.setVendorArticleNumber(vendorArticleNumber);
						s.setPricePerItem(pricePerItem);
						s.setNameOfItem(nameOfItem);
						s.setCategory(category.charAt(6));
                        
                        sunRefurbArticles.add(s);

					}

				}
                // und beende das Parsen nach der naechsten Leerzeile
				else if (line.length() == 0)
					parse = false;

			}

		}

	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		ParseSunRefurbMail prm;
		try {

			prm =
				new ParseSunRefurbMail(
					new File("/export/home/rb/refurb-mail.txt"));
			prm.parseMail();

		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

}