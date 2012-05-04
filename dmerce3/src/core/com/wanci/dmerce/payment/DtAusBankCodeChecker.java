/*
 * Created on Feb 12, 2004
 *  
 */
package com.wanci.dmerce.payment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.wanci.dmerce.kernel.Boot;
import com.wanci.java.IOUtil;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: DtAusBankCodeChecker.java,v 1.2 2004/05/16 19:44:07 rb Exp $
 *  
 */
public class DtAusBankCodeChecker {

	private boolean DEBUG = false;
	private File blzFile;
	private BankCodeDirectory bcd;

	/**
	 * Constructor
	 * 
	 * @param blzFile
	 */
	public DtAusBankCodeChecker(File blzFile) {
		this.blzFile = blzFile;
		this.bcd = new BankCodeDirectory();
	}

	/**
	 * Print strings: used for debugging
	 * 
	 * @param s
	 * @param s2
	 */
	private void p(String s, String s2) {
		System.out.println(IOUtil.justify(s, IOUtil.LEFT, 50, ".") + s2);
	}

	/**
	 * Return bank code directory
	 * 
	 * @return
	 */
	public BankCodeDirectory getBankCodeDirectory() {
		return bcd;
	}

	/**
	 * Read datasets from file
	 * 
	 * @param charArray
	 */
	private void readDataset(char[] charArray) {

		// Bankleitzahl
		String s1 = LangUtil.charArrayToString(charArray, 0, 8).trim();
		// Merkmal, ob eigene BLZ
		String s2 = LangUtil.charArrayToString(charArray, 8, 1);
		// keine Angabe oder zur Loeschung vorgemerkte BLZ
		String s3 = LangUtil.charArrayToString(charArray, 9, 8);
		// Merkmal, ob eigenes BBk-Girokonto
		String s4 = LangUtil.charArrayToString(charArray, 17, 1);
		// Keine Angabe oder Datum der Loeschung
		String s5 = LangUtil.charArrayToString(charArray, 18, 4);
		// Keine Angabe oder bei Loeschungen lfd. Nr. des Nachfolgeinstituts
		String s6 = LangUtil.charArrayToString(charArray, 22, 5);
		// Verkürzte Bezeichnung der Kreditinstitutsniederlassung
		String s7 = LangUtil.charArrayToString(charArray, 27, 58);
		// Kurzbezeichnung der Kreditinstitusniederlassung
		String s8 = LangUtil.charArrayToString(charArray, 85, 20);
		// Postleitzahl (bei fehlender Angabe Blank)
		String s9 = LangUtil.charArrayToString(charArray, 105, 5);
		// Ort
		String s10 = LangUtil.charArrayToString(charArray, 110, 29);
		// Kurzbezeichnung der Kreditinstitutsniederlassung für Btx und EZÜ
		String s11 = LangUtil.charArrayToString(charArray, 139, 27);
		// Instituts-Nr. für PAN (bei fehlender Angabe Blank)
		String s12 = LangUtil.charArrayToString(charArray, 166, 5);
		// Merkmal, ob Bankleitzahl im gedruckten Bankleitzahlenverzeichnis
		// veröffentlicht wird („1“) oder nicht („0“)
		String s13 = LangUtil.charArrayToString(charArray, 171, 1);
		// BIC (ohne „DE“ für Deutschland) (bei fehlender Angabe Blank)
		String s14 = LangUtil.charArrayToString(charArray, 172, 9);
		// Kennziffer für Prüfzifferberechnungsmethode (alphanumerisch ab 03.
		// Juni 2002)
		String s15 = LangUtil.charArrayToString(charArray, 181, 2);
		// Lfd. Nr. des Datensatzes (numerisch)
		String s16 = LangUtil.charArrayToString(charArray, 183, 5);

		// Create bank code object or modify data of existing
		// object
		int newBankNumber = 0;
		if (s6.indexOf(" ") == -1)
			newBankNumber = Integer.valueOf(s6).intValue();
		DtAusBankCode b =
			new DtAusBankCode(
				Integer.valueOf(s16).intValue(),
				s1,
				s7,
				s1.equals("00000000"),
				newBankNumber);
		bcd.add(b);

		if (DEBUG) {
			p("Bankleitzahl", s1);
			p("Eigene BLZ?", s2.equals("1") ? "ja" : "nein");
			p("L Vorgemerkte BLZ", s3);
			p("Eigenes BBk-Konto", s4.equals("1") ? "ja" : "nein");
			p("L Datum der Loeschung", s5);
			p("L Lfd.Nr. des Nachfolgeinst.", s6);
			p("Verkuerzte Bez. der Niederlassung", s7);
			p("Kurzbez. fuer Niederlassung", s8);
			p("PLZ Ort", s9 + " " + s10);
			p("Kurzbez. fuer Niederlassung fuer BTX", s11);
			p("PAN", s12);
			p("BLZ veroeffentlicht", s13);
			p("BIC", s14);
			p("Kennzimmer für Prüfzifferberechnung", s15);
			p("Lfd. Nr.", s16);
			System.out.println();
		}

	}

	/**
	 * Read bank code directory file from "Deutsche Bundesbank"
	 * 
	 * @throws IOException
	 */
	public void readFile() throws IOException {

		char[] c = new char[188];
		BufferedReader br = new BufferedReader(new FileReader(blzFile));

		while (br.read(c, 0, 188) > 0) {
			readDataset(c);
			// Newline
			br.read(c, 0, 2);
		}

	}

	/**
	 * Test
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Boot.printCopyright("GERMAN BANK CODE CHECKER");

		LangUtil.consoleDebug(true, "START");

		DtAusBankCodeChecker c =
			new DtAusBankCodeChecker(new File("blz0312pc.txt"));
		c.readFile();
		LangUtil.consoleDebug(
			true,
			"Read " + c.getBankCodeDirectory().getCount() + " entries");

		LangUtil.consoleDebug(true, "STOP");

	}

}