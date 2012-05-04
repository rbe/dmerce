/*
 * Created on Jan 6, 2004
 *  
 */
package com.wanci.dmerce.payment;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import com.lowagie.text.DocumentException;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.java.LangUtil;
import com.wanci.java.ZIP;

/**
 * @author rb
 * @version $Id: DtAusTest.java,v 1.8 2004/04/23 15:40:09 rb Exp $
 *  
 */
public class DtAusTest {

	static boolean DEBUG = true;

	static File pdfFile = new File("begleitzettel.pdf");
	static File dtAusFile = new File("DTAUS0.TXT");
	static DtAusSetA a = new DtAusSetA();
	static DtAusSetC c = new DtAusSetC();
	static DtAusSetE e = new DtAusSetE();
	static DtAusWriter writer;
	static DtAusSheet sheet;
	//static String blz = "41450075";
	static String blz = "13091044";
	static String kontoBertram = "9999999999"; // 10 digits
	static String kontoZorro = "8888888883"; // 10 digits

	static void test() throws DocumentException, IOException {

		writer = new DtAusWriter(dtAusFile);

		LangUtil.consoleDebug(true, "Generating SetA");
		a.setKennzeichen("LK");
		a.setBankleitzahl(blz);
		a.setKundenname("Bertram Begünstigt");
		a.setDatum(new Date());
		a.setKontonummer(kontoBertram);
		writer.setA(a);
		String seta = a.toString();
		LangUtil.consoleDebug(DEBUG, "Length: " + seta.length());
		if (DEBUG) {
			System.out.println("Content: '" + seta + "'");
			System.out.println(
				"Compare: '"
					+ "NNNNAKKNNNNNNNN00000000NAMEXXXXXXXXX              TTMMJJ    NNNNNNNNNNNNNNNNNNNN               TTMMJJJJ                        W'");
		}

		for (int i = 0; i < 200; i++) {

			LangUtil.consoleDebug(true, "Generating SetC");
			c.setC4(blz);
			e.addBlz(blz);
			c.setC5(kontoBertram);
			e.addKontonummer(kontoBertram);
			c.setInterneKundennummer("0");
			c.setTextschluessel("05");
			c.setTextschluesselErgaenzung("0");
			c.setC10(blz);
			c.setC11(kontoZorro);
			try {
                c.setBetrag("567,39");
            }
            catch (ParseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                continue;
            }
			e.addSum("567,39");
			c.setC14a("Zörro Zähler");
			c.setC15("Bertram Beguenstigt");
			c.setVerwendungszweck("Verwendungszweck");
			c.setErweiterung("0");
			e.incAnzDatensaetzeC(1);
			String setc = c.toString();
			writer.addC(c);
			LangUtil.consoleDebug(DEBUG, "Length: " + setc.length());
			if (DEBUG) {
				System.out.println("Content: '" + setc + "'");
				System.out.println(
					"Compare: '"
						+ "NNNNCNNNNNNNNMMMMMMMMKKKKKKKKKK0000000000000TTTTT 00000000000NNNNNNNNKKKKKKKKKKBBBBBBBBBBB   NAMEXXXXXXXXXXXXXXXXXXXXXXX        NAMEXXXXXXXXXXXXXXXXXXXXXXXVERWENDUNGSZWECKXXXXXXXXXXXW  NN'");
			}

		}

		LangUtil.consoleDebug(true, "Generating SetE");
		String sete = e.toString();
		writer.setE(e);
		LangUtil.consoleDebug(DEBUG, "Length: " + sete.length());
		if (DEBUG) {
			System.out.println("Content: '" + sete + "'");
			System.out.println(
				"Compare: "
					+ "'NNNNE     NNNNNNN0000000000000KKKKKKKKKKKKKKKKKBBBBBBBBBBBBBBBBBEEEEEEEEEEEEE                                                   '");
		}

		writer.writeFile();
		sheet = new DtAusSheet(pdfFile, e);
		sheet.create();

		ZIP zip = new ZIP("payment.zip");
		zip.addFile(pdfFile.getName(), pdfFile);
		zip.addFile(dtAusFile.getName(), dtAusFile);
		zip.create();

		pdfFile.delete();
		dtAusFile.delete();

	}

	static void test2() throws DocumentException, IOException {

		DtAusLastschrift l = new DtAusLastschrift();

		l.setMyCompanyName("Verband der selbständigen Versicherungskaufleute");
		l.setMyFirstName("Rolf");
		l.setMyLastName("Thiele");
		l.setMyStreet("Meinestr. 1");
		l.setMyZipCode("12345");
		l.setMyCity("Ahlen");
		l.setBankAccountNumber(kontoBertram);
		l.setBankCode(blz);
		l.setBankCompanyName("Sparkasse Soest");
		l.setBankStreet("Puppenstr. 7-9");
		l.setBankZipCode("59494");
		l.setBankCity("Soest");

		for (int i = 0; i < 2500; i++) {
			try {
                l.add(
                	100.00,
                	"Zörro Zähler " + i,
                	kontoZorro.substring(
                		0,
                		kontoZorro.length() - new String("" + i).length())
                		+ i,
                	blz,
                	"Verwendungszweck " + i);
            }
            catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                continue;
            }
		}

		l.generate();

	}

	public static void main(String[] args)
		throws DocumentException, IOException {

		Boot.printCopyright("PAYMENT");
		test2();

	}

}