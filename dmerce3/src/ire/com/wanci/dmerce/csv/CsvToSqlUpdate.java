/*
 * Created on Jul 16, 2003
 *  
 */
package com.wanci.dmerce.csv;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.Map;

import com.wanci.dmerce.exceptions.NoCsvDataPresentException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: CsvToSqlUpdate.java,v 1.2 2004/05/16 19:44:09 rb Exp $
 * 
 * Der Update-Mode veranlasst die Pruefung der Datensaetze vor dem
 * ImportFromFile.
 * 
 * Anhand des aus der CSV-Datei gelesenen Datensatzes wird ein Datensatz in der
 * SQL-Datenbank gesucht.
 * 
 * Wird - ein -exakt- gleicher Datensatz gefunden, so wird der Datensatz aus
 * der CSV-Datei uebersprungen - ein unterschiedlicher Datensatz gefunden, so
 * werden die Daten aus der CSV-Datei in die SQL-Datenbank geschrieben (hier
 * koennte noch eine Option fuer ein History implentiert werden) - kein
 * Datensatz gefunden, so wird ein neuer Datensatz angelegt
 * 
 * Diese Klasse behandelt einen (1) Datensatz
 *  
 */
public class CsvToSqlUpdate {

    /**
     *  
     */
    private boolean DEBUG = false;

    /**
     *  
     */
    private Database jdbcDatabase;

    /**
     *  
     */
    private Mapping csvMapping;

    /**
     *  
     */
    private CsvSettings csvSettings;

    /**
     *  
     */
    private CsvHeader csvHeader;

    /**
     *  
     */
    private CsvData csvData;

    /**
     *  
     */
    private Helper helper;

    /**
     *  
     */
    private ResultSet resultSet;

    /**
     * Prepared Statement fuer UPDATE
     */
    private PreparedStatement prepUpdateStmt;

    /**
     * Soll ein neuer Datensatz angelegt werden?
     */
    private boolean insert = false;

    /**
     * Soll ein Datensatz geaendert werden?
     */
    private boolean update = false;

    /**
     * Constructor
     *  
     */
    public CsvToSqlUpdate(Database jdbcDatabase, CsvSettings csvSettings,
        CsvHeader csvHeader, Mapping csvMapping) {

        this.jdbcDatabase = jdbcDatabase;
        this.csvMapping = csvMapping;
        this.csvHeader = csvHeader;
        setCsvSettings(csvSettings);
        initHelper();

    }

    /**
     * Constructor
     *  
     */
    public CsvToSqlUpdate(Database jdbcDatabase, CsvSettings csvSettings,
        CsvHeader csvHeader, Mapping csvMapping, CsvData csvData) {

        this.jdbcDatabase = jdbcDatabase;
        setCsvSettings(csvSettings);
        this.csvHeader = csvHeader;
        this.csvMapping = csvMapping;
        setCsvDataLine(csvData);
        initHelper();

    }

