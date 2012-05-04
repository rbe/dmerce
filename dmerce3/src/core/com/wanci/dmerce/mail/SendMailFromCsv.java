/*
 * Created on Dec 9, 2003
 *  
 */
package com.wanci.dmerce.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.mail.Address;

import com.wanci.dmerce.csv.CsvData;
import com.wanci.dmerce.csv.CsvHeader;
import com.wanci.dmerce.csv.CsvSettings;
import com.wanci.dmerce.exceptions.NoCsvDataPresentException;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version ${Id}
 * 
 * Use one template file with placeholders for mail text and another data
 * source (such as a plain text file/CSV) for values,that should be inserted.
 * 
 * Example for a template:
 * 
 * Dear Mr. $FIRSTNAME $LASTNAME,
 * 
 * how do you do? bla bla bla
 * 
 * Is your address correct? $STREET $ZIPCODE $CITY
 * 
 * Regards 1Ci GmbH
 * 
 * Example for a CSV file:
 * 
 * FIRSTNAME;LASTNAME;EMAILADDR;STREET;ZIPCODE;CITY
 * Bensmann;Ralf;r.bensmann@1ci.de;Heisstr. 51;48145;Muenster
 *  
 */
public class SendMailFromCsv {
	
	boolean DEBUG = true;

	File template;

	Vector linesOfTemplate = new Vector();

	File csv;

	Vector linesOfCsv = new Vector();

	CsvHeader csvHeader;

	CsvData csvData;

	SendMailFromCsv(File template, File csv) {
		this.template = template;
		this.csv = csv;
	}

	/**
	 * Read a template into a vector of lines
	 * 
	 * @throws IOException
	 */
	void readTemplate() throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(template));
		String line;
		while ((line = br.readLine()) != null) {
			linesOfTemplate.add(line);
		}

		LangUtil.consoleDebug(DEBUG, 
			"Read " + linesOfTemplate.size() + " lines from template");

	}

	/**
	 * Initializes CSV data source
	 * 
	 * @throws IOException
	 */
	void initCsv() throws IOException {

		CsvSettings csvSettings = new CsvSettings();
		csvHeader = new CsvHeader(csvSettings);
		BufferedReader br = new BufferedReader(new FileReader(csv));
		String line;

		// First line of CSV file is the header
		csvHeader.setHeader(br.readLine());
		//csvHeader.dump();

		while ((line = br.readLine()) != null) {

			csvData = new CsvData(csvSettings, line);
			csvData.splitLine();

			linesOfCsv.add(csvData);

		}

		LangUtil.consoleDebug(DEBUG, "Read " + linesOfCsv.size() + " lines from CSV");

	}

	/**
	 * Evaluates template with one line of data from CSV
	 *  
	 */
	void evaluateTemplate(CsvData csvData) {

		SendMail sm = new SendMail("localhost");
		StringBuffer body = new StringBuffer();
		Iterator linesOfTemplateIterator = linesOfTemplate.iterator();

		try {

			while (linesOfTemplateIterator.hasNext()) {

				String line = (String) linesOfTemplateIterator.next();

				// Search for $ and a name of a header from csvHeader
				Iterator headers = csvHeader.getFields().iterator();
				while (headers.hasNext()) {

					String header = (String) headers.next();
					int idx = line.indexOf("$" + header);
					if (idx >= 0) {

						try {

							// Retrieve data
							String fieldData =
								csvData.getFieldDataByPosition(
									csvHeader.getFieldPositionByName(header));

							// Prepend dollar sign for further processing
							// like replaceAll()
							header = "\\$" + header;

							// We got no data from a CSV field
							if (fieldData == null) {

								fieldData = "";

								// Is character before placeholder a space?
								// If yes, remove it to avoid two spaces
								int j = idx + header.length();
								if (Character
									.isSpaceChar(line.charAt(idx - 1)))
									header = " " + header;

							}

							line = line.replaceAll(header, fieldData);

						}
						catch (NoCsvDataPresentException e) {
						}

					}

				}

				body.append(line + "\n");

			}

			sm.setFromHeader("\"1Ci GmbH - Christian Ewe\" <c.ewe@1ci.de>");
			sm.setSubject("Kontaktaufnahme - Partnerschaft/Kooperation");
			sm.setToHeader(
				csvData.getFieldDataByPosition(
					csvHeader.getFieldPositionByName("Mail")));
			sm.addBccHeader("c.ewe@1ci.de");
			sm.addBccHeader("r.bensmann@1ci.de");
			sm.addBody(body.toString(), sm.TEXTHTML);

			sm.sendMail();

			LangUtil.consoleDebug(DEBUG, 
				"Sending mail to " + (Address) sm.getToHeaderIterator().next());

		}
		catch (Exception e) {
			LangUtil.consoleDebug(DEBUG, 
				"Could not send mail to "
					+ (Address) sm.getToHeaderIterator().next()
					+ ": "
					+ e.getCause()
					+ ": "
					+ e.getMessage());
		}

	}

	/**
	 * Process every line of CSV data against template
	 *  
	 */
	void evaluate() {

		Iterator i = linesOfCsv.iterator();
		while (i.hasNext()) {
			CsvData csvData = (CsvData) i.next();
			evaluateTemplate(csvData);
		}

	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		boolean DEBUG = true;
		
		LangUtil.consoleDebug(DEBUG, "START");

		SendMailFromCsv s =
			new SendMailFromCsv(
				new File("/export/home/rb/template.txt"),
				new File("/export/home/rb/csv.txt"));

		try {
			s.initCsv();
			s.readTemplate();
			s.evaluate();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		LangUtil.consoleDebug(DEBUG, "STOP");

	}

}