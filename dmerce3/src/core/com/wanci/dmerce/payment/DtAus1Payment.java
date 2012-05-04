/*
 * Created on Jun 1, 2004
 *  
 */
package com.wanci.dmerce.payment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import com.lowagie.text.DocumentException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.java.ZIP;

/**
 * @author rb
 * @version $Id: DtAus1Payment.java,v 1.4 2004/08/04 17:33:18 rb Exp $
 *  
 */
public class DtAus1Payment extends DtAusLastschrift {

	private Database jdbcDatabase;

	private String selectOperator = "SELECT * FROM t_operator WHERE id = 1";

	/**
	 * Offene Rechnungen, die abgerechnet werden muessen
	 */
	private String selectDTAUS = "SELECT * FROM v_dtaus ORDER BY lastname, firstname";

	public DtAus1Payment(Database jdbcDatabase) throws IOException {
		this.jdbcDatabase = jdbcDatabase;
	}

	/**
	 * Hole Daten des Betreibers von der Datenbank
	 * 
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 */
	public void getOperatorData() throws IllegalArgumentException, SQLException {

		// Retrieve information of customer's bank
		ResultSet rsBank = jdbcDatabase.executeQuery(selectOperator);
		rsBank.next();

		setMyCompanyName(rsBank.getString("company"));
		setMyFirstName(rsBank.getString("firstname"));
		setMyLastName(rsBank.getString("lastname"));
		setMyStreet(rsBank.getString("street"));
		setMyZipCode(rsBank.getString("zipcode"));
		setMyCity(rsBank.getString("city"));
		setBankAccountNumber(rsBank.getString("accountnumber"));
		setBankCode(rsBank.getString("bankcode"));
		setBankCompanyName(rsBank.getString("bankname"));
		setBankStreet(rsBank.getString("bankstreet"));
		setBankZipCode(rsBank.getString("bankzipcode"));
		setBankCity(rsBank.getString("bankcity"));

	}

	/**
	 * Verarbeitung der ausstehenden, offenen Posten zu einer DTAUS- Datei für
	 * die Bank - Aufruf der Prozedur IGGApayment.run_igga (soweit noch nicht
	 * geschehen) - Verarbeiten der Tabelle v_dtaus - Aufruf der Prozedur
	 * WANpayment.invoice_payed wenn Datensatz verarbeitet wurde
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *  
	 */
	public void processInvoices() throws SQLException, FileNotFoundException,
			IOException, DocumentException {

		// Call stored procedure WANpayment.invoice_payed
		CallableStatement invoicePayed = jdbcDatabase
				.getCallableStatement("{call WANpayment.invoice_payed(?)}");

		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(2);

		int no = 0;
		ResultSet rsDTAUS = jdbcDatabase.executeQuery(selectDTAUS);
		while (rsDTAUS.next()) {

			no++;

			int invoiceid = rsDTAUS.getInt("invoiceid");
			String accountowner = rsDTAUS.getString("accountowner").trim();
			String bankcode = rsDTAUS.getString("bankcode");
			String accountnumber = rsDTAUS.getString("accountnumber");
			double amount = rsDTAUS.getDouble("amount");
			String text = rsDTAUS.getString("text").trim();

			try {

				System.out.println(amount + " " + accountowner + " "
						+ accountnumber + " " + bankcode + " " + text);

				// 
				add(amount, accountowner, accountnumber, bankcode, text);

				// Call WANpayment.invoice_payed
				System.out.println("invoice_payed(" + invoiceid + ")");
				invoicePayed.setInt(1, invoiceid);
				invoicePayed.execute();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// Generate files
		generate();

		//
		// This is now done by the user itself via 1PAYMENT web
		// application
		//
		// Send emails
		//CallableStatement sendMails = jdbcDatabase
		//		.getCallableStatement("{call WANpayment.send_todo_mails()}");
		//sendMails.execute();

	}

	/**
	 * Create ZIP archive from generated files
	 * 
	 * @author rb
	 * @version $Id: DtAus1Payment.java,v 1.4 2004/08/04 17:33:18 rb Exp $
	 *  
	 */
	public void generateZIP() throws IOException {

		ZIP zip = new ZIP("payment.zip");
		zip.addFile(pdfFile.getName(), pdfFile);
		zip.addFile(dtAus0.getName(), dtAus0);
		zip.addFile(csvFile.getName(), dtAus0);
		zip.create();

		pdfFile.delete();
		dtAus0.delete();
		csvFile.delete();

	}

	public static void main(String[] args) {

		try {

			Database jdbcDatabase = DatabaseHandler
					.getDatabaseConnection("1payment");
			jdbcDatabase.openConnection();

			DtAus1Payment pay = new DtAus1Payment(jdbcDatabase);
			pay.getOperatorData();
			pay.processInvoices();
			pay.generateZIP();

			jdbcDatabase.closeConnection();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}