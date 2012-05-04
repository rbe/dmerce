/*
 * Created on 06.02.2004
 *  
 */
package com.wanci.dmerce.payment;

import java.text.NumberFormat;
import java.text.ParseException;

import com.wanci.java.IOUtil;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: DtAusSetC.java,v 1.10 2004/06/16 11:09:01 rb Exp $
 * 
 * Zahlungsaustauschsatz
 *  
 */
public class DtAusSetC {

	/**
	 * Satzlaenge: 1, 4 Bytes, 187 Bytes + Erweiterungsteile * 29 Bytes
	 */
	private String satzlaenge;

	private int satzlaengeLength = 4;

	/**
	 * Satzart, 2, 1 Byte, immer "C"
	 */
	private String satzart = "C";

	private int satzartLength = 1;

	/**
	 * Bankleitzahl erstbeteiligtes Institut, 3, 8 Bytes, num
	 */
	private String blzErstInst = "00000000";

	private int blzErstInstLength = 8;

	/**
	 * Bankleitzahl: Kreditinstitut des Beguenstigten/Zahlstelle, 4, 8 Bytes,
	 * num
	 */
	private String c4;

	private int c4Length = 8;

	/**
	 * Kontonummer des Beguenstigten/Zahlstelle, 5, 10 Bytes, num, rechtsbuendig
	 */
	private String c5;

	private int c5Length = 10;

	/**
	 * Interne Kundennummer: 6, 13 Bytes, num, 1. und 13. Byte immer 0
	 */
	private String interneKundennummer = "0";

	private int interneKundennummerLength = 13;

	/**
	 * Textschluessel, 7, 2 Bytes, num
	 */
	private String textschluessel;

	private int textschluesselLength = 2;

	/**
	 * Textschluesselergaenzung, 8, 3 Bytes, num
	 */
	private String textschluesselErgaenzung;

	private int textschluesselErgaenzungLength = 3;

	/**
	 * Bankinternes Feld, 8, 1 Byte, Leerzeichen
	 */
	private String c8 = " ";

	private int c8Length = 1;

	/**
	 * Bankinternes Feld, 9, 11 Bytes, kann DM-Betrag zur Info enthalten
	 */
	private String c9 = "0";

	private int c9Length = 11;

	/**
	 * Bankleitzahl ueberweisendes Institut, 10, 8 Bytes, num
	 */
	private String c10;

	private int c10Length = 8;

	/**
	 * Kontonummer Ueberweisender/Zahlungsempfaenger, 11, 10 Bytes, num,
	 * rechtsbuendig
	 */
	private String c11;

	private int c11Length = 10;

	/**
	 * Betrag in EURO einschl. Nachkommastellen, 12, 11 Bytes, num, 100,12 EURO =
	 * 10012
	 */
	private String betrag;

	private int betragLength = 11;

	private Double doubleBetrag;

	/**
	 * Reserve, 13, 3 Bytes, Leerzeichen
	 */
	private String c13 = " ";

	private int c13Length = 3;

	/**
	 * Beguenstigter/Zahlungspflichtiger, 14a, 27 Bytes, alpha, linksbuendig
	 */
	private String c14a;

	private int c14aLength = 27;

	/**
	 * Abgrenzung des Satzabschnitts, 14b, 20 Bytes, Leerzeichen
	 */
	private String abgrenzung = " ";

	private int abgrenzungLength = 8;

	/**
	 * Beguenstigter/Zahlungspflichtiger, 15, 27 Bytes, alpha
	 */
	private String c15;

	private int c15Length = 27;

	/**
	 * Verwendungszweck, 16, 27 Bytes, alpha
	 */
	private String verwendungszweck;

	private int verwendungszweckLength = 27;

	/**
	 * Waehrungskennzeichen, 17a, 1 Byte, 1 = EURO
	 */
	private String waehrungskennzeichen = "1";

	private int waehrungskennzeichenLength = 1;

	/**
	 * Reserve, 17b, 2 Bytes, Leerzeichen
	 */
	private String c17b = " ";

