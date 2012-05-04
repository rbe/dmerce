/*
 * Created on Jan 9, 2004
 *  
 */
package com.wanci.dmerce.filemanager;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version ${Id}
 * 
 * Load all files to delete Check base directory of every entry against
 * information from dmerce_sys.Configuration Check
 *  
 */
public class DeleteFilesJob {

	boolean DEBUG = true;

	Database jdbcDatabase;

	Vector filesToDelete = new Vector();

	DeleteFilesJob(Database jdbcDatabase) {
		this.jdbcDatabase = jdbcDatabase;
	}

	/**
	 * @param baseDir
	 * @param projectFqhn
	 * @return @throws
	 *         XmlPropertiesFormatException
	 */
	String guessBaseDir(String baseDir, String projectFqhn)
		throws XmlPropertiesFormatException {

		XmlPropertiesReader xpr = XmlPropertiesReader.getInstance();

		String guessedBaseDir =
			xpr.getProperty("dmerce.base")
				+ xpr.getProperty("dmerce.websites")
				+ "/";

		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(projectFqhn, ".");
		int countTokens = st.countTokens();
		for (int i = 0; i < countTokens; i++) {

			if (i < countTokens - 2)
				sb.insert(0, st.nextToken());
			else {
				sb.insert(0, st.nextToken() + "." + st.nextToken());
				i += 2;
			}

			if (i < countTokens - 1)
				sb.insert(0, "/");

		}
		guessedBaseDir += sb.toString();

		return guessedBaseDir;

	}

	/**
	 * @param filename
	 * @param todo
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 */
	void setToDo(String filename, int todo)
		throws IllegalArgumentException, SQLException {

		String stmt =
			"UPDATE DeleteFiles"
				+ " SET ToDo = "
				+ todo
				+ " WHERE FileName = '"
				+ new File(filename).getName()
				+ "'";
		jdbcDatabase.executeUpdate(stmt);

	}

	/**
	 * 
	 *  
	 */
	void deleteFiles() throws IllegalArgumentException, SQLException {

		Iterator i = filesToDelete.iterator();
		while (i.hasNext()) {

			String filename = (String) i.next();
			File f = new File(filename);
			if (!f.delete()) {
				LangUtil.consoleDebug(
					true,
					"File " + filename + " was not deleted!");
				setToDo(filename, 2);
			}
			else
				setToDo(filename, 0);

		}

	}

	/**
	 * @throws SQLException
	 */
	void retrieveFilesToDelete()
		throws XmlPropertiesFormatException, SQLException {

		String stmt =
			"SELECT ID, ProjectFQHN, BaseDir, FileName"
				+ " FROM DeleteFiles"
				+ " WHERE ToDo = 1";
		ResultSet rs = jdbcDatabase.executeQuery(stmt);
		while (rs.next()) {

			String baseDir = rs.getString("BaseDir");
			String fileName = rs.getString("FileName");
			String guessedBaseDir =
				guessBaseDir(baseDir, rs.getString("ProjectFQHN"));
			String fqfn = baseDir + "/" + fileName;

			if (baseDir.indexOf(guessedBaseDir) == -1
				|| fqfn.indexOf("templates") >= 0
				|| fqfn.indexOf(".html") >= 0
				|| fqfn.indexOf(".css") >= 0
				|| fqfn.indexOf(".cfg") >= 0) {

				LangUtil.consoleDebug(DEBUG, "Will NOT delete " + fqfn);
				setToDo(fileName, 2);

			}
			else if (baseDir.indexOf(guessedBaseDir) == 0) {
				filesToDelete.add(fqfn);
				LangUtil.consoleDebug(DEBUG, "Will delete " + fqfn);
			}
			else
				LangUtil.consoleDebug(DEBUG, "Will NOT delete " + fqfn);

		}

	}

	/**
	 * @param args
	 * @throws XmlPropertiesFormatException
	 * @throws SQLException
	 */
	public static void main(String[] args)
		throws XmlPropertiesFormatException, SQLException {

		boolean DEBUG = true;

		Boot.printCopyright("DELETE FILES");

		LangUtil.consoleDebug(DEBUG, "START");

		Database jdbcDatabase =
			DatabaseHandler.getDatabaseConnection("deletefiles");

		if (jdbcDatabase != null) {

			jdbcDatabase.openConnection();

			DeleteFilesJob dfj = new DeleteFilesJob(jdbcDatabase);
			dfj.retrieveFilesToDelete();
			dfj.deleteFiles();

			jdbcDatabase.closeConnection();

		}
		else
			LangUtil.consoleDebug(
				true,
				"Cannot find database connection 'deletefiles' in properties.xml");

		LangUtil.consoleDebug(DEBUG, "STOP");

	}

}