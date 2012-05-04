/*
 * Created on 12.02.2004
 *  
 */
package com.wanci.customer.vsv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
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
 * @version $Id: VsvLastschrift.java,v 1.7 2004/04/23 15:40:09 rb Exp $
 *  
 */
public class VsvLastschrift extends DtAusLastschrift {

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
        + " WHERE t.Bezahlt = 0" + " AND ta.ID = t.TarifID"
        + " AND t.KontoNr != ''" + " AND t.BLZ != ''" + " AND t.Geprueft = 1"
        + " AND t.AustrittVSV IS NULL";

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
    public VsvLastschrift(Database jdbcDatabase) throws IOException {
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

        // Update a member: set status to "payed"
        PreparedStatement updateMember = jdbcDatabase
            .getPreparedStatement("UPDATE Teilnehmer"
                + " SET bezahlt = 1, BeitragslaufAm = ?" + " WHERE ID = ?");
        updateMember.setDate(1, new java.sql.Date(new Date().getTime()));

        // Reason of transfer
        String reason = "Jahresbeitrag "
            + new SimpleDateFormat("yyyy").format(new Date()) + " VSV";
        String reason2 = "Jahresbeitrag Verm.Haft. "
            + new SimpleDateFormat("yy").format(new Date());

        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);

        int no = 0;
        ResultSet rsMember = jdbcDatabase.executeQuery(selectMembers);
        while (rsMember.next()) {

            no++;

            String kontoinhaber = rsMember.getString("Kontoinhaber").trim();
            double beitrag = rsMember.getDouble("Beitrag");

            logWriter.write(no + " Berechne Beitrag für Mitglied" + " '"
                + kontoinhaber + "': " + df.format(beitrag) + " EUR\n");

            try {
                add(beitrag, kontoinhaber, rsMember.getString("KontoNr"), rsMember
                    .getString("BLZ"), reason);
            }
            catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            int beitragsgruppe = rsMember.getInt("BeitragsgruppenID");
            if (beitragsgruppe != 20 || beitragsgruppe != 21) {

                no++;

                logWriter.write(no + " Berechne Beitrag für Mitglied" + " '"
                    + kontoinhaber + "': 75,00 EUR (Verm.Schaden)\n");

                try {
                    add(75, kontoinhaber, rsMember.getString("KontoNr"), rsMember
                        .getString("BLZ"), reason2);
                }
                catch (ParseException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }

            updateMember.setInt(2, rsMember.getInt("ID"));
            updateMember.execute();

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

        Boot.printCopyright("1[PAYMENT] - Beitragslauf VSV e.V.");

        //logWriter.write( "START");

        Database jdbcDatabase = DatabaseHandler.getDatabaseConnection("vsv");
        jdbcDatabase.openConnection();
        //logWriter.write( "Successfully connected to database");

        //logWriter.write( "Begin processing");
        VsvLastschrift v = new VsvLastschrift(jdbcDatabase);
        v.processMembers();

        jdbcDatabase.closeConnection();

        //logWriter.write( "STOP");

    }

}