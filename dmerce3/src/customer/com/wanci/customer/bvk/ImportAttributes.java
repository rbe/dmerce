/*
 * Created on Feb 4, 2004
 *  
 */
package com.wanci.customer.bvk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.StringTokenizer;

import com.wanci.dmerce.cli.ArgumentParser;
import com.wanci.dmerce.csv.CsvHeader;
import com.wanci.dmerce.csv.CsvSettings;
import com.wanci.dmerce.csv.CsvToSqlInsert;
import com.wanci.dmerce.csv.Mapping;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: ImportAttributes.java,v 1.2 2004/08/04 17:29:17 rb Exp $
 *  
 */
public class ImportAttributes {

	boolean DEBUG = false;

	Database jdbcDatabase;

	File newAttributesFile;

	CsvToSqlInsert csvToSqlInsert;

	ImportAttributes(Database jdbcDatabase, File newAttributesFile) {

		this.jdbcDatabase = jdbcDatabase;
		this.newAttributesFile = newAttributesFile;

		CsvSettings csvSettings = new CsvSettings();
		CsvHeader csvHeader = new CsvHeader(csvSettings);
		Mapping csvMapping = new Mapping();
		csvToSqlInsert =
			new CsvToSqlInsert(
				csvSettings,
				csvHeader,
				csvMapping,
				jdbcDatabase);

	}

	void deleteOldEntries() throws IllegalArgumentException, SQLException {

		String stmt =
			"DELETE FROM attributeeverything"
				+ " WHERE chart = 'Member'"
				+ " AND createddatetime IS NULL"
				+ " AND changeddatetime IS NULL";
		LangUtil.consoleDebug(
			DEBUG,
			"Deleting old entries in table 'attributeeverything': " + stmt);
		jdbcDatabase.executeUpdate(stmt);

	}

	void importNewEntries()
		throws IOException, IllegalArgumentException, SQLException {

		BufferedReader br =
			new BufferedReader(new FileReader(newAttributesFile));
		String line;
		while ((line = br.readLine()) != null) {

			StringTokenizer st = new StringTokenizer(line, ";");
			String chartId = st.nextToken();
			String attributeId = st.nextToken();

			String stmt =
				"INSERT INTO attributeeverything"
					+ " (id, chart, chartid, attributeid)"
					+ " VALUES (nextval('s_attributeeverything') , 'Member', "
					+ chartId
					+ ", "
					+ attributeId
					+ ")";
			LangUtil.consoleDebug(
				DEBUG,
				"Inserting new entry in 'attributeeverything': " + stmt);
			jdbcDatabase.executeUpdate(stmt);

		}

	}

	public static void main(String[] args)
		throws IllegalArgumentException, IOException, SQLException {

		boolean DEBUG = true;

		Boot.printCopyright("BVK - IMPORT ATTRIBUTES");

		LangUtil.consoleDebug(DEBUG, "START");

		ArgumentParser ap = new ArgumentParser(args);
		ap.add("-h", null, "Display help");
		ap.add("-f", "", "File to import");
		ap.parse();

		if (args.length < 1 || ap.hasArgument("-h")) {
			ap.usage();
			System.exit(2);
		}
		else {

			LangUtil.consoleDebug(
				DEBUG,
				"Using file " + ap.getString("-f") + " for import");

			ImportAttributes ia =
				new ImportAttributes(
					DatabaseHandler.getDatabaseConnection("csv"),
					new File(ap.getString("-f")));
			ia.deleteOldEntries();
			ia.importNewEntries();

		}

		LangUtil.consoleDebug(DEBUG, "STOP");

	}

}