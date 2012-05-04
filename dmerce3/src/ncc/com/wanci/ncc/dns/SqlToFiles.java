/*
 * Created on Nov 12, 2003
 *  
 */
package com.wanci.ncc.dns;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import com.wanci.dmerce.cli.ArgumentParser;
import com.wanci.dmerce.exceptions.ResourceRecordInvalidException;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: SqlToFiles.java,v 1.2 2004/02/28 22:39:42 rb Exp $
 *  
 */
public class SqlToFiles {

	/**
	 * @param args
	 * @throws ResourceRecordInvalidException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void main(String[] args)
		throws ResourceRecordInvalidException, SQLException, IOException {

		boolean DEBUG = true;
		boolean DEBUG2 = false;

		ArgumentParser ap = new ArgumentParser(args);
		ap.add("-q", null);
		ap.parse();

		if (ap.hasArgument("-q")) {
			DEBUG = false;
			DEBUG2 = false;
		}
		else
			Boot.printCopyright("1[NCC] DNS");

		if (ap.hasArgument("-v"))
			DEBUG2 = true;

		LangUtil.consoleDebug(DEBUG, "START");

		ImportFromSql sqlToBind = new ImportFromSql();
		if (ap.hasArgument("-v"))
			sqlToBind.setDebug(true);
		sqlToBind.init();
		LangUtil.consoleDebug(DEBUG, "Connected to database");
		LangUtil.consoleDebug(DEBUG, "Generating nameserver objects in memory");
		sqlToBind.processNameservers();
		LangUtil.consoleDebug(
			DEBUG,
			"Processed "
				+ sqlToBind.getNameserverCount()
				+ " nameservers and "
				+ sqlToBind.getZonesCount()
				+ " primary zones");
		sqlToBind.deinit();
		LangUtil.consoleDebug(DEBUG, "Disconnected from database");

		Iterator i = sqlToBind.getNameservers();
		while (i.hasNext()) {

			Nameserver n = (Nameserver) i.next();
			String name = n.getName();
			LangUtil.consoleDebug(
				DEBUG2,
				"Dumping nameserver " + name + "\n" + n.getBindString());

			File exportDir = new File("/opt/dmerce/var/dns/export/" + name);
			if (!exportDir.exists())
				exportDir.mkdirs();

			File namedConfFile =
				new File(exportDir.getAbsoluteFile() + "/named.conf");
			FileWriter namedConfWriter = new FileWriter(namedConfFile);
			namedConfWriter.write(n.getBindString());
			namedConfWriter.close();

			if (n.hasPrimaryZones()) {

				File primaryZonesDir =
					new File(exportDir.getAbsolutePath() + "/pri");
				if (!primaryZonesDir.exists())
					primaryZonesDir.mkdirs();

				Iterator primaryZonesIterator = n.getPrimaryZonesIterator();
				while (primaryZonesIterator.hasNext()) {

					Zone z = (Zone) primaryZonesIterator.next();
					String zoneName = z.getName();

					File zoneFile =
						new File(
							primaryZonesDir.getAbsolutePath() + "/" + zoneName);
					LangUtil.consoleDebug(
						DEBUG,
						"Writing zone file '"
							+ zoneFile.getAbsolutePath()
							+ "' for domain '"
							+ zoneName
							+ "'");
					String bindString = z.getBindString();
					if (bindString != null) {
						FileWriter zoneFileWriter = new FileWriter(zoneFile);
						zoneFileWriter.write(bindString);
						zoneFileWriter.close();
					}

				}

			}

			if (n.hasSecondaryZones()) {
				File secondaryZonesDir =
					new File(exportDir.getAbsolutePath() + "/sec");
				secondaryZonesDir.mkdirs();
			}

		}

		LangUtil.consoleDebug(DEBUG, "STOP");

	}

}