	private int c17bLength = 2;

	/**
	 * Erweiterungszeichen, 17b, 2 Bytes, Anzahl der Erweiterungssaetze
	 */
	private String erweiterung;

	private int erweiterungLength = 2;

	/**
	 * Constructor
	 *  
	 */
	public DtAusSetC() {
	}

	/**
	 * @return
	 */
	String getC15() {
		return IOUtil.justify(c15.toUpperCase(), IOUtil.LEFT, c15Length, " ");
	}

	/**
	 * @param c15
	 */
	public void setC15(String c15) {
		this.c15 = LangUtil.normalizeUmlauts(c15);
	}

	/**
	 * @return Returns the betrag.
	 */
	String getBetrag() {
		return IOUtil.justify(betrag, IOUtil.RIGHT, betragLength, "0");
	}
	
	Double getBetragAsDouble() {
		return doubleBetrag;
	}

	/**
	 * @param betrag
	 *            The betrag to set.
	 */
	public void setBetrag(String betrag) throws ParseException {

		Double d = new Double(betrag);
		doubleBetrag = d;
		NumberFormat n = NumberFormat.getInstance();
		n.setMinimumFractionDigits(2);
		this.betrag = n.format(d).replaceAll("\\.", "");

	}

	/**
	 * @return Returns the c4.
	 */
	String getC4() {
		return IOUtil.justify(c4, IOUtil.LEFT, c4Length, "0");
	}

	/**
	 * @param c4
	 *            The c4 to set.
	 */
	public void setC4(String c4) {
		this.c4 = c4;
	}

	/**
	 * @return Returns the blzErstInst.
	 */
	String getBlzErstInst() {
		return IOUtil.justify(blzErstInst, IOUtil.LEFT, blzErstInstLength, "0");
	}

	/**
	 * @param blzErstInst
	 *            The blzErstInst to set.
	 */
	public void setBlzErstInst(String blzErstInst) {
		this.blzErstInst = blzErstInst;
	}

	/**
	 * @return Returns the c10.
	 */
	String getC10() {
		return IOUtil.justify(c10, IOUtil.LEFT, c10Length, "0");
	}

	/**
	 * @param c10
	 *            The c10 to set.
	 */
	public void setC10(String c10) {
		this.c10 = c10;
	}

	/**
	 * @return Returns the erweiterung.
	 */
	String getErweiterung() {

		String s;

		s = IOUtil.justify(erweiterung, IOUtil.LEFT, erweiterungLength, "0");

		if (erweiterung.equals("0")) {
			s += IOUtil.justify(" ", IOUtil.LEFT, 256 - 187, " ");
		}

		return s;

	}

	/**
	 * @param erweiterung
	 *            The erweiterung to set.
	 */
	public void setErweiterung(String erweiterung) {
		this.erweiterung = erweiterung;
	}

	/**
	 * @return Returns the interneKundennummer.
	 */
	String getInterneKundennummer() {

		return IOUtil.justify(interneKundennummer, IOUtil.LEFT,
				interneKundennummerLength, "0");

	}

	/**
	 * @param interneKundennummer
	 *            The interneKundennummer to set.
	 */
	public void setInterneKundennummer(String interneKundennummer) {
		this.interneKundennummer = interneKundennummer;
	}

	/**
	 * @return Returns the c5.
	 */
	String getC5() {
		return IOUtil.justify(c5, IOUtil.RIGHT, c5Length, "0");
	}

	/**
	 * @param c5
	 *            The c5 to set.
	 */
	public void setC5(String c5) {
		this.c5 = c5;
	}

	/**
	 * @return Returns the c11.
	 */
	String getC11() {
		return IOUtil.justify(c11, IOUtil.RIGHT, c11Length, "0");
	}

	/**
	 * @param c11
	 *            The c11 to set.
	 */
	public void setC11(String c11) {
		this.c11 = c11;
	}

