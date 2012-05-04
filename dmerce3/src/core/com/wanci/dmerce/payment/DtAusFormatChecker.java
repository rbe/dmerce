/*
 * Created on Feb 13, 2004
 *  
 */
package com.wanci.dmerce.payment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.wanci.dmerce.kernel.Boot;
import com.wanci.java.IOUtil;
import com.wanci.java.LangUtil;
import com.wanci.java.ZIP;

/**
 * @author rb
 * @version $Id: DtAusFormatChecker.java,v 1.6 2004/05/16 19:44:07 rb Exp $
 *  
 */
public class DtAusFormatChecker {

	private boolean DEBUG = true;
	private File dtAusFile;
	private BufferedReader br;
	private DtAusBankCodeChecker dtAusBankCodeChecker = null;
	private BankCodeDirectory bankCodeDirectory = null;

	public DtAusFormatChecker(
		File dtAusFile,
		DtAusBankCodeChecker dtAusBankCodeChecker)
		throws FileNotFoundException {

		this.dtAusFile = dtAusFile;
		this.br = new BufferedReader(new FileReader(dtAusFile));
		this.dtAusBankCodeChecker = dtAusBankCodeChecker;
		if (dtAusBankCodeChecker != null)
			bankCodeDirectory = dtAusBankCodeChecker.getBankCodeDirectory();

	}

	/**
	 * @param s
	 * @param s2
	 */
	void p(String s, String s2) {

		System.out.println(
			IOUtil.justify(s, IOUtil.LEFT, 35, ".")
				+ IOUtil.justify(s2, IOUtil.LEFT, 30, ".")
				+ "Laenge: "
				+ s2.length());

	}

	/**
	 * @param br
	 * @throws IOException
	 */
	void checkA(BufferedReader br) throws IOException {

		// A Satz
		int setALength = 128;
		char[] setA = new char[128];
		br.read(setA, 0, 128);
		LangUtil.consoleDebug(
			DEBUG,
			"Read dataset A: " + setA.length + " bytes");

		String satzlaenge = LangUtil.charArrayToString(setA, 0, 4);
		String satzart = LangUtil.charArrayToString(setA, 4, 1);
		String kennzeichen = LangUtil.charArrayToString(setA, 5, 2);
		String bankleitzahl = LangUtil.charArrayToString(setA, 7, 8);
		String a5 = LangUtil.charArrayToString(setA, 15, 8);
		String kundenname = LangUtil.charArrayToString(setA, 23, 27);
		String datum = LangUtil.charArrayToString(setA, 50, 6);
		String a8 = LangUtil.charArrayToString(setA, 56, 4);
		String kontonummer = LangUtil.charArrayToString(setA, 60, 10);
		String referenznummer = LangUtil.charArrayToString(setA, 70, 10);
		String a11a = LangUtil.charArrayToString(setA, 80, 15);
		String ausfuehrungsdatum = LangUtil.charArrayToString(setA, 95, 8);
		String a11c = LangUtil.charArrayToString(setA, 103, 24);
		String waehrungskennzeichen = LangUtil.charArrayToString(setA, 127, 1);

		p(" A1,  4,Satzlaenge", satzlaenge);
		p(" A2,  1,Satzart", satzart);
		p(" A3,  1,Kennzeichen", kennzeichen);
		p(" A4,  8,Bankleitzahl", bankleitzahl);
		p(" A5,  8,Nullen", a5);
		p(" A6, 27,Kundenname", kundenname);
		p(" A7,  6,Datum", datum);
		p(" A8,  4,Leerstellen", a8);
		p(" A9, 10,Kontonummer", kontonummer);
		p("A10, 10,Referenznummer", referenznummer);
		p("A11a,15,Reserve", a11a);
		p("A11b, 8,Ausfuehrungsdatum", ausfuehrungsdatum);
		p("A11c,24,Reserve", a11c);
		p("A12,  1,Waehrungskennzeichen", waehrungskennzeichen);

	}

