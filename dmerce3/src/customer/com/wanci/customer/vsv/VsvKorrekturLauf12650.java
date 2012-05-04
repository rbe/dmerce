/*
 * Created on May 20, 2004
 *  
 */
package com.wanci.customer.vsv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lowagie.text.DocumentException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.dmerce.payment.DtAusLastschrift;
import com.wanci.java.ZIP;

/**
 * @author rb
 * @version $Id: VsvKorrekturLauf12650.java,v 1.2 2004/06/03 23:51:54 rb Exp $
 *  
 */
public class VsvKorrekturLauf12650 extends DtAusLastschrift {

    private boolean DEBUG = true;

    private File log = new File("lastschrift.log");

    private FileWriter logWriter;

    private Database jdbcDatabase;

    private String selectMembers = "SELECT t.ID," + " t.Name," + " t.Vorname,"
        + " t.Strasse," + " t.PLZ," + " t.Ort," + " t.Kontoinhaber,"
        + " t.Bank," + " t.BLZ," + " t.KontoNr," + " t.Geprueft,"
        + " t.DatenVersendetDatum," + " t.EintrittVSV,"
        + " ta.ID as BeitragsgruppenID," + " ta.NameDE,"
        + " ta.Betrag as Beitrag" + " FROM Teilnehmer as t, Tarif as ta"
        + " WHERE t.Bezahlt = 1" + " AND ta.ID = t.TarifID"
        + " AND t.KontoNr != ''" + " AND t.BLZ != ''" + " AND t.Geprueft = 1"
        + " AND t.AustrittVSV IS NULL" + " AND ta.ID = 7"
        + " AND t.ID NOT IN (2209, 2026, 2025, 2154, 1735, 2254)";

    private String selectBank = "SELECT Organisation, Name, Firstname, Street, ZipCode, City,"
        + " POBox, ZipCodePOBox, Phone, Fax, Mobile, Email, URL,"
        + " TreasurerBankCode, TreasurerAccountNumber, TreasurerAccountOwner,"
        + " BankName, BankReceiver, BankStreet, BankZipCode, Filename,"
        + " DebitNo, BankBankCode, TreasurerBank, PaymentNo, PaymentType,"
        + " FileReceiverEmail, BankCity, BankReceiverTitle"
        + " FROM PaymentInformation" + " WHERE ID = 1";

    /**
     * Constructor
     *  
     */
    public VsvKorrekturLauf12650(Database jdbcDatabase) throws IOException {
        this.jdbcDatabase = jdbcDatabase;
        logWriter = new FileWriter(log);
    }

    /**
     * Process members of VSV
     * 
     * @throws IllegalArgumentException
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws DocumentException
     */
    void processMembers() throws IllegalArgumentException, SQLException,
        FileNotFoundException, IOException, DocumentException {

        // Retrieve information of customer's bank
        ResultSet rsBank = jdbcDatabase.executeQuery(selectBank);
        rsBank.next();
        setMyCompanyName(rsBank.getString("TreasurerAccountOwner"));
        setMyFirstName(rsBank.getString("Firstname"));
        setMyLastName(rsBank.getString("Name"));
        setMyStreet(rsBank.getString("Street"));
        setMyZipCode(rsBank.getString("ZipCode"));
        setMyCity(rsBank.getString("City"));
        setBankAccountNumber(rsBank.getString("TreasurerAccountNumber"));
        setBankCode(rsBank.getString("TreasurerBankCode"));
        setBankCompanyName(rsBank.getString("BankName"));
        setBankStreet(rsBank.getString("BankStreet"));
        setBankZipCode(rsBank.getString("BankZipCode"));
        setBankCity(rsBank.getString("BankCity"));

        // Reason of transfer
        String reason = "Jahresbeitrag "
            + new SimpleDateFormat("yyyy").format(new Date()) + " VSV";

        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);

        System.out.println("SQL Stmt:\n" + selectMembers + "\n");

        int no = 0;
        ResultSet rsMember = jdbcDatabase.executeQuery(selectMembers);
        while (rsMember.next()) {

            no++;

            String kontoinhaber = rsMember.getString("Kontoinhaber").trim();
            double beitrag = rsMember.getDouble("Beitrag");

            System.out.println(no + " Berechne Beitrag für Mitglied" + " '"
                + kontoinhaber + "': " + df.format(beitrag) + " EUR\n");

            try {
                add(126.5 - 12.65, kontoinhaber, rsMember.getString("KontoNr"),
                    rsMember.getString("BLZ"), reason);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }

        }

        generate();

        ZIP zip = new ZIP("payment.zip");
        zip.addFile(pdfFile.getName(), pdfFile);
        zip.addFile(dtAus0.getName(), dtAus0);
        zip.addFile(log.getName(), log);
        zip.create();

        pdfFile.delete();
        dtAus0.delete();
        log.delete();

    }

    /**
     * @param args
     * @throws IllegalArgumentException
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws IOException
     * @throws DocumentException
     */
    public static void main(String[] args) throws IllegalArgumentException,
        FileNotFoundException, SQLException, IOException, DocumentException {

        boolean DEBUG = true;

        Boot
            .printCopyright("1[PAYMENT] - Beitragslauf VSV e.V. - Korrektur 126,50 EUR");
        
        //logWriter.write( "START");

        Database jdbcDatabase = DatabaseHandler.getDatabaseConnection("vsv");
        jdbcDatabase.openConnection();
        //logWriter.write( "Successfully connected to database");

        //logWriter.write( "Begin processing");
        VsvKorrekturLauf12650 v = new VsvKorrekturLauf12650(jdbcDatabase);
        v.processMembers();

        jdbcDatabase.closeConnection();

        //logWriter.write( "STOP");

    }

}