	/**
	 * @return Returns the textschluessel.
	 */
	String getTextschluessel() {

		return IOUtil.justify(textschluessel, IOUtil.LEFT,
				textschluesselLength, "0");

	}

	/**
	 * @param textschluessel
	 *            The textschluessel to set.
	 */
	public void setTextschluessel(String textschluessel) {
		this.textschluessel = textschluessel;
	}

	/**
	 * @return Returns the textschluesselErgaenzung.
	 */
	String getTextschluesselErgaenzung() {

		return IOUtil.justify(textschluesselErgaenzung, IOUtil.LEFT,
				textschluesselErgaenzungLength, "0");

	}

	/**
	 * @param textschluesselErgaenzung
	 *            The textschluesselErgaenzung to set.
	 */
	public void setTextschluesselErgaenzung(String textschluesselErgaenzung) {
		this.textschluesselErgaenzung = textschluesselErgaenzung;
	}

	/**
	 * @return Returns the verwendungszweck.
	 */
	String getVerwendungszweck() {

		return IOUtil.justify(verwendungszweck.toUpperCase(), IOUtil.LEFT,
				verwendungszweckLength, " ");

	}

	/**
	 * @param verwendungszweck
	 *            The verwendungszweck to set.
	 */
	public void setVerwendungszweck(String verwendungszweck) {
		this.verwendungszweck = LangUtil.normalizeUmlauts(verwendungszweck);
	}

	/**
	 * @return Returns the c14a.
	 */
	String getC14a() {
		return IOUtil.justify(c14a.toUpperCase(), IOUtil.LEFT, c14aLength, " ");
	}

	/**
	 * @param c14a
	 *            The c14a to set.
	 */
	public void setC14a(String c14a) {
		this.c14a = LangUtil.normalizeUmlauts(c14a);
	}

	/**
	 * @return Returns the abgrenzung.
	 */
	String getAbgrenzung() {
		return IOUtil.justify(abgrenzung, IOUtil.LEFT, abgrenzungLength, " ");
	}

	/**
	 * @return Returns the c13.
	 */
	String getC13() {
		return IOUtil.justify(c13, IOUtil.LEFT, c13Length, " ");
	}

	/**
	 * @return Returns the c17b.
	 */
	String getC17b() {
		return IOUtil.justify(c17b, IOUtil.LEFT, c17bLength, " ");
	}

	/**
	 * @return Returns the c8.
	 */
	String getC8() {
		return IOUtil.justify(c8, IOUtil.LEFT, c8Length, " ");
	}

	/**
	 * @return Returns the c9.
	 */
	String getC9() {
		return IOUtil.justify(c9, IOUtil.LEFT, c9Length, "0");
	}

	/**
	 * @return Returns the satzart.
	 */
	public String getSatzart() {
		return IOUtil.justify(satzart, IOUtil.LEFT, satzartLength, " ");
	}

	/**
	 * @return Returns the satzlaenge.
	 */
	String getSatzlaenge(String s) {

		return IOUtil.justify(String.valueOf(s.length() + satzlaengeLength),
				IOUtil.RIGHT, satzlaengeLength, "0");

	}

	/**
	 * @return Returns the waehrungskennzeichen.
	 */
	String getWaehrungskennzeichen() {

		return IOUtil.justify(waehrungskennzeichen, IOUtil.LEFT,
				waehrungskennzeichenLength, "0");

	}

	/**
	 *  
	 */
	public String toString() {

		String s = getSatzart() + getBlzErstInst() + getC4() + getC5()
				+ getInterneKundennummer() + getTextschluessel()
				+ getTextschluesselErgaenzung() + getC8() + getC9() + getC10()
				+ getC11() + getBetrag() + getC13() + getC14a()
				+ getAbgrenzung() + getC15() + getVerwendungszweck()
				+ getWaehrungskennzeichen() + getC17b() + getErweiterung();

		if (erweiterung.equals("0"))
			return new String("0187" + s).substring(0, 256);
		else
			return getSatzlaenge(s) + s;

	}

}