	/**
	 * @param charArray
	 * @param br
	 * @throws IOException
	 */
	void checkC(char[] charArray, BufferedReader br) throws IOException {

		String satzlaenge = LangUtil.charArrayToString(charArray, 0, 4);
		String satzart = LangUtil.charArrayToString(charArray, 4, 1);

		// C Satz, 1. konstanter Teil
		char[] setC = new char[128];
		LangUtil.consoleDebug(
			DEBUG,
			"Reading C dataset with " + satzlaenge + " bytes");

		int readBytes = 5;

		readBytes += br.read(setC, 0, 8);
		String c3 = LangUtil.charArrayToString(setC, 0, 8);

		readBytes += br.read(setC, 0, 8);
		String c4 = LangUtil.charArrayToString(setC, 0, 8);

		readBytes += br.read(setC, 0, 10);
		String c5 = LangUtil.charArrayToString(setC, 0, 10);

		readBytes += br.read(setC, 0, 13);
		String c6 = LangUtil.charArrayToString(setC, 0, 13);

		readBytes += br.read(setC, 0, 2);
		String c7a = LangUtil.charArrayToString(setC, 0, 2);

		readBytes += br.read(setC, 0, 3);
		String c7b = LangUtil.charArrayToString(setC, 0, 3);

		readBytes += br.read(setC, 0, 1);
		String c8 = LangUtil.charArrayToString(setC, 0, 1);

		readBytes += br.read(setC, 0, 11);
		String c9 = LangUtil.charArrayToString(setC, 0, 11);

		readBytes += br.read(setC, 0, 8);
		String c10 = LangUtil.charArrayToString(setC, 0, 8);

		readBytes += br.read(setC, 0, 10);
		String c11 = LangUtil.charArrayToString(setC, 0, 10);

		readBytes += br.read(setC, 0, 11);
		String c12 = LangUtil.charArrayToString(setC, 0, 11);

		readBytes += br.read(setC, 0, 3);
		String c13 = LangUtil.charArrayToString(setC, 0, 3);

		readBytes += br.read(setC, 0, 27);
		String c14a = LangUtil.charArrayToString(setC, 0, 27);

		readBytes += br.read(setC, 0, 8);
		String c14b = LangUtil.charArrayToString(setC, 0, 8);

		readBytes += br.read(setC, 0, 27);
		String c15 = LangUtil.charArrayToString(setC, 0, 27);

		readBytes += br.read(setC, 0, 27);
		String c16 = LangUtil.charArrayToString(setC, 0, 27);

		readBytes += br.read(setC, 0, 1);
		String c17a = LangUtil.charArrayToString(setC, 0, 1);

		readBytes += br.read(setC, 0, 2);
		String c17b = LangUtil.charArrayToString(setC, 0, 2);

		readBytes += br.read(setC, 0, 2);
		String c18 = LangUtil.charArrayToString(setC, 0, 2);

		p(" C1,  4, Satzlaenge", satzlaenge);
		p(" C2,  1, Satzart", satzart);
		p(" C3,  8, BLZ 1. Institut", c3);
		p(" C4,  8, Bankleitzahl", c4);
		p(" C5, 10, Kontonummer", c5);
		p(" C6, 11, Interne Kundennummer", c6);
		p(" C7a, 2, Textschluessel", c7a);
		p(" C7b, 3, Textschl. Ergaenzung", c7b);
		p(" C8,  1, bankinternes Feld", c8);
		p(" C9, 11, Nullen", c9);
		p("C10, 10, Bankleitzahl", c10);
		p("C11,  8, Kontonummer", c11);
		p("C12, 11, Betrag in EUR", c12);
		p("C13,  3, Leerstellen", c13);
		p("C14a,27, Name", c14a);
		p("C14b, 8, Abgrenzung", c14b);
		p("C15, 27, Name", c15);
		p("C16, 27, Verwendungszweck", c16);
		p("C17a, 1, Waehrungskennzeichen", c17a);
		p("C17b, 2, Leerstellen", c17b);
		p("C18,  2, Erweiterungskennzeichen", c18);

		if (dtAusBankCodeChecker != null && bankCodeDirectory != null) {

			DtAusBankCode bankCodeC3 = bankCodeDirectory.getBankCode(c3);
			if (bankCodeC3 != null)
				p(
					"Pruefe BLZ  C3",
					(bankCodeC3.isOutdated()
						? "ungueltig"
						: bankCodeC3.getBankName()));

			DtAusBankCode bankCodeC4 = bankCodeDirectory.getBankCode(c4);
			p(
				"Pruefe BLZ  C4",
				(bankCodeC4.isOutdated()
					? "ungueltig, neu: "
						+ bankCodeDirectory
							.getBankCode(bankCodeC4.getNewBankNumber())
							.getBankCode()
					: bankCodeC4.getBankName()));

			DtAusBankCode bankCodeC10 = bankCodeDirectory.getBankCode(c10);
			p(
				"Pruefe BLZ C10",
				(bankCodeC10.isOutdated()
					? "ungueltig, neu: "
						+ bankCodeDirectory
							.getBankCode(bankCodeC10.getNewBankNumber())
							.getBankCode()
					: bankCodeC10.getBankName()));

		}

	}

