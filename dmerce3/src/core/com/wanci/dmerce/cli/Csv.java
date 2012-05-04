/*
 * Created on Jun 6, 2003
 *
 */
package com.wanci.dmerce.cli;

import java.util.Iterator;

import com.wanci.dmerce.csv.CsvSettings;
import com.wanci.dmerce.csv.ExportMapReader;
import com.wanci.dmerce.csv.ExportSqlToCsv;
import com.wanci.dmerce.csv.ImportCsvToSql;
import com.wanci.dmerce.csv.ImportMapReader;
import com.wanci.dmerce.csv.Mapping;
import com.wanci.dmerce.csv.Mappings;
import com.wanci.dmerce.exceptions.CsvMapRuleException;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.java.LangUtil;
import com.wanci.java.TimeUtil;

/**
 * @author rb
 * @version $Id: Csv.java,v 1.7 2004/04/23 15:33:51 rb Exp $
 * 
 * Command Line Interface fuer das Tool CSV <->SQL
 *  
 */
public class Csv implements Cli {

    private String[] args;

    private ArgumentParser aap;

    /**
     *  
     */
    private CsvSettings csvSettings;

    /**
     *  
     */
    private Mappings csvMapReader;

    /**
     * @param csvMapReader
     * @param csvSettings
     */
    public Csv(String[] args) {

        //this.csvMapReader = csvMapReader;
        //this.csvSettings = csvSettings;

        this.args = args;
        parseArguments();

        if (args.length == 0 || aap.hasArgument("-h") || !aap.hasArgument("-m")) {
            aap.usage();
            System.exit(2);
        }

        csvSettings = new CsvSettings();
        csvSettings.setDelimiter(aap.getString("-l"));
        csvSettings.setEncloser(aap.getString("-e"));

    }

    /**
     * 
     * @throws XmlPropertiesFormatException
     */
    public void go() throws XmlPropertiesFormatException {

        try {

            if (aap.hasArgument("-import")) {

                LangUtil.consoleDebug(true, "IMPORT MODE");
                if (aap.hasArgument("-u"))
                    LangUtil.consoleDebug(true, "WITH UPDATE OPTION");

                csvMapReader = new ImportMapReader();
                csvMapReader.setMapFileName(aap.getString("-m"));
                csvMapReader.readMapFile();

                csvToSql(aap.hasArgument("-u"));

            }
            else if (aap.hasArgument("-export")) {

                LangUtil.consoleDebug(true, "EXPORT MODE");

                csvMapReader = new ExportMapReader();
                csvMapReader.setMapFileName(aap.getString("-m"));
                csvMapReader.readMapFile();

                sqlToCsv();

            }

        }
        catch (CsvMapRuleException e) {
            System.out.println("Cannot parse map file: " + e.getMessage());
        }

    } /*
       * (non-Javadoc)
       * 
       * @see com.wanci.dmerce.cli.Cli#parseArguments(java.lang.String[])
       */

    public ArgumentParser parseArguments() {

        aap = new ArgumentParser(args);

        aap.add("-import", null, "Import CSV data into SQL database");
        aap.add("-u", null, "Import option: update mode");
        aap.add("-export", null, "Export SQL database to CSV data");
        aap.add("-l", ";", "Field delimiter");
        aap.add("-e", "", "Field encloser (for strings)");
        aap.add("-m", "", "Map filename (default: csv.map)");
        aap.add("-x", null, "");
        aap.add("-h", null, "Show help");
        aap.parse();

        return aap;

    }

    /**
     * 
     *  
     */
    public void csvToSql(boolean updateMode)
            throws XmlPropertiesFormatException {

        /*
         * Anstatt pro Map vorzugehen, ist es sinnvoller pro CSV-Datei alle
         * Maps durchzugehen!
         */
        
        System.out.println("csvToSql(): updateMode="+updateMode);

        Iterator csvMaps = csvMapReader.getMapsIterator();
        System.out.println("csvMaps="+csvMaps.toString());
        while (csvMaps.hasNext()) {
            
            System.out.println("while begin");

            Mapping csvMapping = (Mapping) csvMaps.next();
            String csvTableName = csvMapping.getCsvTableName();
            String sqlTableName = csvMapping.getSqlTableName();
            
            System.out.println("csvTableName="+csvTableName
                + " sqlTableName="+sqlTableName);

            LangUtil.consoleDebug(true, "Importing '" + sqlTableName
                    + "' from '" + csvTableName + ".csv'");

            ImportCsvToSql csvImport = new ImportCsvToSql(csvSettings);
            csvImport.setFile(csvTableName + ".csv");

            try {

                csvImport.setMap(csvMapping);
                csvImport.setUpdateMode(updateMode);

                LangUtil.consoleDebug(true, "Processing map: " + csvTableName
                        + " -> " + sqlTableName);

                csvImport.processMap();

                LangUtil.consoleDebug(true, "Processed map: " + csvTableName
                        + " -> " + sqlTableName);

            }
            catch (Exception e) {
                e.printStackTrace();
                LangUtil.consoleDebug(true, "Cannot find or access file "
                        + csvTableName + ".csv: " + e.getCause() + ": "
                        + e.getMessage());
                continue;
            }

        }

    }

    /**
     * 
     *  
     */
    public void sqlToCsv() {

        Iterator csvMaps = csvMapReader.getMapsIterator();
        while (csvMaps.hasNext()) {

            Mapping csvMapping = (Mapping) csvMaps.next();
            String csvTableName = csvMapping.getCsvTableName();
            String sqlTableName = csvMapping.getSqlTableName();

            String exportFileName = csvTableName + ".exp."
                    + TimeUtil.getIsoTimeString() + ".csv";

            LangUtil.consoleDebug(true, "Exporting '" + sqlTableName + "' to '"
                    + exportFileName + "'");

            try {
                ExportSqlToCsv csvExport = new ExportSqlToCsv(csvSettings);
                csvExport.openFile(exportFileName);

                csvExport.setMap(csvMapping);

                LangUtil.consoleDebug(true, "Processing map: " + sqlTableName
                        + " -> " + csvTableName);

                csvExport.processMap();

                LangUtil.consoleDebug(true, "Processed map: " + sqlTableName
                        + " -> " + csvTableName);

                csvExport.closeFile();
            }
            catch (Exception e) {
                e.printStackTrace();
                LangUtil.consoleDebug(true, "Cannot find or access file "
                        + csvTableName + ".csv: " + e.getCause() + ": "
                        + e.getMessage());
                continue;
            }

        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) throws XmlPropertiesFormatException {

        long startTime = System.currentTimeMillis();

        Boot.printCopyright("CSV<->SQL IMPORT/EXPORT TOOL");
        LangUtil.consoleDebug(true, "START");

        Csv csv = new Csv(args);
        csv.go();

        long endTime = System.currentTimeMillis();
        LangUtil.consoleDebug(true, "Execution took " + (endTime - startTime)
                / 1000 + " seconds");
        LangUtil.consoleDebug(true, "STOP");

    }

}