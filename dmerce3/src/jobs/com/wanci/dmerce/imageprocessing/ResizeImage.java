/*
 * Created on May 28, 2003
 *  
 */
package com.wanci.dmerce.imageprocessing;

import java.awt.Dimension;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: ResizeImage.java,v 1.1 2004/02/06 18:36:40 rb Exp $
 * 
 * Verkleinert oder vergroessert ein Bild nach folgenden Vorgaben:
 * 
 * <p>
 * <li>Die "Erlaubnis" fuer verkleinern und/oder vergroessern muss gegeben
 * sein (setScaleDown() und setScaleUp())
 * <li>Wenn die beiden Werte X und Y gegeben sind, dann wird dann Bild auf
 * diese Werte gebracht
 * <li>Wird nur einer der beiden Werte X oder Y gesetzt, so wird der fehlende
 * Wert proportional zum gegebenen Wert berechnet.
 * </p>
 *  
 */
public class ResizeImage {

	private boolean debug = false;

	private String tableName;

	private int id;

	private String pictureBasePath;

	private String pictureURL;

	private int resizeToY;

	private int resizeToX;

	private String pictureFileName;

	private ImageInfo sourceImageInfo;

	private MagickImage sourceImage;

	private Dimension sourceImageDimension;

	private MagickImage scaledImage;

	private String backupImageFileName;

	private boolean scaleDown = false;

	private boolean scaleUp = false;

	public ResizeImage() {
	}

	public void analyzeImage() throws MagickException {

		pictureFileName = pictureBasePath + "/" + pictureURL;

		sourceImageInfo = new ImageInfo(pictureFileName);
		sourceImage = new MagickImage(sourceImageInfo);
		sourceImageDimension = sourceImage.getDimension();

		debug(
			"Actual dimension is "
				+ sourceImageDimension.width
				+ "x"
				+ sourceImageDimension.height
				+ " Format="
				+ sourceImage.getImageFormat());

		setBackupImageFileName();

	}

	public void debug(String msg) {
		if (pictureFileName != null)
			LangUtil.consoleDebug(debug, "[" + pictureFileName + "] " + msg);
		else
			LangUtil.consoleDebug(debug, msg);
	}

	public String getBackupImageFileName() {
		return backupImageFileName;
	}

	public int getDimensionX() {
		return sourceImageDimension.width;
	}

	public int getDimensionY() {
		return sourceImageDimension.height;
	}

	/**
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getPictureBasePath() {
		return pictureBasePath;
	}

	/**
	 * @return
	 */
	public String getPictureURL() {
		return pictureURL;
	}

	public String getPictureFileName() {
		return pictureBasePath + "/" + pictureURL;
	}

	public int getResizeToX() {
		return resizeToX;
	}

	public int getResizeToY() {
		return resizeToY;
	}

	/**
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}

	public String getUpdateStatement() {

		return "UPDATE "
			+ tableName
			+ " SET PictureToDo = 0,"
			+ " PictureURL = '"
			+ pictureURL
			+ "' WHERE ID = "
			+ id;

	}

	private double a(int v1, int v2) {

		double j1;

		if (v1 < v2)
			j1 = (double) v1 / (double) v2;
		else
			j1 = v2;

		return j1;

	}

	private int[] calculateResizeToValues() {

		int newX = 0;
		int newY = 0;

		//
		// beide Werte gegeben
		//
		if (resizeToX > 0 && resizeToY > 0 && scaleDown && scaleUp) {

			newX = resizeToX;
			newY = resizeToY;

		}
		else {

			//
			// verkleinern, nur 1 Wert gegeben
			//
			if (scaleDown) {

				// X gegeben, Y nicht
				if (resizeToX > 0 && resizeToY == 0)
					if (resizeToX < sourceImageDimension.width) {

						double j2 =
							(double) sourceImageDimension.height
								* a(resizeToX, sourceImageDimension.width);
						newX = resizeToX;
						newY = (int) j2;

					}

				// Y gegeben, X nicht
				if (resizeToX == 0 && resizeToY > 0)
					if (resizeToY < sourceImageDimension.height) {

						double j2 =
							(double) sourceImageDimension.height
								* a(resizeToY, sourceImageDimension.height);
						newX = (int) j2;
						newY = resizeToY;

					}

			}

			//
			// vergroessern, nur 1 Wert gegeben
			//
			if (scaleUp) {

				// X gegeben, Y nicht
				if (resizeToX > 0 && resizeToY == 0)
					if (resizeToX < sourceImageDimension.width) {

						double j2 =
							(double) sourceImageDimension.height
								* a(resizeToX, sourceImageDimension.width);
						newX = resizeToX;
						newY = (int) j2;

					}

				// Y gegeben, X nicht
				if (resizeToX == 0 && resizeToY > 0)
					if (resizeToY < sourceImageDimension.height) {

						double j2 =
							(double) sourceImageDimension.height
								* a(resizeToY, sourceImageDimension.height);
						newX = (int) j2;
						newY = resizeToY;

					}

			}

		}

		return new int[] { newX, newY };

	}

	public void resizeImage() throws MagickException {

		int[] xy = calculateResizeToValues();
		if (xy[0] != 0 && xy[1] != 0) {

			debug("Backing up file as '" + backupImageFileName + "'");

			sourceImage.setFileName(backupImageFileName);
			sourceImage.writeImage(sourceImageInfo);

			scaledImage = sourceImage.scaleImage(xy[0], xy[1]);
			scaledImage.setFileName(pictureFileName);
			scaledImage.writeImage(sourceImageInfo);

			debug(
				"New dimension is "
					+ xy[0]
					+ "x"
					+ xy[1]
					+ " Format="
					+ scaledImage.getImageFormat());

		}
		else {
			debug("Image does not need to be scaled");
		}

	}

	/**
	 * @param i
	 */
	public void setId(int i) {
		id = i;
	}

	private void setBackupImageFileName() {

		String[] spfn = LangUtil.splitFilenameExtension(pictureURL);
		backupImageFileName =
			getPictureBasePath()
				+ "/"
				+ spfn[0]
				+ "_"
				+ getDimensionX()
				+ "x"
				+ getDimensionY()
				+ "."
				+ spfn[1];

	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * @param string
	 */
	public void setPictureBasePath(String string) {
		pictureBasePath = string;
	}

	/**
	 * @param string
	 */
	public void setResizeToY(int i) {
		resizeToY = i;
	}

	/**
	 * @param string
	 */
	public void setPictureURL(String string) {
		pictureURL = string;
	}

	/**
	 * @param string
	 */
	public void setResizeToX(int i) {
		resizeToX = i;
	}

	/**
	 * Erlaubt es, dass das Bild verkleinert werden darf
	 * 
	 * @param b
	 */
	public void setScaleDown(boolean b) {
		debug("Will scale image down if needed");
		this.scaleDown = b;
	}

	/**
	 * Erlaubt es, dass das Bild vergroessert werden darf
	 * 
	 * @param b
	 */
	public void setScaleUp(boolean b) {
		debug("Will scale image up if needed");
		this.scaleUp = b;
	}

	/**
	 * @param string
	 */
	public void setTableName(String string) {
		tableName = string;
	}

}