	/**
	 * @param charArray
	 * @param br
	 * @throws IOException
	 */
	void checkE(char[] charArray, BufferedReader br) throws IOException {

		String satzlaenge = LangUtil.charArrayToString(charArray, 0, 4);
		String satzart = LangUtil.charArrayToString(charArray, 4, 1);

		// E Satz
		char[] setE = new char[128];
		LangUtil.consoleDebug(
			DEBUG,
			"Reading E dataset with " + satzlaenge + " bytes");

		int readBytes = 5;

		readBytes += br.read(setE, 0, 5);
		String reserve = LangUtil.charArrayToString(setE, 0, 5);

		readBytes += br.read(setE, 0, 7);
		String anzDatensatzeC = LangUtil.charArrayToString(setE, 0, 7);

		readBytes += br.read(setE, 0, 13);
		String reserve2 = LangUtil.charArrayToString(setE, 0, 13);

		readBytes += br.read(setE, 0, 17);
		String summeKontonr = LangUtil.charArrayToString(setE, 0, 17);

		readBytes += br.read(setE, 0, 17);
		String summeBlz = LangUtil.charArrayToString(setE, 0, 17);

		readBytes += br.read(setE, 0, 13);
		String summeEuro = LangUtil.charArrayToString(setE, 0, 13);

		readBytes += br.read(setE, 0, 51);
		String abgrenzung = LangUtil.charArrayToString(setE, 0, 51);

		p(" E1, 4, Satzlaenge", satzlaenge);
		p(" E2, 1, Satzart", satzart);
		p(" E3, 5, Reserve, Leerstellen", reserve);
		p(" E4, 7, Anzahl Datensaetze C", anzDatensatzeC);
		p(" E5,13, Reserve, Nullen", reserve2);
		p(" E6,17, Kontrollsumme Kontonr.", summeKontonr);
		p(" E7,17, Kontrollsumme BLZ", summeBlz);
		p(" E8, 5, Summe EURO", summeEuro);
		p(" E9,51, Abgrenzung", abgrenzung);

	}

	/**
	 * @throws IOException
	 */
	public void check() throws IOException {

		checkA(br);

		// Check C dataset
		int cDatasetsFound = 0;
		char[] c = new char[5];
		boolean isCDataset = true;
		while (isCDataset) {

			br.read(c, 0, 5);
			LangUtil.consoleDebug(
				DEBUG,
				"Next 5 bytes: " + c[0] + c[1] + c[2] + c[3] + c[4]);
			if (c[4] == 'C') {

				cDatasetsFound++;

				LangUtil.consoleDebug(
					DEBUG,
					"Found C dataset #" + cDatasetsFound);

				int len =
					Integer
						.valueOf(new String("" + c[0] + c[1] + c[2] + c[3]))
						.intValue();

				checkC(c, br);
				br.read(new char[256 - 187], 0, 256 - 187);

			}
			else {
				isCDataset = false;
				LangUtil.consoleDebug(DEBUG, "No more C datasets found");
			}

		}

		// Check dataset E
		if (new String("" + c[0] + c[1] + c[2] + c[3] + c[4]).equals("0128E"))
			checkE(c, br);
		else
			LangUtil.consoleDebug(true, "ERROR: No E dataset found");

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Boot.printCopyright("PAYMENT - DTAUS FORMAT CHECK");

		LangUtil.consoleDebug(true, "START");

		DtAusBankCodeChecker b = null;
		File blzFile = new File("blz0312pc.txt");
		if (blzFile.exists()) {
			b = new DtAusBankCodeChecker(blzFile);
			b.readFile();
		}
		else
			LangUtil.consoleDebug(true, "No bank code file found!");

		ZIP z = new ZIP(new File("payment.zip"));
		File f = z.getFile("DTAUS0.TXT");

		DtAusFormatChecker c = new DtAusFormatChecker(f, b);
		c.check();

		f.delete();

		LangUtil.consoleDebug(true, "STOP");

	}

}