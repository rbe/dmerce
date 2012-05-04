/*
 * Created on 11.02.2004
 *  
 */
package com.wanci.dmerce.payment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import com.lowagie.text.DocumentException;

/**
 * @author rb
 * @version $Id: DtAusLastschrift.java,v 1.7 2004/08/04 17:32:20 rb Exp $
 * 
 * Lastschriften beim beleglosen Datenträgeraustausch
 *  
 */
public class DtAusLastschrift implements DtAus {

	String myCompanyName;

	String myFirstName;

	String myLastName;

	String myStreet;

	String myZipCode;

	String myCity;

	String bankCompanyName;

	String bankFirstName;

	String bankLastName;

	String bankStreet;

	String bankZipCode;

	String bankCity;

	String bankCode;

	String bankAccountNumber;

	DtAusSetA dtAusSetA;

	DtAusSetC dtAusSetC;

	DtAusSetE dtAusSetE;

	DtAusSheet dtAusSheet;

	DtAusWriter dtAusWriter;

	NumberFormat nf = NumberFormat.getInstance(new Locale("de", "DE"));

	protected File dtAus0 = new File("DTAUS0.TXT");

	protected File pdfFile = new File("begleitzettel.pdf");

	protected File csvFile = new File("lastschriften.txt");

	FileWriter csvFileWriter;

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 *  
	 */
	public DtAusLastschrift() throws IOException {

		dtAusSetA = new DtAusSetA();
		dtAusSetE = new DtAusSetE();
		dtAusWriter = new DtAusWriter(dtAus0);

		dtAusSetA.setKennzeichen("LK");
		dtAusSetA.setDatum(new Date());

		csvFile.createNewFile();
		csvFileWriter = new FileWriter(csvFile);
		// Write header
		csvFileWriter.write("Name;BLZ;Kontonummer;Betrag;Verwendungszweck\r\n");

		nf.setMinimumFractionDigits(2);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.payment.DtAus#add(long, java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void add(double sum, String name, String accountNumber,
			String bankCode, String reason) throws ParseException, IOException {

		dtAusSetE.addKontonummer(accountNumber);
		dtAusSetE.addBlz(bankCode);
		dtAusSetE.addSum(String.valueOf(sum));
		dtAusSetE.incAnzDatensaetzeC(1);

		DtAusSetC dtAusSetC = new DtAusSetC();
		dtAusSetC.setC4(bankCode);
		dtAusSetC.setC5(accountNumber);
		dtAusSetC.setInterneKundennummer("0");
		dtAusSetC.setTextschluessel("05");
		dtAusSetC.setTextschluesselErgaenzung("0");
		dtAusSetC.setC10(this.bankCode);
		dtAusSetC.setC11(this.bankAccountNumber);
		dtAusSetC.setBetrag(String.valueOf(sum));
		dtAusSetC.setC14a(name);
		//dtAusSetC.setC15(myFirstName + " " + myLastName);
		dtAusSetC.setC15(myCompanyName);
		dtAusSetC.setVerwendungszweck(reason);
		dtAusSetC.setErweiterung("0");
		dtAusWriter.addC(dtAusSetC);

		csvFileWriter.write(name.trim() + ";" + bankCode + ";" + accountNumber
				+ ";" + nf.format(sum) + ";" + reason.trim() + "\r\n");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.payment.DtAus#setBankAccountNumber(java.lang.String)
	 */
	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.payment.DtAus#setBankCity(java.lang.String)
	 */
	public void setBankCity(String bankCity) {
		this.bankCity = bankCity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.payment.DtAus#setBankCode(java.lang.String)
	 */
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.payment.DtAus#setBankCompanyName(java.lang.String)
	 */
	public void setBankCompanyName(String bankCompanyName) {
		this.bankCompanyName = bankCompanyName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.payment.DtAus#setBankFirstName(java.lang.String)
	 */
	public void setBankFirstName(String bankFirstName) {
		this.bankFirstName = bankFirstName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.payment.DtAus#setBankLastName(java.lang.String)
	 */
	public void setBankLastName(String bankLastName) {
		this.bankLastName = bankLastName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.payment.DtAus#setBankStreet(java.lang.String)
	 */
	public void setBankStreet(String bankStreet) {
		this.bankStreet = bankStreet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.payment.DtAus#setBankZipCode(java.lang.String)
	 */
	public void setBankZipCode(String bankZipCode) {
		this.bankZipCode = bankZipCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.payment.DtAus#setMyCity(java.lang.String)
	 */
	public void setMyCity(String myCity) {
		this.myCity = myCity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.payment.DtAus#setMyCompanyName(java.lang.String)
	 */
	public void setMyCompanyName(String myCompanyName) {
		this.myCompanyName = myCompanyName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.payment.DtAus#setMyFirstName(java.lang.String)
	 */
	public void setMyFirstName(String myFirstName) {
		this.myFirstName = myFirstName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.payment.DtAus#setMyLastName(java.lang.String)
	 */
	public void setMyLastName(String myLastName) {
		this.myLastName = myLastName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.payment.DtAus#setMyStreet(java.lang.String)
	 */
	public void setMyStreet(String myStreet) {
		this.myStreet = myStreet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.payment.DtAus#setMyZipCode(java.lang.String)
	 */
	public void setMyZipCode(String myZipCode) {
		this.myZipCode = myZipCode;
	}

	/**
	 * @throws IOException
	 */
	public void generate() throws IOException, FileNotFoundException,
			DocumentException {

		// Dataset A
		dtAusSetA.setBankleitzahl(bankCode);
		dtAusSetA.setKundenname(myCompanyName);
		dtAusSetA.setKontonummer(bankAccountNumber);

		// Set A and E dataset in writer
		dtAusWriter.setA(dtAusSetA);
		dtAusWriter.setE(dtAusSetE);
		// Write and close file
		dtAusWriter.writeFile();

		// Generate PDF sheet
		dtAusSheet = new DtAusSheet(pdfFile, dtAusSetE);
		dtAusSheet.setDtAusFileName(dtAus0.getName());
		dtAusSheet.setSubmitterCompanyName(myCompanyName);
		dtAusSheet.setSubmitterFirstName(myFirstName);
		dtAusSheet.setSubmitterLastName(myLastName);
		dtAusSheet.setSubmitterStreet(myStreet);
		dtAusSheet.setSubmitterZipCode(myZipCode);
		dtAusSheet.setSubmitterCity(myCity);
		dtAusSheet.setSubmitterAccountNumber(bankAccountNumber);
		dtAusSheet.setBankCompanyName(bankCompanyName);
		dtAusSheet.setBankFirstName(bankFirstName);
		dtAusSheet.setBankLastName(bankLastName);
		dtAusSheet.setBankStreet(bankStreet);
		dtAusSheet.setBankZipCode(bankZipCode);
		dtAusSheet.setBankCity(bankCity);
		dtAusSheet.setBankCode(bankCode);
		// Create and close file
		dtAusSheet.create();

		// Close file
		csvFileWriter.close();

	}

}