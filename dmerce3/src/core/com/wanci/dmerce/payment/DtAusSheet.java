/*
 * Created on 06.02.2004
 *  
 */
package com.wanci.dmerce.payment;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author rb
 * @version ${Id}
 *  
 */
public class DtAusSheet {

	/**
	 * The document itself
	 */
	private Document document = new Document(PageSize.A4, 50, 50, 50, 50);
	/**
	 * A pdf writer that listens to document
	 */
	private PdfWriter pdfWriter;
	/**
	 * Colors
	 */
	private Color black = new Color(0, 0, 0);
	private Color blue = new Color(0, 0, 255);
	/**
	 * Predefine fonts used by our document
	 */
	private Font timesRomanNormalBlack =
		FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, black);
	private Font timesRomanBoldUnderlineBlue =
		FontFactory.getFont(
			FontFactory.HELVETICA_BOLD,
			20,
			Font.BOLD | Font.UNDERLINE,
			blue);
	/**
	 *  
	 */
	private String dtAusFileName = "DTAUS0.TXT";
	/**
	 * Company name of submitter
	 */
	private String submitterCompanyName =
		"Machsewohl Deinearbeit GmbH & Co. KG";
	/**
	 *  
	 */
	private String submitterFirstName = "Sigfried";
	/**
	 *  
	 */
	private String submitterLastName = "Setz Diewerte";
	/**
	 *  
	 */
	private String submitterStreet = "Musterstr. 1";
	/**
	 *  
	 */
	private String submitterZipCode = "12345";
	/**
	 *  
	 */
	private String submitterAccountNumber;
	/**
	 *  
	 */
	private String submitterCity = "Musterstadt";
	/**
	 *  
	 */
	private String bankCompanyName = "Gibbe Kohle AG";
	/**
	 *  
	 */
	private String bankFirstName = "Gotthilf";
	/**
	 *  
	 */
	private String bankLastName = "Gierig";
	/**
	 *  
	 */
	private String bankStreet = "Musterstr. 1";
	/**
	 *  
	 */
	private String bankZipCode = "12345";
	/**
	 *  
	 */
	private String bankCity = "Musterstadt";
	/**
	 *  
	 */
	private String bankCode = "12345678";

	private DtAusSetE dtAusSetE;

	/**
	 * @param fileName
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	public DtAusSheet(File fileName, DtAusSetE dtAusSetE)
		throws FileNotFoundException, DocumentException {

		this.dtAusSetE = dtAusSetE;

		pdfWriter =
			PdfWriter.getInstance(document, new FileOutputStream(fileName));
		document.open();

	}

	private void addHeader() throws DocumentException {

		Paragraph p1 = new Paragraph();
		p1.add(new Chunk(submitterCompanyName + "\n", timesRomanNormalBlack));
		p1.add(
			new Chunk(
				submitterFirstName + " " + submitterLastName + "\n",
				timesRomanNormalBlack));
		p1.add(new Chunk(submitterStreet + "\n", timesRomanNormalBlack));
		p1.add(
			new Chunk(
				submitterZipCode + " " + submitterCity + "\n",
				timesRomanNormalBlack));
		p1.add(new Chunk("\n\n\n", timesRomanNormalBlack));
		document.add(p1);

		Paragraph p2 = new Paragraph();
		p2.add(new Chunk(bankCompanyName + "\n", timesRomanNormalBlack));
		/*
		 * if (bankLastName != null) p2.add( new Chunk( "z.H. " + bankFirstName + " " +
		 * bankLastName + "\n", timesRomanNormalBlack));
		 */
		p2.add(new Chunk(bankStreet + "\n\n", timesRomanNormalBlack));
		p2.add(
			new Chunk(
				bankZipCode + " " + bankCity + "\n",
				timesRomanNormalBlack));
		p2.add(new Chunk("\n\n\n", timesRomanNormalBlack));
		document.add(p2);

		Paragraph p3 = new Paragraph();
		p3.add(new Chunk("\n", timesRomanNormalBlack));
		p3.add(
			new Chunk("Diskettenbegleitzettel", timesRomanBoldUnderlineBlue));
		p3.add(new Chunk("\n\n", timesRomanNormalBlack));
		p3.add(
			new Chunk(
				"Belegloser Datenträgeraustausch",
				timesRomanBoldUnderlineBlue));
		p3.add(new Chunk("\n\n\n", timesRomanNormalBlack));
		document.add(p3);

	}

	private void addTable() throws DocumentException {

		PdfPTable pdfTable = new PdfPTable(2);
		pdfTable.setTotalWidth(400);
		pdfTable.getDefaultCell().setBorder(0);
		
		pdfTable.addCell("Dateiname");
		pdfTable.addCell(dtAusFileName);
		
		pdfTable.addCell("Zahlart");
		pdfTable.addCell("Lastschriften");
		
		pdfTable.addCell("Erstellungsdatum");
		pdfTable.addCell(new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
		
		pdfTable.addCell("Anzahl Datensätze C");
		pdfTable.addCell(
			dtAusSetE.getAnzDatensaetzeC().replaceFirst("^0+", ""));
		
		pdfTable.addCell("Summe EURO");
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(2);
		pdfTable.addCell(df.format(dtAusSetE.getSummeBetraegeAsDouble()));
		
		pdfTable.addCell("Kontrollsumme Kontonummern");
		pdfTable.addCell(
			dtAusSetE.getSummeKontonummern().replaceFirst("^0+", ""));
		
		pdfTable.addCell("Kontrollsumme Bankleitzahlen");
		pdfTable.addCell(dtAusSetE.getSummeBlz().replaceFirst("^0+", ""));
		
		pdfTable.addCell("Auftraggeber");
		pdfTable.addCell(
			submitterCompanyName
				+ ", "
				+ submitterStreet
				+ ", "
				+ submitterZipCode
				+ " "
				+ submitterCity);
		
		pdfTable.addCell("Kontonummer des Auftraggebers");
		pdfTable.addCell(submitterAccountNumber);
		
		pdfTable.addCell("Bankleitzahl des Auftraggebers");
		pdfTable.addCell(bankCode);
		
		pdfTable.addCell("Auftrag an");
		pdfTable.addCell(
			bankCompanyName
				+ ", "
				+ bankStreet
				+ ", "
				+ bankZipCode
				+ " "
				+ bankCity);
		
		pdfTable.writeSelectedRows(
			0,
			-1,
			50,
			420,
			pdfWriter.getDirectContent());

		pdfTable = new PdfPTable(2);
		pdfTable.setTotalWidth(400);
		pdfTable.getDefaultCell().setBorder(0);
		pdfTable.addCell(new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
		pdfTable.addCell("____________________________");
		pdfTable.addCell("Erstellungsdatum");
		pdfTable.addCell("Unterschrift(en) des Auftraggebers");
		pdfTable.writeSelectedRows(
			0,
			-1,
			50,
			100,
			pdfWriter.getDirectContent());

	}

	/**
	 * Save and close the document
	 *  
	 */
	void create() throws DocumentException {
		addHeader();
		addTable();
		document.close();
	}

	/**
	 * @param bankCity
	 *            The bankCity to set.
	 */
	public void setBankCity(String bankCity) {
		this.bankCity = bankCity;
	}

	/**
	 * @param bankCompanyName
	 *            The bankCompanyName to set.
	 */
	public void setBankCompanyName(String bankCompanyName) {
		this.bankCompanyName = bankCompanyName;
	}

	/**
	 * @param bankFirstName
	 *            The bankFirstName to set.
	 */
	public void setBankFirstName(String bankFirstName) {
		this.bankFirstName = bankFirstName;
	}

	/**
	 * @param bankLastName
	 *            The bankLastName to set.
	 */
	public void setBankLastName(String bankLastName) {
		this.bankLastName = bankLastName;
	}

	/**
	 * @param bankStreet
	 *            The bankStreet to set.
	 */
	public void setBankStreet(String bankStreet) {
		this.bankStreet = bankStreet;
	}

	/**
	 * @param bankZipCode
	 *            The bankZipCode to set.
	 */
	public void setBankZipCode(String bankZipCode) {
		this.bankZipCode = bankZipCode;
	}

	/**
	 * @param submitterCity
	 *            The submitterCity to set.
	 */
	public void setSubmitterCity(String submitterCity) {
		this.submitterCity = submitterCity;
	}

	/**
	 * @param submitterCompanyName
	 *            The submitterCompanyName to set.
	 */
	public void setSubmitterCompanyName(String submitterCompanyName) {
		this.submitterCompanyName = submitterCompanyName;
	}

	/**
	 * @param submitterFirstName
	 *            The submitterFirstName to set.
	 */
	public void setSubmitterFirstName(String submitterFirstName) {
		this.submitterFirstName = submitterFirstName;
	}

	/**
	 * @param submitterLastName
	 *            The submitterLastName to set.
	 */
	public void setSubmitterLastName(String submitterLastName) {
		this.submitterLastName = submitterLastName;
	}

	/**
	 * @param submitterStreet
	 *            The submitterStreet to set.
	 */
	public void setSubmitterStreet(String submitterStreet) {
		this.submitterStreet = submitterStreet;
	}

	/**
	 * @param submitterZipCode
	 *            The submitterZipCode to set.
	 */
	public void setSubmitterZipCode(String submitterZipCode) {
		this.submitterZipCode = submitterZipCode;
	}

	/**
	 * @return Returns the submitterAccountNumber.
	 */
	public String getSubmitterAccountNumber() {
		return submitterAccountNumber;
	}

	/**
	 * @param submitterAccountNumber
	 *            The submitterAccountNumber to set.
	 */
	public void setSubmitterAccountNumber(String submitterAccountNumber) {
		this.submitterAccountNumber = submitterAccountNumber;
	}

	/**
	 * @return Returns the dtAusFileName.
	 */
	public String getDtAusFileName() {
		return dtAusFileName;
	}

	/**
	 * @param dtAusFileName
	 *            The dtAusFileName to set.
	 */
	public void setDtAusFileName(String dtAusFileName) {
		this.dtAusFileName = dtAusFileName;
	}

	/**
	 * @return Returns the bankCode.
	 */
	public String getBankCode() {
		return bankCode;
	}

	/**
	 * @param bankCode
	 *            The bankCode to set.
	 */
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

}