    /**
     * Fuehrt die Pruefung durch
     *  
     */
    public void check() throws SQLException, NoCsvDataPresentException {

        try {

            if (prepUpdateStmt == null)
                generatePreparedStatement();

            ResultSet r = getSqlResult();
            int rows = 0;
            while (r.next())
                rows++;

            if (rows > 0) {
                update = true;
                insert = false;
            }
            else {
                insert = true;
                update = false;
            }

            //System.out.println("got " + rows + " rows -> update=" + update
            //    + " insert=" + insert);

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Vergleicht die CSV Daten mit den Daten aus der SQL-Datenbank
     *  
     */
    /*
     * private void compare() throws SQLException {
     * 
     * int cc = resultSetMetaData.getColumnCount(); //System.out.println("cc=" +
     * cc); while (resultSet.next()) {
     * 
     * for (int j = 1; j < cc; j++) {
     * 
     * String columnName = resultSetMetaData.getColumnName(j); Object e1 =
     * resultSet.getObject(j);
     * 
     * String csvFieldName = csvMapping.getMappedFieldName(columnName); String
     * e2 = csvData.getFieldDataByPosition(
     * csvHeader.getFieldPositionByName(csvFieldName)); MapRules mr =
     * csvMapping.getCsvMapRules(csvFieldName);
     * 
     * boolean a = (e1 == e2); System.out.println( mr.getFieldType() + ": " +
     * e1 + " = " + e2 + "? -> " + a); } } }
     */

    /**
     *  
     */
    private void generatePreparedStatement() throws SQLException {

        StringBuffer stmt = new StringBuffer("UPDATE "
            + csvMapping.getSqlTableName() + " SET ");
        Iterator fieldNamesIterator;

        int fieldPos = 0;
        fieldNamesIterator = csvMapping.getFieldNameMapIterator();
        while (fieldNamesIterator.hasNext()) {

            Map.Entry m = (Map.Entry) fieldNamesIterator.next();
            String csvFieldName = (String) m.getKey();
            String sqlFieldName = csvMapping.getMappedFieldName(csvFieldName);

            LangUtil.consoleDebug(DEBUG, "FIELD#=" + ++fieldPos + " "
                + csvFieldName + " -> " + sqlFieldName);

            if (!sqlFieldName.equalsIgnoreCase("id")) {

                stmt.append(sqlFieldName + " = ?");
                if (fieldNamesIterator.hasNext())
                    stmt.append(", ");

            }

        }

        stmt.append(" WHERE ID = ?");
        String s = stmt.toString().replaceAll(",  WHERE", " WHERE");

        prepUpdateStmt = jdbcDatabase.getPreparedStatement(s);
        LangUtil.consoleDebug(DEBUG, "Generated prepared statement: " + s);

    }

    /**
     * Holt den entsprechenden Datensatz aus der SQL-Datenbank
     *  
     */
    private ResultSet getSqlResult() throws SQLException,
        NoCsvDataPresentException {

        String stmt = "SELECT "
            + helper.getFieldNamesSeparatedByComma()
            + " FROM "
            + csvMapping.getSqlTableName()
            + " WHERE ID = "
            + csvData.getFieldDataByPosition(csvHeader
                .getFieldPositionByName(csvMapping.getMappedFieldName("ID")));
        //System.out.println(stmt);

        resultSet = jdbcDatabase.executeQuery(stmt);
        return resultSet;

    }

    /**
     * 
     *  
     */
    private void initHelper() {
        helper = new Helper(csvSettings, csvHeader, csvMapping);
    }

    /**
     * @return
     */
    public boolean isInsert() {
        return insert;
    }

    /**
     * @return
     */
    public boolean isUpdate() {
        return update;
    }

    /**
     * @param line
     */
    public void setCsvDataLine(CsvData csvData) {
        csvData.splitLine();
        this.csvData = csvData;
    }

    /**
     * @param csvSettings
     */
    public void setCsvSettings(CsvSettings csvSettings) {
        this.csvSettings = csvSettings;
    }

    /**
     * @param i
     * @param fieldType
     * @param data
     * @throws SQLException
     */
    public void setValues(int i, String fieldType, String data)
        throws SQLException {

        if (fieldType.equalsIgnoreCase("varchar")) {

            LangUtil.consoleDebug(DEBUG, "Set field#" + i + ", type="
                + fieldType + " to value=" + data);

            if (data != null)
                prepUpdateStmt.setString(i, data);
            else
                prepUpdateStmt.setNull(i, Types.VARCHAR);

        }
        else if (fieldType.equalsIgnoreCase("number")) {

            LangUtil.consoleDebug(DEBUG, "Setting field #" + i + ", type="
                + fieldType + " to value=" + data);

            if (data != null)
                prepUpdateStmt.setInt(i, Integer.valueOf(data).intValue());
            else
                prepUpdateStmt.setNull(i, Types.INTEGER);

        }
        else if (fieldType.equalsIgnoreCase("date")) {

            LangUtil.consoleDebug(DEBUG, "Set field#" + i + ", type="
                + fieldType + " to value=" + data);

            if (data != null) {

                try {
                    prepUpdateStmt.setDate(i, helper.parseGermanDate(data));
                }
                catch (Exception e) {

                    LangUtil.consoleDebug(DEBUG, "Error while parsing date: "
                        + data);
                    prepUpdateStmt.setNull(i, Types.DATE);

                }

            }
            else
                prepUpdateStmt.setNull(i, Types.DATE);

        }

    }

    /**
     * Aendert den Datensatz in der SQL-Datenbank auf die Werte aus der
     * CSV-Datei
     */
    public void update() throws SQLException, NoCsvDataPresentException {

        prepUpdateStmt.clearParameters();

        int id = 0;

        int fieldCount = 0;
        Iterator fieldNamesIterator = csvMapping.getFieldNameMapIterator();
        while (fieldNamesIterator.hasNext()) {

            //Map.Entry m = (Map.Entry) fieldNamesIterator.next();
            //String sqlFieldName = (String) m.getValue();
            //String csvFieldName =
            // csvMapping.getMappedFieldName(sqlFieldName);

            Map.Entry m = (Map.Entry) fieldNamesIterator.next();
            String csvFieldName = (String) m.getKey();
            String sqlFieldName = csvMapping.getMappedFieldName(csvFieldName);

            String data = csvData.getFieldDataByPosition(csvHeader
                .getFieldPositionByName(csvFieldName));
            MapRules csvMapRules = csvMapping.getCsvMapRules(csvFieldName);

            if (sqlFieldName.equalsIgnoreCase("ID")) {
                id = Integer.valueOf(data).intValue();
                continue;
            }

            fieldCount++;

            LangUtil.consoleDebug(DEBUG, "FIELD#=" + fieldCount + " "
                + csvFieldName + " -> " + sqlFieldName + " XTO1="
                + csvMapRules.isXto1() + " DATA=" + data);

            setValues(fieldCount, csvMapRules.getFieldType(), data);

        }

        if (id > 0) {
            fieldCount++;
            LangUtil.consoleDebug(DEBUG, "Set field#" + fieldCount
                + ", to value=" + id);
            //prepUpdateStmt.setInt(43, id);
            prepUpdateStmt.setInt(fieldCount, id);
            prepUpdateStmt.execute();
        }

    }

}