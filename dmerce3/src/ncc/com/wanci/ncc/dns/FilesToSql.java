/*
 * Created on Nov 12, 2003
 *
 */
package com.wanci.ncc.dns;

import java.io.File;
import java.sql.SQLException;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: FilesToSql.java,v 1.2 2004/02/28 22:39:42 rb Exp $
 *
 */
public class FilesToSql {

	/**
	 * 
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException, DmerceException {

		boolean DEBUG = true;
		int importCount = 0;

		Database jdbcDatabase = DatabaseHandler.getDatabaseConnection("ncc");

		ExportToSql export = new ExportToSql(jdbcDatabase);
		export.init();

		Boot.printCopyright();
		LangUtil.consoleDebug(DEBUG, "START");
		long startTime = System.currentTimeMillis();

		File f = new File("/opt/dmerce/var/dns/import");
		if (!f.exists())
			f.mkdirs();
		File[] files = f.listFiles();
		for (int i = 0; i < files.length; i++) {

			String filename = files[i].getName();

			LangUtil.consoleDebug(
				DEBUG,
				"Importing zone from file " + filename);

			try {

				export.zoneToDatabase(new ImportFromFile(files[i]).recreate());
				importCount++;

			}
			catch (Exception e) {

				LangUtil.consoleDebug(
					DEBUG,
					"Import from file "
						+ filename
						+ " failed. Exception: "
						+ e.getCause()
						+ ": "
						+ e.getMessage());

				e.printStackTrace();

			}

		}

		long stopTime = System.currentTimeMillis();
		long delta = (stopTime - startTime) / 1000;

		if (importCount > 0)
			LangUtil.consoleDebug(
				DEBUG,
				"Imported "
					+ importCount
					+ " files in "
					+ delta
					+ " seconds (ca. "
					+ (delta / 60)
					+ " minutes)");
		LangUtil.consoleDebug(DEBUG, "STOP");

		export.deinit();

	}

}