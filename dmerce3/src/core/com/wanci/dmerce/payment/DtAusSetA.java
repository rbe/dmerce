/*
 * Created on 06.02.2004
 *  
 */
package com.wanci.dmerce.payment;

import java.util.Date;

import com.wanci.java.IOUtil;
import com.wanci.java.LangUtil;
import com.wanci.java.TimeUtil;

/**
 * @author rb
 * @version ${Id}
 * 
 * Datentraeger-Vorsatz
 *  
 */
public class DtAusSetA {

	/**
	 * Satzlaenge, immer 128 Bytes 1, 4 Bytes, num
	 */
	private String satzlaenge = "128";
	private int satzlaengeLength = 4;
	/**
	 * Satzart, immer 'A' 2, 1 Byte, alpha
	 */
	private String satzart = "A";
	private int satzartLength = 1;
	/**
	 * Kennzeichen 3, 2 Bytes, alpha, GK/LK, GB/LB G=Gutschrift L=Lastschrift
	 * K=Kundendiskette B=Bankdiskette In unserem Falle also: LK
	 */
	private String kennzeichen;
	private int kennzeichenLength = 2;
	/**
	 * Bankleitzahl 4, 8 Bytes, alpha
	 */
	private String bankleitzahl;
	private int bankleitzahlLength = 8;
	/**
	 * ?!?!, 5, 8 Bytes, Nullen
	 */
	private String a5 = "0";
	private int a5Length = 8;
	/**
	 * Kundenname, 6, 27 Bytes, alpha
	 */
	private String kundenname;
	private int kundennameLength = 27;
	/**
	 * Diskettenerstellungsdatum, TTMMJJ, 7, 6 Bytes, num
	 */
	private Date datum;
	private int datumLength = 6;
	/**
	 * Reserve, 8, 4 Bytes, Leerstellen
	 */
	private String a8 = " ";
	private int a8Length = 4;
	/**
	 * Kontonummer, 9, 10 Bytes, num
	 *  
	 */
	private String kontonummer;
	private int kontonummerLength = 10;
	/**
	 * Referenznummer - Angabe freigestellt
	 */
	private String referenznummer = "0";
	private int referenznummerLength = 10;
	/**
	 * Reserve, 11a, 15 Bytes
	 */
	private String a11a = " ";
	private int a11aLength = 15;
	/**
	 * Ausfuehrungsdatum Angabe freigestellt, nicht juenger als Diskettendatum
	 * und nicht aelter als + 15 Tage ueber 'datum', 11b, 6 Bytes, TTMMJJJJ
	 */
	private Date ausfuehrungsdatum;
	private int ausfuehrungsdatumLength = 8;
	/**
	 * Reserve, 11c, 24 Bytes, Leerstellen
	 */
	private String a11c = " ";
	private int a11cLength = 24;
	/**
	 * Waehrungskennzeichen, 12, 1 Byte, 1 = EURO
	 */
	private String waehrungskennzeichen = "1";
	private int waehrungskennzeichenLength = 1;

	public DtAusSetA() {
	}

	String getSatzlaenge() {

		return IOUtil.justify(satzlaenge, IOUtil.RIGHT, satzlaengeLength, "0");

	}

	String getSatzart() {

		return IOUtil.justify(satzart, IOUtil.LEFT, satzartLength, " ");

	}

	/**
	 * Setze Kennzeichen
	 * 
	 * @param kennzeichen
	 */
	void setKennzeichen(String kennzeichen) {

		if (kennzeichen.equals("GK")
			|| kennzeichen.equals("LK")
			|| kennzeichen.equals("GB")
			|| kennzeichen.equals("LB"))
			this.kennzeichen = kennzeichen;

	}

	/**
	 * Liefere Kennzeichen zurueck
	 * 
	 * @return
	 */
	String getKennzeichen() {
		return IOUtil.justify(kennzeichen, IOUtil.LEFT, kennzeichenLength, " ");
	}

	void setBankleitzahl(String bankleitzahl) {
		this.bankleitzahl = bankleitzahl;
	}

	String getBankleitzahl() {

		return IOUtil.justify(
			bankleitzahl,
			IOUtil.LEFT,
			bankleitzahlLength,
			"0");

	}

	String getA5() {
		return IOUtil.justify(a5, IOUtil.LEFT, a5Length, "0");
	}

	void setKundenname(String kundenname) {
		kundenname = LangUtil.normalizeUmlauts(kundenname);
		this.kundenname = kundenname;
	}

	String getKundenname() {

		return IOUtil.justify(
			kundenname.toUpperCase(),
			IOUtil.LEFT,
			kundennameLength,
			" ");

	}

	void setDatum(Date datum) {
		this.datum = datum;
	}

	String getDatum() {

		return IOUtil.justify(
			TimeUtil.formatDate(datum, "ddMMyy"),
			IOUtil.LEFT,
			datumLength,
			" ");

	}

	String getA8() {
		return IOUtil.justify(a8, IOUtil.LEFT, a8Length, " ");
	}

	void setKontonummer(String kontonummer) {
		this.kontonummer = kontonummer;
	}

	String getKontonummer() {

		return IOUtil.justify(
			kontonummer,
			IOUtil.RIGHT,
			kontonummerLength,
			"0");

	}

	void setReferenznummer(String referenznummer) {
		this.referenznummer = referenznummer;
	}

	String getReferenznummer() {

		return IOUtil.justify(
			referenznummer,
			IOUtil.LEFT,
			referenznummerLength,
			"0");

	}

	String getA11a() {
		return IOUtil.justify(a11a, IOUtil.LEFT, a11aLength, " ");
	}

	void setAusfuehrungsdatum(Date ausfuehrungsdatum) {
		this.ausfuehrungsdatum = ausfuehrungsdatum;
	}

	String getAusfuehrungsdatum() {

		String s;

		if (ausfuehrungsdatum != null)
			s =
				IOUtil.justify(
					TimeUtil.formatDate(ausfuehrungsdatum, "ddMMyyyy"),
					IOUtil.LEFT,
					ausfuehrungsdatumLength,
					" ");
		else
			s = IOUtil.justify(" ", IOUtil.LEFT, ausfuehrungsdatumLength, " ");

		return s;

	}

	String getA11c() {
		return IOUtil.justify(a11c, IOUtil.LEFT, a11cLength, " ");
	}

	String getWaehrungskennzeichen() {

		return IOUtil.justify(
			waehrungskennzeichen,
			IOUtil.LEFT,
			waehrungskennzeichenLength,
			" ");

	}

	/**
	 *  
	 */
	public String toString() {

		String s =
			getSatzlaenge()
				+ getSatzart()
				+ getKennzeichen()
				+ getBankleitzahl()
				+ getA5()
				+ getKundenname()
				+ getDatum()
				+ getA8()
				+ getKontonummer()
				+ getReferenznummer()
				+ getA11a()
				+ getAusfuehrungsdatum()
				+ getA11c()
				+ getWaehrungskennzeichen();

		return s.substring(0, 128);

	}

}