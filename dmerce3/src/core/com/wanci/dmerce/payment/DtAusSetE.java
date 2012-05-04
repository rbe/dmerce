/*
 * Created on 06.02.2004
 *  
 */
package com.wanci.dmerce.payment;

import com.wanci.java.IOUtil;

/**
 * @author rb
 * @version ${Id}
 * 
 * Datentraeger-Nachsatz
 *  
 */
public class DtAusSetE {

	/**
	 * Satzlaenge, 1, 4 Bytes
	 */
	private String satzlaenge = "128";
	private int satzlaengeLength = 4;
	/**
	 * Satzart, 2, 1 Byte, immer E
	 */
	private String satzart = "E";
	private int satzartLength = 1;
	/**
	 * Reserve, 3, 5 Bytes, Leerzeichen
	 */
	private String e3 = " ";
	private int e3Length = 5;
	/**
	 * Anzahl der Datensaetze C, 4, 7 Bytes, num
	 */
	private long anzDatensaetzeC;
	private int anzDatensaetzeCLength = 7;
	/**
	 * Reserve, 5, 13 Bytes, Leerzeichen
	 */
	private String e5 = "0";
	private int e5Length = 13;
	/**
	 * Summe der Kontonummern aus C.5, 6, 17 Bytes, num
	 */
	private long summeKontonummern;
	private int summeKontonummernLength = 17;
	/**
	 * Summe der Bankleitzahlen aus C.4, 7, 17 Bytes, num
	 */
	private long summeBlz;
	private int summeBlzLength = 17;
	/**
	 * Summe der Betraege aus C.12, 8, 13 Bytes, num
	 */
	private long summeBetraege;
	private int summeBetraegeLength = 13;
	/**
	 * Abgrenzung des Satzabschnitts
	 */
	private String abgrenzung = " ";
	private int abgrenzungLength = 51;

	/**
	 * Constructor
	 *  
	 */
	public DtAusSetE() {
	}

	/**
	 * @param anzDatensaetzeC
	 * @param summeKontonummern
	 * @param summeBlz
	 * @param summeBetraege
	 */
	public DtAusSetE(
		long anzDatensaetzeC,
		long summeKontonummern,
		long summeBlz,
		long summeBetraege) {

		this.anzDatensaetzeC = anzDatensaetzeC;
		this.summeKontonummern = summeKontonummern;
		this.summeBlz = summeBlz;
		this.summeBetraege = summeBetraege;

	}

	/**
	 * @return Returns the abgrenzung.
	 */
	String getAbgrenzung() {
		return IOUtil.justify(abgrenzung, IOUtil.LEFT, abgrenzungLength, " ");
	}

	/**
	 * @return Returns the anzDatensaetzeC.
	 */
	public String getAnzDatensaetzeC() {

		return IOUtil.justify(
			String.valueOf(anzDatensaetzeC),
			IOUtil.RIGHT,
			anzDatensaetzeCLength,
			"0");

	}

	public void incAnzDatensaetzeC(int inc) {
		this.anzDatensaetzeC += inc;
	}

	/**
	 * @return Returns the e3.
	 */
	String getE3() {
		return IOUtil.justify(e3, IOUtil.LEFT, e3Length, " ");
	}

	/**
	 * @return Returns the e5.
	 */
	String getE5() {
		return IOUtil.justify(e5, IOUtil.RIGHT, e5Length, "0");
	}

	/**
	 * @return Returns the satzart.
	 */
	String getSatzart() {
		return IOUtil.justify(satzart, IOUtil.LEFT, satzartLength, " ");
	}

	/**
	 * @return Returns the satzlaenge.
	 */
	String getSatzlaenge() {
		return IOUtil.justify(satzlaenge, IOUtil.RIGHT, satzlaengeLength, "0");
	}

	public void addSum(String sum) {
		sum = sum.replaceAll(",", "");
		sum = sum.replaceAll("\\.0$", ".00"); // .00 will come as: .0
		sum = sum.replaceAll("\\.", "");
		summeBetraege += Long.valueOf(sum).longValue();
	}

	/**
	 * @return Returns the summeBetraege.
	 */
	public String getSummeBetraege() {

		return IOUtil.justify(
			String.valueOf(summeBetraege),
			IOUtil.RIGHT,
			summeBetraegeLength,
			"0");

	}

	public double getSummeBetraegeAsDouble() {
		return summeBetraege / 100.0; // divide by 100 because we save
		// sums without fraction sign (100.00 EUR = 10000)
	}

	public void addBlz(String blz) {
		summeBlz += Long.valueOf(blz).longValue();
	}

	/**
	 * @return Returns the summeBlz.
	 */
	public String getSummeBlz() {

		return IOUtil.justify(
			String.valueOf(summeBlz),
			IOUtil.RIGHT,
			summeBlzLength,
			"0");

	}

	public void addKontonummer(String kontonummer) {
		summeKontonummern += Long.valueOf(kontonummer).longValue();
	}

	/**
	 * @return Returns the summeKontonummern.
	 */
	public String getSummeKontonummern() {

		return IOUtil.justify(
			String.valueOf(summeKontonummern),
			IOUtil.RIGHT,
			summeKontonummernLength,
			"0");

	}

	/**
	 *  
	 */
	public String toString() {

		return getSatzlaenge()
			+ getSatzart()
			+ getE3()
			+ getAnzDatensaetzeC()
			+ getE5()
			+ getSummeKontonummern()
			+ getSummeBlz()
			+ getSummeBetraege()
			+ getAbgrenzung();

	}

}