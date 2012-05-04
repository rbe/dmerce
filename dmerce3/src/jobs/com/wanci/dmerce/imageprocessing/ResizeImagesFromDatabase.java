/*
 * Created on May 16, 2003
 *
 */
package com.wanci.dmerce.imageprocessing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: ResizeImagesFromDatabase.java,v 1.1 2004/02/06 18:36:40 rb Exp $
 *
 * - Alle Tabellennamen einer Datenbank/Schema finden
 * - Alle Tabellen nach den Feldern
 *      PictureBasePath
 *      PictureWidth
 *      PictureHeight
 *      PictureURL
 *      PictureToDo: 1 = verkleinern, 2 = vergroessern, 3 = beides
 *   durchsuchen
 * - Alle Eintraege nehmen und diese dann durch
 *   ein Skript laufen lassen, dem die Parameter PictureWidth und
 *   PictureHeight mitgegeben wird
 * - Nutzt Datenbankverbindung, die in dmerce.properties als qd_resizeimages
 *   angegeben wird
 * 
 */
public class ResizeImagesFromDatabase {

	private boolean debug = false;

	private Database jdbcDatabase;

	public ResizeImagesFromDatabase() throws DmerceException {
		jdbcDatabase =
			DatabaseHandler.getDatabaseConnection("qd_resizeimages");
	}

	public ResizeImage analyze(String tableName, ResultSet rs)
		throws SQLException {

		ResizeImage i = null;

		int id = rs.getInt(1);
		String pictureBasePath = rs.getString(2);
		String pictureURL = rs.getString(3);
		int pictureWidth = rs.getInt(4);
		int pictureHeight = rs.getInt(5);
		int pictureToDo = rs.getInt(6);

		if (pictureBasePath != null
			&& pictureURL != null
			&& (pictureWidth > 0 || pictureHeight > 0)) {

			LangUtil.consoleDebug(
				debug,
				"Found image to resize in table '"
					+ tableName
					+ "' ID "
					+ id
					+ " PictureBasePath='"
					+ pictureBasePath
					+ "' PictureURL='"
					+ pictureURL
					+ "'");

			i = new ResizeImage();
			i.setDebug(debug);
			i.setTableName(tableName);
			i.setId(id);
			i.setPictureBasePath(pictureBasePath);
			i.setPictureURL(pictureURL);
			i.setResizeToX(pictureWidth);
			i.setResizeToY(pictureHeight);

			switch (pictureToDo) {

				case 1 :
					i.setScaleDown(true);
					break;
				case 2 :
					i.setScaleUp(true);
					break;
				case 3 :
					i.setScaleDown(true);
					i.setScaleUp(true);
					break;

			}

		}

		return i;

	}

	public void closeConnection() throws SQLException {
		jdbcDatabase.closeConnection();
	}

	public void debug(String pictureFileName, String msg) {
		LangUtil.consoleDebug(debug, "[" + pictureFileName + "] " + msg);
	}

	/**
	 * Liefert eine Liste aller Images, die resized
	 * werden sollen
	 *
	 */
	private Iterator getImagesToResize() throws SQLException {

		Vector images = new Vector();
		ResultSet tables = jdbcDatabase.getTables();

		while (tables.next()) {

			String tableName = tables.getString(3);
			String stmt =
				"SELECT ID, PictureBasePath, PictureURL, PictureWidth, PictureHeight, PictureToDo"
					+ "  FROM "
					+ tableName
					+ " WHERE PictureToDo >= 1 AND PictureToDo <= 3"
					+ "   AND PictureBasePath IS NOT NULL"
					+ "   AND PictureURL IS NOT NULL"
					+ "   AND (PictureWidth > 0 OR PictureHeight > 0)";

			try {

				ResultSet rs = jdbcDatabase.executeQuery(stmt);
				while (rs.next()) {

					ResizeImage i = analyze(tableName, rs);
					if (i != null)
						images.add(i);

				}

			}
			catch (SQLException se) {

				String msg = se.getMessage().trim();

				if (msg.indexOf("not found") == -1)
					LangUtil.consoleDebug(
						debug,
						"ERROR: Table "
							+ tableName
							+ ": "
							+ "Statement: "
							+ stmt
							+ ": "
							+ se.getMessage().trim());

			}

		}

		return images.iterator();

	}

	private boolean resizeImage(ResizeImage v) {

		String originalImageFileName = v.getPictureFileName();

		try {

			v.analyzeImage();

			try {
				v.resizeImage();
			}
			catch (Exception e) {

				debug(
					originalImageFileName,
					"ERROR: Could not resize image:" + e.getMessage());

				return false;

			}

			String updateStmt = v.getUpdateStatement();
			try {
				//System.out.println(updateStmt);
				jdbcDatabase.executeUpdate(updateStmt);
			}
			catch (SQLException se) {

				debug(
					originalImageFileName,
					"ERROR: Cannot execute SQL statement: "
						+ updateStmt
						+ ": "
						+ se.getMessage());

				return false;

			}

		}
		catch (Exception e) {

			debug(
				originalImageFileName,
				"ERROR: Could not analyze image: " + e.getMessage());

			return false;

		}

		return true;

	}

	public void go() {

		int imagesOk = 0;
		int imagesNotOk = 0;

		try {

			Iterator images = getImagesToResize();
			while (images.hasNext()) {

				ResizeImage v = (ResizeImage) images.next();
				if (resizeImage(v))
					imagesOk++;
				else
					imagesNotOk++;

			}

			LangUtil.consoleDebug(
				true,
				imagesOk
					+ " of "
					+ (imagesOk + imagesNotOk)
					+ " images processed: "
					+ imagesNotOk
					+ " had errors");

		}
		catch (SQLException se) {

			LangUtil.consoleDebug(
				true,
				"ERROR: Cannot find any images to resize:" + se.getMessage());

		}

	}

	public void openConnection() throws SQLException {
		jdbcDatabase.openConnection();
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}