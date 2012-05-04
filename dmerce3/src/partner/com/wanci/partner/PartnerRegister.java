/*
 * Created on Oct 27, 2003
 *
 */
package com.wanci.partner;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.dmerce.mail.SendMail;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version ${Id}
 *
 */
public class PartnerRegister {

	boolean DEBUG = true;

	/**
	 * JDBC database connection
	 */
	Database d;

	PartnerRegisterSqlPool sqlpool;

	/**
	 * 
	 */
	public PartnerRegister() {
		d = DatabaseHandler.getDatabaseConnection("blpreg");
	}

	/**
	 * 
	 * @throws SQLException
	 */
	public void deinit() throws SQLException {
		d.closeConnection();
	}

	/**
	 * 
	 * @throws SQLException
	 */
	public void init() throws SQLException {
		d.openConnection();
		sqlpool = new PartnerRegisterSqlPool(d);
	}

	/**
	 * Process partner test registrations.
	 * mysql> desc t_blpreg;
	 * +-----------+--------------+------+-----+-----------------+----------------+
	 * | Field     | Type         | Null | Key | Default         | Extra          |
	 * +-----------+--------------+------+-----+-----------------+----------------+
	 * | id        | int(11)      |      | PRI | NULL            | auto_increment |
	 * | partnerid | int(11)      |      |     | 1               |                |
	 * | company   | varchar(100) | YES  |     | NULL            |                |
	 * | firstname | varchar(100) |      |     |                 |                |
	 * | lastname  | varchar(100) |      |     |                 |                |
	 * | street    | varchar(100) |      |     |                 |                |
	 * | zipcode   | varchar(5)   |      |     |                 |                |
	 * | city      | varchar(100) |      |     |                 |                |
	 * | country   | varchar(100) | YES  |     | NULL            |                |
	 * | phone     | varchar(100) | YES  |     | NULL            |                |
	 * | fax       | varchar(100) | YES  |     | NULL            |                |
	 * | email     | varchar(100) |      |     |                 |                |
	 * | remarks   | varchar(250) | YES  |     | NULL            |                |
	 * | tacread   | char(1)      |      |     |                 |                |
	 * | productid | varchar(50)  |      |     | UNKNOWN PRODUCT |                |
	 * | todo      | int(1)       |      |     | 1               |                |
	 * +-----------+--------------+------+-----+-----------------+----------------+
	 */
	private void processPartnerTestRegistrations()
		throws
			IllegalArgumentException,
			SQLException,
			AddressException,
			MessagingException {

		LangUtil.consoleDebug(DEBUG, "Querying database for new registrations");

		int ok = 0;
		int notok = 0;

		ResultSet rs = sqlpool.sNewRegistrations.executeQuery();
		while (rs.next()) {

			int id = rs.getInt("id");
			LangUtil.consoleDebug(DEBUG, "Processing new registration #" + id);

			try {

				String partnerCompany = rs.getString("partnercompany");
				String partnerEmail = rs.getString("partneremail");
				String partnerPhone = rs.getString("partnerPhone");
				String email = rs.getString("email");
				String company = rs.getString("company");
				String firstname = rs.getString("firstname");
				String lastname = rs.getString("lastname");
				String street = rs.getString("street");
				String zipcode = rs.getString("zipcode");
				String city = rs.getString("city");
				String phone = rs.getString("phone");
				String fax = rs.getString("fax");
				String remarks = rs.getString("remarks");
				String productid = rs.getString("fullname");

				// Check for null-values
				company = company == null ? "keine Angabe" : company;
				street = street == null ? "keine Angabe" : street;
				city = city == null ? "keine Angabe" : city;
				phone = phone == null ? "keine Angabe" : phone;
				fax = fax == null ? "keine Angabe" : fax;
				remarks = remarks == null ? "keine Angabe" : remarks;

				// Check for length() == 0
				company = company.length() == 0 ? "keine Angabe" : company;
				street = street.length() == 0 ? "keine Angabe" : street;
				city = city.length() == 0 ? "keine Angabe" : city;
				phone = phone.length() == 0 ? "keine Angabe" : phone;
				fax = fax.length() == 0 ? "keine Angabe" : fax;
				remarks = remarks.length() == 0 ? "keine Angabe" : remarks;

				SendMail s = new SendMail();
				s.setSubject("Registrierung eines Testzuganges fuer " + productid);
				s.setFromHeader(partnerEmail);
				s.setToHeader(email);
				s.addCcHeader(partnerEmail);
				s.addBccHeader("partner-services@1ci.de");
				StringBuffer sb = new StringBuffer();
				sb.append("Sehr geehrte(r) Herr/Frau ");
				if (firstname.length() > 1)
					sb.append(firstname + " ");
				sb.append(
					lastname
						+ ","
						+ "\n\n"
						+ "vielen Dank fuer die Registrierung eines Testzugangs zu"
						+ "\n\n\t"
						+ productid
						+ "\n\n"
						+ "Sie haben folgende Daten eingegeben:\n"
						+ "\n--- BEGINN ---"
						+ "\nFirma:\t\t"
						+ company
						+ "\nName:\t\t");

				if (firstname.length() > 0)
					sb.append(firstname + " ");

				sb.append(
					lastname + "\nAnschrift:\t" + street + "\nPLZ Ort:\t");

				if (zipcode.length() > 0)
					sb.append(zipcode + " ");

				sb.append(
					city
						+ "\nTelefon:\t"
						+ phone
						+ "\nFax:\t\t"
						+ fax
						+ "\nEmail:\t\t"
						+ email
						+ "\nBemerkung:\n"
						+ remarks
						+ "\n--- ENDE ---"
						+ "\n\n"
						+ "Wir werden Ihre Anfrage umgehend bearbeiten. Bitte"
						+ "\nhaben Sie ein wenig Geduld. Sie werden automatisch"
						+ "\nunter der von Ihnen angegebenen Email-Adresse informiert,"
						+ "\nsobald Ihr Testsystem zur Verfuegung steht."
						+ "\n\nBei Fragen stehen wir Ihnen selbstverstaendlich"
						+ "\njederzeit gern zur Verfuegung. Wenden Sie sich bitte an"
						+ "\nunseren Support unter "
						+ partnerEmail
						+ " oder "
						+ partnerPhone
						+ "\n\nVielen Dank!"
						+ "\n\nMit freundlichem Gruss"
						+ "\nIhr Team von "
						+ partnerCompany);

				s.addBody(sb.toString(), s.TEXTPLAIN);

				d.executeUpdate(
					"UPDATE t_blpreg SET todo = 0, mailsentat = SYSDATE() WHERE id = " + id);

				LangUtil.consoleDebug(
					DEBUG,
					"Sending mail for new registration #" + id);
				s.sendMail();

				ok++;

			}
			catch (Exception e) {
				LangUtil.consoleDebug(
					true,
					"Could not send new registration #"
						+ id
						+ ": "
						+ e.getMessage());
				notok++;
			}

		}

		LangUtil.consoleDebug(
			DEBUG,
			"Processed "
				+ (ok + notok)
				+ " requests. SUCCESS="
				+ ok
				+ " ERROR="
				+ notok);

	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		LangUtil.consoleDebug(true, "START");
		PartnerRegister pr = new PartnerRegister();
		pr.init();
		pr.processPartnerTestRegistrations();
		pr.deinit();
		LangUtil.consoleDebug(true, "STOP");

	}

}
