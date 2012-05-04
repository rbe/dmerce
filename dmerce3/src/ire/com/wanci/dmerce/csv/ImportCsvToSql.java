package com.wanci.dmerce.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.wanci.dmerce.exceptions.NoCsvDataPresentException;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: ImportCsvToSql.java,v 1.2 2004/05/16 19:44:09 rb Exp $
 * 
 * Importiert eine .csv-Textdatei und fuegt die Daten in eine SQL-Datenbank
 * ein. Ein Mapfile bestimmt dabei, welche Daten aus der .csv-Datei in die
 * SQL-Tabellen kommt und wohin. Es ist wichtig, dass die .csv-Datei einen
 * Header mit den Feldnamen in der ersten Zeile enthaelt.
 *  
 */
public class ImportCsvToSql {

    /**
     *  
     */
    protected boolean debug = true;

    /**
     *  
     */
    protected CsvSettings csvSettings;

    /**
     *  
     */
    protected CsvHeader csvHeader;

    /**
     *  
     */
    protected Mapping csvMapping;

    /**
     *  
     */
    protected String csvTableName;

    /**
     *  
     */
    protected String sqlTableName;

    /**
     *  
     */
    protected Database jdbcDatabase;

    /**
     *  
     */
    protected String fileName;

    /**
     *  
     */
    protected BufferedReader file;

    protected CsvToSqlInsert csvToSqlInsert;

    /**
     * Update-Mode? Siehe Klasse "CsvToSqlUpdate"
     */
    protected boolean updateMode = false;

    protected CsvToSqlUpdate csvToSqlUpdate;

    protected int processedLines;

    /**
     * @param csvSettings
     */
    public ImportCsvToSql(CsvSettings csvSettings)
        throws XmlPropertiesFormatException {

        this.csvSettings = csvSettings;

        try {
            jdbcDatabase = DatabaseHandler.getDatabaseConnection("csv");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Gibt Informationen ueber den Header einer CSV-Datei aus
     *  
     */
    public void dumpHeader() {

        LangUtil.consoleDebug(debug, "#fields in header: "
            + csvHeader.getFieldCount());

        for (int i = 0; i < csvHeader.getFieldCount(); i++) {

            String actField = csvHeader.getFieldNameByPosition(i);
            String sqlFieldName = csvMapping.getMappedFieldName(actField);

            MapRules csvMapRules = csvMapping.getCsvMapRules(actField);

            LangUtil.consoleDebug(debug, "Field " + i + ": " + csvTableName
                + "." + actField + " -> " + sqlTableName + "." + sqlFieldName
                + " type=" + csvMapRules.getFieldType());

        }
    }

    /**
     * @return
     */
    public String getNextLine() {
        try {
            return file.readLine();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Importiert CSV Daten per SQL INSERT-Statement
     * 
     * @param csvData
     * @throws SQLException
     */
    protected void insert(CsvData csvData) throws SQLException,
        NoCsvDataPresentException {

        csvToSqlInsert.setCsvDataLine(csvData);
        PreparedStatement p = csvToSqlInsert.getInsertStatement();
        if (p != null) {

            p.executeUpdate();
            LangUtil.consoleDebug(true, "#" + processedLines
                + " INSERTED CSV-DATA='" + csvData.getLine() + "'");

        }
        else
            LangUtil.consoleDebug(true, "#" + processedLines
                + " WILL NOT INSERT CSV-DATA='" + csvData.getLine() + "'");

    }

    /**
     * 
     *  
     */
    public void processMap() {

        try {

            LangUtil.consoleDebug(debug, "Opening database connection");
            jdbcDatabase.openConnection();

        }
        catch (SQLException se) {
            LangUtil.consoleDebug(true, "Cannot open database connection");
            System.exit(2);
        }

        try {

            LangUtil
                .consoleDebug(debug, "Generating prepared INSERT statement");
            csvToSqlInsert.generatePreparedStatement();

        }
        catch (SQLException se) {

            LangUtil.consoleDebug(true,
                "Cannot create prepared INSERT statement");
            System.exit(2);

        }

        String line = getNextLine();
        while (line != null) {

            line.trim();
            CsvData csvData = new CsvData(csvSettings, line);

            if (line.length() >= 1) {

                try {

                    processedLines++;

                    // Update-Mode?
                    if (updateMode) {

                        csvToSqlUpdate.setCsvDataLine(new CsvData(line));
                        csvToSqlUpdate.check();
                        if (csvToSqlUpdate.isInsert())
                            insert(csvData);
                        else if (csvToSqlUpdate.isUpdate()) {

                            csvToSqlUpdate.update();
                            LangUtil.consoleDebug(true, "#" + processedLines
                                + " UPDATED CSV-DATA='" + csvData.getLine()
                                + "'");

                        }
                    }
                    else
                        insert(csvData);

                }
                catch (Exception e) {

                    //e.printStackTrace();

                    LangUtil.consoleDebug(true, "#" + processedLines
                        + " ERROR '" + e.getCause() + "': '"
                        + e.getMessage().trim() + "' WITH CSV-DATA='"
                        + csvData.getLine() + "'");
                }

            }

            line = getNextLine();

        }

        try {
            LangUtil.consoleDebug(debug, "Closing database connection");
            jdbcDatabase.closeConnection();
        }
        catch (SQLException se) {
            se.printStackTrace();
            System.exit(2);
        }

    }

    /**
     * @param fileName
     */
    public void setFile(String fileName) {

        this.fileName = fileName;

        try {
            file = new BufferedReader(new FileReader(fileName));
        }
        catch (IOException e) {
            LangUtil.consoleDebug(debug, "Error while reading file '"
                + fileName + "': '" + e.getMessage() + "'");
        }
    }

    /**
     * @param csvMapping
     */
    public void setMap(Mapping csvMapping) {

        this.csvMapping = csvMapping;

        csvTableName = csvMapping.getCsvTableName();
        sqlTableName = csvMapping.getSqlTableName();

        LangUtil.consoleDebug(debug, "Reading header line");
        csvHeader = new CsvHeader(csvSettings);
        csvHeader.setHeader(getNextLine());
        LangUtil.consoleDebug(debug, "CSV Header: " + csvHeader.getHeader());

        LangUtil.consoleDebug(debug, "Initializing CsvToSqlInsert");
        csvToSqlInsert = new CsvToSqlInsert(csvSettings, csvHeader, csvMapping,
            jdbcDatabase);

    }

    /**
     * @param updateMode
     */
    public void setUpdateMode(boolean updateMode) throws SQLException {

        this.updateMode = updateMode;

        LangUtil.consoleDebug(debug, "Initializing CsvToSqlUpdate");
        if (jdbcDatabase != null && csvSettings != null && csvHeader != null
            && csvMapping != null) {

            csvToSqlUpdate = new CsvToSqlUpdate(jdbcDatabase, csvSettings,
                csvHeader, csvMapping);

        }
        else {
            LangUtil.consoleDebug(debug, "Cannot initialize CsvToSqlUpdate");
            System.exit(2);
        }

    }

}